package com.otg.flasherpro;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMG = 101;
    private TextView tvStatus, tvOtg, tvLog;
    private Button btnPick, btnRefresh, btnFlash;
    private Spinner spDevice;
    private String selectedImgPath = null;
    private String[] blockDevices;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tvStatus); tvOtg = findViewById(R.id.tvOtg); tvLog = findViewById(R.id.tvLog);
        btnPick = findViewById(R.id.btnPick); btnRefresh = findViewById(R.id.btnRefresh); btnFlash = findViewById(R.id.btnFlash); spDevice = findViewById(R.id.spDevice);
        btnPick.setOnClickListener(v -> pickImage()); btnRefresh.setOnClickListener(v -> refreshOtgDevices()); btnFlash.setOnClickListener(v -> flashNow());
        refreshOtgDevices(); checkRoot();
    }
    void log(String s){ runOnUiThread(() -> tvLog.append(s+"\n")); }
    void checkRoot(){ boolean rooted = isRooted(); tvStatus.setText(rooted? "ROOT: YES - UNIVERSAL" : "ROOT: NO - Need Root!"); log("Root: "+rooted); }
    boolean isRooted(){ try{ Process p=Runtime.getRuntime().exec("su -c id"); BufferedReader r=new BufferedReader(new InputStreamReader(p.getInputStream())); String line=r.readLine(); p.waitFor(); return line!=null && line.contains("uid=0"); }catch(Exception e){return false;} }
    void refreshOtgDevices(){
        String[] candidates = {"/dev/block/sda","/dev/block/sdb","/dev/block/sdc","/dev/block/sda1","/dev/block/sdb1","/dev/sda","/dev/sdb","/dev/block/vold/public:8,1"};
        java.util.ArrayList<String> found=new java.util.ArrayList<>();
        File sysBlock=new File("/sys/block");
        if(sysBlock.exists()){ File[] files=sysBlock.listFiles(); if(files!=null){ for(File f:files){ String name=f.getName(); if(name.startsWith("sd")){ File rem=new File(f,"removable"); try{ if(rem.exists()){ String v=new String(java.nio.file.Files.readAllBytes(rem.toPath())).trim(); if(v.equals("1")) found.add("/dev/block/"+name); } }catch(Exception ignored){} } } } }
        for(String c:candidates){ if(new File(c).exists() &&!found.contains(c)) found.add(c); }
        blockDevices=found.toArray(new String[0]);
        if(blockDevices.length==0){ tvOtg.setText("OTG: NOT FOUND - Colok Flashdisk!"); blockDevices=new String[]{"/dev/block/sda"}; } else { tvOtg.setText("OTG: FOUND "+blockDevices.length+" - UNIVERSAL"); }
        ArrayAdapter<String> ad=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, blockDevices); spDevice.setAdapter(ad); log("Scan: "+java.util.Arrays.toString(blockDevices));
    }
    void pickImage(){ Intent i=new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); startActivityForResult(i, PICK_IMG); }
    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){ super.onActivityResult(requestCode,resultCode,data); if(requestCode==PICK_IMG && resultCode==Activity.RESULT_OK && data!=null){ Uri uri=data.getData(); selectedImgPath=copyUriToCache(uri); tvStatus.setText("IMG: "+new File(selectedImgPath).getName()); log("Selected: "+selectedImgPath+" size: "+new File(selectedImgPath).length()); } }
    String copyUriToCache(Uri uri){ try{ InputStream in=getContentResolver().openInputStream(uri); File outFile=new File(getCacheDir(),"flash.img"); FileOutputStream out=new FileOutputStream(outFile); byte[] buf=new byte[8192]; int len; while((len=in.read(buf))>0) out.write(buf,0,len); in.close(); out.close(); return outFile.getAbsolutePath(); }catch(Exception e){ log("Copy fail: "+e.getMessage()); return null; } }
    void flashNow(){
        if(selectedImgPath==null){ Toast.makeText(this,"Pilih IMG dulu!",0).show(); return; }
        if(!isRooted()){ Toast.makeText(this,"Need ROOT!",0).show(); return; }
        String target=spDevice.getSelectedItem().toString().trim().split(" ")[0]; if(!target.startsWith("/dev/")) target="/dev/block/sda";
        tvLog.setText(""); log("=== UNIVERSAL FLASH START ==="); log("IMG: "+selectedImgPath); log("TARGET: "+target); log("NO VINCE CHECK - ALL DEVICES");
        new Thread(() -> { try{ String cmd=String.format("dd if=\"%s\" of=\"%s\" bs=4M status=progress; echo EXIT:$?", selectedImgPath, target); log("Exec: su -c "+cmd); Process p=Runtime.getRuntime().exec(new String[]{"su","-c", cmd}); BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader er=new BufferedReader(new InputStreamReader(p.getErrorStream())); String line; while((line=br.readLine())!=null) log(line); while((line=er.readLine())!=null) log(line); p.waitFor(); log("DONE CODE: "+p.exitValue()); runOnUiThread(() -> Toast.makeText(this,"Flash Selesai! "+p.exitValue(),1).show()); }catch(Exception e){ log("Ex: "+e.getMessage()); } }).start();
    }
}
