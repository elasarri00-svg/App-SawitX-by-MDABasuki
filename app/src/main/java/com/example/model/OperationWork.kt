package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operation_works")
data class OperationWork(
    @PrimaryKey val id: String, // e.g. WRK-20260820-001
    val workType: String, // PANEN, SEMPROT, PEMUPUKAN, INFRASTRUKTUR, LAINNYA
    val subCategory: String, // e.g. "Piringan", "Jalan", "Urea", "Kastrasi"
    val estate: String,
    val divisi: String,
    val blok: String,
    val date: String,
    val mandor: String,
    val pic: String,
    val workersCount: Int,
    val luasHa: Double,
    val targetQty: Double,
    val targetUnit: String,
    val realisasiQty: Double,
    val realisasiUnit: String,
    val progressPercent: Int, // 0 - 100
    // Specific for Panen
    val jumlahTbs: Int = 0,
    val tonaseKg: Double = 0.0,
    val kondisiBuah: String = "Masak Optimal", // Mentah, Masak, Lewat Masak, Busuk, Tangkai Panjang
    // Specific for Semprot
    val jenisHerbisida: String = "",
    val dosisHerbisida: String = "",
    val volumeLarutanLiter: Double = 0.0,
    // Specific for Pemupukan
    val jenisPupuk: String = "",
    val dosisPupukKgPokok: Double = 0.0,
    // Photos & Location
    val photoBefore: String = "",
    val photoAfter: String = "",
    val latitude: Double = 0.5381,
    val longitude: Double = 101.4478,
    val gpsAccuracy: Double = 4.2,
    val catatan: String = "",
    val status: String = "SEDANG_BERJALAN", // BELUM_DIKERJAKAN, SEDANG_BERJALAN, SELESAI
    val syncStatus: String = "SYNCED", // SYNCED, PENDING_SYNC, FAILED_SYNC
    val timestamp: Long = System.currentTimeMillis()
)
