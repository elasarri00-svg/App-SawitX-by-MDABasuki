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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldFinding
import com.example.model.OperationWork
import com.example.ui.components.FindingItemCard
import com.example.ui.components.FindingStatusChip
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SawitViewModel,
    onNavigateTab: (String) -> Unit,
    onSelectFinding: (FieldFinding) -> Unit,
    onOpenAddFinding: () -> Unit,
    onOpenAddWork: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val works by viewModel.allWorks.collectAsState()
    val findings by viewModel.allFindings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    // Calculations
    val totalWorks = works.size
    val worksDone = works.count { it.status == "SELESAI" }
    val worksInProgress = works.count { it.status == "SEDANG_BERJALAN" }
    val worksPending = works.count { it.status == "BELUM_DIKERJAKAN" }

    val totalFindings = findings.size
    val findingsOpen = findings.count { it.status == "OPEN" }
    val findingsOnProgress = findings.count { it.status == "ON_PROGRESS" || it.status == "ON PROGRESS" }
    val findingsWaitingVerif = findings.count { it.status == "WAITING_VERIFICATION" || it.status == "WAITING VERIFICATION" }
    val findingsRevisi = findings.count { it.status == "REVISI" }
    val findingsVerifiedClosed = findings.count { it.status == "VERIFIED" || it.status == "CLOSED" }
    val findingsOverdue = findings.count { it.priority == "KRITIS" && (it.status == "OPEN" || it.status == "ON_PROGRESS") }

    val completionRate = if (totalFindings > 0) ((findingsVerifiedClosed.toFloat() / totalFindings) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EarthWarmNeutral),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // 1. Estate Context Location Pill
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(PalmGreenContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = PalmGreen40,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user?.estate ?: "Estate Riau Perdana",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SlateGrayDark
                            )
                            Text(
                                text = user?.divisi ?: "Semua Divisi Operasional",
                                fontSize = 10.sp,
                                color = SlateGrayMedium
                            )
                        }
                    }

                    Surface(
                        color = PalmGreenContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "GPS AKURAT",
                            color = PalmGreen40,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Urgent / Critical Action Alert (if any)
        if (findingsWaitingVerif > 0 || findingsOverdue > 0) {
            item {
                Surface(
                    color = if (findingsOverdue > 0) StatusOpenRedContainer else StatusWaitingBlueContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (findingsOverdue > 0) StatusOpenRed.copy(alpha = 0.3f) else StatusWaitingBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (findingsOverdue > 0) StatusOpenRed else StatusWaitingBlue,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (findingsOverdue > 0) Icons.Default.Warning else Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (findingsOverdue > 0) "Perhatian: $findingsOverdue Temuan Kritis / Overdue" else "Ada $findingsWaitingVerif Temuan Menunggu Verifikasi",
                                fontWeight = FontWeight.Bold,
                                color = if (findingsOverdue > 0) StatusOpenRed else StatusWaitingBlue,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Segera lakukan tindak lanjut fisik & tanda tangan digital.",
                                fontSize = 10.sp,
                                color = SlateGrayDark
                            )
                        }
                        Button(
                            onClick = { onNavigateTab("VERIFIKASI") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (findingsOverdue > 0) StatusOpenRed else StatusWaitingBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Periksa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // 3. Highlight KPI Grid (Dual Aspect Square Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Vibrant Emerald Card
                Surface(
                    color = PalmGreen40,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab("PEKERJAAN") }
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF059669), Color(0xFF047857))
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "HARI INI",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text(
                                text = "$worksDone/$totalWorks",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Pekerjaan Selesai",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Card 2: Clean Amber Card
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab("TEMUAN") }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(HarvestGoldContainer, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = HarvestGoldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Surface(
                                color = StatusOpenRedContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "ACTION",
                                    color = StatusOpenRed,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", findingsOpen),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = SlateGrayDark
                            )
                            Text(
                                text = "Temuan Open",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateGrayMedium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Quick Category Row (Panen, Semprot, Pupuk, Infra)
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryLauncherItem(
                        icon = Icons.Default.Agriculture,
                        label = "Panen",
                        onClick = onOpenAddWork
                    )
                    CategoryLauncherItem(
                        icon = Icons.Default.Opacity,
                        label = "Semprot",
                        onClick = onOpenAddWork
                    )
                    CategoryLauncherItem(
                        icon = Icons.Default.Spa,
                        label = "Pupuk",
                        onClick = onOpenAddWork
                    )
                    CategoryLauncherItem(
                        icon = Icons.Default.Engineering,
                        label = "Infra",
                        onClick = { onNavigateTab("PEKERJAAN") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Monitoring Temuan Section
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header with emerald vertical indicator bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
                                    .background(PalmGreen40, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Monitoring Temuan",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = SlateGrayDark
                            )
                        }

                        Surface(
                            color = PalmGreenContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { onNavigateTab("TEMUAN") }
                        ) {
                            Text(
                                text = "Lihat Semua",
                                color = PalmGreen40,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar & Percentage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$findingsVerifiedClosed dari $totalFindings temuan selesai",
                            fontSize = 11.sp,
                            color = SlateGrayMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$completionRate%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = PalmGreen40
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { if (totalFindings > 0) findingsVerifiedClosed.toFloat() / totalFindings else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PalmGreen40,
                        trackColor = SlateGraySoft
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5 Status Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FindingStatusBox("Open", "$findingsOpen", StatusOpenRed, Modifier.weight(1f))
                        FindingStatusBox("Progress", "$findingsOnProgress", StatusProgressYellow, Modifier.weight(1f))
                        FindingStatusBox("Verif", "$findingsWaitingVerif", StatusWaitingBlue, Modifier.weight(1f))
                        FindingStatusBox("Revisi", "$findingsRevisi", StatusRevisionOrange, Modifier.weight(1f))
                        FindingStatusBox("Verified", "$findingsVerifiedClosed", StatusVerifiedGreen, Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. Recent Findings List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TEMUAN TERBARU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGrayMedium,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    color = PalmGreenContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable(onClick = onOpenAddFinding)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = PalmGreen40,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Tambah",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalmGreen40
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (findings.isEmpty()) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada temuan lapangan tercatat.",
                            color = SlateGrayMedium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(findings.take(5)) { finding ->
                FindingItemCard(
                    finding = finding,
                    onClick = { onSelectFinding(finding) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CategoryLauncherItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(PalmGreenContainer, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PalmGreen20,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SlateGrayDark
        )
    }
}

@Composable
fun FindingStatusBox(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
