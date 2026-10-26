package app.vanshajpahwa.domain.data.model.update

data class UpdateData(
    val tagName: String,
    val releaseTime: String?,
    val body: String,
    // The release's APK, for installing from inside the app. Null when the release has none.
    val apkUrl: String? = null,
)