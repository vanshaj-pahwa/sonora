package app.vanshajpahwa.domain.repository

import app.vanshajpahwa.domain.data.entities.NotificationEntity
import app.vanshajpahwa.domain.data.model.cookie.CookieItem
import app.vanshajpahwa.domain.data.type.RecentlyType
import app.vanshajpahwa.domain.manager.DataStoreManager
import kotlinx.coroutines.flow.Flow

interface CommonRepository {
    fun init(cookiePath: String, dataStoreManager: DataStoreManager)

    // Database
    fun closeDatabase()

    fun getDatabasePath(): String?

    suspend fun databaseDaoCheckpoint()

    // Recently data
    fun getAllRecentData(): Flow<List<RecentlyType>>

    // Notifications
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun getAllNotifications(): Flow<List<NotificationEntity>?>

    suspend fun isNotificationExists(link: String): Boolean

    suspend fun deleteNotification(id: Long)

    suspend fun writeTextToFile(text: String, filePath: String): Boolean

    suspend fun getCookiesFromInternalDatabase(url: String, packageName: String): CookieItem
}