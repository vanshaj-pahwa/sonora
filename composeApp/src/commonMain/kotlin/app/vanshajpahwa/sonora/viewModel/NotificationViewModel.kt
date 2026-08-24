package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.domain.data.entities.NotificationEntity
import app.vanshajpahwa.domain.repository.CommonRepository
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    commonRepository: CommonRepository,
) : BaseViewModel() {
    private var _listNotification: MutableStateFlow<List<NotificationEntity>?> =
        MutableStateFlow(null)
    val listNotification: StateFlow<List<NotificationEntity>?> = _listNotification

    init {
        viewModelScope.launch {
            commonRepository.getAllNotifications().collect { notificationEntities ->
                _listNotification.value =
                    notificationEntities?.sortedByDescending {
                        it.time
                    }
            }
        }
    }
}