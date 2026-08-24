package app.vanshajpahwa.data.dataStore

import java.util.Locale

actual fun defaultRegionCode(): String = Locale.getDefault().country.takeIf { it.length == 2 } ?: "US"
