package app.vanshajpahwa.sonora.utils

import org.jetbrains.compose.resources.getString
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.explicit_content_blocked
import app.vanshajpahwa.sonora.resources.new_albums
import app.vanshajpahwa.sonora.resources.new_singles
import app.vanshajpahwa.sonora.resources.this_app_needs_to_access_your_notification
import app.vanshajpahwa.sonora.resources.time_out_check_internet_connection_or_change_piped_instance_in_settings

object ComposeResUtils {
    suspend fun getResString(
        type: StringType,
        vararg format: String,
    ): String =
        when (type) {
            StringType.EXPLICIT_CONTENT_BLOCKED -> {
                getString(Res.string.explicit_content_blocked)
            }

            StringType.NOTIFICATION_REQUEST -> {
                getString(Res.string.this_app_needs_to_access_your_notification)
            }

            StringType.TIME_OUT_ERROR -> {
                getString(Res.string.time_out_check_internet_connection_or_change_piped_instance_in_settings, *format)
            }

            StringType.NEW_SINGLES -> {
                getString(Res.string.new_singles)
            }

            StringType.NEW_ALBUMS -> {
                getString(Res.string.new_albums)
            }
        }

    enum class StringType {
        EXPLICIT_CONTENT_BLOCKED,
        NOTIFICATION_REQUEST,
        TIME_OUT_ERROR,
        NEW_SINGLES,
        NEW_ALBUMS,
    }
}