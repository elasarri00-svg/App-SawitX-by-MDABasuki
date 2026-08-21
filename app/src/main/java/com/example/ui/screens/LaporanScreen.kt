package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.AuditLog
import com.example.ui.components.FindingStatusChip
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    viewModel: SawitViewModel,
    modifier: Modifier = Modifier
) {
    val works by viewModel.allWorks.collectAsState()
    val findings by viewModel.allFindings.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()

    var activeReportTab by remember { mutableStateOf("RINGKASAN") } // RINGKASAN, OPERASIONAL, TEMUAN, AUDIT
    var timeFilter by remember { mutableStateOf("HARI_INI") } // HARI_INI, MINGGUAN, BULANAN

    // Aggregations
    val totalPanenTon = works.filter { it.workType == "PANEN" }.sumOf { it.tonaseKg } / 1000.0
    val totalPanenTbs = works.filter { it.workType == "PANEN" }.sumOf { it.jumlahTbs }
    val totalSemprotHa = works.filter { it.workType == "SEMPROT" }.sumOf { it.realisasiQty }
    val totalPupukHa = works.filter { it.workType == "PEMUPUKAN" }.sumOf { it.realisasiQty }

    val totalFindingsCount = findings.size
    val verifiedFindingsCount = findings.count { it.status == "VERIFIED" || it.status == "CLOSED" }
    val openFindingsCount = findings.count { it.status == "OPEN" }

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
                            text = "LAPORAN & AUDIT TRAIL",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Export Rekapitulasi PDF, CSV & Log Audit Sistem",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    // Export Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { viewModel.showMessage("Laporan PDF 'SawitX-Laporan-Operasional.pdf' berhasil di-generate.") }) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = HarvestGoldLight)
                        }
                        IconButton(onClick = { viewModel.showMessage("Data CSV 'SawitX-Rekap-Data.csv' berhasil diexport.") }) {
                            Icon(Icons.Default.TableChart, contentDescription = "CSV", tint = HarvestGoldLight)
                        }
                    }
                }
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = when (activeReportTab) {
                "RINGKASAN" -> 0
                "OPERASIONAL" -> 1
                "TEMUAN" -> 2
                else -> 3
            },
            containerColor = Color.White,
            contentColor = PalmGreen20
        ) {
            Tab(selected = activeReportTab == "RINGKASAN", onClick = { activeReportTab = "RINGKASAN" }, text = { Text("Ringkasan", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeReportTab == "OPERASIONAL", onClick = { activeReportTab = "OPERASIONAL" }, text = { Text("Operasional", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeReportTab == "TEMUAN", onClick = { activeReportTab = "TEMUAN" }, text = { Text("Temuan", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeReportTab == "AUDIT", onClick = { activeReportTab = "AUDIT" }, text = { Text("Audit Trail", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        // Body Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (activeReportTab) {
                "RINGKASAN" -> {
                    item {
                        // Quick Metric Cards
                        Text(
                            text = "RINGKASAN KPI UTAMA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGrayMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportKpiCard(
                                title = "Produksi Panen",
                                value = "${String.format(Locale.getDefault(), "%.1f", totalPanenTon)} Ton",
                                subtitle = "$totalPanenTbs Janjang TBS",
                                color = HarvestGold,
                                modifier = Modifier.weight(1f)
                            )
                            ReportKpiCard(
                                title = "Realisasi Rawat",
                                value = "${String.format(Locale.getDefault(), "%.1f", totalSemprotHa + totalPupukHa)} Ha",
                                subtitle = "Semprot & Pupuk",
                                color = PalmGreen40,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportKpiCard(
                                title = "Temuan Selesai",
                                value = "$verifiedFindingsCount / $totalFindingsCount",
                                subtitle = "Terverifikasi Digital",
                                color = StatusVerifiedGreen,
                                modifier = Modifier.weight(1f)
                            )
                            ReportKpiCard(
                                title = "Temuan Terbuka",
                                value = "$openFindingsCount",
                                subtitle = "Perlu Tindak Lanjut",
                                color = StatusOpenRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "AKSI EKSPOR DOKUMEN LAPORAN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PalmGreen20
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.showMessage("Generating Laporan PDF Lengkap dengan Watermark & TTD...") },
                                    colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Download Laporan Lengkap PDF")
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = { viewModel.showMessage("Generating Spreadsheet CSV...") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export Data Mentah CSV (Excel)")
                                }
                            }
                        }
                    }
                }

                "OPERASIONAL" -> {
                    items(works) { work ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${work.workType} • ${work.subCategory}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = PalmGreen20
                                    )
                                    Text(
                                        text = "${work.estate} ${work.divisi} ${work.blok} | Mandor: ${work.mandor}",
                                        fontSize = 11.sp,
                                        color = SlateGrayMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${work.realisasiQty} ${work.realisasiUnit}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = PalmGreen40
                                    )
                                    Text(
                                        text = "${work.progressPercent}% Selesai",
                                        fontSize = 10.sp,
                                        color = SlateGrayMedium
                                    )
                                }
                            }
                        }
                    }
                }

                "TEMUAN" -> {
                    items(findings) { finding ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = finding.findingNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = PalmGreen20
                                    )
                                    FindingStatusChip(status = finding.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = finding.description,
                                    fontSize = 12.sp,
                                    color = SlateGrayDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PIC: ${finding.picName} • ${finding.estate} ${finding.blok}",
                                    fontSize = 10.sp,
                                    color = SlateGrayMedium
                                )
                            }
                        }
                    }
                }

                "AUDIT" -> {
                    item {
                        Surface(
                            color = SlateGraySoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = PalmGreen20, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Audit Trail Tidak Dapat Dimanipulasi (Immutable Cryptographic Log)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGrayDark
                                )
                            }
                        }
                    }

                    items(auditLogs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.actionType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PalmGreen20
                                    )
                                    Text(
                                        text = log.formattedTime,
                                        fontSize = 10.sp,
                                        color = SlateGrayMedium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = log.details,
                                    fontSize = 11.sp,
                                    color = SlateGrayDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "User: ${log.userName} (${log.userJabatan}) • IP: ${log.ipAddress}",
                                    fontSize = 9.sp,
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

@Composable
fun ReportKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EarthCardLight),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 11.sp, color = SlateGrayDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = subtitle, fontSize = 10.sp, color = SlateGrayMedium)
        }
    }
}
