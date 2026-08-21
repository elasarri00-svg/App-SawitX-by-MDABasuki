package com.example.data

import com.example.model.AppNotification
import com.example.model.AuditLog
import com.example.model.FieldFinding
import com.example.model.FindingProgressHistory
import com.example.model.FindingVerification
import com.example.model.OperationWork
import com.example.model.UserAccount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InitialDataSeeder {

    suspend fun seedDatabase(database: SawitDatabase) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val today = dateFormat.format(Date())
        val nowTime = timeFormat.format(Date())

        // 1. Preload Users
        val defaultUsers = listOf(
            UserAccount(
                username = "mandor1",
                passwordHash = "123456", // In production hashed with Argon2/SHA256
                fullName = "Budi Santoso",
                jabatan = "Mandor Lapangan Panen & Rawat",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 1",
                phone = "0812-7890-1122",
                profilePhotoUrl = "",
                isBiometricEnabled = true,
                registeredDeviceId = "DEV-SM-A546E-01",
                canCreateFinding = true,
                canRepair = true,
                canVerify = false,
                canViewAllReports = false
            ),
            UserAccount(
                username = "asisten1",
                passwordHash = "123456",
                fullName = "Ahmad Basuki, S.P.",
                jabatan = "Asisten Divisi 2",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 2",
                phone = "0813-8822-3344",
                profilePhotoUrl = "",
                isBiometricEnabled = true,
                registeredDeviceId = "DEV-SM-A546E-02",
                canCreateFinding = true,
                canRepair = true,
                canVerify = true,
                canViewAllReports = true
            ),
            UserAccount(
                username = "askep1",
                passwordHash = "123456",
                fullName = "Ir. Hendra Wijaya",
                jabatan = "Kepala Tanaman / Askep",
                estate = "Estate Riau Perdana",
                divisi = "Semua Divisi",
                phone = "0811-6655-4433",
                profilePhotoUrl = "",
                isBiometricEnabled = false,
                registeredDeviceId = "DEV-SM-A546E-03",
                canCreateFinding = true,
                canRepair = false,
                canVerify = true,
                canViewAllReports = true
            ),
            UserAccount(
                username = "manager1",
                passwordHash = "123456",
                fullName = "MDA Basuki, M.M.",
                jabatan = "Estate Manager",
                estate = "Estate Riau Perdana",
                divisi = "Semua Divisi",
                phone = "0811-9988-7766",
                profilePhotoUrl = "",
                isBiometricEnabled = true,
                registeredDeviceId = "DEV-SM-A546E-04",
                canCreateFinding = true,
                canRepair = true,
                canVerify = true,
                canViewAllReports = true
            )
        )
        database.userAccountDao().insertUsers(defaultUsers)

        // 2. Preload Operation Works
        val defaultWorks = listOf(
            OperationWork(
                id = "WRK-PAN-001",
                workType = "PANEN",
                subCategory = "Panen Rotasi 7 Hari",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 1",
                blok = "Blok A12",
                date = today,
                mandor = "Budi Santoso",
                pic = "Pemanen Regu 1 (14 Orang)",
                workersCount = 14,
                luasHa = 28.5,
                targetQty = 18500.0,
                targetUnit = "Kg",
                realisasiQty = 19240.0,
                realisasiUnit = "Kg",
                progressPercent = 100,
                jumlahTbs = 1012,
                tonaseKg = 19240.0,
                kondisiBuah = "Masak Optimal (BJR 19.0 kg)",
                latitude = 0.5385,
                longitude = 101.4480,
                gpsAccuracy = 3.5,
                catatan = "Ancak selesai bersih, buah terangkut seluruhnya ke TPH utama.",
                status = "SELESAI",
                syncStatus = "SYNCED"
            ),
            OperationWork(
                id = "WRK-SMP-002",
                workType = "SEMPROT",
                subCategory = "Semprot Piringan & Pasar Pikul",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 2",
                blok = "Blok B05",
                date = today,
                mandor = "Sukamto",
                pic = "Tim Semprot Div 2 (8 Orang)",
                workersCount = 8,
                luasHa = 32.0,
                targetQty = 32.0,
                targetUnit = "Ha",
                realisasiQty = 24.0,
                realisasiUnit = "Ha",
                progressPercent = 75,
                jenisHerbisida = "Glifosat 480 SL & Metil Metsulfuron",
                dosisHerbisida = "1.5 Liter/Ha + 75 gr/Ha",
                volumeLarutanLiter = 450.0,
                latitude = 0.5412,
                longitude = 101.4521,
                gpsAccuracy = 4.1,
                catatan = "Cuaca cerah, piringan bebas gulma berdaun lebar.",
                status = "SEDANG_BERJALAN",
                syncStatus = "SYNCED"
            ),
            OperationWork(
                id = "WRK-PUP-003",
                workType = "PEMUPUKAN",
                subCategory = "Aplikasi Pupuk MOP Semester II",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 1",
                blok = "Blok C08",
                date = today,
                mandor = "Budi Santoso",
                pic = "Tim Pemupuk Mekanis",
                workersCount = 10,
                luasHa = 40.0,
                targetQty = 8000.0,
                targetUnit = "Kg",
                realisasiQty = 8000.0,
                realisasiUnit = "Kg",
                progressPercent = 100,
                jenisPupuk = "MOP (Muriate of Potash / KCl 60%)",
                dosisPupukKgPokok = 1.5,
                latitude = 0.5361,
                longitude = 101.4429,
                gpsAccuracy = 3.8,
                catatan = "Ditebar merata di lingkar piringan pokok sawit TM.",
                status = "SELESAI",
                syncStatus = "SYNCED"
            ),
            OperationWork(
                id = "WRK-INF-004",
                workType = "INFRASTRUKTUR",
                subCategory = "Perbaikan Jalan Main Road & Gorong-gorong",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 3",
                blok = "Blok D02",
                date = today,
                mandor = "Rahmat Hidayat",
                pic = "Operator Motor Grader & Compact",
                workersCount = 4,
                luasHa = 2.4,
                targetQty = 3500.0,
                targetUnit = "Meter",
                realisasiQty = 1800.0,
                realisasiUnit = "Meter",
                progressPercent = 51,
                latitude = 0.5450,
                longitude = 101.4590,
                gpsAccuracy = 5.0,
                catatan = "Penimbunan batu sirtu dan pemadatan jalan angkut TBS.",
                status = "SEDANG_BERJALAN",
                syncStatus = "PENDING_SYNC"
            ),
            OperationWork(
                id = "WRK-CUS-005",
                workType = "LAINNYA",
                subCategory = "Sensus Global Hama Ulat Api & Rayap",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 2",
                blok = "Blok B09",
                date = today,
                mandor = "Sukamto",
                pic = "Sensus Team",
                workersCount = 6,
                luasHa = 25.0,
                targetQty = 25.0,
                targetUnit = "Ha",
                realisasiQty = 0.0,
                realisasiUnit = "Ha",
                progressPercent = 0,
                latitude = 0.5428,
                longitude = 101.4499,
                gpsAccuracy = 4.8,
                catatan = "Jadwal pengerjaan dimulai esok pagi pukul 07:00.",
                status = "BELUM_DIKERJAKAN",
                syncStatus = "SYNCED"
            )
        )
        database.operationWorkDao().insertWorks(defaultWorks)

        // 3. Preload Field Findings (Temuan Lapangan)
        val defaultFindings = listOf(
            FieldFinding(
                id = "TMN-EST1-DIV1-20260820-001",
                findingNumber = "TMN-EST1-DIV1-001",
                date = today,
                time = "08:15",
                reporterName = "Ahmad Basuki, S.P.",
                reporterJabatan = "Asisten Divisi 2",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 1",
                blok = "Blok A14",
                findingType = "Panen",
                priority = "TINGGI",
                description = "Ditemukan buah mentah terpanen 12 janjang dan brondolan tidak dikutip di gawangan pasar rintis.",
                picName = "Budi Santoso",
                targetDueDate = "2026-08-21",
                latitude = 0.5392,
                longitude = 101.4471,
                gpsAccuracy = 3.9,
                status = "ON_PROGRESS",
                progressPercent = 50,
                photoBefore = "sample_finding_buah_mentah",
                photoAfter = "",
                watermarkText = "TEMUAN PERKEBUNAN\nAhmad Basuki | $today 08:15\nLat: 0.5392, Long: 101.4471\nEstate Riau Perdana - Div 1 - Blok A14\nNo: TMN-EST1-DIV1-001",
                syncStatus = "SYNCED"
            ),
            FieldFinding(
                id = "TMN-EST1-DIV2-20260820-002",
                findingNumber = "TMN-EST1-DIV2-002",
                date = today,
                time = "09:30",
                reporterName = "Ir. Hendra Wijaya",
                reporterJabatan = "Kepala Tanaman / Askep",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 2",
                blok = "Blok B07",
                findingType = "Drainase",
                priority = "KRITIS",
                description = "Parit outlet utama tersumbat tumpukan kayu lapuk mengakibatkan genangan air di ancak panen baris 15-22.",
                picName = "Budi Santoso",
                targetDueDate = "2026-08-20",
                latitude = 0.5420,
                longitude = 101.4533,
                gpsAccuracy = 3.2,
                status = "WAITING_VERIFICATION",
                progressPercent = 100,
                photoBefore = "sample_finding_drainase_sumbat",
                photoAfter = "sample_finding_drainase_bersih",
                watermarkText = "TEMUAN PERKEBUNAN\nIr. Hendra Wijaya | $today 09:30\nLat: 0.5420, Long: 101.4533\nEstate Riau Perdana - Div 2 - Blok B07\nNo: TMN-EST1-DIV2-002",
                syncStatus = "SYNCED"
            ),
            FieldFinding(
                id = "TMN-EST1-DIV2-20260820-003",
                findingNumber = "TMN-EST1-DIV2-003",
                date = today,
                time = "10:45",
                reporterName = "Budi Santoso",
                reporterJabatan = "Mandor Lapangan",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 2",
                blok = "Blok B12",
                findingType = "K3",
                priority = "SEDANG",
                description = "Operator semprot herbisida tidak memakai kacamata pelindung dan sarung tangan nitril standar K3.",
                picName = "Sukamto",
                targetDueDate = "2026-08-20",
                latitude = 0.5441,
                longitude = 101.4510,
                gpsAccuracy = 4.0,
                status = "REVISI",
                progressPercent = 25,
                photoBefore = "sample_finding_k3_apd",
                photoAfter = "",
                watermarkText = "TEMUAN PERKEBUNAN\nBudi Santoso | $today 10:45\nLat: 0.5441, Long: 101.4510\nEstate Riau Perdana - Div 2 - Blok B12\nNo: TMN-EST1-DIV2-003",
                syncStatus = "SYNCED"
            ),
            FieldFinding(
                id = "TMN-EST1-DIV3-20260820-004",
                findingNumber = "TMN-EST1-DIV3-004",
                date = "2026-08-19",
                time = "14:10",
                reporterName = "Ahmad Basuki, S.P.",
                reporterJabatan = "Asisten Divisi 2",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 3",
                blok = "Blok C03",
                findingType = "Jalan",
                priority = "TINGGI",
                description = "Jembatan kayu jembatan blok patah satu gelondong, rawan amblas untuk truk DT 8 ton muatan sawit.",
                picName = "Rahmat Hidayat",
                targetDueDate = "2026-08-20",
                latitude = 0.5350,
                longitude = 101.4410,
                gpsAccuracy = 2.9,
                status = "VERIFIED",
                progressPercent = 100,
                photoBefore = "sample_jembatan_rusak",
                photoAfter = "sample_jembatan_diperbaiki",
                watermarkText = "TEMUAN PERKEBUNAN\nAhmad Basuki | 2026-08-19 14:10\nLat: 0.5350, Long: 101.4410\nEstate Riau Perdana - Div 3 - Blok C03\nNo: TMN-EST1-DIV3-004",
                syncStatus = "SYNCED",
                isLocked = true
            ),
            FieldFinding(
                id = "TMN-EST1-DIV1-20260820-005",
                findingNumber = "TMN-EST1-DIV1-005",
                date = today,
                time = "11:20",
                reporterName = "MDA Basuki, M.M.",
                reporterJabatan = "Estate Manager",
                estate = "Estate Riau Perdana",
                divisi = "Divisi 1",
                blok = "Blok A05",
                findingType = "Hama/penyakit",
                priority = "KRITIS",
                description = "Ditemukan serangan ulat api (Setothosea asigna) populasi > 10 ulat per pelepah di 15 pokok berdekatan.",
                picName = "Budi Santoso",
                targetDueDate = "2026-08-21",
                latitude = 0.5370,
                longitude = 101.4455,
                gpsAccuracy = 3.6,
                status = "OPEN",
                progressPercent = 0,
                photoBefore = "sample_ulat_api",
                photoAfter = "",
                watermarkText = "TEMUAN PERKEBUNAN\nMDA Basuki | $today 11:20\nLat: 0.5370, Long: 101.4455\nEstate Riau Perdana - Div 1 - Blok A05\nNo: TMN-EST1-DIV1-005",
                syncStatus = "SYNCED"
            )
        )
        database.fieldFindingDao().insertFindings(defaultFindings)

        // 4. Preload Progress History
        val defaultHistories = listOf(
            FindingProgressHistory(
                findingId = "TMN-EST1-DIV2-002",
                userName = "Budi Santoso",
                date = today,
                time = "10:15",
                percentage = 25,
                notes = "Mobilisasi 4 tenaga kerja manual dan pembersihan tahap 1.",
                latitude = 0.5420,
                longitude = 101.4533,
                gpsAccuracy = 3.4
            ),
            FindingProgressHistory(
                findingId = "TMN-EST1-DIV2-002",
                userName = "Budi Santoso",
                date = today,
                time = "13:40",
                percentage = 75,
                notes = "Kayu lapuk diangkat dan aliran parit mulai mengalir lancar.",
                latitude = 0.5420,
                longitude = 101.4533,
                gpsAccuracy = 3.1
            ),
            FindingProgressHistory(
                findingId = "TMN-EST1-DIV2-002",
                userName = "Budi Santoso",
                date = today,
                time = "15:10",
                percentage = 100,
                notes = "Pekerjaan tuntas 100%, parit normal, foto sesudah telah diunggah. Mengajukan verifikasi.",
                latitude = 0.5420,
                longitude = 101.4533,
                gpsAccuracy = 3.0
            )
        )
        database.findingProgressHistoryDao().insertHistories(defaultHistories)

        // 5. Preload Verification record for verified finding
        val verifiedDoc = FindingVerification(
            verificationId = "VRF-2026-X984",
            findingId = "TMN-EST1-DIV3-20260820-004",
            findingNumber = "TMN-EST1-DIV3-004",
            description = "Jembatan kayu diperkuat bantalan baja dan balok kayu ulin baru.",
            picName = "Rahmat Hidayat",
            verifierName = "Ir. Hendra Wijaya",
            verifierJabatan = "Kepala Tanaman / Askep",
            date = today,
            time = "16:20",
            verificationLatitude = 0.5351,
            verificationLongitude = 101.4411,
            verificationGpsAccuracy = 2.8,
            decision = "VERIFIED",
            signaturePath = "M10,40 Q30,10 60,35 T120,40",
            deviceInfo = "SM-A546E (Android 14) / Cert-ID: #98421",
            documentHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            statement = "Saya menyatakan telah memeriksa secara langsung kondisi fisik lapangan dan keabsahan perbaikan temuan ini."
        )
        database.findingVerificationDao().insertVerification(verifiedDoc)

        // 6. Preload Notifications
        val defaultNotifications = listOf(
            AppNotification(
                title = "Verifikasi Diajukan",
                message = "PIC Budi Santoso mengajukan verifikasi penyelesaian temuan Drainase Parit Blok B07.",
                category = "WAITING_VERIFICATION",
                referenceId = "TMN-EST1-DIV2-20260820-002"
            ),
            AppNotification(
                title = "Temuan Baru Dibuat",
                message = "Estate Manager membuat temuan kritis Ulat Api di Blok A05.",
                category = "FINDING_NEW",
                referenceId = "TMN-EST1-DIV1-20260820-005"
            ),
            AppNotification(
                title = "Revisi Diperlukan",
                message = "Temuan APD K3 Semprot di Blok B12 memerlukan perbaikan ulang perlengkapan APD.",
                category = "REVISION",
                referenceId = "TMN-EST1-DIV2-20260820-003"
            )
        )
        database.appNotificationDao().insertNotifications(defaultNotifications)

        // 7. Preload Audit Trail Logs
        val defaultLogs = listOf(
            AuditLog(
                userName = "Ahmad Basuki, S.P.",
                userJabatan = "Asisten Divisi 2",
                actionType = "LOGIN",
                details = "User login sukses via Biometric Device Binding",
                referenceId = "DEV-SM-A546E-02"
            ),
            AuditLog(
                userName = "MDA Basuki, M.M.",
                userJabatan = "Estate Manager",
                actionType = "CREATE_FINDING",
                details = "Membuat temuan baru: Hama Ulat Api di Blok A05",
                referenceId = "TMN-EST1-DIV1-20260820-005"
            ),
            AuditLog(
                userName = "Budi Santoso",
                userJabatan = "Mandor Lapangan",
                actionType = "UPDATE_PROGRESS",
                details = "Memperbarui progres temuan Drainase Blok B07 menjadi 100% dan mengajukan verifikasi",
                referenceId = "TMN-EST1-DIV2-20260820-002"
            ),
            AuditLog(
                userName = "Ir. Hendra Wijaya",
                userJabatan = "Kepala Tanaman / Askep",
                actionType = "DIGITAL_SIGNATURE",
                details = "Menandatangani digital & memverifikasi temuan Jembatan C03 (VRF-2026-X984)",
                referenceId = "TMN-EST1-DIV3-20260820-004"
            )
        )
        database.auditLogDao().insertLogs(defaultLogs)
    }
}
