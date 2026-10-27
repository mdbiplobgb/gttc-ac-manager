package com.example.model

import androidx.compose.ui.graphics.Color

data class SeriesDefinition(
    val name: String,
    val platforms: List<String>,
    val primaryColor: Color,
    val gradientColors: List<Color>,
    val badgeColor: Color
)

object SeriesData {
    val SERIES_LIST = listOf(
        SeriesDefinition(
            name = "11-Series",
            platforms = listOf("1111bet.bdt", "1333bet.win", "1777bet.com"),
            primaryColor = Color(0xFF1E40AF),
            gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)),
            badgeColor = Color(0xFFDBEAFE)
        ),
        SeriesDefinition(
            name = "22-Series",
            platforms = listOf("2222bet.net", "2777bet.co", "299bet.com"),
            primaryColor = Color(0xFF0369A1),
            gradientColors = listOf(Color(0xFF075985), Color(0xFF0284C7)),
            badgeColor = Color(0xFFE0F2FE)
        ),
        SeriesDefinition(
            name = "33-Series",
            platforms = listOf("3333bet.ai", "399bet.com", "3999bet.net"),
            primaryColor = Color(0xFF0F766E),
            gradientColors = listOf(Color(0xFF115E59), Color(0xFF0D9488)),
            badgeColor = Color(0xFFCCFBF1)
        ),
        SeriesDefinition(
            name = "47-Series",
            platforms = listOf("4777bet.net", "499bet.org", "566bet.com"),
            primaryColor = Color(0xFF4338CA),
            gradientColors = listOf(Color(0xFF3730A3), Color(0xFF4F46E5)),
            badgeColor = Color(0xFFE0E7FF)
        ),
        SeriesDefinition(
            name = "57-Series",
            platforms = listOf("577bet.win", "6777bet.net", "7000bet.ai"),
            primaryColor = Color(0xFF6D28D9),
            gradientColors = listOf(Color(0xFF5B21B6), Color(0xFF7C3AED)),
            badgeColor = Color(0xFFEDE9FE)
        ),
        SeriesDefinition(
            name = "75-Series",
            platforms = listOf("7555bet.org", "786bet.win", "8777bet.co"),
            primaryColor = Color(0xFF0284C7),
            gradientColors = listOf(Color(0xFF0369A1), Color(0xFF38BDF8)),
            badgeColor = Color(0xFFE0F2FE)
        ),
        SeriesDefinition(
            name = "89-Series",
            platforms = listOf("8999bet.tv", "9777bet.win", "Bk666.win"),
            primaryColor = Color(0xFF1D4ED8),
            gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)),
            badgeColor = Color(0xFFDBEAFE)
        ),
        SeriesDefinition(
            name = "Bk-Series",
            platforms = listOf("Bk999.com", "K7777bet.co", "Qq777bet.ai"),
            primaryColor = Color(0xFF0F172A),
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF334155)),
            badgeColor = Color(0xFFF1F5F9)
        )
    )

    fun getSeriesNames(): List<String> = SERIES_LIST.map { it.name }

    fun getPlatformsForSeries(series: String): List<String> {
        return SERIES_LIST.find { it.name.equals(series, ignoreCase = true) }?.platforms
            ?: emptyList()
    }

    fun getSeriesForPlatform(platform: String): String {
        return SERIES_LIST.find { seriesDef ->
            seriesDef.platforms.any { it.equals(platform, ignoreCase = true) }
        }?.name ?: "11-Series"
    }

    fun getDefaultSignupUrl(platform: String): String {
        val clean = platform.lowercase().trim()
        return "https://$clean/register?ref=GTTC_OFFICIAL&source=ac_manager"
    }

    val ALL_INITIAL_PLATFORMS_MAP: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        for (series in SERIES_LIST) {
            for (p in series.platforms) {
                map[p] = series.name
            }
        }
        map
    }

    fun getAllInitialPlatforms(): List<PlatformItem> {
        val list = mutableListOf<PlatformItem>()
        for (series in SERIES_LIST) {
            for (platform in series.platforms) {
                list.add(
                    PlatformItem(
                        platform = platform,
                        series = series.name,
                        signupUrl = getDefaultSignupUrl(platform),
                        rewardPoint = 300,
                        isMismatchedOrDuplicate = false
                    )
                )
            }
        }
        return list
    }
}
