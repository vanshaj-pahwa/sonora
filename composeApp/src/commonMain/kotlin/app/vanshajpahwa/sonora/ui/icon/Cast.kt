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
val SonoraIcons.Cast: ImageVector
  get() {
    if (_Cast != null) {
      return _Cast!!
    }
    _Cast =
      ImageVector.Builder(
          name = "Cast",
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
            moveTo(22f, 18f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 20f)
            horizontalLineTo(15.83f)
            quadToRelative(-0.35f, 0f, -0.59f, -0.26f)
            reflectiveQuadTo(14.98f, 19.13f)
            quadTo(14.83f, 16.75f, 13.89f, 14.69f)
            reflectiveQuadTo(11.44f, 11.02f)
            quadTo(9.93f, 9.42f, 7.91f, 8.4f)
            quadTo(5.9f, 7.38f, 3.58f, 7.1f)
            quadTo(2.93f, 7.02f, 2.46f, 6.59f)
            reflectiveQuadTo(2f, 5.5f)
            reflectiveQuadTo(2.46f, 4.42f)
            reflectiveQuadTo(3.58f, 4f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(22f, 5.18f, 22f, 6f)
            verticalLineTo(18f)
            close()
            moveTo(8f, 20f)
            quadTo(7.6f, 20f, 7.3f, 19.76f)
            reflectiveQuadTo(6.93f, 19.13f)
            quadTo(6.65f, 17.55f, 5.51f, 16.45f)
            reflectiveQuadTo(2.8f, 15.08f)
            quadTo(2.43f, 15.03f, 2.21f, 14.71f)
            reflectiveQuadTo(2f, 14f)
            quadTo(2f, 13.58f, 2.28f, 13.29f)
            reflectiveQuadTo(2.93f, 13.05f)
            quadToRelative(2.35f, 0.3f, 4.01f, 1.98f)
            reflectiveQuadToRelative(1.99f, 4.03f)
            quadToRelative(0.05f, 0.4f, -0.22f, 0.68f)
            reflectiveQuadTo(8f, 20f)
            close()
            moveToRelative(4f, 0f)
            quadToRelative(-0.42f, 0f, -0.71f, -0.27f)
            reflectiveQuadToRelative(-0.34f, -0.7f)
            quadTo(10.6f, 15.83f, 8.34f, 13.6f)
            quadTo(6.08f, 11.38f, 2.88f, 11.05f)
            quadTo(2.48f, 11f, 2.24f, 10.7f)
            reflectiveQuadTo(2f, 10f)
            quadTo(2f, 9.57f, 2.26f, 9.27f)
            quadTo(2.53f, 8.98f, 2.9f, 9.02f)
            quadToRelative(4.03f, 0.33f, 6.85f, 3.13f)
            reflectiveQuadToRelative(3.2f, 6.83f)
            quadTo(13f, 19.4f, 12.71f, 19.7f)
            reflectiveQuadTo(12f, 20f)
            close()
            moveTo(3.25f, 20f)
            quadTo(2.73f, 20f, 2.36f, 19.64f)
            reflectiveQuadTo(2f, 18.75f)
            reflectiveQuadTo(2.36f, 17.86f)
            reflectiveQuadTo(3.25f, 17.5f)
            reflectiveQuadToRelative(0.89f, 0.36f)
            reflectiveQuadTo(4.5f, 18.75f)
            reflectiveQuadTo(4.14f, 19.64f)
            reflectiveQuadTo(3.25f, 20f)
            close()
          }
        }
        .build()
    return _Cast!!
  }

private var _Cast: ImageVector? = null
