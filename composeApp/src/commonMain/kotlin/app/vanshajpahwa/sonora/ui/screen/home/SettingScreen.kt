package app.vanshajpahwa.sonora.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.MarqueeAnimationMode
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.alpha
import app.vanshajpahwa.sonora.ui.component.WaveformTrack
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import app.vanshajpahwa.sonora.ui.component.LocalSettingsSearchQuery
import app.vanshajpahwa.sonora.ui.component.settingMatchesQuery
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.resources.search_settings
import app.vanshajpahwa.sonora.resources.no_settings_found
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eygraber.uri.toKmpUri
import app.vanshajpahwa.common.LIMIT_CACHE_SIZE
import app.vanshajpahwa.common.QUALITY
import app.vanshajpahwa.common.SUPPORTED_LANGUAGE
import app.vanshajpahwa.common.SUPPORTED_LOCATION
import app.vanshajpahwa.common.SponsorBlockType
import app.vanshajpahwa.common.VIDEO_QUALITY
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.manager.DataStoreManager.Values.TRUE
import app.vanshajpahwa.domain.repository.ImportProgress
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.ui.fileSaverResult
import app.vanshajpahwa.sonora.expect.ui.isWallpaperDynamicColorSupported
import app.vanshajpahwa.sonora.expect.ui.openEqResult
import app.vanshajpahwa.sonora.extension.bytesToMB
import app.vanshajpahwa.sonora.extension.displayString
import app.vanshajpahwa.sonora.extension.isTwoLetterCode
import app.vanshajpahwa.sonora.extension.isValidProxyHost
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.ActionButton
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.component.SettingChoice
import app.vanshajpahwa.sonora.ui.component.SettingChoiceRow
import app.vanshajpahwa.sonora.ui.component.SettingItem
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.Error
import app.vanshajpahwa.sonora.ui.icon.PeopleAlt
import app.vanshajpahwa.sonora.ui.icon.PlaylistAdd
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.CreditDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.DiscordLoginDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.LoginDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.SpotifyLoginDestination
import app.vanshajpahwa.sonora.ui.theme.md_theme_dark_primary
import app.vanshajpahwa.sonora.ui.theme.parseThemeColorHex
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.utils.VersionManager
import app.vanshajpahwa.sonora.viewModel.ImportViewModel
import app.vanshajpahwa.sonora.viewModel.SettingAlertState
import app.vanshajpahwa.sonora.viewModel.SettingBasicAlertState
import app.vanshajpahwa.sonora.viewModel.SettingsViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.ChipColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.mohamedrejeb.calf.core.ExperimentalCalfApi
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.about_us
import app.vanshajpahwa.sonora.resources.add_an_account
import app.vanshajpahwa.sonora.resources.ai
import app.vanshajpahwa.sonora.resources.ai_api_key
import app.vanshajpahwa.sonora.resources.ai_provider
import app.vanshajpahwa.sonora.resources.anonymous
import app.vanshajpahwa.sonora.resources.app_name
import app.vanshajpahwa.sonora.resources.artwork_style
import app.vanshajpahwa.sonora.resources.artwork_style_note
import app.vanshajpahwa.sonora.resources.artwork_style_ring
import app.vanshajpahwa.sonora.resources.spin_artwork_description
import app.vanshajpahwa.sonora.resources.spin_artwork
import app.vanshajpahwa.sonora.resources.artwork_style_square
import app.vanshajpahwa.sonora.resources.audio
import app.vanshajpahwa.sonora.resources.author
import app.vanshajpahwa.sonora.resources.author_name
import app.vanshajpahwa.sonora.resources.auto_backup
import app.vanshajpahwa.sonora.resources.auto_backup_description
import app.vanshajpahwa.sonora.resources.auto_check_for_update
import app.vanshajpahwa.sonora.resources.auto_check_for_update_description
import app.vanshajpahwa.sonora.resources.backup
import app.vanshajpahwa.sonora.resources.backup_downloaded
import app.vanshajpahwa.sonora.resources.backup_downloaded_description
import app.vanshajpahwa.sonora.resources.backup_frequency
import app.vanshajpahwa.sonora.resources.balance_media_loudness
import app.vanshajpahwa.sonora.resources.better_lyrics
import app.vanshajpahwa.sonora.resources.blog_notification_description
import app.vanshajpahwa.sonora.resources.blog_notification_title
import app.vanshajpahwa.sonora.resources.blurred_backdrop
import app.vanshajpahwa.sonora.resources.blurred_backdrop_description
import app.vanshajpahwa.sonora.resources.buy_me_a_coffee
import app.vanshajpahwa.sonora.resources.cancel
import app.vanshajpahwa.sonora.resources.canvas_info
import app.vanshajpahwa.sonora.resources.categories_sponsor_block
import app.vanshajpahwa.sonora.resources.change
import app.vanshajpahwa.sonora.resources.change_language_warning
import app.vanshajpahwa.sonora.resources.check_for_update
import app.vanshajpahwa.sonora.resources.checking
import app.vanshajpahwa.sonora.resources.clear
import app.vanshajpahwa.sonora.resources.clear_canvas_cache
import app.vanshajpahwa.sonora.resources.clear_downloaded_cache
import app.vanshajpahwa.sonora.resources.clear_player_cache
import app.vanshajpahwa.sonora.resources.clear_thumbnail_cache
import app.vanshajpahwa.sonora.resources.content_country
import app.vanshajpahwa.sonora.resources.contributor_email
import app.vanshajpahwa.sonora.resources.contributor_name
import app.vanshajpahwa.sonora.resources.crossfade
import app.vanshajpahwa.sonora.resources.crossfade_auto
import app.vanshajpahwa.sonora.resources.crossfade_description
import app.vanshajpahwa.sonora.resources.crossfade_dj_mode
import app.vanshajpahwa.sonora.resources.crossfade_dj_mode_description
import app.vanshajpahwa.sonora.resources.crossfade_duration
import app.vanshajpahwa.sonora.resources.custom_ai_model_id
import app.vanshajpahwa.sonora.resources.custom_color
import app.vanshajpahwa.sonora.resources.custom_model_id_messages
import app.vanshajpahwa.sonora.resources.daily
import app.vanshajpahwa.sonora.resources.database
import app.vanshajpahwa.sonora.resources.default_models
import app.vanshajpahwa.sonora.resources.description_and_licenses
import app.vanshajpahwa.sonora.resources.developer_blog
import app.vanshajpahwa.sonora.resources.developer_blog_tagline
import app.vanshajpahwa.sonora.resources.discord_integration
import app.vanshajpahwa.sonora.resources.donation
import app.vanshajpahwa.sonora.resources.download_quality
import app.vanshajpahwa.sonora.resources.downloaded_cache
import app.vanshajpahwa.sonora.resources.enable_canvas
import app.vanshajpahwa.sonora.resources.enable_liquid_glass_effect
import app.vanshajpahwa.sonora.resources.enable_liquid_glass_effect_description
import app.vanshajpahwa.sonora.resources.enable_rich_presence
import app.vanshajpahwa.sonora.resources.enable_sponsor_block
import app.vanshajpahwa.sonora.resources.enable_spotify_lyrics
import app.vanshajpahwa.sonora.resources.font_family
import app.vanshajpahwa.sonora.resources.font_system
import app.vanshajpahwa.sonora.resources.free_space
import app.vanshajpahwa.sonora.resources.gemini
import app.vanshajpahwa.sonora.resources.guest
import app.vanshajpahwa.sonora.resources.help_build_lyrics_database
import app.vanshajpahwa.sonora.resources.help_build_lyrics_database_description
import app.vanshajpahwa.sonora.resources.http
import app.vanshajpahwa.sonora.resources.import_data
import app.vanshajpahwa.sonora.resources.import_failed
import app.vanshajpahwa.sonora.resources.import_playlists_from_other_apps
import app.vanshajpahwa.sonora.resources.import_progress_songs
import app.vanshajpahwa.sonora.resources.import_reading_file
import app.vanshajpahwa.sonora.resources.import_result
import app.vanshajpahwa.sonora.resources.import_result_skipped
import app.vanshajpahwa.sonora.resources.intro_login_to_discord
import app.vanshajpahwa.sonora.resources.intro_login_to_spotify
import app.vanshajpahwa.sonora.resources.invalid
import app.vanshajpahwa.sonora.resources.invalid_api_key
import app.vanshajpahwa.sonora.resources.invalid_host
import app.vanshajpahwa.sonora.resources.invalid_language_code
import app.vanshajpahwa.sonora.resources.invalid_port
import app.vanshajpahwa.sonora.resources.keep_backups
import app.vanshajpahwa.sonora.resources.keep_backups_format
import app.vanshajpahwa.sonora.resources.keep_service_alive
import app.vanshajpahwa.sonora.resources.keep_service_alive_description
import app.vanshajpahwa.sonora.resources.keep_your_youtube_playlist_offline
import app.vanshajpahwa.sonora.resources.keep_your_youtube_playlist_offline_description
import app.vanshajpahwa.sonora.resources.kill_service_on_exit
import app.vanshajpahwa.sonora.resources.kill_service_on_exit_description
import app.vanshajpahwa.sonora.resources.language
import app.vanshajpahwa.sonora.resources.last_backup
import app.vanshajpahwa.sonora.resources.last_checked_at
import app.vanshajpahwa.sonora.resources.limit_player_cache
import app.vanshajpahwa.sonora.resources.local_tracking_description
import app.vanshajpahwa.sonora.resources.local_tracking_title
import app.vanshajpahwa.sonora.resources.log_in_to_discord
import app.vanshajpahwa.sonora.resources.log_in_to_spotify
import app.vanshajpahwa.sonora.resources.log_out
import app.vanshajpahwa.sonora.resources.log_out_from_discord
import app.vanshajpahwa.sonora.resources.log_out_from_spotify
import app.vanshajpahwa.sonora.resources.log_out_warning
import app.vanshajpahwa.sonora.resources.logged_in
import app.vanshajpahwa.sonora.resources.logged_in_as
import app.vanshajpahwa.sonora.resources.lrclib
import app.vanshajpahwa.sonora.resources.lyrics
import app.vanshajpahwa.sonora.resources.main_lyrics_provider
import app.vanshajpahwa.sonora.resources.manage_your_youtube_accounts
import app.vanshajpahwa.sonora.resources.monthly
import app.vanshajpahwa.sonora.resources.never
import app.vanshajpahwa.sonora.resources.no_account
import app.vanshajpahwa.sonora.resources.normalize_volume
import app.vanshajpahwa.sonora.resources.not_available_while_casting
import app.vanshajpahwa.sonora.resources.ok
import app.vanshajpahwa.sonora.resources.open_system_equalizer
import app.vanshajpahwa.sonora.resources.openai
import app.vanshajpahwa.sonora.resources.openai_api_compatible
import app.vanshajpahwa.sonora.resources.other_app
import app.vanshajpahwa.sonora.resources.play_explicit_content
import app.vanshajpahwa.sonora.resources.play_explicit_content_description
import app.vanshajpahwa.sonora.resources.playback
import app.vanshajpahwa.sonora.resources.player_cache
import app.vanshajpahwa.sonora.resources.proxy
import app.vanshajpahwa.sonora.resources.proxy_description
import app.vanshajpahwa.sonora.resources.proxy_host
import app.vanshajpahwa.sonora.resources.proxy_host_message
import app.vanshajpahwa.sonora.resources.proxy_password
import app.vanshajpahwa.sonora.resources.proxy_password_message
import app.vanshajpahwa.sonora.resources.proxy_port
import app.vanshajpahwa.sonora.resources.proxy_port_message
import app.vanshajpahwa.sonora.resources.proxy_type
import app.vanshajpahwa.sonora.resources.proxy_username
import app.vanshajpahwa.sonora.resources.proxy_username_message
import app.vanshajpahwa.sonora.resources.quality
import app.vanshajpahwa.sonora.resources.restore_your_data
import app.vanshajpahwa.sonora.resources.restore_your_saved_data
import app.vanshajpahwa.sonora.resources.rich_presence_info
import app.vanshajpahwa.sonora.resources.save
import app.vanshajpahwa.sonora.resources.save_all_your_playlist_data
import app.vanshajpahwa.sonora.resources.save_last_played
import app.vanshajpahwa.sonora.resources.save_last_played_track_and_queue
import app.vanshajpahwa.sonora.resources.save_playback_state
import app.vanshajpahwa.sonora.resources.save_shuffle_and_repeat_mode
import app.vanshajpahwa.sonora.resources.seek_bar_line
import app.vanshajpahwa.sonora.resources.seek_bar_style
import app.vanshajpahwa.sonora.resources.seek_bar_waves
import app.vanshajpahwa.sonora.resources.send_back_listening_data_to_google
import app.vanshajpahwa.sonora.resources.set
import app.vanshajpahwa.sonora.resources.settings
import app.vanshajpahwa.sonora.resources.settings_section_account_region
import app.vanshajpahwa.sonora.resources.settings_section_ai
import app.vanshajpahwa.sonora.resources.settings_section_backup
import app.vanshajpahwa.sonora.resources.settings_section_network
import app.vanshajpahwa.sonora.resources.settings_section_privacy_preferences
import app.vanshajpahwa.sonora.resources.settings_section_streaming_downloads
import app.vanshajpahwa.sonora.resources.signed_in
import app.vanshajpahwa.sonora.resources.sonora_lyrics
import app.vanshajpahwa.sonora.resources.skip_no_music_part
import app.vanshajpahwa.sonora.resources.skip_silent
import app.vanshajpahwa.sonora.resources.skip_sponsor_part_of_video
import app.vanshajpahwa.sonora.resources.socks
import app.vanshajpahwa.sonora.resources.sponsorBlock
import app.vanshajpahwa.sonora.resources.sponsor_block_intro
import app.vanshajpahwa.sonora.resources.spotify
import app.vanshajpahwa.sonora.resources.spotify_canvas_cache
import app.vanshajpahwa.sonora.resources.spotify_lyrícs_info
import app.vanshajpahwa.sonora.resources.storage
import app.vanshajpahwa.sonora.resources.theme
import app.vanshajpahwa.sonora.resources.theme_color
import app.vanshajpahwa.sonora.resources.theme_color_custom
import app.vanshajpahwa.sonora.resources.theme_color_default
import app.vanshajpahwa.sonora.resources.theme_color_wallpaper
import app.vanshajpahwa.sonora.resources.theme_mode_dark
import app.vanshajpahwa.sonora.resources.theme_mode_light
import app.vanshajpahwa.sonora.resources.theme_mode_system
import app.vanshajpahwa.sonora.resources.third_party_libraries
import app.vanshajpahwa.sonora.resources.thumbnail_cache
import app.vanshajpahwa.sonora.resources.translation_language
import app.vanshajpahwa.sonora.resources.translation_language_message
import app.vanshajpahwa.sonora.resources.translucent_bottom_navigation_bar
import app.vanshajpahwa.sonora.resources.translucent_mini_player
import app.vanshajpahwa.sonora.resources.scrolling_titles
import app.vanshajpahwa.sonora.resources.scrolling_titles_description
import app.vanshajpahwa.sonora.resources.translucent_mini_player_description
import app.vanshajpahwa.sonora.resources.unknown
import app.vanshajpahwa.sonora.resources.update_channel
import app.vanshajpahwa.sonora.resources.upload_your_listening_history_to_youtube_music_server_it_will_make_yt_music_recommendation_system_better_working_only_if_logged_in
import app.vanshajpahwa.sonora.resources.use_ai_translation
import app.vanshajpahwa.sonora.resources.use_ai_translation_description
import app.vanshajpahwa.sonora.resources.use_your_system_equalizer
import app.vanshajpahwa.sonora.resources.user_interface
import app.vanshajpahwa.sonora.resources.version
import app.vanshajpahwa.sonora.resources.version_format
import app.vanshajpahwa.sonora.resources.video_download_quality
import app.vanshajpahwa.sonora.resources.video_quality
import app.vanshajpahwa.sonora.resources.warning
import app.vanshajpahwa.sonora.resources.weekly
import app.vanshajpahwa.sonora.resources.what_segments_will_be_skipped
import app.vanshajpahwa.sonora.resources.you_can_see_the_content_below_the_bottom_bar
import app.vanshajpahwa.sonora.resources.youtube_account
import app.vanshajpahwa.sonora.resources.youtube_subtitle_language
import app.vanshajpahwa.sonora.resources.youtube_subtitle_language_message
import app.vanshajpahwa.sonora.resources.youtube_transcript
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalCoilApi::class,
    ExperimentalHazeMaterialsApi::class,
    FormatStringsInDatetimeFormats::class,
    ExperimentalCalfApi::class,
)
@Composable
fun SettingScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
) {
    val platformContext = LocalPlatformContext.current
    val pl = com.mohamedrejeb.calf.core.LocalPlatformContext.current
    val localDensity = LocalDensity.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    var width by rememberSaveable { mutableIntStateOf(0) }

    // Backup and restore
    val formatter =
        LocalDateTime.Format {
            byUnicodePattern("yyyyMMddHHmmss")
        }
    val appName = stringResource(Res.string.app_name)

    val backupLauncher =
        fileSaverResult(
            "${appName}_${
                now().format(
                    formatter,
                )
            }.backup",
            "application/octet-stream",
        ) { uri ->
            uri?.let {
                viewModel.backup(it.toKmpUri())
            }
        }

    val restoreLauncher =
        rememberFilePickerLauncher(
            type =
                FilePickerFileType.All,
            selectionMode = FilePickerSelectionMode.Single,
        ) { file ->
            file.firstOrNull()?.getPath(pl)?.toKmpUri()?.let {
                viewModel.restore(it)
            }
        }

    // Import playlists converted on the web. Unlike restore, the file is read through Calf's
    // KmpFile rather than a Uri, so no expect/actual is needed. The type stays All because a
    // converted.json arrives with whatever MIME its source assigned it, and an application/json
    // filter would hide it on some hosts.
    val importViewModel: ImportViewModel = koinViewModel()
    val importState by importViewModel.importState.collectAsStateWithLifecycle()
    val importLauncher =
        rememberFilePickerLauncher(
            type =
                FilePickerFileType.All,
            selectionMode = FilePickerSelectionMode.Single,
        ) { file ->
            file.firstOrNull()?.let {
                importViewModel.import(it, pl)
            }
        }

    // Open equalizer
    val resultLauncher = openEqResult(viewModel.getAudioSessionId())

    val enableTranslucentNavBar by remember { viewModel.translucentBottomBar.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val enableTranslucentMiniPlayer by remember { viewModel.translucentMiniPlayer.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val enableTitleScrolling by remember { viewModel.titleScrolling.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = true)
    val language by viewModel.language.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()
    val quality by viewModel.quality.collectAsStateWithLifecycle()
    val downloadQuality by viewModel.downloadQuality.collectAsStateWithLifecycle()
    val videoDownloadQuality by viewModel.videoDownloadQuality.collectAsStateWithLifecycle()
    val keepYoutubePlaylistOffline by viewModel.keepYouTubePlaylistOffline.collectAsStateWithLifecycle()
    val localTrackingEnabled by viewModel.localTrackingEnabled.collectAsStateWithLifecycle(initialValue = false)
    val blogNotificationEnabled by viewModel.blogNotificationEnabled.collectAsStateWithLifecycle()
    val combineLocalAndYouTubeLiked by viewModel.combineLocalAndYouTubeLiked.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()
    val sendData by remember { viewModel.sendBackToGoogle.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val normalizeVolume by remember { viewModel.normalizeVolume.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val skipSilent by remember { viewModel.skipSilent.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val savePlaybackState by remember { viewModel.savedPlaybackState.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val saveLastPlayed by remember { viewModel.saveRecentSongAndQueue.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val killServiceOnExit by remember { viewModel.killServiceOnExit.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = true)
    val mainLyricsProvider by viewModel.mainLyricsProvider.collectAsStateWithLifecycle()
    val youtubeSubtitleLanguage by viewModel.youtubeSubtitleLanguage.collectAsStateWithLifecycle()
    val spotifyLoggedIn by viewModel.spotifyLogIn.collectAsStateWithLifecycle()
    val spotifyLyrics by viewModel.spotifyLyrics.collectAsStateWithLifecycle()
    val spotifyCanvas by viewModel.spotifyCanvas.collectAsStateWithLifecycle()
    val enableSponsorBlock by remember { viewModel.sponsorBlockEnabled.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val skipSegments by viewModel.sponsorBlockCategories.collectAsStateWithLifecycle()
    val playerCache by viewModel.cacheSize.collectAsStateWithLifecycle()
    val downloadedCache by viewModel.downloadedCacheSize.collectAsStateWithLifecycle()
    val thumbnailCache by viewModel.thumbCacheSize.collectAsStateWithLifecycle()
    val canvasCache by viewModel.canvasCacheSize.collectAsStateWithLifecycle()
    val limitPlayerCache by viewModel.playerCacheLimit.collectAsStateWithLifecycle()
    val fraction by viewModel.fraction.collectAsStateWithLifecycle()
    val lastCheckUpdate by viewModel.lastCheckForUpdate.collectAsStateWithLifecycle()
    val explicitContentEnabled by viewModel.explicitContentEnabled.collectAsStateWithLifecycle()
    val usingProxy by viewModel.usingProxy.collectAsStateWithLifecycle()
    val proxyType by viewModel.proxyType.collectAsStateWithLifecycle()
    val proxyHost by viewModel.proxyHost.collectAsStateWithLifecycle()
    val proxyPort by viewModel.proxyPort.collectAsStateWithLifecycle()
    val proxyUsername by viewModel.proxyUsername.collectAsStateWithLifecycle()
    val proxyPassword by viewModel.proxyPassword.collectAsStateWithLifecycle()
    val autoCheckUpdate by viewModel.autoCheckUpdate.collectAsStateWithLifecycle()
    val aiProvider by viewModel.aiProvider.collectAsStateWithLifecycle()
    val isHasApiKey by viewModel.isHasApiKey.collectAsStateWithLifecycle()
    val useAITranslation by viewModel.useAITranslation.collectAsStateWithLifecycle()
    val translationLanguage by viewModel.translationLanguage.collectAsStateWithLifecycle()
    val customModelId by viewModel.customModelId.collectAsStateWithLifecycle()
    val customOpenAIBaseUrl by viewModel.customOpenAIBaseUrl.collectAsStateWithLifecycle()
    val customOpenAIHeaders by viewModel.customOpenAIHeaders.collectAsStateWithLifecycle()
    val helpBuildLyricsDatabase by viewModel.helpBuildLyricsDatabase.collectAsStateWithLifecycle()
    val contributor by viewModel.contributor.collectAsStateWithLifecycle()
    val backupDownloaded by viewModel.backupDownloaded.collectAsStateWithLifecycle()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsStateWithLifecycle()
    val autoBackupFrequency by viewModel.autoBackupFrequency.collectAsStateWithLifecycle()
    val autoBackupMaxFiles by viewModel.autoBackupMaxFiles.collectAsStateWithLifecycle()
    val autoBackupLastTime by viewModel.autoBackupLastTime.collectAsStateWithLifecycle()
    val updateChannel by viewModel.updateChannel.collectAsStateWithLifecycle()
    val enableLiquidGlass by viewModel.enableLiquidGlass.collectAsStateWithLifecycle()
    val themeMode by sharedViewModel.getThemeMode().collectAsStateWithLifecycle(DataStoreManager.THEME_MODE_DARK)
    val themeColorSource by sharedViewModel.getThemeColorSource().collectAsStateWithLifecycle(DataStoreManager.THEME_COLOR_DEFAULT)
    val fontChoice by sharedViewModel.getFontFamilyChoice().collectAsStateWithLifecycle(DataStoreManager.FONT_MANROPE)
    val blurredBackdrop by sharedViewModel.getBlurredPlayerBackdrop().collectAsStateWithLifecycle(false)
    val seekBarStyle by sharedViewModel.getSeekBarStyle().collectAsStateWithLifecycle(DataStoreManager.SEEK_BAR_WAVES)
    val enableSpinArtwork by remember { viewModel.spinArtwork.map { it == TRUE } }.collectAsStateWithLifecycle(initialValue = false)
    val artworkStyle by
        sharedViewModel
            .getPlayerArtworkStyle()
            .collectAsStateWithLifecycle(DataStoreManager.ARTWORK_STYLE_RING)
    val customThemeColorHex by sharedViewModel.getCustomThemeColor().collectAsStateWithLifecycle(DataStoreManager.DEFAULT_THEME_COLOR_HEX)
    var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }
    val discordLoggedIn by viewModel.discordLoggedIn.collectAsStateWithLifecycle()
    val richPresenceEnabled by viewModel.richPresenceEnabled.collectAsStateWithLifecycle()
    val keepServiceAlive by viewModel.keepServiceAlive.collectAsStateWithLifecycle()

    val crossfadeEnabled by viewModel.crossfadeEnabled.collectAsStateWithLifecycle()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsStateWithLifecycle()
    val crossfadeDjMode by viewModel.crossfadeDjMode.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()

    val isCheckingUpdate by sharedViewModel.isCheckingUpdate.collectAsStateWithLifecycle()

    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )

    val checkForUpdateSubtitle by remember {
        derivedStateOf {
            if (isCheckingUpdate) {
                return@derivedStateOf runBlocking { getString(Res.string.checking) }
            } else {
                val lastCheckLong = lastCheckUpdate?.toLong() ?: 0L
                return@derivedStateOf runBlocking {
                    getString(
                        Res.string.last_checked_at,
                        DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneId.systemDefault())
                            .format(Instant.ofEpochMilli(lastCheckLong)),
                    )
                }
            }
        }
    }
    var showYouTubeAccountDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showThirdPartyLibraries by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        viewModel.getAllGoogleAccount()
    }

    var settingsQuery by rememberSaveable { mutableStateOf("") }

    // Settings gets its own backdrop rather than reusing the app level one, for the same reason
    // Library and Home do: the nav graph is already wrapped in a layerBackdrop, so glass drawn
    // inside it would be sampling the layer it is drawing into and take the RuntimeShader down.
    // The source below is a sibling of the section cards, never an ancestor.
    val settingsGlass = enableLiquidGlass && getPlatform() == Platform.Android
    val settingsBackdrop = rememberBackdrop(MaterialTheme.colorScheme.background)
    val sectionBackdrop = settingsBackdrop.takeIf { settingsGlass }

    LaunchedEffect(true) {
        viewModel.getData()
        viewModel.getThumbCacheSize(platformContext)
    }

    CompositionLocalProvider(LocalSettingsSearchQuery provides settingsQuery) {
    Box(Modifier.fillMaxSize()) {
        if (settingsGlass) {
            // Drawn first, so it is underneath the list and beside the cards in the tree. Glass
            // needs something with tonal variation to refract; a flat surface reads as a plain
            // translucent panel, so this gives it a gentle ramp to bend.
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .layerBackdrop(settingsBackdrop)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    MaterialTheme.colorScheme.background,
                                ),
                            ),
                        ),
            )
        }
    LazyColumn(
        contentPadding = innerPadding,
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .hazeSource(hazeState),
    ) {
        item {
            Spacer(Modifier.height(64.dp))
        }
        item(key = "settings_search") {
            SettingsSearchField(
                query = settingsQuery,
                onQueryChange = { settingsQuery = it },
            )
        }
        item(key = "user_interface") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.user_interface))
                val themeModeLabels =
                    listOf(
                        DataStoreManager.THEME_MODE_SYSTEM to stringResource(Res.string.theme_mode_system),
                        DataStoreManager.THEME_MODE_DARK to stringResource(Res.string.theme_mode_dark),
                        DataStoreManager.THEME_MODE_LIGHT to stringResource(Res.string.theme_mode_light),
                    )
                SettingItem(
                    title = stringResource(Res.string.theme),
                    subtitle = themeModeLabels.firstOrNull { it.first == themeMode }?.second ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.theme) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect = themeModeLabels.map { (it.first == themeMode) to it.second },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        val selected = state.selectOne?.getSelected()
                                        themeModeLabels.firstOrNull { it.second == selected }?.first?.let {
                                            sharedViewModel.setThemeMode(it)
                                        }
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                val colorSourceLabels =
                    buildList {
                        add(DataStoreManager.THEME_COLOR_DEFAULT to stringResource(Res.string.theme_color_default))
                        if (isWallpaperDynamicColorSupported()) {
                            add(DataStoreManager.THEME_COLOR_WALLPAPER to stringResource(Res.string.theme_color_wallpaper))
                        }
                        add(DataStoreManager.THEME_COLOR_CUSTOM to stringResource(Res.string.theme_color_custom))
                    }
                SettingItem(
                    title = stringResource(Res.string.theme_color),
                    subtitle = colorSourceLabels.firstOrNull { it.first == themeColorSource }?.second ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.theme_color) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect = colorSourceLabels.map { (it.first == themeColorSource) to it.second },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        val selected = state.selectOne?.getSelected()
                                        colorSourceLabels.firstOrNull { it.second == selected }?.first?.let {
                                            sharedViewModel.setThemeColorSource(it)
                                            if (it == DataStoreManager.THEME_COLOR_CUSTOM) {
                                                showColorPickerDialog = true
                                            }
                                        }
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                // Both of these are visual choices, so they are picked inline as a radio group
                // rather than through a select-one dialog. The previews are the whole point of these
                // two settings, and a dialog would replace them with a list of words at the exact
                // moment the user is trying to compare them.
                val seekBarChoices =
                    listOf(
                        SettingChoice(
                            value = DataStoreManager.SEEK_BAR_WAVES,
                            label = stringResource(Res.string.seek_bar_waves),
                        ) {
                            WaveformTrack(
                                progress = 0.45f,
                                seed = 20260806L,
                                modifier = Modifier.fillMaxWidth().height(28.dp),
                                playedColor = MaterialTheme.colorScheme.primary,
                                unplayedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                            )
                        },
                        SettingChoice(
                            value = DataStoreManager.SEEK_BAR_LINE,
                            label = stringResource(Res.string.seek_bar_line),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(SonoraShapes.Container)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.26f)),
                                )
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth(0.45f)
                                            .height(4.dp)
                                            .clip(SonoraShapes.Container)
                                            .background(MaterialTheme.colorScheme.primary),
                                )
                            }
                        },
                    )
                SettingItem(
                    title = stringResource(Res.string.seek_bar_style),
                    subtitle = seekBarChoices.firstOrNull { it.value == seekBarStyle }?.label ?: "",
                    otherView = {
                        SettingChoiceRow(
                            choices = seekBarChoices,
                            selected = seekBarStyle,
                            onSelect = { sharedViewModel.setSeekBarStyle(it) },
                        )
                    },
                )
                val artworkChoices =
                    listOf(
                        SettingChoice(
                            value = DataStoreManager.ARTWORK_STYLE_RING,
                            label = stringResource(Res.string.artwork_style_ring),
                        ) {
                            val accent = MaterialTheme.colorScheme.primary
                            Canvas(modifier = Modifier.size(46.dp)) {
                                val stroke = 3.dp.toPx()
                                val arcSize = Size(size.width - stroke, size.height - stroke)
                                val topLeft = Offset(stroke / 2f, stroke / 2f)
                                // Same 80 degree bottom gap and start angle the player uses, so the
                                // preview reads as the control it stands for.
                                drawArc(
                                    color = accent.copy(alpha = 0.26f),
                                    startAngle = 130f,
                                    sweepAngle = 280f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                                )
                                drawArc(
                                    color = accent,
                                    startAngle = 130f,
                                    sweepAngle = 280f * 0.45f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                                )
                                // The image, inset inside the ring exactly as it is in the player.
                                drawCircle(
                                    color = accent.copy(alpha = 0.55f),
                                    radius = size.minDimension / 2f - 7.dp.toPx(),
                                )
                            }
                        },
                        SettingChoice(
                            value = DataStoreManager.ARTWORK_STYLE_SQUARE,
                            label = stringResource(Res.string.artwork_style_square),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(46.dp)
                                        .clip(SonoraShapes.Media)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
                            )
                        },
                    )
                SettingItem(
                    title = stringResource(Res.string.artwork_style),
                    subtitle = artworkChoices.firstOrNull { it.value == artworkStyle }?.label ?: "",
                    otherView = {
                        Column {
                            SettingChoiceRow(
                                choices = artworkChoices,
                                selected = artworkStyle,
                                onSelect = { sharedViewModel.setPlayerArtworkStyle(it) },
                            )
                            Spacer(Modifier.height(10.dp))
                            // Says up front that this setting does not apply to video, rather than
                            // leaving the user to notice the shape ignoring them and assume it broke.
                            Text(
                                text = stringResource(Res.string.artwork_style_note),
                                style = typo().bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
                // Only with the ring shape. The square has no disc to turn, so the control would be
                // present and do nothing, which is worse than not offering it.
                if (artworkStyle == DataStoreManager.ARTWORK_STYLE_RING) {
                    SettingItem(
                        title = stringResource(Res.string.spin_artwork),
                        subtitle = stringResource(Res.string.spin_artwork_description),
                        switch = (enableSpinArtwork to { viewModel.setSpinArtwork(it) }),
                    )
                }
                SettingItem(
                    title = stringResource(Res.string.blurred_backdrop),
                    subtitle = stringResource(Res.string.blurred_backdrop_description),
                    switch = (blurredBackdrop to { sharedViewModel.setBlurredPlayerBackdrop(it) }),
                )
                val fontLabels =
                    listOf(
                        DataStoreManager.FONT_MANROPE to "Manrope",
                        DataStoreManager.FONT_JAKARTA to "Plus Jakarta Sans",
                        DataStoreManager.FONT_OUTFIT to "Outfit",
                        DataStoreManager.FONT_SPACE_GROTESK to "Space Grotesk",
                        DataStoreManager.FONT_MONTSERRAT to "Montserrat",
                        DataStoreManager.FONT_INTER to "Inter",
                        DataStoreManager.FONT_POPPINS to "Poppins",
                        DataStoreManager.FONT_BRICOLAGE to "Bricolage Grotesque",
                        DataStoreManager.FONT_FIGTREE to "Figtree",
                        DataStoreManager.FONT_SYSTEM to stringResource(Res.string.font_system),
                    )
                SettingItem(
                    title = stringResource(Res.string.font_family),
                    subtitle = fontLabels.firstOrNull { it.first == fontChoice }?.second ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.font_family) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect = fontLabels.map { (it.first == fontChoice) to it.second },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        val selected = state.selectOne?.getSelected()
                                        fontLabels.firstOrNull { it.second == selected }?.first?.let {
                                            sharedViewModel.setFontFamilyChoice(it)
                                        }
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                if (themeColorSource == DataStoreManager.THEME_COLOR_CUSTOM) {
                    SettingItem(
                        title = stringResource(Res.string.custom_color),
                        subtitle = "#${customThemeColorHex.takeLast(6)}",
                        onClick = { showColorPickerDialog = true },
                    )
                }
                SettingItem(
                    title = stringResource(Res.string.translucent_bottom_navigation_bar),
                    subtitle = stringResource(Res.string.you_can_see_the_content_below_the_bottom_bar),
                    switch = (enableTranslucentNavBar to { viewModel.setTranslucentBottomBar(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.translucent_mini_player),
                    subtitle = stringResource(Res.string.translucent_mini_player_description),
                    switch = (enableTranslucentMiniPlayer to { viewModel.setTranslucentMiniPlayer(it) }),
                    // The mini player is fully transparent under liquid glass already, so this would
                    // be a control with nothing to do.
                    isEnable = !enableLiquidGlass,
                )
                SettingItem(
                    title = stringResource(Res.string.scrolling_titles),
                    subtitle = stringResource(Res.string.scrolling_titles_description),
                    switch = (enableTitleScrolling to { viewModel.setTitleScrolling(it) }),
                )
                if (getPlatform() == Platform.Android) {
                    SettingItem(
                        title = stringResource(Res.string.enable_liquid_glass_effect),
                        subtitle = stringResource(Res.string.enable_liquid_glass_effect_description),
                        switch = (enableLiquidGlass to { viewModel.setEnableLiquidGlass(it) }),
                        isEnable = getPlatform() == Platform.Android,
                    )
                }
            }
        }
        item(key = "content") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_account_region))
                SettingItem(
                    title = stringResource(Res.string.youtube_account),
                    subtitle = stringResource(Res.string.manage_your_youtube_accounts),
                    onClick = {
                        viewModel.getAllGoogleAccount()
                        showYouTubeAccountDialog = true
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.language),
                    subtitle = SUPPORTED_LANGUAGE.getLanguageFromCode(language ?: "en-US"),
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.language) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            SUPPORTED_LANGUAGE.items.map {
                                                (it.toString() == SUPPORTED_LANGUAGE.getLanguageFromCode(language ?: "en-US")) to it.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        val code = SUPPORTED_LANGUAGE.getCodeFromLanguage(state.selectOne?.getSelected() ?: "English")
                                        viewModel.setBasicAlertData(
                                            SettingBasicAlertState(
                                                title = runBlocking { getString(Res.string.warning) },
                                                message = runBlocking { getString(Res.string.change_language_warning) },
                                                confirm =
                                                    runBlocking { getString(Res.string.change) } to {
                                                        sharedViewModel.activityRecreate()
                                                        viewModel.setBasicAlertData(null)
                                                        viewModel.changeLanguage(code)
                                                    },
                                                dismiss = runBlocking { getString(Res.string.cancel) },
                                            ),
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.content_country),
                    subtitle = location ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.content_country) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            SUPPORTED_LOCATION.items.map { item ->
                                                (item.toString() == location) to item.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.changeLocation(
                                            state.selectOne?.getSelected() ?: "US",
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
            }
        }
        item(key = "streaming") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_streaming_downloads))
                SettingItem(
                    title = stringResource(Res.string.quality),
                    subtitle = quality ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.quality) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            QUALITY.items.map { item ->
                                                (item.toString() == quality) to item.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.changeQuality(state.selectOne?.getSelected())
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.download_quality),
                    subtitle = downloadQuality ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.download_quality) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            QUALITY.items.map { item ->
                                                (item.toString() == downloadQuality) to item.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        state.selectOne?.getSelected()?.let { viewModel.setDownloadQuality(it) }
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.video_quality),
                    subtitle = videoQuality ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.video_quality) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            VIDEO_QUALITY.items.map { item ->
                                                (item.toString() == videoQuality) to item.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.changeVideoQuality(state.selectOne?.getSelected() ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.video_download_quality),
                    subtitle = videoDownloadQuality ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.video_download_quality) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            VIDEO_QUALITY.items.map { item ->
                                                (item.toString() == videoDownloadQuality) to item.toString()
                                            },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setVideoDownloadQuality(state.selectOne?.getSelected() ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
            }
        }
        item(key = "privacy") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_privacy_preferences))
                SettingItem(
                    title = stringResource(Res.string.send_back_listening_data_to_google),
                    subtitle =
                        stringResource(
                            Res.string
                                .upload_your_listening_history_to_youtube_music_server_it_will_make_yt_music_recommendation_system_better_working_only_if_logged_in,
                        ),
                    switch = (sendData to { viewModel.setSendBackToGoogle(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.play_explicit_content),
                    subtitle = stringResource(Res.string.play_explicit_content_description),
                    switch = (explicitContentEnabled to { viewModel.setExplicitContentEnabled(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.keep_your_youtube_playlist_offline),
                    subtitle = stringResource(Res.string.keep_your_youtube_playlist_offline_description),
                    switch = (keepYoutubePlaylistOffline to { viewModel.setKeepYouTubePlaylistOffline(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.local_tracking_title),
                    subtitle = stringResource(Res.string.local_tracking_description),
                    switch = (localTrackingEnabled to { viewModel.setLocalTrackingEnabled(it) }),
                )
                /*
                SettingItem(
                    title = stringResource(Res.string.combine_local_and_youtube_liked_songs),
                    subtitle = stringResource(Res.string.combine_local_and_youtube_liked_songs_description),
                    switch = (combineLocalAndYouTubeLiked to { viewModel.setCombineLocalAndYouTubeLiked(it) })
                )
                 */
            }
        }
        item(key = "network") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_network))
                SettingItem(
                    title = stringResource(Res.string.proxy),
                    subtitle = stringResource(Res.string.proxy_description),
                    switch = (usingProxy to { viewModel.setUsingProxy(it) }),
                )
            }
        }
        item(key = "proxy") {
            Crossfade(usingProxy) { it ->
                if (it) {
                    Column {
                        SettingItem(
                            title = stringResource(Res.string.proxy_type),
                            subtitle =
                                when (proxyType) {
                                    DataStoreManager.ProxyType.PROXY_TYPE_HTTP -> stringResource(Res.string.http)
                                    DataStoreManager.ProxyType.PROXY_TYPE_SOCKS -> stringResource(Res.string.socks)
                                },
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.proxy_type) },
                                        selectOne =
                                            SettingAlertState.SelectData(
                                                listSelect =
                                                    listOf(
                                                        (proxyType == DataStoreManager.ProxyType.PROXY_TYPE_HTTP) to
                                                            runBlocking {
                                                                getString(
                                                                    Res.string.http,
                                                                )
                                                            },
                                                        (proxyType == DataStoreManager.ProxyType.PROXY_TYPE_SOCKS) to
                                                            runBlocking { getString(Res.string.socks) },
                                                    ),
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                viewModel.setProxy(
                                                    if (state.selectOne?.getSelected() == runBlocking { getString(Res.string.socks) }) {
                                                        DataStoreManager.ProxyType.PROXY_TYPE_SOCKS
                                                    } else {
                                                        DataStoreManager.ProxyType.PROXY_TYPE_HTTP
                                                    },
                                                    proxyHost,
                                                    proxyPort,
                                                )
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
                        SettingItem(
                            title = stringResource(Res.string.proxy_host),
                            subtitle = proxyHost,
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.proxy_host) },
                                        message = runBlocking { getString(Res.string.proxy_host_message) },
                                        textField =
                                            SettingAlertState.TextFieldData(
                                                label = runBlocking { getString(Res.string.proxy_host) },
                                                value = proxyHost,
                                                verifyCodeBlock = {
                                                    isValidProxyHost(it) to runBlocking { getString(Res.string.invalid_host) }
                                                },
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                viewModel.setProxy(
                                                    proxyType,
                                                    state.textField?.value ?: "",
                                                    proxyPort,
                                                )
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
                        SettingItem(
                            title = stringResource(Res.string.proxy_port),
                            subtitle = proxyPort.toString(),
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.proxy_port) },
                                        message = runBlocking { getString(Res.string.proxy_port_message) },
                                        textField =
                                            SettingAlertState.TextFieldData(
                                                label = runBlocking { getString(Res.string.proxy_port) },
                                                value = proxyPort.toString(),
                                                verifyCodeBlock = {
                                                    (it.toIntOrNull() != null) to runBlocking { getString(Res.string.invalid_port) }
                                                },
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                viewModel.setProxy(
                                                    proxyType,
                                                    proxyHost,
                                                    state.textField?.value?.toIntOrNull() ?: 0,
                                                )
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
                        SettingItem(
                            title = stringResource(Res.string.proxy_username),
                            subtitle = proxyUsername,
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.proxy_username) },
                                        message = runBlocking { getString(Res.string.proxy_username_message) },
                                        textField =
                                            SettingAlertState.TextFieldData(
                                                label = runBlocking { getString(Res.string.proxy_username) },
                                                value = proxyUsername,
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                viewModel.setProxyCredentials(
                                                    state.textField?.value ?: "",
                                                    proxyPassword,
                                                )
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
                        SettingItem(
                            title = stringResource(Res.string.proxy_password),
                            subtitle =
                                if (proxyPassword.isEmpty()) {
                                    ""
                                } else {
                                    "\u2022".repeat(proxyPassword.length)
                                },
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.proxy_password) },
                                        message = runBlocking { getString(Res.string.proxy_password_message) },
                                        textField =
                                            SettingAlertState.TextFieldData(
                                                label = runBlocking { getString(Res.string.proxy_password) },
                                                value = proxyPassword,
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                viewModel.setProxyCredentials(
                                                    proxyUsername,
                                                    state.textField?.value ?: "",
                                                )
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
        if (getPlatform() == Platform.Android) {
            item(key = "audio") {
                Column(SettingsSectionModifier(sectionBackdrop)) {
                    SettingSectionHeader(stringResource(Res.string.audio))
                    SettingItem(
                        title = stringResource(Res.string.normalize_volume),
                        subtitle = stringResource(Res.string.balance_media_loudness),
                        switch = (normalizeVolume to { viewModel.setNormalizeVolume(it) }),
                    )
                    SettingItem(
                        title = stringResource(Res.string.skip_silent),
                        subtitle = stringResource(Res.string.skip_no_music_part),
                        switch = (skipSilent to { viewModel.setSkipSilent(it) }),
                    )
                    SettingItem(
                        title = stringResource(Res.string.open_system_equalizer),
                        subtitle =
                            if (castState.isRemote) {
                                stringResource(Res.string.not_available_while_casting)
                            } else {
                                stringResource(Res.string.use_your_system_equalizer)
                            },
                        isEnable = !castState.isRemote,
                        onClick = {
                            coroutineScope.launch {
                                resultLauncher.launch()
                            }
                        },
                    )
                }
            }
        }
        item(key = "playback") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.playback))
                SettingItem(
                    title = stringResource(Res.string.save_playback_state),
                    subtitle = stringResource(Res.string.save_shuffle_and_repeat_mode),
                    switch = (savePlaybackState to { viewModel.setSavedPlaybackState(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.save_last_played),
                    subtitle = stringResource(Res.string.save_last_played_track_and_queue),
                    switch = (saveLastPlayed to { viewModel.setSaveLastPlayed(it) }),
                )
                if (getPlatform() == Platform.Android) {
                    SettingItem(
                        title = stringResource(Res.string.kill_service_on_exit),
                        subtitle = stringResource(Res.string.kill_service_on_exit_description),
                        switch = (killServiceOnExit to { viewModel.setKillServiceOnExit(it) }),
                    )
                    SettingItem(
                        title = stringResource(Res.string.keep_service_alive),
                        subtitle = stringResource(Res.string.keep_service_alive_description),
                        switch = (keepServiceAlive to { viewModel.setKeepServiceAlive(it) }),
                    )
                }
            }
        }
        // Crossfade Settings (all platforms)
        item(key = "crossfade_settings") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingItem(
                    title = stringResource(Res.string.crossfade),
                    subtitle =
                        if (castState.isRemote) {
                            stringResource(Res.string.not_available_while_casting)
                        } else {
                            stringResource(Res.string.crossfade_description)
                        },
                    switch = (crossfadeEnabled to { viewModel.setCrossfadeEnabled(it) }),
                    isEnable = !castState.isRemote,
                )
                AnimatedVisibility(visible = crossfadeEnabled) {
                    Column {
                        SettingItem(
                            title = stringResource(Res.string.crossfade_duration),
                            subtitle =
                                if (castState.isRemote) {
                                    stringResource(Res.string.not_available_while_casting)
                                } else if (crossfadeDuration == DataStoreManager.CROSSFADE_DURATION_AUTO) {
                                    stringResource(Res.string.crossfade_auto)
                                } else {
                                    "${crossfadeDuration / 1000}s"
                                },
                            isEnable = !castState.isRemote,
                            onClick = {
                                viewModel.setAlertData(
                                    SettingAlertState(
                                        title = runBlocking { getString(Res.string.crossfade_duration) },
                                        selectOne =
                                            SettingAlertState.SelectData(
                                                listSelect =
                                                    listOf(
                                                        (crossfadeDuration == DataStoreManager.CROSSFADE_DURATION_AUTO) to
                                                            runBlocking { getString(Res.string.crossfade_auto) },
                                                        (crossfadeDuration == 1000) to "1s",
                                                        (crossfadeDuration == 2000) to "2s",
                                                        (crossfadeDuration == 3000) to "3s",
                                                        (crossfadeDuration == 5000) to "5s",
                                                        (crossfadeDuration == 8000) to "8s",
                                                        (crossfadeDuration == 10000) to "10s",
                                                        (crossfadeDuration == 12000) to "12s",
                                                        (crossfadeDuration == 15000) to "15s",
                                                        (crossfadeDuration == 20000) to "20s",
                                                        (crossfadeDuration == 30000) to "30s",
                                                    ),
                                            ),
                                        confirm =
                                            runBlocking { getString(Res.string.change) } to { state ->
                                                val duration =
                                                    when (state.selectOne?.getSelected()) {
                                                        runBlocking {
                                                            getString(
                                                                Res.string.crossfade_auto,
                                                            )
                                                        },
                                                        -> DataStoreManager.CROSSFADE_DURATION_AUTO
                                                        "1s" -> 1000
                                                        "2s" -> 2000
                                                        "3s" -> 3000
                                                        "5s" -> 5000
                                                        "8s" -> 8000
                                                        "10s" -> 10000
                                                        "12s" -> 12000
                                                        "15s" -> 15000
                                                        "20s" -> 20000
                                                        "30s" -> 30000
                                                        else -> 5000
                                                    }
                                                viewModel.setCrossfadeDuration(duration)
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            },
                        )
//                        if (getPlatform() == Platform.Android) {
                        SettingItem(
                            title = stringResource(Res.string.crossfade_dj_mode),
                            subtitle =
                                if (castState.isRemote) {
                                    stringResource(Res.string.not_available_while_casting)
                                } else {
                                    stringResource(Res.string.crossfade_dj_mode_description)
                                },
                            switch = ((crossfadeDjMode) to { viewModel.setCrossfadeDjMode(it) }),
                            isEnable = !castState.isRemote,
                        )
//                        }
                    }
                }
            }
        }
        item(key = "lyrics") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.lyrics))
                SettingItem(
                    title = stringResource(Res.string.main_lyrics_provider),
                    subtitle =
                        when (mainLyricsProvider) {
                            DataStoreManager.SONORA -> stringResource(Res.string.sonora_lyrics)
                            DataStoreManager.YOUTUBE -> stringResource(Res.string.youtube_transcript)
                            DataStoreManager.LRCLIB -> stringResource(Res.string.lrclib)
                            DataStoreManager.BETTER_LYRICS -> stringResource(Res.string.better_lyrics)
                            else -> stringResource(Res.string.unknown)
                        },
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.main_lyrics_provider) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            listOf(
                                                (mainLyricsProvider == DataStoreManager.SONORA) to
                                                    runBlocking { getString(Res.string.sonora_lyrics) },
                                                (mainLyricsProvider == DataStoreManager.YOUTUBE) to
                                                    runBlocking { getString(Res.string.youtube_transcript) },
                                                (mainLyricsProvider == DataStoreManager.LRCLIB) to runBlocking { getString(Res.string.lrclib) },
                                                (mainLyricsProvider == DataStoreManager.BETTER_LYRICS) to
                                                    runBlocking { getString(Res.string.better_lyrics) },
                                            ),
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setLyricsProvider(
                                            when (state.selectOne?.getSelected()) {
                                                runBlocking { getString(Res.string.sonora_lyrics) } -> DataStoreManager.SONORA
                                                runBlocking { getString(Res.string.youtube_transcript) } -> DataStoreManager.YOUTUBE
                                                runBlocking { getString(Res.string.lrclib) } -> DataStoreManager.LRCLIB
                                                runBlocking { getString(Res.string.better_lyrics) } -> DataStoreManager.BETTER_LYRICS
                                                else -> DataStoreManager.SONORA
                                            },
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )

                SettingItem(
                    title = stringResource(Res.string.translation_language),
                    subtitle = translationLanguage ?: "",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.translation_language) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.translation_language) },
                                        value = translationLanguage ?: "",
                                        verifyCodeBlock = {
                                            (it.length == 2 && it.isTwoLetterCode()) to
                                                runBlocking { getString(Res.string.invalid_language_code) }
                                        },
                                    ),
                                message = runBlocking { getString(Res.string.translation_language_message) },
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setTranslationLanguage(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                    isEnable = true,
                )
                SettingItem(
                    title = stringResource(Res.string.youtube_subtitle_language),
                    subtitle = youtubeSubtitleLanguage,
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.youtube_subtitle_language) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.youtube_subtitle_language) },
                                        value = youtubeSubtitleLanguage,
                                        verifyCodeBlock = {
                                            (it.length == 2 && it.isTwoLetterCode()) to
                                                runBlocking { getString(Res.string.invalid_language_code) }
                                        },
                                    ),
                                message = runBlocking { getString(Res.string.youtube_subtitle_language_message) },
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setYoutubeSubtitleLanguage(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.help_build_lyrics_database),
                    subtitle = stringResource(Res.string.help_build_lyrics_database_description),
                    switch = (helpBuildLyricsDatabase to { viewModel.setHelpBuildLyricsDatabase(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.contributor_name),
                    subtitle = contributor.first.ifEmpty { stringResource(Res.string.anonymous) },
                    isEnable = helpBuildLyricsDatabase,
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.contributor_name) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.contributor_name) },
                                        value = "",
                                    ),
                                message = "",
                                confirm =
                                    runBlocking { getString(Res.string.set) } to { state ->
                                        viewModel.setContributorName(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.contributor_email),
                    subtitle = contributor.second.ifEmpty { stringResource(Res.string.anonymous) },
                    isEnable = helpBuildLyricsDatabase,
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.contributor_email) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.contributor_email) },
                                        value = "",
                                        verifyCodeBlock = {
                                            if (it.isNotEmpty()) {
                                                (it.contains("@")) to runBlocking { getString(Res.string.invalid) }
                                            } else {
                                                true to ""
                                            }
                                        },
                                    ),
                                message = "",
                                confirm =
                                    runBlocking { getString(Res.string.set) } to { state ->
                                        viewModel.setContributorEmail(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
            }
        }
        item(key = "AI") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_ai))
                SettingItem(
                    title = stringResource(Res.string.ai_provider),
                    subtitle =
                        when (aiProvider) {
                            DataStoreManager.AI_PROVIDER_OPENAI -> stringResource(Res.string.openai)
                            DataStoreManager.AI_PROVIDER_GEMINI -> stringResource(Res.string.gemini)
                            DataStoreManager.AI_PROVIDER_CUSTOM_OPENAI -> stringResource(Res.string.openai_api_compatible)
                            else -> stringResource(Res.string.unknown)
                        },
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.ai_provider) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            listOf(
                                                (mainLyricsProvider == DataStoreManager.AI_PROVIDER_OPENAI) to
                                                    runBlocking { getString(Res.string.openai) },
                                                (mainLyricsProvider == DataStoreManager.AI_PROVIDER_GEMINI) to
                                                    runBlocking { getString(Res.string.gemini) },
                                                (mainLyricsProvider == DataStoreManager.AI_PROVIDER_CUSTOM_OPENAI) to
                                                    runBlocking { getString(Res.string.openai_api_compatible) },
                                            ),
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setAIProvider(
                                            when (state.selectOne?.getSelected()) {
                                                runBlocking { getString(Res.string.openai) } -> DataStoreManager.AI_PROVIDER_OPENAI
                                                runBlocking { getString(Res.string.gemini) } -> DataStoreManager.AI_PROVIDER_GEMINI
                                                runBlocking {
                                                    getString(
                                                        Res.string.openai_api_compatible,
                                                    )
                                                },
                                                -> DataStoreManager.AI_PROVIDER_CUSTOM_OPENAI

                                                else -> DataStoreManager.AI_PROVIDER_OPENAI
                                            },
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.ai_api_key),
                    subtitle = if (isHasApiKey) "XXXXXXXXXX" else "N/A",
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.ai_api_key) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.ai_api_key) },
                                        value = "",
                                        verifyCodeBlock = {
                                            (it.isNotEmpty()) to runBlocking { getString(Res.string.invalid_api_key) }
                                        },
                                    ),
                                message = "",
                                confirm =
                                    runBlocking { getString(Res.string.set) } to { state ->
                                        viewModel.setAIApiKey(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.custom_ai_model_id),
                    subtitle = customModelId.ifEmpty { stringResource(Res.string.default_models) },
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.custom_ai_model_id) },
                                textField =
                                    SettingAlertState.TextFieldData(
                                        label = runBlocking { getString(Res.string.custom_ai_model_id) },
                                        value = "",
                                        verifyCodeBlock = {
                                            (it.isNotEmpty() && !it.contains(" ")) to runBlocking { getString(Res.string.invalid) }
                                        },
                                    ),
                                message = runBlocking { getString(Res.string.custom_model_id_messages) },
                                confirm =
                                    runBlocking { getString(Res.string.set) } to { state ->
                                        viewModel.setCustomModelId(state.textField?.value ?: "")
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                // Custom OpenAI Base URL - only show when Custom OpenAI is selected
                if (aiProvider == DataStoreManager.AI_PROVIDER_CUSTOM_OPENAI) {
                    SettingItem(
                        title = "Custom Base URL",
                        subtitle = customOpenAIBaseUrl.ifEmpty { "https://api.openai.com/v1/" },
                        onClick = {
                            viewModel.setAlertData(
                                SettingAlertState(
                                    title = "Custom Base URL",
                                    textField =
                                        SettingAlertState.TextFieldData(
                                            label = "Base URL",
                                            value = customOpenAIBaseUrl,
                                            verifyCodeBlock = {
                                                (it.isEmpty() || it.startsWith("http")) to "Invalid URL format"
                                            },
                                        ),
                                    message = "Enter OpenAI-compatible API base URL (e.g., https://api.openai.com/v1/)",
                                    confirm =
                                        runBlocking { getString(Res.string.set) } to { state ->
                                            viewModel.setCustomOpenAIBaseUrl(state.textField?.value ?: "")
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    SettingItem(
                        title = "Custom Headers",
                        subtitle = if (customOpenAIHeaders.isNotEmpty()) "Configured" else "Not set",
                        onClick = {
                            viewModel.setAlertData(
                                SettingAlertState(
                                    title = "Custom Headers (JSON)",
                                    textField =
                                        SettingAlertState.TextFieldData(
                                            label = "Headers JSON",
                                            value = customOpenAIHeaders,
                                            verifyCodeBlock = { input ->
                                                if (input.isEmpty()) {
                                                    true to null
                                                } else {
                                                    try {
                                                        // Simple validation: check if it looks like JSON
                                                        val trimmed = input.trim()
                                                        (trimmed.startsWith("{") && trimmed.endsWith("}")) to "Invalid JSON format"
                                                    } catch (e: Exception) {
                                                        false to "Invalid JSON format"
                                                    }
                                                }
                                            },
                                        ),
                                    message = "Enter custom headers in JSON format:\n{\"key1\":\"value1\",\"key2\":\"value2\"}",
                                    confirm =
                                        runBlocking { getString(Res.string.set) } to { state ->
                                            viewModel.setCustomOpenAIHeaders(state.textField?.value ?: "")
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                }
                SettingItem(
                    title = stringResource(Res.string.use_ai_translation),
                    subtitle = stringResource(Res.string.use_ai_translation_description),
                    switch = (useAITranslation to { viewModel.setAITranslation(it) }),
                    isEnable = isHasApiKey,
                    onDisable = {
                        if (useAITranslation) {
                            viewModel.setAITranslation(false)
                        }
                    },
                )
            }
        }
        item(key = "sponsor_block") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.sponsorBlock))
                SettingItem(
                    title = stringResource(Res.string.enable_sponsor_block),
                    subtitle = stringResource(Res.string.skip_sponsor_part_of_video),
                    switch = (enableSponsorBlock to { viewModel.setSponsorBlockEnabled(it) }),
                )
                val listName =
                    SponsorBlockType.toList().map { it.displayString() }
                SettingItem(
                    title = stringResource(Res.string.categories_sponsor_block),
                    subtitle = stringResource(Res.string.what_segments_will_be_skipped),
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.categories_sponsor_block) },
                                multipleSelect =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            listName
                                                .mapIndexed { index, item ->
                                                    (
                                                        skipSegments?.contains(
                                                            SponsorBlockType.toList().getOrNull(index)?.value,
                                                        ) == true
                                                    ) to item
                                                }.also {
                                                    Logger.w("SettingScreen", "SettingAlertState: $skipSegments")
                                                    Logger.w("SettingScreen", "SettingAlertState: $it")
                                                },
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.save) } to { state ->
                                        viewModel.setSponsorBlockCategories(
                                            state.multipleSelect
                                                ?.getListSelected()
                                                ?.map { selected ->
                                                    listName.indexOf(selected)
                                                }?.mapNotNull { s ->
                                                    SponsorBlockType.toList().getOrNull(s).let {
                                                        it?.value
                                                    }
                                                }?.toCollection(ArrayList()) ?: arrayListOf(),
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                    isEnable = enableSponsorBlock,
                )
                val beforeUrl = stringResource(Res.string.sponsor_block_intro).substringBefore("https://sponsor.ajay.app/")
                val afterUrl = stringResource(Res.string.sponsor_block_intro).substringAfter("https://sponsor.ajay.app/")
                Text(
                    buildAnnotatedString {
                        append(beforeUrl)
                        withLink(
                            LinkAnnotation.Url(
                                "https://sponsor.ajay.app/",
                                TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary)),
                            ),
                        ) {
                            append("https://sponsor.ajay.app/")
                        }
                        append(afterUrl)
                    },
                    style = typo().bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                )
            }
        }
        if (getPlatform() == Platform.Android) {
            item(key = "storage") {
                Column(SettingsSectionModifier(sectionBackdrop)) {
                    SettingSectionHeader(stringResource(Res.string.storage))
                    SettingItem(
                        title = stringResource(Res.string.player_cache),
                        subtitle = "${playerCache.bytesToMB()} MB",
                        onClick = {
                            viewModel.setBasicAlertData(
                                SettingBasicAlertState(
                                    title = runBlocking { getString(Res.string.clear_player_cache) },
                                    message = null,
                                    confirm =
                                        runBlocking { getString(Res.string.clear) } to {
                                            viewModel.clearPlayerCache()
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    SettingItem(
                        title = stringResource(Res.string.downloaded_cache),
                        subtitle = "${downloadedCache.bytesToMB()} MB",
                        onClick = {
                            viewModel.setBasicAlertData(
                                SettingBasicAlertState(
                                    title = runBlocking { getString(Res.string.clear_downloaded_cache) },
                                    message = null,
                                    confirm =
                                        runBlocking { getString(Res.string.clear) } to {
                                            viewModel.clearDownloadedCache()
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    SettingItem(
                        title = stringResource(Res.string.thumbnail_cache),
                        subtitle = "${thumbnailCache.bytesToMB()} MB",
                        onClick = {
                            viewModel.setBasicAlertData(
                                SettingBasicAlertState(
                                    title = runBlocking { getString(Res.string.clear_thumbnail_cache) },
                                    message = null,
                                    confirm =
                                        runBlocking { getString(Res.string.clear) } to {
                                            viewModel.clearThumbnailCache(platformContext)
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    // Only when Spotify is actually connected. Canvas art is genuinely fetched from
                    // Spotify, so this is a real cache and not dead weight, but listing it to someone
                    // who has never linked an account advertises storage they cannot have used.
                    if (spotifyLoggedIn) {
                    SettingItem(
                        title = stringResource(Res.string.spotify_canvas_cache),
                        subtitle = "${canvasCache.bytesToMB()} MB",
                        onClick = {
                            viewModel.setBasicAlertData(
                                SettingBasicAlertState(
                                    title = runBlocking { getString(Res.string.clear_canvas_cache) },
                                    message = null,
                                    confirm =
                                        runBlocking { getString(Res.string.clear) } to {
                                            viewModel.clearCanvasCache()
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    }
                    SettingItem(
                        title = stringResource(Res.string.limit_player_cache),
                        subtitle = LIMIT_CACHE_SIZE.getItemFromData(limitPlayerCache).toString(),
                        onClick = {
                            viewModel.setAlertData(
                                SettingAlertState(
                                    title = runBlocking { getString(Res.string.limit_player_cache) },
                                    selectOne =
                                        SettingAlertState.SelectData(
                                            listSelect =
                                                LIMIT_CACHE_SIZE.items.map { item ->
                                                    (item == LIMIT_CACHE_SIZE.getItemFromData(limitPlayerCache)) to item.toString()
                                                },
                                        ),
                                    confirm =
                                        runBlocking { getString(Res.string.change) } to { state ->
                                            viewModel.setPlayerCacheLimit(
                                                LIMIT_CACHE_SIZE.getDataFromItem(state.selectOne?.getSelected()),
                                            )
                                        },
                                    dismiss = runBlocking { getString(Res.string.cancel) },
                                ),
                            )
                        },
                    )
                    Box(
                        Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 16.dp,
                        ),
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(SonoraShapes.Container)
                                    .onGloballyPositioned { layoutCoordinates ->
                                        with(localDensity) {
                                            width =
                                                layoutCoordinates.size.width
                                                    .toDp()
                                                    .value
                                                    .toInt()
                                        }
                                    },
                        ) {
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.otherApp * width).dp,
                                            ).background(
                                                md_theme_dark_primary,
                                            ).fillMaxHeight(),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.downloadCache * width).dp,
                                            ).background(
                                                Color(0xD540FF17),
                                            ).fillMaxHeight(),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.playerCache * width).dp,
                                            ).background(
                                                Color(0xD5FFFF00),
                                            ).fillMaxHeight(),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.canvasCache * width).dp,
                                            ).background(
                                                Color.Cyan,
                                            ).fillMaxHeight(),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.thumbCache * width).dp,
                                            ).background(
                                                Color.Magenta,
                                            ).fillMaxHeight(),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.appDatabase * width).dp,
                                            ).background(
                                                Color.White,
                                            ),
                                )
                            }
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(
                                                (fraction.freeSpace * width).dp,
                                            ).background(
                                                Color.DarkGray,
                                            ).fillMaxHeight(),
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    md_theme_dark_primary,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.other_app), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Green,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.downloaded_cache), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Yellow,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.player_cache), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Cyan,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.spotify_canvas_cache), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Magenta,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.thumbnail_cache), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.White,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.database), style = typo().bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.LightGray,
                                ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.free_space), style = typo().bodySmall)
                    }
                }
            }
        }
        item(key = "backup") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.settings_section_backup))
                SettingItem(
                    title = stringResource(Res.string.backup_downloaded),
                    subtitle = stringResource(Res.string.backup_downloaded_description),
                    switch = (backupDownloaded to { viewModel.setBackupDownloaded(it) }),
                )
                // Auto Backup (Android only)
                if (getPlatform() == Platform.Android) {
                    SettingItem(
                        title = stringResource(Res.string.auto_backup),
                        subtitle = stringResource(Res.string.auto_backup_description),
                        switch = (autoBackupEnabled to { viewModel.setAutoBackupEnabled(it) }),
                    )
                    AnimatedVisibility(visible = autoBackupEnabled) {
                        Column {
                            SettingItem(
                                title = stringResource(Res.string.backup_frequency),
                                subtitle =
                                    when (autoBackupFrequency) {
                                        DataStoreManager.AUTO_BACKUP_FREQUENCY_DAILY -> stringResource(Res.string.daily)
                                        DataStoreManager.AUTO_BACKUP_FREQUENCY_WEEKLY -> stringResource(Res.string.weekly)
                                        DataStoreManager.AUTO_BACKUP_FREQUENCY_MONTHLY -> stringResource(Res.string.monthly)
                                        else -> stringResource(Res.string.daily)
                                    },
                                onClick = {
                                    viewModel.setAlertData(
                                        SettingAlertState(
                                            title = runBlocking { getString(Res.string.backup_frequency) },
                                            selectOne =
                                                SettingAlertState.SelectData(
                                                    listSelect =
                                                        listOf(
                                                            (autoBackupFrequency == DataStoreManager.AUTO_BACKUP_FREQUENCY_DAILY) to
                                                                runBlocking { getString(Res.string.daily) },
                                                            (autoBackupFrequency == DataStoreManager.AUTO_BACKUP_FREQUENCY_WEEKLY) to
                                                                runBlocking { getString(Res.string.weekly) },
                                                            (autoBackupFrequency == DataStoreManager.AUTO_BACKUP_FREQUENCY_MONTHLY) to
                                                                runBlocking { getString(Res.string.monthly) },
                                                        ),
                                                ),
                                            confirm =
                                                runBlocking { getString(Res.string.change) } to { state ->
                                                    val frequency =
                                                        when (state.selectOne?.getSelected()) {
                                                            runBlocking {
                                                                getString(
                                                                    Res.string.daily,
                                                                )
                                                            },
                                                            -> DataStoreManager.AUTO_BACKUP_FREQUENCY_DAILY
                                                            runBlocking {
                                                                getString(
                                                                    Res.string.weekly,
                                                                )
                                                            },
                                                            -> DataStoreManager.AUTO_BACKUP_FREQUENCY_WEEKLY
                                                            runBlocking {
                                                                getString(
                                                                    Res.string.monthly,
                                                                )
                                                            },
                                                            -> DataStoreManager.AUTO_BACKUP_FREQUENCY_MONTHLY
                                                            else -> DataStoreManager.AUTO_BACKUP_FREQUENCY_DAILY
                                                        }
                                                    viewModel.setAutoBackupFrequency(frequency)
                                                },
                                            dismiss = runBlocking { getString(Res.string.cancel) },
                                        ),
                                    )
                                },
                            )
                            SettingItem(
                                title = stringResource(Res.string.keep_backups),
                                subtitle = stringResource(Res.string.keep_backups_format, "$autoBackupMaxFiles"),
                                onClick = {
                                    viewModel.setAlertData(
                                        SettingAlertState(
                                            title = runBlocking { getString(Res.string.keep_backups) },
                                            selectOne =
                                                SettingAlertState.SelectData(
                                                    listSelect =
                                                        listOf(
                                                            (autoBackupMaxFiles == 3) to "3",
                                                            (autoBackupMaxFiles == 5) to "5",
                                                            (autoBackupMaxFiles == 10) to "10",
                                                            (autoBackupMaxFiles == 15) to "15",
                                                        ),
                                                ),
                                            confirm =
                                                runBlocking { getString(Res.string.change) } to { state ->
                                                    val maxFiles = state.selectOne?.getSelected()?.toIntOrNull() ?: 5
                                                    viewModel.setAutoBackupMaxFiles(maxFiles)
                                                },
                                            dismiss = runBlocking { getString(Res.string.cancel) },
                                        ),
                                    )
                                },
                            )
                            SettingItem(
                                title = stringResource(Res.string.last_backup),
                                subtitle =
                                    if (autoBackupLastTime == 0L) {
                                        stringResource(Res.string.never)
                                    } else {
                                        DateTimeFormatter
                                            .ofPattern("yyyy-MM-dd HH:mm:ss")
                                            .withZone(ZoneId.systemDefault())
                                            .format(Instant.ofEpochMilli(autoBackupLastTime))
                                    },
                            )
                        }
                    }
                }
                SettingItem(
                    title = stringResource(Res.string.backup),
                    subtitle = stringResource(Res.string.save_all_your_playlist_data),
                    onClick = {
                        coroutineScope.launch {
                            backupLauncher.launch()
                        }
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.restore_your_data),
                    subtitle = stringResource(Res.string.restore_your_saved_data),
                    onClick = {
                        coroutineScope.launch {
                            restoreLauncher.launch()
                        }
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.import_data),
                    subtitle = stringResource(Res.string.import_playlists_from_other_apps),
                    onClick = {
                        coroutineScope.launch {
                            importLauncher.launch()
                        }
                    },
                )
            }
        }
        item(key = "about_us") {
            Column(SettingsSectionModifier(sectionBackdrop)) {
                SettingSectionHeader(stringResource(Res.string.about_us))
                SettingItem(
                    title = stringResource(Res.string.version),
                    subtitle = stringResource(Res.string.version_format, VersionManager.getVersionName()),
                    onClick = {
                        navController.navigate(CreditDestination)
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.auto_check_for_update),
                    subtitle = stringResource(Res.string.auto_check_for_update_description),
                    switch = (autoCheckUpdate to { viewModel.setAutoCheckUpdate(it) }),
                )
                SettingItem(
                    title = stringResource(Res.string.update_channel),
                    subtitle =
                        if (updateChannel == DataStoreManager.FDROID) {
                            "F-Droid"
                        } else {
                            "Sonora GitHub Release"
                        },
                    onClick = {
                        viewModel.setAlertData(
                            SettingAlertState(
                                title = runBlocking { getString(Res.string.update_channel) },
                                selectOne =
                                    SettingAlertState.SelectData(
                                        listSelect =
                                            listOf(
                                                (updateChannel == DataStoreManager.FDROID) to "F-Droid",
                                                (updateChannel == DataStoreManager.GITHUB) to "Sonora GitHub Release",
                                            ),
                                    ),
                                confirm =
                                    runBlocking { getString(Res.string.change) } to { state ->
                                        viewModel.setUpdateChannel(
                                            when (state.selectOne?.getSelected()) {
                                                "F-Droid" -> DataStoreManager.FDROID
                                                "Sonora GitHub Release" -> DataStoreManager.GITHUB
                                                else -> DataStoreManager.GITHUB
                                            },
                                        )
                                    },
                                dismiss = runBlocking { getString(Res.string.cancel) },
                            ),
                        )
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.check_for_update),
                    subtitle = checkForUpdateSubtitle,
                    onClick = {
                        sharedViewModel.checkForUpdate()
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.author),
                    subtitle = stringResource(Res.string.author_name),
                    onClick = {
                        uriHandler.openUri("https://github.com/vanshaj-pahwa/sonora")
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.developer_blog),
                    subtitle = stringResource(Res.string.developer_blog_tagline),
                    onClick = {
                        uriHandler.openUri("https://github.com/vanshaj-pahwa/sonora")
                    },
                )
                if (getPlatform() == Platform.Android) {
                    SettingItem(
                        title = stringResource(Res.string.blog_notification_title),
                        subtitle = stringResource(Res.string.blog_notification_description),
                        switch = (blogNotificationEnabled to { viewModel.setBlogNotificationEnabled(it) }),
                    )
                }
                SettingItem(
                    title = stringResource(Res.string.buy_me_a_coffee),
                    subtitle = stringResource(Res.string.donation),
                    onClick = {
                        uriHandler.openUri("https://github.com/sponsors/vanshaj-pahwa")
                    },
                )
                SettingItem(
                    title = stringResource(Res.string.third_party_libraries),
                    subtitle = stringResource(Res.string.description_and_licenses),
                    onClick = {
                        showThirdPartyLibraries = true
                    },
                )
            }
        }
        item(key = "end") {
            EndOfPage()
        }
    }
    }
    }
    importState?.let { progress ->
        ImportProgressDialog(
            progress = progress,
            onDismiss = importViewModel::dismiss,
        )
    }
    val basisAlertData by viewModel.basicAlertData.collectAsStateWithLifecycle()
    if (basisAlertData != null) {
        val alertBasicState = basisAlertData ?: return
        AlertDialog(
            onDismissRequest = { viewModel.setBasicAlertData(null) },
            title = {
                Text(
                    text = alertBasicState.title,
                    style = typo().titleSmall,
                )
            },
            text = {
                if (alertBasicState.message != null) {
                    Text(text = alertBasicState.message)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertBasicState.confirm.second.invoke()
                        viewModel.setBasicAlertData(null)
                    },
                ) {
                    Text(text = alertBasicState.confirm.first)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.setBasicAlertData(null)
                    },
                ) {
                    Text(text = alertBasicState.dismiss)
                }
            },
        )
    }
    if (showColorPickerDialog) {
        val presetColors =
            listOf(
                "FF8ECAE6",
                "FF00A4FF",
                "FF9B72CF",
                "FFEF6C9B",
                "FFEF5350",
                "FFF4A340",
                "FFFFCA28",
                "FF66BB6A",
                "FF26A69A",
                "FFBDBDBD",
            )
        var pendingHex by rememberSaveable { mutableStateOf(customThemeColorHex.takeLast(6)) }
        val parsedColor = parseThemeColorHex(pendingHex)
        AlertDialog(
            onDismissRequest = { showColorPickerDialog = false },
            title = { Text(text = stringResource(Res.string.custom_color), style = typo().titleSmall) },
            text = {
                Column {
                    presetColors.chunked(5).forEach { rowColors ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            rowColors.forEach { hex ->
                                val color = parseThemeColorHex(hex) ?: Color.Gray
                                val isSelected = pendingHex.equals(hex.takeLast(6), ignoreCase = true)
                                Box(
                                    modifier =
                                        Modifier
                                            .padding(4.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (isSelected) 3.dp else 0.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                shape = CircleShape,
                                            ).clickable { pendingHex = hex.takeLast(6) },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    TextField(
                        value = pendingHex,
                        onValueChange = { pendingHex = it.removePrefix("#").take(8).uppercase() },
                        label = { Text("HEX") },
                        prefix = { Text("#") },
                        singleLine = true,
                        isError = parsedColor == null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = parsedColor != null,
                    onClick = {
                        parsedColor?.let {
                            val argb = "FF${pendingHex.takeLast(6).uppercase()}"
                            sharedViewModel.setCustomThemeColor(argb)
                            sharedViewModel.setThemeColorSource(DataStoreManager.THEME_COLOR_CUSTOM)
                        }
                        showColorPickerDialog = false
                    },
                ) { Text(text = stringResource(Res.string.change)) }
            },
            dismissButton = {
                TextButton(onClick = { showColorPickerDialog = false }) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
    if (showYouTubeAccountDialog) {
        BasicAlertDialog(
            onDismissRequest = { },
            modifier = Modifier.wrapContentSize(),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                shadowElevation = 1.dp,
            ) {
                val googleAccounts by viewModel.googleAccounts.collectAsStateWithLifecycle(
                    minActiveState = Lifecycle.State.RESUMED,
                )
                LaunchedEffect(googleAccounts) {
                    Logger.w(
                        "SettingScreen",
                        "LaunchedEffect: ${
                            googleAccounts.data?.map {
                                it.name to it.isUsed
                            }
                        }",
                    )
                }
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                        ) {
                            IconButton(
                                onClick = { showYouTubeAccountDialog = false },
                                colors =
                                    IconButtonDefaults.iconButtonColors().copy(
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                    ),
                                modifier =
                                    Modifier
                                        .align(Alignment.CenterStart)
                                        .fillMaxHeight(),
                            ) {
                                Icon(SonoraIcons.Close, null, tint = MaterialTheme.colorScheme.onSurface)
                            }
                            Text(
                                stringResource(Res.string.youtube_account),
                                style = typo().titleMedium,
                                modifier =
                                    Modifier
                                        .align(Alignment.Center)
                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                        .wrapContentWidth(),
                            )
                        }
                    }
                    if (googleAccounts is LocalResource.Success) {
                        val data = googleAccounts.data
                        if (data.isNullOrEmpty()) {
                            item {
                                Text(
                                    stringResource(Res.string.no_account),
                                    style = typo().bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                )
                            }
                        } else {
                            items(data) {
                                Row(
                                    modifier =
                                        Modifier
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                viewModel.setUsedAccount(it)
                                            },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Spacer(Modifier.width(24.dp))
                                    AsyncImage(
                                        model =
                                            ImageRequest
                                                .Builder(LocalPlatformContext.current)
                                                .data(it.thumbnailUrl)
                                                .crossfade(550)
                                                .build(),
                                        placeholder = rememberVectorPainter(SonoraIcons.PeopleAlt),
                                        error = rememberVectorPainter(SonoraIcons.PeopleAlt),
                                        contentDescription = it.name,
                                        modifier =
                                            Modifier
                                                .size(48.dp)
                                                .clip(CircleShape),
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(it.name, style = typo().labelMedium, color = MaterialTheme.colorScheme.onBackground)
                                        Text(it.email, style = typo().bodySmall)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    AnimatedVisibility(it.isUsed) {
                                        Text(
                                            stringResource(Res.string.signed_in),
                                            style = typo().bodySmall,
                                            maxLines = 2,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.widthIn(0.dp, 64.dp),
                                        )
                                    }
                                    Spacer(Modifier.width(24.dp))
                                }
                            }
                        }
                    } else {
                        item {
                            CenterLoadingBox(
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                            )
                        }
                    }
                    item {
                        Column {
                            ActionButton(
                                icon = SonoraIcons.PeopleAlt,
                                text = Res.string.guest,
                            ) {
                                viewModel.setUsedAccount(null)
                                showYouTubeAccountDialog = false
                            }
                            ActionButton(
                                icon = SonoraIcons.Close,
                                text = Res.string.log_out,
                            ) {
                                viewModel.setBasicAlertData(
                                    SettingBasicAlertState(
                                        title = runBlocking { getString(Res.string.warning) },
                                        message = runBlocking { getString(Res.string.log_out_warning) },
                                        confirm =
                                            runBlocking { getString(Res.string.log_out) } to {
                                                viewModel.logOutAllYouTube()
                                                showYouTubeAccountDialog = false
                                            },
                                        dismiss = runBlocking { getString(Res.string.cancel) },
                                    ),
                                )
                            }
                            ActionButton(
                                icon = SonoraIcons.PlaylistAdd,
                                text = Res.string.add_an_account,
                            ) {
                                showYouTubeAccountDialog = false
                                navController.navigate(LoginDestination)
                            }
                        }
                    }
                }
            }
        }
    }
    val alertData by viewModel.alertData.collectAsStateWithLifecycle()
    if (alertData != null) {
        val alertState = alertData ?: return
        // AlertDialog
        AlertDialog(
            onDismissRequest = { viewModel.setAlertData(null) },
            title = {
                Text(
                    text = alertState.title,
                    style = typo().titleSmall,
                )
            },
            text = {
                if (alertState.message != null) {
                    Column {
                        Text(text = alertState.message)
                        if (alertState.textField != null) {
                            val verify =
                                alertState.textField.verifyCodeBlock?.invoke(
                                    alertState.textField.value,
                                ) ?: (true to null)
                            TextField(
                                value = alertState.textField.value,
                                onValueChange = {
                                    viewModel.setAlertData(
                                        alertState.copy(
                                            textField =
                                                alertState.textField.copy(
                                                    value = it,
                                                ),
                                        ),
                                    )
                                },
                                isError = !verify.first,
                                label = { Text(text = alertState.textField.label) },
                                supportingText = {
                                    if (!verify.first) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = verify.second ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (!verify.first) {
                                        SonoraIcons.Error
                                    }
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 6.dp,
                                        ),
                            )
                        }
                    }
                } else if (alertState.selectOne != null) {
                    LazyColumn(
                        Modifier
                            .padding(vertical = 6.dp)
                            .heightIn(0.dp, 500.dp),
                    ) {
                        items(alertState.selectOne.listSelect) { item ->
                            val onSelect = {
                                viewModel.setAlertData(
                                    alertState.copy(
                                        selectOne =
                                            alertState.selectOne.copy(
                                                listSelect =
                                                    alertState.selectOne.listSelect.toMutableList().map {
                                                        if (it == item) {
                                                            true to it.second
                                                        } else {
                                                            false to it.second
                                                        }
                                                    },
                                            ),
                                    ),
                                )
                            }
                            Row(
                                Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSelect.invoke()
                                    }.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = item.first,
                                    onClick = {
                                        onSelect.invoke()
                                    },
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.second,
                                    style = typo().bodyMedium,
                                    maxLines = 1,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(align = Alignment.CenterVertically)
                                            .titleMarquee(
                                                iterations = Int.MAX_VALUE,
                                                animationMode = MarqueeAnimationMode.Immediately,
                                            ).focusable(),
                                )
                            }
                        }
                    }
                } else if (alertState.multipleSelect != null) {
                    LazyColumn(
                        Modifier.padding(vertical = 6.dp),
                    ) {
                        items(alertState.multipleSelect.listSelect) { item ->
                            val onCheck = {
                                viewModel.setAlertData(
                                    alertState.copy(
                                        multipleSelect =
                                            alertState.multipleSelect.copy(
                                                listSelect =
                                                    alertState.multipleSelect.listSelect.toMutableList().map {
                                                        if (it == item) {
                                                            !it.first to it.second
                                                        } else {
                                                            it
                                                        }
                                                    },
                                            ),
                                    ),
                                )
                            }
                            Row(
                                Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onCheck.invoke()
                                    }.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = item.first,
                                    onCheckedChange = {
                                        onCheck.invoke()
                                    },
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(text = item.second, style = typo().bodyMedium, maxLines = 1)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertState.confirm.second.invoke(alertState)
                        viewModel.setAlertData(null)
                    },
                    enabled =
                        if (alertState.textField?.verifyCodeBlock != null) {
                            alertState.textField.verifyCodeBlock
                                .invoke(
                                    alertState.textField.value,
                                ).first
                        } else {
                            true
                        },
                ) {
                    Text(text = alertState.confirm.first)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.setAlertData(null)
                    },
                ) {
                    Text(text = alertState.dismiss)
                }
            },
        )
    }

    if (showThirdPartyLibraries) {
        val libraries by produceLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }
        val lazyListState = rememberLazyListState()
        val canScrollBackward by remember {
            derivedStateOf {
                lazyListState.canScrollBackward
            }
        }
        val sheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = {
                    !canScrollBackward
                },
            )
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet(
            modifier =
                Modifier
                    .fillMaxHeight(),
            onDismissRequest = {
                showThirdPartyLibraries = false
            },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {},
            scrimColor = Color.Black.copy(alpha = .5f),
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            shape = RectangleShape,
        ) {
            // Capture theme colors here: the ChipColors getters below run outside composition.
            val surfaceContainerHighestColor = MaterialTheme.colorScheme.surfaceContainerHighest
            val onSurfaceColor = MaterialTheme.colorScheme.onSurface
            LibrariesContainer(
                libraries?.copy(
                    libraries =
                        libraries
                            ?.libraries
                            ?.distinctBy {
                                it.name
                            }?.toImmutableList() ?: emptyList<Library>().toImmutableList(),
                ),
                Modifier.fillMaxSize(),
                lazyListState = lazyListState,
                contentPadding = innerPadding,
                colors =
                    LibraryDefaults.libraryColors(
                        licenseChipColors =
                            object : ChipColors {
                                override val containerColor: Color
                                    get() = surfaceContainerHighestColor
                                override val contentColor: Color
                                    get() = onSurfaceColor
                            },
                    ),
                header = {
                    item {
                        TopAppBar(
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            title = {
                                Text(
                                    text =
                                        stringResource(
                                            Res.string.third_party_libraries,
                                        ),
                                    style = typo().titleMedium,
                                )
                            },
                            navigationIcon = {
                                Box(Modifier.padding(horizontal = 5.dp)) {
                                    RippleIconButton(
                                        SonoraIcons.ArrowBackIosNew,
                                        Modifier
                                            .size(32.dp),
                                        true,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    ) {
                                        coroutineScope.launch {
                                            sheetState.hide()
                                            showThirdPartyLibraries = false
                                        }
                                    }
                                }
                            },
                        )
                    }
                },
            )
        }
    }

    TopAppBar(
        title = {
            Text(
                text =
                    stringResource(
                        Res.string.settings,
                    ),
                style = typo().titleMedium,
            )
        },
        navigationIcon = {
            Box(Modifier.padding(horizontal = 5.dp)) {
                RippleIconButton(
                    SonoraIcons.ArrowBackIosNew,
                    Modifier
                        .size(32.dp),
                    true,
                    tint = MaterialTheme.colorScheme.onSurface,
                ) {
                    navController.navigateUp()
                }
            }
        },
        modifier =
            Modifier
                .hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                    blurEnabled = true
                },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
    )
}

