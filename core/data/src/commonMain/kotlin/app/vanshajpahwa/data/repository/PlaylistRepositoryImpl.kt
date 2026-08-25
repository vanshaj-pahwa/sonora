package app.vanshajpahwa.data.repository

import app.vanshajpahwa.data.db.datasource.LocalDataSource
import app.vanshajpahwa.data.extension.getFullDataFromDB
import app.vanshajpahwa.data.mapping.toListTrack
import app.vanshajpahwa.data.mapping.toTrack
import app.vanshajpahwa.data.mapping.toYouTubeWatchEndpoint
import app.vanshajpahwa.data.parser.parseLibraryPlaylist
import app.vanshajpahwa.data.parser.parseNextLibraryPlaylist
import app.vanshajpahwa.data.parser.parsePlaylistData
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.SetVideoIdEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.entities.YourYouTubePlaylistList
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.browse.playlist.Author
import app.vanshajpahwa.domain.data.model.browse.playlist.PlaylistBrowse
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.ChartItem
import app.vanshajpahwa.domain.data.type.PlaylistType
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.repository.PlaylistRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.domain.utils.isRadioMix
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import app.vanshajpahwa.kotlinytmusicscraper.models.MusicShelfRenderer
import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem
import app.vanshajpahwa.kotlinytmusicscraper.models.WatchEndpoint
import app.vanshajpahwa.kotlinytmusicscraper.pages.NextPage
import app.vanshajpahwa.kotlinytmusicscraper.parser.getPlaylistContinuation
import app.vanshajpahwa.kotlinytmusicscraper.parser.getPlaylistRadioEndpoint
import app.vanshajpahwa.kotlinytmusicscraper.parser.getPlaylistShuffleEndpoint
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

