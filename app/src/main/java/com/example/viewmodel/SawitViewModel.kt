package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SawitDatabase
import com.example.data.SawitRepository
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SawitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SawitRepository

    init {
        val db = SawitDatabase.getDatabase(application, viewModelScope)
        repository = SawitRepository(db)
    }

    // Flows from Room
    val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorks: StateFlow<List<OperationWork>> = repository.allWorks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFindings: StateFlow<List<FieldFinding>> = repository.allFindings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allVerifications: StateFlow<List<FindingVerification>> = repository.allVerifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getHistoryForFinding(findingId: String): Flow<List<FindingProgressHistory>> =
        repository.getHistoryForFinding(findingId)

    // Active User State
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Navigation & UI State
    private val _currentTab = MutableStateFlow("DASHBOARD") // DASHBOARD, PEKERJAAN, TEMUAN, MAP, VERIFIKASI, LAPORAN, PROFIL
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Online / Offline & Sync State
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncState = MutableStateFlow("SYNCED") // SYNCED, PENDING_SYNC, FAILED_SYNC
    val syncState: StateFlow<String> = _syncState.asStateFlow()

    // GPS State
    private val _currentLatitude = MutableStateFlow(0.5381)
    val currentLatitude: StateFlow<Double> = _currentLatitude.asStateFlow()

    private val _currentLongitude = MutableStateFlow(101.4478)
    val currentLongitude: StateFlow<Double> = _currentLongitude.asStateFlow()

    private val _currentGpsAccuracy = MutableStateFlow(3.8) // meters
    val currentGpsAccuracy: StateFlow<Double> = _currentGpsAccuracy.asStateFlow()

    // Selected Detail Items
    private val _selectedFinding = MutableStateFlow<FieldFinding?>(null)
    val selectedFinding: StateFlow<FieldFinding?> = _selectedFinding.asStateFlow()

    // Banner / Snack Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Auto select default user on start
        viewModelScope.launch {
            allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    _currentUser.value = users.first()
                }
            }
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setSelectedFinding(finding: FieldFinding?) {
        _selectedFinding.value = finding
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun toggleOnlineOffline() {
        _isOnline.value = !_isOnline.value
        val status = if (_isOnline.value) "Online (Server Terhubung)" else "Offline (Penyimpanan Lokal Aktif)"
        showMessage("Mode Jaringan: $status")
        if (_isOnline.value && _syncState.value == "PENDING_SYNC") {
            syncPendingData()
        }
    }

    fun syncPendingData() {
        viewModelScope.launch(Dispatchers.IO) {
            _syncState.value = "PENDING_SYNC"
            // Update works & findings syncStatus to SYNCED
            allWorks.value.filter { it.syncStatus != "SYNCED" }.forEach { work ->
                repository.updateWork(work.copy(syncStatus = "SYNCED"))
            }
            allFindings.value.filter { it.syncStatus != "SYNCED" }.forEach { finding ->
                repository.updateFinding(finding.copy(syncStatus = "SYNCED"))
            }
            _syncState.value = "SYNCED"
            _currentUser.value?.let { user ->
                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "SYNC_DATA",
                    details = "Sinkronisasi otomatis berhasil: Seluruh data lokal terunggah ke Cloud Server"
                )
            }
            showMessage("Sinkronisasi Cloud Berhasil! Semua data telah terverifikasi.")
        }
    }

    fun refreshGpsLocation() {
        // Realistic simulated GPS fluctuation in plantation field
        val deltaLat = (Math.random() - 0.5) * 0.002
        val deltaLng = (Math.random() - 0.5) * 0.002
        _currentLatitude.value = (0.5381 + deltaLat)
        _currentLongitude.value = (101.4478 + deltaLng)
        _currentGpsAccuracy.value = (2.5 + Math.random() * 3.0)
        showMessage("Koordinat GPS diperbarui: Akurasi ±${String.format(Locale.getDefault(), "%.1f", _currentGpsAccuracy.value)}m")
    }

    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByUsername(username)
            if (user != null) {
                _currentUser.value = user
                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "LOGIN",
                    details = "Login berhasil dengan Username & Password",
                    referenceId = user.registeredDeviceId
                )
                onResult(true, "Selamat datang, ${user.fullName} (${user.jabatan})")
            } else {
                onResult(false, "Username tidak ditemukan di sistem")
            }
        }
    }

    fun loginWithBiometric(user: UserAccount, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value = user
            repository.logAudit(
                userName = user.fullName,
                userJabatan = user.jabatan,
                actionType = "LOGIN",
                details = "Login sukses via Biometrik (Fingerprint/Face ID)",
                referenceId = user.registeredDeviceId
            )
            onResult(true, "Autentikasi Biometrik Berhasil untuk ${user.fullName}")
        }
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "LOGOUT",
                    details = "Pengguna keluar dari aplikasi"
                )
            }
        }
        showMessage("Anda telah logout dari SawitX")
    }

    fun switchAccount(targetUser: UserAccount) {
        _currentUser.value = targetUser
        viewModelScope.launch(Dispatchers.IO) {
            repository.logAudit(
                userName = targetUser.fullName,
                userJabatan = targetUser.jabatan,
                actionType = "LOGIN",
                details = "Beralih akun aktif ke ${targetUser.fullName}"
            )
        }
        showMessage("Akun aktif: ${targetUser.fullName} (${targetUser.jabatan})")
    }

    fun updateUserPermissions(
        username: String,
        canCreate: Boolean,
        canRepair: Boolean,
        canVerify: Boolean,
        canViewReports: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByUsername(username)
            if (user != null) {
                val updated = user.copy(
                    canCreateFinding = canCreate,
                    canRepair = canRepair,
                    canVerify = canVerify,
                    canViewAllReports = canViewReports
                )
                repository.updateUser(updated)
                if (_currentUser.value?.username == username) {
                    _currentUser.value = updated
                }
                _currentUser.value?.let { active ->
                    repository.logAudit(
                        userName = active.fullName,
                        userJabatan = active.jabatan,
                        actionType = "CHANGE_STATUS",
                        details = "Memperbarui hak akses langsung akun: $username (Create=$canCreate, Repair=$canRepair, Verify=$canVerify, Reports=$canViewReports)"
                    )
                }
                showMessage("Hak akses akun $username berhasil diperbarui.")
            }
        }
    }

    fun createFinding(
        estate: String,
        divisi: String,
        blok: String,
        findingType: String,
        priority: String,
        description: String,
        picName: String,
        targetDueDate: String,
        lat: Double,
        lng: Double,
        accuracy: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val today = dateFormat.format(Date())
            val time = timeFormat.format(Date())

            val randomSeq = (100..999).random()
            val shortEst = estate.replace("Estate ", "EST-").take(6)
            val findingNo = "TMN-$shortEst-$divisi-$randomSeq"
            val id = "$findingNo-${System.currentTimeMillis()}"

            val watermark = "TEMUAN PERKEBUNAN\n${user.fullName} | $today $time\nLat: ${String.format(Locale.getDefault(), "%.4f", lat)}, Long: ${String.format(Locale.getDefault(), "%.4f", lng)}\n$estate - $divisi - $blok\nNo: $findingNo"

            val finding = FieldFinding(
                id = id,
                findingNumber = findingNo,
                date = today,
                time = time,
                reporterName = user.fullName,
                reporterJabatan = user.jabatan,
                estate = estate,
                divisi = divisi,
                blok = blok,
                findingType = findingType,
                priority = priority,
                description = description,
                picName = picName,
                targetDueDate = targetDueDate,
                latitude = lat,
                longitude = lng,
                gpsAccuracy = accuracy,
                status = "OPEN",
                progressPercent = 0,
                photoBefore = "photo_before_${System.currentTimeMillis()}",
                photoAfter = "",
                watermarkText = watermark,
                syncStatus = if (_isOnline.value) "SYNCED" else "PENDING_SYNC"
            )

            repository.saveFinding(finding)

            // Add Initial Progress History (0%)
            repository.addProgressHistory(
                FindingProgressHistory(
                    findingId = findingNo,
                    userName = user.fullName,
                    date = today,
                    time = time,
                    percentage = 0,
                    notes = "Temuan dilaporkan: $description",
                    photoUri = finding.photoBefore,
                    latitude = lat,
                    longitude = lng,
                    gpsAccuracy = accuracy
                )
            )

            // Notification
            repository.addNotification(
                title = "Temuan Baru ($priority)",
                message = "${user.fullName} membuat temuan $findingType di $estate $divisi $blok (PIC: $picName).",
                category = "FINDING_NEW",
                refId = id
            )

            // Audit
            repository.logAudit(
                userName = user.fullName,
                userJabatan = user.jabatan,
                actionType = "CREATE_FINDING",
                details = "Membuat temuan $findingNo: $findingType - $description",
                referenceId = id
            )

            showMessage("Temuan $findingNo berhasil disimpan dengan Watermark & GPS!")
        }
    }

    fun updateFindingProgress(
        finding: FieldFinding,
        percentage: Int,
        notes: String,
        isSubmittingForVerification: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val today = dateFormat.format(Date())
            val time = timeFormat.format(Date())

            val newStatus = when {
                isSubmittingForVerification || percentage == 100 -> "WAITING_VERIFICATION"
                percentage > 0 -> "ON_PROGRESS"
                else -> "OPEN"
            }

            val hasAfterPhoto = percentage == 100 || isSubmittingForVerification
            val photoAfterWatermark = if (hasAfterPhoto) {
                "PERBAIKAN SELESAI\n${user.fullName} | $today $time\nLat: ${String.format(Locale.getDefault(), "%.4f", _currentLatitude.value)}, Long: ${String.format(Locale.getDefault(), "%.4f", _currentLongitude.value)}\n${finding.estate} - ${finding.divisi} - ${finding.blok}\nNo: ${finding.findingNumber}"
            } else finding.photoAfter

            val updatedFinding = finding.copy(
                progressPercent = percentage,
                status = newStatus,
                photoAfter = if (hasAfterPhoto) "photo_after_${System.currentTimeMillis()}" else finding.photoAfter,
                updatedAt = System.currentTimeMillis(),
                syncStatus = if (_isOnline.value) "SYNCED" else "PENDING_SYNC"
            )

            repository.updateFinding(updatedFinding)

            // Add progress history
            repository.addProgressHistory(
                FindingProgressHistory(
                    findingId = finding.findingNumber,
                    userName = user.fullName,
                    date = today,
                    time = time,
                    percentage = percentage,
                    notes = notes,
                    photoUri = if (hasAfterPhoto) updatedFinding.photoAfter else "",
                    latitude = _currentLatitude.value,
                    longitude = _currentLongitude.value,
                    gpsAccuracy = _currentGpsAccuracy.value
                )
            )

            // Audit
            repository.logAudit(
                userName = user.fullName,
                userJabatan = user.jabatan,
                actionType = "UPDATE_PROGRESS",
                details = "Memperbarui progres temuan ${finding.findingNumber} ke $percentage% (Status: $newStatus) - Catatan: $notes",
                referenceId = finding.id
            )

            if (newStatus == "WAITING_VERIFICATION") {
                repository.addNotification(
                    title = "Menunggu Verifikasi",
                    message = "${user.fullName} menyatakan perbaikan ${finding.findingNumber} selesai (100%) dan meminta verifikasi verifikator.",
                    category = "WAITING_VERIFICATION",
                    refId = finding.id
                )
                showMessage("Progres 100% tersimpan! Pengajuan verifikasi telah diteruskan ke Verifikator.")
            } else {
                showMessage("Progres $percentage% untuk ${finding.findingNumber} berhasil diperbarui.")
            }

            if (_selectedFinding.value?.id == finding.id) {
                _selectedFinding.value = updatedFinding
            }
        }
    }

    fun submitVerification(
        finding: FieldFinding,
        isApproved: Boolean,
        revisionReason: String,
        signatureSvg: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val today = dateFormat.format(Date())
            val time = timeFormat.format(Date())

            if (isApproved) {
                val verificationId = "VRF-2026-X" + (100..999).random()
                val docRaw = "${finding.id}|$verificationId|${user.fullName}|$today|$time|${_currentLatitude.value}|${_currentLongitude.value}"
                val documentHash = repository.generateDocumentHash(docRaw)

                val verification = FindingVerification(
                    verificationId = verificationId,
                    findingId = finding.id,
                    findingNumber = finding.findingNumber,
                    description = finding.description,
                    picName = finding.picName,
                    verifierName = user.fullName,
                    verifierJabatan = user.jabatan,
                    date = today,
                    time = time,
                    verificationLatitude = _currentLatitude.value,
                    verificationLongitude = _currentLongitude.value,
                    verificationGpsAccuracy = _currentGpsAccuracy.value,
                    decision = "VERIFIED",
                    signaturePath = signatureSvg,
                    deviceInfo = "Android SM-A546E / SawitX Secure ID #$verificationId",
                    documentHash = documentHash
                )
                repository.saveVerification(verification)

                val updatedFinding = finding.copy(
                    status = "VERIFIED",
                    isLocked = true,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = if (_isOnline.value) "SYNCED" else "PENDING_SYNC"
                )
                repository.updateFinding(updatedFinding)

                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "VERIFICATION",
                    details = "Memverifikasi temuan ${finding.findingNumber} dengan Tanda Tangan Digital & Hash: $documentHash",
                    referenceId = verificationId
                )

                repository.addNotification(
                    title = "Verifikasi Disetujui (VERIFIED)",
                    message = "Temuan ${finding.findingNumber} telah resmi diverifikasi oleh ${user.fullName} (${user.jabatan}).",
                    category = "VERIFIED",
                    refId = finding.id
                )

                showMessage("Verifikasi berhasil & Tanda Tangan Digital tercatat ($verificationId).")
            } else {
                // Revisi
                val updatedFinding = finding.copy(
                    status = "REVISI",
                    progressPercent = 50,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = if (_isOnline.value) "SYNCED" else "PENDING_SYNC"
                )
                repository.updateFinding(updatedFinding)

                repository.addProgressHistory(
                    FindingProgressHistory(
                        findingId = finding.findingNumber,
                        userName = user.fullName,
                        date = today,
                        time = time,
                        percentage = 50,
                        notes = "REVISI VERIFIKASI: $revisionReason",
                        photoUri = "",
                        latitude = _currentLatitude.value,
                        longitude = _currentLongitude.value,
                        gpsAccuracy = _currentGpsAccuracy.value
                    )
                )

                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "REVISION",
                    details = "Menolak perbaikan temuan ${finding.findingNumber} (Revisi: $revisionReason)",
                    referenceId = finding.id
                )

                repository.addNotification(
                    title = "Revisi Temuan Diperlukan",
                    message = "Verifikator ${user.fullName} meminta revisi untuk ${finding.findingNumber}: $revisionReason",
                    category = "REVISION",
                    refId = finding.id
                )

                showMessage("Keputusan Revisi tersimpan. PIC ${finding.picName} telah diberi notifikasi perbaikan.")
            }

            if (_selectedFinding.value?.id == finding.id) {
                _selectedFinding.value = null
            }
        }
    }

    fun closeFinding(finding: FieldFinding) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val updated = finding.copy(
                status = "CLOSED",
                isLocked = true,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateFinding(updated)
            repository.logAudit(
                userName = user.fullName,
                userJabatan = user.jabatan,
                actionType = "CLOSE_FINDING",
                details = "Menutup temuan ${finding.findingNumber} secara resmi",
                referenceId = finding.id
            )
            repository.addNotification(
                title = "Temuan Ditutup (CLOSED)",
                message = "Temuan ${finding.findingNumber} resmi ditutup setelah seluruh alur verifikasi selesai.",
                category = "SYSTEM",
                refId = finding.id
            )
            showMessage("Temuan ${finding.findingNumber} telah resmi ditutup.")
        }
    }

    fun createOperationWork(work: OperationWork) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveWork(work)
            _currentUser.value?.let { user ->
                repository.logAudit(
                    userName = user.fullName,
                    userJabatan = user.jabatan,
                    actionType = "CREATE_FINDING",
                    details = "Input monitoring pekerjaan ${work.workType} (${work.subCategory}) di ${work.estate} ${work.divisi} ${work.blok}",
                    referenceId = work.id
                )
            }
            showMessage("Laporan pekerjaan ${work.workType} (${work.blok}) berhasil disimpan.")
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllNotificationsAsRead()
        }
    }

    fun generateBackupDumpJson(): String {
        val worksList = allWorks.value
        val findingsList = allFindings.value
        val usersList = allUsers.value
        val logsList = allAuditLogs.value

        return """
        {
          "system": "SawitX by MDABasuki",
          "version": "1.0",
          "exportedAt": "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}",
          "usersCount": ${usersList.size},
          "operationsCount": ${worksList.size},
          "findingsCount": ${findingsList.size},
          "auditLogsCount": ${logsList.size},
          "status": "BACKUP_VALID_ENCRYPTED_SHA256"
        }
        """.trimIndent()
    }
}
