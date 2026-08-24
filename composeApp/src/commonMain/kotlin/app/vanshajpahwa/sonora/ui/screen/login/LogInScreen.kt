package app.vanshajpahwa.sonora.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.expect.ui.PlatformWebView
import app.vanshajpahwa.sonora.expect.ui.createWebViewCookieManager
import app.vanshajpahwa.sonora.expect.ui.rememberWebViewState
import app.vanshajpahwa.sonora.ui.component.DevLogInBottomSheet
import app.vanshajpahwa.sonora.ui.component.DevLogInType
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.LogoDev
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.LogInViewModel
import app.vanshajpahwa.sonora.viewModel.SettingsViewModel
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.log_in
import app.vanshajpahwa.sonora.resources.login_failed
import app.vanshajpahwa.sonora.resources.login_success

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: LogInViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    hideBottomNavigation: () -> Unit,
    showBottomNavigation: () -> Unit,
) {
    val hazeState = rememberHazeState()
    val coroutineScope = rememberCoroutineScope()
    var devLoginSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val state = rememberWebViewState()

    LaunchedEffect(state) {
        snapshotFlow { state.value }.collect {
            Logger.d(
                "LogInScreen",
                "WebViewState: ${
                    when (it) {
                        is app.vanshajpahwa.sonora.expect.ui.WebViewState.Finished -> "Finished"
                        is app.vanshajpahwa.sonora.expect.ui.WebViewState.Loading -> "Loading ${it.progress}%"
                    }
                }",
            )
        }
    }

    // Hide bottom navigation when entering this screen
    LaunchedEffect(Unit) {
        hideBottomNavigation()
        createWebViewCookieManager().removeAllCookies()
    }

    // Show bottom navigation when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            showBottomNavigation()
        }
    }

    Box(modifier = Modifier.fillMaxSize().hazeSource(state = hazeState)) {
        Column {
            Spacer(
                Modifier
                    .size(
                        innerPadding.calculateTopPadding() + 64.dp,
                    ),
            )
            // WebView for YouTube Music login
            PlatformWebView(
                state,
                Config.LOG_IN_URL,
                aboveContent = {
                    if (devLoginSheet) {
                        DevLogInBottomSheet(
                            onDismiss = {
                                devLoginSheet = false
                            },
                            onDone = { cookie ->
                                coroutineScope.launch {
                                    val success = settingsViewModel.addAccount(cookie)
                                    if (success) {
                                        viewModel.makeToast(getString(Res.string.login_success))
                                        navController.navigateUp()
                                    } else {
                                        viewModel.makeToast(getString(Res.string.login_failed))
                                    }
                                }
                            },
                            type = DevLogInType.YouTube,
                        )
                    }
                }
            ) { url ->
                Logger.d("LogInScreen", "Current URL: $url")
                if (url == Config.YOUTUBE_MUSIC_MAIN_URL) {
                    coroutineScope.launch {
                        val success =
                            createWebViewCookieManager()
                                .getCookie(url)
                                .takeIf {
                                    it.isNotEmpty()
                                }?.let {
                                    settingsViewModel.addAccount(it)
                                } ?: false

                        createWebViewCookieManager().removeAllCookies()

                        if (success) {
                            viewModel.makeToast(getString(Res.string.login_success))
                            navController.navigateUp()
                        } else {
                            viewModel.makeToast(getString(Res.string.login_failed))
                        }
                    }
                }
            }
        }

        // Top App Bar with haze effect
        TopAppBar(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                        blurEnabled = true
                    },
            title = {
                Text(
                    text = stringResource(Res.string.log_in),
                    style = typo().titleMedium,
                )
            },
            navigationIcon = {
                Box(Modifier.padding(horizontal = 5.dp)) {
                    RippleIconButton(
                        SonoraIcons.ArrowBackIosNew,
                        Modifier.size(32.dp),
                        true,
                    ) {
                        navController.navigateUp()
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        devLoginSheet = true
                    },
                ) {
                    Icon(
                        SonoraIcons.LogoDev,
                        "Developer Mode",
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
        )
    }
}