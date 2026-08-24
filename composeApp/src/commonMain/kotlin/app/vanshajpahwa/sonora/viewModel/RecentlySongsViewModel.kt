package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.sonora.pagination.RecentPagingSource
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel

class RecentlySongsViewModel(
    private val songRepository: SongRepository,
) : BaseViewModel() {
    val recentlySongs =
        Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
            ),
        ) {
            RecentPagingSource(songRepository)
        }.flow.cachedIn(viewModelScope)
}