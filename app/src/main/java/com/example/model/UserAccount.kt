package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val fullName: String,
    val jabatan: String,
    val estate: String,
    val divisi: String,
    val phone: String,
    val profilePhotoUrl: String = "",
    val isBiometricEnabled: Boolean = false,
    val registeredDeviceId: String = "DEV-SM-A546E-01",
    val canCreateFinding: Boolean = true,
    val canRepair: Boolean = true,
    val canVerify: Boolean = false,
    val canViewAllReports: Boolean = false,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)
