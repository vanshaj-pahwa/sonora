package app.vanshajpahwa.data.di

import app.vanshajpahwa.common.Config
import app.vanshajpahwa.data.mediaservice.createMediaServiceHandler
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaHandlerModule =
    module {
        single<MediaPlayerHandler>(createdAtStart = true) {
            createMediaServiceHandler(
                dataStoreManager = get(),
                songRepository = get(),
                streamRepository = get(),
                localPlaylistRepository = get(),
                analyticsRepository = get(),
                coroutineScope = get<CoroutineScope>(named(Config.SERVICE_SCOPE)),
            )
        }
    }