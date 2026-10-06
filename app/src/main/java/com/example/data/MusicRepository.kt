package com.example.data

import com.example.model.*
import com.example.R

object MusicRepository {

    val SAMPLE_TRACKS = listOf(
        Track(
            id = "t1",
            title = "Cyberpunk Horizon",
            artist = "Neon Odyssey",
            album = "Midnight Velocity (2026)",
            durationMs = 214_000L, // 3:34
            coverDrawableRes = R.drawable.cover_neon_vibes_1791305842026,
            format = AudioFormatType.FLAC_HI_RES,
            folderPath = "/Almacenamiento/Música/Hi-Res",
            smartTags = listOf("#HiRes", "#Synthwave", "#Favoritos", "#Karaoke"),
            isFavorite = true,
            isOfflineAvailable = true,
            isCloudSynced = true,
            fileSizeMb = 48.6,
            year = 2026,
            genre = "Synthwave / Cyberpunk",
            lyrics = listOf(
                LyricLine(0L, "[Intro: Sintetizadores analógicos y pulso 808]", "[Intro instrumental]"),
                LyricLine(5000L, "Luces de neón cortando la oscuridad", "Neon lights slicing through the dark"),
                LyricLine(11000L, "La ciudad despierta a cien por hora", "The city awakens at full speed"),
                LyricLine(17000L, "Bajo el cielo violeta de medianoche", "Under the midnight violet sky"),
                LyricLine(23000L, "Sentimos el ritmo en cada respiración", "We feel the rhythm in every breath"),
                LyricLine(29000L, "¡Baila conmigo en la frecuencia digital!", "Dance with me in the digital frequency!"),
                LyricLine(36000L, "Nada puede detener esta corriente hoy", "Nothing can stop this flow today"),
                LyricLine(42000L, "Guitarras eléctricas y ecos en el aire", "Electric guitars and echoes in the air"),
                LyricLine(49000L, "Somos eternos bajo el resplandor", "We are eternal beneath the glow"),
                LyricLine(57000L, "[Solo de sintetizador analógico 96kHz]", "[Solo de sintetizador]"),
                LyricLine(68000L, "Siente la vibración que recorre tu piel", "Feel the vibration running on your skin"),
                LyricLine(75000L, "El futuro es ahora, no mires atrás", "The future is now, do not look back"),
                LyricLine(84000L, "¡Lark Player sonando en alta fidelidad!", "Lark Player playing in high fidelity!"),
                LyricLine(95000L, "[Outro: Fadeout con reverberación espacial]", "[Outro]")
            )
        ),
        Track(
            id = "t2",
            title = "Atardecer Acústico",
            artist = "Luna & Sol",
            album = "Cuerdas del Alma",
            durationMs = 195_000L, // 3:15
            coverDrawableRes = R.drawable.cover_acoustic_sunset_1791305854631,
            format = AudioFormatType.WAV_STUDIO,
            folderPath = "/Almacenamiento/Música/Acústico",
            smartTags = listOf("#Acústico", "#Relax", "#Karaoke", "#Playa"),
            isFavorite = true,
            isOfflineAvailable = true,
            isCloudSynced = true,
            fileSizeMb = 65.2,
            year = 2026,
            genre = "Acoustic / Indie Folk",
            lyrics = listOf(
                LyricLine(0L, "[Intro: Arpegio de guitarra y olas de mar]", "[Intro guitarra]"),
                LyricLine(4500L, "Las olas susurran historias del mar", "Waves whisper stories of the ocean"),
                LyricLine(10000L, "El viento dorado acaricia tu mirar", "The golden wind touches your gaze"),
                LyricLine(16000L, "Dejamos las prisas en la arena atrás", "We leave the rush behind on the sand"),
                LyricLine(22000L, "Y en esta melodía volvemos a soñar", "And in this tune we dream once more"),
                LyricLine(28000L, "Canta conmigo al caer el sol", "Sing with me as the sun goes down"),
                LyricLine(35000L, "Llenando de paz nuestro corazón", "Filling our hearts with serene peace"),
                LyricLine(43000L, "[Puente de armónicos naturales]", "[Armónicos]"),
                LyricLine(52000L, "El crepúsculo abraza la inmensidad", "Twilight embraces the vastness"),
                LyricLine(60000L, "Un acorde sincero nos da libertad", "A sincere chord gives us freedom")
            )
        ),
        Track(
            id = "t3",
            title = "Pulse Electro 2026",
            artist = "DJ Quantum",
            album = "Subwoofer Universe",
            durationMs = 242_000L, // 4:02
            coverDrawableRes = R.drawable.cover_electronic_pulse_1791305868392,
            format = AudioFormatType.DOLBY_ATMOS,
            folderPath = "/Almacenamiento/Música/EDM",
            smartTags = listOf("#EDM", "#Gym", "#BassBoost", "#HiRes"),
            isFavorite = false,
            isOfflineAvailable = true,
            isCloudSynced = true,
            fileSizeMb = 42.1,
            year = 2026,
            genre = "EDM / Electro House",
            lyrics = listOf(
                LyricLine(0L, "[Intro: Drop de graves profundos a 128 BPM]", "[Drop de graves]"),
                LyricLine(6000L, "Feel the electric pulse across the floor", "Siente el pulso eléctrico en la pista"),
                LyricLine(12000L, "Turn up the bass and give us more", "Sube los bajos y danos más"),
                LyricLine(18000L, "Frequency rising, reach for the sky", "Frecuencia subiendo, alcanza el cielo"),
                LyricLine(24000L, "Soundwaves vibrating, we fly so high", "Ondas sonoras vibrando, volamos alto"),
                LyricLine(30000L, "¡Drop the bass! Siente la energía total", "¡Suelta los bajos con Lark EQ!"),
                LyricLine(40000L, "[Drop masivo con subwoofer a 30Hz]", "[Subwoofer 30Hz]")
            )
        ),
        Track(
            id = "t4",
            title = "Midnight Lofi Coffee",
            artist = "Tokyo Beats",
            album = "Rainy Day Study Sessions",
            durationMs = 178_000L, // 2:58
            coverDrawableRes = R.drawable.cover_lofi_midnight_1791305882020,
            format = AudioFormatType.FLAC_HI_RES,
            folderPath = "/Almacenamiento/Música/Lofi",
            smartTags = listOf("#Lofi", "#Estudiar", "#Chill", "#Noche"),
            isFavorite = true,
            isOfflineAvailable = true,
            isCloudSynced = true,
            fileSizeMb = 31.8,
            year = 2025,
            genre = "Lo-Fi Hip Hop / Chillhop",
            lyrics = listOf(
                LyricLine(0L, "[Intro: Gotas de lluvia en ventana y vinilo cálido]", "[Sonido de lluvia]"),
                LyricLine(5000L, "Lluvia en la ventana, café humeante", "Rain on window, steaming coffee"),
                LyricLine(12000L, "Pensamientos que flotan en el instante", "Thoughts floating in the moment"),
                LyricLine(19000L, "Teclado suave, compás relajante", "Gentle keys, soothing tempo"),
                LyricLine(26000L, "La noche es joven, la mente constante", "The night is young, mind focused"),
                LyricLine(34000L, "Sonido de vinilo girando sin fin", "Vinyl record spinning softly"),
                LyricLine(42000L, "Paz absoluta en mi rincón", "Absolute peaceful solitude")
            )
        ),
        Track(
            id = "t5",
            title = "Ritmo Latino Fuego",
            artist = "Sangre Tropical",
            album = "Caribe Sensación 2026",
            durationMs = 205_000L, // 3:25
            coverDrawableRes = R.drawable.cover_neon_vibes_1791305842026,
            format = AudioFormatType.MP3_320K,
            folderPath = "/Almacenamiento/Música/Latino",
            smartTags = listOf("#Latino", "#Fiesta", "#Karaoke", "#Bailar"),
            isFavorite = true,
            isOfflineAvailable = true,
            isCloudSynced = true,
            fileSizeMb = 18.2,
            year = 2026,
            genre = "Urbano Latino / Salsa Fusion",
            lyrics = listOf(
                LyricLine(0L, "[Intro: Percusión caribeña y metales vibrantes]", "[Intro percusión]"),
                LyricLine(5000L, "¡Que suene el timbal y prenda la fiesta!", "Let the timbal sound and party ignite!"),
                LyricLine(11000L, "Bailando pegao hasta que amanezca", "Dancing tight till sunrise"),
                LyricLine(17000L, "Con este sabor que nadie molesta", "With this flavor no one bothers"),
                LyricLine(23000L, "¡Sube el volumen y siente la orquesta!", "Turn up the volume and feel the band!"),
                LyricLine(30000L, "¡Azúcar y candela en la pista!", "Sugar and fire on the floor!")
            )
        )
    )

