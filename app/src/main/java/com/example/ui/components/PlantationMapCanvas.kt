package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FieldFinding
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.sqrt

@Composable
fun PlantationMapCanvas(
    findings: List<FieldFinding>,
    selectedFinding: FieldFinding?,
    onFindingSelected: (FieldFinding) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Map bounds in coordinates
    val minLat = 0.5330
    val maxLat = 0.5470
    val minLng = 101.4390
    val maxLng = 101.4610

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2F20))
            .border(1.dp, PalmGreen60.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(findings) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        // Check which finding marker was tapped
                        var closestFinding: FieldFinding? = null
                        var minDistance = 80f // tap threshold in px

                        findings.forEach { finding ->
                            val normX = ((finding.longitude - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.1f, 0.9f)
                            val normY = (1f - ((finding.latitude - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.1f, 0.9f)
                            val pinX = normX * w
                            val pinY = normY * h
                            val dist = sqrt((tapOffset.x - pinX) * (tapOffset.x - pinX) + (tapOffset.y - pinY) * (tapOffset.y - pinY))
                            if (dist < minDistance) {
                                minDistance = dist
                                closestFinding = finding
                            }
                        }

                        closestFinding?.let { onFindingSelected(it) }
                    }
                }
        ) {
            canvasSize = size
            val w = size.width
            val h = size.height

            // 1. Draw Estate Palm Grid Blocks (Blok A, B, C, D)
            val blockCols = 4
            val blockRows = 3
            val colW = w / blockCols
            val rowH = h / blockRows

            for (r in 0 until blockRows) {
                for (c in 0 until blockCols) {
                    val blockColor = if ((r + c) % 2 == 0) Color(0xFF19281A) else Color(0xFF1D2E1F)
                    drawRect(
                        color = blockColor,
                        topLeft = Offset(c * colW, r * rowH),
                        size = Size(colW, rowH)
                    )
                    // Block border (Roads / Rintis)
                    drawRect(
                        color = Color(0xFF2E4030),
                        topLeft = Offset(c * colW, r * rowH),
                        size = Size(colW, rowH),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // 2. Draw Main Road (MR) & Collection Road (CR)
            drawLine(
                color = Color(0xFFD7CCC8).copy(alpha = 0.6f),
                start = Offset(0f, h * 0.5f),
                end = Offset(w, h * 0.5f),
                strokeWidth = 6f
            )
            drawLine(
                color = Color(0xFFBCAAA4).copy(alpha = 0.5f),
                start = Offset(w * 0.5f, 0f),
                end = Offset(w * 0.5f, h),
                strokeWidth = 5f
            )

            // 3. Draw Drainage Canal / Parit Primer
            drawLine(
                color = Color(0xFF0288D1).copy(alpha = 0.6f),
                start = Offset(w * 0.15f, 0f),
                end = Offset(w * 0.85f, h),
                strokeWidth = 3f
            )

            // 4. Draw Findings Pin Markers
            findings.forEach { finding ->
                val normX = ((finding.longitude - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.1f, 0.9f)
                val normY = (1f - ((finding.latitude - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.1f, 0.9f)
                val pinX = normX * w
                val pinY = normY * h

                val markerColor = when (finding.status.uppercase()) {
                    "OPEN" -> StatusOpenRed
                    "ON_PROGRESS", "ON PROGRESS" -> StatusProgressYellow
                    "WAITING_VERIFICATION", "WAITING VERIFICATION" -> StatusWaitingBlue
                    "REVISI" -> StatusRevisionOrange
                    "VERIFIED", "CLOSED" -> StatusVerifiedGreen
                    else -> SlateGrayMedium
                }

                val isSelected = selectedFinding?.id == finding.id

                // Draw pulse ring if selected or critical
                if (isSelected || finding.priority == "KRITIS") {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.35f),
                        radius = if (isSelected) 26f else 20f,
                        center = Offset(pinX, pinY)
                    )
                }

                // Outer pin circle
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 14f else 10f,
                    center = Offset(pinX, pinY)
                )

                // Inner status pin
                drawCircle(
                    color = markerColor,
                    radius = if (isSelected) 11f else 7.5f,
                    center = Offset(pinX, pinY)
                )
            }
        }

        // Map Legend Overlay at Top Right
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(
                    text = "ESTATE RIAU PERDANA",
                    color = HarvestGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).background(StatusOpenRed, CircleShape))
                    Text(" Open", color = Color.White, fontSize = 8.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(7.dp).background(StatusProgressYellow, CircleShape))
                    Text(" Progress", color = Color.White, fontSize = 8.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(7.dp).background(StatusWaitingBlue, CircleShape))
                    Text(" Verif", color = Color.White, fontSize = 8.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(7.dp).background(StatusVerifiedGreen, CircleShape))
                    Text(" Selesai", color = Color.White, fontSize = 8.sp)
                }
            }
        }

        // Compass Rose / North Indicator at Top Left
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(30.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("N ▲", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Map Help text
        Text(
            text = "Ketuk pin untuk melihat detail temuan",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
    }
}
