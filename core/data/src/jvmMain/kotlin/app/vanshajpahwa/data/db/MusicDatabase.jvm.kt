package app.vanshajpahwa.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import app.vanshajpahwa.common.DB_NAME
import app.vanshajpahwa.data.io.getHomeFolderPath
import java.io.File

actual fun getDatabaseBuilder(
    converters: Converters
): RoomDatabase.Builder<MusicDatabase> {
    return Room.databaseBuilder<MusicDatabase>(
        name = getDatabasePath()
    ).addTypeConverter(converters)
}

actual fun getDatabasePath(): String {
    val dbFile = File(getHomeFolderPath(listOf(".sonora", "db")), DB_NAME)
    return dbFile.absolutePath
}