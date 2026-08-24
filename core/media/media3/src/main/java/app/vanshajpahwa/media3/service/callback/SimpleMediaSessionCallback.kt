package app.vanshajpahwa.media3.service.callback

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_GET_TIMELINE
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.common.MEDIA_CUSTOM_COMMAND
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import app.vanshajpahwa.domain.mediaservice.handler.PlayerEvent
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.repository.HomeRepository
import app.vanshajpahwa.domain.repository.LocalPlaylistRepository
import app.vanshajpahwa.domain.repository.PlaylistRepository
import app.vanshajpahwa.domain.repository.SearchRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.repository.StreamRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toArrayListTrack
import app.vanshajpahwa.domain.utils.toListName
import app.vanshajpahwa.domain.utils.toListTrack
import app.vanshajpahwa.domain.utils.toPlaylistEntity
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.media3.R
import app.vanshajpahwa.media3.extension.toMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch

private const val TAG = "AndroidAuto"

@UnstableApi
internal class SimpleMediaSessionCallback(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaPlayerHandler: MediaPlayerHandler,
    private val searchRepository: SearchRepository,
    private val songRepository: SongRepository,
    private val localPlaylistRepository: LocalPlaylistRepository,
    private val playlistRepository: PlaylistRepository,
    private val homeRepository: HomeRepository,
    private val streamRepository: StreamRepository,
) : MediaLibrarySession.Callback {
    var toggleLike: () -> Unit = {
        mediaPlayerHandler.toggleLike()
    }
    var toggleRadio: () -> Unit = {
        mediaPlayerHandler.toggleRadio()
    }
    private val searchTempList = mutableListOf<Track>()
    private val listHomeItem = mutableListOf<HomeItem>()

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        Logger.w(TAG, "onConnect: ${controller.packageName}")
        val sessionCommands =
            MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS
                .buildUpon()
                // Add custom commands
                .add(SessionCommand(MEDIA_CUSTOM_COMMAND.LIKE, Bundle()))
                .add(SessionCommand(MEDIA_CUSTOM_COMMAND.REPEAT, Bundle()))
                .add(SessionCommand(MEDIA_CUSTOM_COMMAND.RADIO, Bundle()))
                .add(SessionCommand(MEDIA_CUSTOM_COMMAND.SHUFFLE, Bundle()))
                .add(SessionCommand(MEDIA_CUSTOM_COMMAND.GET_PLATFORM_TOKEN, Bundle()))
                .build()
        return MediaSession.ConnectionResult
            .AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setAvailablePlayerCommands(
                Player.Commands
                    .Builder()
                    .addAllCommands()
                    .remove(COMMAND_GET_TIMELINE)
                    .build(),
            ).build()
    }

    override fun onPlayerCommandRequest(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        playerCommand: Int,
    ): Int {
        Logger.w(TAG, "Player Command $playerCommand")
        scope.launch {
            when (playerCommand) {
                COMMAND_SEEK_TO_NEXT -> {
                    mediaPlayerHandler.onPlayerEvent(PlayerEvent.Next)
                }
                COMMAND_SEEK_TO_PREVIOUS -> {
                    mediaPlayerHandler.onPlayerEvent(PlayerEvent.Previous)
                }
                COMMAND_GET_TIMELINE -> {
                }
            }
        }
        return super.onPlayerCommandRequest(session, controller, playerCommand)
    }

    @UnstableApi
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            MEDIA_CUSTOM_COMMAND.LIKE -> {
                toggleLike()
            }

            MEDIA_CUSTOM_COMMAND.REPEAT -> {
                session.player.repeatMode =
                    when (session.player.repeatMode) {
                        ExoPlayer.REPEAT_MODE_OFF -> ExoPlayer.REPEAT_MODE_ONE
                        ExoPlayer.REPEAT_MODE_ONE -> ExoPlayer.REPEAT_MODE_ALL
                        ExoPlayer.REPEAT_MODE_ALL -> ExoPlayer.REPEAT_MODE_OFF
                        else -> ExoPlayer.REPEAT_MODE_OFF
                    }
            }

            MEDIA_CUSTOM_COMMAND.RADIO -> {
                toggleRadio()
            }

            MEDIA_CUSTOM_COMMAND.SHUFFLE -> {
                scope.launch {
                    mediaPlayerHandler.onPlayerEvent(PlayerEvent.Shuffle)
                }
            }

            MEDIA_CUSTOM_COMMAND.GET_PLATFORM_TOKEN -> {
                return Futures.immediateFuture(
                    SessionResult(
                        SessionResult.RESULT_SUCCESS,
                        Bundle().apply {
                            putParcelable(MEDIA_CUSTOM_COMMAND.KEY_PLATFORM_TOKEN, session.platformToken)
                        },
                    ),
                )
            }
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    /**
     * Root hints. Android Auto reads these from the *root* only — the same keys attached to a
     * children result are ignored, which is why search support moved here from [onGetChildren].
     *
     * The two content-style hints are what decide grid versus list. Without them the host defaults
     * everything to a list, which is what made Home a column of text rows. Browsable nodes
     * (playlists, albums) become artwork tiles; playable nodes (songs) stay rows, because a grid of
     * song titles is harder to scan at a glance than a list with a thumbnail.
     */
    private fun rootHints() =
        Bundle().apply {
            putBoolean(MEDIA_SEARCH_SUPPORTED, true)
            putBoolean(CONTENT_STYLE_SUPPORTED, true)
            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID_ITEM)
            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST_ITEM)
        }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        Futures.immediateFuture(
            LibraryResult.ofItem(
                MediaItem
                    .Builder()
                    .setMediaId(ROOT)
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setIsPlayable(false)
                            .setIsBrowsable(false)
                            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                            .build(),
                    ).build(),
                MediaLibraryService.LibraryParams
                    .Builder()
                    .setExtras(rootHints())
                    .build(),
            ),
        )

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<Void>> =
        scope.future(Dispatchers.IO) {
            val searchResult =
                searchRepository.getSearchDataSong(query).lastOrNull()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let {
                                searchTempList.clear()
                                searchTempList.addAll(it.toListTrack())
                            }
                            resource.data
                        }

                        else -> {
                            emptyList()
                        }
                    }
                }
            if (searchResult != null) {
                session.notifySearchResultChanged(browser, query, searchResult.size, params)
            }
            LibraryResult.ofVoid()
        }

    @UnstableApi
    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        scope.future(Dispatchers.IO) {
            LibraryResult.ofItemList(
                searchTempList.map {
                    it.toMediaItemWithoutPath()
                },
                params,
            )
        }

    @UnstableApi
    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        scope.future(Dispatchers.IO) {
            // Hints live on the root (see rootHints); children just echo the caller's params back.
            return@future LibraryResult.ofItemList(
                when (parentId) {
                    ROOT -> {
                        listOf(
                            browsableMediaItem(
                                HOME,
                                context.getString(R.string.home),
                                context.getString(R.string.available_online),
                                drawableUri(R.drawable.home_android_auto),
                                MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
                            ),
                            // Recents earns the second slot: in a car the most likely thing you
                            // want is what you were already listening to.
                            browsableMediaItem(
                                RECENTLY_PLAYED,
                                context.getString(R.string.recently_played),
                                null,
                                drawableUri(R.drawable.baseline_history_24),
                                MediaMetadata.MEDIA_TYPE_PLAYLIST,
                            ),
                            browsableMediaItem(
                                FAVORITE,
                                context.getString(R.string.favorites),
                                null,
                                drawableUri(R.drawable.baseline_favorite_24),
                                MediaMetadata.MEDIA_TYPE_PLAYLIST,
                            ),
                            browsableMediaItem(
                                SONG,
                                context.getString(R.string.songs),
                                null,
                                drawableUri(R.drawable.baseline_album_24),
                                MediaMetadata.MEDIA_TYPE_PLAYLIST,
                            ),
                            browsableMediaItem(
                                DOWNLOADED,
                                context.getString(R.string.downloaded),
                                null,
                                drawableUri(R.drawable.baseline_downloaded),
                                MediaMetadata.MEDIA_TYPE_PLAYLIST,
                            ),
                            browsableMediaItem(
                                MOST_PLAYED,
                                context.getString(R.string.most_played),
                                null,
                                drawableUri(R.drawable.baseline_trending_up_24),
                                MediaMetadata.MEDIA_TYPE_PLAYLIST,
                            ),
                            browsableMediaItem(
                                PLAYLIST,
                                context.getString(R.string.playlists),
                                null,
                                drawableUri(R.drawable.baseline_playlist_add_24),
                                MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS,
                            ),
                        )
                    }

                    SONG -> {
                        songRepository
                            .getAllSongs(1000)
                            .last()
                            .sortedBy { it.inLibrary }
                            .map { it.toMediaItem(parentId) }
                    }

                    FAVORITE -> {
                        songRepository
                            .getLikedSongs()
                            .first()
                            .map { it.toMediaItem(parentId) }
                    }

                    // Newest first, capped: a driver scrolling a thousand rows is the problem
                    // this tab exists to avoid.
                    RECENTLY_PLAYED -> {
                        songRepository
                            .getRecentSongsAsFlow(RECENTS_LIMIT)
                            .first()
                            .map { it.toMediaItem(parentId) }
                    }

                    DOWNLOADED -> {
                        songRepository
                            .getDownloadedSongs()
                            .first()
                            ?.map { it.toMediaItem(parentId) }
                            ?: emptyList()
                    }

                    // Ordered by play time, matching what the Most played list shows in the app.
                    MOST_PLAYED -> {
                        songRepository
                            .getMostPlayedSongs()
                            .first()
                            .sortedByDescending { it.totalPlayTime }
                            .map { it.toMediaItem(parentId) }
                    }

                    PLAYLIST -> {
                        localPlaylistRepository
                            .getAllLocalPlaylists()
                            .first()
                            .sortedBy { it.inLibrary }
                            .map {
                                browsableMediaItem(
                                    "$PLAYLIST/${it.id}",
                                    it.title,
                                    "${it.tracks?.size ?: 0} ${context.getString(R.string.track)}",
                                    it.thumbnail?.toUri(),
                                    MediaMetadata.MEDIA_TYPE_PLAYLIST,
                                )
                            }
                    }

                    HOME -> {
                        val temp =
                            homeRepository
                                .getHomeData(
                                    viewString = context.getString(R.string.view_count),
                                    songString = context.getString(R.string.song),
                                ).lastOrNull()
                                ?.data
                        listHomeItem.clear()
                        listHomeItem.addAll(temp?.second ?: emptyList())
                        // Stop as soon as the grid can be filled.
                        //
                        // This loop used to run to exhaustion: every continuation page of the home
                        // feed, fetched sequentially over the network, before onGetChildren
                        // returned anything. The result was then cut to HOME_TILE_LIMIT tiles, so
                        // all but the first page or two was downloaded purely to be thrown away.
                        // Opening the Home tab in a car paid for the whole feed to see twelve
                        // covers. The page cap is a second belt: a feed that never yields enough
                        // tiles must still not paginate forever.
                        if (!temp?.first.isNullOrEmpty()) {
                            var continueParam = temp.first
                            var pages = 0
                            while (continueParam != null &&
                                pages < HOME_MAX_CONTINUATION_PAGES &&
                                homeTileCount() < HOME_TILE_LIMIT
                            ) {
                                pages++
                                homeRepository
                                    .getHomeDataContinue(
                                        continueParam,
                                        viewString = context.getString(R.string.view_count),
                                        songString = context.getString(R.string.song),
                                    ).lastOrNull()
                                    .let {
                                        listHomeItem.addAll(it?.data?.second ?: emptyList())
                                        continueParam = it?.data?.first
                                    }
                            }
                        }
                        // Flattened one level on purpose. Returning the shelves themselves gave a
                        // column of category names with no artwork, which is the worst possible
                        // input to a grid: every tile would be an empty square. The playlists
                        // inside them have real covers and are what the user actually wants to
                        // open, so Home leads with those instead. Ids keep the same shape the
                        // nested branch already resolves, so no new lookup path is introduced.
                        listHomeItem
                            .flatMap { shelf ->
                                shelf.contents.filterNotNull().mapNotNull { content ->
                                    val playlistId = content.playlistId ?: return@mapNotNull null
                                    val art =
                                        content.thumbnails
                                            .lastOrNull()
                                            ?.url
                                            ?.toUri()
                                            ?: return@mapNotNull null
                                    browsableMediaItem(
                                        "$HOME/${shelf.title}/$PLAYLIST/$playlistId",
                                        content.title,
                                        content.description ?: shelf.title,
                                        art,
                                        MediaMetadata.MEDIA_TYPE_PLAYLIST,
                                    )
                                }
                            // Driver-distraction guidance caps what is worth showing before the
                            // host locks scrolling above speed; beyond this nobody reaches them.
                            }.distinctBy { it.mediaId }
                            .take(HOME_TILE_LIMIT)
                    }

                    else -> {
                        when {
                            parentId.startsWith("$HOME/") -> {
                                if (parentId.split("/").size == 2) {
                                    val homeItem =
                                        listHomeItem.find {
                                            it.title == parentId.split("/").getOrNull(1)
                                        }
                                    homeItem
                                        ?.contents
                                        ?.filter { it?.playlistId != null || it?.videoId != null }
                                        ?.mapNotNull {
                                            if (it?.playlistId != null) {
                                                browsableMediaItem(
                                                    id = "$HOME/${homeItem.title}/$PLAYLIST/${it.playlistId}",
                                                    title = it.title,
                                                    subtitle = it.description,
                                                    iconUri =
                                                        it.thumbnails
                                                            .lastOrNull()
                                                            ?.url
                                                            ?.toUri(),
                                                    mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
                                                )
                                            } else if (it?.videoId != null) {
                                                it
                                                    .toTrack()
                                                    .toSongEntity()
                                                    .toMediaItem("$HOME/${homeItem.title}/$SONG")
                                            } else {
                                                null
                                            }
                                        }
                                        ?: emptyList()
                                } else {
                                    val playlistId = parentId.split("/").getOrNull(3)
                                    if (playlistId != null) {
                                        val playlist =
                                            playlistRepository
                                                .getFullPlaylistData(playlistId, context.getString(R.string.view_count))
                                                .lastOrNull()
                                        if (playlist?.data?.tracks.isNullOrEmpty()) {
                                            emptyList()
                                        } else {
                                            playlist.data?.toPlaylistEntity()?.let { playlistRepository.insertAndReplacePlaylist(it) }
                                            // One transaction for the playlist, not one commit per
                                            // track awaited in turn. The car waited on every one of
                                            // those before it was handed a single row to draw.
                                            val entities = playlist.data?.tracks?.map { it.toSongEntity() }.orEmpty()
                                            songRepository.insertSongs(entities)
                                            entities.map { it.toMediaItem(parentId) }
                                        }
                                    } else {
                                        emptyList()
                                    }
                                }
                            }

                            parentId.startsWith("$PLAYLIST/") -> {
                                val playlistId = parentId.split("/").getOrNull(1)
                                if (playlistId != null) {
                                    val playlist =
                                        localPlaylistRepository.getLocalPlaylist(playlistId.toLong()).lastOrNull()?.data
                                    if (playlist != null) {
                                        Logger.w(TAG, "onGetChildren: $playlist")
                                        val tracks = playlist.tracks
                                        if (tracks.isNullOrEmpty()) {
                                            emptyList()
                                        } else {
                                            songRepository
                                                .getSongsByListVideoId(tracks)
                                                .lastOrNull()
                                                ?.map { it.toMediaItem(parentId) } ?: emptyList()
                                        }
                                    } else {
                                        emptyList()
                                    }
                                } else {
                                    emptyList()
                                }
                            }

                            else -> {
                                emptyList()
                            }
                        }
                    }
                },
                params,
            )
        }

    @UnstableApi
    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        scope.future(Dispatchers.IO) {
            songRepository.getSongById(mediaId).first()?.let {
                LibraryResult.ofItem(it.toMediaItem(), null)
            } ?: streamRepository.getFullMetadata(mediaId).lastOrNull()?.data?.let {
                LibraryResult.ofItem(it.toMediaItemWithoutPath(), null)
            } ?: LibraryResult.ofError(SessionError.ERROR_UNKNOWN)
        }

    @UnstableApi
    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long,
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> =
        scope.future {
            // Play from Android Auto
            val defaultResult =
                MediaSession.MediaItemsWithStartPosition(emptyList(), startIndex, startPositionMs)
            val path =
                mediaItems.firstOrNull()?.mediaId?.split("/")
                    ?: return@future defaultResult
            when (path.firstOrNull()) {
                SONG -> {
                    playSingleTrack(path.getOrNull(1))
                    defaultResult
                }

                FAVORITE -> {
                    playFromLocalList(
                        songs = songRepository.getLikedSongs().first(),
                        songId = path.getOrNull(1),
                        playlistName = context.getString(R.string.favorites),
                    )
                    defaultResult
                }

                RECENTLY_PLAYED -> {
                    playFromLocalList(
                        // Same query and same cap as the browse tree, so the queue is the list the
                        // driver is looking at rather than a different slice of it.
                        songs = songRepository.getRecentSongsAsFlow(RECENTS_LIMIT).first(),
                        songId = path.getOrNull(1),
                        playlistName = context.getString(R.string.recently_played),
                    )
                    defaultResult
                }

                DOWNLOADED -> {
                    playFromLocalList(
                        songs = songRepository.getDownloadedSongs().first().orEmpty(),
                        songId = path.getOrNull(1),
                        playlistName = context.getString(R.string.downloaded),
                    )
                    defaultResult
                }

                MOST_PLAYED -> {
                    playFromLocalList(
                        songs =
                            songRepository
                                .getMostPlayedSongs()
                                .first()
                                .sortedByDescending { it.totalPlayTime },
                        songId = path.getOrNull(1),
                        playlistName = context.getString(R.string.most_played),
                    )
                    defaultResult
                }

                PLAYLIST -> {
                    val songId = path.getOrNull(2) ?: return@future defaultResult
                    val playlistId = path.getOrNull(1) ?: return@future defaultResult
                    Logger.d(TAG, "onSetMediaItems playlistId: $playlistId")
                    var title = ""
                    val songs =
                        localPlaylistRepository
                            .getLocalPlaylist(playlistId.toLong())
                            .lastOrNull()
                            ?.data
                            ?.also {
                                title = it.title
                            }?.tracks
                            ?.let {
                                songRepository.getSongsByListVideoId(it)
                            }?.lastOrNull()
                    Logger.w(TAG, "onSetMediaItems songs: $songs")
                    if (songs.isNullOrEmpty()) {
                        defaultResult
                    } else {
                        var index = 0
                        val clickedSong =
                            songs
                                .firstOrNull { it.videoId == songId }
                                ?.also {
                                    index = songs.indexOf(it)
                                }?.toTrack()
                                ?: run {
                                    playSingleTrack(songId)
                                    return@future defaultResult
                                }
                        mediaPlayerHandler.setQueueData(
                            QueueData.Data(
                                listTracks = songs.toArrayListTrack(),
                                firstPlayedTrack = clickedSong,
                                playlistId = playlistId,
                                playlistName = "${
                                    context.getString(
                                        R.string.playlists,
                                    )
                                } \"${title}\"",
                                playlistType = PlaylistType.LOCAL_PLAYLIST,
                                continuation = null,
                            ),
                        )
                        mediaPlayerHandler.loadMediaItem(
                            clickedSong,
                            Config.PLAYLIST_CLICK,
                            index,
                        )
                        defaultResult
                    }
                }

                HOME -> {
                    val type = path.getOrNull(2) ?: return@future defaultResult
                    val content = listHomeItem.find { it.title == path.getOrNull(1) }?.contents
                    if (type == SONG) {
                        val songId = path.getOrNull(3) ?: return@future defaultResult
                        val songs =
                            content?.filter { it?.videoId != null }?.mapNotNull {
                                it?.toTrack()
                            }
                        if (songs.isNullOrEmpty()) {
                            defaultResult
                        } else {
                            // Off the critical path on purpose. loadMediaItem inserts the clicked
                            // track itself, so nothing below needs the rest of the shelf on disk
                            // first; awaiting a write per song only delayed audio starting.
                            scope.launch {
                                songRepository.insertSongs(songs.map { it.toSongEntity() })
                            }
                            val firstQueue =
                                songs.firstOrNull { it.videoId == songId }
                                    ?: run {
                                        playSingleTrack(songId)
                                        return@future defaultResult
                                    }
                            mediaPlayerHandler.setQueueData(
                                QueueData.Data(
                                    listTracks = songs,
                                    firstPlayedTrack = firstQueue,
                                    playlistId = "RDAMVM$songId",
                                    playlistName = "\"${firstQueue.title}\" Radio",
                                    playlistType = PlaylistType.RADIO,
                                    continuation = null,
                                ),
                            )
                            mediaPlayerHandler.loadMediaItem(
                                firstQueue,
                                Config.SONG_CLICK,
                                0,
                            )
                            defaultResult
                        }
                    } else if (type == PLAYLIST) {
                        val songId = path.getOrNull(4) ?: return@future defaultResult
                        val playlistId = path.getOrNull(3) ?: return@future defaultResult
                        Logger.d(TAG, "onSetMediaItems playlistId: $playlistId")
                        val playlistEntity = playlistRepository.getPlaylist(playlistId).first()
                        Logger.w(TAG, "onSetMediaItems playlistEntity: $playlistEntity")
                        val tracks = playlistEntity?.tracks
                        if (tracks.isNullOrEmpty()) {
                            defaultResult
                        } else if (tracks.isNotEmpty()) {
                            val songs =
                                tracks
                                    .let {
                                        songRepository
                                            .getSongsByListVideoId(tracks)
                                            .first()
                                            .sortedBy {
                                                tracks.indexOf(it.videoId)
                                            }.also {
                                                Logger.w(TAG, "onSetMediaItems list songs: $it")
                                            }
                                    }
                            var index = 0
                            val clickedSong =
                                songs
                                    .firstOrNull { it.videoId == songId }
                                    ?.also {
                                        index = songs.indexOf(it)
                                    }?.toTrack()
                                    ?: run {
                                        playSingleTrack(songId)
                                        return@future defaultResult
                                    }
                            mediaPlayerHandler.setQueueData(
                                QueueData.Data(
                                    listTracks = songs.toArrayListTrack(),
                                    firstPlayedTrack = clickedSong,
                                    playlistId = playlistId,
                                    playlistName = "${
                                        context.getString(
                                            R.string.playlists,
                                        )
                                    } \"${playlistEntity.title}\"",
                                    playlistType = PlaylistType.LOCAL_PLAYLIST,
                                    continuation = null,
                                ),
                            )
                            mediaPlayerHandler.loadMediaItem(
                                clickedSong,
                                Config.PLAYLIST_CLICK,
                                index,
                            )
                            defaultResult
                        } else {
                            defaultResult
                        }
                    } else {
                        // Unrecognised home node type. Still play the tapped track rather than
                        // letting an empty list resume the previous one.
                        playSingleTrack(path.lastOrNull())
                        defaultResult
                    }
                }

                // Unknown prefix. Never return the empty list on its own: the host reads that as
                // "leave the queue alone", and the play() that follows resumes the previous song.
                else -> {
                    playSingleTrack(path.lastOrNull())
                    defaultResult
                }
            }
        }

    /**
     * Play [songId] out of a flat local list, using the rest of that list as the queue.
     *
     * Favourites, Downloaded, Most played and Recents are one operation over four different
     * queries, and they were written out four times. Recents was the copy nobody made, which is the
     * whole of the wrong song bug: with no arm to match, a Recents tap fell through to else.
     *
     * Falls back to the single track when the id is not in the list, so a stale browse row still
     * plays what the driver tapped instead of resuming something else.
     */
    private suspend fun playFromLocalList(
        songs: List<SongEntity>,
        songId: String?,
        playlistName: String,
    ): Boolean {
        if (songId == null) return false
        // indexOfFirst, not firstOrNull then indexOf. indexOf matches by equality, so a list holding
        // two equal rows resolved the click to the earlier one and played the wrong index.
        val index = songs.indexOfFirst { it.videoId == songId }
        if (index < 0) return playSingleTrack(songId)
        val clickedSong = songs[index].toTrack()
        mediaPlayerHandler.setQueueData(
            QueueData.Data(
                listTracks = songs.toArrayListTrack(),
                firstPlayedTrack = clickedSong,
                playlistId = null,
                playlistName = playlistName,
                playlistType = PlaylistType.LOCAL_PLAYLIST,
                continuation = null,
            ),
        )
        mediaPlayerHandler.loadMediaItem(clickedSong, Config.PLAYLIST_CLICK, index)
        return true
    }

    /**
     * Last resort: play exactly the track that was tapped, with no surrounding queue.
     *
     * Every playable media id this browse tree emits ends with the video id, whatever the prefix,
     * so the trailing segment always identifies the track on its own. Losing the queue is a real
     * cost, but the alternative is worse: an empty item list leaves the queue untouched and the
     * host's play() then resumes whatever was already loaded, so the driver hears a song that was
     * never on screen. That is how a Recents tap produced an unrelated track.
     */
    private suspend fun playSingleTrack(videoId: String?): Boolean {
        if (videoId.isNullOrBlank()) return false
        val track =
            songRepository.getSongById(videoId).first()?.toTrack()
                ?: searchTempList.firstOrNull { it.videoId == videoId }
                ?: streamRepository.getFullMetadata(videoId).lastOrNull()?.data
                ?: return false
        mediaPlayerHandler.setQueueData(
            QueueData.Data(
                listTracks = arrayListOf(track),
                firstPlayedTrack = track,
                playlistId = "RDAMVM$videoId",
                playlistName = "\"${track.title}\" Radio",
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        mediaPlayerHandler.loadMediaItem(track, Config.SONG_CLICK, 0)
        return true
    }

    /**
     * How many tiles the flattened home grid could build right now.
     *
     * Mirrors the filter the HOME branch applies: a tile needs a playlist id and a cover, and
     * duplicates across shelves collapse. Used to decide when paginating further is pointless.
     */
    private fun homeTileCount(): Int =
        listHomeItem
            .flatMap { shelf -> shelf.contents.filterNotNull() }
            .filter { it.playlistId != null && it.thumbnails.lastOrNull()?.url != null }
            .distinctBy { it.playlistId }
            .size

    private fun drawableUri(
        @DrawableRes id: Int,
    ) = Uri
        .Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(id))
        .appendPath(context.resources.getResourceTypeName(id))
        .appendPath(context.resources.getResourceEntryName(id))
        .build()

    private fun browsableMediaItem(
        id: String,
        title: String,
        subtitle: String?,
        iconUri: Uri?,
        mediaType: Int = MediaMetadata.MEDIA_TYPE_MUSIC,
    ) = MediaItem
        .Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setArtist(subtitle)
                .setArtworkUri(iconUri)
                .setIsPlayable(false)
                .setIsBrowsable(true)
                .setMediaType(mediaType)
                .build(),
        ).build()

    private fun SongEntity.toMediaItem(path: String) =
        MediaItem
            .Builder()
            .setMediaId("$path/${this.videoId}")
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setTitle(this.title)
                    .setSubtitle(this.artistName?.joinToString(", "))
                    .setArtist(this.artistName?.joinToString(" "))
                    .setArtworkUri(this.thumbnails?.toUri())
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .build(),
            ).build()

    private fun Track.toMediaItemWithoutPath(path: String? = SONG) =
        MediaItem
            .Builder()
            .setMediaId(if (path == null) this.videoId else "$path/${this.videoId}")
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setTitle(this.title)
                    .setSubtitle(this.artists?.toListName()?.connectArtists())
                    .setArtist(this.artists?.toListName()?.connectArtists())
                    .setArtworkUri(
                        this.thumbnails
                            ?.lastOrNull()
                            ?.url
                            ?.toUri(),
                    ).setIsPlayable(true)
                    .setIsBrowsable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .build(),
            ).build()

    companion object {
        const val ROOT = "root"
        const val SONG = "song"
        const val HOME = "home"
        const val ONLINE_PLAYLIST = "online_playlist"
        const val PLAYLIST = "playlist"
        const val FAVORITE = "favorite"
        const val DOWNLOADED = "downloaded"
        const val MOST_PLAYED = "most_played"
        const val RECENTLY_PLAYED = "recently_played"

        /** Tiles on the Auto home grid. Four across, three rows, before the host stops scrolling. */
        const val HOME_TILE_LIMIT = 12

        /** Enough to find something, short enough that a driver is not scrolling for it. */
        const val RECENTS_LIMIT = 50

        /**
         * Ceiling on home feed continuation pages fetched to fill the grid.
         *
         * Only reached when a feed is unusually thin on playlist shelves; normally the tile count
         * satisfies HOME_TILE_LIMIT within a page or two and the loop stops on its own.
         */
        const val HOME_MAX_CONTINUATION_PAGES = 3

        const val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"

        // Android Auto content style. These keys are part of the media browse contract rather than
        // a media3 API, so they are spelled out here instead of imported.
        const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
        const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
        const val CONTENT_STYLE_LIST_ITEM = 1
        const val CONTENT_STYLE_GRID_ITEM = 2
    }
}