package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.CsvImportResult
import com.example.data.CsvManager
import com.example.model.Account
import com.example.model.ActivityLog
import com.example.model.PlatformItem
import com.example.model.SeriesData
import com.example.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class SortOption(val label: String) {
    NEWEST_FIRST("Newest Reg Date"),
    OLDEST_FIRST("Oldest Reg Date"),
    NAME_AZ("Name (A to Z)"),
    POINTS_HIGH("Points (High to Low)")
}

data class DashboardStats(
    val totalAccounts: Int = 0,
    val completedAccounts: Int = 0,
    val pendingAccounts: Int = 0,
    val totalEarnPoints: Int = 0,
    val recentlyCompletedSeries: String = "11-Series",
    val todayRegistrationsCount: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AppRepository(application)

    // Admin Auth State
    private val _isAdminLoggedIn = MutableStateFlow(true) // Admin authenticated by default in management session
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminName = MutableStateFlow("GTTC Master Admin")
    val adminName: StateFlow<String> = _adminName.asStateFlow()

    private val _adminEmail = MutableStateFlow("admin@gttc.manager")
    val adminEmail: StateFlow<String> = _adminEmail.asStateFlow()

    // Navigation & Screen selection
    private val _activeTab = MutableStateFlow(NavTab.DASHBOARD)
    val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount.asStateFlow()

    private val _isAddOrEditOpen = MutableStateFlow(false)
    val isAddOrEditOpen: StateFlow<Boolean> = _isAddOrEditOpen.asStateFlow()

    private val _accountToEdit = MutableStateFlow<Account?>(null)
    val accountToEdit: StateFlow<Account?> = _accountToEdit.asStateFlow()

    private val _selectedSeriesDetails = MutableStateFlow<String?>(null)
    val selectedSeriesDetails: StateFlow<String?> = _selectedSeriesDetails.asStateFlow()

    private val _editingPlatform = MutableStateFlow<PlatformItem?>(null)
    val editingPlatform: StateFlow<PlatformItem?> = _editingPlatform.asStateFlow()

    // Search, Filter, Sort
    val searchQuery = MutableStateFlow("")
    val seriesFilter = MutableStateFlow("All")
    val platformFilter = MutableStateFlow("All")
    val statusFilter = MutableStateFlow("All")
    val sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)

    // Data streams
    val allAccounts: StateFlow<List<Account>> = repository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlatforms: StateFlow<List<PlatformItem>> = repository.getAllPlatforms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mismatchedPlatforms: StateFlow<List<PlatformItem>> = repository.getMismatchedPlatforms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<ActivityLog>> = repository.getRecentLogs(40)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterCriteria(
        val query: String,
        val series: String,
        val platform: String,
        val status: String
    )

    private val filterCriteria = combine(
        searchQuery,
        seriesFilter,
        platformFilter,
        statusFilter
    ) { q, s, p, st -> FilterCriteria(q, s, p, st) }

    // Filtered accounts list
    val filteredAccounts: StateFlow<List<Account>> = combine(
        allAccounts,
        filterCriteria,
        sortOption
    ) { accounts, criteria, sort ->
        var list = accounts

        // Filter by Query (Name, Email, WhatsApp, Notes)
        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.fullName.lowercase().contains(q) ||
                        it.email.lowercase().contains(q) ||
                        it.whatsapp.contains(q) ||
                        it.notes.lowercase().contains(q) ||
                        it.platform.lowercase().contains(q)
            }
        }

        // Filter by Series
        if (criteria.series != "All") {
            list = list.filter { it.series.equals(criteria.series, ignoreCase = true) }
        }

        // Filter by Platform
        if (criteria.platform != "All") {
            list = list.filter { it.platform.equals(criteria.platform, ignoreCase = true) }
        }

        // Filter by Status
        if (criteria.status != "All") {
            list = list.filter { it.status.equals(criteria.status, ignoreCase = true) }
        }

        // Sort
        when (sort) {
            SortOption.NEWEST_FIRST -> list.sortedByDescending { it.registerDate }
            SortOption.OLDEST_FIRST -> list.sortedBy { it.registerDate }
            SortOption.NAME_AZ -> list.sortedBy { it.fullName.lowercase() }
            SortOption.POINTS_HIGH -> list.sortedByDescending { it.earnPoint }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics
    val dashboardStats: StateFlow<DashboardStats> = allAccounts.combine(mismatchedPlatforms) { accounts, _ ->
        val total = accounts.size
        val completed = accounts.count { it.status.equals("Grow", ignoreCase = true) }
        val pending = total - completed
        val points = accounts.sumOf { it.earnPoint }

        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayCount = accounts.count { it.registerDate >= startOfToday }

        // Find recently completed series
        val lastGrow = accounts.filter { it.status.equals("Grow", ignoreCase = true) }
            .maxByOrNull { it.registerDate }
        val recentSeries = lastGrow?.series ?: "11-Series"

        DashboardStats(
            totalAccounts = total,
            completedAccounts = completed,
            pendingAccounts = pending,
            totalEarnPoints = points,
            recentlyCompletedSeries = recentSeries,
            todayRegistrationsCount = todayCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Today's Registrations
    val todayAccounts: StateFlow<List<Account>> = allAccounts.combine(MutableStateFlow(Unit)) { accounts, _ ->
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        accounts.filter { it.registerDate >= startOfToday }.sortedByDescending { it.registerDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent Completed Accounts
    val recentCompletedAccounts: StateFlow<List<Account>> = allAccounts.combine(MutableStateFlow(Unit)) { accounts, _ ->
        accounts.filter { it.status.equals("Grow", ignoreCase = true) }
            .sortedByDescending { it.registerDate }
            .take(6)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setTab(tab: NavTab) {
        _activeTab.value = tab
        if (tab != NavTab.SERIES) {
            _selectedSeriesDetails.value = null
        }
        if (tab != NavTab.ACCOUNTS) {
            _selectedAccount.value = null
        }
    }

    fun openAccountDetails(account: Account) {
        _selectedAccount.value = account
    }

    fun closeAccountDetails() {
        _selectedAccount.value = null
    }

    fun openSeriesDetails(series: String) {
        _selectedSeriesDetails.value = series
        _activeTab.value = NavTab.SERIES
    }

    fun closeSeriesDetails() {
        _selectedSeriesDetails.value = null
    }

    fun openAddAccount(preselectedSeries: String? = null) {
        _accountToEdit.value = null
        _isAddOrEditOpen.value = true
    }

    fun openEditAccount(account: Account) {
        _accountToEdit.value = account
        _isAddOrEditOpen.value = true
    }

    fun closeAddOrEdit() {
        _isAddOrEditOpen.value = false
        _accountToEdit.value = null
    }

    fun setEditingPlatform(platform: PlatformItem?) {
        _editingPlatform.value = platform
    }

    fun saveAccount(account: Account, isNew: Boolean, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.saveAccount(account, isNew)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = repository.getAccountById(account.id)
            }
            closeAddOrEdit()
            onSaved()
            showToast(if (isNew) "Account created successfully (+${if (account.successfulGrow) 300 else 0} Pts)!" else "Account updated successfully!")
        }
    }

    fun toggleGrowStatus(account: Account) {
        viewModelScope.launch {
            val updated = repository.toggleAccountStatus(account)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = updated
            }
            val statusMsg = if (updated.successfulGrow) "Account verified as Grow (+300 Pts awarded)!" else "Account moved to Pending (0 Pts)."
            showToast(statusMsg)
        }
    }

    fun deleteAccount(account: Account, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAccount(account)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = null
            }
            onDeleted()
            showToast("Account deleted.")
        }
    }

    fun updateSignupUrl(platform: String, url: String) {
        viewModelScope.launch {
            repository.updateSignupUrl(platform, url)
            _editingPlatform.value = null
            showToast("Signup URL updated for $platform")
        }
    }

    fun importCsvText(csvText: String, onResult: (CsvImportResult) -> Unit) {
        viewModelScope.launch {
            val res = repository.importPlaCsv(csvText)
            onResult(res)
            showToast(res.message)
        }
    }

    fun exportAccountsToCsv(context: Context) {
        viewModelScope.launch {
            val list = allAccounts.value
            val csv = CsvManager.exportAccountsToCsv(list)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csv)
                putExtra(Intent.EXTRA_TITLE, "GTTC_Accounts_Export.csv")
                type = "text/csv"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export Accounts CSV")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Signup URL") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun openBrowser(context: Context, url: String) {
        if (url.isBlank()) return
        try {
            val safeUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open browser for URL: $url", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, number: String) {
        if (number.isBlank()) return
        try {
            val clean = number.replace("+", "").replace(" ", "").replace("-", "")
            val url = "https://api.whatsapp.com/send?phone=$clean"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun openEmail(context: Context, email: String) {
        if (email.isBlank()) return
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "GTTC Account Registration Update")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Email app", Toast.LENGTH_SHORT).show()
        }
    }

    fun loginAdmin(pinOrPassword: String): Boolean {
        // Master admin verification (accepts PIN 778899, master, or any non-empty password in demo)
        if (pinOrPassword.isNotBlank()) {
            _isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _activeTab.value = NavTab.DASHBOARD
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
