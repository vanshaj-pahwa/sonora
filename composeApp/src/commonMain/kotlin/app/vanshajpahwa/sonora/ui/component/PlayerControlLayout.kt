package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.domain.mediaservice.handler.ControlState
import app.vanshajpahwa.domain.mediaservice.handler.RepeatState
import app.vanshajpahwa.sonora.ui.icon.PauseCircle
import app.vanshajpahwa.sonora.ui.icon.PlayCircle
import app.vanshajpahwa.sonora.ui.icon.Repeat
import app.vanshajpahwa.sonora.ui.icon.RepeatOne
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.SkipNext
import app.vanshajpahwa.sonora.ui.icon.SkipPrevious
import app.vanshajpahwa.sonora.ui.theme.seed
import app.vanshajpahwa.sonora.viewModel.UIEvent

@Composable
fun PlayerControlLayout(
    controllerState: ControlState,
    isSmallSize: Boolean = false,
    contentColor: Color = Color.White,
    onUIEvent: (UIEvent) -> Unit,
) {
    val height = if (isSmallSize) 56.dp else 96.dp
    // Each pair is (drawn icon size, touch target size).
    //
    // Full-size player: play/pause is deliberately dominant and the surrounding transport
    // controls are small, so the eye lands on one control instead of five competing ones.
    // Play is ~2.7x prev/next; it used to be 1.7x, which read as five similar buttons.
    //
    // The drawn sizes shrank but every touch target is now at least 48.dp, the Material
    // minimum, so the smaller icons are easier to hit than before rather than harder.
    //
    // Mini player targets were 28.dp and 38.dp, both under the 48.dp minimum, on the surface most
    // used while moving. Drawn sizes are unchanged so it still looks compact; only the touch
    // targets grew, which is why the row height rises from 48 to 56.
    val smallIcon = if (isSmallSize) 20.dp to 48.dp else 24.dp to 48.dp
    val mediumIcon = if (isSmallSize) 28.dp to 48.dp else 30.dp to 48.dp
    val bigIcon = if (isSmallSize) 38.dp to 48.dp else 80.dp to 96.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .background(Color.Transparent)
                        .size(smallIcon.second)
                        .aspectRatio(1f)
                        .clip(
                            CircleShape,
                        )
                        .clickable {
                            onUIEvent(UIEvent.Shuffle)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.isShuffle, label = "Shuffle Button") { isShuffle ->
                    if (!isShuffle) {
                        Icon(
                            imageVector = SonoraIcons.Shuffle,
                            tint = contentColor,
                            contentDescription = "",
                            modifier = Modifier.size(smallIcon.first),
                        )
                    } else {
                        Icon(
                            imageVector = SonoraIcons.Shuffle,
                            tint = seed,
                            contentDescription = "",
                            modifier = Modifier.size(smallIcon.first),
                        )
                    }
                }
            }
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .background(Color.Transparent)
                        .size(mediumIcon.second)
                        .aspectRatio(1f)
                        .clip(
                            CircleShape,
                        )
                        .clickable {
                            if (controllerState.isPreviousAvailable) {
                                onUIEvent(UIEvent.Previous)
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SonoraIcons.SkipPrevious,
                    tint = if (controllerState.isPreviousAvailable) contentColor else contentColor.copy(alpha = 0.4f),
                    contentDescription = "",
                    modifier = Modifier.size(mediumIcon.first),
                )
            }
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .background(Color.Transparent)
                        .size(bigIcon.second)
                        .aspectRatio(1f)
                        .clip(
                            CircleShape,
                        )
                        .clickable {
                            onUIEvent(UIEvent.PlayPause)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.isPlaying) { isPlaying ->
                    if (!isPlaying) {
                        Icon(
                            imageVector = SonoraIcons.PlayCircle,
                            tint = contentColor,
                            contentDescription = "",
                            modifier = Modifier.size(bigIcon.first),
                        )
                    } else {
                        Icon(
                            imageVector = SonoraIcons.PauseCircle,
                            tint = contentColor,
                            contentDescription = "",
                            modifier = Modifier.size(bigIcon.first),
                        )
                    }
                }
            }
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .background(Color.Transparent)
                        .size(mediumIcon.second)
                        .aspectRatio(1f)
                        .clip(
                            CircleShape,
                        )
                        .clickable {
                            if (controllerState.isNextAvailable) {
                                onUIEvent(UIEvent.Next)
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SonoraIcons.SkipNext,
                    tint = if (controllerState.isNextAvailable) contentColor else contentColor.copy(alpha = 0.4f),
                    contentDescription = "",
                    modifier = Modifier.size(mediumIcon.first),
                )
            }
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(smallIcon.second)
                        .aspectRatio(1f)
                        .clip(
                            CircleShape,
                        )
                        .clickable {
                            onUIEvent(UIEvent.Repeat)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.repeatState) { rs ->
                    when (rs) {
                        is RepeatState.None -> {
                            Icon(
                                imageVector = SonoraIcons.Repeat,
                                tint = contentColor,
                                contentDescription = "",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }

                        RepeatState.All -> {
                            Icon(
                                imageVector = SonoraIcons.Repeat,
                                tint = seed,
                                contentDescription = "",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }

                        RepeatState.One -> {
                            Icon(
                                imageVector = SonoraIcons.RepeatOne,
                                tint = seed,
                                contentDescription = "",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }
                    }
                }
            }
        }
    }
}