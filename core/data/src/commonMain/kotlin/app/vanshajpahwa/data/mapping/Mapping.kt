package app.vanshajpahwa.data.mapping

import app.vanshajpahwa.data.parser.toListThumbnail
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.canvas.CanvasResult
import app.vanshajpahwa.domain.data.model.mediaService.SponsorSkipSegments
import app.vanshajpahwa.domain.data.model.metadata.Line
import app.vanshajpahwa.domain.data.model.metadata.Lyrics
import app.vanshajpahwa.domain.data.model.searchResult.albums.AlbumsResult
import app.vanshajpahwa.domain.data.model.searchResult.artists.ArtistsResult
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Album
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.data.model.searchResult.songs.SongsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.model.searchResult.videos.VideosResult
import app.vanshajpahwa.domain.data.model.streams.YouTubeWatchEndpoint
import app.vanshajpahwa.kotlinytmusicscraper.models.AccountInfo
import app.vanshajpahwa.kotlinytmusicscraper.models.AlbumItem
import app.vanshajpahwa.kotlinytmusicscraper.models.ArtistItem
import app.vanshajpahwa.kotlinytmusicscraper.models.PlaylistItem
import app.vanshajpahwa.kotlinytmusicscraper.models.SearchSuggestions
import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem
import app.vanshajpahwa.kotlinytmusicscraper.models.VideoItem
import app.vanshajpahwa.kotlinytmusicscraper.models.WatchEndpoint
import app.vanshajpahwa.kotlinytmusicscraper.models.response.PipedResponse
import app.vanshajpahwa.kotlinytmusicscraper.models.sponsorblock.SkipSegments
import app.vanshajpahwa.kotlinytmusicscraper.models.youtube.Transcript
import app.vanshajpahwa.kotlinytmusicscraper.models.youtube.YouTubeInitialPage
import app.vanshajpahwa.spotify.model.response.spotify.CanvasResponse
import app.vanshajpahwa.spotify.model.response.spotify.SpotifyLyricsResponse
import app.vanshajpahwa.lyrics.models.response.LyricsResponse
import app.vanshajpahwa.lyrics.models.response.TranslatedLyricsResponse
import app.vanshajpahwa.lyrics.parser.parseRichSyncLyrics
import app.vanshajpahwa.lyrics.parser.parseSyncedLyrics
import app.vanshajpahwa.lyrics.parser.parseUnsyncedLyrics
import kotlin.jvm.JvmName

internal fun SongItem.toTrack(): Track =
    Track(
        album = this.album.let { Album(it?.id ?: "", it?.name ?: "") },
        artists = this.artists.map { artist -> Artist(id = artist.id ?: "", name = artist.name) },
        duration = this.duration.toString(),
        durationSeconds = this.duration,
        isAvailable = false,
        isExplicit = this.explicit,
        likeStatus = null,
        thumbnails = this.thumbnails?.thumbnails?.toListThumbnail() ?: listOf(),
        title = this.title,
        videoId = this.id,
        videoType = null,
        category = null,
        feedbackTokens = null,
        resultType = null,
        year = null,
    )

internal fun VideoItem.toTrack(): Track =
    Track(
        album = this.album.let { Album(it?.id ?: "", it?.name ?: "") },
        artists = this.artists.map { artist -> Artist(id = artist.id ?: "", name = artist.name) },
        duration = this.duration.toString(),
        durationSeconds = this.duration,
        isAvailable = false,
        isExplicit = false,
        likeStatus = null,
        thumbnails = this.thumbnails?.thumbnails?.toListThumbnail() ?: listOf(),
        title = this.title,
        videoId = this.id,
        videoType = null,
        category = null,
        feedbackTokens = null,
        resultType = null,
        year = null,
    )

@JvmName("SongItemtoTrack")
internal fun List<SongItem>?.toListTrack(): ArrayList<Track> {
    val listTrack = arrayListOf<Track>()
    if (this != null) {
        for (item in this) {
            listTrack.add(item.toTrack())
        }
    }
    return listTrack
}

