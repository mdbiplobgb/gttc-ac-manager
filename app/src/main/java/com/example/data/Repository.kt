package com.example.data

import android.content.Context
import com.example.model.Account
import com.example.model.ActivityLog
import com.example.model.PlatformItem
import com.example.model.SeriesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class AppRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val accountDao = database.accountDao()
    private val platformDao = database.platformDao()
    private val activityLogDao = database.activityLogDao()

    // Accounts
    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()

    fun getAccountsBySeries(series: String): Flow<List<Account>> =
        accountDao.getAccountsBySeries(series)

    fun getAccountsByStatus(status: String): Flow<List<Account>> =
        accountDao.getAccountsByStatus(status)

    fun searchAccounts(query: String): Flow<List<Account>> =
        accountDao.searchAccounts(query)

    suspend fun getAccountById(id: String): Account? = accountDao.getAccountById(id)

    suspend fun checkDuplicateAccount(fullName: String, email: String, whatsapp: String): Account? {
        return accountDao.findDuplicate(fullName.trim(), email.trim(), whatsapp.trim())
    }

    suspend fun saveAccount(account: Account, isNew: Boolean): Result<Unit> {
        return try {
            val processedAccount = if (account.successfulGrow) {
                account.copy(earnPoint = 300, status = "Grow")
            } else {
                account.copy(earnPoint = 0, status = "Pending")
            }
            if (isNew) {
                accountDao.insertAccount(processedAccount)
                activityLogDao.insertLog(
                    ActivityLog(
                        accountId = processedAccount.id,
                        accountName = processedAccount.fullName,
                        actionType = "CREATED",
                        description = "Created account for ${processedAccount.fullName} in ${processedAccount.series} (${processedAccount.platform}) - Status: ${processedAccount.status} (${processedAccount.earnPoint} pts)"
                    )
                )
            } else {
                accountDao.updateAccount(processedAccount)
                activityLogDao.insertLog(
                    ActivityLog(
                        accountId = processedAccount.id,
                        accountName = processedAccount.fullName,
                        actionType = "UPDATED",
                        description = "Updated account details for ${processedAccount.fullName} - Status: ${processedAccount.status} (${processedAccount.earnPoint} pts)"
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleAccountStatus(account: Account): Account {
        val newGrow = !account.successfulGrow
        val updated = account.copy(
            successfulGrow = newGrow,
            status = if (newGrow) "Grow" else "Pending",
            earnPoint = if (newGrow) 300 else 0
        )
        accountDao.updateAccount(updated)
        activityLogDao.insertLog(
            ActivityLog(
                accountId = updated.id,
                accountName = updated.fullName,
                actionType = "STATUS_CHANGED",
                description = "Changed status of ${updated.fullName} to ${updated.status} (${updated.earnPoint} pts)"
            )
        )
        return updated
    }

    suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account)
        activityLogDao.insertLog(
            ActivityLog(
                accountId = account.id,
                accountName = account.fullName,
                actionType = "DELETED",
                description = "Deleted account for ${account.fullName}"
            )
        )
    }

    // Platforms
    fun getAllPlatforms(): Flow<List<PlatformItem>> = platformDao.getAllPlatforms()

    fun getPlatformsBySeries(series: String): Flow<List<PlatformItem>> =
        platformDao.getPlatformsBySeries(series)

    fun getMismatchedPlatforms(): Flow<List<PlatformItem>> =
        platformDao.getMismatchedPlatforms()

    suspend fun updateSignupUrl(platform: String, url: String) {
        platformDao.updateSignupUrl(platform, url)
        activityLogDao.insertLog(
            ActivityLog(
                actionType = "URL_EDITED",
                description = "Updated signup URL for platform $platform: $url"
            )
        )
    }

    suspend fun importPlaCsv(csvText: String): CsvImportResult {
        val result = CsvManager.parsePlaCsv(csvText)
        if (result.successful.isNotEmpty()) {
            platformDao.insertPlatforms(result.successful)
        }
        if (result.mismatchedOrDuplicate.isNotEmpty()) {
            platformDao.insertPlatforms(result.mismatchedOrDuplicate)
        }
        activityLogDao.insertLog(
            ActivityLog(
                actionType = "CSV_IMPORTED",
                description = "Imported pla.csv: ${result.successful.size} active, ${result.mismatchedOrDuplicate.size} marked for review."
            )
        )
        return result
    }

    // Logs & Stats
    fun getRecentLogs(limit: Int = 30): Flow<List<ActivityLog>> = activityLogDao.getRecentLogs(limit)

    fun getTotalAccountsCount(): Flow<Int> = accountDao.getTotalAccountsCount()
    fun getGrowAccountsCount(): Flow<Int> = accountDao.getGrowAccountsCount()
    fun getPendingAccountsCount(): Flow<Int> = accountDao.getPendingAccountsCount()
    fun getTotalEarnPoints(): Flow<Int> = accountDao.getTotalEarnPoints()
    fun getTodayAccounts(startOfDay: Long): Flow<List<Account>> = accountDao.getTodayAccounts(startOfDay)
    fun getRecentGrowAccounts(limit: Int = 5): Flow<List<Account>> = accountDao.getRecentGrowAccounts(limit)

    // Backup & Restore
    suspend fun exportBackupJson(): String {
        val accounts = accountDao.getAllAccounts().first()
        val platforms = platformDao.getAllPlatforms().first()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportTime", System.currentTimeMillis())

        val accountsArray = JSONArray()
        for (a in accounts) {
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("fullName", a.fullName)
            obj.put("profileImage", a.profileImage)
            obj.put("whatsapp", a.whatsapp)
            obj.put("email", a.email)
            obj.put("series", a.series)
            obj.put("platform", a.platform)
            obj.put("maintainSeries", a.maintainSeries)
            obj.put("successfulGrow", a.successfulGrow)
            obj.put("earnPoint", a.earnPoint)
            obj.put("registerDate", a.registerDate)
            obj.put("status", a.status)
            obj.put("notes", a.notes)
            accountsArray.put(obj)
        }
        root.put("accounts", accountsArray)

        val platformsArray = JSONArray()
        for (p in platforms) {
            val obj = JSONObject()
            obj.put("platform", p.platform)
            obj.put("series", p.series)
            obj.put("signupUrl", p.signupUrl)
            obj.put("rewardPoint", p.rewardPoint)
            obj.put("isMismatchedOrDuplicate", p.isMismatchedOrDuplicate)
            obj.put("reviewNote", p.reviewNote)
            platformsArray.put(obj)
        }
        root.put("platforms", platformsArray)

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val accountsArray = root.optJSONArray("accounts")
            if (accountsArray != null) {
                val accountsList = mutableListOf<Account>()
                for (i in 0 until accountsArray.length()) {
                    val obj = accountsArray.getJSONObject(i)
                    accountsList.add(
                        Account(
                            id = obj.getString("id"),
                            fullName = obj.getString("fullName"),
                            profileImage = obj.optString("profileImage", ""),
                            whatsapp = obj.optString("whatsapp", ""),
                            email = obj.optString("email", ""),
                            series = obj.getString("series"),
                            platform = obj.getString("platform"),
                            maintainSeries = obj.optString("maintainSeries", ""),
                            successfulGrow = obj.optBoolean("successfulGrow", false),
                            earnPoint = obj.optInt("earnPoint", 0),
                            registerDate = obj.optLong("registerDate", System.currentTimeMillis()),
                            status = obj.optString("status", "Pending"),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                accountDao.insertAccounts(accountsList)
            }

            val platformsArray = root.optJSONArray("platforms")
            if (platformsArray != null) {
                val platformsList = mutableListOf<PlatformItem>()
                for (i in 0 until platformsArray.length()) {
                    val obj = platformsArray.getJSONObject(i)
                    platformsList.add(
                        PlatformItem(
                            platform = obj.getString("platform"),
                            series = obj.getString("series"),
                            signupUrl = obj.optString("signupUrl", ""),
                            rewardPoint = obj.optInt("rewardPoint", 300),
                            isMismatchedOrDuplicate = obj.optBoolean("isMismatchedOrDuplicate", false),
                            reviewNote = obj.optString("reviewNote", "")
                        )
                    )
                }
                platformDao.insertPlatforms(platformsList)
            }

            activityLogDao.insertLog(
                ActivityLog(
                    actionType = "RESTORE",
                    description = "Restored database from JSON backup."
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
