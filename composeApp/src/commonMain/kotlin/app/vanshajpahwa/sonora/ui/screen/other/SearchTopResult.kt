package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.searchResult.artists.ArtistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.SongsResult
import app.vanshajpahwa.domain.data.model.searchResult.videos.VideosResult
import app.vanshajpahwa.domain.data.type.SearchResultType
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.sonora.extension.toShortViewCount
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.artist
import app.vanshajpahwa.sonora.resources.play
import app.vanshajpahwa.sonora.resources.save
import app.vanshajpahwa.sonora.resources.shuffle
import app.vanshajpahwa.sonora.resources.song
import app.vanshajpahwa.sonora.resources.start_radio
import app.vanshajpahwa.sonora.resources.video
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.ui.component.ShimmerSearchItem
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.icon.ArrowForwardIos
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.PlaylistAdd
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.ArtistSpotlight
import app.vanshajpahwa.sonora.viewModel.SearchType
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource

/**
 * The single result most likely to be what the query asked for, or null with nothing to offer.
 *
 * Search runs one request per type, each ranked by YouTube within its own type, and nothing ranks
 * across them. This scores the head of the songs, videos and artists lists against the query:
 * how much of the query the title and artist names account for, how much of the title the query
 * names, and a small nudge for YouTube's own order. "you karan aujla" lands on the song You, while
 * "karan aujla" lands on the artist, whose name is the whole query. A query that matches nothing
 * falls back to YouTube's first song.
 */
fun pickTopResult(
    query: String,
    songs: List<SongsResult>,
    videos: List<VideosResult>,
    artists: List<ArtistsResult>,
): SearchResultType? {
    val queryTokens = tokens(query).toSet()
    val candidates =
        songs.take(5).mapIndexed { rank, song ->
            Candidate(song, song.title.orEmpty(), song.artists.orEmpty().map { it.name }, rank, 0.0)
        } +
            artists.take(2).mapIndexed { rank, artist ->
                Candidate(artist, artist.artist, emptyList(), rank, 0.0)
            } +
            // Behind songs on a tie: the same track as a music video is the weaker thing to feature.
            videos.take(3).mapIndexed { rank, video ->
                Candidate(video, video.title, video.artists.orEmpty().map { it.name }, rank, -0.1)
            }
    if (queryTokens.isEmpty()) return candidates.firstOrNull()?.result
    return candidates.maxByOrNull { it.score(queryTokens) }?.result
}

private class Candidate(
    val result: SearchResultType,
    val name: String,
    val credits: List<String>,
    val rank: Int,
    val typeBias: Double,
) {
    fun score(query: Set<String>): Double {
        val nameTokens = tokens(name)
        if (nameTokens.isEmpty()) return Double.NEGATIVE_INFINITY
        val creditTokens = credits.flatMap(::tokens).toSet()
        val coverage = query.count { it in nameTokens || it in creditTokens }.toDouble() / query.size
        val precision = nameTokens.count { it in query }.toDouble() / nameTokens.size
        return coverage + 0.5 * precision - 0.05 * rank + typeBias
    }
}

// Bracketed asides ("(feat. X)", "[Official Video]") and joining words say nothing about which
// result was meant, and left in they dilute every score equally.
private val BRACKETED = Regex("""\(.*?\)|\[.*?]""")
private val SEPARATORS = Regex("""[^\p{L}\p{N}]+""")
private val FILLER = setOf("by", "the", "a", "ft", "feat", "official", "video", "audio", "lyrics", "song")

private fun tokens(text: String): List<String> =
    text
        .lowercase()
        .replace(BRACKETED, " ")
        .split(SEPARATORS)
        .filter { it.isNotBlank() && it !in FILLER }

/** One titled block of the All results, capped at [limit] rows with the rest behind See all. */
data class SearchSection(
    val type: SearchType,
    val items: List<SearchResultType>,
    val limit: Int,
)

/** Fills the top result card for whichever kind of result won. */
@Composable
fun SearchTopResult(
    result: SearchResultType,
    spotlight: ArtistSpotlight?,
    currentVideoId: String?,
    onPlay: (Track, String) -> Unit,
    onSave: (SongEntity) -> Unit,
    onStartRadio: (ArtistsResult) -> Unit,
    onShuffle: (ArtistSpotlight) -> Unit,
    onPlaySpotlightSong: (ArtistSpotlight, Int) -> Unit,
    onMoreSong: (Track) -> Unit,
    onOpenArtist: (ArtistsResult) -> Unit,
    modifier: Modifier = Modifier,
    glass: Boolean = false,
) {
    when (result) {
        is SongsResult -> {
            val track = result.toTrack()
            val play = { onPlay(track, Config.SONG_CLICK) }
            SearchTrackTopResult(
                title = result.title.orEmpty(),
                subtitle = details(stringResource(Res.string.song), result.artists.orEmpty().map { it.name }, result.duration),
                thumbnail = result.thumbnails?.lastOrNull()?.url,
                onPlay = play,
                onSave = { onSave(track.toSongEntity()) },
                glass = glass,
                modifier = modifier,
            )
        }

        is VideosResult -> {
            val track = result.toTrack()
            val play = { onPlay(track, Config.VIDEO_CLICK) }
            SearchTrackTopResult(
                title = result.title,
                subtitle = details(stringResource(Res.string.video), result.artists.orEmpty().map { it.name }, result.duration),
                thumbnail = result.thumbnails?.lastOrNull()?.url,
                onPlay = play,
                onSave = { onSave(track.toSongEntity()) },
                glass = glass,
                modifier = modifier,
            )
        }

        is ArtistsResult -> {
            // Only use a spotlight fetched for this artist; a previous search's may still be held.
            val loaded = spotlight?.takeIf { it.channelId == result.browseId }
            SearchArtistTopResult(
                artist = result,
                spotlight = loaded,
                currentVideoId = currentVideoId,
                onStartRadio = { onStartRadio(result) },
                onShuffle = onShuffle,
                onPlaySong = onPlaySpotlightSong,
                onMoreSong = onMoreSong,
                onOpen = { onOpenArtist(result) },
                glass = glass,
                modifier = modifier,
            )
        }

        else -> Unit
    }
}

