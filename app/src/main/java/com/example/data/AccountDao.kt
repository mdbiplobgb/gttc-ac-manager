package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY registerDate DESC")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): Account?

    @Query("SELECT * FROM accounts WHERE series = :series ORDER BY registerDate DESC")
    fun getAccountsBySeries(series: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE platform = :platform ORDER BY registerDate DESC")
    fun getAccountsByPlatform(platform: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE status = :status ORDER BY registerDate DESC")
    fun getAccountsByStatus(status: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE fullName LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR whatsapp LIKE '%' || :query || '%' ORDER BY registerDate DESC")
    fun searchAccounts(query: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE LOWER(fullName) = LOWER(:fullName) OR (email != '' AND LOWER(email) = LOWER(:email)) OR (whatsapp != '' AND whatsapp = :whatsapp) LIMIT 1")
    suspend fun findDuplicate(fullName: String, email: String, whatsapp: String): Account?

    @Query("SELECT COUNT(*) FROM accounts")
    fun getTotalAccountsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'Grow'")
    fun getGrowAccountsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'Pending'")
    fun getPendingAccountsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(earnPoint), 0) FROM accounts")
    fun getTotalEarnPoints(): Flow<Int>

    @Query("SELECT * FROM accounts WHERE registerDate >= :startOfDayTimestamp ORDER BY registerDate DESC")
    fun getTodayAccounts(startOfDayTimestamp: Long): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE status = 'Grow' ORDER BY registerDate DESC LIMIT :limit")
    fun getRecentGrowAccounts(limit: Int = 5): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<Account>)

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)

    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts()
}
