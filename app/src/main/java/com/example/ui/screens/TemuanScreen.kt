package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.FieldFinding
import com.example.ui.components.FindingItemCard
import com.example.ui.components.FindingStatusChip
import com.example.ui.components.GpsAccuracyBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.WatermarkedPhotoViewer
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemuanScreen(
    viewModel: SawitViewModel,
    onSelectFinding: (FieldFinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val findings by viewModel.allFindings.collectAsState()
    var selectedStatusFilter by remember { mutableStateOf("SEMUA") }
    var selectedTypeFilter by remember { mutableStateOf("SEMUA") }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val statusFilters = listOf("SEMUA", "OPEN", "ON PROGRESS", "WAITING VERIFICATION", "REVISI", "VERIFIED", "CLOSED")
    val typeFilters = listOf(
        "SEMUA", "Panen", "Semprot", "Pupuk", "Infrastruktur", "Jalan", "Drainase",
        "K3", "Lingkungan", "Alat kerja", "Alat berat", "Kendaraan", "Tanaman", "Hama/penyakit", "Keamanan", "Lainnya"
    )

    val filteredFindings = findings.filter { finding ->
        val matchesStatus = if (selectedStatusFilter == "SEMUA") true else {
            val normalizedStatus = finding.status.replace("_", " ")
            normalizedStatus.equals(selectedStatusFilter, ignoreCase = true)
        }
        val matchesType = if (selectedTypeFilter == "SEMUA") true else finding.findingType.equals(selectedTypeFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() ||
                finding.findingNumber.contains(searchQuery, ignoreCase = true) ||
                finding.description.contains(searchQuery, ignoreCase = true) ||
                finding.blok.contains(searchQuery, ignoreCase = true) ||
                finding.picName.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesType && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = StatusOpenRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddAPhoto, contentDescription = null) },
                text = { Text("Buat Temuan Lapangan", fontWeight = FontWeight.Bold) }
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
                                text = "MONITORING TEMUAN LAPANGAN",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Dokumentasi Foto Watermark, GPS & Progres Perbaikan",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            color = HarvestGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${filteredFindings.size} Temuan",
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
                        placeholder = { Text("Cari nomor temuan, blok, PIC, atau deskripsi...", fontSize = 12.sp, color = SlateGrayLight) },
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

            // Status Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(statusFilters) { status ->
                    val isSelected = selectedStatusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = status },
                        label = { Text(status, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PalmGreen20,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Category Type Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGraySoft.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(typeFilters) { type ->
                    val isSelected = selectedTypeFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTypeFilter = type },
                        label = { Text(type, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HarvestGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // Findings List
            if (filteredFindings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = SlateGrayMedium.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada temuan dengan filter yang dipilih.",
                            color = SlateGrayMedium,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFindings) { finding ->
                        FindingItemCard(
                            finding = finding,
                            onClick = { onSelectFinding(finding) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateFindingDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFindingDialog(
    viewModel: SawitViewModel,
    onDismiss: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val lat by viewModel.currentLatitude.collectAsState()
    val lng by viewModel.currentLongitude.collectAsState()
    val accuracy by viewModel.currentGpsAccuracy.collectAsState()

    var estate by remember { mutableStateOf(user?.estate ?: "Estate Riau Perdana") }
    var divisi by remember { mutableStateOf(user?.divisi ?: "Divisi 2") }
    var blok by remember { mutableStateOf("Blok B08") }
    var findingType by remember { mutableStateOf("Panen") }
    var priority by remember { mutableStateOf("TINGGI") }
    var description by remember { mutableStateOf("") }
    var picName by remember { mutableStateOf("Budi Santoso") }
    var targetDueDate by remember { mutableStateOf("2026-08-22") }

    val findingTypesList = listOf(
        "Panen", "Semprot", "Pupuk", "Infrastruktur", "Jalan", "Drainase",
        "K3", "Lingkungan", "Alat kerja", "Alat berat", "Kendaraan", "Tanaman",
        "Hama/penyakit", "Keamanan", "Housekeeping", "Lainnya"
    )

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val today = dateFormat.format(Date())
    val time = timeFormat.format(Date())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotEmpty()) {
                        viewModel.createFinding(
                            estate = estate,
                            divisi = divisi,
                            blok = blok,
                            findingType = findingType,
                            priority = priority,
                            description = description,
                            picName = picName,
                            targetDueDate = targetDueDate,
                            lat = lat,
                            lng = lng,
                            accuracy = accuracy
                        )
                        onDismiss()
                    } else {
                        viewModel.showMessage("Harap isi deskripsi temuan lapangan terlebih dahulu.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusOpenRed)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan Temuan & Watermark", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = SlateGrayMedium)
            }
        },
        title = {
            Column {
                Text(
                    text = "Buat Temuan Lapangan",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = StatusOpenRed
                )
                Text(
                    text = "Foto Kamera Watermark & GPS Wajib Dilampirkan",
                    fontSize = 11.sp,
                    color = SlateGrayMedium
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Pelapor & Waktu
                item {
                    Surface(
                        color = SlateGraySoft,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Pelapor: ${user?.fullName ?: "Petugas"} (${user?.jabatan ?: "Operasional"})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SlateGrayDark
                            )
                            Text(
                                text = "Waktu: $today $time WIB",
                                fontSize = 11.sp,
                                color = SlateGrayMedium
                            )
                        }
                    }
                }

                // Location fields
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = divisi,
                            onValueChange = { divisi = it },
                            label = { Text("Divisi") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = blok,
                            onValueChange = { blok = it },
                            label = { Text("Blok") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Jenis Temuan Chips
                item {
                    Text("Jenis Temuan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(findingTypesList) { type ->
                            FilterChip(
                                selected = findingType == type,
                                onClick = { findingType = type },
                                label = { Text(type, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PalmGreen20,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Priority selection
                item {
                    Text("Tingkat Prioritas:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("RENDAH", "SEDANG", "TINGGI", "KRITIS").forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (p == "KRITIS") StatusOpenRed else if (p == "TINGGI") Color(0xFFE65100) else HarvestGold,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Deskripsi Temuan
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Detail Temuan *") },
                        placeholder = { Text("Contoh: Buat mentah terpanen 12 janjang...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                // PIC & Target Date
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = picName,
                            onValueChange = { picName = it },
                            label = { Text("Nama PIC") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetDueDate,
                            onValueChange = { targetDueDate = it },
                            label = { Text("Target Selesai") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Camera Watermark Simulation View
                item {
                    WatermarkedPhotoViewer(
                        title = "$findingType ($blok)",
                        watermarkText = "TEMUAN PERKEBUNAN\n${user?.fullName} | $today $time\nLat: ${String.format(Locale.getDefault(), "%.4f", lat)}, Long: ${String.format(Locale.getDefault(), "%.4f", lng)}\n$estate - $divisi - $blok\nNo: TMN-AUTO-GENERATED",
                        photoType = "SEBELUM"
                    )
                }

                // GPS Accuracy & "AMBIL LOKASI SAYA"
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
