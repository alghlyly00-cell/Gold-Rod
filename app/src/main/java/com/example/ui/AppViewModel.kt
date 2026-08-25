package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.model.Association
import com.example.model.Member
import com.example.model.Payment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val allAssociations: StateFlow<List<Association>> = repository.allAssociations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getAssociation(id: Int): StateFlow<Association?> = repository.getAssociationById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getMembersByAssociation(id: Int): StateFlow<List<Member>> = repository.getMembersByAssociation(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMember(id: Int): StateFlow<Member?> = repository.getMemberById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getPaymentsByAssociation(id: Int): StateFlow<List<Payment>> = repository.getPaymentsByAssociation(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getPaymentsByMember(id: Int): StateFlow<List<Payment>> = repository.getPaymentsByMember(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getAuditLogsByAssociation(id: Int): StateFlow<List<com.example.model.AuditLog>> = repository.getLogsByAssociation(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertAssociation(association: Association) = viewModelScope.launch {
        repository.insertAssociation(association)
    }

    fun updateAssociation(association: Association) = viewModelScope.launch {
        repository.updateAssociation(association)
    }

    fun deleteAssociation(association: Association) = viewModelScope.launch {
        repository.deleteAssociation(association)
        val log = com.example.model.AuditLog(
            associationId = association.id,
            memberId = null,
            actionType = "ASSOCIATION_DELETED",
            memberName = association.name,
            oldAmount = null,
            newAmount = null,
            timestamp = System.currentTimeMillis(),
            reason = "تم حذف الجمعية بالكامل"
        )
        repository.insertAuditLog(log)
    }

    fun insertMember(member: Member) = viewModelScope.launch {
        repository.insertMember(member)
    }

    fun updateMember(member: Member) = viewModelScope.launch {
        repository.updateMember(member)
    }

    fun deleteMember(member: Member) = viewModelScope.launch {
        repository.deleteMember(member)
        val log = com.example.model.AuditLog(
            associationId = member.associationId,
            memberId = member.id,
            actionType = "MEMBER_DELETED",
            memberName = member.name,
            oldAmount = null,
            newAmount = null,
            timestamp = System.currentTimeMillis(),
            reason = "تم حذف العضو"
        )
        repository.insertAuditLog(log)
    }

    fun recordPayment(member: Member, paymentAmount: Double, note: String?) = viewModelScope.launch {
        val newPayment = Payment(
            memberId = member.id,
            associationId = member.associationId,
            amount = paymentAmount,
            date = System.currentTimeMillis(),
            transactionNumber = "TXN-${System.currentTimeMillis()}",
            status = "COMPLETED",
            notes = note
        )
        repository.insertPaymentAndUpdateMember(newPayment, member)

        val log = com.example.model.AuditLog(
            associationId = member.associationId,
            memberId = member.id,
            actionType = "PAYMENT_ADDED",
            memberName = member.name,
            oldAmount = null,
            newAmount = paymentAmount,
            timestamp = System.currentTimeMillis(),
            reason = "تم تسجيل دفعة جديدة"
        )
        repository.insertAuditLog(log)
    }

    fun deletePayment(payment: Payment, member: Member) = viewModelScope.launch {
        repository.deletePaymentAndUpdateMember(payment, member)
        val log = com.example.model.AuditLog(
            associationId = member.associationId,
            memberId = member.id,
            actionType = "PAYMENT_DELETED",
            memberName = member.name,
            oldAmount = payment.amount,
            newAmount = null,
            timestamp = System.currentTimeMillis(),
            reason = "تم حذف الدفعة"
        )
        repository.insertAuditLog(log)
    }
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
