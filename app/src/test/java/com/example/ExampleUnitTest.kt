package com.example

import com.example.data.MusicRepository
import com.example.model.EqualizerState
import com.example.model.LarkThemeMode
import com.example.ui.components.formatTime
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun tracks_areLoadedWithLyricsAndHiResFormats() {
        val tracks = MusicRepository.SAMPLE_TRACKS
        assertTrue("Tracks should not be empty", tracks.isNotEmpty())

        val cyberpunkTrack = tracks.first { it.id == "t1" }
        assertEquals("Cyberpunk Horizon", cyberpunkTrack.title)
        assertTrue("Track must have synced lyrics", cyberpunkTrack.lyrics.isNotEmpty())
        assertTrue("Should support FLAC Hi-Res", cyberpunkTrack.format.isHiRes)
    }

    @Test
    fun equalizerPresets_areProperlyConfigured() {
        val presets = EqualizerState.PRESETS
        assertTrue(presets.containsKey("Lark Signature"))
        assertTrue(presets.containsKey("Bass Booster"))
        assertTrue(presets.containsKey("Rock"))
        assertTrue(presets.containsKey("Electronic"))

        val bassBoosterBands = presets["Bass Booster"]!!
        assertEquals(5, bassBoosterBands.size)
        assertTrue("Bass band should be boosted", bassBoosterBands[0] > 5.0f)
    }

    @Test
    fun formatTime_correctlyFormatsMilliseconds() {
        assertEquals("00:00", formatTime(0L))
        assertEquals("01:05", formatTime(65_000L))
        assertEquals("03:34", formatTime(214_000L))
    }

    @Test
    fun themeModes_containAllLarkStyles() {
        val themes = LarkThemeMode.values()
        assertEquals(5, themes.size)
        assertTrue(themes.contains(LarkThemeMode.LARK_CYAN))
        assertTrue(themes.contains(LarkThemeMode.CYBERPUNK))
        assertTrue(themes.contains(LarkThemeMode.AMOLED_BLACK))
    }
}

