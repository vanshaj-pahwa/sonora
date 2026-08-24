package app.vanshajpahwa.sonora.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.vanshajpahwa.domain.manager.DataStoreManager
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.bricolage_variable
import app.vanshajpahwa.sonora.resources.figtree_variable
import app.vanshajpahwa.sonora.resources.inter_variable
import app.vanshajpahwa.sonora.resources.jakarta_variable
import app.vanshajpahwa.sonora.resources.manrope_variable
import app.vanshajpahwa.sonora.resources.montserrat_variable
import app.vanshajpahwa.sonora.resources.outfit_variable
import app.vanshajpahwa.sonora.resources.poppins_bold
import app.vanshajpahwa.sonora.resources.poppins_medium
import app.vanshajpahwa.sonora.resources.poppins_regular
import app.vanshajpahwa.sonora.resources.space_grotesk_variable

/**
 * A variable font registered at the four weights the type scale asks for.
 *
 * The weight axis has to be set explicitly. Declaring the same variable file four times with only a
 * [FontWeight] tells the matcher which entry to pick, but nothing tells the rasteriser which
 * instance to cut, so every entry renders at the file's default instance and the whole app comes out
 * at one weight. Passing FontVariation.Settings(weight) is what actually moves the axis.
 */
@Composable
private fun variableFamily(resource: FontResource): FontFamily =
    FontFamily(
        listOf(
            FontWeight.Normal,
            FontWeight.Medium,
            FontWeight.SemiBold,
            FontWeight.Bold,
        ).map { weight ->
            Font(
                resource,
                weight,
                FontStyle.Normal,
                FontVariation.Settings(FontVariation.weight(weight.weight)),
            )
        },
    )

/**
 * The app typeface.
 *
 * Manrope is the default rather than Poppins. Measured from the font files themselves: Manrope holds
 * essentially the same x-height (0.540 em against Poppins' 0.548, so letters read the same size at a
 * given sp) while running about 10% narrower per lowercase letter (0.503 em against 0.561). Poppins
 * is a geometric display face, built from near-circular forms with wide sidebearings, and this app is
 * mostly 11 to 13sp rows of a track title over an artist name, both of them truncated. That extra
 * width buys no legibility and costs characters before the ellipsis.
 *
 * It is also 165KB as one variable file, against 1.4MB for Poppins' three static ones.
 *
 * Poppins stays available in the picker, so this is a change of default, not a removal.
 */
@Composable
fun fontFamily(choice: String = DataStoreManager.FONT_MANROPE): FontFamily =
    when (choice) {
        // Platform face. Costs nothing in APK size and follows any font the user has set at
        // system level, which is the accessible option.
        DataStoreManager.FONT_SYSTEM -> FontFamily.Default
        DataStoreManager.FONT_BRICOLAGE -> variableFamily(Res.font.bricolage_variable)
        DataStoreManager.FONT_FIGTREE -> variableFamily(Res.font.figtree_variable)
        DataStoreManager.FONT_JAKARTA -> variableFamily(Res.font.jakarta_variable)
        DataStoreManager.FONT_OUTFIT -> variableFamily(Res.font.outfit_variable)
        DataStoreManager.FONT_SPACE_GROTESK -> variableFamily(Res.font.space_grotesk_variable)
        DataStoreManager.FONT_MONTSERRAT -> variableFamily(Res.font.montserrat_variable)
        DataStoreManager.FONT_INTER -> variableFamily(Res.font.inter_variable)
        // Three static cuts, registered against the weights that actually use them. Only
        // poppins_medium was registered at one point, mapped to Normal, while the type scale asks for
        // SemiBold and Bold throughout; the renderer answered those by synthesising a fake bold from
        // the Medium outline, which is why headings looked soft.
        DataStoreManager.FONT_POPPINS ->
            FontFamily(
                Font(Res.font.poppins_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.poppins_medium, FontWeight.Medium, FontStyle.Normal),
                Font(Res.font.poppins_bold, FontWeight.SemiBold, FontStyle.Normal),
                Font(Res.font.poppins_bold, FontWeight.Bold, FontStyle.Normal),
            )
        else -> variableFamily(Res.font.manrope_variable)
    }

/**
 * When true, [typo] keeps the original always-light text colors (pure white titles, #A8A8A8 body)
 * regardless of theme. Immersive screens drawn over dark artwork provide `true` so their text stays
 * readable at light theme. Everything else leaves it false and gets theme-aware colors.
 */
val LocalForceDarkText = staticCompositionLocalOf { false }

/**
 * The user's chosen typeface, provided by [AppTheme].
 *
 * A CompositionLocal rather than a parameter because `typo()` is called directly from roughly two
 * hundred call sites; threading the choice through all of them would be a far larger change than
 * the feature warrants.
 */
val LocalFontChoice = staticCompositionLocalOf { DataStoreManager.FONT_MANROPE }

@Composable
fun typo(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    forceDark: Boolean = LocalForceDarkText.current,
    fontChoice: String = LocalFontChoice.current,
): Typography {
    val fontFamily = fontFamily(fontChoice)

    // Titles were pure white, everything else muted gray (#A8A8A8) in the old dark-only palette.
    // forceDark keeps that; otherwise both come from the scheme (theme-aware light/dark).
    val titleColor = if (forceDark) Color.White else colorScheme.onBackground
    val bodyColor = if (forceDark) Color(0xFFA8A8A8) else colorScheme.onSurfaceVariant

    val typo =
        Typography(
            /***
             * This typo().is use for the title of the Playlist, Artist, Song, Album, etc. in Home, Mood, Genre, Playlist, etc.
             */
            titleSmall =
                TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily,
                    color = titleColor,
                ),
            titleMedium =
                TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily,
                    color = titleColor,
                ),
            titleLarge =
                TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = titleColor,
                ),
            bodySmall =
                TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            bodyMedium =
                TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            bodyLarge =
                TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            displayLarge =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            headlineMedium =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            headlineLarge =
                TextStyle(
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            /**
             * Material components that take a label (FilterChip, Button, TextButton) default to
             * labelLarge. Leaving it undefined let them fall through to Material's built-in style,
             * which carries the platform font, so the Home chips rendered in a different typeface
             * from the rest of the app.
             *
             * Metrics are Material's own defaults on purpose, so only the typeface changes and
             * nothing shifts size. No color either: chips and buttons supply their own content
             * color, and setting one here would override the selected/unselected label states.
             */
            labelLarge =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily,
                ),
            labelMedium =
                TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            labelSmall =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily,
                    color = bodyColor,
                ),
            // ...
        )
    return typo
}
