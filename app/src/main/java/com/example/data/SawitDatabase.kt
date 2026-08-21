package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AppNotification
import com.example.model.AuditLog
import com.example.model.FieldFinding
import com.example.model.FindingProgressHistory
import com.example.model.FindingVerification
import com.example.model.OperationWork
import com.example.model.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccount::class,
        OperationWork::class,
        FieldFinding::class,
        FindingProgressHistory::class,
        FindingVerification::class,
        AuditLog::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SawitDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun operationWorkDao(): OperationWorkDao
    abstract fun fieldFindingDao(): FieldFindingDao
    abstract fun findingProgressHistoryDao(): FindingProgressHistoryDao
    abstract fun findingVerificationDao(): FindingVerificationDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: SawitDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SawitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SawitDatabase::class.java,
                    "sawitx_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                InitialDataSeeder.seedDatabase(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
