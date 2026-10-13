package com.otg.flasherpro;
import android.app.PendingIntent;
import android.content.*;
import android.hardware.usb.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private TextView tvStatus, tvLog;
    private Button btnPick, btnFlash;
    private Spinner spDevice;
    private String selectedImgPath = null;
    private UsbManager usbManager;
    private static final String ACTION_USB = "com.otg.flasherpro.USB_PERMISSION";
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        tvStatus=findViewById(R.id.tvStatus); tvLog=findViewById(R.id.tvLog);
        btnPick=findViewById(R.id.btnPick); btnFlash=findViewById(R.id.btnFlash); spDevice=findViewById(R.id.spDevice);
        usbManager=(UsbManager)getSystemService(USB_SERVICE);
        btnPick.setOnClickListener(v->pick()); btnFlash.setOnClickListener(v->flash());
        scan();
        registerReceiver(receiver, new IntentFilter(ACTION_USB));
    }
    void log(String s){ runOnUiThread(()->tvLog.append(s+"\n")); }
    void scan(){
        HashMap<String, UsbDevice> list=usbManager.getDeviceList();
        String[] names=new String[list.size()];
        int i=0; for(UsbDevice d: list.values()) names[i++]=d.getDeviceName()+" - "+d.getProductName();
        if(names.length==0) names=new String[]{"/storage/ - (Colok OTG, nanti kedetek)"};
        // Also scan mounted OTG volumes
        StorageManager sm=(StorageManager)getSystemService(STORAGE_SERVICE);
        List<StorageVolume> vols=sm.getStorageVolumes();
        java.util.ArrayList<String> all=new java.util.ArrayList<>();
        for(String n: names) all.add(n);
        for(StorageVolume v: vols){ if(v.isRemovable()){ all.add("OTG Volume: "+v.getDescription(this)); log("Found removable: "+v.getDescription(this)); } }
        ArrayAdapter<String> ad=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, all);
        spDevice.setAdapter(ad);
        tvStatus.setText("MODE: UNIVERSAL NON-ROOT ✓ Found "+all.size()+" device - NO VINCE - NO SU");
        log("Scan USB: "+all.size()+" - Universal, no su, no vince check");
    }
    void pick(){ Intent i=new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); startActivityForResult(i,101); }
    @Override protected void onActivityResult(int a,int b,Intent d){ super.onActivityResult(a,b,d); if(a==101 && b==RESULT_OK){ Uri uri=d.getData(); selectedImgPath=copy(uri); tvStatus.setText("IMG: "+new File(selectedImgPath).getName()); log("IMG: "+selectedImgPath+" "+new File(selectedImgPath).length()/1024/1024+"MB"); } }
    String copy(Uri uri){ try{ InputStream in=getContentResolver().openInputStream(uri); File f=new File(getCacheDir(),"flash.img"); FileOutputStream out=new FileOutputStream(f); byte[] buf=new byte[8192]; int l; while((l=in.read(buf))>0) out.write(buf,0,l); in.close(); out.close(); return f.getAbsolutePath(); }catch(Exception e){ log(e.getMessage()); return null; } }
    void flash(){
        if(selectedImgPath==null){ Toast.makeText(this,"Pilih IMG dulu",0).show(); return; }
        // Find OTG mount point - universal way
        File[] ext= getExternalFilesDirs(null);
        String target=null;
        for(File f: ext){ if(f!=null){ String p=f.getAbsolutePath(); log("Check ext: "+p); if(p.contains("/storage/") &&!p.contains("/emulated/")){ target=p; } } }
        // Fallback scan /storage
        File storage=new File("/storage");
        if(storage.exists()){ File[] fs=storage.listFiles(); if(fs!=null) for(File f: fs){ if(f.isDirectory() &&!f.getName().equals("emulated") &&!f.getName().equals("self")){ log("Found /storage/"+f.getName()); if(target==null) target="/storage/"+f.getName(); } } }
        if(target==null){ log("OTG mount not found, trying SAF - please pick OTG root via file manager"); Toast.makeText(this,"Colok OTG, buka file manager, lihat mount point /storage/XXXX",1).show(); return; }
        String finalTarget=target;
        tvLog.setText("");
        log("=== FLASH UNIVERSAL NON-ROOT ==="); log("IMG: "+selectedImgPath); log("OTG TARGET FOLDER: "+finalTarget); log("NO SU - NO VINCE");
        new Thread(()->{
            try{
                File src=new File(selectedImgPath);
                File dest=new File(finalTarget+"/flashed_"+src.getName());
                // If target is app private dir, copy to real OTG root via MediaStore? For universal we copy to app's OTG dir first
                InputStream in=new FileInputStream(src);
                FileOutputStream out=new FileOutputStream(dest);
                byte[] buf=new byte[1024*1024*4];
                long total=src.length(), wrote=0; int r;
                while((r=in.read(buf))>0){ out.write(buf,0,r); wrote+=r; int pct=(int)(wrote*100/total); if(wrote% (10*1024*1024)==0) log("Copy "+pct+"% "+wrote/1024/1024+"MB"); }
                in.close(); out.close();
                log("=== DONE 100% COPIED TO: "+dest.getAbsolutePath()+" ===");
                log("Note: Untuk RAW dd tanpa root butuh custom driver, versi ini copy file IMG ke OTG (universal, work semua HP)");
                runOnUiThread(()->Toast.makeText(this,"Selesai copy ke OTG: "+dest.getName(),1).show());
            }catch(Exception e){ log("FAIL: "+e.getMessage()); }
        }).start();
    }
    BroadcastReceiver receiver=new BroadcastReceiver(){ public void onReceive(Context c,Intent i){ if(ACTION_USB.equals(i.getAction())){ scan(); } } };
}
