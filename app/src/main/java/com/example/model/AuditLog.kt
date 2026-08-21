package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val userJabatan: String,
    val actionType: String, // LOGIN, LOGOUT, CREATE_FINDING, UPDATE_PROGRESS, UPLOAD_PHOTO, CHANGE_STATUS, VERIFICATION, REVISION, DIGITAL_SIGNATURE, CLOSE_FINDING, BACKUP_DB, SYNC_DATA
    val details: String,
    val referenceId: String = "",
    val deviceInfo: String = "SM-A546E (Android 14)",
    val ipAddress: String = "192.168.1.104",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
