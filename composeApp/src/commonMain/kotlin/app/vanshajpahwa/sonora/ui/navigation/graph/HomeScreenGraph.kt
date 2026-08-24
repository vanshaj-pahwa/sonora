package app.vanshajpahwa.sonora.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.vanshajpahwa.sonora.ui.navigation.destination.home.AnalyticsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.CreditDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.LocalFilesDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.MoodDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.NotificationDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.RecentlySongsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.SettingsDestination
import app.vanshajpahwa.sonora.ui.screen.home.LocalFilesScreen
import app.vanshajpahwa.sonora.ui.screen.home.MoodScreen
import app.vanshajpahwa.sonora.ui.screen.home.NotificationScreen
import app.vanshajpahwa.sonora.ui.screen.home.RecentlySongsScreen
import app.vanshajpahwa.sonora.ui.screen.home.SettingScreen
import app.vanshajpahwa.sonora.ui.screen.home.analytics.AnalyticsScreen
import app.vanshajpahwa.sonora.ui.screen.other.CreditScreen

fun NavGraphBuilder.homeScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<CreditDestination> {
        CreditScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<MoodDestination> { entry ->
        val params = entry.toRoute<MoodDestination>().params
        MoodScreen(
            navController = navController,
            params = params,
        )
    }
    composable<NotificationDestination> {
        NotificationScreen(
            navController = navController,
        )
    }
    composable<LocalFilesDestination> {
        LocalFilesScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<RecentlySongsDestination> {
        RecentlySongsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<SettingsDestination> {
        SettingScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<AnalyticsDestination> {
        AnalyticsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}