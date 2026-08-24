package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

/**
 * The shared card surface, used by the Library shortcuts and the Home mood and genre tiles.
 *
 * It exists so those two screens cannot drift apart. Both previously carried their own idea of what
 * a card was: Library an ElevatedCard with Material elevation, Home another with a 4dp radius and a
 * 6dp lift. Neither matched the rest of the app, and fixing them separately would only have
 * guaranteed they diverge again the next time one is touched.
 *
 * Only the shape and fill live here now. The accent spine variant this file also held was for the
 * mood and genre tiles, and those moved to MoodCategoryCard, the same card Search uses.
 *
 * Deliberately opaque. The fill is the theme's surface variant already composited over the
 * background rather than left translucent, so it is a flat, slightly lighter dark surface rather
 * than a see-through one. Depth here comes from tone, never from blur or transparency.
 */
val SonoraCardShape = SonoraShapes.Container

@Composable
fun sonoraCardFill(): Color =
    MaterialTheme.colorScheme.surfaceVariant
        .copy(alpha = 0.45f)
        .compositeOver(MaterialTheme.colorScheme.background)

