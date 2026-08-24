package app.vanshajpahwa.sonora.expect

import androidx.compose.runtime.Composable

expect fun copyToClipboard(
    label: String,
    text: String,
)