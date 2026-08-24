package app.vanshajpahwa.domain.repository

import app.vanshajpahwa.domain.data.entities.LyricsEntity
import app.vanshajpahwa.domain.data.entities.TranslatedLyricsEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.browse.artist.ArtistLogo
import app.vanshajpahwa.domain.data.model.canvas.CanvasResult
import app.vanshajpahwa.domain.data.model.metadata.Lyrics
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LyricsCanvasRepository {
    fun getSavedLyrics(videoId: String): Flow<LyricsEntity?>

    suspend fun insertLyrics(lyricsEntity: LyricsEntity)

    suspend fun insertTranslatedLyrics(translatedLyrics: TranslatedLyricsEntity)

    fun getSavedTranslatedLyrics(
        videoId: String,
        language: String,
    ): Flow<TranslatedLyricsEntity?>

    suspend fun removeTranslatedLyrics(
        videoId: String,
        language: String,
    )

    fun getYouTubeCaption(
        preferLang: String,
        videoId: String,
    ): Flow<Resource<Pair<Lyrics, Lyrics?>>>

    fun getCanvas(
        dataStoreManager: DataStoreManager,
        videoId: String,
        duration: Int,
    ): Flow<Resource<CanvasResult>>

    suspend fun updateCanvasUrl(
        videoId: String,
        canvasUrl: String,
    )

    suspend fun updateCanvasThumbUrl(
        videoId: String,
        canvasThumbUrl: String,
    )

    fun getSpotifyLyrics(
        dataStoreManager: DataStoreManager,
        query: String,
        duration: Int?,
    ): Flow<Resource<Lyrics>>

    fun getLrclibLyricsData(
        sartist: String,
        strack: String,
        duration: Int?,
    ): Flow<Resource<Lyrics>>

    fun getBetterLyrics(
        artist: String,
        track: String,
        duration: Int?,
    ): Flow<Resource<Lyrics>>

    /** Fetch the artist's name-logo image + dominant color from the hidden catalog. */
    fun getArtistLogo(artistName: String): Flow<Resource<ArtistLogo>>

    fun getAITranslationLyrics(
        lyrics: Lyrics,
        targetLanguage: String,
    ): Flow<Resource<Lyrics>>

    fun getSonoraLyrics(videoId: String): Flow<Resource<Lyrics>>

    fun getSonoraTranslatedLyrics(
        videoId: String,
        language: String,
    ): Flow<Resource<Lyrics>>

    fun voteSonoraLyrics(
        lyricsId: String,
        upvote: Boolean,
    ): Flow<Resource<String>>

    fun voteSonoraTranslatedLyrics(
        translatedLyricsId: String,
        upvote: Boolean,
    ): Flow<Resource<String>>

    fun insertSonoraLyrics(
        dataStoreManager: DataStoreManager,
        track: Track,
        duration: Int,
        lyrics: Lyrics,
    ): Flow<Resource<String>>

    fun insertSonoraTranslatedLyrics(
        dataStoreManager: DataStoreManager,
        track: Track,
        translatedLyrics: Lyrics,
        language: String,
    ): Flow<Resource<String>>
}