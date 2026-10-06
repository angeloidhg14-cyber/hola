package com.example.model

enum class AudioFormatType(
    val label: String,
    val sampleRate: String,
    val bitDepth: String,
    val isHiRes: Boolean
) {
    FLAC_HI_RES("FLAC Hi-Res", "96.0 kHz", "24-bit", true),
    WAV_STUDIO("WAV Master", "192.0 kHz", "32-bit float", true),
    DOLBY_ATMOS("Dolby Atmos Spatial", "48.0 kHz", "Spatial 3D", true),
    MP3_320K("MP3 HD", "44.1 kHz", "320 kbps", false),
    AAC_STREAM("AAC Pro", "48.0 kHz", "256 kbps", false)
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverDrawableRes: Int,
    val format: AudioFormatType = AudioFormatType.FLAC_HI_RES,
    val folderPath: String = "/Almacenamiento/Música/Hi-Res",
    val smartTags: List<String> = listOf("#Favoritos", "#HiRes"),
    val lyrics: List<LyricLine> = emptyList(),
    val isFavorite: Boolean = false,
    val isOfflineAvailable: Boolean = true,
    val isCloudSynced: Boolean = true,
    val fileSizeMb: Double = 34.2,
    val year: Int = 2026,
    val genre: String = "Electronic / Synthwave"
)
