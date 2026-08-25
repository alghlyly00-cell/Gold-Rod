package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Association
import com.example.model.Member
import com.example.model.Payment
import com.example.model.AuditLog

@Database(entities = [Association::class, Member::class, Payment::class, AuditLog::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun associationDao(): AssociationDao
    abstract fun memberDao(): MemberDao
    abstract fun paymentDao(): PaymentDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `audit_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `associationId` INTEGER, 
                        `memberId` INTEGER, 
                        `actionType` TEXT NOT NULL, 
                        `memberName` TEXT, 
                        `oldAmount` REAL, 
                        `newAmount` REAL, 
                        `timestamp` INTEGER NOT NULL, 
                        `reason` TEXT
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_associationId` ON `audit_logs` (`associationId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_memberId` ON `audit_logs` (`memberId`)")
            }
        }
    }
}
