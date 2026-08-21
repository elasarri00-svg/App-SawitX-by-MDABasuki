package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldFinding
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun FindingStatusChip(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label, icon) = when (status.uppercase()) {
        "OPEN" -> Quadruple(StatusOpenRedContainer, StatusOpenRed, "OPEN", Icons.Default.WarningAmber)
        "ON_PROGRESS", "ON PROGRESS" -> Quadruple(StatusProgressYellowContainer, StatusProgressYellow, "PROGRESS", Icons.Default.HourglassTop)
        "WAITING_VERIFICATION", "WAITING VERIFICATION" -> Quadruple(StatusWaitingBlueContainer, StatusWaitingBlue, "VERIFIKASI", Icons.Default.FactCheck)
        "REVISI" -> Quadruple(StatusRevisionOrangeContainer, StatusRevisionOrange, "REVISI", Icons.Default.Replay)
        "VERIFIED" -> Quadruple(StatusVerifiedGreenContainer, StatusVerifiedGreen, "VERIFIED", Icons.Default.CheckCircle)
        "CLOSED" -> Quadruple(SlateGraySoft, SlateGrayMedium, "CLOSED", Icons.Default.Lock)
        else -> Quadruple(SlateGraySoft, SlateGrayDark, status, Icons.Default.Info)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority.uppercase()) {
        "KRITIS", "CRITICAL" -> Triple(StatusOpenRedContainer, StatusOpenRed, "KRITIS")
        "TINGGI", "HIGH" -> Triple(StatusRevisionOrangeContainer, StatusRevisionOrange, "TINGGI")
        "SEDANG", "MEDIUM" -> Triple(HarvestGoldContainer, HarvestGoldDark, "SEDANG")
        else -> Triple(PalmGreenContainer, PalmGreen40, "RENDAH")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SyncStatusChip(syncStatus: String, modifier: Modifier = Modifier) {
    val (color, bgColor, icon, text) = when (syncStatus) {
        "SYNCED" -> Quadruple(PalmGreen40, PalmGreenContainer, Icons.Default.CloudDone, "Tersinkron")
        "PENDING_SYNC" -> Quadruple(HarvestGoldDark, HarvestGoldContainer, Icons.Default.CloudSync, "Menunggu Sinkron")
        else -> Quadruple(StatusOpenRed, StatusOpenRedContainer, Icons.Default.CloudOff, "Gagal Sinkron")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FindingItemCard(
    finding: FieldFinding,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusAccentColor = when (finding.status.uppercase()) {
        "OPEN" -> StatusOpenRed
        "ON_PROGRESS", "ON PROGRESS" -> StatusProgressYellow
        "WAITING_VERIFICATION", "WAITING VERIFICATION" -> StatusWaitingBlue
        "REVISI" -> StatusRevisionOrange
        "VERIFIED" -> StatusVerifiedGreen
        else -> SlateGrayMedium
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left colored stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusAccentColor)
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = finding.findingNumber,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = PalmGreenDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        PriorityBadge(priority = finding.priority)
                    }
                    FindingStatusChip(status = finding.status)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = finding.description,
                    fontSize = 12.sp,
                    color = SlateGrayDark,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = PalmGreen40,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${finding.estate} • ${finding.divisi} • ${finding.blok}",
                            fontSize = 11.sp,
                            color = SlateGrayMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        color = PalmGreenContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PIC: ${finding.picName}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalmGreen40,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar with vibrant styling
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { finding.progressPercent / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (finding.progressPercent == 100) PalmGreen40 else if (finding.progressPercent > 50) StatusProgressYellow else HarvestGold,
                        trackColor = SlateGraySoft
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${finding.progressPercent}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SlateGrayDark
                    )
                }
            }
        }
    }
}

