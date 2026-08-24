package app.vanshajpahwa.sonora.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// Greyscale treatments applied to a Modifier or a TextStyle.
// Split out of the former UIExt.kt, which had grown into an unrelated grab bag.

fun Modifier.greyScale() = this.then(GreyScaleModifier())

fun TextStyle.greyScale(): TextStyle =
    this.copy(
        color = Color.Gray,
    )
