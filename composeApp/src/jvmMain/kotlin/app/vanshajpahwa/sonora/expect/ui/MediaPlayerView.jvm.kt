package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import app.vanshajpahwa.domain.data.model.metadata.Lyrics
import app.vanshajpahwa.domain.data.model.streams.TimeLine
import app.vanshajpahwa.media_jvm_ui.ui.MediaPlayerViewWithSubtitleJvm
import app.vanshajpahwa.media_jvm_ui.ui.MediaPlayerViewWithUrl
import app.vanshajpahwa.sonora.ui.theme.typo

@Composable
actual fun MediaPlayerView(
    url: String,
    modifier: Modifier,
    // Desktop never renders the portrait artist-header canvas, so cropToBounds is
    // accepted for signature parity but intentionally unused here.
    @Suppress("UNUSED_PARAMETER") cropToBounds: Boolean,
) {
    MediaPlayerViewWithUrl(
        url = url,
        modifier = modifier,
    )
}

@Composable
actual fun MediaPlayerViewWithSubtitle(
    modifier: Modifier,
    playerName: String,
    shouldPip: Boolean,
    shouldShowSubtitle: Boolean,
    shouldScaleDownSubtitle: Boolean,
    isInPipMode: Boolean,
    timelineState: TimeLine,
    lyricsData: Lyrics?,
    translatedLyricsData: Lyrics?,
    mainTextStyle: TextStyle,
    translatedTextStyle: TextStyle,
) {
    MediaPlayerViewWithSubtitleJvm(
        playerName = playerName,
        modifier = modifier,
        shouldShowSubtitle = shouldShowSubtitle,
        shouldScaleDownSubtitle = shouldScaleDownSubtitle,
        timelineState = timelineState,
        lyricsData = lyricsData,
        translatedLyricsData = translatedLyricsData,
        mainTextStyle = typo().bodyLarge,
        translatedTextStyle = typo().bodyMedium,
    )
}