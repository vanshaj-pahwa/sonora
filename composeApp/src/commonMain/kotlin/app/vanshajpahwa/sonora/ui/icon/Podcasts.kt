package app.vanshajpahwa.sonora.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
val SonoraIcons.Podcasts: ImageVector
  get() {
    if (_Podcasts != null) {
      return _Podcasts!!
    }
    _Podcasts =
      ImageVector.Builder(
          name = "Podcasts",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(11f, 21f)
            verticalLineTo(13.73f)
            quadTo(10.55f, 13.45f, 10.28f, 13.01f)
            reflectiveQuadTo(10f, 12f)
            quadToRelative(0f, -0.83f, 0.59f, -1.41f)
            reflectiveQuadTo(12f, 10f)
            reflectiveQuadToRelative(1.41f, 0.59f)
            quadTo(14f, 11.18f, 14f, 12f)
            quadToRelative(0f, 0.57f, -0.27f, 1.02f)
            quadTo(13.45f, 13.48f, 13f, 13.73f)
            verticalLineTo(21f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(12f, 22f)
            reflectiveQuadTo(11.29f, 21.71f)
            quadTo(11f, 21.43f, 11f, 21f)
            close()
            moveTo(5.83f, 18.52f)
            quadToRelative(-0.3f, 0.3f, -0.72f, 0.3f)
            reflectiveQuadTo(4.4f, 18.5f)
            quadTo(3.28f, 17.18f, 2.64f, 15.53f)
            reflectiveQuadTo(2f, 12f)
            quadTo(2f, 9.92f, 2.79f, 8.1f)
            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
            quadTo(9.93f, 2f, 12f, 2f)
            reflectiveQuadToRelative(3.9f, 0.79f)
            reflectiveQuadToRelative(3.17f, 2.14f)
            quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
            quadTo(22f, 9.92f, 22f, 12f)
            quadToRelative(0f, 1.88f, -0.64f, 3.53f)
            reflectiveQuadTo(19.6f, 18.5f)
            quadToRelative(-0.27f, 0.32f, -0.69f, 0.34f)
            reflectiveQuadTo(18.2f, 18.55f)
            quadToRelative(-0.27f, -0.28f, -0.27f, -0.7f)
            reflectiveQuadTo(18.2f, 17.1f)
            quadToRelative(0.85f, -1.05f, 1.33f, -2.35f)
            reflectiveQuadTo(20f, 12f)
            quadTo(20f, 8.65f, 17.68f, 6.32f)
            reflectiveQuadTo(12f, 4f)
            reflectiveQuadTo(6.33f, 6.32f)
            reflectiveQuadTo(4f, 12f)
            quadToRelative(0f, 1.45f, 0.48f, 2.74f)
            reflectiveQuadToRelative(1.35f, 2.34f)
            quadTo(6.1f, 17.4f, 6.11f, 17.81f)
            reflectiveQuadTo(5.83f, 18.52f)
            close()
            moveTo(8.65f, 15.7f)
            quadTo(8.35f, 16f, 7.93f, 16.01f)
            reflectiveQuadTo(7.25f, 15.68f)
            quadTo(6.68f, 14.9f, 6.34f, 13.98f)
            reflectiveQuadTo(6f, 12f)
            quadTo(6f, 9.5f, 7.75f, 7.75f)
            reflectiveQuadTo(12f, 6f)
            reflectiveQuadToRelative(4.25f, 1.75f)
            reflectiveQuadTo(18f, 12f)
            quadToRelative(0f, 1.05f, -0.34f, 1.99f)
            reflectiveQuadToRelative(-0.91f, 1.69f)
            quadTo(16.5f, 16f, 16.08f, 16.01f)
            reflectiveQuadTo(15.35f, 15.73f)
            quadToRelative(-0.28f, -0.28f, -0.29f, -0.7f)
            reflectiveQuadTo(15.3f, 14.25f)
            quadToRelative(0.32f, -0.5f, 0.51f, -1.06f)
            reflectiveQuadTo(16f, 12f)
            quadTo(16f, 10.35f, 14.83f, 9.17f)
            reflectiveQuadTo(12f, 8f)
            reflectiveQuadTo(9.18f, 9.17f)
            reflectiveQuadTo(8f, 12f)
            quadToRelative(0f, 0.65f, 0.19f, 1.2f)
            reflectiveQuadTo(8.7f, 14.25f)
            quadToRelative(0.25f, 0.35f, 0.24f, 0.76f)
            quadTo(8.93f, 15.43f, 8.65f, 15.7f)
            close()
          }
        }
        .build()
    return _Podcasts!!
  }

private var _Podcasts: ImageVector? = null
