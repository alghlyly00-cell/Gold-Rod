package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.model.Association
import com.example.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssociationScreen(navController: NavController, viewModel: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var membersCount by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) } // Default 1 month later
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val showStartDatePicker = {
        val c = Calendar.getInstance()
        c.timeInMillis = startDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newC = Calendar.getInstance()
                newC.set(year, month, dayOfMonth)
                startDate = newC.timeInMillis
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val showEndDatePicker = {
        val c = Calendar.getInstance()
        c.timeInMillis = endDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newC = Calendar.getInstance()
                newC.set(year, month, dayOfMonth)
                endDate = newC.timeInMillis
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إنشاء جمعية جديدة", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم الجمعية *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("الوصف (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = membersCount,
                onValueChange = { membersCount = it },
                label = { Text("عدد الأعضاء *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("مبلغ الاشتراك لكل عضو *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("مدة الجمعية (بالأشهر) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sdf.format(Date(startDate)),
                    onValueChange = {},
                    label = { Text("تاريخ البداية") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = showStartDatePicker) {
                            Icon(Icons.Default.DateRange, contentDescription = "اختر تاريخ")
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = sdf.format(Date(endDate)),
                    onValueChange = {},
                    label = { Text("تاريخ النهاية") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = showEndDatePicker) {
                            Icon(Icons.Default.DateRange, contentDescription = "اختر تاريخ")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank() && membersCount.isNotBlank() && amount.isNotBlank() && duration.isNotBlank()) {
                        val assoc = Association(
                            name = name,
                            description = description,
                            expectedMembersCount = membersCount.toIntOrNull() ?: 0,
                            subscriptionAmount = amount.toDoubleOrNull() ?: 0.0,
                            durationMonths = duration.toIntOrNull() ?: 0,
                            startDate = startDate,
                            endDate = endDate,
                            paymentMethod = null,
                            notes = null
                        )
                        viewModel.insertAssociation(assoc)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && membersCount.isNotBlank() && amount.isNotBlank() && duration.isNotBlank()
            ) {
                Text("إنشاء الجمعية", modifier = Modifier.padding(8.dp), fontSize = 18.sp)
            }
        }
    }
}