internal class PlaylistRepositoryImpl(
    private val dataStoreManager: DataStoreManager,
    private val localDataSource: LocalDataSource,
    private val youTube: YouTube,
) : PlaylistRepository {
    override fun getAllPlaylists(limit: Int): Flow<List<PlaylistEntity>> =
        flow {
            emit(localDataSource.getAllPlaylists(limit))
        }.flowOn(Dispatchers.IO)

    override fun getPlaylist(id: String): Flow<PlaylistEntity?> =
        flow {
            emit(localDataSource.getPlaylist(id))
        }.flowOn(Dispatchers.IO)

    override fun getLikedPlaylists(): Flow<List<PlaylistEntity>> =
        flow {
            emit(
                getFullDataFromDB { limit, offset ->
                    localDataSource.getLikedPlaylists(limit, offset)
                },
            )
        }.flowOn(Dispatchers.IO)

    override suspend fun insertPlaylist(playlistEntity: PlaylistEntity) =
        withContext(Dispatchers.IO) { localDataSource.insertPlaylist(playlistEntity) }

    override suspend fun insertAndReplacePlaylist(playlistEntity: PlaylistEntity) =
        withContext(Dispatchers.IO) {
            val oldPlaylist = getPlaylist(playlistEntity.id).firstOrNull()
            if (oldPlaylist != null) {
                localDataSource.insertAndReplacePlaylist(
                    playlistEntity.copy(
                        downloadState = oldPlaylist.downloadState,
                        liked = oldPlaylist.liked,
                    ),
                )
            } else {
                localDataSource.insertAndReplacePlaylist(playlistEntity)
            }
        }

    override suspend fun insertRadioPlaylist(playlistEntity: PlaylistEntity) =
        withContext(Dispatchers.IO) { localDataSource.insertRadioPlaylist(playlistEntity) }

    override suspend fun updatePlaylistLiked(
        playlistId: String,
        likeStatus: Int,
    ) = withContext(Dispatchers.Main) {
        localDataSource.updatePlaylistLiked(
            likeStatus,
            playlistId,
        )
    }

    override suspend fun updatePlaylistInLibrary(
        inLibrary: LocalDateTime,
        playlistId: String,
    ) = withContext(Dispatchers.Main) {
        localDataSource.updatePlaylistInLibrary(
            inLibrary,
            playlistId,
        )
    }

    override suspend fun updatePlaylistDownloadState(
        playlistId: String,
        downloadState: Int,
    ) = withContext(Dispatchers.Main) {
        localDataSource.updatePlaylistDownloadState(
            downloadState,
            playlistId,
        )
    }

    override fun getAllDownloadedPlaylist(): Flow<List<PlaylistType>> =
        flow { emit(localDataSource.getAllDownloadedPlaylist()) }.flowOn(Dispatchers.IO)

    override fun getAllDownloadingPlaylist(): Flow<List<PlaylistType>> =
        flow { emit(localDataSource.getAllDownloadingPlaylist()) }.flowOn(Dispatchers.IO)

    private suspend fun insertSetVideoId(setVideoId: SetVideoIdEntity) = withContext(Dispatchers.IO) { localDataSource.insertSetVideoId(setVideoId) }

    override fun getRadio(
        radioId: String,
        defaultDescription: String,
        radioString: String,
        viewString: String,
        originalTrack: SongEntity?,
        artist: ArtistEntity?,
    ): Flow<Resource<Pair<PlaylistBrowse, String?>>> =
        if (radioId.isRadioMix()) {
            getRDATRadioData(radioId, viewString)
        } else {
            flow {
                runCatching {
                    youTube
                        .next(endpoint = WatchEndpoint(playlistId = radioId))
                        .onSuccess { next ->
                            Logger.w("Radio", "Title: ${next.title}")
                            val data: ArrayList<SongItem> = arrayListOf()
                            data.addAll(next.items)
                            var continuation = next.continuation
                            Logger.w("Radio", "data: ${data.size}")
                            var count = 0
                            while (continuation != null && count < 3) {
                                youTube
                                    .next(
                                        endpoint = WatchEndpoint(playlistId = radioId),
                                        continuation = continuation,
                                    ).onSuccess { nextContinue ->
                                        data.addAll(nextContinue.items)
                                        continuation = nextContinue.continuation
                                        if (data.size >= 50) {
                                            count = 3
                                        }
                                        Logger.w("Radio", "data: ${data.size}")
                                        count++
                                    }.onFailure {
                                        count = 3
                                    }
                            }
                            val listTrackResult = data.toListTrack()
                            if (originalTrack != null) {
                                listTrackResult.add(0, originalTrack.toTrack())
                            }
                            Logger.w("Repository", "data: ${data.size}")
                            val playlistBrowse =
                                PlaylistBrowse(
                                    author = Author(id = "", name = "YouTube Music"),
                                    description =
                                    defaultDescription,
                                    duration = "",
                                    durationSeconds = 0,
                                    id = radioId,
                                    privacy = "PRIVATE",
                                    thumbnails =
                                        listOf(
                                            Thumbnail(
                                                544,
                                                originalTrack?.thumbnails ?: artist?.thumbnails ?: "",
                                                544,
                                            ),
                                        ),
                                    title = "${originalTrack?.title ?: artist?.name} $radioString",
                                    trackCount = listTrackResult.size,
                                    tracks = listTrackResult,
                                    year = now().year.toString(),
                                )
                            Logger.w("Repository", "playlistBrowse: $playlistBrowse")
                            emit(Resource.Success<Pair<PlaylistBrowse, String?>>(Pair(playlistBrowse, continuation)))
                        }.onFailure { exception ->
                            exception.printStackTrace()
                            emit(Resource.Error<Pair<PlaylistBrowse, String?>>(exception.message.toString()))
                        }
                }
            }.flowOn(Dispatchers.IO)
        }

    override fun getRDATRadioData(
        radioId: String,
        viewString: String,
    ): Flow<Resource<Pair<PlaylistBrowse, String?>>> =
        flow<Resource<Pair<PlaylistBrowse, String?>>> {
            runCatching {
                val id =
                    if (radioId.startsWith("VL")) {
                        radioId
                    } else {
                        "VL$radioId"
                    }
                youTube
                    .customQuery(browseId = id, setLogin = true)
                    .onSuccess { result ->
                        val listContent: ArrayList<MusicShelfRenderer.Content> = arrayListOf()
                        val data: List<MusicShelfRenderer.Content>? =
                            result.contents
                                ?.singleColumnBrowseResultsRenderer
                                ?.tabs
                                ?.get(
                                    0,
                                )?.tabRenderer
                                ?.content
                                ?.sectionListRenderer
                                ?.contents
                                ?.get(
                                    0,
                                )?.musicPlaylistShelfRenderer
                                ?.contents
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.secondaryContents
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicPlaylistShelfRenderer
                                    ?.contents
                        if (data != null) {
                            Logger.d("Data", "data: $data")
                            Logger.d("Data", "data size: ${data.size}")
                            listContent.addAll(data)
                        }
                        val header =
                            result.header?.musicDetailHeaderRenderer
                                ?: result.header?.musicEditablePlaylistDetailHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicResponsiveHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicEditablePlaylistDetailHeaderRenderer
                                    ?.header
                                    ?.musicResponsiveHeaderRenderer
                        Logger.d("Header", "header: $header")
                        val finalContinueParam =
                            result.getPlaylistContinuation()
                        Logger.d("Repository", "playlist data: ${listContent.size}")
                        Logger.d("Repository", "continueParam: $finalContinueParam")
//                        else {
//                            var listTrack = playlistBrowse.tracks.toMutableList()
                        Logger.d("Repository", "playlist final data: ${listContent.size}")
                        if (finalContinueParam != null) {
                            parsePlaylistData(header, listContent, radioId, viewString)?.let { playlist ->
                                emit(
                                    Resource.Success(
                                        Pair(
                                            playlist.copy(
                                                author = Author("", "YouTube Music"),
                                            ),
                                            finalContinueParam,
                                        ),
                                    ),
                                )
                            } ?: emit(Resource.Error("Can't parse data"))
                        } else {
                            emit(Resource.Error("Continue param is null"))
                        }
                    }.onFailure { e ->
                        Logger.e("Playlist Data", e.message ?: "Error")
                        emit(Resource.Error(e.message.toString()))
                    }
            }
        }.flowOn(Dispatchers.IO)

    override fun getFullPlaylistData(
        playlistId: String,
        viewString: String,
    ): Flow<Resource<PlaylistBrowse>> =
        flow {
            runCatching {
                var id = ""
                id +=
                    if (!playlistId.startsWith("VL")) {
                        "VL$playlistId"
                    } else {
                        playlistId
                    }
                Logger.d("getPlaylistData", "playlist id: $id")
                youTube
                    .customQuery(browseId = id, setLogin = true)
                    .onSuccess { result ->
                        val listContent: ArrayList<Track> = arrayListOf()
                        val data: List<MusicShelfRenderer.Content>? =
                            result.contents
                                ?.singleColumnBrowseResultsRenderer
                                ?.tabs
                                ?.get(
                                    0,
                                )?.tabRenderer
                                ?.content
                                ?.sectionListRenderer
                                ?.contents
                                ?.get(
                                    0,
                                )?.musicPlaylistShelfRenderer
                                ?.contents
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.secondaryContents
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicPlaylistShelfRenderer
                                    ?.contents
                        if (data != null) {
                            Logger.d("getPlaylistData", "data: $data")
                            Logger.d("getPlaylistData", "data size: ${data.size}")
                        }
                        val header =
                            result.header?.musicDetailHeaderRenderer
                                ?: result.header?.musicEditablePlaylistDetailHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicResponsiveHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicEditablePlaylistDetailHeaderRenderer
                                    ?.header
                                    ?.musicResponsiveHeaderRenderer
                        Logger.d("getPlaylistData", "header: $header")
                        var continueParam =
                            result.getPlaylistContinuation()
                        var count = 0
                        Logger.d("getPlaylistData", "playlist data: ${listContent.size}")
                        Logger.d("getPlaylistData", "continueParam: $continueParam")
//                        else {
//                            var listTrack = playlistBrowse.tracks.toMutableList()
                        while (continueParam != null) {
                            youTube
                                .customQuery(
                                    browseId = null,
                                    continuation = continueParam,
                                    setLogin = true,
                                ).onSuccess { values ->
                                    Logger.d("getPlaylistData", "continue: $continueParam")
                                    Logger.d(
                                        "getPlaylistData",
                                        "values: ${values.onResponseReceivedActions}",
                                    )
                                    val dataMore: List<SongItem> =
                                        values.onResponseReceivedActions
                                            ?.firstOrNull()
                                            ?.appendContinuationItemsAction
                                            ?.continuationItems
                                            ?.apply {
                                                Logger.w("getPlaylistData", "dataMore: ${this.size}")
                                            }?.mapNotNull {
                                                NextPage.fromMusicResponsiveListItemRenderer(
                                                    it.musicResponsiveListItemRenderer ?: return@mapNotNull null,
                                                )
                                            } ?: emptyList()
                                    listContent.addAll(dataMore.map { it.toTrack() })
                                    continueParam =
                                        values.getPlaylistContinuation()
                                    count++
                                }.onFailure {
                                    Logger.e("getPlaylistData", "Error: ${it.message}")
                                    continueParam = null
                                    count++
                                }
                        }
                        Logger.d("getPlaylistData", "playlist final data: ${listContent.size}")
                        parsePlaylistData(header, data ?: emptyList(), playlistId, viewString)?.let { playlist ->
                            emit(
                                Resource.Success<PlaylistBrowse>(
                                    playlist.copy(
                                        tracks =
                                            playlist.tracks.toMutableList().apply {
                                                addAll(listContent)
                                            },
                                        trackCount = (playlist.trackCount + listContent.size),
                                    ),
                                ),
                            )
                        } ?: emit(Resource.Error<PlaylistBrowse>("Error"))
                    }.onFailure { e ->
                        Logger.e("getPlaylistData", e.message ?: "Error")
                        emit(Resource.Error<PlaylistBrowse>(e.message.toString()))
                    }
            }
        }.flowOn(Dispatchers.IO)

    /**
     * Serves the stored copy of a playlist when the request for it fails, or the error if there is
     * no usable copy. Every failure path in [getPlaylistData] routes through here, including the
     * parse that returns nothing, so an offline open behaves the same whichever way it fails.
     *
     * The continuation is null because a cached playlist holds only the page that was stored, and
     * claiming more is available would leave the caller paging into nothing.
     */
    private suspend fun FlowCollector<Resource<Pair<PlaylistBrowse, String?>>>.emitCachedPlaylistOrError(
        playlistId: String,
        message: String,
    ) {
        val cached = cachedPlaylist(playlistId)
        if (cached != null) {
            Logger.d("getPlaylistData", "served $playlistId from cache")
            emit(Resource.Success(cached to null))
        } else {
            emit(Resource.Error(message))
        }
    }

    /**
     * Rebuilds a playlist from the stored row and the songs it references, or null if too little was
     * kept to be worth showing.
     *
     * Order comes from the stored track list because `WHERE videoId IN (...)` does not preserve it.
     * The author id is not stored, only the name, so callers resolve the artist by name.
     */
    private suspend fun cachedPlaylist(playlistId: String): PlaylistBrowse? {
        val id = playlistId.removePrefix("VL")
        val playlist = localDataSource.getPlaylist(id) ?: localDataSource.getPlaylist(playlistId) ?: return null
        val trackIds = playlist.tracks.orEmpty()
        if (trackIds.isEmpty()) return null
        val byId = localDataSource.getSongByListVideoIdFull(trackIds).associateBy { it.videoId }
        val tracks = trackIds.mapNotNull { byId[it]?.toTrack() }
        if (tracks.isEmpty()) return null
        return PlaylistBrowse(
            author = Author(id = "", name = playlist.author.orEmpty()),
            description = playlist.description,
            duration = playlist.duration,
            durationSeconds = playlist.durationSeconds,
            id = playlist.id,
            privacy = playlist.privacy,
            thumbnails = listOf(Thumbnail(height = 544, url = playlist.thumbnails, width = 544)),
            title = playlist.title,
            trackCount = playlist.trackCount,
            tracks = tracks,
            year = playlist.year.orEmpty(),
        )
    }

    override fun getPlaylistData(
        playlistId: String,
        viewString: String,
    ): Flow<Resource<Pair<PlaylistBrowse, String?>>> =
        flow {
            runCatching {
                var id = ""
                id +=
                    if (!playlistId.startsWith("VL")) {
                        "VL$playlistId"
                    } else {
                        playlistId
                    }
                Logger.d("getPlaylistData", "playlist id: $id")
                youTube
                    .customQuery(browseId = id, setLogin = true)
                    .onSuccess { result ->
                        val listContent: ArrayList<Track> = arrayListOf()
                        val data: List<MusicShelfRenderer.Content>? =
                            result.contents
                                ?.singleColumnBrowseResultsRenderer
                                ?.tabs
                                ?.get(
                                    0,
                                )?.tabRenderer
                                ?.content
                                ?.sectionListRenderer
                                ?.contents
                                ?.get(
                                    0,
                                )?.musicPlaylistShelfRenderer
                                ?.contents
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.secondaryContents
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicPlaylistShelfRenderer
                                    ?.contents
                        if (data != null) {
                            Logger.d("getPlaylistData", "data: $data")
                            Logger.d("getPlaylistData", "data size: ${data.size}")
                        }
                        val header =
                            result.header?.musicDetailHeaderRenderer
                                ?: result.header?.musicEditablePlaylistDetailHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicResponsiveHeaderRenderer
                                ?: result.contents
                                    ?.twoColumnBrowseResultsRenderer
                                    ?.tabs
                                    ?.get(0)
                                    ?.tabRenderer
                                    ?.content
                                    ?.sectionListRenderer
                                    ?.contents
                                    ?.get(0)
                                    ?.musicEditablePlaylistDetailHeaderRenderer
                                    ?.header
                                    ?.musicResponsiveHeaderRenderer
                        Logger.d("getPlaylistData", "header: $header")
                        val continueParam =
                            result.getPlaylistContinuation()
                        val radioEndpoint =
                            result.getPlaylistRadioEndpoint()
                        val shuffleEndpoint =
                            result.getPlaylistShuffleEndpoint()
                        Logger.d("getPlaylistData", "Endpoint: $radioEndpoint $shuffleEndpoint")
                        try {
                            parsePlaylistData(header, data ?: emptyList(), playlistId, viewString)?.let { playlist ->
                                emit(
                                    Resource.Success<Pair<PlaylistBrowse, String?>>(
                                        Pair(
                                            playlist.copy(
                                                tracks =
                                                    playlist.tracks.toMutableList().apply {
                                                        addAll(listContent)
                                                    },
                                                trackCount = (playlist.trackCount + listContent.size),
                                                shuffleEndpoint = shuffleEndpoint?.toYouTubeWatchEndpoint(),
                                                radioEndpoint = radioEndpoint?.toYouTubeWatchEndpoint(),
                                            ),
                                            continueParam,
                                        ),
                                    ),
                                )
                            } ?: emitCachedPlaylistOrError(playlistId, "Error")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            emitCachedPlaylistOrError(playlistId, e.message.toString())
                        }
                    }.onFailure { e ->
                        Logger.e("getPlaylistData", e.message ?: "Error")
                        emitCachedPlaylistOrError(playlistId, e.message.toString())
                    }
            }
        }.flowOn(Dispatchers.IO)

    override fun getLibraryPlaylist(): Flow<List<PlaylistsResult>?> =
        flow {
            youTube
                .getLibraryPlaylists()
                .onSuccess { data ->
                    val input =
                        data.contents
                            ?.singleColumnBrowseResultsRenderer
                            ?.tabs
                            ?.get(
                                0,
                            )?.tabRenderer
                            ?.content
                            ?.sectionListRenderer
                            ?.contents
                            ?.get(
                                0,
                            )?.gridRenderer
                            ?.items
                    val listItem = mutableListOf<PlaylistsResult>()
                    if (input.isNullOrEmpty()) {
                        Logger.w("Library", "No playlists found")
                        emit(null)
                        return@onSuccess
                    }
                    listItem.addAll(
                        parseLibraryPlaylist(input),
                    )
                    var continuation =
                        data.contents
                            ?.singleColumnBrowseResultsRenderer
                            ?.tabs
                            ?.firstOrNull()
                            ?.tabRenderer
                            ?.content
                            ?.sectionListRenderer
                            ?.contents
                            ?.firstOrNull()
                            ?.gridRenderer
                            ?.continuations
                            ?.firstOrNull()
                            ?.nextContinuationData
                            ?.continuation
                    while (continuation != null) {
                        youTube
                            .nextYouTubePlaylists(continuation)
                            .onSuccess { nextData ->
                                continuation = nextData.second
                                Logger.w("Library", "continuation: $continuation")
                                val nextInput = nextData.first
                                listItem.addAll(
                                    parseNextLibraryPlaylist(nextInput),
                                )
                            }.onFailure { exception ->
                                exception.printStackTrace()
                                Logger.e("Library", "Error: ${exception.message}")
                                continuation = null
                            }
                    }
                    if (listItem.isNotEmpty()) {
                        emit(listItem)
                        val account = localDataSource.getUsedGoogleAccount()
                        val isNeeded =
                            dataStoreManager.keepYouTubePlaylistOffline.first() == DataStoreManager.TRUE &&
                                dataStoreManager.loggedIn.first() == DataStoreManager.TRUE && account != null
                        if (isNeeded) {
                            insertYourYouTubePlaylist(
                                YourYouTubePlaylistList(
                                    emailPageId = "${account.email}_${account.pageId ?: ""}",
                                    listBrowseIds = listItem.map { it.browseId },
                                ),
                            )
                        }
                    } else {
                        emit(null)
                    }
                }.onFailure { e ->
                    Logger.e("Library", "Error: ${e.message}")
                    e.printStackTrace()
                    val account = localDataSource.getUsedGoogleAccount()
                    val isNeeded =
                        dataStoreManager.keepYouTubePlaylistOffline.first() == DataStoreManager.TRUE &&
                            dataStoreManager.loggedIn.first() == DataStoreManager.TRUE && account != null
                    if (isNeeded) {
                        val list =
                            getYourYouTubePlaylistList("${account.email}_${account.pageId ?: ""}")
                                .lastOrNull()
                        if (list != null) {
                            emit(
                                list.listBrowseIds.mapNotNull { id ->
                                    getPlaylist(id).lastOrNull()?.let {
                                        PlaylistsResult(
                                            author = it.author ?: "",
                                            browseId = it.id,
                                            category = "",
                                            itemCount = "${ it.trackCount }",
                                            resultType = "",
                                            thumbnails =
                                                listOf(
                                                    Thumbnail(
                                                        width = 544,
                                                        url = it.thumbnails,
                                                        height = 544,
                                                    ),
                                                ),
                                            title = it.title,
                                        )
                                    }
                                },
                            )
                        } else {
                            emit(null)
                        }
                    } else {
                        emit(null)
                    }
                }
        }.flowOn(Dispatchers.IO)

    override fun getMixedForYou(): Flow<List<PlaylistsResult>?> =
        flow {
            youTube
                .getMixedForYou()
                .onSuccess { data ->
                    val input =
                        data.contents
                            ?.singleColumnBrowseResultsRenderer
                            ?.tabs
                            ?.get(
                                0,
                            )?.tabRenderer
                            ?.content
                            ?.sectionListRenderer
                            ?.contents
                            ?.get(
                                0,
                            )?.gridRenderer
                            ?.items
                    val listItem = mutableListOf<PlaylistsResult>()
                    if (input.isNullOrEmpty()) {
                        Logger.w("Mixed For You", "No playlists found")
                        emit(null)
                        return@onSuccess
                    }
                    listItem.addAll(
                        parseLibraryPlaylist(input),
                    )
                    var continuation =
                        data.contents
                            ?.singleColumnBrowseResultsRenderer
                            ?.tabs
                            ?.firstOrNull()
                            ?.tabRenderer
                            ?.content
                            ?.sectionListRenderer
                            ?.contents
                            ?.firstOrNull()
                            ?.gridRenderer
                            ?.continuations
                            ?.firstOrNull()
                            ?.nextContinuationData
                            ?.continuation
                    while (continuation != null) {
                        youTube
                            .nextYouTubePlaylists(continuation)
                            .onSuccess { nextData ->
                                continuation = nextData.second
                                Logger.w("Mixed For You", "continuation: $continuation")
                                val nextInput = nextData.first
                                listItem.addAll(
                                    parseNextLibraryPlaylist(nextInput),
                                )
                            }.onFailure { exception ->
                                exception.printStackTrace()
                                Logger.e("Mixed For You", "Error: ${exception.message}")
                                continuation = null
                            }
                    }
                    if (listItem.isNotEmpty()) {
                        emit(listItem)
                    } else {
                        emit(null)
                    }
                }
        }.flowOn(Dispatchers.IO)

    override fun updateYourYouTubePlaylistTitle(
        playlistId: String,
        newTitle: String,
    ): Flow<Resource<String>> =
        flow {
            youTube
                .editPlaylist(playlistId, newTitle)
                .onSuccess {
                    emit(Resource.Success(it.toString()))
                }.onFailure {
                    emit(Resource.Error<String>(it.message ?: "Unknown error"))
                }
        }.flowOn(Dispatchers.IO)

    override suspend fun insertYourYouTubePlaylist(yourYouTubePlaylist: YourYouTubePlaylistList) =
        withContext(Dispatchers.IO) {
            localDataSource.insertYourYouTubePlaylist(yourYouTubePlaylist)
        }

    override suspend fun deleteAllYourYouTubePlaylist() =
        withContext(Dispatchers.IO) {
            localDataSource.deleteAllYourYouTubePlaylist()
        }

    override fun getYourYouTubePlaylistList(emailPageId: String): Flow<YourYouTubePlaylistList?> =
        flow {
            emit(localDataSource.getYourYouTubePlaylistList(emailPageId))
        }.flowOn(Dispatchers.IO)

}