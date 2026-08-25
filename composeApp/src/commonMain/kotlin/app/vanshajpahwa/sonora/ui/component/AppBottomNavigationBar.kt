package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.animateColorAsState
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import app.vanshajpahwa.sonora.ui.navigation.destination.home.HomeDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.search.SearchDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import app.vanshajpahwa.sonora.extension.greyScale
import org.jetbrains.compose.resources.painterResource
import app.vanshajpahwa.sonora.resources.*

/**
 * Opacity of the capsule when the translucent setting is on, which it is by default.
 *
 * Kept low on purpose: seeing content through the bar is the effect, so this trades some label
 * legibility for it and text scrolling behind can still compete with the tab labels. Raising this
 * is the wrong fix for that, since sharpness rather than brightness is what competes; a blur or a
 * gradient behind the labels is, and the liquid glass variant already blurs.
 */
private const val TRANSLUCENT_BAR_ALPHA = 0.85f

/**
 * The floating capsule nav bar.
 *
 * Detached from the screen edge rather than spanning it. That is the whole point of the shape: an
 * edge-to-edge bar is the Material default and reads as "an Android app", while a capsule reads as
 * a deliberate one. The glass variant on the other side of the `isLiquidGlass` toggle is already
 * shaped this way, so this also stops the two variants looking like different apps.
 *
 * The active tab takes a filled accent pill behind icon and label together, not a tint on the icon
 * alone. A colour-only cue has to compete with album art bleeding through a translucent bar; a
 * filled shape does not, and it survives the artwork behind it changing on every track.
 *
 * All three tabs keep their labels. Icon-only inactive tabs look cleaner in a screenshot and cost
 * real discoverability on the control people press most, so the trade is not worth taking here.
 */
