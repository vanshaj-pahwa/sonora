package app.vanshajpahwa.sonora.expect

import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.ui.mini_player.MiniPlayerManager

actual fun toggleMiniPlayer() {
    Logger.d("MiniPlayer", "Toggle called, current state: ${MiniPlayerManager.isOpen}")
    MiniPlayerManager.isOpen = !MiniPlayerManager.isOpen
    Logger.d("MiniPlayer", "New state: ${MiniPlayerManager.isOpen}")
}
