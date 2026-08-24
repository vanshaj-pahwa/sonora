package app.vanshajpahwa.media3.service

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import app.vanshajpahwa.common.MEDIA_NOTIFICATION
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.media3.R
import app.vanshajpahwa.media3.extension.toCommandButton
import app.vanshajpahwa.media3.utils.CoilBitmapLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.system.exitProcess

@UnstableApi
internal class SimpleMediaService :
    MediaLibraryService(),
    KoinComponent {
    private val coroutineScope by inject<CoroutineScope>(named(app.vanshajpahwa.common.Config.SERVICE_SCOPE))
    // Session-level player from DI: the ForwardingPlayer wrapped with Cast support in the
    // full build (plain ForwardingPlayer in the FOSS build).
    private val player: Player by inject<Player>(qualifier = named(app.vanshajpahwa.common.Config.MAIN_PLAYER))
    private val coilBitmapLoader: CoilBitmapLoader by inject<CoilBitmapLoader>()

    private var mediaSession: MediaLibrarySession? = null

    private val simpleMediaSessionCallback: MediaLibrarySession.Callback by inject<MediaLibrarySession.Callback>()

    private val simpleMediaServiceHandler: MediaPlayerHandler by inject<MediaPlayerHandler>()
    private val dataStoreManager: DataStoreManager by inject<DataStoreManager>()

    private val binder = MusicBinder()


    inner class MusicBinder : Binder() {
        val service: SimpleMediaService
            get() = this@SimpleMediaService

        fun setActivitySession(
            context: Context,
            activity: Class<out Activity>,
        ) {
            mediaSession?.setSessionActivity(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, activity),
                    PendingIntent.FLAG_IMMUTABLE,
                ),
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Logger.w("Service", "Simple Media Service Bound")
        return super.onBind(intent) ?: binder
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        Logger.w("Service", "Simple Media Service Created")

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { MEDIA_NOTIFICATION.NOTIFICATION_ID },
                MEDIA_NOTIFICATION.NOTIFICATION_CHANNEL_ID,
                R.string.notification_channel_name,
            ).apply {
                setSmallIcon(R.drawable.mono)
            },
        )

        if (mediaSession == null) {
            mediaSession =
                provideMediaLibrarySession(
                    this,
                    player,
                    simpleMediaSessionCallback,
                )
        }

        simpleMediaServiceHandler.onUpdateNotification = { list ->
            val commandButtonList = list.map { it.toCommandButton(this) }
            mediaSession?.setMediaButtonPreferences(
                commandButtonList,
            )
        }

        val sessionToken = SessionToken(this, ComponentName(this, SimpleMediaService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({ controllerFuture.get() }, MoreExecutors.directExecutor())

        // The legacy PlayerNotificationManager that used to live here has been removed.
        //
        // It posted a SECOND media notification (id 2026, channel media_playback_channel) for the
        // same player that MediaSessionService already notifies for through
        // DefaultMediaNotificationProvider. Two notification systems owning one player is not a
        // configuration that can work: whichever one claimed the foreground service suppressed the
        // other, so with "keep service alive" on the Media3 notification never appeared, and on a
        // track change the two fought over the same foreground slot, which is the notification
        // vanishing and returning.
        //
        // MediaSessionService already keeps the service in the foreground for the whole time the
        // player is playing, which is what that setting was reaching for, so nothing is lost by
        // letting Media3 own the notification outright.

    }

    @UnstableApi
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        Logger.w("Service", "Simple Media Service Received Action: ${intent?.action}")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaSession

    @UnstableApi
    override fun onUpdateNotification(
        session: MediaSession,
        startInForegroundRequired: Boolean,
    ) {
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    @UnstableApi
    fun release() {
        Logger.w("Service", "Starting release process")
        runBlocking {
            try {
                // Release MediaSession (don't release player - CrossfadeExoPlayerAdapter manages it)
                mediaSession?.run {
                    this.player.pause()
                    this.player.playWhenReady = false
                    // Don't call this.player.release() - CrossfadeExoPlayerAdapter manages player lifecycle
                    this.release()
                }
                // Release handler (contains coroutines and jobs, which also releases the adapter)
                simpleMediaServiceHandler.release()
                mediaSession = null
                Logger.w("Service", "Simple Media Service Released")
            } catch (e: Exception) {
                Logger.e("Service", "Error during release")
            }
        }
    }

    @UnstableApi
    override fun onDestroy() {
        super.onDestroy()
        Logger.w("Service", "Simple Media Service Destroyed")
        if (simpleMediaServiceHandler.shouldReleaseOnTaskRemoved()) {
            release()
        }
    }

    override fun onTrimMemory(level: Int) {
        Logger.w("Service", "Simple Media Service Trim Memory Level: $level")
        simpleMediaServiceHandler.mayBeSaveRecentSong()
    }

    @UnstableApi
    override fun onTaskRemoved(rootIntent: Intent?) {
        Logger.w("Service", "Simple Media Service Task Removed")
        if (simpleMediaServiceHandler.shouldReleaseOnTaskRemoved()) {
            release()
            super.onTaskRemoved(rootIntent)
            exitProcess(0)
        }
    }

    // Can't inject by Koin because it depend on service
    @UnstableApi
    private fun provideMediaLibrarySession(
        service: MediaLibraryService,
        player: Player,
        callback: MediaLibrarySession.Callback,
    ): MediaLibrarySession =
        MediaLibrarySession
            .Builder(
                service,
                player,
                callback,
            ).setId(this.javaClass.name)
            .setBitmapLoader(coilBitmapLoader)
            .build()

    private fun isAppInForeground(): Boolean {
        val appProcessInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}