internal fun Track.toSongItemForDownload(): SongItem =
    SongItem(
        id = this.videoId,
        title = this.title,
        artists =
            this.artists?.map {
                app.vanshajpahwa.kotlinytmusicscraper.models.Artist(
                    id = it.id ?: "",
                    name = it.name,
                )
            } ?: emptyList(),
        album =
            app.vanshajpahwa.kotlinytmusicscraper.models.Album(
                id = this.album?.id ?: "",
                name = this.album?.name ?: "",
            ),
        duration = this.durationSeconds,
        thumbnail = this.thumbnails?.lastOrNull()?.url ?: "",
        explicit = this.isExplicit,
    )

internal fun app.vanshajpahwa.lyrics.domain.Lyrics.toLyrics(): Lyrics {
    val lines: ArrayList<Line> = arrayListOf()
    if (this.lyrics != null) {
        this.lyrics?.lines?.forEach {
            lines.add(
                Line(
                    endTimeMs = it.endTimeMs,
                    startTimeMs = it.startTimeMs,
                    syllables = it.syllables ?: listOf(),
                    words = it.words,
                ),
            )
        }
        return Lyrics(
            error = false,
            lines = lines,
            syncType = this.lyrics!!.syncType,
        )
    } else {
        return Lyrics(
            error = true,
            lines = null,
            syncType = null,
        )
    }
}

internal fun Lyrics.toLibraryLyrics(): app.vanshajpahwa.lyrics.domain.Lyrics =
    app.vanshajpahwa.lyrics.domain.Lyrics(
        lyrics =
            app.vanshajpahwa.lyrics.domain.Lyrics.LyricsX(
                lines =
                    this.lines?.map {
                        app.vanshajpahwa.lyrics.domain.Lyrics.LyricsX.Line(
                            endTimeMs = it.endTimeMs,
                            startTimeMs = it.startTimeMs,
                            syllables = listOf(),
                            words = it.words,
                        )
                    },
                syncType = this.syncType,
            ),
    )

internal fun SpotifyLyricsResponse.toLyrics(): Lyrics {
    val lines: ArrayList<Line> = arrayListOf()
    this.lyrics.lines.forEach {
        lines.add(
            Line(
                endTimeMs = it.endTimeMs,
                startTimeMs = it.startTimeMs,
                syllables = listOf(),
                words = it.words,
            ),
        )
    }
    return Lyrics(
        error = false,
        lines = lines,
        syncType = this.lyrics.syncType,
    )
}

internal fun PipedResponse.toTrack(videoId: String): Track =
    Track(
        album = null,
        artists =
            listOf(
                Artist(
                    this.uploaderUrl?.replace("/channel/", ""),
                    this.uploader.toString(),
                ),
            ),
        duration = "",
        durationSeconds = 0,
        isAvailable = false,
        isExplicit = false,
        likeStatus = "INDIFFERENT",
        thumbnails =
            listOf(
                Thumbnail(
                    720,
                    this.thumbnailUrl ?: "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg",
                    1080,
                ),
            ),
        title = this.title ?: " ",
        videoId = videoId,
        videoType = "Song",
        category = "",
        feedbackTokens = null,
        resultType = null,
        year = "",
    )

internal fun YouTubeInitialPage.toTrack(): Track {
    val initialPage = this

    return Track(
        album = null,
        artists =
            listOf(
                Artist(
                    name = initialPage.videoDetails?.author ?: "",
                    id = initialPage.videoDetails?.channelId,
                ),
            ),
        duration = initialPage.videoDetails?.lengthSeconds,
        durationSeconds = initialPage.videoDetails?.lengthSeconds?.toInt() ?: 0,
        isAvailable = false,
        isExplicit = false,
        likeStatus = null,
        thumbnails =
            initialPage.videoDetails
                ?.thumbnail
                ?.thumbnails
                ?.toListThumbnail() ?: listOf(),
        title = initialPage.videoDetails?.title ?: "",
        videoId = initialPage.videoDetails?.videoId ?: "",
        videoType = "",
        category = "",
        feedbackTokens = null,
        resultType = "",
        year = "",
    )
}

