package app.vanshajpahwa.data.db

import DatabaseDao
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.EpisodeEntity
import app.vanshajpahwa.domain.data.entities.FollowedArtistSingleAndAlbum
import app.vanshajpahwa.domain.data.entities.GoogleAccountEntity
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity
import app.vanshajpahwa.domain.data.entities.LyricsEntity
import app.vanshajpahwa.domain.data.entities.NewFormatEntity
import app.vanshajpahwa.domain.data.entities.NotificationEntity
import app.vanshajpahwa.domain.data.entities.PairSongLocalPlaylist
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.PodcastsEntity
import app.vanshajpahwa.domain.data.entities.QueueEntity
import app.vanshajpahwa.domain.data.entities.SearchHistory
import app.vanshajpahwa.domain.data.entities.SetVideoIdEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.entities.SongInfoEntity
import app.vanshajpahwa.domain.data.entities.TranslatedLyricsEntity
import app.vanshajpahwa.domain.data.entities.YourYouTubePlaylistList
import app.vanshajpahwa.domain.data.entities.analytics.EventArtistEntity
import app.vanshajpahwa.domain.data.entities.analytics.PlaybackEventEntity

@Database(
    entities = [
        NewFormatEntity::class, SongInfoEntity::class, SearchHistory::class, SongEntity::class, ArtistEntity::class,
        AlbumEntity::class, PlaylistEntity::class, LocalPlaylistEntity::class, LyricsEntity::class, QueueEntity::class,
        SetVideoIdEntity::class, PairSongLocalPlaylist::class, GoogleAccountEntity::class, FollowedArtistSingleAndAlbum::class,
        NotificationEntity::class, TranslatedLyricsEntity::class, PodcastsEntity::class, EpisodeEntity::class,
        YourYouTubePlaylistList::class, PlaybackEventEntity::class, EventArtistEntity::class
    ],
    version = 24,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3), AutoMigration(
            from = 1,
            to = 3,
        ), AutoMigration(from = 3, to = 4), AutoMigration(from = 2, to = 4), AutoMigration(
            from = 3,
            to = 5,
        ), AutoMigration(4, 5), AutoMigration(6, 7), AutoMigration(
            7,
            8,
            spec = AutoMigration7_8::class,
        ), AutoMigration(8, 9),
        AutoMigration(9, 10),
        AutoMigration(from = 11, to = 12, spec = AutoMigration11_12::class),
        AutoMigration(13, 14),
        AutoMigration(14, 15),
        AutoMigration(15, 16),
        AutoMigration(16, 17),
        AutoMigration(17, 18),
        AutoMigration(16, 18),
        AutoMigration(15, 18),
        AutoMigration(18, 19),
        AutoMigration(17, 19),
        AutoMigration(16, 19),
        AutoMigration(19, 20),
        AutoMigration(18, 20),
        AutoMigration(17, 20),
        AutoMigration(20, 21),
        AutoMigration(19, 21),
        AutoMigration(18, 21),
        AutoMigration(21, 22),
        AutoMigration(20, 22),
        AutoMigration(19, 22),
        AutoMigration(22, 23),
        AutoMigration(21, 23),
        AutoMigration(20, 23),
        AutoMigration(23, 24),
        AutoMigration(22, 24),
        AutoMigration(21, 24),
    ],
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): DatabaseDao
}

expect fun getDatabaseBuilder(converters: Converters): RoomDatabase.Builder<MusicDatabase>

expect fun getDatabasePath(): String