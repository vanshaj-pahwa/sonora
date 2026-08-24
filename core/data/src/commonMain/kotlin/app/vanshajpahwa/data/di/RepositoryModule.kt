package app.vanshajpahwa.data.di

import app.vanshajpahwa.common.Config.SERVICE_SCOPE
import app.vanshajpahwa.data.io.fileDir
import app.vanshajpahwa.data.repository.AccountRepositoryImpl
import app.vanshajpahwa.data.repository.AlbumRepositoryImpl
import app.vanshajpahwa.data.repository.AnalyticsRepositoryImpl
import app.vanshajpahwa.data.repository.ArtistRepositoryImpl
import app.vanshajpahwa.data.repository.CommonRepositoryImpl
import app.vanshajpahwa.data.repository.HomeRepositoryImpl
import app.vanshajpahwa.data.repository.ImportRepositoryImpl
import app.vanshajpahwa.data.repository.LocalPlaylistRepositoryImpl
import app.vanshajpahwa.data.repository.LyricsCanvasRepositoryImpl
import app.vanshajpahwa.data.repository.PlaylistRepositoryImpl
import app.vanshajpahwa.data.repository.PodcastRepositoryImpl
import app.vanshajpahwa.data.repository.SearchRepositoryImpl
import app.vanshajpahwa.data.repository.SongRepositoryImpl
import app.vanshajpahwa.data.repository.StreamRepositoryImpl
import app.vanshajpahwa.data.repository.UpdateRepositoryImpl
import app.vanshajpahwa.domain.repository.AccountRepository
import app.vanshajpahwa.domain.repository.AlbumRepository
import app.vanshajpahwa.domain.repository.AnalyticsRepository
import app.vanshajpahwa.domain.repository.ArtistRepository
import app.vanshajpahwa.domain.repository.CommonRepository
import app.vanshajpahwa.domain.repository.HomeRepository
import app.vanshajpahwa.domain.repository.ImportRepository
import app.vanshajpahwa.domain.repository.LocalPlaylistRepository
import app.vanshajpahwa.domain.repository.LyricsCanvasRepository
import app.vanshajpahwa.domain.repository.PlaylistRepository
import app.vanshajpahwa.domain.repository.PodcastRepository
import app.vanshajpahwa.domain.repository.SearchRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.repository.StreamRepository
import app.vanshajpahwa.domain.repository.UpdateRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AccountRepository>(createdAtStart = true) {
            AccountRepositoryImpl(get(), get())
        }

        single<AlbumRepository>(createdAtStart = true) {
            AlbumRepositoryImpl(get(), get())
        }

        single<ArtistRepository>(createdAtStart = true) {
            ArtistRepositoryImpl(get(), get())
        }

        single<CommonRepository>(createdAtStart = true) {
            CommonRepositoryImpl(get(named(SERVICE_SCOPE)), get(), get(), get(), get(), get()).apply {
                this.init("${fileDir()}/ytdlp-cookie.txt", get())
            }
        }

        single<HomeRepository>(createdAtStart = true) {
            HomeRepositoryImpl(get(), get())
        }

        single<ImportRepository>(createdAtStart = true) {
            ImportRepositoryImpl(get())
        }

        single<LocalPlaylistRepository>(createdAtStart = true) {
            LocalPlaylistRepositoryImpl(get(), get())
        }

        single<LyricsCanvasRepository>(createdAtStart = true) {
            LyricsCanvasRepositoryImpl(get(), get(), get(), get(), get())
        }

        single<PlaylistRepository>(createdAtStart = true) {
            PlaylistRepositoryImpl(get(), get(), get())
        }

        single<PodcastRepository>(createdAtStart = true) {
            PodcastRepositoryImpl(get(), get())
        }

        single<SearchRepository>(createdAtStart = true) {
            SearchRepositoryImpl(get(), get())
        }

        single<SongRepository>(createdAtStart = true) {
            SongRepositoryImpl(get(), get(), get())
        }

        single<StreamRepository>(createdAtStart = true) {
            StreamRepositoryImpl(get(), get())
        }

        single<UpdateRepository>(createdAtStart = true) {
            UpdateRepositoryImpl(get())
        }

        single<AnalyticsRepository>(createdAtStart = true) {
            AnalyticsRepositoryImpl(get())
        }
    }