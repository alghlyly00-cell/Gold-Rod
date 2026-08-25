package com.example.data

import com.example.model.Association
import com.example.model.Member
import com.example.model.Payment
import com.example.model.AuditLog
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val associationDao: AssociationDao,
    private val memberDao: MemberDao,
    private val paymentDao: PaymentDao,
    private val auditLogDao: AuditLogDao
) {
    val allAssociations: Flow<List<Association>> = associationDao.getAllAssociations()

    fun getAssociationById(id: Int) = associationDao.getAssociationById(id)
    suspend fun insertAssociation(association: Association) = associationDao.insertAssociation(association)
    suspend fun updateAssociation(association: Association) = associationDao.updateAssociation(association)
    suspend fun deleteAssociation(association: Association) = associationDao.deleteAssociation(association)

    fun getMembersByAssociation(associationId: Int) = memberDao.getMembersByAssociation(associationId)
    fun getMemberById(id: Int) = memberDao.getMemberById(id)
    suspend fun insertMember(member: Member) = memberDao.insertMember(member)
    suspend fun updateMember(member: Member) = memberDao.updateMember(member)
    suspend fun deleteMember(member: Member) = memberDao.deleteMember(member)

    fun getPaymentsByMember(memberId: Int) = paymentDao.getPaymentsByMember(memberId)
    fun getPaymentsByAssociation(associationId: Int) = paymentDao.getPaymentsByAssociation(associationId)
    
    suspend fun insertPaymentAndUpdateMember(payment: Payment, member: Member) {
        paymentDao.insertPayment(payment)
        memberDao.updateMember(member)
    }

    suspend fun deletePaymentAndUpdateMember(payment: Payment, member: Member) {
        paymentDao.deletePayment(payment)
        memberDao.updateMember(member)
    }

    suspend fun insertAuditLog(log: AuditLog) = auditLogDao.insert(log)
    fun getLogsByAssociation(associationId: Int) = auditLogDao.getLogsByAssociation(associationId)
    fun getLogsByMember(memberId: Int) = auditLogDao.getLogsByMember(memberId)
}
