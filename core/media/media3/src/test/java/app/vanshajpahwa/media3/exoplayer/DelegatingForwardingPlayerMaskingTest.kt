package app.vanshajpahwa.media3.exoplayer

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import app.vanshajpahwa.media3.exoplayer.DelegatingForwardingPlayer.Masking.maskPlaybackState
import app.vanshajpahwa.media3.exoplayer.DelegatingForwardingPlayer.Masking.maskPlayWhenReady
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the state masking that keeps the media notification alive across a track swap.
 *
 * A skip builds a new ExoPlayer and swaps it in under the MediaSession. A fresh player reports
 * STATE_IDLE with playWhenReady false, and Media3 responds to idle-and-paused by tearing the
 * notification down and leaving the foreground service, which is the notification visibly vanishing
 * and returning on every skip.
 *
 * The cases that matter most are the negative ones. A mask that never clears is worse than the bug
 * it fixes, because a genuinely paused player would keep reporting itself as playing for the rest of
 * the session and the notification would never return to a pause state.
 */
@UnstableApi
class DelegatingForwardingPlayerMaskingTest {
    @Test
    fun `idle during a swap reports buffering`() {
        assertEquals(
            Player.STATE_BUFFERING,
            maskPlaybackState(Player.STATE_IDLE, isSwapping = true, suppressEnded = false),
        )
    }

    @Test
    fun `idle outside a swap stays idle`() {
        assertEquals(
            Player.STATE_IDLE,
            maskPlaybackState(Player.STATE_IDLE, isSwapping = false, suppressEnded = false),
        )
    }

    @Test
    fun `ended is masked only while suppressed`() {
        assertEquals(
            Player.STATE_BUFFERING,
            maskPlaybackState(Player.STATE_ENDED, isSwapping = false, suppressEnded = true),
        )
        assertEquals(
            Player.STATE_ENDED,
            maskPlaybackState(Player.STATE_ENDED, isSwapping = false, suppressEnded = false),
        )
    }

    @Test
    fun `ready and buffering are never rewritten`() {
        for (raw in intArrayOf(Player.STATE_READY, Player.STATE_BUFFERING)) {
            assertEquals(raw, maskPlaybackState(raw, isSwapping = true, suppressEnded = true))
            assertEquals(raw, maskPlaybackState(raw, isSwapping = false, suppressEnded = false))
        }
    }

    @Test
    fun `ended during a swap is not turned into buffering by the idle rule`() {
        // Only suppressEnded may mask ENDED. The swap flag must not quietly extend to it, or the end
        // of a queue would look like it was still loading something.
        assertEquals(
            Player.STATE_ENDED,
            maskPlaybackState(Player.STATE_ENDED, isSwapping = true, suppressEnded = false),
        )
    }

    @Test
    fun `playWhenReady takes the intended value during a swap`() {
        assertTrue(maskPlayWhenReady(raw = false, isSwapping = true, override = true))
    }

    @Test
    fun `a swap that was not going to play is not forced to play`() {
        assertFalse(maskPlayWhenReady(raw = false, isSwapping = true, override = false))
    }

    @Test
    fun `a stale override masks nothing once the swap flag is down`() {
        // This is the leak case. The adapter clears both in a finally, including on cancellation
        // from a rapid second skip; if only the flag were cleared this must still report the truth.
        assertFalse(maskPlayWhenReady(raw = false, isSwapping = false, override = true))
    }

    @Test
    fun `with no override the real value passes through`() {
        assertTrue(maskPlayWhenReady(raw = true, isSwapping = true, override = null))
        assertFalse(maskPlayWhenReady(raw = false, isSwapping = true, override = null))
    }
}
