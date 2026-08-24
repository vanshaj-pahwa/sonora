package app.vanshajpahwa.kotlinytmusicscraper.extractor

import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem
import app.vanshajpahwa.kotlinytmusicscraper.models.response.DownloadProgress

actual class Extractor {
    actual fun init() {
    }

    actual fun logIn(cookie: String?) {}

    actual fun newPipePlayer(videoId: String): List<Pair<Int, String>> = emptyList()

    actual fun mergeAudioVideoDownload(filePath: String): DownloadProgress = DownloadProgress.failed("Not supported on iOS")

    actual fun saveAudioWithThumbnail(
        filePath: String,
        track: SongItem,
    ): DownloadProgress = DownloadProgress.failed("Not supported on iOS")
}