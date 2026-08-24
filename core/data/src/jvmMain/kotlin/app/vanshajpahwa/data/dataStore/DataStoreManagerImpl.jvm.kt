package app.vanshajpahwa.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.vanshajpahwa.common.SETTINGS_FILENAME
import app.vanshajpahwa.data.io.getHomeFolderPath
import createDataStore
import java.io.File

actual fun createDataStoreInstance(): DataStore<Preferences> = createDataStore(
    producePath = {
        val file = File(getHomeFolderPath(listOf(".sonora")), "$SETTINGS_FILENAME.preferences_pb")
        file.absolutePath
    }
)