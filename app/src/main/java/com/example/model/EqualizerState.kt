package com.example.model

data class EqualizerState(
    val isEnabled: Boolean = true,
    val selectedPreset: String = "Lark Signature",
    val bandLevels: List<Float> = listOf(4.5f, 2.0f, 0.5f, 3.0f, 5.0f), // dB [-12 .. +12]
    val bandLabels: List<String> = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz"),
    val bassBoost: Float = 68f, // 0..100
    val virtualizer3D: Float = 55f, // 0..100
    val loudnessGain: Float = 40f, // 0..100 (Volume booster up to 200%)
    val reverbEffect: String = "Concierto en Vivo", // Estudio, Salón, Concierto, etc.
    val karaokeVocalCut: Boolean = false // Vocal remover / Karaoke guide
) {
    companion object {
        val PRESETS: Map<String, List<Float>> = mapOf(
            "Lark Signature" to listOf(4.5f, 2.0f, 0.5f, 3.0f, 5.0f),
            "Bass Booster" to listOf(9.0f, 6.0f, 1.0f, 0.0f, -1.0f),
            "Pop" to listOf(2.0f, 4.0f, 6.0f, 3.0f, 1.0f),
            "Rock" to listOf(5.0f, 3.0f, -1.0f, 4.0f, 6.0f),
            "Electronic" to listOf(6.5f, 3.5f, 0.0f, 4.5f, 5.5f),
            "Vocal Boost" to listOf(-2.0f, 1.0f, 6.0f, 5.0f, 2.0f),
            "Jazz" to listOf(3.0f, 1.5f, 2.0f, 2.5f, 4.0f),
            "Heavy Metal" to listOf(7.0f, 2.0f, -2.0f, 5.0f, 7.5f),
            "Acústico" to listOf(3.5f, 3.0f, 2.0f, 3.5f, 4.0f),
            "Plano" to listOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        )

        val REVERB_OPTIONS = listOf("Desactivado", "Estudio Acústico", "Salón Grande", "Concierto en Vivo", "Catedral Espacial")
    }
}
