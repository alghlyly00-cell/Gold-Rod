package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable object Splash
@Serializable object Home
@Serializable object CreateAssociation
@Serializable data class AssociationDetails(val id: Int)
@Serializable data class CreateMember(val associationId: Int)
@Serializable data class MemberDetails(val memberId: Int)
@Serializable data class EditMember(val memberId: Int)
@Serializable data class FinancialAnalysis(val associationId: Int)
@Serializable data class ReviewCenter(val associationId: Int)