internal fun Transcript.toLyrics(): Lyrics {
    val lines =
        this.text.map {
            Line(
                endTimeMs = "0",
                startTimeMs = (it.start.toFloat() * 1000).toInt().toString(),
                syllables = listOf(),
                words = it.content.replace(Regex("<[^>]*>"), ""),
            )
        }
    val sortedLine = lines.sortedBy { it.startTimeMs.toInt() }
    return Lyrics(
        error = false,
        lines = sortedLine,
        syncType = "LINE_SYNCED",
    )
}

internal fun AlbumItem.toAlbumsResult(): AlbumsResult =
    AlbumsResult(
        artists =
            this.artists?.map {
                Artist(
                    id = it.id ?: "",
                    name = it.name,
                )
            } ?: emptyList(),
        browseId = this.id,
        category = this.title,
        duration = "",
        isExplicit = this.explicit,
        resultType = "ALBUM",
        thumbnails =
            listOf(
                Thumbnail(
                    width = 720,
                    url = this.thumbnail,
                    height = 720,
                ),
            ),
        title = this.title,
        type = if (isSingle) "SINGLE" else "ALBUM",
        year = this.year?.toString() ?: "",
    )

// Sonora Lyrics Extension
internal fun LyricsResponse.toLyrics(): Lyrics? =
    (
        richSyncLyrics?.takeIf { it.isNotEmpty() }?.let {
            parseRichSyncLyrics(it)
        }
            ?: syncedLyrics?.let { if (it.isNotEmpty() && it.isNotBlank()) parseSyncedLyrics(it) else null }
            ?: (
                if (plainLyric.isNotEmpty() && plainLyric.isNotBlank()) {
                    parseUnsyncedLyrics(plainLyric)
                } else {
                    null
                }
            )
    )?.toLyrics()

internal fun TranslatedLyricsResponse.toLyrics(): Lyrics = parseSyncedLyrics(this.translatedLyric).toLyrics()

internal fun SearchSuggestions.toDomainSearchSuggestions(): app.vanshajpahwa.domain.data.model.searchResult.SearchSuggestions =
    app.vanshajpahwa.domain.data.model.searchResult.SearchSuggestions(
        queries = this.queries,
        recommendedItems =
            this.recommendedItems.map {
                when (it) {
                    is SongItem -> {
                        SongsResult(
                            album =
                                Album(
                                    id = it.album?.id ?: "",
                                    name = it.album?.name ?: "",
                                ),
                            artists =
                                it.artists.map { artist ->
                                    Artist(
                                        id = artist.id ?: "",
                                        name = artist.name,
                                    )
                                },
                            category = "",
                            duration = it.duration.toString(),
                            durationSeconds = it.duration,
                            feedbackTokens = null,
                            isExplicit = it.explicit,
                            resultType = "Song",
                            thumbnails = it.thumbnails?.thumbnails?.toListThumbnail() ?: listOf(),
                            title = it.title,
                            videoId = it.id,
                            videoType = null,
                            year = "",
                        )
                    }

                    is AlbumItem -> {
                        AlbumsResult(
                            artists =
                                it.artists?.map {
                                    Artist(
                                        id = it.id ?: "",
                                        name = it.name,
                                    )
                                } ?: emptyList(),
                            browseId = it.browseId,
                            category = "",
                            duration = "",
                            isExplicit = it.explicit,
                            resultType = "ALBUM",
                            thumbnails =
                                listOf(
                                    Thumbnail(
                                        width = 720,
                                        url = it.thumbnail,
                                        height = 720,
                                    ),
                                ),
                            title = it.title,
                            type = if (it.isSingle) "SINGLE" else "ALBUM",
                            year = it.year?.toString() ?: "",
                        )
                    }

                    is ArtistItem -> {
                        ArtistsResult(
                            artist = it.title,
                            browseId = it.id,
                            category = "",
                            radioId = it.radioEndpoint?.playlistId ?: "",
                            resultType = "ARTIST",
                            shuffleId = it.shuffleEndpoint?.playlistId ?: "",
                            thumbnails =
                                listOf(
                                    Thumbnail(
                                        width = 720,
                                        url = it.thumbnail,
                                        height = 720,
                                    ),
                                ),
                        )
                    }

                    is PlaylistItem -> {
                        PlaylistsResult(
                            author = it.author?.name ?: "YouTube Music",
                            browseId = it.id,
                            category = "",
                            itemCount = "0",
                            resultType = "PLAYLIST",
                            thumbnails =
                                listOf(
                                    Thumbnail(
                                        width = 720,
                                        url = it.thumbnail,
                                        height = 720,
                                    ),
                                ),
                            title = it.title,
                        )
                    }

                    is VideoItem -> {
                        VideosResult(
                            artists =
                                it.artists.map { artist ->
                                    Artist(
                                        id = artist.id ?: "",
                                        name = artist.name,
                                    )
                                },
                            category = null,
                            duration = it.duration?.toString(),
                            durationSeconds = it.duration,
                            resultType = "VIDEO",
                            thumbnails = it.thumbnails?.thumbnails?.toListThumbnail() ?: listOf(),
                            title = it.title,
                            videoId = it.id,
                            videoType = null,
                            views = it.view,
                            year = "",
                        )
                    }
                }
            },
    )

