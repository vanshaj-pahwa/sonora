package app.vanshajpahwa.sonora.extension

import androidx.compose.runtime.Composable
import app.vanshajpahwa.common.SponsorBlockType
import app.vanshajpahwa.domain.data.model.browse.artist.ArtistBrowse
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.utils.FilterState
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.viewModel.ArtistScreenData
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.custom_order
import app.vanshajpahwa.sonora.resources.day_s_ago
import app.vanshajpahwa.sonora.resources.filler
import app.vanshajpahwa.sonora.resources.hour_s_ago
import app.vanshajpahwa.sonora.resources.interaction
import app.vanshajpahwa.sonora.resources.intro
import app.vanshajpahwa.sonora.resources.month_s_ago
import app.vanshajpahwa.sonora.resources.music_off_topic
import app.vanshajpahwa.sonora.resources.na_na
import app.vanshajpahwa.sonora.resources.newer_first
import app.vanshajpahwa.sonora.resources.older_first
import app.vanshajpahwa.sonora.resources.outro
import app.vanshajpahwa.sonora.resources.poi_highlight
import app.vanshajpahwa.sonora.resources.preview
import app.vanshajpahwa.sonora.resources.recently
import app.vanshajpahwa.sonora.resources.self_promotion
import app.vanshajpahwa.sonora.resources.sponsor
import app.vanshajpahwa.sonora.resources.title
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.time.ExperimentalTime

fun String?.removeDuplicateWords(): String {
    if (this == null) {
        return "null"
    } else {
        val regex = Regex("\\b(\\w+)\\b\\s*(?=.*\\b\\1\\b)")
        return this.replace(regex, "")
    }
}

fun <T> Iterable<T>.indexMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    forEachIndexed { i, v ->
        map[v] = i
    }
    return map
}

infix fun <E> Collection<E>.symmetricDifference(other: Collection<E>): Set<E> {
    val left = this subtract other
    val right = other subtract this
    return left union right
}

@OptIn(ExperimentalTime::class)
@Composable
fun LocalDateTime.formatTimeAgo(): String {
    val now = now()
    val duration =
        this
            .toInstant(TimeZone.currentSystemDefault())
            .periodUntil(now.toInstant(TimeZone.currentSystemDefault()), TimeZone.currentSystemDefault())

    val monthsDiff = duration.months + (duration.years * 12)
    val daysDiff = duration.days

    // For hours, we need to calculate manually since Period doesn't include hours
    val thisInstant = this.toInstant(TimeZone.currentSystemDefault())
    val nowInstant = now.toInstant(TimeZone.currentSystemDefault())
    val hoursDiff = (nowInstant - thisInstant).inWholeHours

    return when {
        monthsDiff >= 1 -> stringResource(Res.string.month_s_ago, monthsDiff)
        daysDiff >= 1 -> stringResource(Res.string.day_s_ago, daysDiff)
        hoursDiff >= 2 -> stringResource(Res.string.hour_s_ago, hoursDiff)
        else -> stringResource(Res.string.recently)
    }
}

@Composable
fun formatDuration(duration: Long): String {
    if (duration < 0L) return stringResource(Res.string.na_na)
    val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds: Long = (
        TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    )
    return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
}

fun parseTimestampToMilliseconds(timestamp: String): Double {
    val parts = timestamp.split(":")
    val totalSeconds =
        when (parts.size) {
            2 -> {
                try {
                    val minutes = parts[0].toDouble()
                    val seconds = parts[1].toDouble()
                    (minutes * 60 + seconds)
                } catch (e: NumberFormatException) {
                    // Handle parsing error
                    e.printStackTrace()
                    return 0.0
                }
            }

            3 -> {
                try {
                    val hours = parts[0].toDouble()
                    val minutes = parts[1].toDouble()
                    val seconds = parts[2].toDouble()
                    (hours * 3600 + minutes * 60 + seconds)
                } catch (e: NumberFormatException) {
                    // Handle parsing error
                    e.printStackTrace()
                    return 0.0
                }
            }

            else -> {
                // Handle incorrect format
                return 0.0
            }
        }
    return totalSeconds * 1000
}

fun InputStream.zipInputStream(): ZipInputStream = ZipInputStream(this)

fun OutputStream.zipOutputStream(): ZipOutputStream = ZipOutputStream(this)

fun Long?.bytesToMB(): Long {
    val mbInBytes = 1024 * 1024
    return this?.div(mbInBytes) ?: 0L
}

fun getSizeOfFile(dir: File): Long {
    var dirSize: Long = 0
    if (!dir.listFiles().isNullOrEmpty()) {
        for (f in dir.listFiles()!!) {
            dirSize += f.length()
            if (f.isDirectory) {
                dirSize += getSizeOfFile(f)
            }
        }
    }
    return dirSize
}

fun ArtistBrowse.toArtistScreenData(): ArtistScreenData =
    ArtistScreenData(
        title = this.name,
        imageUrl = this.thumbnails?.lastOrNull()?.url,
        subscribers = this.subscribers,
        playCount = this.views,
        isChannel = this.songs == null,
        channelId = this.channelId,
        radioParam = this.radioId,
        shuffleParam = this.shuffleId,
        description = this.description,
        listSongParam = this.songs?.browseId,
        popularSongs = this.songs?.results?.map { it.toTrack() } ?: emptyList(),
        singles = this.singles,
        albums = this.albums,
        video =
            this.video?.let { video ->
                ArtistBrowse.Videos(video.map { it.toTrack() }, this.videoList)
            },
        related = this.related,
        featuredOn = this.featuredOn ?: emptyList(),
    )