@Composable
fun AppBottomNavigationBar(
    startDestination: Any = HomeDestination,
    navController: NavController,
    isTranslucentBackground: Boolean = false,
    isScrolledToTop: Boolean = true,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavScreens =
        listOf(
            BottomNavScreen.Home,
            BottomNavScreen.Search,
            BottomNavScreen.Library,
        )
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(
            when (startDestination) {
                is HomeDestination -> BottomNavScreen.Home.ordinal
                is SearchDestination -> BottomNavScreen.Search.ordinal
                is LibraryDestination -> BottomNavScreen.Library.ordinal
                else -> BottomNavScreen.Home.ordinal
            },
        )
    }

    // Collapse behaviour, ported from the glass variant so it is a Sonora behaviour rather than a
    // glass only quirk. Two triggers, matching that variant exactly:
    //   - on Search the bar is always collapsed, because that screen is a list you scan and the bar
    //     is buying back vertical space it does not need
    //   - anywhere else it follows the scroll, expanded at the top and collapsed once you move
    // The collapsed button offers somewhere to GO rather than the screen already on display, which
    // is why it shows Search everywhere except on Search itself, where it shows Home as the way back.
    var isInSearchDestination by remember { mutableStateOf(false) }
    LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.destination?.let { current ->
            isInSearchDestination = current.hasRoute(SearchDestination::class)
        }
    }
    var isExpanded by remember { mutableStateOf(true) }
    LaunchedEffect(isInSearchDestination) { isExpanded = !isInSearchDestination }
    LaunchedEffect(isScrolledToTop) {
        if (!isInSearchDestination) isExpanded = isScrolledToTop
    }

    if (!isExpanded) {
        val collapsedScreen =
            if (isInSearchDestination) BottomNavScreen.Home else BottomNavScreen.Search
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        // 56dp rather than the 48dp floor: this is the only navigation control on
                        // screen while collapsed, and a dark circle on a dark page is easy to lose.
                        .size(56.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (isTranslucentBackground) {
                                MaterialTheme.colorScheme.surface.copy(alpha = TRANSLUCENT_BAR_ALPHA)
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                        ).selectable(
                            selected = false,
                            onClick = {
                                selectedIndex = collapsedScreen.ordinal
                                navController.navigate(collapsedScreen.destination) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    collapsedScreen.icon()
                }
            }
        }
        return
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
    ) {
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = SonoraShapes.Capsule)
                    .clip(SonoraShapes.Capsule)
                    .background(
                        // Translucent mode still needs a floor under the text. Album art scrolling
                        // behind a fully transparent capsule made the labels unreadable on light
                        // covers, which is the "looks like a bug" case rather than a glass effect.
                        if (isTranslucentBackground) {
                            MaterialTheme.colorScheme.surface.copy(alpha = TRANSLUCENT_BAR_ALPHA)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                    ).padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            // One pill that travels, rather than a pill fading out under the old tab while another
            // fades in under the new one. A cross fade reads as two separate shapes blinking; a
            // single moving indicator reads as one object being carried to where you pointed, and
            // it is the thing that tells you the tabs are siblings. The glass variant already
            // slides its blob, so this also stops the two nav bars behaving differently.
            //
            // Every tab takes weight(1f), so each is exactly this wide and the offset is a plain
            // multiple of the index. No measurement of individual children is needed.
            val itemWidth = maxWidth / bottomNavScreens.size
            val pillOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                // Slightly underdamped so it settles rather than stopping dead. A visible bounce on
                // a control this small would read as sloppy, so the overshoot stays under one pixel
                // at these distances and only shows up as easing.
                animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                label = "navPillOffset",
            )
            // matchParentSize keeps the pill out of the parent's own size calculation, so the row of
            // tabs still decides the capsule height and the pill just fills whatever that turns out
            // to be. Declared before the Row so it paints behind the icons and labels.
            Box(Modifier.matchParentSize()) {
                Box(
                    Modifier
                        .offset(x = pillOffset)
                        .width(itemWidth)
                        .fillMaxHeight()
                        .clip(SonoraShapes.Capsule)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                bottomNavScreens.forEach { screen ->
                    val selected = selectedIndex == screen.ordinal
                    NavCapsuleItem(
                        screen = screen,
                        selected = selected,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (selected) {
                                if (currentBackStackEntry?.destination?.hierarchy?.any {
                                        it.hasRoute(screen.destination::class)
                                    } == true
                                ) {
                                    reloadDestinationIfNeeded(screen.destination::class)
                                } else {
                                    navController.navigate(screen.destination)
                                }
                            } else {
                                selectedIndex = screen.ordinal
                                navController.navigate(screen.destination) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavCapsuleItem(
    screen: BottomNavScreen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Black on the brand blue in both themes: onPrimary would flip light in dark mode and lose
    // contrast against the accent.
    val content by animateColorAsState(
        targetValue =
            if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(220),
        label = "navContent",
    )
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier =
            modifier
                // 48dp is the floor for a comfortable target; the capsule's own padding sits
                // outside it so the touch area is not eaten by the rounded shape.
                .heightIn(min = 48.dp)
                .clip(SonoraShapes.Capsule)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.Tab,
                    interactionSource = interaction,
                    indication = null,
                ).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // BottomNavScreen.icon is a pre-baked composable with no tint parameter, so the colour has
        // to arrive through LocalContentColor, which is what Icon falls back to.
        CompositionLocalProvider(LocalContentColor provides content) {
            screen.icon()
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(screen.title),
            style = typo().bodySmall,
            color = content,
        )
    }
}

@Composable
fun AppNavigationRail(
    startDestination: Any = HomeDestination,
    navController: NavController,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavScreens =
        listOf(
            BottomNavScreen.Home,
            BottomNavScreen.Search,
            BottomNavScreen.Library,
        )
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(
            when (startDestination) {
                is HomeDestination -> BottomNavScreen.Home.ordinal
                is SearchDestination -> BottomNavScreen.Search.ordinal
                is LibraryDestination -> BottomNavScreen.Library.ordinal
                else -> BottomNavScreen.Home.ordinal // Default to Home if not recognized
            },
        )
    }
    NavigationRail {
        Spacer(Modifier.height(16.dp))
        Box(Modifier.padding(horizontal = 16.dp)) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.mono),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .height(32.dp)
                            .clip(CircleShape),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        bottomNavScreens.forEachIndexed { index, screen ->
            NavigationRailItem(
                icon = screen.icon,
                label = {
                    Text(
                        stringResource(screen.title),
                        style =
                            if (selectedIndex == screen.ordinal) {
                                typo().bodySmall
                            } else {
                                typo().bodySmall.greyScale()
                            },
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    if (selectedIndex == screen.ordinal) {
                        if (currentBackStackEntry?.destination?.hierarchy?.any {
                                it.hasRoute(screen.destination::class)
                            } == true
                        ) {
                            reloadDestinationIfNeeded(
                                screen.destination::class,
                            )
                        } else {
                            navController.navigate(screen.destination)
                        }
                    } else {
                        selectedIndex = screen.ordinal
                        navController.navigate(screen.destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}