    val INITIAL_PLAYLISTS = listOf(
        Playlist(
            id = "pl-1",
            name = "Audiofilo Master Hi-Res",
            description = "Pistas FLAC y WAV a 96/192kHz sin compresión para audífonos de alta fidelidad.",
            trackIds = listOf("t1", "t2", "t4"),
            coverDrawableRes = R.drawable.cover_neon_vibes_1791305842026,
            smartTags = listOf("#HiRes", "#FLAC", "#Audiophile"),
            isAutoGenerated = false
        ),
        Playlist(
            id = "pl-2",
            name = "Karaoke Time en Vivo",
            description = "Canciones con letras sincronizadas palabra por palabra y guía vocal.",
            trackIds = listOf("t1", "t2", "t5"),
            coverDrawableRes = R.drawable.cover_acoustic_sunset_1791305854631,
            smartTags = listOf("#Karaoke", "#Letras", "#Cantar"),
            isAutoGenerated = false
        ),
        Playlist(
            id = "pl-3",
            name = "Energía Gym & Bass Boost",
            description = "Graves contundentes optimizados con el ecualizador Lark Bass Booster.",
            trackIds = listOf("t3", "t1", "t5"),
            coverDrawableRes = R.drawable.cover_electronic_pulse_1791305868392,
            smartTags = listOf("#Gym", "#BassBoost", "#Cardio"),
            isAutoGenerated = true
        ),
        Playlist(
            id = "pl-4",
            name = "Estudio & Concentración Nocturna",
            description = "Beats relajantes de baja frecuencia para lectura y productividad.",
            trackIds = listOf("t4", "t2"),
            coverDrawableRes = R.drawable.cover_lofi_midnight_1791305882020,
            smartTags = listOf("#Lofi", "#Estudio", "#Chill"),
            isAutoGenerated = true
        )
    )

