package app.vanshajpahwa.domain.data.entities

import app.vanshajpahwa.common.Config
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.vanshajpahwa.domain.data.entities.DownloadState.STATE_NOT_DOWNLOADED
import app.vanshajpahwa.domain.data.type.RecentlyType
import app.vanshajpahwa.domain.extension.now
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "song")
data class SongEntity(
    @PrimaryKey(autoGenerate = false) val videoId: String = "",
    val albumId: String? = null,
    val albumName: String? = null,
    val artistId: List<String>? = null,
    val artistName: List<String>? = null,
    val duration: String,
    val durationSeconds: Int,
    val isAvailable: Boolean,
    val isExplicit: Boolean,
    val likeStatus: String,
    val thumbnails: String? = null,
    val title: String,
    val videoType: String,
    val category: String?,
    val resultType: String?,
    val liked: Boolean = false,
    val totalPlayTime: Long = 0,
    val downloadState: Int = STATE_NOT_DOWNLOADED,
    val favoriteAt: LocalDateTime? = now(),
    val downloadedAt: LocalDateTime? = now(),
    // Deliberately the sentinel, not now(). This column is what "recently played" is ordered by,
    // and defaulting it to the present meant every path that saved a song without thinking about it
    // (import, download a full album, add to a local playlist) announced that song as just played.
    // Playback sets it explicitly through updateSongInLibrary, so the only code that marks a song
    // recent is now the code that actually plays it, and forgetting to override is safe.
    val inLibrary: LocalDateTime = Config.REMOVED_SONG_DATE_TIME,
    val canvasUrl: String? = null,
    val canvasThumbUrl: String? = null,
) : RecentlyType {
    override fun objectType(): RecentlyType.Type = RecentlyType.Type.SONG
}