/**
 * Clamp a YouTube image URL's `w`/`h` size spec to the smaller dimension so it's square
 * (`...=w2880-h1200-...` -> `...=w1200-h1200-...`). Only the size spec after the last `=` is
 * touched. Returns the url unchanged when it has no `w`/`h` spec or is already square.
 */
fun String.toSquareThumbnailUrl(): String {
    val eq = lastIndexOf('=')
    if (eq < 0 || eq == lastIndex) return this
    val base = substring(0, eq + 1)
    val spec = substring(eq + 1)
    val width = Regex("(?:^|-)w(\\d+)").find(spec)?.groupValues?.get(1)?.toIntOrNull()
    val height = Regex("(?:^|-)h(\\d+)").find(spec)?.groupValues?.get(1)?.toIntOrNull()
    if (width == null || height == null || width == height) return this
    val min = minOf(width, height)
    return base +
        spec.replace(Regex("(^|-)([wh])\\d+")) { m -> "${m.groupValues[1]}${m.groupValues[2]}$min" }
}

fun isValidProxyHost(host: String): Boolean {
    // Regular expression to validate proxy host (without port)
    val proxyHostRegex =
        Regex(
            pattern = "^(?!-)[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(?<!-)\$",
            options = setOf(RegexOption.IGNORE_CASE),
        )

    // Return true if the host matches the regex or is an IP address
    return proxyHostRegex.matches(host) || isIPAddress(host)
}

private fun isIPAddress(host: String): Boolean {
    // Check if the host is an IPv4 address
    val ipv4Regex =
        Regex(
            pattern = "^([0-9]{1,3}\\.){3}[0-9]{1,3}\$",
        )
    if (ipv4Regex.matches(host)) {
        return host.split('.').all { it.toInt() in 0..255 }
    }

    // Check if the host is an IPv6 address
    val ipv6Regex =
        Regex(
            pattern = "^[0-9a-fA-F:]+$",
        )
    return ipv6Regex.matches(host)
}

fun String.isTwoLetterCode(): Boolean {
    val regex = "^[A-Za-z]{2}$".toRegex()
    return regex.matches(this)
}

fun FilterState.displayNameRes(): StringResource =
    when (this) {
        FilterState.NewerFirst -> Res.string.newer_first
        FilterState.OlderFirst -> Res.string.older_first
        FilterState.Title -> Res.string.title
        FilterState.CustomOrder -> Res.string.custom_order
    }

@Composable
fun String?.ifNullOrEmpty(defaultValue: @Composable () -> String): String = if (isNullOrEmpty()) defaultValue() else this

@Composable
fun SponsorBlockType.displayString(): String =
    when (this) {
        SponsorBlockType.FILLER -> stringResource(Res.string.filler)
        SponsorBlockType.INTERACTION -> stringResource(Res.string.interaction)
        SponsorBlockType.INTRO -> stringResource(Res.string.intro)
        SponsorBlockType.MUSIC_OFF_TOPIC -> stringResource(Res.string.music_off_topic)
        SponsorBlockType.OUTRO -> stringResource(Res.string.outro)
        SponsorBlockType.POI_HIGHLIGHT -> stringResource(Res.string.poi_highlight)
        SponsorBlockType.PREVIEW -> stringResource(Res.string.preview)
        SponsorBlockType.SELF_PROMOTION -> stringResource(Res.string.self_promotion)
        SponsorBlockType.SPONSOR -> stringResource(Res.string.sponsor)
    }
/**
 * Compacts a count for display: "1,234,567 views" reads as "1.2M views", 4123 as "4.1K".
 *
 * A label after the number is kept. Anything already shortened is returned untouched, recognised by
 * a unit letter sitting directly against the digits as in "3.9M subscribers": those arrive that way
 * from YouTube, localised, and rebuilding them would replace the user's language with English.
 */
fun String.toShortViewCount(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) return this
    val end = trimmed.indexOfFirst { !it.isDigit() && it != ',' && it != '.' && it != ' ' }
    val head = if (end == -1) trimmed else trimmed.substring(0, end)
    val tail = if (end == -1) "" else trimmed.substring(end).trim()
    // A letter touching the digits means it is already a unit, not a label.
    if (end != -1 && !head.endsWith(" ")) return this
    val value = head.filter { it.isDigit() }.toLongOrNull() ?: return this
    if (value < 1_000L) return this
    val short =
        when {
            value >= 1_000_000_000L -> value.compactTo(1_000_000_000L, "B")
            value >= 1_000_000L -> value.compactTo(1_000_000L, "M")
            else -> value.compactTo(1_000L, "K")
        }
    return if (tail.isEmpty()) short else "$short $tail"
}

/**
 * One decimal place below ten, none above, so 9.9M keeps its precision and 152M does not carry a
 * digit nobody reads.
 */
private fun Long.compactTo(
    unit: Long,
    suffix: String,
): String {
    val whole = this / unit
    val tenth = (this % unit) * 10 / unit
    return if (whole < 10 && tenth > 0) "$whole.$tenth$suffix" else "$whole$suffix"
}
