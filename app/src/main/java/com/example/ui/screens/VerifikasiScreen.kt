package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldFinding
import com.example.ui.components.BeforeAfterCompareView
import com.example.ui.components.DigitalSignaturePad
import com.example.ui.components.FindingItemCard
import com.example.ui.components.FindingStatusChip
import com.example.ui.components.GpsAccuracyBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiScreen(
    viewModel: SawitViewModel,
    preselectedFinding: FieldFinding? = null,
    onSelectFindingDetail: (FieldFinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val findings by viewModel.allFindings.collectAsState()
    val verifications by viewModel.allVerifications.collectAsState()

    var activeTab by remember { mutableStateOf("MENUNGGU") } // MENUNGGU, RIWAYAT
    var activeVerifyingFinding by remember { mutableStateOf<FieldFinding?>(preselectedFinding) }

    val waitingList = findings.filter {
        it.status == "WAITING_VERIFICATION" || it.status == "WAITING VERIFICATION"
    }

    val verifiedList = findings.filter {
        it.status == "VERIFIED" || it.status == "CLOSED"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthWarmNeutral)
    ) {
        // Header
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
                            text = "VERIFIKASI & TTD DIGITAL",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Inspeksi Fisik Lapangan & Pengesahan Digital",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        color = if (waitingList.isNotEmpty()) StatusWaitingBlue else PalmGreen40,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${waitingList.size} Menunggu",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = if (activeTab == "MENUNGGU") 0 else 1,
            containerColor = Color.White,
            contentColor = PalmGreen20
        ) {
            Tab(
                selected = activeTab == "MENUNGGU",
                onClick = { activeTab = "MENUNGGU" },
                text = { Text("Menunggu Verifikasi (${waitingList.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == "RIWAYAT",
                onClick = { activeTab = "RIWAYAT" },
                text = { Text("Riwayat Terverifikasi (${verifiedList.size})", fontWeight = FontWeight.Bold) }
            )
        }

        // Body List
        if (activeTab == "MENUNGGU") {
            if (waitingList.isEmpty()) {
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
                            tint = PalmGreen40,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Semua temuan telah terverifikasi!",
                            fontWeight = FontWeight.Bold,
                            color = SlateGrayDark,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tidak ada temuan yang tertunda di antrean verifikasi.",
                            color = SlateGrayMedium,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(waitingList) { finding ->
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
                                        Text(
                                            text = finding.findingNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = PalmGreen20
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        PriorityBadge(priority = finding.priority)
                                    }
                                    FindingStatusChip(status = finding.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = finding.description,
                                    fontSize = 13.sp,
                                    color = SlateGrayDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "PIC: ${finding.picName} • ${finding.estate} ${finding.divisi} ${finding.blok}",
                                    fontSize = 11.sp,
                                    color = SlateGrayMedium
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onSelectFindingDetail(finding) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Lihat Riwayat", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { activeVerifyingFinding = finding },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusWaitingBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verifikasi & TTD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Verified List History
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(verifiedList) { finding ->
                    FindingItemCard(
                        finding = finding,
                        onClick = { onSelectFindingDetail(finding) }
                    )
                }
            }
        }
    }

    // Modal Sheet: Perform Verification with Digital Signature
    activeVerifyingFinding?.let { findingToVerify ->
        PerformVerificationModal(
            finding = findingToVerify,
            viewModel = viewModel,
            onDismiss = { activeVerifyingFinding = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformVerificationModal(
    finding: FieldFinding,
    viewModel: SawitViewModel,
    onDismiss: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val lat by viewModel.currentLatitude.collectAsState()
    val lng by viewModel.currentLongitude.collectAsState()
    val accuracy by viewModel.currentGpsAccuracy.collectAsState()

    var hasSignature by remember { mutableStateOf(false) }
    var signatureData by remember { mutableStateOf("") }
    var showRevisionInput by remember { mutableStateOf(false) }
    var revisionReason by remember { mutableStateOf("") }
    var agreeStatement by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (showRevisionInput) {
                Button(
                    onClick = {
                        if (revisionReason.isNotEmpty()) {
                            viewModel.submitVerification(
                                finding = finding,
                                isApproved = false,
                                revisionReason = revisionReason,
                                signatureSvg = ""
                            )
                            onDismiss()
                        } else {
                            viewModel.showMessage("Harap isi alasan penolakan/revisi terlebih dahulu.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRevisionOrange)
                ) {
                    Text("Kirim Keputusan Revisi", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (hasSignature && agreeStatement) {
                            viewModel.submitVerification(
                                finding = finding,
                                isApproved = true,
                                revisionReason = "",
                                signatureSvg = signatureData
                            )
                            onDismiss()
                        } else {
                            viewModel.showMessage("Harap berikan tanda tangan digital di layar terlebih dahulu.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusVerifiedGreen),
                    enabled = hasSignature && agreeStatement
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("TERIMA & TANDA TANGAN (VERIFIED)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            if (!showRevisionInput) {
                OutlinedButton(
                    onClick = { showRevisionInput = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRevisionOrange)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tolak / Revisi", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            } else {
                TextButton(onClick = { showRevisionInput = false }) {
                    Text("Kembali ke Verifikasi")
                }
            }
        },
        title = {
            Column {
                Text(
                    text = "Verifikasi Lapangan: ${finding.findingNumber}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = PalmGreen20
                )
                Text(
                    text = "Verifikator: ${user?.fullName ?: "Petugas"} (${user?.jabatan ?: "Asisten"})",
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
                if (showRevisionInput) {
                    // Revision Form
                    item {
                        Surface(
                            color = StatusRevisionOrangeContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Formulir Catatan Revisi Perbaikan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = StatusRevisionOrange
                                )
                                Text(
                                    text = "Wajib mengisi alasan teknis mengapa perbaikan belum diterima.",
                                    fontSize = 11.sp,
                                    color = SlateGrayDark
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = revisionReason,
                            onValueChange = { revisionReason = it },
                            label = { Text("Alasan Penolakan / Revisi *") },
                            placeholder = { Text("Contoh: Parit masih ada sisa endapan lumpur...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                } else {
                    // Approval Workflow: Photos + GPS + TTD
                    item {
                        BeforeAfterCompareView(
                            photoBeforeWatermark = finding.watermarkText,
                            photoAfterWatermark = "KONDISI SELESAI\nPIC: ${finding.picName}\nLat: ${String.format(Locale.getDefault(), "%.4f", lat)}, Long: ${String.format(Locale.getDefault(), "%.4f", lng)}\n${finding.estate} - ${finding.blok}\nNo: ${finding.findingNumber}",
                            hasAfterPhoto = true
                        )
                    }

                    item {
                        Surface(
                            color = SlateGraySoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Perbandingan GPS Koordinat:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = SlateGrayDark
                                )
                                Text(
                                    text = "• Lokasi Awal: ${String.format(Locale.getDefault(), "%.5f", finding.latitude)}, ${String.format(Locale.getDefault(), "%.5f", finding.longitude)}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SlateGrayMedium
                                )
                                Text(
                                    text = "• Lokasi Verifikasi: ${String.format(Locale.getDefault(), "%.5f", lat)}, ${String.format(Locale.getDefault(), "%.5f", lng)} (±${String.format(Locale.getDefault(), "%.1f", accuracy)}m)",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = PalmGreen40,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        DigitalSignaturePad(
                            onSignatureChanged = { hasSig, pData ->
                                hasSignature = hasSig
                                signatureData = pData
                            }
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = agreeStatement,
                                onCheckedChange = { agreeStatement = it }
                            )
                            Text(
                                text = "Saya menyatakan telah memeriksa secara langsung kondisi fisik lapangan dan keabsahan perbaikan temuan ini.",
                                fontSize = 10.sp,
                                color = SlateGrayDark
                            )
                        }
                    }
                }
            }
        }
    )
}
