package app.vanshajpahwa.data.io

import app.vanshajpahwa.data.db.documentDirectory
import okio.FileSystem

actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
actual fun fileDir(): String = documentDirectory() + "/Sonora"