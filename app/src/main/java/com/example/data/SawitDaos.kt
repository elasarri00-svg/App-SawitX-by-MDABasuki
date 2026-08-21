package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AppNotification
import com.example.model.AuditLog
import com.example.model.FieldFinding
import com.example.model.FindingProgressHistory
import com.example.model.FindingVerification
import com.example.model.OperationWork
import com.example.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)
}

@Dao
interface OperationWorkDao {
    @Query("SELECT * FROM operation_works ORDER BY timestamp DESC")
    fun getAllWorks(): Flow<List<OperationWork>>

    @Query("SELECT * FROM operation_works WHERE workType = :type ORDER BY timestamp DESC")
    fun getWorksByType(type: String): Flow<List<OperationWork>>

    @Query("SELECT * FROM operation_works WHERE id = :id LIMIT 1")
    suspend fun getWorkById(id: String): OperationWork?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWork(work: OperationWork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorks(works: List<OperationWork>)

    @Update
    suspend fun updateWork(work: OperationWork)

    @Query("DELETE FROM operation_works WHERE id = :id")
    suspend fun deleteWorkById(id: String)
}

@Dao
interface FieldFindingDao {
    @Query("SELECT * FROM field_findings ORDER BY createdAt DESC")
    fun getAllFindings(): Flow<List<FieldFinding>>

    @Query("SELECT * FROM field_findings WHERE id = :id LIMIT 1")
    suspend fun getFindingById(id: String): FieldFinding?

    @Query("SELECT * FROM field_findings WHERE status = :status ORDER BY createdAt DESC")
    fun getFindingsByStatus(status: String): Flow<List<FieldFinding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinding(finding: FieldFinding)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFindings(findings: List<FieldFinding>)

    @Update
    suspend fun updateFinding(finding: FieldFinding)

    @Query("DELETE FROM field_findings WHERE id = :id")
    suspend fun deleteFindingById(id: String)
}

@Dao
interface FindingProgressHistoryDao {
    @Query("SELECT * FROM finding_progress_history WHERE findingId = :findingId ORDER BY timestamp ASC")
    fun getHistoryForFinding(findingId: String): Flow<List<FindingProgressHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: FindingProgressHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<FindingProgressHistory>)
}

@Dao
interface FindingVerificationDao {
    @Query("SELECT * FROM finding_verifications WHERE findingId = :findingId LIMIT 1")
    suspend fun getVerificationByFindingId(findingId: String): FindingVerification?

    @Query("SELECT * FROM finding_verifications ORDER BY timestamp DESC")
    fun getAllVerifications(): Flow<List<FindingVerification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: FindingVerification)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 300")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AuditLog>)
}

@Dao
interface AppNotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}
