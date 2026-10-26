package app.vanshajpahwa.sonora.di

import app.vanshajpahwa.sonora.viewModel.AlbumViewModel
import app.vanshajpahwa.sonora.viewModel.AnalyticsViewModel
import app.vanshajpahwa.sonora.viewModel.ArtistViewModel
import app.vanshajpahwa.sonora.viewModel.CatalogViewModel
import app.vanshajpahwa.sonora.viewModel.HomeViewModel
import app.vanshajpahwa.sonora.viewModel.MoodArtworkStore
import app.vanshajpahwa.sonora.viewModel.LocalFilesViewModel
import app.vanshajpahwa.sonora.viewModel.MoreLikeViewModel
import app.vanshajpahwa.sonora.viewModel.ImportViewModel
import app.vanshajpahwa.sonora.viewModel.LibraryDynamicPlaylistViewModel
import app.vanshajpahwa.sonora.viewModel.LibraryViewModel
import app.vanshajpahwa.sonora.viewModel.LocalPlaylistViewModel
import app.vanshajpahwa.sonora.viewModel.LogInViewModel
import app.vanshajpahwa.sonora.viewModel.MoodViewModel
import app.vanshajpahwa.sonora.viewModel.MoreAlbumsViewModel
import app.vanshajpahwa.sonora.viewModel.NotificationViewModel
import app.vanshajpahwa.sonora.viewModel.NowPlayingBottomSheetViewModel
import app.vanshajpahwa.sonora.viewModel.PlaylistViewModel
import app.vanshajpahwa.sonora.viewModel.PodcastViewModel
import app.vanshajpahwa.sonora.viewModel.RecentlySongsViewModel
import app.vanshajpahwa.sonora.viewModel.DurationBackfill
import app.vanshajpahwa.sonora.viewModel.RemoteFeedHealth
import app.vanshajpahwa.sonora.viewModel.SearchViewModel
import app.vanshajpahwa.sonora.viewModel.SettingsViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NowPlayingBottomSheetViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        single { MoodArtworkStore(get()) }
        single { RemoteFeedHealth() }
        single { DurationBackfill(get(), get()) }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ImportViewModel(
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LocalFilesViewModel()
        }
        viewModel {
            CatalogViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            MoreLikeViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LogInViewModel(
                get(),
            )
        }
        viewModel {
            PodcastViewModel(
                get(),
            )
        }
        viewModel {
            MoreAlbumsViewModel(
                get(),
            )
        }
        viewModel {
            RecentlySongsViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NotificationViewModel(
                get(),
            )
        }
        viewModel {
            MoodViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AnalyticsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }