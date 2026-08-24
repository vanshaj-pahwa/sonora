package app.vanshajpahwa.kotlinytmusicscraper.models.body

import app.vanshajpahwa.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class NotificationBody(
    val context: Context,
    val notificationsMenuRequestType: String = "NOTIFICATIONS_MENU_REQUEST_TYPE_INBOX",
)