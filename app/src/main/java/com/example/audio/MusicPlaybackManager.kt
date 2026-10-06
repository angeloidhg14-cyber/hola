package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RepeatMode {
    OFF, ALL, ONE
}

data class PlaybackState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val sleepTimerMinutesLeft: Int? = null,
    val waveformFrequencies: List<Float> = List(24) { 0.2f }
)

class MusicPlaybackManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var toneGenerator: ToneGenerator? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val queue = mutableListOf<Track>()
    private var currentQueueIndex = 0

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
        } catch (_: Exception) {
            // Safe fallback if audio hardware is restricted
        }
    }

    fun setQueue(tracks: List<Track>, startIndex: Int = 0) {
        queue.clear()
        queue.addAll(tracks)
        currentQueueIndex = startIndex.coerceIn(0, (tracks.size - 1).coerceAtLeast(0))
        if (queue.isNotEmpty()) {
            val track = queue[currentQueueIndex]
            _playbackState.update {
                it.copy(
                    currentTrack = track,
                    currentPositionMs = 0L,
                    durationMs = track.durationMs
                )
            }
        }
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        if (newQueue != null) {
            queue.clear()
            queue.addAll(newQueue)
        }
        val foundIndex = queue.indexOfFirst { it.id == track.id }
        if (foundIndex != -1) {
            currentQueueIndex = foundIndex
        } else {
            queue.add(0, track)
            currentQueueIndex = 0
        }

        _playbackState.update {
            it.copy(
                currentTrack = track,
                isPlaying = true,
                currentPositionMs = 0L,
                durationMs = track.durationMs
            )
        }
        playSoftTone()
        startProgressTicker()
    }

    fun togglePlayPause() {
        val currentState = _playbackState.value
        if (currentState.currentTrack == null && queue.isNotEmpty()) {
            playTrack(queue[currentQueueIndex])
            return
        }

        val newPlaying = !currentState.isPlaying
        _playbackState.update { it.copy(isPlaying = newPlaying) }

        if (newPlaying) {
            playSoftTone()
            startProgressTicker()
        } else {
            stopProgressTicker()
        }
    }

    fun seekTo(positionMs: Long) {
        val duration = _playbackState.value.durationMs
        val clamped = positionMs.coerceIn(0L, duration.coerceAtLeast(1L))
        _playbackState.update { it.copy(currentPositionMs = clamped) }
    }

    fun skipNext() {
        if (queue.isEmpty()) return
        if (_playbackState.value.isShuffle) {
            currentQueueIndex = queue.indices.random()
        } else {
            currentQueueIndex = (currentQueueIndex + 1) % queue.size
        }
        playTrack(queue[currentQueueIndex])
    }

    fun skipPrevious() {
        if (queue.isEmpty()) return
        if (_playbackState.value.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }
        currentQueueIndex = if (currentQueueIndex - 1 < 0) queue.size - 1 else currentQueueIndex - 1
        playTrack(queue[currentQueueIndex])
    }

    fun toggleShuffle() {
        _playbackState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun cycleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.update { it.copy(repeatMode = nextMode) }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackState.update { it.copy(playbackSpeed = speed) }
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        _playbackState.update { it.copy(sleepTimerMinutesLeft = minutes) }

        if (minutes != null && minutes > 0) {
            sleepTimerJob = scope.launch {
                var remaining = minutes
                while (remaining > 0 && isActive) {
                    delay(60_000L)
                    remaining -= 1
                    _playbackState.update { it.copy(sleepTimerMinutesLeft = remaining) }
                }
                // Stop playback when timer finishes
                _playbackState.update { it.copy(isPlaying = false, sleepTimerMinutesLeft = null) }
                stopProgressTicker()
            }
        }
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _playbackState.value.isPlaying) {
                val stepMs = (100L * _playbackState.value.playbackSpeed).toLong()
                delay(100L)

                val state = _playbackState.value
                val newPos = state.currentPositionMs + stepMs

                if (newPos >= state.durationMs && state.durationMs > 0) {
                    when (state.repeatMode) {
                        RepeatMode.ONE -> {
                            _playbackState.update { it.copy(currentPositionMs = 0L) }
                        }
                        RepeatMode.ALL -> {
                            skipNext()
                        }
                        RepeatMode.OFF -> {
                            if (currentQueueIndex < queue.size - 1) {
                                skipNext()
                            } else {
                                _playbackState.update { it.copy(isPlaying = false, currentPositionMs = state.durationMs) }
                                stopProgressTicker()
                            }
                        }
                    }
                } else {
                    // Generate dynamic audio visualizer waveform amplitudes
                    val visualizer = List(24) { i ->
                        val base = (Math.sin((newPos / 150.0) + i) * 0.4 + 0.5).toFloat()
                        base.coerceIn(0.1f, 1.0f)
                    }
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = newPos,
                            waveformFrequencies = visualizer
                        )
                    }
                }
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun playSoftTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) {}
    }
}
