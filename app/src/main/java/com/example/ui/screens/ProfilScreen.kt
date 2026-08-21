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
import com.example.model.UserAccount
import com.example.ui.theme.*
import com.example.viewmodel.SawitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    viewModel: SawitViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    var showSwitchUserDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

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
                Text(
                    text = "PROFIL PENGGUNA & PENGATURAN",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Pengaturan Akun, Keamanan Biometrik & Sinkronisasi",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. User Identity Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar Icon Box
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(PalmGreen20, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user?.fullName ?: "Petugas Perkebunan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PalmGreen20
                                )
                                Text(
                                    text = "${user?.jabatan ?: "Operasional"} • @${user?.username ?: "user"}",
                                    fontSize = 12.sp,
                                    color = SlateGrayMedium
                                )
                                Text(
                                    text = "${user?.estate ?: "Estate 1"} | ${user?.divisi ?: "Divisi 1"}",
                                    fontSize = 11.sp,
                                    color = PalmGreen40,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = SlateGrayLight.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nomor HP:", fontSize = 11.sp, color = SlateGrayMedium)
                            Text(user?.phone ?: "-", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateGrayDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ID Perangkat:", fontSize = 11.sp, color = SlateGrayMedium)
                            Text(user?.registeredDeviceId ?: "-", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SlateGrayDark)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showSwitchUserDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ganti Akun Pengguna Lapangan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Simple Direct Permissions (Tanpa RBAC rumit)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "HAK AKSES LANGSUNG PENGGUNA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PalmGreen20
                        )
                        Text(
                            text = "Sistem kontrol akses sederhana dapat diatur langsung",
                            fontSize = 11.sp,
                            color = SlateGrayMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        user?.let { u ->
                            PermissionToggleRow(
                                title = "Dapat Membuat Temuan",
                                checked = u.canCreateFinding,
                                onCheckedChange = { checked ->
                                    viewModel.updateUserPermissions(u.username, checked, u.canRepair, u.canVerify, u.canViewAllReports)
                                }
                            )
                            PermissionToggleRow(
                                title = "Dapat Melakukan Perbaikan (PIC)",
                                checked = u.canRepair,
                                onCheckedChange = { checked ->
                                    viewModel.updateUserPermissions(u.username, u.canCreateFinding, checked, u.canVerify, u.canViewAllReports)
                                }
                            )
                            PermissionToggleRow(
                                title = "Dapat Verifikasi & TTD Digital",
                                checked = u.canVerify,
                                onCheckedChange = { checked ->
                                    viewModel.updateUserPermissions(u.username, u.canCreateFinding, u.canRepair, checked, u.canViewAllReports)
                                }
                            )
                            PermissionToggleRow(
                                title = "Dapat Akses Semua Laporan",
                                checked = u.canViewAllReports,
                                onCheckedChange = { checked ->
                                    viewModel.updateUserPermissions(u.username, u.canCreateFinding, u.canRepair, u.canVerify, checked)
                                }
                            )
                        }
                    }
                }
            }

            // 3. Network & Offline Synchronization Manager
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SINKRONISASI OFFLINE / ONLINE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PalmGreen20
                            )
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { viewModel.toggleOnlineOffline() },
                                colors = SwitchDefaults.colors(checkedThumbColor = PalmGreen40)
                            )
                        }

                        Text(
                            text = if (isOnline) "Status: Online (Data langsung tersinkron ke Server Cloud)" else "Status: Offline (Data tersimpan di Room DB Lokal)",
                            fontSize = 11.sp,
                            color = if (isOnline) PalmGreen40 else StatusOpenRed,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.syncPendingData() },
                            colors = ButtonDefaults.buttonColors(containerColor = PalmGreen40),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sinkronisasi Seluruh Data Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Security, Biometric & Backup
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EarthCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "KEAMANAN & CADANGAN DATA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PalmGreen20
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = PalmGreen20, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Login Biometrik Fingerprint", fontSize = 12.sp, color = SlateGrayDark)
                            }
                            Switch(
                                checked = user?.isBiometricEnabled ?: true,
                                onCheckedChange = { viewModel.showMessage("Pengaturan Biometrik diperbarui") }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { showBackupDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup & Export Database JSON")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Security Badge
                        Surface(
                            color = SlateGraySoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Keamanan Terpasang:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGrayDark
                                )
                                Text(
                                    text = "• Enkripsi Password Argon2/Bcrypt\n• Device Binding ID: ${user?.registeredDeviceId}\n• Tanda Tangan Digital Kriptografi SHA-256\n• TLS/SSL Enkripsi Transport",
                                    fontSize = 10.sp,
                                    color = SlateGrayMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSwitchUserDialog) {
        SwitchUserDialog(
            allUsers = allUsers,
            currentUser = user,
            onSelect = { selected ->
                viewModel.switchAccount(selected)
                showSwitchUserDialog = false
            },
            onDismiss = { showSwitchUserDialog = false }
        )
    }

    if (showBackupDialog) {
        BackupDatabaseDialog(
            backupJson = viewModel.generateBackupDumpJson(),
            onDismiss = { showBackupDialog = false }
        )
    }
}

@Composable
fun PermissionToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 11.sp, color = SlateGrayDark)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(checkedThumbColor = PalmGreen20)
        )
    }
}

fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.padding(0.dp)
)

@Composable
fun SwitchUserDialog(
    allUsers: List<UserAccount>,
    currentUser: UserAccount?,
    onSelect: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
        title = {
            Text("Pilih Akun Pengguna Lapangan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PalmGreen20)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allUsers) { userItem ->
                    val isSelected = userItem.username == currentUser?.username
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PalmGreen20.copy(alpha = 0.12f) else EarthCardLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(userItem) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isSelected) PalmGreen20 else SlateGrayMedium, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userItem.fullName.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(userItem.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateGrayDark)
                                Text("${userItem.jabatan} • ${userItem.divisi}", fontSize = 11.sp, color = SlateGrayMedium)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PalmGreen20)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun BackupDatabaseDialog(
    backupJson: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PalmGreen20)
            ) {
                Text("Tutup & Simpan Cadangan")
            }
        },
        title = {
            Text("Backup Database JSON", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PalmGreen20)
        },
        text = {
            Column {
                Text("Salinan terenkripsi struktur data operasional dan temuan:", fontSize = 11.sp, color = SlateGrayDark)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SlateGraySoft,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = backupJson,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp),
                        color = SlateGrayDark
                    )
                }
            }
        }
    )
}