@Composable
private fun SearchTrackTopResult(
    title: String,
    subtitle: String,
    thumbnail: String?,
    onPlay: () -> Unit,
    onSave: () -> Unit,
    glass: Boolean,
    modifier: Modifier = Modifier,
) {
    TopResultContainer(artwork = thumbnail, glass = glass, modifier = modifier.clickable(onClick = onPlay)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier = Modifier.size(84.dp).clip(SonoraShapes.Media),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typo().titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = typo().bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        TopResultActions {
            PrimaryAction(stringResource(Res.string.play), SonoraIcons.PlayArrow, onPlay)
            SecondaryAction(stringResource(Res.string.save), SonoraIcons.PlaylistAdd, onSave)
        }
    }
}

/**
 * The artist as one self-contained card: who they are, how to start listening, and their popular
 * songs, before any other result. The header row opens the artist page.
 *
 * The audience line and the songs come from [spotlight], an artist fetch that lands a moment after
 * the search does. Until then the card stands on the search row alone.
 */
@Composable
private fun SearchArtistTopResult(
    artist: ArtistsResult,
    spotlight: ArtistSpotlight?,
    currentVideoId: String?,
    onStartRadio: () -> Unit,
    onShuffle: (ArtistSpotlight) -> Unit,
    onPlaySong: (ArtistSpotlight, Int) -> Unit,
    onMoreSong: (Track) -> Unit,
    onOpen: () -> Unit,
    glass: Boolean,
    modifier: Modifier = Modifier,
) {
    TopResultContainer(artwork = artist.thumbnails.lastOrNull()?.url, glass = glass, modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpen)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = artist.thumbnails.lastOrNull()?.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.artist,
                    style = typo().titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                val stat =
                    listOfNotNull(
                        spotlight?.subscribers?.takeIf { it.isNotBlank() }?.toShortViewCount(),
                        spotlight?.audience?.takeIf { it.isNotBlank() }?.toShortViewCount(),
                    ).joinToString(" • ")
                Text(
                    text = stat.ifBlank { stringResource(Res.string.artist) },
                    style = typo().bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = SonoraIcons.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
        val canShuffle = spotlight?.shuffle != null
        if (artist.radioId.isNotBlank() || canShuffle) {
            Spacer(Modifier.height(16.dp))
            TopResultActions {
                if (artist.radioId.isNotBlank()) {
                    PrimaryAction(stringResource(Res.string.start_radio), SonoraIcons.PlayArrow, onStartRadio)
                }
                if (spotlight != null && canShuffle) {
                    SecondaryAction(stringResource(Res.string.shuffle), SonoraIcons.Shuffle) { onShuffle(spotlight) }
                }
            }
        }
        if (spotlight == null) {
            // The artist fetch is in flight. Rows of the same height as the songs that replace them,
            // so the card does not jump when they land.
            Column(modifier = Modifier.padding(top = 12.dp)) {
                repeat(5) { ShimmerSearchItem() }
            }
        }
        AnimatedVisibility(
            visible = !spotlight?.topSongs.isNullOrEmpty(),
            enter = fadeIn() + expandVertically(),
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                spotlight?.let { loaded ->
                    loaded.topSongs.forEachIndexed { index, track ->
                        SongFullWidthItems(
                            track = track,
                            isPlaying = track.videoId == currentVideoId,
                            showDuration = true,
                            modifier = Modifier,
                            onClickListener = { onPlaySong(loaded, index) },
                            onMoreClickListener = { onMoreSong(track) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * The card body. With liquid glass on, the result's own artwork, darkened, sits behind it as the
 * glass source, the way the album header uses its art. The source is a sibling of the glass and
 * never its parent: glass inside its own source is a render feedback loop that crashes the shader.
 */
@Composable
private fun TopResultContainer(
    artwork: String?,
    glass: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!glass) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(SonoraShapes.Container)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp),
        ) { content() }
        return
    }
    val backdrop = rememberBackdrop(Color.Black)
    Box(modifier = modifier.fillMaxWidth().clip(SonoraShapes.Container)) {
        Box(modifier = Modifier.matchParentSize().layerBackdrop(backdrop)) {
            AsyncImage(
                model = artwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Keeps white text legible over bright art once the glass has blurred it.
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .liquidGlass(backdrop, SonoraShapes.Container, interactive = false)
                    .padding(vertical = 16.dp),
        ) { content() }
    }
}

@Composable
private fun TopResultActions(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

/** The one accented control on the card. */
@Composable
private fun RowScope.PrimaryAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = label, style = typo().labelLarge)
    }
}

@Composable
private fun RowScope.SecondaryAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = label, style = typo().labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun details(
    kind: String,
    artists: List<String>,
    duration: String?,
): String = listOf(kind, artists.joinToString(), duration.orEmpty()).filter { it.isNotBlank() }.joinToString(" • ")
