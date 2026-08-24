package app.vanshajpahwa.sonora.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntSize
import app.vanshajpahwa.domain.data.model.ui.ScreenSizeInfo
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

// Platform hooks and one-off helpers with no better home.
// The themed, gradient, scroll and palette helpers that used to live here now sit in files
// named after what they do.

@Composable
expect fun getScreenSizeInfo(): ScreenSizeInfo

@Composable
expect fun KeepScreenOn()

@Composable
expect fun rememberIsInPipMode(): Boolean

fun ImageBitmap.toResizedBitmap(
    width: Int,
    height: Int,
): ImageBitmap {
    val resized = ImageBitmap(width, height)
    val canvas = Canvas(resized)
    canvas.drawImageRect(
        image = this,
        dstSize = IntSize(width, height),
        paint = Paint(),
    )
    return resized
}

fun getStringBlocking(res: StringResource): String =
    runBlocking {
        getString(res)
    }
