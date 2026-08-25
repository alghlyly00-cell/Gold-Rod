package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import com.example.ui.navigation.Home
import com.example.ui.navigation.CreateMember
import com.example.ui.navigation.MemberDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssociationDetailsScreen(navController: NavController, viewModel: AppViewModel, associationId: Int) {
    val association by viewModel.getAssociation(associationId).collectAsStateWithLifecycle()
    val members by viewModel.getMembersByAssociation(associationId).collectAsStateWithLifecycle()
    val payments by viewModel.getPaymentsByAssociation(associationId).collectAsStateWithLifecycle()

    if (association == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val assoc = association!!
    
    val totalExpectedAmount = assoc.expectedMembersCount * assoc.subscriptionAmount * assoc.durationMonths
    val totalPaid = payments.sumOf { it.amount }
    val remainingAmount = max(0.0, totalExpectedAmount - totalPaid)
    
    val now = System.currentTimeMillis()
    val totalDays = max(1L, (assoc.endDate - assoc.startDate) / (1000 * 60 * 60 * 24))
    val passedDays = max(0L, (now - assoc.startDate) / (1000 * 60 * 60 * 24))
    val remainingDays = max(0L, (assoc.endDate - now) / (1000 * 60 * 60 * 24))

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF006A6A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person, // Fallback icon
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column {
                            Text("جمعيتي", fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                            Text(assoc.name, fontSize = 12.sp, color = Color(0xFF40484C))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة", tint = Color(0xFF40484C))
                        }
                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "إعدادات", tint = Color(0xFF40484C))
                        }
                    }
                }
                Divider(color = Color(0xFFDDE3EA), thickness = 1.dp)
            }
        },
        bottomBar = {
            Column {
                Divider(color = Color(0xFFDDE3EA), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate(Home) { popUpTo(0) } }) {
                        Box(modifier = Modifier.background(Color(0xFFCCE8E8), RoundedCornerShape(50)).padding(horizontal = 20.dp, vertical = 4.dp)) {
                            Icon(Icons.Default.Home, contentDescription = "الرئيسية", tint = Color(0xFF006A6A))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الرئيسية", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006A6A))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Person, contentDescription = "الأعضاء", tint = Color(0xFF40484C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الأعضاء", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF40484C))
                    }
                    Box(
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF006A6A))
                            .clickable { navController.navigate(CreateMember(assoc.id)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة عضو", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate(com.example.ui.navigation.FinancialAnalysis(assoc.id)) }) {
                        Icon(Icons.Default.List, contentDescription = "السجلات", tint = Color(0xFF40484C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("السجلات", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF40484C))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate(com.example.ui.navigation.ReviewCenter(assoc.id)) }) {
                        Icon(Icons.Default.Warning, contentDescription = "المراجعة", tint = Color(0xFF40484C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("المراجعة", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF40484C))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            val gradient = Brush.linearGradient(
                colors = listOf(Color(0xFF006A6A), Color(0xFF004F4F))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(gradient)
                    .padding(20.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Text("إجمالي المبالغ المجموعة", color = Color(0xFFB2DFDF), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$totalPaid", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ر.س", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                        val percentage = if (totalExpectedAmount > 0) ((totalPaid / totalExpectedAmount) * 100).toInt() else 0
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("$percentage% مكتمل", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = if (totalExpectedAmount > 0) (totalPaid / totalExpectedAmount).toFloat() else 0f)
                                .height(8.dp)
                                .background(Color.White, RoundedCornerShape(50))
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("المبلغ المتبقي", color = Color(0xFFB2DFDF), fontSize = 10.sp, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$remainingAmount ر.س", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("نهاية الجمعية", color = Color(0xFFB2DFDF), fontSize = 10.sp, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (remainingDays > 0) "بعد $remainingDays يوم" else "منتهية", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Members Stat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFE6F3F3), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF006A6A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("أعضاء", fontSize = 10.sp, color = Color(0xFF40484C))
                    Text("${members.size}/${assoc.expectedMembersCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                }
                // Cycle Stat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF2F1F9), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF5D5B8D), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("دورة رقم", fontSize = 10.sp, color = Color(0xFF40484C))
                    Text("01", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                }
                // Days passed Stat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFF1E8), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF8B4A00), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("أيام مرت", fontSize = 10.sp, color = Color(0xFF40484C))
                    Text("$passedDays", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFDDE3EA), RoundedCornerShape(28.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("حالة دفع الأعضاء (الدورة الحالية)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                    Text("عرض الكل", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF006A6A))
                }
                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                
                if (members.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا يوجد أعضاء حاليا.", color = Color(0xFF40484C))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(members) { member ->
                            MemberCard(
                                member = member,
                                totalPaidByMember = payments.filter { it.memberId == member.id }.sumOf { it.amount },
                                onClick = { navController.navigate(MemberDetails(member.id)) }
                            )
                        }
                    }
                }
            }
        }
        
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("حذف الجمعية") },
                text = { Text("هل أنت متأكد من حذف هذه الجمعية؟ سيتم حذف جميع الأعضاء والمدفوعات المتعلقة بها بشكل نهائي.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteAssociation(assoc)
                        navController.popBackStack()
                    }) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
fun MemberCard(member: com.example.model.Member, totalPaidByMember: Double, onClick: () -> Unit) {
    val isPaid = totalPaidByMember >= member.expectedTotalAmount
    val isPartial = totalPaidByMember > 0 && !isPaid
    val statusText = if (isPaid) "مدفوع" else if (isPartial) "جزئي" else "متأخر"
    
    val badgeBg = if (isPaid) Color(0xFFD1E8E8) else if (isPartial) Color(0xFFFFEFD4) else Color(0xFFFFDAD6)
    val badgeText = if (isPaid) Color(0xFF002020) else if (isPartial) Color(0xFF2F1500) else Color(0xFF410002)
    
    val avatarBg = if (isPaid) Color(0xFFEADDFF) else if (isPartial) Color(0xFFE0E2E5) else Color(0xFFFFDAD6)
    val avatarText = if (isPaid) Color(0xFF21005D) else if (isPartial) Color(0xFF191C1E) else Color(0xFF410002)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(member.name.take(1), fontWeight = FontWeight.Bold, color = avatarText)
            }
            Column {
                Text(member.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                Text("الرقم: #${member.memberNumber} | ${member.expectedTotalAmount} ر.س", fontSize = 10.sp, color = Color(0xFF70797E))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(statusText, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = badgeText)
            }
            if (isPaid) {
                Box(
                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF006A6A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            } else if (isPartial) {
                Box(
                    modifier = Modifier.size(24.dp).border(2.dp, Color(0xFF006A6A), RoundedCornerShape(6.dp)).background(Color(0xFF006A6A).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF006A6A), modifier = Modifier.size(16.dp))
                }
            } else {
                Box(
                    modifier = Modifier.size(24.dp).border(2.dp, Color(0xFFDDE3EA), RoundedCornerShape(6.dp))
                )
            }
        }
    }
    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemberScreen(navController: NavController, viewModel: AppViewModel, associationId: Int) {
    val association by viewModel.getAssociation(associationId).collectAsStateWithLifecycle()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var memberNumber by remember { mutableStateOf("") }

    if (association == null) return
    val assoc = association!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة عضو") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم العضو *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الهاتف (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = memberNumber,
                onValueChange = { memberNumber = it },
                label = { Text("رقم العضو *") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (name.isNotBlank() && memberNumber.isNotBlank()) {
                        val expectedTotal = assoc.subscriptionAmount * assoc.durationMonths
                        val newMember = com.example.model.Member(
                            associationId = associationId,
                            name = name,
                            phoneNumber = phone.takeIf { it.isNotBlank() },
                            memberNumber = memberNumber.toIntOrNull() ?: 0,
                            subscriptionAmount = assoc.subscriptionAmount,
                            expectedTotalAmount = expectedTotal,
                            status = "UNPAID"
                        )
                        viewModel.insertMember(newMember)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && memberNumber.isNotBlank()
            ) {
                Text("حفظ", modifier = Modifier.padding(8.dp), fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(navController: NavController, viewModel: AppViewModel, memberId: Int) {
    val member by viewModel.getMember(memberId).collectAsStateWithLifecycle()
    val payments by viewModel.getPaymentsByMember(memberId).collectAsStateWithLifecycle()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentAmount by remember { mutableStateOf("") }
    var confirmCancelPayment by remember { mutableStateOf<com.example.model.Payment?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (member == null) return
    val m = member!!
    val totalPaid = payments.sumOf { it.amount }
    val remaining = max(0.0, m.expectedTotalAmount - totalPaid)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(m.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف العضو")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                paymentAmount = m.subscriptionAmount.toString()
                showPaymentDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "تسجيل دفعة")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("بيانات العضو", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Divider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("رقم العضو:")
                            Text("${m.memberNumber}", fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("الإجمالي المطلوب:")
                            Text("${m.expectedTotalAmount} ريال", fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("المدفوع:")
                            Text("$totalPaid ريال", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("المتبقي:")
                            Text("$remaining ريال", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                        val statusText = if (remaining <= 0) "مكتمل" else if (totalPaid > 0) "جزئي" else "غير مدفوع"
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("الحالة:")
                            Text(statusText, color = if (remaining <= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Text("سجل الدفعات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (payments.isEmpty()) {
                item {
                    Text("لا توجد دفعات مسجلة.")
                }
            } else {
                items(payments) { payment ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${payment.amount} ريال", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(sdf.format(Date(payment.date)), style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = { confirmCancelPayment = payment },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("إلغاء")
                            }
                        }
                    }
                }
            }
        }
        
        if (showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("تسجيل دفعة جديدة") },
                text = {
                    OutlinedTextField(
                        value = paymentAmount,
                        onValueChange = { paymentAmount = it },
                        label = { Text("المبلغ") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val amount = paymentAmount.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.recordPayment(m, amount, null)
                            showPaymentDialog = false
                        }
                    }) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaymentDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }

        if (confirmCancelPayment != null) {
            AlertDialog(
                onDismissRequest = { confirmCancelPayment = null },
                title = { Text("تأكيد الإلغاء") },
                text = { Text("هل أنت متأكد من إلغاء هذه الدفعة؟") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePayment(confirmCancelPayment!!, m)
                        confirmCancelPayment = null
                    }) {
                        Text("نعم، ألغِ الدفعة", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmCancelPayment = null }) {
                        Text("تراجع")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("حذف العضو") },
                text = { Text("هل أنت متأكد من حذف هذا العضو؟ سيتم حذف جميع المدفوعات المتعلقة به بشكل نهائي.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteMember(m)
                        navController.popBackStack()
                    }) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
fun EditMemberScreen(navController: NavController, viewModel: AppViewModel, memberId: Int) {
    Text("Edit Member: $memberId")
}
