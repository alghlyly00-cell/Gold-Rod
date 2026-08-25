package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.model.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(log: AuditLog)

    @Query("SELECT * FROM audit_logs WHERE associationId = :associationId ORDER BY timestamp DESC")
    fun getLogsByAssociation(associationId: Int): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getLogsByMember(memberId: Int): Flow<List<AuditLog>>
}
