package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Account
import com.example.model.ActivityLog
import com.example.model.PlatformItem
import com.example.model.SeriesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [Account::class, PlatformItem::class, ActivityLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun platformDao(): PlatformDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gttc_ac_manager.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialData(database, context.applicationContext)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase, context: Context? = null) {
            // First load default platforms
            val defaultPlatforms = SeriesData.getAllInitialPlatforms()
            database.platformDao().insertPlatforms(defaultPlatforms)

            // Try reading bundled pla.csv
            val plaCsvText = try {
                context?.assets?.open("pla.csv")?.bufferedReader()?.use { it.readText() }
            } catch (e: Exception) {
                null
            } ?: CsvManager.generateSamplePlaCsv()

            val importResult = CsvManager.parsePlaCsv(plaCsvText)
            if (importResult.successful.isNotEmpty()) {
                database.platformDao().insertPlatforms(importResult.successful)
            }
            if (importResult.mismatchedOrDuplicate.isNotEmpty()) {
                database.platformDao().insertPlatforms(importResult.mismatchedOrDuplicate)
            }

            // Seed initial realistic accounts
            val now = System.currentTimeMillis()
            val oneDay = 86400000L
            val sampleAccounts = listOf(
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Tareq Hasan",
                    profileImage = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801712345678",
                    email = "tareq.hasan@gmail.com",
                    series = "11-Series",
                    platform = "1111bet.bdt",
                    maintainSeries = "Primary Team A",
                    successfulGrow = true,
                    earnPoint = 300,
                    registerDate = now - (oneDay * 0),
                    status = "Grow",
                    notes = "Completed KYC and deposited. VIP member."
                ),
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Shakil Ahmed",
                    profileImage = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801819876543",
                    email = "shakil.ahmed99@yahoo.com",
                    series = "11-Series",
                    platform = "1333bet.win",
                    maintainSeries = "Special Unit",
                    successfulGrow = true,
                    earnPoint = 300,
                    registerDate = now - (oneDay * 1),
                    status = "Grow",
                    notes = "Verified referral signup via WhatsApp link."
                ),
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Nafis Mahmud",
                    profileImage = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801911223344",
                    email = "nafis.m@gmail.com",
                    series = "22-Series",
                    platform = "2222bet.net",
                    maintainSeries = "Group 2",
                    successfulGrow = false,
                    earnPoint = 0,
                    registerDate = now - (oneDay * 0),
                    status = "Pending",
                    notes = "Signup registered, awaiting first verification step."
                ),
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Arif Chowdhury",
                    profileImage = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801622334455",
                    email = "arif.chowdhury@hotmail.com",
                    series = "33-Series",
                    platform = "3333bet.ai",
                    maintainSeries = "Fast Track",
                    successfulGrow = true,
                    earnPoint = 300,
                    registerDate = now - (oneDay * 2),
                    status = "Grow",
                    notes = "Successfully passed tier 1 verification."
                ),
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Farhana Islam",
                    profileImage = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801733445566",
                    email = "farhana.i@outlook.com",
                    series = "47-Series",
                    platform = "4777bet.net",
                    maintainSeries = "Group 4",
                    successfulGrow = false,
                    earnPoint = 0,
                    registerDate = now - (oneDay * 3),
                    status = "Pending",
                    notes = "Document submitted, under review."
                ),
                Account(
                    id = UUID.randomUUID().toString(),
                    fullName = "Kamrul Islam",
                    profileImage = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop&q=80",
                    whatsapp = "+8801844556677",
                    email = "kamrul.is@gmail.com",
                    series = "Bk-Series",
                    platform = "Bk999.com",
                    maintainSeries = "Elite Circle",
                    successfulGrow = true,
                    earnPoint = 300,
                    registerDate = now - (oneDay * 1),
                    status = "Grow",
                    notes = "Completed fast-track onboarding with maximum reward."
                )
            )
            database.accountDao().insertAccounts(sampleAccounts)

            // Seed initial activity log
            database.activityLogDao().insertLog(
                ActivityLog(
                    actionType = "SYSTEM_INIT",
                    description = "Gttc-Ac Manager database initialized with 24 platforms across 8 series and initial records."
                )
            )
        }
    }
}
