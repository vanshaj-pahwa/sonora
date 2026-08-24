package app.vanshajpahwa.data.di.loader

import app.vanshajpahwa.data.di.databaseModule
import app.vanshajpahwa.data.di.mediaHandlerModule
import app.vanshajpahwa.data.di.repositoryModule
import org.koin.core.context.loadKoinModules

fun loadAllModules() {
    loadKoinModules(
        listOf(
            databaseModule,
            repositoryModule,
        ),
    )
    loadKoinModules(mediaHandlerModule)
    loadMediaService()
}

expect fun loadMediaService()