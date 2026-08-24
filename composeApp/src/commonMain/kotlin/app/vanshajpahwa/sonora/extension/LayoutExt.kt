package app.vanshajpahwa.sonora.extension

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

// Small layout helpers that Compose does not ship: grids, padding, alignment.
// Split out of the former UIExt.kt, which had grown into an unrelated grab bag.

@Composable
fun NonLazyGrid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content:
        @Composable()
        (Int) -> Unit,
) {
    Column(modifier = modifier) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.isElementVisible(onVisibilityChanged: (Boolean) -> Unit) =
    composed {
        val isVisible by remember { derivedStateOf { mutableStateOf(false) } }
        LaunchedEffect(isVisible.value) { onVisibilityChanged.invoke(isVisible.value) }
        this.onGloballyPositioned { layoutCoordinates ->
            isVisible.value = layoutCoordinates.parentLayoutCoordinates?.let {
                val parentBounds = it.boundsInWindow()
                val childBounds = layoutCoordinates.boundsInWindow()
                parentBounds.overlaps(childBounds)
            } == true
        }
    }

@Composable
fun animateAlignmentAsState(targetAlignment: Alignment): State<Alignment> {
    val biased = targetAlignment as BiasAlignment
    val horizontal by animateFloatAsState(biased.horizontalBias)
    val vertical by animateFloatAsState(biased.verticalBias)
    return remember { derivedStateOf { BiasAlignment(horizontal, vertical) } }
}

@Composable
fun PaddingValues.copy(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null,
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = start ?: this.calculateStartPadding(layoutDirection),
        top = top ?: this.calculateTopPadding(),
        end = end ?: this.calculateEndPadding(layoutDirection),
        bottom = bottom ?: this.calculateBottomPadding(),
    )
}
