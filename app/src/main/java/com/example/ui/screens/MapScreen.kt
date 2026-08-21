package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.FieldFinding
import com.example.ui.components.FindingItemCard
import com.example.ui.components.PlantationMapCanvas
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel

@Composable
fun MapScreen(
    viewModel: SawitViewModel,
    onSelectFinding: (FieldFinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val findings by viewModel.allFindings.collectAsState()
    var selectedStatus by remember { mutableStateOf("SEMUA") }
    var selectedFindingOnMap by remember { mutableStateOf<FieldFinding?>(null) }

    val statusFilters = listOf("SEMUA", "OPEN", "ON PROGRESS", "WAITING VERIFICATION", "REVISI", "VERIFIED")

    val filteredFindings = findings.filter { finding ->
        if (selectedStatus == "SEMUA") true
        else finding.status.replace("_", " ").equals(selectedStatus, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthWarmNeutral)
    ) {
        // Map Header Bar
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
                            text = "PETA MONITORING TEMUAN",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Distribusi Geospasial GPS & Status Blok Sawit",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = { viewModel.refreshGpsLocation() }) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Refresh GPS",
                            tint = HarvestGoldLight
                        )
                    }
                }
            }
        }

        // Status Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(statusFilters) { st ->
                val isSelected = selectedStatus == st
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatus = st },
                    label = { Text(st, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PalmGreen20,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Interactive Plantation Map Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
        ) {
            PlantationMapCanvas(
                findings = filteredFindings,
                selectedFinding = selectedFindingOnMap,
                onFindingSelected = { selectedFindingOnMap = it },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Selected Finding Bottom Card Preview
        selectedFindingOnMap?.let { finding ->
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TEMUAN TERPILIH DI PETA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalmGreen20
                        )
                        IconButton(
                            onClick = { selectedFindingOnMap = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SlateGrayMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    FindingItemCard(
                        finding = finding,
                        onClick = { onSelectFinding(finding) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onSelectFinding(finding) },
                        colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Detail & Riwayat Lengkap", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
