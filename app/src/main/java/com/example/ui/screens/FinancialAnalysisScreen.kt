package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialAnalysisScreen(navController: NavController, viewModel: AppViewModel, associationId: Int) {
    val assoc by viewModel.getAssociation(associationId).collectAsStateWithLifecycle()
    val members by viewModel.getMembersByAssociation(associationId).collectAsStateWithLifecycle()
    val payments by viewModel.getPaymentsByAssociation(associationId).collectAsStateWithLifecycle()
    val auditLogs by viewModel.getAuditLogsByAssociation(associationId).collectAsStateWithLifecycle()

    if (assoc == null) return

    val totalExpected = assoc!!.expectedMembersCount * assoc!!.subscriptionAmount * assoc!!.durationMonths
    val totalPaid = payments.sumOf { it.amount }
    val remaining = totalExpected - totalPaid
    val collectionRate = if (totalExpected > 0) (totalPaid / totalExpected) * 100 else 0.0

    val fullyPaidMembers = members.count { m ->
        payments.filter { it.memberId == m.id }.sumOf { it.amount } >= m.expectedTotalAmount
    }
    val partiallyPaidMembers = members.count { m ->
        val paid = payments.filter { it.memberId == m.id }.sumOf { it.amount }
        paid > 0 && paid < m.expectedTotalAmount
    }
    val unpaidMembers = members.size - fullyPaidMembers - partiallyPaidMembers
    
    val lateMembersCount = members.count { m ->
        // Simulating late check based on expected cycles.
        // Actually, we calculate it dynamically:
        val expectedPerCycle = assoc!!.subscriptionAmount
        val now = System.currentTimeMillis()
        val passedMonths = ((now - assoc!!.startDate) / (1000L * 60 * 60 * 24 * 30)).toInt() + 1
        val expectedCurrentPaid = expectedPerCycle * passedMonths
        val paid = payments.filter { it.memberId == m.id }.sumOf { it.amount }
        paid < expectedCurrentPaid && now > assoc!!.startDate
    }

    val systemMessage = when {
        collectionRate >= 95 -> "الجمعية تسير بشكل ممتاز."
        collectionRate >= 70 -> "هناك بعض الدفعات المتأخرة."
        else -> "يوجد تأخر مالي كبير ويُنصح بمراجعة الدفعات."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التحليل المالي", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ملخص التحليل التلقائي", fontWeight = FontWeight.Bold, color = Color(0xFF006A6A))
                            TextButton(onClick = { /* TODO: Implement PDF Export later */ }) {
                                Text("تصدير تقرير", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(systemMessage, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("نسبة التحصيل: ${collectionRate.toInt()}%", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { (collectionRate / 100).toFloat() },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                            color = Color(0xFF006A6A),
                            trackColor = Color(0xFFDDE3EA)
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBox(modifier = Modifier.weight(1f), label = "إجمالي المطلوب", value = "$totalExpected")
                    StatBox(modifier = Modifier.weight(1f), label = "إجمالي المدفوع", value = "$totalPaid", textColor = Color(0xFF006A6A))
                    StatBox(modifier = Modifier.weight(1f), label = "المتبقي", value = "$remaining", textColor = Color(0xFFB00020))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBox(modifier = Modifier.weight(1f), label = "مكتمل الدفع", value = "$fullyPaidMembers")
                    StatBox(modifier = Modifier.weight(1f), label = "دفع جزئي", value = "$partiallyPaidMembers")
                    StatBox(modifier = Modifier.weight(1f), label = "لم يدفع", value = "$unpaidMembers", textColor = Color(0xFFB00020))
                }
            }

            item {
                Text("السجل المالي (Audit Log)", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
            
            items(auditLogs) { log ->
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(log.actionType, fontWeight = FontWeight.Bold, color = Color(0xFF006A6A))
                            Text(sdf.format(Date(log.timestamp)), fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("العضو: ${log.memberName ?: "غير معروف"}", fontSize = 14.sp)
                        if (log.oldAmount != null) {
                            Text("المبلغ القديم: ${log.oldAmount} ر.س", fontSize = 12.sp)
                        }
                        if (log.newAmount != null) {
                            Text("المبلغ الجديد: ${log.newAmount} ر.س", fontSize = 12.sp)
                        }
                        Text("السبب: ${log.reason ?: "لا يوجد"}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(modifier: Modifier = Modifier, label: String, value: String, textColor: Color = Color.Black) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
