package app.vanshajpahwa.domain.data.model.home

import app.vanshajpahwa.domain.data.model.home.chart.Chart
import app.vanshajpahwa.domain.data.model.mood.Mood
import app.vanshajpahwa.domain.utils.Resource

data class HomeDataCombine(
    val home: Resource<Pair<String?, List<HomeItem>>>,
    val mood: Resource<Mood>,
    val chart: Resource<Chart>,
    val newRelease: Resource<List<HomeItem>>,
)