package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.GttcBottomNav
import com.example.ui.components.GttcTopBar
import com.example.ui.components.NavTab
import com.example.ui.screens.AccountDetailsScreen
import com.example.ui.screens.AccountListScreen
import com.example.ui.screens.AddEditAccountScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GttcAcManagerTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GttcAcManagerTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val isAddOrEditOpen by viewModel.isAddOrEditOpen.collectAsState()
    val accountToEdit by viewModel.accountToEdit.collectAsState()
    val selectedSeriesDetails by viewModel.selectedSeriesDetails.collectAsState()

    if (!isLoggedIn) {
        AdminLoginScreen(
            onLoginSuccess = { viewModel.loginAdmin("778899") }
        )
        return
    }

    // Handle Back Press hierarchy
    BackHandler(enabled = isAddOrEditOpen || selectedAccount != null || selectedSeriesDetails != null || activeTab != NavTab.DASHBOARD) {
        when {
            isAddOrEditOpen -> viewModel.closeAddOrEdit()
            selectedAccount != null -> viewModel.closeAccountDetails()
            selectedSeriesDetails != null -> viewModel.closeSeriesDetails()
            activeTab != NavTab.DASHBOARD -> viewModel.setTab(NavTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAddOrEditOpen && selectedAccount == null) {
                val subtitle = when (activeTab) {
                    NavTab.DASHBOARD -> "Dashboard & Statistics"
                    NavTab.ACCOUNTS -> "Account Management"
                    NavTab.ADD -> "Add New Account"
                    NavTab.SERIES -> if (selectedSeriesDetails != null) "Series Platforms" else "Series Wise Accounts"
                    NavTab.SETTINGS -> "Admin Settings & Data"
                }
                GttcTopBar(
                    subtitle = subtitle,
                    onAdminProfileClick = { viewModel.setTab(NavTab.SETTINGS) }
                )
            }
        },
        bottomBar = {
            if (!isAddOrEditOpen && selectedAccount == null) {
                GttcBottomNav(
                    selectedTab = activeTab,
                    onTabSelected = { tab ->
                        if (tab == NavTab.ADD) {
                            viewModel.openAddAccount()
                        } else {
                            viewModel.setTab(tab)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isAddOrEditOpen -> {
                    AddEditAccountScreen(
                        viewModel = viewModel,
                        accountToEdit = accountToEdit,
                        onDismiss = { viewModel.closeAddOrEdit() }
                    )
                }
                selectedAccount != null -> {
                    AccountDetailsScreen(
                        account = selectedAccount!!,
                        viewModel = viewModel,
                        onBack = { viewModel.closeAccountDetails() }
                    )
                }
                else -> {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            NavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                            NavTab.ACCOUNTS -> AccountListScreen(viewModel = viewModel)
                            NavTab.ADD -> DashboardScreen(viewModel = viewModel) // Add is handled via isAddOrEditOpen dialog
                            NavTab.SERIES -> SeriesScreen(viewModel = viewModel)
                            NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
