package app.vanshajpahwa.common

/**
 * A per-track answer to "play the video?", overriding the global setting.
 *
 * The media source is built on the playback thread by a factory that cannot suspend, so the choice
 * has to be readable synchronously from there. Null means no choice has been made for this track
 * and the stored setting decides.
 */
object VideoPlaybackOverride {
    @Volatile
    var enabled: Boolean? = null
}
