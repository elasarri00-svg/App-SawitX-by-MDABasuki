package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun BeforeAfterCompareView(
    photoBeforeWatermark: String,
    photoAfterWatermark: String,
    hasAfterPhoto: Boolean,
    modifier: Modifier = Modifier
) {
    var isSplitView by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    Card(
        colors = CardDefaults.cardColors(containerColor = EarthCardLight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = null,
                        tint = PalmGreen20,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PERBANDINGAN: SEBELUM | SESUDAH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PalmGreen20
                    )
                }

                // Switch button between Side-by-Side and Interactive Split Slider
                if (hasAfterPhoto) {
                    FilledTonalButton(
                        onClick = { isSplitView = !isSplitView },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (isSplitView) "Mode Berdampingan" else "Mode Geser Interaktif",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!hasAfterPhoto) {
                // Only Before Photo available yet
                WatermarkedPhotoViewer(
                    title = "Kondisi Temuan Awal",
                    watermarkText = photoBeforeWatermark,
                    photoType = "SEBELUM"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = HarvestGoldContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Foto sesudah perbaikan belum diunggah. PIC wajib mengunggah foto saat progres 100%.",
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            } else if (!isSplitView) {
                // Side by Side View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        WatermarkedPhotoViewer(
                            title = "Sebelum",
                            watermarkText = photoBeforeWatermark,
                            photoType = "SEBELUM"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        WatermarkedPhotoViewer(
                            title = "Sesudah",
                            watermarkText = photoAfterWatermark,
                            photoType = "SESUDAH"
                        )
                    }
                }
            } else {
                // Interactive Split Slider View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val newX = change.position.x / size.width
                                sliderPosition = newX.coerceIn(0.05f, 0.95f)
                            }
                        }
                ) {
                    // Left: Before Canvas Visual
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val splitX = w * sliderPosition

                        // Draw Before side (Left)
                        drawRect(
                            color = Color(0xFF3E2723),
                            size = androidx.compose.ui.geometry.Size(splitX, h)
                        )
                        // Draw After side (Right)
                        drawRect(
                            color = Color(0xFF1B5E20),
                            topLeft = Offset(splitX, 0f),
                            size = androidx.compose.ui.geometry.Size(w - splitX, h)
                        )

                        // Divider line
                        drawLine(
                            color = Color.White,
                            start = Offset(splitX, 0f),
                            end = Offset(splitX, h),
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    // Labels
                    Text(
                        text = "◀ SEBELUM PERBAIKAN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(StatusOpenRed.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Text(
                        text = "SESUDAH SELESAI ▶",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(StatusVerifiedGreen.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    // Drag Indicator Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (sliderPosition - 0.5f).dp * 200) // visual feedback
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, PalmGreen20, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Geser",
                            tint = PalmGreen20,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Watermark overlay at bottom
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = "Geser titik tengah ke kiri/kanan untuk inspeksi perubahan fisik secara detail",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
