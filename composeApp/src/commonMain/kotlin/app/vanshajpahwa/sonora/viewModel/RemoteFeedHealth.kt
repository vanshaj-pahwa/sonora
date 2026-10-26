package app.vanshajpahwa.sonora.viewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ConnectionStatus {
    Online,
    Offline,

    /** Briefly, after Offline, so the recovery is acknowledged before the bar goes away. */
    BackOnline,
}

/**
 * Whether the app can reach the network, shared app wide.
 *
 * Two signals feed it. The system's default network says when the connection drops and when a
 * validated one comes back, which is what lets the bar react without a tap. Remote fetches still
 * report too, because a network can be up while every request fails, and from the sofa an
 * unreachable backend and a dropped connection look the same.
 *
 * Held here rather than on a screen's view model because the bar that reports it lives in the app
 * scaffold next to the mini player, outside any one screen's composition.
 */
class RemoteFeedHealth {
    private val _status = MutableStateFlow(ConnectionStatus.Online)

    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    /**
     * Report the outcome of a remote fetch.
     *
     * Only a total failure counts. A partially loaded page still has something to show, and a bar
     * saying otherwise while shelves sit on screen was the exact complaint that started this.
     */
    fun report(allRequestsFailed: Boolean) {
        _status.update { current ->
            when {
                allRequestsFailed -> ConnectionStatus.Offline
                current == ConnectionStatus.Offline -> ConnectionStatus.BackOnline
                else -> current
            }
        }
    }

    fun onNetworkLost() {
        _status.value = ConnectionStatus.Offline
    }

    /** A validated network appeared. Retries the feed, so Home fills in without a tap. */
    fun onNetworkAvailable() {
        var recovered = false
        _status.update { current ->
            if (current == ConnectionStatus.Offline) {
                recovered = true
                ConnectionStatus.BackOnline
            } else {
                current
            }
        }
        if (recovered) requestRetry()
    }

    fun backOnlineShown() {
        _status.update { if (it == ConnectionStatus.BackOnline) ConnectionStatus.Online else it }
    }

    private val _retries = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Whoever owns the fetch listens here, so the bar can offer Retry without knowing who that is. */
    val retries: SharedFlow<Unit> = _retries.asSharedFlow()

    fun requestRetry() {
        _retries.tryEmit(Unit)
    }
}