/**
 * Progress and outcome of a playlist import.
 *
 * Only dismissible once the import has finished — cancelling mid-write would leave the database
 * half-populated with no way to tell the user which half.
 */
@Composable
private fun ImportProgressDialog(
    progress: ImportProgress,
    onDismiss: () -> Unit,
) {
    val finished = progress is ImportProgress.Success || progress is ImportProgress.Error
    AlertDialog(
        onDismissRequest = { if (finished) onDismiss() },
        properties =
            DialogProperties(
                dismissOnBackPress = finished,
                dismissOnClickOutside = finished,
            ),
        title = {
            Text(
                text =
                    stringResource(
                        if (progress is ImportProgress.Error) Res.string.import_failed else Res.string.import_data,
                    ),
                style = typo().titleSmall,
            )
        },
        text = {
            Column {
                when (progress) {
                    is ImportProgress.Preparing -> {
                        Text(
                            text = stringResource(Res.string.import_reading_file),
                            style = typo().bodyMedium,
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    is ImportProgress.Importing -> {
                        Text(
                            text = stringResource(Res.string.import_progress_songs, progress.processed, progress.total),
                            style = typo().bodyMedium,
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = {
                                if (progress.total > 0) progress.processed.toFloat() / progress.total else 0f
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    is ImportProgress.Success -> {
                        Text(
                            text =
                                stringResource(
                                    Res.string.import_result,
                                    progress.result.playlistsCreated,
                                    progress.result.songsImported,
                                ),
                            style = typo().bodyMedium,
                        )
                        if (progress.result.skippedEntries > 0) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.import_result_skipped, progress.result.skippedEntries),
                                style = typo().bodySmall,
                            )
                        }
                    }

                    is ImportProgress.Error -> {
                        Text(
                            text = progress.message,
                            style = typo().bodyMedium,
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (finished) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(Res.string.ok))
                }
            }
        },
    )
}

/**
 * Section heading in Settings ("Interface", "Content", "Audio",...).
 *
 * Uppercase and medium weight, where these used to be bold mixed-case. A section heading labels the
 * group beneath it; set bold at 16sp it carried more weight than the setting titles it was
 * introducing, so the page read as a list of headings rather than a list of settings. The letter
 * spacing is what keeps uppercase legible once the weight comes down.
 */
/**
 * A section break has to out-measure a row break, or it does not read as a break at all.
 *
 * SettingItem pads 8.dp top and bottom, so consecutive rows sit 16.dp apart. The header padded 8.dp
 * vertically too, which put exactly 16.dp above it as well: the gap starting a new section was the
 * same size as the gap between two settings inside one, so a header attached itself to whatever was
 * above it.
 *
 * 28.dp top makes that gap 36.dp against the 16.dp between rows, better than twice the rhythm, which
 * is enough for the break to be read rather than measured. The bottom stays at 8.dp; the header
 * already sat close to its own first row and that was never the problem.
 */
// The card now owns the separation between sections, so the header only needs enough room to sit
// off the card's top edge. This was 28dp from when sections were flat on the page with nothing but
// whitespace dividing them, and left a visible gap above every heading once the cards arrived.
private val SECTION_HEADER_TOP_PADDING = 4.dp
private val SECTION_HEADER_BOTTOM_PADDING = 8.dp

/**
 * Search box at the top of settings.
 *
 * Sits in the scrolling list rather than pinned to the app bar. Settings is a screen people scroll
 * through to browse as often as they search it, and a permanently pinned field spends most of its
 * life taking a row away from the content while suggesting the list is too long to read.
 */
@Composable
private fun SettingsSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        placeholder = { Text(stringResource(Res.string.search_settings)) },
        leadingIcon = {
            Icon(SonoraIcons.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(SonoraIcons.Close, contentDescription = stringResource(Res.string.clear))
                }
            }
        },
        singleLine = true,
        shape = SonoraShapes.Capsule,
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
    )
}

