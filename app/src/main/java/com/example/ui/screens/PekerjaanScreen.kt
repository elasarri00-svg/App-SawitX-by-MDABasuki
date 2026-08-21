package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OperationWork
import com.example.ui.components.GpsAccuracyBadge
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.WatermarkedPhotoViewer
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PekerjaanScreen(
    viewModel: SawitViewModel,
    modifier: Modifier = Modifier
) {
    val works by viewModel.allWorks.collectAsState()
    var selectedCategoryTab by remember { mutableStateOf("SEMUA") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddWorkDialog by remember { mutableStateOf(false) }

    val categories = listOf("SEMUA", "PANEN", "SEMPROT", "PEMUPUKAN", "INFRASTRUKTUR", "LAINNYA")

    val filteredWorks = works.filter { work ->
        val matchesCategory = if (selectedCategoryTab == "SEMUA") true else work.workType.equals(selectedCategoryTab, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() ||
                work.subCategory.contains(searchQuery, ignoreCase = true) ||
                work.blok.contains(searchQuery, ignoreCase = true) ||
                work.mandor.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddWorkDialog = true },
                containerColor = PalmGreen20,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Input Pekerjaan", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EarthWarmNeutral)
                .padding(innerPadding)
        ) {
            // Header Bar
            Surface(
                color = PalmGreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MONITORING OPERASIONAL",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Panen, Rawat, Pemupukan & Infrastruktur",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            color = HarvestGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${filteredWorks.size} Kegiatan",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari blok, mandor, atau jenis pekerjaan...", fontSize = 12.sp, color = SlateGrayLight) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HarvestGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = HarvestGold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }

            // Category Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryTab == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryTab = cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PalmGreen20,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Works List
            if (filteredWorks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = SlateGrayMedium.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada catatan pekerjaan untuk kategori ini.",
                            color = SlateGrayMedium,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredWorks) { work ->
                        OperationWorkCard(work = work)
                    }
                }
            }
        }
    }

    if (showAddWorkDialog) {
        AddOperationWorkDialog(
            viewModel = viewModel,
            onDismiss = { showAddWorkDialog = false }
        )
    }
}

