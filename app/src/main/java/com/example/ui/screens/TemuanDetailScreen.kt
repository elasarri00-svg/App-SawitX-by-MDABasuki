package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldFinding
import com.example.ui.components.BeforeAfterCompareView
import com.example.ui.components.FindingStatusChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.WatermarkedPhotoViewer
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemuanDetailScreen(
    finding: FieldFinding,
    viewModel: SawitViewModel,
    onBack: () -> Unit,
    onNavigateToVerify: (FieldFinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val allFindings by viewModel.allFindings.collectAsState()
    val currentFinding = allFindings.find { it.id == finding.id } ?: finding
    val historyList by viewModel.getHistoryForFinding(currentFinding.findingNumber).collectAsState(initial = emptyList())

    var showUpdateProgressDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentFinding.findingNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${currentFinding.estate} • ${currentFinding.blok}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PalmGreenDark),
                actions = {
                    SyncStatusChip(
                        syncStatus = currentFinding.syncStatus,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Update progress button (For PIC / Repairer)
                    if (!currentFinding.isLocked && currentFinding.status != "WAITING_VERIFICATION") {
                        Button(
                            onClick = { showUpdateProgressDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Update Progres", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Direct link to Verification & Digital Sign (if user has canVerify permission)
                    if ((user?.canVerify == true || currentFinding.status == "WAITING_VERIFICATION") && currentFinding.status != "VERIFIED" && currentFinding.status != "CLOSED") {
                        Button(
                            onClick = { onNavigateToVerify(currentFinding) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusWaitingBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verifikasi Lapangan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Close finding button (if verified)
                    if (currentFinding.status == "VERIFIED") {
                        Button(
                            onClick = {
                                viewModel.closeFinding(currentFinding)
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateGrayDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tutup Temuan Resmi", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(EarthWarmNeutral)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Status & Meta Overview Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PriorityBadge(priority = currentFinding.priority)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = SlateGraySoft,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentFinding.findingType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateGrayDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            FindingStatusChip(status = currentFinding.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentFinding.description,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateGrayDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Divider(color = SlateGrayLight.copy(alpha = 0.5f))

                        Spacer(modifier = Modifier.height(10.dp))

                        // Info grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pelapor:", fontSize = 10.sp, color = SlateGrayMedium)
                                Text(currentFinding.reporterName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateGrayDark)
                                Text(currentFinding.reporterJabatan, fontSize = 10.sp, color = SlateGrayMedium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PIC Penanggung Jawab:", fontSize = 10.sp, color = SlateGrayMedium)
                                Text(currentFinding.picName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmGreen40)
                                Text("Target: ${currentFinding.targetDueDate}", fontSize = 10.sp, color = SlateGrayMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // GPS Location Coordinates Box
                        Surface(
                            color = PalmGreen20.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = PalmGreen20,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GPS Temuan: ${String.format(Locale.getDefault(), "%.5f", currentFinding.latitude)}, ${String.format(Locale.getDefault(), "%.5f", currentFinding.longitude)} (Akurasi ±${currentFinding.gpsAccuracy}m)",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = PalmGreen20
                                )
                            }
                        }
                    }
                }
            }

            // 2. Before & After Photo Comparison View
            item {
                BeforeAfterCompareView(
                    photoBeforeWatermark = currentFinding.watermarkText,
                    photoAfterWatermark = "PERBAIKAN SELESAI\n${currentFinding.picName} | Selesai 100%\nLat: ${String.format(Locale.getDefault(), "%.4f", currentFinding.latitude)}, Long: ${String.format(Locale.getDefault(), "%.4f", currentFinding.longitude)}\n${currentFinding.estate} - ${currentFinding.divisi} - ${currentFinding.blok}\nNo: ${currentFinding.findingNumber}",
                    hasAfterPhoto = currentFinding.photoAfter.isNotEmpty()
                )
            }

            // 3. Progress History Timeline (0%, 25%, 50%, 75%, 100%)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HISTORI & TIMELINE PROGRES",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PalmGreen20
                            )
                            Text(
                                text = "Progres: ${currentFinding.progressPercent}%",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = if (currentFinding.progressPercent == 100) PalmGreen40 else HarvestGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { currentFinding.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (currentFinding.progressPercent == 100) PalmGreen40 else HarvestGold,
                            trackColor = SlateGraySoft
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Timeline Items
                        if (historyList.isEmpty()) {
                            Text(
                                text = "Belum ada riwayat perbaikan progres.",
                                fontSize = 12.sp,
                                color = SlateGrayMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            historyList.forEachIndexed { index, hist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Step marker
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(if (hist.percentage == 100) PalmGreen40 else HarvestGold, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${hist.percentage}%",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (index < historyList.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(30.dp)
                                                    .background(SlateGrayLight)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = hist.userName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = SlateGrayDark
                                            )
                                            Text(
                                                text = "${hist.date} ${hist.time}",
                                                fontSize = 10.sp,
                                                color = SlateGrayMedium
                                            )
                                        }
                                        Text(
                                            text = hist.notes,
                                            fontSize = 11.sp,
                                            color = SlateGrayMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUpdateProgressDialog) {
        UpdateProgressDialog(
            finding = currentFinding,
            viewModel = viewModel,
            onDismiss = { showUpdateProgressDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProgressDialog(
    finding: FieldFinding,
    viewModel: SawitViewModel,
    onDismiss: () -> Unit
) {
    var percentage by remember { mutableIntStateOf(finding.progressPercent) }
    var notes by remember { mutableStateOf("") }
    val isSubmittingVerification = percentage == 100

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateFindingProgress(
                        finding = finding,
                        percentage = percentage,
                        notes = notes.ifEmpty { "Update progres perbaikan ke $percentage%" },
                        isSubmittingForVerification = isSubmittingVerification
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSubmittingVerification) StatusWaitingBlue else PalmGreen20
                )
            ) {
                Text(
                    text = if (isSubmittingVerification) "Ajukan Verifikasi (100%)" else "Simpan Progres",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = SlateGrayMedium)
            }
        },
        title = {
            Text(
                text = "Update Progres Temuan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PalmGreen20
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pilih Persentase Selesai:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGrayMedium)

                // 5 Percentage Steps: 0%, 25%, 50%, 75%, 100%
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(0 to "0% Belum", 25 to "25% Persiapan", 50 to "50% Proses", 75 to "75% Hampir", 100 to "100% Selesai").forEach { (pct, lbl) ->
                        FilterChip(
                            selected = percentage == pct,
                            onClick = { percentage = pct },
                            label = { Text("$pct%", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (pct == 100) PalmGreen40 else HarvestGold,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Perbaikan / Tindak Lanjut") },
                    placeholder = { Text("Uraikan pekerjaan yang sudah dilakukan...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                if (isSubmittingVerification) {
                    Surface(
                        color = StatusWaitingBlueContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = StatusWaitingBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Foto Sesudah & GPS verifikasi akan otomatis terlampir saat status menjadi 100%.",
                                fontSize = 10.sp,
                                color = StatusWaitingBlue
                            )
                        }
                    }
                }
            }
        }
    )
}
