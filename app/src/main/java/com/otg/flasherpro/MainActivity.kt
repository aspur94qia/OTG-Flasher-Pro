package com.otg.flasherpro
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var splash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit){ delay(2500); splash=false }
            if(splash){
                Box(Modifier.fillMaxSize().background(Color(0xFF0F1A1F)), contentAlignment=Alignment.Center){
                    Column(horizontalAlignment=androidx.compose.ui.Alignment.CenterHorizontally){
                        Text("⚡", fontSize=56.sp); Text("OTG Flasher Pro", color=Color.White, fontSize=26.sp); Text("v2.0", color=Color(0xFF4CC9A0))
                    }
                }
            } else {
                Box(Modifier.fillMaxSize().background(Color(0xFF0F1A1F)).padding(16.dp), contentAlignment=Alignment.Center){
                    Text("OTG Flasher Pro - Device Connected: Redmi 5 Plus FASTBOOT\n\nBuild full 400 baris akan saya push setelah ini", color=Color.White)
                }
            }
        }
    }
}