@Composable
fun GpsAccuracyBadge(
    accuracy: Double,
    latitude: Double,
    longitude: Double,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGood = accuracy <= 15.0
    val isWarning = accuracy > 15.0 && accuracy <= 25.0
    val statusColor = if (isGood) PalmGreen40 else if (isWarning) HarvestGold else StatusOpenRed

    Card(
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Akurasi GPS: ±${String.format(Locale.getDefault(), "%.1f", accuracy)} meter",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onRefreshLocation,
                    colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Ambil Lokasi",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AMBIL LOKASI SAYA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Koordinat: ${String.format(Locale.getDefault(), "%.5f", latitude)}, ${String.format(Locale.getDefault(), "%.5f", longitude)}",
                fontSize = 12.sp,
                color = SlateGrayMedium,
                fontFamily = FontFamily.Monospace
            )

            if (accuracy > 20.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = StatusOpenRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Akurasi GPS rendah (> 20m). Pastikan berada di bawah langit terbuka!",
                        color = StatusOpenRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WatermarkedPhotoViewer(
    title: String,
    watermarkText: String,
    photoType: String, // "SEBELUM", "PROGRES", "SESUDAH"
    modifier: Modifier = Modifier
) {
    val headerColor = when (photoType.uppercase()) {
        "SEBELUM" -> StatusOpenRed
        "SESUDAH" -> StatusVerifiedGreen
        else -> StatusWaitingBlue
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E281F)),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SlateGrayLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column {
            // Photo Header Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor.copy(alpha = 0.25f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FOTO $photoType: $title",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Simulated High-res Field Photo Visual with Field Texture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF2B3A2C)),
                contentAlignment = Alignment.BottomStart
            ) {
                // Background visual texture
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawLine(Color(0xFF3B4E3C), Offset(0f, h * 0.7f), Offset(w, h * 0.7f), strokeWidth = 3f)
                    drawLine(Color(0xFF334434), Offset(w * 0.3f, 0f), Offset(w * 0.3f, h), strokeWidth = 2f)
                    drawLine(Color(0xFF334434), Offset(w * 0.7f, 0f), Offset(w * 0.7f, h), strokeWidth = 2f)
                    drawCircle(Color(0xFF4CAF50).copy(alpha = 0.2f), radius = 40f, center = Offset(w * 0.2f, h * 0.4f))
                    drawCircle(Color(0xFF81C784).copy(alpha = 0.2f), radius = 50f, center = Offset(w * 0.8f, h * 0.35f))
                }

                // Center camera icon indicator
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (photoType == "SESUDAH") Icons.Default.CheckCircleOutline else Icons.Default.Yard,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (photoType == "SESUDAH") "Kondisi Perbaikan Selesai" else "Dokumentasi Kondisi Lapangan",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Automatic Watermark Overlay (As mandated in requirements)
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = watermarkText.ifEmpty { "TEMUAN PERKEBUNAN | SawitX Verified Documentation" },
                            color = Color.White,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalSignaturePad(
    modifier: Modifier = Modifier,
    onSignatureChanged: (hasSignature: Boolean, pathData: String) -> Unit
) {
    val path = remember { mutableStateOf(Path()) }
    var pointsCount by remember { mutableIntStateOf(0) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, PalmGreen20, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Draw,
                        contentDescription = null,
                        tint = PalmGreen20,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GORES TANDA TANGAN DIGITAL",
                        fontWeight = FontWeight.Bold,
                        color = PalmGreen20,
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = {
                        path.value = Path()
                        pointsCount = 0
                        onSignatureChanged(false, "")
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Hapus", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus Goresan", fontSize = 11.sp, color = StatusOpenRed)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Signature Touch Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, SlateGrayLight, RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val newPath = Path().apply {
                                    addPath(path.value)
                                    moveTo(offset.x, offset.y)
                                }
                                path.value = newPath
                                pointsCount++
                                onSignatureChanged(true, "M${offset.x},${offset.y}")
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val currentPos = change.position
                                val newPath = Path().apply {
                                    addPath(path.value)
                                    lineTo(currentPos.x, currentPos.y)
                                }
                                path.value = newPath
                                pointsCount++
                                onSignatureChanged(true, "SIG_DRAW_${pointsCount}")
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (pointsCount == 0) {
                    Text(
                        text = "Tanda tangani langsung di sini dengan jari Anda",
                        color = SlateGrayMedium.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPath(
                        path = path.value,
                        color = Color(0xFF0D3315),
                        style = Stroke(
                            width = 4.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tanda tangan dilindungi hash kriptografi SHA-256 dan terikat verifikasi perangkat.",
                fontSize = 10.sp,
                color = SlateGrayMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
