package app.vanshajpahwa.sonora.expect

import androidx.compose.runtime.Composable

/**
 * Where the audio is actually coming out.
 *
 * Exists to answer one question quickly: is this playing somewhere I meant it to. The failure it
 * guards against is specific and recoverable but loud, music arriving from the phone speaker in a
 * gym because a pair of earbuds dropped without saying so.
 */
data class OutputDevice(
    val kind: OutputKind,
    /**
     * What the device calls itself, when it says. Blank for the phone's own speaker, which needs no
     * name, and for routes the platform describes only by type.
     */
    val name: String = "",
)

/**
 * The kinds worth telling apart on screen.
 *
 * Deliberately coarse. Android reports a Bluetooth route as A2DP whether it is a pair of earbuds, a
 * kitchen speaker or a car stereo, so splitting [Bluetooth] further would mean guessing, and a
 * confident wrong icon is worse than an honest general one. The device name carries that detail
 * accurately because it is whatever the device calls itself.
 *
 * [Car] is the exception and is not inferred from the audio route at all: projection to Android Auto
 * is reported directly, so it can be stated rather than assumed.
 */
enum class OutputKind {
    Phone,
    Wired,
    Bluetooth,
    Speaker,
    Cast,
    Car,
}

/**
 * The current output, recomposing when it changes.
 *
 * Returns [OutputKind.Phone] when nothing else is connected, which is the truth rather than a
 * fallback: with no headphones and no car, the phone speaker is where the sound goes.
 */
@Composable
expect fun rememberOutputDevice(): OutputDevice
