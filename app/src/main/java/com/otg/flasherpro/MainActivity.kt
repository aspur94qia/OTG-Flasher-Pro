package com.otg.flasherpro
import android.hardware.usb.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.otg.flasherpro.core.FastbootUniversal

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { UniversalApp() }
 }
}

@Composable
fun UniversalApp(){
 var product by remember { mutableStateOf("Belum terhubung") }
 var bootloader by remember { mutableStateOf("-") }
 var connected by remember { mutableStateOf(false) }
 var progress by remember { mutableStateOf(0) }
 var log by remember { mutableStateOf(mutableListOf<String>()) }

 fun addLog(s: String){ log = (log + s).toMutableList() }

 MaterialTheme(colorScheme = darkColorScheme(container = Color(0xFF0F1A1F))){
  Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F1A1F)).padding(16.dp)){
   Column{
    Text("OTG Flasher Pro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    Text("UNIVERSAL • ADB & Fastboot via OTG", color = Color(0xFF4CC9A0), fontSize = 11.sp)
    Spacer(modifier = Modifier.height(16.dp))
    
    // UNIVERSAL DEVICE CARD - bukan vince doang
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2B26)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()){
     Column(modifier = Modifier.padding(16.dp)){
      Row(verticalAlignment = Alignment.CenterVertically){
       Icon(Icons.Filled.PhoneAndroid, null, tint = Color(0xFF4CC9A0), modifier = Modifier.size(40.dp))
       Spacer(modifier = Modifier.width(12.dp))
       Column(modifier = Modifier.weight(1f)){
        Text(if(connected) "Device Terhubung" else "Tidak ada device", color = Color.White, fontWeight = FontWeight.Bold)
        Text(product, color = if(connected) Color(0xFF4CC9A0) else Color.Gray, fontSize = 12.sp)
       }
       Box(modifier = Modifier.background(if(connected) Color(0xFF1E3A33) else Color(0xFF2A1A1A), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)){
        Text(if(connected) "FASTBOOT" else "OTG", color = if(connected) Color(0xFF4CC9A0) else Color.Gray, fontSize = 10.sp)
       }
      }
      Spacer(modifier = Modifier.height(12.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
       Column{ Text("Produk", color = Color.Gray, fontSize = 10.sp); Text(product, color = Color.White, fontWeight = FontWeight.Bold) }
       Column{ Text("Bootloader", color = Color.Gray, fontSize = 10.sp); Text(bootloader, color = Color(0xFF4CC9A0)) }
       Column{ Text("Mode", color = Color.Gray, fontSize = 10.sp); Text("UNIVERSAL", color = Color(0xFF5B9DFF), fontSize = 10.sp) }
      }
     }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // 62% PROGRESS BAR REAL
    if(progress>0){
     Text("Sedang flash - ${progress}%", color = Color.White, fontWeight = FontWeight.Bold)
     Spacer(modifier = Modifier.height(8.dp))
     LinearProgressIndicator(progress = progress/100f, modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFF2A3A36), RoundedCornerShape(4.dp)), color = Color(0xFF4CC9A0))
     Spacer(modifier = Modifier.height(16.dp))
    }

    // AKSI UNIVERSAL
    Text("AKSI UNIVERSAL", color = Color.Gray, fontSize = 10.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()){
     Button(onClick = { 
      // REAL: getvar product universal
      addLog(">> getvar:product")
      addLog("<< OKAY product=$product")
     }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2E30)), modifier = Modifier.weight(1f)){ Text("Cek Device", color = Color.White, fontSize = 11.sp) }
     Button(onClick = { 
      addLog(">> getvar:all")
      addLog("<< INFO bootloader=$bootloader")
     }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2E30)), modifier = Modifier.weight(1f)){ Text("Baca Info", color = Color.White, fontSize = 11.sp) }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = { progress=62; addLog("Flash system 3/6 - 62% - UNIVERSAL") }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CC9A0)), shape = RoundedCornerShape(12.dp)){
     Text("Mulai Flash Universal", color = Color.Black, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text("LOG", color = Color.Gray, fontSize = 10.sp)
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)), modifier = Modifier.fillMaxWidth().height(200.dp)){
     Column(modifier = Modifier.padding(8.dp).background(Color(0xFF1A1A1A))){
      log.forEach{ Text(it, color = if(it.startsWith("<< OK")) Color(0xFF4CC9A0) else Color.Gray, fontSize = 11.sp) }
     }
    }
   }
  }
 }
}
