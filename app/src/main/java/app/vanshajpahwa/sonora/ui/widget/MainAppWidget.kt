package app.vanshajpahwa.sonora.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.LocalSize
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.compose.ui.unit.DpSize
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.toBitmap
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.mediaservice.handler.RepeatState
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.MainActivity
import app.vanshajpahwa.sonora.R
import app.vanshajpahwa.sonora.ui.theme.seed
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MainAppWidget :
    GlanceAppWidget(),
    KoinComponent {
    /**
     * Two layouts, not one stretched.
     *
     * The widget declares 4x1 by default but is resizable both ways, and it used to render a single
     * layout at every size: five controls, a 96dp cover and the logo all competing for a strip about
     * 60dp tall. Responsive lets the short default drop to the three controls that matter and lets
     * the taller size keep the full set.
     */
    override val sizeMode = SizeMode.Responsive(setOf(COMPACT_SIZE, WIDE_SIZE))

    val sharedViewModel by inject<SharedViewModel>()
    private val dataStoreManager by inject<DataStoreManager>()
    val serviceScope by inject<CoroutineScope>(named(Config.SERVICE_SCOPE))

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        // No manual collect-and-update loop here, deliberately.
        //
        // There used to be two collectors on controllerState and nowPlayingScreenData, each calling
        // update() on every emission. update() re-invokes provideGlance, which launched two more
        // collectors, which called update again: a self feeding rebuild loop. A track change emits
        // on both flows at once, so a single skip kicked off several overlapping rebuilds, and every
        // rebuild throws the RemoteViews away and constructs them again. That is what made the
        // widget disappear on skip and reappear a few seconds later once the storm settled.
        //
        // provideContent below already collects both flows with collectAsState, which is how Glance
        // is meant to observe state: it recomposes in place instead of recreating the widget.

        // A widget cannot collect DataStore during composition, so the toggle is read once here and
        // applies from the next refresh. Both values are complete surfaces on their own, so the
        // widget never renders broken while it waits for that refresh.
        val glassEnabled =
            runCatching { dataStoreManager.enableLiquidGlass.first() == DataStoreManager.TRUE }
                .getOrDefault(false)

        provideContent {
            GlanceTheme {
                val scope = rememberCoroutineScope()

                val controllerState by sharedViewModel.controllerState.collectAsState()
                val screenDataState by sharedViewModel.nowPlayingScreenData.collectAsState()

                val thumbUrl by remember {
                    derivedStateOf {
                        screenDataState.thumbnailURL
                    }
                }

                val title by remember {
                    derivedStateOf {
                        screenDataState.nowPlayingTitle
                    }
                }

                val artist by remember {
                    derivedStateOf {
                        screenDataState.artistName
                    }
                }

                var randomImage by remember(thumbUrl) { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(thumbUrl) {
                    val request =
                        ImageRequest
                            .Builder(context)
                            .data(thumbUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(thumbUrl + "BIGGER")
                            .crossfade(true)
                            .placeholder(R.drawable.holder)
                            .error(R.drawable.holder)
                            .allowHardware(false)
                            .build()
                    val result = ImageLoader(context).execute(request)
                    randomImage =
                        when (result) {
                            is SuccessResult -> result.image.toBitmap()

                            else -> {
                                null
                            }
                        }
                }

                // Flat controlled surface. The palette colour pulled from the cover used to fill
                // the whole widget, which is where the loud orange came from: readable over some
                // covers, a wash over others, and never the app's own language. The cover now
                // appears only as the thumbnail, so title and artist always sit on a known dark
                // ground and need no scrim at all.
                val surface = if (glassEnabled) Color(0xCC0F1216) else Color(0xFF0F1216)
                val compact = LocalSize.current.height < WIDE_SIZE.height

                Box(
                    GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(surface))
                        .cornerRadius(20.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = GlanceModifier.size(if (compact) 46.dp else 64.dp),
                        ) {
                            randomImage?.let {
                                Image(
                                    provider = ImageProvider(it),
                                    contentDescription = "",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = GlanceModifier.fillMaxSize().cornerRadius(10.dp),
                                )
                            } ?: CircularProgressIndicator(
                                GlanceModifier.size(20.dp),
                                color = ColorProvider(Color.White),
                            )
                        }
                        Column(
                            GlanceModifier.defaultWeight().padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = title,
                                style =
                                    TextStyle(
                                        color = ColorProvider(Color.White),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Start,
                                    ),
                                maxLines = 1,
                                modifier = GlanceModifier.fillMaxWidth(),
                            )
                            Text(
                                text = artist,
                                style =
                                    TextStyle(
                                        color = ColorProvider(Color(0xFF9AA4B0)),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Start,
                                    ),
                                maxLines = 1,
                                modifier = GlanceModifier.fillMaxWidth(),
                            )
                        }
                        // The Sonora mark, using ic_launcher_foreground so it is literally the same
                        // artwork the launcher shows rather than a second copy that could drift.
                        // The old widget used mono.xml, which is white; this is the Sky Blue version,
                        // which is what reads as ours on the dark surface.
                        Image(
                            provider = ImageProvider(R.drawable.ic_launcher_foreground),
                            contentDescription = "Sonora",
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier.size(if (compact) 22.dp else 28.dp),
                        )
                        Spacer(GlanceModifier.size(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.wrapContentSize(),
                        ) {
                            // Shuffle and repeat exist only on the taller layout. On the 4x1 strip
                            // they were the difference between three comfortable targets and five
                            // cramped ones.
                            if (!compact) {
                                CircleIconButton(
                                    modifier = GlanceModifier.size(28.dp),
                                    imageProvider = ImageProvider(R.drawable.baseline_shuffle_24),
                                    contentDescription = "Shuffle",
                                    contentColor =
                                        if (controllerState.isShuffle) {
                                            ColorProvider(seed)
                                        } else {
                                            ColorProvider(Color(0xFF6B7580))
                                        },
                                    backgroundColor = ColorProvider(Color.Transparent),
                                    onClick = { sharedViewModel.onUIEvent(UIEvent.Shuffle) },
                                )
                                Spacer(GlanceModifier.size(4.dp))
                            }
                            CircleIconButton(
                                modifier = GlanceModifier.size(32.dp),
                                imageProvider = ImageProvider(R.drawable.baseline_skip_previous_24),
                                contentDescription = "Previous",
                                contentColor =
                                    ColorProvider(
                                        if (controllerState.isPreviousAvailable) Color.White else Color(0xFF5A636D),
                                    ),
                                backgroundColor = ColorProvider(Color.Transparent),
                                enabled = controllerState.isPreviousAvailable,
                                onClick = { sharedViewModel.onUIEvent(UIEvent.Previous) },
                            )
                            Spacer(GlanceModifier.size(2.dp))
                            // The one control worth finding without looking, so it takes the accent.
                            CircleIconButton(
                                modifier = GlanceModifier.size(if (compact) 42.dp else 48.dp),
                                imageProvider =
                                    if (controllerState.isPlaying) {
                                        ImageProvider(R.drawable.baseline_pause_circle_24)
                                    } else {
                                        ImageProvider(R.drawable.baseline_play_circle_24)
                                    },
                                contentDescription = if (controllerState.isPlaying) "Pause" else "Play",
                                contentColor = ColorProvider(seed),
                                backgroundColor = ColorProvider(Color.Transparent),
                                onClick = { sharedViewModel.onUIEvent(UIEvent.PlayPause) },
                            )
                            Spacer(GlanceModifier.size(2.dp))
                            CircleIconButton(
                                modifier = GlanceModifier.size(32.dp),
                                imageProvider = ImageProvider(R.drawable.baseline_skip_next_24),
                                contentDescription = "Next",
                                contentColor =
                                    ColorProvider(
                                        if (controllerState.isNextAvailable) Color.White else Color(0xFF5A636D),
                                    ),
                                backgroundColor = ColorProvider(Color.Transparent),
                                enabled = controllerState.isNextAvailable,
                                onClick = { sharedViewModel.onUIEvent(UIEvent.Next) },
                            )
                            if (!compact) {
                                Spacer(GlanceModifier.size(4.dp))
                                CircleIconButton(
                                    modifier = GlanceModifier.size(28.dp),
                                    imageProvider =
                                        when (controllerState.repeatState) {
                                            RepeatState.None -> ImageProvider(R.drawable.baseline_repeat_24)
                                            RepeatState.All -> ImageProvider(R.drawable.baseline_repeat_24_enable)
                                            RepeatState.One -> ImageProvider(R.drawable.baseline_repeat_one_24)
                                        },
                                    contentDescription = "Repeat",
                                    contentColor =
                                        if (controllerState.repeatState is RepeatState.None) {
                                            ColorProvider(Color(0xFF6B7580))
                                        } else {
                                            ColorProvider(seed)
                                        },
                                    backgroundColor = ColorProvider(Color.Transparent),
                                    onClick = { sharedViewModel.onUIEvent(UIEvent.Repeat) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        // Height decides the layout. The default 4x1 strip is short, and that is exactly the
        // constraint the old single layout ignored.
        private val COMPACT_SIZE = DpSize(250.dp, 60.dp)
        private val WIDE_SIZE = DpSize(300.dp, 110.dp)
    }

}