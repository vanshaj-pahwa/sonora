package app.vanshajpahwa.domain.repository

import app.vanshajpahwa.domain.data.model.update.UpdateData
import app.vanshajpahwa.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun checkForGithubReleaseUpdate(): Flow<Resource<UpdateData>>
    fun checkForFdroidUpdate(): Flow<Resource<UpdateData>>
}