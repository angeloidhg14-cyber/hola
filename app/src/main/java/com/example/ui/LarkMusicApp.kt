package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.LarkMusicTheme
import com.example.viewmodel.LarkMusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LarkMusicApp(
    viewModel: LarkMusicViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val filteredTracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val allTracks by viewModel.tracks.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSmartTag by viewModel.selectedSmartTag.collectAsStateWithLifecycle()
    val libraryTab by viewModel.libraryTab.collectAsStateWithLifecycle()
    val equalizerState by viewModel.equalizerState.collectAsStateWithLifecycle()
    val cloudSyncState by viewModel.cloudSyncState.collectAsStateWithLifecycle()
    val aiRecommendations by viewModel.aiRecommendations.collectAsStateWithLifecycle()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsStateWithLifecycle()
    val streamingChannels by viewModel.streamingChannels.collectAsStateWithLifecycle()
    val openApiConfig by viewModel.openApiConfig.collectAsStateWithLifecycle()
    val autoDarkReadingMode by viewModel.autoDarkReadingMode.collectAsStateWithLifecycle()
    val pushNotificationsEnabled by viewModel.pushNotificationsEnabled.collectAsStateWithLifecycle()

    val isFullScreenOpen by viewModel.isFullScreenPlayerOpen.collectAsStateWithLifecycle()
    val isKaraokeOpen by viewModel.isKaraokeModeOpen.collectAsStateWithLifecycle()
    val isEqualizerOpen by viewModel.isEqualizerOpen.collectAsStateWithLifecycle()
    val isSleepTimerOpen by viewModel.isSleepTimerOpen.collectAsStateWithLifecycle()
    val isCloudVaultOpen by viewModel.isCloudVaultOpen.collectAsStateWithLifecycle()
    val isOpenApiOpen by viewModel.isOpenApiDialogOpen.collectAsStateWithLifecycle()
    val isCreatePlaylistOpen by viewModel.isCreatePlaylistOpen.collectAsStateWithLifecycle()
    val isVoiceSearchOpen by viewModel.isVoiceSearchDialogOpen.collectAsStateWithLifecycle()
    val voiceTranscript by viewModel.voiceSearchQuery.collectAsStateWithLifecycle()
    val isVoiceListening by viewModel.isVoiceListening.collectAsStateWithLifecycle()
    val snackBarMsg by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentNavTab by remember { mutableStateOf(0) } // 0: Biblioteca, 1: Descubrir, 2: Ajustes

    // Trigger in-app snackbars
    LaunchedEffect(snackBarMsg) {
        snackBarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    // Handle back button smoothly
    BackHandler(enabled = isFullScreenOpen || isKaraokeOpen || selectedPlaylist != null) {
        when {
            isKaraokeOpen -> viewModel.isKaraokeModeOpen.value = false
            isFullScreenOpen -> viewModel.isFullScreenPlayerOpen.value = false
            selectedPlaylist != null -> viewModel.selectPlaylist(null)
        }
    }

    LarkMusicTheme(themeMode = currentTheme) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("lark_music_root_scaffold"),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Mini Player above navigation bar when a track exists
                    if (playbackState.currentTrack != null && !isFullScreenOpen) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipNext() },
                            onOpenPlayer = { viewModel.isFullScreenPlayerOpen.value = true },
                            onOpenEqualizer = { viewModel.isEqualizerOpen.value = true }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = currentNavTab == 0,
                            onClick = { currentNavTab = 0 },
                            icon = { Icon(Icons.Rounded.LibraryMusic, contentDescription = "Biblioteca") },
                            label = { Text("Biblioteca") },
                            modifier = Modifier.testTag("nav_item_library")
                        )
                        NavigationBarItem(
                            selected = currentNavTab == 1,
                            onClick = { currentNavTab = 1 },
                            icon = { Icon(Icons.Rounded.Explore, contentDescription = "Descubrir") },
                            label = { Text("Descubrir") },
                            modifier = Modifier.testTag("nav_item_discover")
                        )
                        NavigationBarItem(
                            selected = currentNavTab == 2,
                            onClick = { currentNavTab = 2 },
                            icon = { Icon(Icons.Rounded.Palette, contentDescription = "Ajustes") },
                            label = { Text("Ajustes") },
                            modifier = Modifier.testTag("nav_item_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentNavTab) {
                    0 -> LibraryScreen(
                        tracks = filteredTracks,
                        playlists = playlists,
                        currentTrackId = playbackState.currentTrack?.id,
                        isPlaying = playbackState.isPlaying,
                        searchQuery = searchQuery,
                        selectedTag = selectedSmartTag,
                        selectedTab = libraryTab,
                        onTabSelected = { viewModel.setLibraryTab(it) },
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        onTagSelected = { viewModel.setSelectedSmartTag(it) },
                        onTrackSelected = { track -> viewModel.playTrack(track) },
                        onToggleFavorite = { trackId -> viewModel.toggleFavorite(trackId) },
                        onCreatePlaylistClick = { viewModel.isCreatePlaylistOpen.value = true },
                        onSelectPlaylist = { pl -> viewModel.selectPlaylist(pl) },
                        onOpenVoiceSearch = {
                            viewModel.isVoiceSearchDialogOpen.value = true
                            viewModel.startVoiceListening()
                        },
                        onOpenCloudVault = { viewModel.isCloudVaultOpen.value = true }
                    )
                    1 -> DiscoverStreamingScreen(
                        streamingChannels = streamingChannels,
                        aiRecommendations = aiRecommendations,
                        isGeneratingAi = isGeneratingAi,
                        pushNotificationsEnabled = pushNotificationsEnabled,
                        onTogglePushNotifications = { viewModel.togglePushNotifications() },
                        onChannelSelected = { channel -> viewModel.playTrack(channel.sampleTrack) },
                        onGenerateAiMix = { prompt -> viewModel.generateAiPlaylistWithPrompt(prompt) },
                        onPlayAiMix = { mix ->
                            val tracksToPlay = allTracks.filter { mix.recommendedTrackIds.contains(it.id) }
                            if (tracksToPlay.isNotEmpty()) {
                                viewModel.playTrack(tracksToPlay.first(), tracksToPlay)
                            }
                        },
                        onOpenDeveloperApi = { viewModel.isOpenApiDialogOpen.value = true }
                    )
                    2 -> SettingsThemeScreen(
                        currentTheme = currentTheme,
                        onSelectTheme = { viewModel.setTheme(it) },
                        autoDarkReadingMode = autoDarkReadingMode,
                        onToggleAutoDark = { viewModel.toggleAutoDarkReadingMode() },
                        onOpenCloudVault = { viewModel.isCloudVaultOpen.value = true },
                        onOpenDeveloperApi = { viewModel.isOpenApiDialogOpen.value = true }
                    )
                }

                // Full Screen Player Modal
                AnimatedVisibility(
                    visible = isFullScreenOpen,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    FullScreenPlayer(
                        playbackState = playbackState,
                        onDismiss = { viewModel.isFullScreenPlayerOpen.value = false },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onSeekTo = { pos -> viewModel.seekTo(pos) },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onCycleRepeat = { viewModel.cycleRepeatMode() },
                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                        onOpenKaraoke = { viewModel.isKaraokeModeOpen.value = true },
                        onOpenEqualizer = { viewModel.isEqualizerOpen.value = true },
                        onSetPlaybackSpeed = { spd -> viewModel.setPlaybackSpeed(spd) },
                        onOpenSleepTimer = { viewModel.isSleepTimerOpen.value = true },
                        onCutRingtone = { trk -> viewModel.simulateRingtoneCut(trk) }
                    )
                }

                // Synchronized Karaoke Modal
                if (isKaraokeOpen && playbackState.currentTrack != null) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.isKaraokeModeOpen.value = false },
                        containerColor = if (autoDarkReadingMode) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
                    ) {
                        KaraokeLyricsView(
                            track = playbackState.currentTrack!!,
                            currentPositionMs = playbackState.currentPositionMs,
                            isVocalCutEnabled = equalizerState.karaokeVocalCut,
                            onToggleVocalCut = { viewModel.toggleKaraokeVocalCut() },
                            onSeekTo = { pos -> viewModel.seekTo(pos) }
                        )
                    }
                }
            }

            // Equalizer Sheet Dialog
            if (isEqualizerOpen) {
                EqualizerSheet(
                    equalizerState = equalizerState,
                    onBandChange = { band, db -> viewModel.setEqualizerBandLevel(band, db) },
                    onPresetSelect = { preset -> viewModel.applyEqualizerPreset(preset) },
                    onBassBoostChange = { boost -> viewModel.setBassBoost(boost) },
                    onVirtualizerChange = { v -> viewModel.setVirtualizer(v) },
                    onLoudnessChange = { l -> viewModel.setLoudnessGain(l) },
                    onReverbSelect = { r -> viewModel.setReverb(r) },
                    onDismiss = { viewModel.isEqualizerOpen.value = false }
                )
            }

            // Sleep Timer Sheet
            if (isSleepTimerOpen) {
                SleepTimerSheet(
                    currentMinutesLeft = playbackState.sleepTimerMinutesLeft,
                    onSetTimer = { mins -> viewModel.setSleepTimer(mins) },
                    onDismiss = { viewModel.isSleepTimerOpen.value = false }
                )
            }

            // Cloud Vault Sheet
            if (isCloudVaultOpen) {
                CloudSyncVaultSheet(
                    syncState = cloudSyncState,
                    onTriggerSync = { viewModel.triggerInstantSync() },
                    onToggleE2E = { viewModel.toggleE2EEncryption() },
                    onToggleAutoSync = { viewModel.toggleAutoSync() },
                    onDismiss = { viewModel.isCloudVaultOpen.value = false }
                )
            }

            // Open API Developer Dialog
            if (isOpenApiOpen) {
                OpenApiDeveloperDialog(
                    config = openApiConfig,
                    onDismiss = { viewModel.isOpenApiDialogOpen.value = false },
                    onCopyKey = { viewModel.showNotification("Clave API copiada al portapapeles 📋") }
                )
            }

            // Voice Search Listening Dialog
            if (isVoiceSearchOpen) {
                VoiceSearchDialog(
                    isListening = isVoiceListening,
                    currentTranscript = voiceTranscript,
                    onStartListening = { viewModel.startVoiceListening() },
                    onDismiss = { viewModel.isVoiceSearchDialogOpen.value = false }
                )
            }

            // Create Custom Playlist Dialog
            if (isCreatePlaylistOpen) {
                CreatePlaylistDialog(
                    onDismiss = { viewModel.isCreatePlaylistOpen.value = false },
                    onCreate = { name, desc, tags ->
                        viewModel.createPlaylist(name, desc, tags)
                    }
                )
            }

            // Playlist Detail Sheet
            if (selectedPlaylist != null) {
                PlaylistDetailSheet(
                    playlist = selectedPlaylist!!,
                    allTracks = allTracks,
                    currentTrackId = playbackState.currentTrack?.id,
                    isPlaying = playbackState.isPlaying,
                    onTrackSelected = { trk -> viewModel.playTrack(trk) },
                    onPlayAll = { trks -> viewModel.playTrack(trks.first(), trks) },
                    onRemoveTrack = { trkId -> viewModel.removeTrackFromPlaylist(selectedPlaylist!!.id, trkId) },
                    onDismiss = { viewModel.selectPlaylist(null) }
                )
            }
        }
    }
}
