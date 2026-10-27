package com.example.data

import com.example.model.Account
import com.example.model.PlatformItem
import com.example.model.SeriesData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CsvImportResult(
    val totalProcessed: Int,
    val successful: List<PlatformItem>,
    val mismatchedOrDuplicate: List<PlatformItem>,
    val message: String
)

object CsvManager {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // Direct mapping for common shorthand tokens in pla.csv to canonical platforms
    private val SHORTHAND_MAP = mapOf(
        "1111" to "1111bet.bdt",
        "1333" to "1333bet.win",
        "1777" to "1777bet.com",
        "2222" to "2222bet.net",
        "2777" to "2777bet.co",
        "299" to "299bet.com",
        "3333" to "3333bet.ai",
        "399" to "399bet.com",
        "3999" to "3999bet.net",
        "39999" to "3999bet.net",
        "4777" to "4777bet.net",
        "499" to "499bet.org",
        "566" to "566bet.com",
        "5666" to "566bet.com",
        "577" to "577bet.win",
        "5777" to "577bet.win",
        "6777" to "6777bet.net",
        "7000" to "7000bet.ai",
        "7555" to "7555bet.org",
        "786" to "786bet.win",
        "8777" to "8777bet.co",
        "8999" to "8999bet.tv",
        "9777" to "9777bet.win",
        "bk66" to "Bk666.win",
        "bk666" to "Bk666.win",
        "bk99" to "Bk999.com",
        "bk999" to "Bk999.com",
        "k7777" to "K7777bet.co",
        "qq777" to "Qq777bet.ai"
    )

    fun resolvePlatform(rawToken: String): String? {
        val clean = rawToken.trim().lowercase()
        // Check shorthand map
        SHORTHAND_MAP[clean]?.let { return it }

        // Check exact match in all platforms
        for (p in SeriesData.ALL_INITIAL_PLATFORMS_MAP.keys) {
            if (p.equals(clean, ignoreCase = true) || clean.startsWith(p.substringBefore(".").lowercase())) {
                return p
            }
        }
        return null
    }

    fun parsePlaCsv(csvContent: String): CsvImportResult {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) {
            return CsvImportResult(0, emptyList(), emptyList(), "CSV content is empty.")
        }

        val successfulList = mutableListOf<PlatformItem>()
        val reviewList = mutableListOf<PlatformItem>()
        val urlToPlatformMap = mutableMapOf<String, String>()
        val assignedPlatforms = mutableSetOf<String>()

        for (line in lines) {
            val lower = line.lowercase()
            if (lower.startsWith("series,") || lower.startsWith("platform,") || lower.startsWith("token,")) {
                continue
            }

            val delimiter = when {
                line.contains(",") -> ","
                line.contains("\t") -> "\t"
                line.contains(";") -> ";"
                else -> ","
            }
            val tokens = line.split(delimiter).map { it.trim().trim('"', '\'') }
            if (tokens.isEmpty()) continue

            val rawIdentifier = tokens[0]
            val rawUrl = if (tokens.size >= 2) tokens[1] else ""

            val resolvedPlatform = resolvePlatform(rawIdentifier)

            if (resolvedPlatform == null) {
                // Unknown / Mismatched platform
                reviewList.add(
                    PlatformItem(
                        platform = rawIdentifier,
                        series = "Unknown Series",
                        signupUrl = rawUrl,
                        rewardPoint = 300,
                        isMismatchedOrDuplicate = true,
                        reviewNote = "Platform '$rawIdentifier' does not belong to any of the 8 official series."
                    )
                )
            } else {
                val series = SeriesData.getSeriesForPlatform(resolvedPlatform)
                val cleanUrl = rawUrl.trim()
                val normalizedUrl = cleanUrl.lowercase().trimEnd('/')

                // Check for duplicate URL
                val existingOwner = urlToPlatformMap[normalizedUrl]
                val isAlreadyAssigned = assignedPlatforms.contains(resolvedPlatform.lowercase())

                if (existingOwner != null && existingOwner != resolvedPlatform) {
                    reviewList.add(
                        PlatformItem(
                            platform = resolvedPlatform,
                            series = series,
                            signupUrl = cleanUrl,
                            rewardPoint = 300,
                            isMismatchedOrDuplicate = true,
                            reviewNote = "Duplicate URL detected: exactly identical to URL assigned to $existingOwner."
                        )
                    )
                } else if (isAlreadyAssigned) {
                    reviewList.add(
                        PlatformItem(
                            platform = resolvedPlatform,
                            series = series,
                            signupUrl = cleanUrl,
                            rewardPoint = 300,
                            isMismatchedOrDuplicate = true,
                            reviewNote = "Duplicate entry for platform '$resolvedPlatform'."
                        )
                    )
                } else {
                    urlToPlatformMap[normalizedUrl] = resolvedPlatform
                    assignedPlatforms.add(resolvedPlatform.lowercase())
                    successfulList.add(
                        PlatformItem(
                            platform = resolvedPlatform,
                            series = series,
                            signupUrl = cleanUrl,
                            rewardPoint = 300,
                            isMismatchedOrDuplicate = false,
                            reviewNote = ""
                        )
                    )
                }
            }
        }

