package com.otg.flasherpro
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val Bg = Color(0xFF0F1A1F)
val BgCard = Color(0xFF1A2828)
val BgCard2 = Color(0xFF1E2F2F)
val Accent = Color(0xFF4CC9A0)
val AccentBlue = Color(0xFF5B9DFF)
val DarkGreen = Color(0xFF123A36)
val TextGray = Color(0xFF8AA5A0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OTGApp() }
    }
}

@Composable
fun OTGApp() {
    var screen by remember { mutableStateOf(1) }
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(2500); showSplash = false }
    MaterialTheme(colorScheme = darkColorScheme(background = Bg)) {
        Box(Modifier.fillMaxSize().background(Bg)) {
            if (showSplash) SplashScreen()
            else {
                when(screen) {
                    1 -> DeviceScreen({ screen=2 }, { screen=3 })
                    2 -> PartisiScreen({ screen=1 }, { screen=3 })
                    3 -> UrutanScreen({ screen=2 }, { screen=4 })
                    4 -> FlashingScreen({ screen=3 })
                    else -> DeviceScreen({ screen=2 }, { screen=3 })
                }
                BottomNav(screen, { screen = it })
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF1A3A35), Bg), radius = 800f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(120.dp).clip(RoundedCornerShape(28.dp)).background(BgCard).background(Brush.linearGradient(listOf(DarkGreen, BgCard))), contentAlignment = Alignment.Center) { Text("⚡", fontSize = 56.sp) }
            Spacer(Modifier.height(24.dp))
            Text("OTG Flasher Pro", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Flasher ADB dan fastboot universal", color = TextGray, fontSize = 13.sp)
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Accent))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Accent.copy(alpha=0.5f)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Accent.copy(alpha=0.2f)))
            }
            Spacer(Modifier.height(12.dp))
            Text("Menunggu perangkat lewat OTG", color = TextGray, fontSize = 12.sp)
        }
        Text("v2.0 · semua perangkat Android", color = TextGray, fontSize = 11.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom=24.dp))
    }
}

@Composable
fun DeviceScreen(toPartisi: ()->Unit, toUrutan: ()->Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp).padding(bottom=70.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("OTG Flasher Pro", color=Color.White, fontWeight=FontWeight.Bold, fontSize=18.sp)
            Box(Modifier.clip(RoundedCornerShape(12.dp)).background(BgCard).padding(horizontal=10.dp, vertical=4.dp)) { Text("v2.0", color=TextGray, fontSize=11.sp) }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(BgCard).padding(16.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(DarkGreen), contentAlignment=Alignment.Center){ Text("📱", fontSize=22.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Redmi 5 Plus", color=Color.White, fontWeight=FontWeight.Bold)
                        Text("● vince · terhubung lewat OTG", color=Accent, fontSize=11.sp)
                    }
                    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(DarkGreen).padding(8.dp,4.dp)){ Text("FASTBOOT", color=Accent, fontSize=10.sp, fontWeight=FontWeight.Bold) }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("Bootloader","Terbuka", Accent, Modifier.weight(1f))
                    InfoChip("Produk","vince", Color.White, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("Slot","A-only", Color.White, Modifier.weight(1f))
                    InfoChip("Unduh maks","512 MB", Color.White, Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("AKSI CEPAT", color=TextGray, fontSize=11.sp, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(10.dp)){
            ActionCard("Baca partisi","dari perangkat","≡", Modifier.weight(1f), toPartisi)
            ActionCard("Cari file otomatis","sesuai vince","⌕", Modifier.weight(1f), toPartisi)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(10.dp)){
            ActionCard("Backup partisi","boot, persist, modem","⬇", Modifier.weight(1f), toPartisi)
            ActionCard("Rencana flash","7 langkah","↗", Modifier.weight(1f), toUrutan)
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Accent).clickable{ toUrutan() }.padding(16.dp), contentAlignment=Alignment.Center){ Text("Mulai flash terpandu", color=Color.Black, fontWeight=FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2615)).padding(12.dp)){ Text("Setiap file dicek SHA-256 dan setiap langkah diverifikasi sebelum lanjut. Partisi persist dan efs dilindungi.", color=Color(0xFFD8C88A), fontSize=11.sp) }
    }
}

