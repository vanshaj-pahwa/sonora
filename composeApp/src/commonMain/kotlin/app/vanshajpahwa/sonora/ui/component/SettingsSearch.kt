package app.vanshajpahwa.sonora.ui.component

import androidx.compose.runtime.compositionLocalOf

/**
 * The active settings search query, provided by the settings screen.
 *
 * A composition local so [SettingItem] can filter itself. The alternative was a data model of all 76
 * settings to filter before rendering, but each one carries its own click handler, switch state and
 * enable gate, so that model would have had to describe behaviour as well as text, and every new
 * setting would need adding in two places forever. Filtering at the leaf keeps one definition per
 * setting and makes search work for settings nobody remembered to register.
 */
val LocalSettingsSearchQuery = compositionLocalOf { "" }

/**
 * Punctuation and case carry no meaning in a search box, so both sides are flattened to lowercase
 * words before anything is compared.
 */
private fun normalize(text: String): String = text.lowercase().map { if (it.isLetterOrDigit()) it else ' ' }.joinToString("")

/**
 * Words people actually type mapped to the words the settings actually use.
 *
 * Search fails most often when the user's word for a thing is not the developer's word for it.
 * Someone looking for "dark mode" should not have to know the setting is called "Theme", and someone
 * typing "battery" is looking for whatever the app calls background behaviour. This is a small hand
 * written table rather than anything clever, because the vocabulary of one settings screen is small
 * and finite, and a wrong guess here is worse than no guess.
 */
private val SYNONYMS: Map<String, List<String>> =
    mapOf(
        "dark" to listOf("theme", "amoled", "black", "night"),
        "light" to listOf("theme"),
        "colour" to listOf("color", "theme", "custom"),
        "color" to listOf("theme", "custom"),
        "font" to listOf("typeface", "text"),
        "text" to listOf("font", "title", "lyrics"),
        "lyric" to listOf("lyrics", "translation", "translate"),
        "translate" to listOf("translation", "lyrics", "language"),
        "sound" to listOf("audio", "quality", "volume", "normalization"),
        "audio" to listOf("sound", "quality", "volume"),
        "quality" to listOf("audio", "video", "bitrate"),
        "storage" to listOf("cache", "download", "downloaded", "space"),
        "cache" to listOf("storage", "space", "thumbnail"),
        "offline" to listOf("download", "downloaded"),
        "download" to listOf("offline", "storage"),
        "account" to listOf("youtube", "login", "google", "spotify"),
        "login" to listOf("account", "sign", "youtube", "spotify"),
        "privacy" to listOf("data", "tracking", "analytics", "history"),
        "history" to listOf("recent", "played", "tracking", "search"),
        "scroll" to listOf("scrolling", "marquee", "title"),
        "glass" to listOf("liquid", "translucent", "blur"),
        "blur" to listOf("glass", "liquid", "backdrop", "translucent"),
        "transparent" to listOf("translucent", "glass"),
        "player" to listOf("mini", "now", "playing", "playback"),
        "notification" to listOf("notify", "alert"),
        "backup" to listOf("restore", "export", "import"),
        "restore" to listOf("backup", "import"),
        "language" to listOf("locale", "region", "translation"),
        "video" to listOf("quality", "youtube"),
        "sleep" to listOf("timer"),
        "crossfade" to listOf("fade", "transition", "mix"),
    )

/**
 * Whether a setting should stay visible for [query].
 *
 * Every token in the query has to match something, so extra words narrow the results rather than
 * widening them, which is what people expect when they add a word to refine a search.
 *
 * A token matches three ways, in descending order of confidence:
 *  1. it appears in the title or subtitle
 *  2. one of its synonyms appears there
 *  3. its letters appear in order somewhere in the text, which catches typos and abbreviations, so
 *     "trnslu" and "lqdglass" still find the right rows
 *
 * The subsequence pass is deliberately restricted to tokens of three characters or more. On one or
 * two letters almost everything is a subsequence of almost everything, and the list would stop
 * filtering at exactly the moment the user has typed the least.
 */
fun settingMatchesQuery(
    query: String,
    title: String,
    subtitle: String,
): Boolean {
    val q = normalize(query).trim()
    if (q.isEmpty()) return true
    val haystack = normalize("$title $subtitle")
    return q.split(" ").filter { it.isNotEmpty() }.all { token ->
        haystack.contains(token) ||
            SYNONYMS[token]?.any { haystack.contains(it) } == true ||
            (token.length >= 3 && isSubsequence(token, haystack))
    }
}

/** True when every character of [needle] appears in [haystack] in order, gaps allowed. */
private fun isSubsequence(
    needle: String,
    haystack: String,
): Boolean {
    var i = 0
    for (c in haystack) {
        if (i < needle.length && needle[i] == c) i++
        if (i == needle.length) return true
    }
    return i == needle.length
}