@Composable
fun OperationWorkCard(work: OperationWork, modifier: Modifier = Modifier) {
    val (typeColor, typeIcon) = when (work.workType.uppercase()) {
        "PANEN" -> Pair(HarvestGold, Icons.Default.Yard)
        "SEMPROT" -> Pair(PalmGreen40, Icons.Default.Grass)
        "PEMUPUKAN" -> Pair(Color(0xFF00897B), Icons.Default.Science)
        "INFRASTRUKTUR" -> Pair(Color(0xFFE65100), Icons.Default.Construction)
        else -> Pair(SlateGrayMedium, Icons.Default.Assignment)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = EarthCardLight),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Badge + Sync Chip + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = typeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = work.workType, color = typeColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = work.blok,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = PalmGreen20
                    )
                }

                SyncStatusChip(syncStatus = work.syncStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subcategory / Description
            Text(
                text = work.subCategory,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SlateGrayDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Estate & Location details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${work.estate} • ${work.divisi}",
                    fontSize = 11.sp,
                    color = SlateGrayMedium
                )
                Text(
                    text = "Mandor: ${work.mandor}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateGrayDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target vs Realisasi Box
            Surface(
                color = SlateGraySoft.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TARGET", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)
                        Text(
                            text = "${work.targetQty} ${work.targetUnit}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGrayDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "REALISASI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PalmGreen40)
                        Text(
                            text = "${work.realisasiQty} ${work.realisasiUnit}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = PalmGreen40
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "PROGRES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)
                        Text(
                            text = "${work.progressPercent}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (work.progressPercent == 100) PalmGreen40 else HarvestGold
                        )
                    }
                }
            }

            // Specific Details by Type
            if (work.workType == "PANEN" && work.jumlahTbs > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TBS: ${work.jumlahTbs} Janjang (${work.tonaseKg} Kg)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = work.kondisiBuah,
                        fontSize = 11.sp,
                        color = PalmGreen40,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (work.workType == "SEMPROT" && work.jenisHerbisida.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Herbisida: ${work.jenisHerbisida} • Dosis: ${work.dosisHerbisida}",
                    fontSize = 11.sp,
                    color = SlateGrayMedium
                )
            } else if (work.workType == "PEMUPUKAN" && work.jenisPupuk.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pupuk: ${work.jenisPupuk} • Dosis: ${work.dosisPupukKgPokok} kg/pokok",
                    fontSize = 11.sp,
                    color = SlateGrayMedium
                )
            }

            if (work.catatan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${work.catatan}",
                    fontSize = 11.sp,
                    color = SlateGrayMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: GPS & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = PalmGreen40,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.4f", work.latitude)}, ${String.format(Locale.getDefault(), "%.4f", work.longitude)} (±${work.gpsAccuracy}m)",
                        fontSize = 10.sp,
                        color = SlateGrayMedium
                    )
                }

                Surface(
                    color = if (work.status == "SELESAI") PalmGreen20 else HarvestGold,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = work.status.replace("_", " "),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOperationWorkDialog(
    viewModel: SawitViewModel,
    onDismiss: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val lat by viewModel.currentLatitude.collectAsState()
    val lng by viewModel.currentLongitude.collectAsState()
    val accuracy by viewModel.currentGpsAccuracy.collectAsState()

    var workType by remember { mutableStateOf("PANEN") }
    var subCategory by remember { mutableStateOf("Panen ancak harian") }
    var estate by remember { mutableStateOf(user?.estate ?: "Estate Riau Perdana") }
    var divisi by remember { mutableStateOf(user?.divisi ?: "Divisi 1") }
    var blok by remember { mutableStateOf("Blok A15") }
    var mandor by remember { mutableStateOf(user?.fullName ?: "Budi Santoso") }
    var pic by remember { mutableStateOf("Regu Pemanen 1") }
    var workersCount by remember { mutableStateOf("12") }
    var luasHa by remember { mutableStateOf("25.0") }
    var targetQty by remember { mutableStateOf("15000") }
    var realisasiQty by remember { mutableStateOf("15400") }
    var unit by remember { mutableStateOf("Kg") }
    var jumlahTbs by remember { mutableStateOf("820") }
    var kondisiBuah by remember { mutableStateOf("Masak Optimal") }
    var jenisHerbisida by remember { mutableStateOf("Glifosat 480 SL") }
    var jenisPupuk by remember { mutableStateOf("Urea 46% N") }
    var catatan by remember { mutableStateOf("Kondisi ancak bersih, buah terkumpul di TPH.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = dateFormat.format(Date())
                    val tQty = targetQty.toDoubleOrNull() ?: 1.0
                    val rQty = realisasiQty.toDoubleOrNull() ?: 0.0
                    val progress = if (tQty > 0) ((rQty / tQty) * 100).toInt().coerceIn(0, 100) else 100

                    val newWork = OperationWork(
                        id = "WRK-${System.currentTimeMillis()}",
                        workType = workType,
                        subCategory = subCategory,
                        estate = estate,
                        divisi = divisi,
                        blok = blok,
                        date = today,
                        mandor = mandor,
                        pic = pic,
                        workersCount = workersCount.toIntOrNull() ?: 5,
                        luasHa = luasHa.toDoubleOrNull() ?: 10.0,
                        targetQty = tQty,
                        targetUnit = unit,
                        realisasiQty = rQty,
                        realisasiUnit = unit,
                        progressPercent = progress,
                        jumlahTbs = jumlahTbs.toIntOrNull() ?: 0,
                        tonaseKg = rQty,
                        kondisiBuah = kondisiBuah,
                        jenisHerbisida = jenisHerbisida,
                        jenisPupuk = jenisPupuk,
                        latitude = lat,
                        longitude = lng,
                        gpsAccuracy = accuracy,
                        catatan = catatan,
                        status = if (progress >= 100) "SELESAI" else "SEDANG_BERJALAN",
                        syncStatus = "SYNCED"
                    )
                    viewModel.createOperationWork(newWork)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20)
            ) {
                Text("Simpan Laporan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = SlateGrayMedium)
            }
        },
        title = {
            Text(
                text = "Input Pekerjaan Operasional",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PalmGreen20
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Jenis Pekerjaan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("PANEN", "SEMPROT", "PEMUPUKAN", "INFRASTRUKTUR").forEach { type ->
                            FilterChip(
                                selected = workType == type,
                                onClick = {
                                    workType = type
                                    unit = if (type == "PANEN") "Kg" else if (type == "INFRASTRUKTUR") "Meter" else "Ha"
                                },
                                label = { Text(type, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PalmGreen20,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = subCategory,
                        onValueChange = { subCategory = it },
                        label = { Text("Nama/Sub-Pekerjaan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = blok,
                            onValueChange = { blok = it },
                            label = { Text("Blok") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = luasHa,
                            onValueChange = { luasHa = it },
                            label = { Text("Luas (Ha)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetQty,
                            onValueChange = { targetQty = it },
                            label = { Text("Target ($unit)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = realisasiQty,
                            onValueChange = { realisasiQty = it },
                            label = { Text("Realisasi ($unit)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (workType == "PANEN") {
                    item {
                        OutlinedTextField(
                            value = jumlahTbs,
                            onValueChange = { jumlahTbs = it },
                            label = { Text("Jumlah TBS (Janjang)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = { Text("Catatan Lapangan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    GpsAccuracyBadge(
                        accuracy = accuracy,
                        latitude = lat,
                        longitude = lng,
                        onRefreshLocation = { viewModel.refreshGpsLocation() }
                    )
                }
            }
        }
    )
}
