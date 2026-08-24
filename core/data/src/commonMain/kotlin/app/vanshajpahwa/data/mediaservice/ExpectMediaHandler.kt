package app.vanshajpahwa.data.mediaservice

import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import app.vanshajpahwa.domain.repository.AnalyticsRepository
import app.vanshajpahwa.domain.repository.LocalPlaylistRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.repository.StreamRepository
import kotlinx.coroutines.CoroutineScope

expect fun createMediaServiceHandler(
    dataStoreManager: DataStoreManager,
    songRepository: SongRepository,
    streamRepository: StreamRepository,
    localPlaylistRepository: LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: CoroutineScope,
): MediaPlayerHandler