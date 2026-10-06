package com.example.model

data class SyncedDevice(
    val id: String,
    val name: String,
    val deviceType: String, // "Teléfono Principal", "Tablet Pro", "Smart TV Lark Cast", "Web Player"
    val isCurrentDevice: Boolean,
    val lastSyncAgo: String,
    val isOnline: Boolean
)

data class CloudSyncState(
    val isAutoSyncEnabled: Boolean = true,
    val isEndToEndEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM + XChaCha20",
    val statusText: String = "Sincronizado en tiempo real",
    val totalCloudVaultSizeMb: Double = 1420.5,
    val totalTracksInCloud: Int = 184,
    val offlineHighResTracksCount: Int = 42,
    val devices: List<SyncedDevice> = listOf(
        SyncedDevice("dev-1", "Este Dispositivo (Android)", "Teléfono Principal", true, "Ahora mismo", true),
        SyncedDevice("dev-2", "Galaxy Tab S9 Ultra", "Tablet", false, "Hace 2 min", true),
        SyncedDevice("dev-3", "Lark Web Player (Chrome Mac)", "Escritorio", false, "Hace 15 min", true),
        SyncedDevice("dev-4", "Sony Bravia 4K TV (Cast)", "Smart TV", false, "Ayer", false)
    )
)
