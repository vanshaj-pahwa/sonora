package app.vanshajpahwa.sonora.expect

enum class Orientation {
    PORTRAIT, LANDSCAPE, UNSPECIFIED
}

expect fun currentOrientation(): Orientation