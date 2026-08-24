package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.extension.shimmer
import app.vanshajpahwa.sonora.ui.theme.LocalAppColors

@Composable
fun HomeItemShimmer() {
    Column {
        Box(
            Modifier
                .width(150.dp)
                .height(36.dp)
                .padding(vertical = 8.dp)
                .background(
                    color = LocalAppColors.current.shimmerBackground,
                ).clip(SonoraShapes.Media)
                .shimmer(),
        )
        LazyRow(userScrollEnabled = false) {
            items(10) {
                PlaylistShimmer()
            }
        }
    }
}

@Composable
fun PlaylistShimmer() {
    Column(
        Modifier
            .height(270.dp)
            .padding(10.dp),
    ) {
        Box(
            Modifier
                .size(160.dp)
                .clip(
                    SonoraShapes.Media,
                ).background(
                    color = LocalAppColors.current.shimmerBackground,
                ).shimmer(),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            Modifier
                .width(130.dp)
                .height(18.dp)
                .clip(
                    SonoraShapes.Media,
                ).background(
                    color = LocalAppColors.current.shimmerBackground,
                ).shimmer(),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            Modifier
                .width(130.dp)
                .height(18.dp)
                .clip(
                    SonoraShapes.Media,
                ).background(
                    color = LocalAppColors.current.shimmerBackground,
                ).shimmer(),
        )
    }
}

@Composable
fun QuickPicksShimmerItem() {
    Row(
        Modifier
            .height(70.dp)
            .padding(10.dp),
    ) {
        Box(
            Modifier
                .size(50.dp)
                .clip(SonoraShapes.Media)
                .background(LocalAppColors.current.shimmerBackground)
                .shimmer(),
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.CenterVertically),
        ) {
            Box(
                Modifier
                    .width(300.dp)
                    .height(21.dp)
                    .clip(SonoraShapes.Media)
                    .background(LocalAppColors.current.shimmerBackground)
                    .shimmer(),
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                Modifier
                    .width(260.dp)
                    .height(21.dp)
                    .clip(SonoraShapes.Media)
                    .background(LocalAppColors.current.shimmerBackground)
                    .shimmer(),
            )
        }
    }
}

@Composable
fun QuickPicksShimmer() {
    Column {
        Box(
            Modifier
                .width(150.dp)
                .height(36.dp)
                .padding(vertical = 8.dp)
                .background(
                    color = LocalAppColors.current.shimmerBackground,
                ).clip(SonoraShapes.Media)
                .shimmer(),
        )
        LazyColumn(userScrollEnabled = false) {
            items(4) {
                QuickPicksShimmerItem()
            }
        }
    }
}

@Composable
fun HomeShimmer() {
    // Mirrors the shape of the real Home, in order: resume card, a three card row, the artist
    // circles, two wide tiles, then feed shelves. It used to open with the vertical Quick picks list
    // and ten carousels, which is the layout Home had before it became local first, so the skeleton
    // promised something different from what arrived and every block jumped on load.
    Column {
        Spacer(Modifier.height(8.dp))
        ShimmerBlock(width = 0.dp, height = 84.dp, corner = 16.dp)

        Spacer(Modifier.height(26.dp))
        ShimmerBlock(width = 150.dp, height = 24.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(3) {
                Column(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(SonoraShapes.Media)
                            .background(LocalAppColors.current.shimmerBackground)
                            .shimmer(),
                    )
                    Spacer(Modifier.height(7.dp))
                    ShimmerBlock(width = 80.dp, height = 12.dp, gutter = 0.dp)
                    Spacer(Modifier.height(4.dp))
                    ShimmerBlock(width = 60.dp, height = 10.dp, gutter = 0.dp)
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        ShimmerBlock(width = 170.dp, height = 24.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            repeat(4) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(LocalAppColors.current.shimmerBackground)
                            .shimmer(),
                    )
                    Spacer(Modifier.height(7.dp))
                    ShimmerBlock(width = 56.dp, height = 10.dp, gutter = 0.dp)
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        Row(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(104.dp)
                        .clip(SonoraShapes.Media)
                        .background(LocalAppColors.current.shimmerBackground)
                        .shimmer(),
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Column(Modifier.padding(horizontal = 16.dp)) {
            repeat(2) { HomeItemShimmer() }
        }
    }
}

/**
 * One shimmering bar. A [width] of 0.dp means fill the row minus [gutter], which is how the full width
 * blocks (the resume card) are drawn without repeating the modifier chain each time.
 */
@Composable
private fun ShimmerBlock(
    width: Dp,
    height: Dp,
    corner: Dp = 8.dp,
    gutter: Dp = 16.dp,
) {
    Box(
        Modifier
            .padding(horizontal = gutter)
            .then(if (width == 0.dp) Modifier.fillMaxWidth() else Modifier.width(width))
            .height(height)
            .clip(SonoraShapes.Media)
            .background(LocalAppColors.current.shimmerBackground)
            .shimmer(),
    )
}

@Composable
fun ShimmerSearchItem() {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail shimmer
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SonoraShapes.Media)
                .background(LocalAppColors.current.shimmerBackground)
                .shimmer()
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Text content shimmer
        Column {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(16.dp)
                    .clip(SonoraShapes.Media)
                    .background(LocalAppColors.current.shimmerBackground)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(14.dp)
                    .clip(SonoraShapes.Media)
                    .background(LocalAppColors.current.shimmerBackground)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .clip(SonoraShapes.Media)
                    .background(LocalAppColors.current.shimmerBackground)
                    .shimmer()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerSearchItemPreview() {
    ShimmerSearchItem()
}