internal fun CanvasResponse.toCanvasResult(): CanvasResult? {
    val canvasUrl = this.canvases.firstOrNull()?.canvas_url ?: return null
    val canvasThumbs = this.canvases.firstOrNull()?.thumbsOfCanva
    val thumbUrl =
        if (!canvasThumbs.isNullOrEmpty()) {
            (
                canvasThumbs.let { thumb ->
                    thumb
                        .maxByOrNull {
                            (it.height ?: 0) + (it.width ?: 0)
                        }?.url
                } ?: canvasThumbs.first().url
            )
        } else {
            null
        }
    return CanvasResult(
        isVideo = canvasUrl.contains(".mp4"),
        canvasUrl = canvasUrl,
        canvasThumbUrl = thumbUrl,
    )
}

internal fun YouTubeWatchEndpoint.toWatchEndpoint(): WatchEndpoint =
    WatchEndpoint(
        videoId = this.videoId,
        playlistId = this.playlistId,
        playlistSetVideoId = this.playlistSetVideoId,
        params = this.params,
        index = this.index,
        watchEndpointMusicSupportedConfigs =
            this.watchEndpointMusicSupportedConfigs?.let { supportedConfig ->
                WatchEndpoint.WatchEndpointMusicSupportedConfigs(
                    watchEndpointMusicConfig =
                        WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig(
                            musicVideoType =
                                supportedConfig.watchEndpointMusicConfig.musicVideoType,
                        ),
                )
            },
    )

internal fun WatchEndpoint.toYouTubeWatchEndpoint(): YouTubeWatchEndpoint =
    YouTubeWatchEndpoint(
        videoId = this.videoId,
        playlistId = this.playlistId,
        playlistSetVideoId = this.playlistSetVideoId,
        params = this.params,
        index = this.index,
        watchEndpointMusicSupportedConfigs =
            this.watchEndpointMusicSupportedConfigs?.let { supportedConfig ->
                YouTubeWatchEndpoint.WatchEndpointMusicSupportedConfigs(
                    watchEndpointMusicConfig =
                        YouTubeWatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig(
                            musicVideoType =
                                supportedConfig.watchEndpointMusicConfig.musicVideoType,
                        ),
                )
            },
    )

internal fun SkipSegments.toSponsorSkipSegments(): SponsorSkipSegments =
    SponsorSkipSegments(
        actionType = this.actionType,
        category = this.category,
        description = this.description,
        locked = this.locked,
        segment = this.segment,
        uUID = this.uUID,
        videoDuration = this.videoDuration,
        votes = this.votes,
    )

internal fun AccountInfo.toDomainAccountInfo(): app.vanshajpahwa.domain.data.model.account.AccountInfo =
    app.vanshajpahwa.domain.data.model.account.AccountInfo(
        name = this.name,
        email = this.email,
        pageId = this.pageId,
        thumbnails = thumbnails.toListThumbnail(),
    )