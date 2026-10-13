package com.otg.flasherpro.core
import android.hardware.usb.*
import android.util.Log

class FastbootUniversal(private val conn: UsbDeviceConnection, private val epIn: UsbEndpoint, private val epOut: UsbEndpoint) {
 fun send(cmd: String): String {
  val buf = cmd.toByteArray()
  conn.bulkTransfer(epOut, buf, buf.size, 5000)
  val resp = ByteArray(1024)
  val n = conn.bulkTransfer(epIn, resp, resp.size, 5000)
  return if(n>0) String(resp,0,n) else ""
 }
 fun getVar(name: String): String {
  val r = send("getvar:$name")
  return r.replace("OKAY","").replace("INFO","").trim()
 }
 fun flash(partition: String, data: ByteArray, onProgress: (Int)->Unit): Boolean {
  // UNIVERSAL: pecah 512MB sesuai screenshot tapi untuk semua device
  val chunk = 512*1024*1024
  var offset = 0
  while(offset < data.size){
   val end = minOf(offset+chunk, data.size)
   val piece = data.sliceArray(offset until end)
   // download
   var resp = send("download:${String.format("%08x", piece.size)}")
   if(!resp.contains("DATA")){
    Log.e("FLASH","download fail $resp"); return false
   }
   conn.bulkTransfer(epOut, piece, piece.size, 10000)
   resp = ByteArray(64).let{
    conn.bulkTransfer(epIn, it, it.size, 5000)
    String(it).trim()
   }
   // flash
   resp = send("flash:$partition")
   if(!resp.contains("OKAY")) return false
   offset = end
   onProgress((offset*100/data.size))
  }
  return true
 }
}
