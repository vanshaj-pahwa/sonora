@file:Suppress("ktlint:standard:filename")

package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

interface PhotoPickerLauncher {
    fun launch()
}

@Composable
expect fun photoPickerResult(onResultUri: (String?) -> Unit): PhotoPickerLauncher