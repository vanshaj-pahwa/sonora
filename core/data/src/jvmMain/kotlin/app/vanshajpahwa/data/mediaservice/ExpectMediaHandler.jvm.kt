package app.vanshajpahwa.data.mediaservice

import app.vanshajpahwa.domain.repository.AnalyticsRepository

actual fun createMediaServiceHandler(
    dataStoreManager: app.vanshajpahwa.domain.manager.DataStoreManager,
    songRepository: app.vanshajpahwa.domain.repository.SongRepository,
    streamRepository: app.vanshajpahwa.domain.repository.StreamRepository,
    localPlaylistRepository: app.vanshajpahwa.domain.repository.LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler =
    JvmMediaPlayerHandlerImpl(
        dataStoreManager = dataStoreManager,
        songRepository = songRepository,
        streamRepository = streamRepository,
        localPlaylistRepository = localPlaylistRepository,
        analyticsRepository = analyticsRepository,
        coroutineScope = coroutineScope,
    )