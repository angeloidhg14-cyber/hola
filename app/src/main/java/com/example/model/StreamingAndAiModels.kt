package com.example.model

data class AiRecommendation(
    val id: String,
    val title: String,
    val promptUsed: String,
    val vibeTag: String,
    val matchScorePercent: Int,
    val recommendedTrackIds: List<String>,
    val explanation: String
)

data class StreamingChannel(
    val id: String,
    val name: String,
    val category: String, // "Trending Global", "Lofi Chill Radio", "Top Latin 2026", "Synthwave 24/7"
    val listenersCount: String,
    val bitRate: String,
    val coverDrawableRes: Int,
    val sampleTrack: Track
)

data class OpenApiConfig(
    val apiKey: String = "lark_live_sk_9a8f2c7104b2e88a3",
    val webhookUrl: String = "https://api.larkmusic.io/v1/sync/events",
    val isEnabled: Boolean = true,
    val rateLimitRequestsPerMin: Int = 1200,
    val registeredThirdPartyApps: List<String> = listOf("Lark Desktop Companion", "Home Assistant Music", "Wear OS Lark Sync")
)
