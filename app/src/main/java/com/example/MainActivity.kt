package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppNotification
import com.example.model.FieldFinding
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: SawitViewModel = viewModel()
                SawitMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SawitMainApp(viewModel: SawitViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedFinding by viewModel.selectedFinding.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val gpsAccuracy by viewModel.currentGpsAccuracy.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showAddFindingDialog by remember { mutableStateOf(false) }
    var showAddWorkDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (selectedFinding == null) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Upper Row: Brand & User Avatar Profile Chip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val currentTitle = when (currentTab) {
                                    "DASHBOARD" -> "DASHBOARD UTAMA"
                                    "PEKERJAAN" -> "MONITORING PEKERJAAN"
                                    "TEMUAN" -> "MONITORING TEMUAN"
                                    "MAP" -> "PETA GEOSPASIAL"
                                    "VERIFIKASI" -> "DESK VERIFIKASI & TTD"
                                    "LAPORAN" -> "LAPORAN & AUDIT TRAIL"
                                    "PROFIL" -> "PROFIL PENGGUNA"
                                    else -> "SAWITX FIELD"
                                }
                                Text(
                                    text = currentTitle,
                                    color = PalmGreen40,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Sawit",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = PalmGreenDark
                                    )
                                    Text(
                                        text = "X",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = PalmGreen60
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = HarvestGoldContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "by MDABasuki",
                                            color = HarvestGoldDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // User Profile Info Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.setTab("PROFIL") }
                                    .padding(start = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = user?.fullName ?: "Petugas Lapangan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SlateGrayDark
                                    )
                                    Text(
                                        text = "${user?.jabatan ?: "Mandor"} • ${user?.divisi ?: "Divisi I"}",
                                        fontSize = 10.sp,
                                        color = SlateGrayMedium
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(PalmGreenContainer, CircleShape)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(PalmGreen40, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (user?.fullName?.take(2) ?: "PL").uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lower Quick Status Bar (GPS, Online/Offline, Notifications)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // GPS Indicator
                                Surface(
                                    color = if (gpsAccuracy <= 15.0) PalmGreenContainer else HarvestGoldContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable { viewModel.refreshGpsLocation() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.GpsFixed,
                                            contentDescription = null,
                                            tint = if (gpsAccuracy <= 15.0) PalmGreen40 else HarvestGoldDark,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "±${String.format(Locale.getDefault(), "%.1f", gpsAccuracy)}m",
                                            color = if (gpsAccuracy <= 15.0) PalmGreen40 else HarvestGoldDark,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Online / Offline Switch Pill
                                Surface(
                                    color = if (isOnline) PalmGreenContainer else StatusOpenRedContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable { viewModel.toggleOnlineOffline() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (isOnline) PalmGreen60 else StatusOpenRed, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            if (isOnline) "ONLINE" else "OFFLINE",
                                            color = if (isOnline) PalmGreen40 else StatusOpenRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Notification Bell
                            Surface(
                                color = if (unreadNotifsCount > 0) HarvestGoldContainer else SlateGraySoft,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { showNotificationsDialog = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadNotifsCount > 0) {
                                                Badge(containerColor = StatusOpenRed) {
                                                    Text("$unreadNotifsCount", color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Notifications,
                                            contentDescription = "Notifikasi",
                                            tint = if (unreadNotifsCount > 0) HarvestGoldDark else SlateGrayMedium,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (unreadNotifsCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "$unreadNotifsCount Baru",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HarvestGoldDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedFinding == null && currentTab != "PROFIL") {
                FloatingActionButton(
                    onClick = { showAddFindingDialog = true },
                    containerColor = PalmGreen40,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("fab_add_finding")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Temuan",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (selectedFinding == null) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        val navItems = listOf(
                            Triple("DASHBOARD", "Home", Icons.Default.Home),
                            Triple("PEKERJAAN", "Kerja", Icons.Default.Agriculture),
                            Triple("TEMUAN", "Temuan", Icons.Default.AddAPhoto),
                            Triple("MAP", "Peta", Icons.Default.Map),
                            Triple("VERIFIKASI", "Verif", Icons.Default.VerifiedUser),
                            Triple("LAPORAN", "Laporan", Icons.Default.Assessment),
                            Triple("PROFIL", "Profil", Icons.Default.AccountCircle)
                        )

                        navItems.forEach { (route, label, icon) ->
                            val isSelected = currentTab == route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setTab(route) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        label,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PalmGreen40,
                                    selectedTextColor = PalmGreen40,
                                    indicatorColor = PalmGreenLight,
                                    unselectedIconColor = SlateGrayMedium,
                                    unselectedTextColor = SlateGrayMedium
                                ),
                                modifier = Modifier.testTag("nav_item_${route.lowercase()}")
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedFinding != null) {
                TemuanDetailScreen(
                    finding = selectedFinding!!,
                    viewModel = viewModel,
                    onBack = { viewModel.setSelectedFinding(null) },
                    onNavigateToVerify = { finding ->
                        viewModel.setSelectedFinding(null)
                        viewModel.setTab("VERIFIKASI")
                    }
                )
            } else {
                when (currentTab) {
                    "DASHBOARD" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateTab = { tab -> viewModel.setTab(tab) },
                        onSelectFinding = { finding -> viewModel.setSelectedFinding(finding) },
                        onOpenAddFinding = { showAddFindingDialog = true },
                        onOpenAddWork = { showAddWorkDialog = true }
                    )
                    "PEKERJAAN" -> PekerjaanScreen(viewModel = viewModel)
                    "TEMUAN" -> TemuanScreen(
                        viewModel = viewModel,
                        onSelectFinding = { finding -> viewModel.setSelectedFinding(finding) }
                    )
                    "MAP" -> MapScreen(
                        viewModel = viewModel,
                        onSelectFinding = { finding -> viewModel.setSelectedFinding(finding) }
                    )
                    "VERIFIKASI" -> VerifikasiScreen(
                        viewModel = viewModel,
                        onSelectFindingDetail = { finding -> viewModel.setSelectedFinding(finding) }
                    )
                    "LAPORAN" -> LaporanScreen(viewModel = viewModel)
                    "PROFIL" -> ProfilScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Add Finding Dialog
    if (showAddFindingDialog) {
        CreateFindingDialog(
            viewModel = viewModel,
            onDismiss = { showAddFindingDialog = false }
        )
    }

    // Add Work Dialog
    if (showAddWorkDialog) {
        AddOperationWorkDialog(
            viewModel = viewModel,
            onDismiss = { showAddWorkDialog = false }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsModalDialog(
            notifications = notifications,
            onMarkAllRead = { viewModel.markNotificationsRead() },
            onDismiss = { showNotificationsDialog = false }
        )
    }
}

@Composable
fun NotificationsModalDialog(
    notifications: List<AppNotification>,
    onMarkAllRead: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onMarkAllRead()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20)
            ) {
                Text("Tandai Semua Dibaca", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pemberitahuan Sistem", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PalmGreen20)
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = HarvestGold)
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada pemberitahuan baru.", color = SlateGrayMedium, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notifications) { notif ->
                        val (icon, tint) = when (notif.category) {
                            "FINDING_NEW" -> Pair(Icons.Default.AddAlert, StatusOpenRed)
                            "WAITING_VERIFICATION" -> Pair(Icons.Default.HourglassTop, StatusWaitingBlue)
                            "VERIFIED" -> Pair(Icons.Default.CheckCircle, StatusVerifiedGreen)
                            "REVISION" -> Pair(Icons.Default.Replay, StatusRevisionOrange)
                            else -> Pair(Icons.Default.Info, SlateGrayMedium)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) PalmGreen20.copy(alpha = 0.08f) else EarthCardLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateGrayDark)
                                    Text(text = notif.message, fontSize = 11.sp, color = SlateGrayMedium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = notif.formattedTime, fontSize = 9.sp, color = SlateGrayLight)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