        return CsvImportResult(
            totalProcessed = lines.size,
            successful = successfulList,
            mismatchedOrDuplicate = reviewList,
            message = "Processed ${lines.size} entries: ${successfulList.size} mapped successfully, ${reviewList.size} highlighted for review."
        )
    }

    fun exportAccountsToCsv(accounts: List<Account>): String {
        val sb = StringBuilder()
        sb.append("ID,Full Name,WhatsApp,Email,Series,Platform,Maintain Series,Status,Earn Point,Register Date,Notes\n")
        for (a in accounts) {
            val dateStr = dateFormat.format(Date(a.registerDate))
            sb.append("${a.id},${escapeCsv(a.fullName)},${escapeCsv(a.whatsapp)},${escapeCsv(a.email)},${escapeCsv(a.series)},${escapeCsv(a.platform)},${escapeCsv(a.maintainSeries)},${a.status},${a.earnPoint},$dateStr,${escapeCsv(a.notes)}\n")
        }
        return sb.toString()
    }

    fun generateSamplePlaCsv(): String {
        return """
1111,http://www.1111bet23.com/?r=kyk4462
1333,http://www.1333bet88.com/?r=ivi8750
1777,http://www.1777bet16.com/?r=wbs5693
2222,http://www.2222bet18.com/?r=iiw2957
2777,http://www.2777bet77.com/?r=gbj1789
299,https://www.299bet13.com/?r=eke5033
3333,http://www.3999bet9.com/?r=ify9780
399,http://www.399bet16.com/?r=bkk1605
39999,http://www.3999bet9.com/?r=ify9780
4777,http://www.4777bet88.com/?r=hew3230
499,http://www.499bet14.com/?r=vzo5636
5777,http://www.5777bet66.com/?r=efb9166
5666,http://www.5666bet8.com/?r=zza9953
6777,http://www.6777bet44.com/?r=yus0376
786,http://www.786bet9.com/?r=jyf8630
7000,http://www.7000bet55.com/?r=waz6735
7555,http://www.7555bet7.com/?r=sea2129
8777,http://www.8777bet88.com/?r=bsm7149
8999,http://www.8999bet2.com/?r=xvt3600
9777,http://www.9777bet88.com/?r=crd8803
Bk66,http://www.bk66630.com/?r=tok6294
Bk99,http://www.bk9929.com/?r=umg1075
K7777,http://www.k77775.com/?r=zje8913
Qq777,http://www.qq77728.com/?r=kfv2812
Xx999,http://www.xx9990.com/?r=xth2280
        """.trimIndent()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