@Composable
private fun SettingSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    // While a search is running the results are a flat list. Keeping the headers would leave a
    // column of category names with one row under each, or worse, headers with nothing under them
    // at all, since a header cannot know whether its own section matched.
    if (LocalSettingsSearchQuery.current.isNotBlank()) return
    Text(
        text = text.uppercase(),
        style =
            typo().labelMedium.copy(
                fontWeight = FontWeight.Medium,
                
                letterSpacing = 1.sp,
            ),
        color = MaterialTheme.colorScheme.onBackground,
        // Padding is applied here rather than as the default value of [modifier]. As a default it
        // vanished the moment any caller passed a modifier of its own, which is a silent way to
        // lose the spacing on exactly one section and not notice.
        modifier =
            modifier.padding(
                top = SECTION_HEADER_TOP_PADDING,
                bottom = SECTION_HEADER_BOTTOM_PADDING,
            ),
    )
}

/**
 * The card each settings section sits in.
 *
 * Settings was the most default looking screen in the app: flat rows on flat black with nothing
 * grouping them. Giving each section a container surface is what makes the screen belong to the
 * same family as Home's cards and the analytics figures, and it costs no vertical space because the
 * rows were already padded.
 */
@Composable
private fun SettingsSectionModifier(backdrop: PlatformBackdrop?): Modifier {
    // No card while searching.
    //
    // A section cannot know whether any of its rows survived the query: SettingItem filters itself,
    // so a section whose rows all filtered out still drew its background and left an empty card
    // floating in the results. Section headers already hide during a search for the same reason,
    // because results read as one flat list rather than sparse groups, so the card goes with them.
    if (LocalSettingsSearchQuery.current.isNotBlank()) return Modifier.fillMaxWidth()

    return Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
        .then(
            // Glass when the toggle is on, a solid container otherwise. Same card, same radius,
            // same padding: only the material changes, which is the rule the nav bar now follows
            // too. The backdrop arrives null when glass is off so there is nothing to sample.
            if (backdrop != null) {
                Modifier.liquidGlass(backdrop, SonoraShapes.Container, interactive = false)
            } else {
                Modifier
                    .clip(SonoraShapes.Container)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            },
        ).padding(horizontal = 16.dp, vertical = 8.dp)
}
