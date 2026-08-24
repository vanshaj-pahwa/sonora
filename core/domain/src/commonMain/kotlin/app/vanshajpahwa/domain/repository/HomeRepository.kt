package app.vanshajpahwa.domain.repository

import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.data.model.home.chart.Chart
import app.vanshajpahwa.domain.data.model.mood.Mood
import app.vanshajpahwa.domain.data.model.mood.genre.GenreObject
import app.vanshajpahwa.domain.data.model.mood.moodmoments.MoodsMomentObject
import app.vanshajpahwa.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    /**
     * @return Pair of continueParams and HomeItem List
     */
    fun getHomeData(
        params: String? = null,
        viewString: String,
        songString: String,
    ): Flow<Resource<Pair<String?, List<HomeItem>>>>

    fun getHomeDataContinue(
        continueParam: String,
        viewString: String,
        songString: String,
    ): Flow<Resource<Pair<String?, List<HomeItem>>>>

    /**
     * YouTube Music's podcasts browse page.
     *
     * A real browse rather than a search: podcasts had no catalog call at all, and standing a page up
     * on a hardcoded search query would have made its contents look arbitrary. This hits the same kind
     * of endpoint the home feed does and comes back as the same [HomeItem] shelves, so it is
     * personalised for a signed in account and popular content otherwise, with no extra parsing.
     */
    /**
     * Any YouTube Music browse page, parsed into the same [HomeItem] shelves the feed uses.
     *
     * Generic rather than one method per page because these responses are identical in shape; the
     * only thing that varies is the browse id. It also sidesteps the dedicated `newRelease()` parser,
     * which returns an empty album list against the current response shape even though the browse
     * itself carries a full "Albums & singles" shelf.
     */
    fun getBrowseShelves(
        browseId: String,
        viewString: String,
        songString: String,
    ): Flow<Resource<List<HomeItem>>>

    fun getNewRelease(
        newReleaseString: String,
        musicVideoString: String,
    ): Flow<Resource<List<HomeItem>>>

    fun getChartData(countryCode: String = "KR"): Flow<Resource<Chart>>

    fun getMoodAndMomentsData(): Flow<Resource<Mood>>

    /**
     * Cover art for one browse category, or null when it cannot be resolved.
     *
     * The category list has no artwork field, so this costs a full category browse the first time
     * and is cached on disk afterwards. Call it lazily — one category at a time, as its tile
     * actually becomes visible — never for the whole list at once.
     */
    fun getMoodCategoryArtwork(params: String): Flow<String?>

    fun getGenreData(params: String): Flow<Resource<GenreObject>>

    fun getMoodData(params: String): Flow<Resource<MoodsMomentObject>>
}