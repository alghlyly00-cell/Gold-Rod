package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Association
import com.example.ui.AppViewModel
import com.example.ui.navigation.AssociationDetails
import com.example.ui.navigation.CreateAssociation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: AppViewModel) {
    val associations by viewModel.allAssociations.collectAsStateWithLifecycle()

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
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column {
                            Text("الرئيسية", fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                            Text("جمعياتي", fontSize = 12.sp, color = Color(0xFF40484C))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Notifications, contentDescription = "تنبيهات", tint = Color(0xFF40484C))
                        }
                        IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Settings, contentDescription = "إعدادات", tint = Color(0xFF40484C))
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            .clickable { navController.navigate(CreateAssociation) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.List, contentDescription = "السجلات", tint = Color(0xFF40484C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("السجلات", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF40484C))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Person, contentDescription = "حسابي", tint = Color(0xFF40484C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("حسابي", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF40484C))
                    }
                }
            }
        }
    ) { padding ->
        if (associations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FF))
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد جمعيات، اضغط على زر + لإضافة جمعية جديدة", color = Color(0xFF40484C))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FF))
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(associations) { association ->
                    AssociationCard(association, onClick = {
                        navController.navigate(AssociationDetails(association.id))
                    })
                }
            }
        }
    }
}

@Composable
fun AssociationCard(association: Association, onClick: () -> Unit) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF006A6A), Color(0xFF004F4F))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    text = association.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("${association.subscriptionAmount} ر.س / شهر", color = Color.White, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("البداية", color = Color(0xFFB2DFDF), fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(sdf.format(Date(association.startDate)), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("النهاية", color = Color(0xFFB2DFDF), fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(sdf.format(Date(association.endDate)), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(modifier = Modifier.weight(0.5f), horizontalAlignment = Alignment.End) {
                    Text("الأعضاء", color = Color(0xFFB2DFDF), fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${association.expectedMembersCount}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