@Composable
fun InfoChip(label:String, value:String, valueColor:Color, mod:Modifier){
    Box(mod.clip(RoundedCornerShape(12.dp)).background(BgCard2).padding(12.dp)){
        Column{ Text(label, color=TextGray, fontSize=10.sp); Text(value, color=valueColor, fontWeight=FontWeight.Bold, fontSize=13.sp) }
    }
}
@Composable
fun ActionCard(title:String, sub:String, icon:String, mod:Modifier, onClick:()->Unit){
    Box(mod.clip(RoundedCornerShape(14.dp)).background(BgCard).clickable{ onClick() }.padding(14.dp)){
        Column{ Text(icon, color=Accent, fontSize=16.sp); Spacer(Modifier.height(8.dp)); Text(title, color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp); Text(sub, color=TextGray, fontSize=11.sp) }
    }
}

@Composable
fun PartisiScreen(back:()->Unit, next:()->Unit){
    var selected by remember { mutableStateOf(setOf("boot","system","vendor","modem")) }
    Column(Modifier.fillMaxSize().padding(16.dp).padding(bottom=70.dp)){
        Text("Pilih partisi", color=Color.White, fontWeight=FontWeight.Bold, fontSize=20.sp)
        Text("Dibaca dari perangkat · 8 dari 38 partisi tampil", color=TextGray, fontSize=11.sp)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BgCard).padding(12.dp)){ Text("⌕ Cari partisi", color=TextGray, fontSize=13.sp) }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.weight(1f)){
            item{
                PartitionItem("boot","64 MB · boot.img","backup", selected.contains("boot"), { selected = if(selected.contains("boot")) selected - "boot" else selected + "boot" })
                PartitionItem("recovery","64 MB · belum ada file","", selected.contains("recovery"), { selected = if(selected.contains("recovery")) selected - "recovery" else selected + "recovery" })
                PartitionItem("system","3,0 GB · system.img","hash OK", selected.contains("system"), { selected = if(selected.contains("system")) selected - "system" else selected + "system" })
                PartitionItem("vendor","1,1 GB · vendor.img","hash OK", selected.contains("vendor"), { selected = if(selected.contains("vendor")) selected - "vendor" else selected + "vendor" })
                PartitionItem("modem","66 MB · NON-HLOS.bin","backup", selected.contains("modem"), { selected = if(selected.contains("modem")) selected - "modem" else selected + "modem" })
            }
        }
        Row(verticalAlignment=Alignment.CenterVertically, modifier=Modifier.fillMaxWidth().padding(top=8.dp), horizontalArrangement=Arrangement.SpaceBetween){
            Column{ Text("Total", color=TextGray, fontSize=10.sp); Text("4,2 GB", color=Color.White, fontWeight=FontWeight.Bold) }
            Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Accent).clickable{ next() }.padding(horizontal=24.dp, vertical=14.dp)){ Text("Lanjut ke urutan (${selected.size} dipilih)", color=Color.Black, fontWeight=FontWeight.Bold, fontSize=13.sp) }
        }
    }
}
@Composable
fun PartitionItem(name:String, desc:String, status:String, checked:Boolean, onToggle:()->Unit){
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BgCard).padding(12.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(if(checked) Accent else Bg).border(1.dp, if(checked) Accent else Color.Gray, RoundedCornerShape(4.dp)).clickable{ onToggle() }, contentAlignment=Alignment.Center){ if(checked) Text("✓", color=Color.Black, fontSize=12.sp, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)){
                Text(name, color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp)
                Text(desc, color=TextGray, fontSize=11.sp)
            }
            if(status.isNotEmpty()) Text(status, color=if(status=="backup") Color(0xFFFFC66E) else Accent, fontSize=10.sp)
        }
    }
}

@Composable
fun UrutanScreen(back:()->Unit, next:()->Unit){
    Column(Modifier.fillMaxSize().padding(16.dp).padding(bottom=70.dp)){
        Text("Rencana urutan flash", color=Color.White, fontWeight=FontWeight.Bold, fontSize=20.sp)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.clip(RoundedCornerShape(20.dp)).background(BgCard).padding(8.dp,4.dp)){ Text("📄 Urutan dari flash_all.sh milik ROM", color=AccentBlue, fontSize=11.sp) }
        Spacer(Modifier.height(16.dp))
        val steps = listOf("Verifikasi perangkat" to "getvar product = vince","Backup" to "boot, modem ke /sdcard/backup","Radio" to "flash modem NON-HLOS.bin","Boot" to "flash boot boot.img","Sistem (dipecah 512 MB)" to "flash system system.img","Vendor" to "flash vendor vendor.img","Selesai" to "reboot (userdata tidak disentuh)")
        LazyColumn(Modifier.weight(1f)){
            items(steps.size){ i->
                Row{
                    Box(Modifier.size(28.dp).clip(CircleShape).background(if(i==0) DarkGreen else BgCard).padding(2.dp), contentAlignment=Alignment.Center){ Text("${i+1}", color=if(i==0) Accent else TextGray, fontWeight=FontWeight.Bold, fontSize=12.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.padding(bottom=18.dp)){ Text(steps[i].first, color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp); Text(steps[i].second, color=TextGray, fontSize=11.sp) }
                }
            }
        }
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween, modifier=Modifier.fillMaxWidth()){
            Column{ Text("Perkiraan", color=TextGray, fontSize=10.sp); Text("9 menit", color=Color.White, fontWeight=FontWeight.Bold) }
            Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Accent).clickable{ next() }.padding(horizontal=32.dp, vertical=14.dp)){ Text("Mulai flash", color=Color.Black, fontWeight=FontWeight.Bold) }
        }
    }
}

