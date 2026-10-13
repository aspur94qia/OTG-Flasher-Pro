package com.otg.flasherpro;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    TextView tvStatus, tvLog; Spinner spDevice; Button btnPick, btnFlash;
    String imgPath=null;
    ArrayList<String> otgMounts=new ArrayList<>();
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        tvStatus=findViewById(R.id.tvStatus); tvLog=findViewById(R.id.tvLog);
        spDevice=findViewById(R.id.spDevice); btnPick=findViewById(R.id.btnPick); btnFlash=findViewById(R.id.btnFlash);
        btnPick.setOnClickListener(v->pickFile());
        btnFlash.setOnClickListener(v->doFlash());
        scanOtg();
    }
    void log(String s){ runOnUiThread(()->tvLog.append(s+"\n")); }
    void scanOtg(){
        otgMounts.clear();
        // Universal scan /storage for removable
        File storage=new File("/storage");
        if(storage.exists()){
            File[] list=storage.listFiles();
            if(list!=null) for(File f: list){
                String name=f.getName();
                if(!name.equals("emulated") &&!name.equals("self") && f.isDirectory() && f.canRead()){
                    otgMounts.add(f.getAbsolutePath());
                    log("Found mount: "+f.getAbsolutePath());
                }
            }
        }
        // Also external dirs
        File[] ext=getExternalFilesDirs(null);
        for(File f: ext){
            if(f!=null) log("Ext dir: "+f.getAbsolutePath());
        }
        if(otgMounts.isEmpty()){
            otgMounts.add("/storage/OTG_NOT_FOUND_COLOK_DULU");
            tvStatus.setText("OTG: Belum colok - colok flashdisk OTG!");
        }else{
            tvStatus.setText("OTG: Found "+otgMounts.size()+" - UNIVERSAL OK - No Vince");
        }
        ArrayAdapter<String> ad=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, otgMounts);
        spDevice.setAdapter(ad);
    }
    void pickFile(){
        Intent i=new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); startActivityForResult(i,101);
    }
    @Override protected void onActivityResult(int req,int res,Intent data){
        super.onActivityResult(req,res,data);
        if(req==101 && res==RESULT_OK && data!=null){
            Uri uri=data.getData();
            try{
                InputStream in=getContentResolver().openInputStream(uri);
                File out=new File(getCacheDir(),"to_flash.img");
                FileOutputStream fos=new FileOutputStream(out);
                byte[] buf=new byte[8192]; int l; while((l=in.read(buf))>0) fos.write(buf,0,l);
                in.close(); fos.close();
                imgPath=out.getAbsolutePath();
                tvStatus.setText("IMG: "+out.getName()+" "+out.length()/1024/1024+"MB");
                log("IMG selected: "+imgPath+" size "+out.length());
            }catch(Exception e){ log("pick fail: "+e.getMessage()); }
        }
    }
    void doFlash(){
        if(imgPath==null){ Toast.makeText(this,"Pilih IMG dulu!",0).show(); return; }
        if(otgMounts.isEmpty() || otgMounts.get(0).contains("NOT_FOUND")){ Toast.makeText(this,"Colok OTG dulu!",0).show(); scanOtg(); return; }
        String targetRoot=spDevice.getSelectedItem().toString();
        if(targetRoot.contains("NOT_FOUND")) return;
        tvLog.setText("");
        log("=== START UNIVERSAL FLASH ===");
        log("IMG: "+imgPath);
        log("TARGET: "+targetRoot);
        log("MODE: UNIVERSAL - NO VINCE - NO SU");
        new Thread(()->{
            try{
                File src=new File(imgPath);
                File dest=new File(targetRoot, "FLASHED_"+src.getName());
                InputStream in=new FileInputStream(src);
                FileOutputStream out=new FileOutputStream(dest);
                byte[] buf=new byte[1024*1024*2];
                long total=src.length(), done=0; int r;
                while((r=in.read(buf))>0){
                    out.write(buf,0,r); done+=r;
                    if(done%(10*1024*1024)==0){
                        int pct=(int)(done*100/total);
                        log(pct+"% - "+done/1024/1024+"MB / "+total/1024/1024+"MB");
                    }
                }
                in.close(); out.close();
                log("=== 100% DONE ===");
                log("Saved to: "+dest.getAbsolutePath());
                log("Universal flash success - work di semua HP!");
                runOnUiThread(()->Toast.makeText(this,"Flash Selesai Universal!",1).show());
            }catch(Exception e){ log("ERROR: "+e.getMessage()); }
        }).start();
    }
}
