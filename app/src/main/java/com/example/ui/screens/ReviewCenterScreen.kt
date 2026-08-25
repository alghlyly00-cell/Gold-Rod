package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import com.example.ui.navigation.MemberDetails

data class Discrepancy(
    val type: String,
    val memberName: String,
    val memberId: Int?,
    val value: String,
    val reason: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewCenterScreen(navController: NavController, viewModel: AppViewModel, associationId: Int) {
    val assoc by viewModel.getAssociation(associationId).collectAsStateWithLifecycle()
    val members by viewModel.getMembersByAssociation(associationId).collectAsStateWithLifecycle()
    val payments by viewModel.getPaymentsByAssociation(associationId).collectAsStateWithLifecycle()

    if (assoc == null) return

    val discrepancies = mutableListOf<Discrepancy>()

    // Check for over-payment
    members.forEach { m ->
        val paid = payments.filter { it.memberId == m.id }.sumOf { it.amount }
        if (paid > m.expectedTotalAmount) {
            discrepancies.add(
                Discrepancy(
                    type = "مبلغ زائد",
                    memberName = m.name,
                    memberId = m.id,
                    value = "${paid - m.expectedTotalAmount} ر.س",
                    reason = "دفع العضو مبلغاً يتجاوز المبلغ المطلوب منه في هذه الجمعية."
                )
            )
        }
    }

    // Check for negative payments
    payments.filter { it.amount < 0 }.forEach { p ->
        val member = members.find { it.id == p.memberId }
        discrepancies.add(
            Discrepancy(
                type = "دفعة سالبة",
                memberName = member?.name ?: "مجهول",
                memberId = member?.id,
                value = "${p.amount} ر.س",
                reason = "تم تسجيل دفعة بقيمة سالبة، وهذا غير صالح."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مركز المراجعة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        if (discrepancies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("لا توجد مشاكل مالية حالياً. الحسابات متطابقة ✓", color = Color(0xFF006A6A), fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FF))
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(discrepancies) { issue ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1E8))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF8B4A00))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(issue.type, fontWeight = FontWeight.Bold, color = Color(0xFF8B4A00))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("العضو: ${issue.memberName}", fontSize = 14.sp)
                            Text("القيمة: ${issue.value}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB00020))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(issue.reason, fontSize = 12.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (issue.memberId != null) {
                                Button(
                                    onClick = { navController.navigate(MemberDetails(issue.memberId)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006A6A))
                                ) {
                                    Text("مراجعة بيانات العضو")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
