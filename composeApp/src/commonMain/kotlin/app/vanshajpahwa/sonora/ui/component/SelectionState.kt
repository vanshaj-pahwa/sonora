package app.vanshajpahwa.sonora.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Multi select for song lists.
 *
 * Hoisted per screen rather than held inside the row, because the row is shared by five screens and
 * a row that owned its own selection could not be asked "how many are selected" by the bar above it.
 *
 * Selection mode is defined as "the set is not empty" rather than tracked with a separate boolean.
 * Two sources of truth for one idea is how you end up with an action bar showing zero items, or an
 * empty bar that will not go away. Exiting the mode is therefore just [clear].
 *
 * Ids are videoIds. They survive the list being re-sorted, re-filtered or paged, which positional
 * indices would not, and every batch action downstream wants ids anyway.
 */
@Stable
class SelectionState(
    initial: Set<String> = emptySet(),
) {
    var selected by mutableStateOf(initial)
        private set

    val isActive: Boolean get() = selected.isNotEmpty()

    val count: Int get() = selected.size

    fun isSelected(id: String): Boolean = id in selected

    fun toggle(id: String) {
        if (id.isEmpty()) return
        selected = if (id in selected) selected - id else selected + id
    }

    fun selectAll(ids: Collection<String>) {
        selected = selected + ids.filter { it.isNotEmpty() }
    }

    fun clear() {
        selected = emptySet()
    }

    companion object {
        val Saver: Saver<SelectionState, List<String>> =
            Saver(
                save = { it.selected.toList() },
                restore = { SelectionState(it.toSet()) },
            )
    }
}

/**
 * Survives rotation and process death, so a long selection is not lost to turning the phone.
 */
@Composable
fun rememberSelectionState(): SelectionState =
    rememberSaveable(saver = SelectionState.Saver) { SelectionState() }