@Composable
fun FlashingScreen(back:()->Unit){
    var progress by remember { mutableStateOf(62f) }
    LaunchedEffect(Unit){ while(true){ delay(2000); progress = (progress+1)%100 } }
    Column(Modifier.fillMaxSize().padding(16.dp).padding(bottom=70.dp), horizontalAlignment=Alignment.CenterHorizontally){
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){
            Text("Sedang flash", color=Color.White, fontWeight=FontWeight.Bold, fontSize=20.sp)
            Box(Modifier.clip(RoundedCornerShape(20.dp)).background(BgCard).padding(10.dp,4.dp)){ Text("Mode aman", color=Accent, fontSize=11.sp) }
        }
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(160.dp), contentAlignment=Alignment.Center){
            CircularProgressIndicator(progress=progress/100f, strokeWidth=12.dp, color=Accent, trackColor=Color(0xFF233333), modifier=Modifier.fillMaxSize())
            Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("${progress.toInt()}%", color=Color.White, fontWeight=FontWeight.Bold, fontSize=32.sp); Text("langkah 5 dari 7", color=TextGray, fontSize=11.sp) }
        }
        Spacer(Modifier.height(16.dp))
        Text("Flash system", color=Color.White, fontWeight=FontWeight.Bold)
        Text("potongan sparse 3 dari 6", color=TextGray, fontSize=11.sp)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)){
            listOf("Kecepatan" to "38 MB/s", "Terkirim" to "1,9 GB", "Sisa" to "3 menit").forEach{
                Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(BgCard).padding(12.dp)){ Column{ Text(it.first, color=TextGray, fontSize=10.sp); Text(it.second, color=Color.White, fontWeight=FontWeight.Bold, fontSize=12.sp) } }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BgCard).padding(12.dp)){
            Column{
                Text("OK getvar product = vince", color=Accent, fontSize=11.sp)
                Text("OK backup boot.img 64 MB", color=Accent, fontSize=11.sp)
                Text("OK backup modem 66 MB", color=Accent, fontSize=11.sp)
                Text("OK flash modem 0,9 s", color=Accent, fontSize=11.sp)
                Text("OK flash boot 1,2 s", color=Accent, fontSize=11.sp)
                Text(".. flash system 3/6", color=Color(0xFF8AD4FF), fontSize=11.sp)
                Text("sha256 potongan 2 cocok", color=TextGray, fontSize=11.sp)
                Text("menunggu respons OKAY", color=TextGray, fontSize=11.sp)
            }
        }
    }
}

@Composable
fun BottomNav(current:Int, onNav:(Int)->Unit){
    Box(Modifier.fillMaxSize(), contentAlignment=Alignment.BottomCenter){
        Box(Modifier.fillMaxWidth().background(Bg).height(64.dp)){
            Row(Modifier.fillMaxWidth().padding(horizontal=16.dp), horizontalArrangement=Arrangement.SpaceAround){
                NavItem("📱","Perangkat", 1, current==1, { onNav(1) })
                NavItem("≡","Partisi", 2, current==2, { onNav(2) })
                NavItem("↗","Urutan", 3, current==3, { onNav(3) })
                NavItem(">_", "Log", 4, current==4, { onNav(4) })
            }
        }
    }
}
@Composable
fun NavItem(icon:String, label:String, idx:Int, sel:Boolean, onClick:()->Unit){
    Column(Modifier.clickable{ onClick() }.padding(8.dp), horizontalAlignment=Alignment.CenterHorizontally){
        Text(icon, color=if(sel) Accent else TextGray, fontSize=16.sp)
        Text(label, color=if(sel) Accent else TextGray, fontSize=10.sp)
    }
}
