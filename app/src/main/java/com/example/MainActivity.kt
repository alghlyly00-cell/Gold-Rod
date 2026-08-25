package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.navigation.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "jamiaty-database"
    ).addMigrations(AppDatabase.MIGRATION_1_2).build()

    val repository = AppRepository(db.associationDao(), db.memberDao(), db.paymentDao(), db.auditLogDao())
    val viewModelFactory = AppViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, viewModelFactory)[AppViewModel::class.java]

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MyApplicationTheme {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Splash) {
                composable<Splash> {
                    SplashScreen(navController)
                }
                composable<Home> {
                    HomeScreen(navController, viewModel)
                }
                composable<CreateAssociation> {
                    CreateAssociationScreen(navController, viewModel)
                }
                composable<AssociationDetails> { backStackEntry ->
                    val route = backStackEntry.toRoute<AssociationDetails>()
                    AssociationDetailsScreen(navController, viewModel, route.id)
                }
                composable<CreateMember> { backStackEntry ->
                    val route = backStackEntry.toRoute<CreateMember>()
                    CreateMemberScreen(navController, viewModel, route.associationId)
                }
                composable<MemberDetails> { backStackEntry ->
                    val route = backStackEntry.toRoute<MemberDetails>()
                    MemberDetailsScreen(navController, viewModel, route.memberId)
                }
                composable<EditMember> { backStackEntry ->
                    val route = backStackEntry.toRoute<EditMember>()
                    EditMemberScreen(navController, viewModel, route.memberId)
                }
                composable<FinancialAnalysis> { backStackEntry ->
                    val route = backStackEntry.toRoute<FinancialAnalysis>()
                    FinancialAnalysisScreen(navController, viewModel, route.associationId)
                }
                composable<ReviewCenter> { backStackEntry ->
                    val route = backStackEntry.toRoute<ReviewCenter>()
                    ReviewCenterScreen(navController, viewModel, route.associationId)
                }
            }
        }
      }
    }
  }
}
