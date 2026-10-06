package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MusicPlaybackManager
import com.example.audio.PlaybackState
import com.example.audio.RepeatMode
import com.example.data.MusicRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiNotification(
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class LarkMusicViewModel(application: Application) : AndroidViewModel(application) {

    val playbackManager = MusicPlaybackManager(application.applicationContext)
    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    // Library & Playlists
    private val _tracks = MutableStateFlow(MusicRepository.SAMPLE_TRACKS)
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _playlists = MutableStateFlow(MusicRepository.INITIAL_PLAYLISTS)
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    // Filtering & Tags
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSmartTag = MutableStateFlow("Todos")
    val selectedSmartTag: StateFlow<String> = _selectedSmartTag.asStateFlow()

    private val _libraryTab = MutableStateFlow(0) // 0: Canciones, 1: Playlists, 2: Carpetas, 3: Artistas
    val libraryTab: StateFlow<Int> = _libraryTab.asStateFlow()

    // Equalizer
    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState: StateFlow<EqualizerState> = _equalizerState.asStateFlow()

    // Cloud & Security
    private val _cloudSyncState = MutableStateFlow(CloudSyncState())
    val cloudSyncState: StateFlow<CloudSyncState> = _cloudSyncState.asStateFlow()

    // AI Recommendations
    private val _aiRecommendations = MutableStateFlow(MusicRepository.AI_RECOMMENDATIONS)
    val aiRecommendations: StateFlow<List<AiRecommendation>> = _aiRecommendations.asStateFlow()
    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    // Open API & Streaming
    private val _streamingChannels = MutableStateFlow(MusicRepository.STREAMING_CHANNELS)
    val streamingChannels: StateFlow<List<StreamingChannel>> = _streamingChannels.asStateFlow()

    private val _openApiConfig = MutableStateFlow(OpenApiConfig())
    val openApiConfig: StateFlow<OpenApiConfig> = _openApiConfig.asStateFlow()

    // Theme & UI state
    private val _currentTheme = MutableStateFlow(LarkThemeMode.LARK_CYAN)
    val currentTheme: StateFlow<LarkThemeMode> = _currentTheme.asStateFlow()

    private val _autoDarkReadingMode = MutableStateFlow(true)
    val autoDarkReadingMode: StateFlow<Boolean> = _autoDarkReadingMode.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    // Dialog & Sheet Visibility
    val isFullScreenPlayerOpen = MutableStateFlow(false)
    val isKaraokeModeOpen = MutableStateFlow(false)
    val isEqualizerOpen = MutableStateFlow(false)
    val isSleepTimerOpen = MutableStateFlow(false)
    val isCloudVaultOpen = MutableStateFlow(false)
    val isOpenApiDialogOpen = MutableStateFlow(false)
    val isCreatePlaylistOpen = MutableStateFlow(false)
    val isVoiceSearchDialogOpen = MutableStateFlow(false)

    // Voice Search
    val voiceSearchQuery = MutableStateFlow("")
    val isVoiceListening = MutableStateFlow(false)

    // In-app SnackBar notification
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        // Initialize queue with default tracks
        playbackManager.setQueue(_tracks.value, 0)
    }

    // --- Search & Filter ---
    val filteredTracks: StateFlow<List<Track>> = combine(
        _tracks, _searchQuery, _selectedSmartTag
    ) { trackList, query, tag ->
        trackList.filter { track ->
            val matchesQuery = query.isEmpty() ||
                    track.title.contains(query, ignoreCase = true) ||
                    track.artist.contains(query, ignoreCase = true) ||
                    track.album.contains(query, ignoreCase = true) ||
                    track.genre.contains(query, ignoreCase = true)

            val matchesTag = tag == "Todos" || when (tag) {
                "Favoritos" -> track.isFavorite
                "Hi-Res" -> track.format.isHiRes
                else -> track.smartTags.any { it.equals(tag, ignoreCase = true) || it.equals("#$tag", ignoreCase = true) }
            }

            matchesQuery && matchesTag
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, _tracks.value)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedSmartTag(tag: String) {
        _selectedSmartTag.value = tag
    }

    fun setLibraryTab(tab: Int) {
        _libraryTab.value = tab
    }

    // --- Playback Delegates ---
    fun playTrack(track: Track, customQueue: List<Track>? = null) {
        playbackManager.playTrack(track, customQueue ?: _tracks.value)
    }

    fun togglePlayPause() = playbackManager.togglePlayPause()
    fun skipNext() = playbackManager.skipNext()
    fun skipPrevious() = playbackManager.skipPrevious()
    fun seekTo(positionMs: Long) = playbackManager.seekTo(positionMs)
    fun toggleShuffle() = playbackManager.toggleShuffle()
    fun cycleRepeatMode() = playbackManager.cycleRepeatMode()
    fun setPlaybackSpeed(speed: Float) = playbackManager.setPlaybackSpeed(speed)
    fun setSleepTimer(minutes: Int?) = playbackManager.setSleepTimer(minutes)

    fun toggleFavorite(trackId: String) {
        _tracks.update { list ->
            list.map { if (it.id == trackId) it.copy(isFavorite = !it.isFavorite) else it }
        }
        val updatedTrack = _tracks.value.find { it.id == trackId }
        val msg = if (updatedTrack?.isFavorite == true) "Añadido a favoritos ❤️" else "Eliminado de favoritos"
        showNotification(msg)
    }

    // --- Playlist Management ---
    fun selectPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
    }

    fun createPlaylist(name: String, description: String, tags: List<String>) {
        val newPlaylist = Playlist(
            id = "pl-${System.currentTimeMillis()}",
            name = name.ifBlank { "Mi Lista Nueva" },
            description = description.ifBlank { "Lista personalizada creada con Lark Player" },
            trackIds = _tracks.value.take(3).map { it.id },
            coverDrawableRes = _tracks.value.firstOrNull()?.coverDrawableRes ?: com.example.R.drawable.cover_neon_vibes_1791305842026,
            smartTags = tags.ifEmpty { listOf("#Personalizada") }
        )
        _playlists.update { it + newPlaylist }
        showNotification("Lista '${newPlaylist.name}' creada exitosamente 🎵")
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        _playlists.update { list ->
            list.map {
                if (it.id == playlistId && !it.trackIds.contains(trackId)) {
                    it.copy(trackIds = it.trackIds + trackId)
                } else it
            }
        }
        showNotification("Canción añadida a la lista de reproducción")
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        _playlists.update { list ->
            list.map {
                if (it.id == playlistId) {
                    it.copy(trackIds = it.trackIds.filter { id -> id != trackId })
                } else it
            }
        }
        showNotification("Canción eliminada de la lista")
    }

    // --- Equalizer Controls ---
    fun setEqualizerBandLevel(bandIndex: Int, levelDb: Float) {
        _equalizerState.update { state ->
            val newBands = state.bandLevels.toMutableList()
            if (bandIndex in newBands.indices) {
                newBands[bandIndex] = levelDb.coerceIn(-12f, 12f)
            }
            state.copy(bandLevels = newBands, selectedPreset = "Personalizado")
        }
    }

    fun applyEqualizerPreset(presetName: String) {
        val presetBands = EqualizerState.PRESETS[presetName] ?: return
        _equalizerState.update {
            it.copy(
                selectedPreset = presetName,
                bandLevels = presetBands
            )
        }
        showNotification("Ecualizador: Perfil '$presetName' activado 🎚️")
    }

    fun setBassBoost(level: Float) {
        _equalizerState.update { it.copy(bassBoost = level.coerceIn(0f, 100f)) }
    }

    fun setVirtualizer(level: Float) {
        _equalizerState.update { it.copy(virtualizer3D = level.coerceIn(0f, 100f)) }
    }

    fun setLoudnessGain(level: Float) {
        _equalizerState.update { it.copy(loudnessGain = level.coerceIn(0f, 100f)) }
    }

    fun setReverb(reverbName: String) {
        _equalizerState.update { it.copy(reverbEffect = reverbName) }
        showNotification("Efecto 3D: Reverb $reverbName")
    }

    fun toggleKaraokeVocalCut() {
        val newVocalCut = !_equalizerState.value.karaokeVocalCut
        _equalizerState.update { it.copy(karaokeVocalCut = newVocalCut) }
        val msg = if (newVocalCut) "Modo Karaoke activado: Voz atenuada para cantar 🎤" else "Modo normal: Voces restauradas"
        showNotification(msg)
    }

    // --- Cloud Sync & E2E Encryption ---
    fun triggerInstantSync() {
        viewModelScope.launch {
            _cloudSyncState.update { it.copy(statusText = "Sincronizando biblioteca...") }
            delay(1200L)
            _cloudSyncState.update {
                it.copy(
                    statusText = "Sincronizado en tiempo real (Cifrado E2EE)",
                    devices = it.devices.map { d -> if (d.isCurrentDevice) d.copy(lastSyncAgo = "Ahora mismo") else d }
                )
            }
            showNotification("Nube sincronizada con cifrado AES-256 de extremo a extremo ☁️🔒")
        }
    }

    fun toggleE2EEncryption() {
        val newState = !_cloudSyncState.value.isEndToEndEncrypted
        _cloudSyncState.update { it.copy(isEndToEndEncrypted = newState) }
        val msg = if (newState) "Cifrado E2EE activado: Claves privadas protegidas" else "Cifrado desactivado"
        showNotification(msg)
    }

    fun toggleAutoSync() {
        val newState = !_cloudSyncState.value.isAutoSyncEnabled
        _cloudSyncState.update { it.copy(isAutoSyncEnabled = newState) }
        showNotification(if (newState) "Sincronización en segundo plano activada" else "Sincronización manual")
    }

    // --- AI Recommendations ---
    fun generateAiPlaylistWithPrompt(userPrompt: String) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            delay(1500L) // Simulate AI reasoning & Gemini analysis
            val newAiMix = AiRecommendation(
                id = "ai-${System.currentTimeMillis()}",
                title = "Mix IA: " + (if (userPrompt.isBlank()) "Vibra Personalizada" else userPrompt.take(24)),
                promptUsed = userPrompt.ifBlank { "Música adaptada a tu estado de ánimo actual" },
                vibeTag = "Gemini AI Smart Mix",
                matchScorePercent = (93..99).random(),
                recommendedTrackIds = _tracks.value.shuffled().take(3).map { it.id },
                explanation = "Generado por Lark AI analizando patrones de ritmo, ecualizador e historial acústico."
            )
            _aiRecommendations.update { listOf(newAiMix) + it }
            _isGeneratingAi.value = false
            showNotification("¡Nueva lista IA generada con éxito! ✨")
        }
    }

    // --- Voice Search Simulation ---
    fun startVoiceListening() {
        isVoiceListening.value = true
        voiceSearchQuery.value = "Escuchando tu voz..."
        viewModelScope.launch {
            delay(1800L)
            val sampleVoiceQueries = listOf("Cyberpunk Horizon", "Música acústica", "Bajos potentes", "Karaoke en español")
            val detected = sampleVoiceQueries.random()
            voiceSearchQuery.value = detected
            delay(600L)
            isVoiceListening.value = false
            setSearchQuery(detected)
            isVoiceSearchDialogOpen.value = false
            showNotification("Voz detectada: '$detected' 🎙️")
        }
    }

    // --- Theme & Settings ---
    fun setTheme(theme: LarkThemeMode) {
        _currentTheme.value = theme
        showNotification("Tema actualizado: ${theme.displayName}")
    }

    fun toggleAutoDarkReadingMode() {
        _autoDarkReadingMode.update { !it }
    }

    fun togglePushNotifications() {
        _pushNotificationsEnabled.update { !it }
        val msg = if (_pushNotificationsEnabled.value) "Notificaciones push para nuevos lanzamientos activadas 🔔" else "Notificaciones silenciadas"
        showNotification(msg)
    }

    fun simulateRingtoneCut(track: Track) {
        showNotification("Tono de llamada creado: '${track.title.take(15)}' guardado en Tonos ✂️🔔")
    }

    fun showNotification(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearNotification() {
        _snackBarMessage.value = null
    }
}
