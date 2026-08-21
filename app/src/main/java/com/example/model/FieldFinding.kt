package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_findings")
data class FieldFinding(
    @PrimaryKey val id: String, // e.g. TMN-EST1-DIV2-20260820-001
    val findingNumber: String,
    val date: String,
    val time: String,
    val reporterName: String,
    val reporterJabatan: String,
    val estate: String,
    val divisi: String,
    val blok: String,
    val findingType: String, // Panen, Semprot, Pupuk, Infrastruktur, Jalan, Drainase, K3, Lingkungan, Alat kerja, Alat berat, Kendaraan, Administrasi, Tanaman, Hama/penyakit, Keamanan, Housekeeping, Lainnya
    val priority: String, // RENDAH, SEDANG, TINGGI, KRITIS
    val description: String,
    val picName: String,
    val targetDueDate: String,
    val latitude: Double,
    val longitude: Double,
    val gpsAccuracy: Double, // in meters
    val status: String = "OPEN", // OPEN, ON_PROGRESS, WAITING_VERIFICATION, REVISI, VERIFIED, CLOSED
    val progressPercent: Int = 0, // 0, 25, 50, 75, 100
    val photoBefore: String = "",
    val photoAfter: String = "",
    val watermarkText: String = "",
    val syncStatus: String = "SYNCED", // SYNCED, PENDING_SYNC, FAILED_SYNC
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isLocked: Boolean = false // Locked after verified/closed unless revisi
)

@Entity(tableName = "finding_progress_history")
data class FindingProgressHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val findingId: String,
    val userName: String,
    val date: String,
    val time: String,
    val percentage: Int, // 0, 25, 50, 75, 100
    val notes: String,
    val photoUri: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val gpsAccuracy: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "finding_verifications")
data class FindingVerification(
    @PrimaryKey val verificationId: String, // VRF-2026-X984
    val findingId: String,
    val findingNumber: String,
    val description: String,
    val picName: String,
    val verifierName: String,
    val verifierJabatan: String,
    val date: String,
    val time: String,
    val verificationLatitude: Double,
    val verificationLongitude: Double,
    val verificationGpsAccuracy: Double,
    val decision: String, // VERIFIED, REVISI
    val revisionReason: String = "",
    val signaturePath: String = "", // SVG/Canvas path points
    val deviceInfo: String = "Android SM-A546E / OneUI 6.1",
    val documentHash: String = "", // SHA256 security hash
    val statement: String = "Saya menyatakan telah memeriksa secara langsung kondisi fisik lapangan dan keabsahan perbaikan temuan ini.",
    val timestamp: Long = System.currentTimeMillis()
)
