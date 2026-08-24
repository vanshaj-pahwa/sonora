package app.vanshajpahwa.data.di

import DatabaseDao
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.vanshajpahwa.data.dataStore.DataStoreManagerImpl
import app.vanshajpahwa.data.dataStore.createDataStoreInstance
import app.vanshajpahwa.data.db.Converters
import app.vanshajpahwa.data.db.MusicDatabase
import app.vanshajpahwa.data.db.datasource.AnalyticsDatasource
import app.vanshajpahwa.data.db.datasource.LocalDataSource
import app.vanshajpahwa.data.db.getDatabaseBuilder
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import app.vanshajpahwa.spotify.Spotify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import app.vanshajpahwa.aiservice.AiClient
import app.vanshajpahwa.lyrics.SonoraLyricsClient
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val databaseModule =
    module {
        single(createdAtStart = true) {
            Converters()
        }
        // Database
        single(createdAtStart = true) {
            getDatabaseBuilder(
                get<Converters>()
            )
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        // DatabaseDao
        single(createdAtStart = true) {
            get<MusicDatabase>().getDatabaseDao()
        }
        // LocalDataSource
        single(createdAtStart = true) {
            LocalDataSource(get<DatabaseDao>())
        }
        // AnalyticsDatasource
        single(createdAtStart = true) {
            AnalyticsDatasource(get<DatabaseDao>())
        }
        // Datastore
        single(createdAtStart = true) {
            createDataStoreInstance()
        }
        // DatastoreManager
        single<DataStoreManager>(createdAtStart = true) {
            DataStoreManagerImpl(get<DataStore<Preferences>>())
        }

        // Move YouTube from Singleton to Koin DI
        single(createdAtStart = true) {
            YouTube()
        }

        single(createdAtStart = true) {
            Spotify()
        }

        single(createdAtStart = true) {
            AiClient()
        }

        single(createdAtStart = true) {
            SonoraLyricsClient()
        }
    }