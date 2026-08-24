package app.vanshajpahwa.sonora.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.MoodAndGenresContentItem
import app.vanshajpahwa.sonora.ui.component.NormalAppBar
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.MoodViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.*

@Composable
fun MoodScreen(
    navController: NavController,
    viewModel: MoodViewModel = koinViewModel(),
    params: String?,
) {
    val moodData by viewModel.moodsMomentObject.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = params) {
        if (params != null) {
            viewModel.getMood(params)
        }
    }

    Column {
        NormalAppBar(
            title = {
                Text(
                    text = moodData?.header ?: "",
                    style = typo().labelMedium,
                )
            },
            leftIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        SonoraIcons.ArrowBackIosNew,
                        contentDescription = "Back",
                    )
                }
            },
        )
        AnimatedVisibility(visible = !loading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(moodData?.items ?: emptyList()) { item ->
                    MoodAndGenresContentItem(
                        data = item,
                        navController = navController,
                    )
                }
                item {
                    EndOfPage()
                }
            }
        }
        AnimatedVisibility(visible = loading) {
            CenterLoadingBox(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}