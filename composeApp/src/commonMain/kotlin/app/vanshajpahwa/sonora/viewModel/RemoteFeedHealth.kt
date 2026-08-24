package app.vanshajpahwa.sonora.viewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Whether the last remote fetch came back, shared app wide.
 *
 * A single boolean rather than a connectivity API on purpose. Nothing in this app has ever asked
 * the system whether it has a network, and the honest signal available is simply whether the
 * requests we made returned. That is also the signal a user cares about: an unreachable backend and
 * a dropped connection look identical from the sofa.
 *
 * Held here rather than on a screen's view model because the bar that reports it lives in the app
 * scaffold next to the mini player, outside any one screen's composition.
 */
class RemoteFeedHealth {
    private val _unreachable = MutableStateFlow(false)

    val unreachable: StateFlow<Boolean> = _unreachable.asStateFlow()

    /**
     * Report the outcome of a remote fetch.
     *
     * Only a total failure counts. A partially loaded page still has something to show, and a bar
     * saying otherwise while shelves sit on screen was the exact complaint that started this.
     */
    fun report(allRequestsFailed: Boolean) {
        _unreachable.value = allRequestsFailed
    }

    private val _retries = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Whoever owns the fetch listens here, so the bar can offer Retry without knowing who that is. */
    val retries: SharedFlow<Unit> = _retries.asSharedFlow()

    fun requestRetry() {
        _retries.tryEmit(Unit)
    }
}
