package app.vanshajpahwa.domain.data.model.home

import app.vanshajpahwa.domain.data.model.home.chart.Chart
import app.vanshajpahwa.domain.data.model.mood.Mood
import app.vanshajpahwa.domain.utils.Resource

data class HomeResponse(
    val homeItem: Resource<ArrayList<HomeItem>>,
    val exploreMood: Resource<Mood>,
    val exploreChart: Resource<Chart>,
)