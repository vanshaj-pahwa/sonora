package app.vanshajpahwa.data.mediaservice

actual fun createMediaServiceHandler(
    dataStoreManager: app.vanshajpahwa.domain.manager.DataStoreManager,
    songRepository: app.vanshajpahwa.domain.repository.SongRepository,
    streamRepository: app.vanshajpahwa.domain.repository.StreamRepository,
    localPlaylistRepository: app.vanshajpahwa.domain.repository.LocalPlaylistRepository,
    analyticsRepository: app.vanshajpahwa.domain.repository.AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler {
    TODO("Not yet implemented")
}