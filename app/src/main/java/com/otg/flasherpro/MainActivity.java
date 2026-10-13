package com.otg.flasherpro;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;
import java.io.*;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String ACTION_USB_PERMISSION = "com.otg.flasherpro.USB_PERMISSION";
    private TextView tvStatus, tvLog;
    private Button btnPick, btnFlash;
    private Spinner spDevice;
    private String selectedImgPath = null;
    private UsbMassStorageDevice[] devices;
    private UsbMassStorageDevice currentDevice;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tvStatus); tvLog = findViewById(R.id.tvLog);
        btnPick = findViewById(R.id.btnPick); btnFlash = findViewById(R.id.btnFlash); spDevice = findViewById(R.id.spDevice);
        btnPick.setOnClickListener(v -> pickImage()); btnFlash.setOnClickListener(v -> flashNonRoot());
        tvStatus.setText("MODE: NON-ROOT UNIVERSAL ✓ No su needed");
        scanUsb();
        registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
    }

    void log(String s){ runOnUiThread(() -> tvLog.append(s+"\n")); }

    void scanUsb(){
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        devices = UsbMassStorageDevice.getMassStorageDevices(this);
        String[] names = new String[devices.length];
        for(int i=0;i<devices.length;i++) names[i]="USB Disk "+i+" - "+devices[i].getUsbDevice().getDeviceName();
        if(names.length==0){ tvStatus.setText("OTG: Colok Flashdisk OTG!"); names=new String[]{"No USB - Colok dulu"}; }
        else tvStatus.setText("OTG: Found "+names.length+" - NON ROOT OK");
        ArrayAdapter<String> ad=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names); spDevice.setAdapter(ad);
        log("USB scan: "+names.length+" device, NO ROOT, NO VINCE CHECK");
    }

    void pickImage(){
        Intent i=new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); startActivityForResult(i,101);
    }
    @Override protected void onActivityResult(int r,int res,Intent d){ super.onActivityResult(r,res,d); if(r==101 && res==RESULT_OK){ Uri uri=d.getData(); selectedImgPath=copyToCache(uri); tvStatus.setText("IMG: "+new File(selectedImgPath).getName()+" "+new File(selectedImgPath).length()/1024/1024+"MB"); log("IMG picked: "+selectedImgPath); } }
    String copyToCache(Uri uri){ try{ InputStream in=getContentResolver().openInputStream(uri); File f=new File(getCacheDir(),"flash.img"); FileOutputStream out=new FileOutputStream(f); byte[] b=new byte[8192]; int l; while((l=in.read(b))>0) out.write(b,0,l); in.close(); out.close(); return f.getAbsolutePath(); }catch(Exception e){ log("copy fail "+e.getMessage()); return null; } }

    void flashNonRoot(){
        if(selectedImgPath==null){ Toast.makeText(this,"Pilih IMG dulu",0).show(); return; }
        if(devices==null || devices.length==0){ Toast.makeText(this,"Colok OTG dulu",0).show(); return; }
        int idx=spDevice.getSelectedItemPosition(); if(idx<0) idx=0; currentDevice=devices[idx];
        UsbManager um=(UsbManager)getSystemService(USB_SERVICE);
        if(!um.hasPermission(currentDevice.getUsbDevice())){
            PendingIntent pi=PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE);
            um.requestPermission(currentDevice.getUsbDevice(),pi); log("Request USB permission..."); return;
        }
        doFlash();
    }

    void doFlash(){
        tvLog.setText(""); log("=== FLASH NON-ROOT START ==="); log("IMG: "+selectedImgPath); log("TARGET: USB OTG - Direct write, no su");
        new Thread(() -> {
            try{
                currentDevice.init();
                Partition p=currentDevice.getPartitions().get(0);
                // MODE 1: RAW DD to block device (partition)
                com.github.mjdev.libaums.driver.BlockDeviceDriver block = p.getBlockDevice();
                long imgSize=new File(selectedImgPath).length();
                log("Block size: "+block.getBlockSize()+" blocks: "+block.getSize()+" img: "+imgSize);
                InputStream in=new FileInputStream(selectedImgPath);
                byte[] buf=new byte[block.getBlockSize()*32];
                long written=0; int read;
                while((read=in.read(buf))>0){
                    block.write(written/block.getBlockSize(), java.nio.ByteBuffer.wrap(buf,0,read));
                    written+=read;
                    int pct=(int)(written*100/imgSize);
                    log("Writing: "+pct+"% "+written/1024/1024+"MB");
                }
                in.close(); currentDevice.close();
                log("=== DONE 100% - NON ROOT SUCCESS ===");
                runOnUiThread(() -> Toast.makeText(this,"Flash Selesai NON-ROOT!",1).show());
            }catch(Exception e){ log("FAIL: "+e.getMessage()); e.printStackTrace(); }
        }).start();
    }

    BroadcastReceiver usbReceiver=new BroadcastReceiver(){
        public void onReceive(Context c,Intent i){ if(ACTION_USB_PERMISSION.equals(i.getAction())){ UsbDevice dev=i.getParcelableExtra(UsbManager.EXTRA_DEVICE); if(i.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){ log("USB Permission GRANTED"); doFlash(); } else log("USB Permission DENIED"); scanUsb(); } }
    };
}
