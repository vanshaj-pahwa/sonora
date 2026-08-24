package app.vanshajpahwa.data.di.loader

import app.vanshajpahwa.media_jvm.di.loadDesktopPlayerModule

actual fun loadMediaService() {
    loadDesktopPlayerModule()
}
