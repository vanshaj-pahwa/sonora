package app.vanshajpahwa.sonora.expect

/**
 * Opens the system's output picker.
 *
 * The system already owns this sheet and lists every route the device can reach, so building our own
 * would duplicate it worse and drift from it. Does nothing where no such picker exists.
 */
expect fun openOutputSwitcher()