    val STREAMING_CHANNELS = listOf(
        StreamingChannel(
            id = "stream-1",
            name = "Lark Global Top 50 Streaming",
            category = "Éxitos Mundiales",
            listenersCount = "2.4M oyentes",
            bitRate = "320 kbps HD",
            coverDrawableRes = R.drawable.cover_neon_vibes_1791305842026,
            sampleTrack = SAMPLE_TRACKS[0]
        ),
        StreamingChannel(
            id = "stream-2",
            name = "24/7 Lofi Coffee Radio Live",
            category = "Radio en Vivo",
            listenersCount = "890K oyentes",
            bitRate = "FLAC Lossless",
            coverDrawableRes = R.drawable.cover_lofi_midnight_1791305882020,
            sampleTrack = SAMPLE_TRACKS[3]
        ),
        StreamingChannel(
            id = "stream-3",
            name = "EDM Club & Festival Live Stream",
            category = "Electrónica en Directo",
            listenersCount = "1.1M oyentes",
            bitRate = "Dolby Atmos 3D",
            coverDrawableRes = R.drawable.cover_electronic_pulse_1791305868392,
            sampleTrack = SAMPLE_TRACKS[2]
        ),
        StreamingChannel(
            id = "stream-4",
            name = "Acústico & Velas Sunset Radio",
            category = "Indie Acústico",
            listenersCount = "450K oyentes",
            bitRate = "WAV Master Stream",
            coverDrawableRes = R.drawable.cover_acoustic_sunset_1791305854631,
            sampleTrack = SAMPLE_TRACKS[1]
        )
    )

    val AI_RECOMMENDATIONS = listOf(
        AiRecommendation(
            id = "ai-1",
            title = "Mix de Noche Ciberpunk",
            promptUsed = "Música electrónica con sintetizadores y ritmo rápido para noche en la ciudad",
            vibeTag = "Modo Futurista 128 BPM",
            matchScorePercent = 98,
            recommendedTrackIds = listOf("t1", "t3"),
            explanation = "Basado en tu gusto por Cyberpunk Horizon y preferencias de ecualización Bass Booster."
        ),
        AiRecommendation(
            id = "ai-2",
            title = "Sesión de Lectura Profunda",
            promptUsed = "Sonido lofi con lluvia para leer sin distracciones",
            vibeTag = "Relajación Alpha Waves",
            matchScorePercent = 95,
            recommendedTrackIds = listOf("t4", "t2"),
            explanation = "Recomendado por la IA de Lark por tus sesiones de escucha nocturna y tempo calmado."
        ),
        AiRecommendation(
            id = "ai-3",
            title = "Karaoke Latino & Baile",
            promptUsed = "Música bailable con letras activas para cantar con amigos",
            vibeTag = "Energía Fiesta 100%",
            matchScorePercent = 92,
            recommendedTrackIds = listOf("t5", "t1"),
            explanation = "Seleccionado automáticamente para aprovechar la función de Karaoke en tiempo real."
        )
    )
}
