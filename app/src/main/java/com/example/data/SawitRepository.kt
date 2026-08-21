package com.example.data

import com.example.model.AppNotification
import com.example.model.AuditLog
import com.example.model.FieldFinding
import com.example.model.FindingProgressHistory
import com.example.model.FindingVerification
import com.example.model.OperationWork
import com.example.model.UserAccount
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SawitRepository(private val db: SawitDatabase) {

    // Users
    val allUsers: Flow<List<UserAccount>> = db.userAccountDao().getAllUsers()
    suspend fun getUserByUsername(username: String): UserAccount? = db.userAccountDao().getUserByUsername(username)
    suspend fun saveUser(user: UserAccount) = db.userAccountDao().insertUser(user)
    suspend fun updateUser(user: UserAccount) = db.userAccountDao().updateUser(user)

    // Works
    val allWorks: Flow<List<OperationWork>> = db.operationWorkDao().getAllWorks()
    fun getWorksByType(type: String): Flow<List<OperationWork>> = db.operationWorkDao().getWorksByType(type)
    suspend fun saveWork(work: OperationWork) = db.operationWorkDao().insertWork(work)
    suspend fun updateWork(work: OperationWork) = db.operationWorkDao().updateWork(work)
    suspend fun deleteWork(id: String) = db.operationWorkDao().deleteWorkById(id)

    // Findings
    val allFindings: Flow<List<FieldFinding>> = db.fieldFindingDao().getAllFindings()
    suspend fun getFindingById(id: String): FieldFinding? = db.fieldFindingDao().getFindingById(id)
    suspend fun saveFinding(finding: FieldFinding) = db.fieldFindingDao().insertFinding(finding)
    suspend fun updateFinding(finding: FieldFinding) = db.fieldFindingDao().updateFinding(finding)
    suspend fun deleteFinding(id: String) = db.fieldFindingDao().deleteFindingById(id)

    // Progress History
    fun getHistoryForFinding(findingId: String): Flow<List<FindingProgressHistory>> =
        db.findingProgressHistoryDao().getHistoryForFinding(findingId)
    suspend fun addProgressHistory(history: FindingProgressHistory) =
        db.findingProgressHistoryDao().insertHistory(history)

    // Verification
    suspend fun getVerificationByFindingId(findingId: String): FindingVerification? =
        db.findingVerificationDao().getVerificationByFindingId(findingId)
    val allVerifications: Flow<List<FindingVerification>> = db.findingVerificationDao().getAllVerifications()
    suspend fun saveVerification(verification: FindingVerification) =
        db.findingVerificationDao().insertVerification(verification)

    // Audit Logs
    val allAuditLogs: Flow<List<AuditLog>> = db.auditLogDao().getAllLogs()
    suspend fun logAudit(
        userName: String,
        userJabatan: String,
        actionType: String,
        details: String,
        referenceId: String = "",
        deviceInfo: String = "Android Device (SawitX)"
    ) {
        val log = AuditLog(
            userName = userName,
            userJabatan = userJabatan,
            actionType = actionType,
            details = details,
            referenceId = referenceId,
            deviceInfo = deviceInfo,
            ipAddress = "192.168.1." + (100..250).random()
        )
        db.auditLogDao().insertLog(log)
    }

    // Notifications
    val allNotifications: Flow<List<AppNotification>> = db.appNotificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.appNotificationDao().getUnreadCount()
    suspend fun addNotification(title: String, message: String, category: String, refId: String = "") {
        val notif = AppNotification(
            title = title,
            message = message,
            category = category,
            referenceId = refId
        )
        db.appNotificationDao().insertNotification(notif)
    }
    suspend fun markAllNotificationsAsRead() = db.appNotificationDao().markAllAsRead()

    // Helper to calculate SHA-256 for Digital Signatures and Documents
    fun generateDocumentHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
