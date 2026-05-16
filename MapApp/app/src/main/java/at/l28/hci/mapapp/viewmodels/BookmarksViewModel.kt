package at.l28.hci.mapapp.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import at.l28.hci.mapapp.ui.screens.Dataset

class BookmarksViewModel : ViewModel() {
    val bookmarks = mutableStateListOf<Dataset>()

    fun toggle(dataset: Dataset) {
        val existing = bookmarks.find { it.id == dataset.id }
        if (existing != null) bookmarks.remove(existing)
        else bookmarks.add(dataset)
    }

    fun isBookmarked(id: String) = bookmarks.any { it.id == id }
}
