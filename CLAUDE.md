# Sonora

Android YouTube Music client built with Compose Multiplatform, by Vanshaj Pahwa.
Licensed GPL-3.0; attribution lives in `LICENSE` and the README's `## Credits`.

**Respond in English only.** No em dashes, no en dashes, no emoji in code, comments or copy.

---

## Commits

**Never run `git commit` or `git push` without being asked for that specific commit.** Not as the
tail of a task, not because the work is finished and looks right. Make the change, say what it is,
and stop. "Fix X" is an instruction to fix X, never an instruction to publish the fix.

Show user-facing wording, meaning README copy, release notes, strings, and anything else a person
will read, before it goes anywhere. Wording is the author's to approve, not the implementer's to
decide.

**Never add a `Co-Authored-By` trailer, or any other trailer.** Not on any commit, ever.

Conventional Commits for the subject line: `type: summary`, lower case after the colon, no full
stop, imperative mood, 72 characters or fewer. Types in use here:

| Type | For |
|---|---|
| `feat` | a capability the user can see |
| `fix` | a defect, with the symptom named |
| `refactor` | shape changes with no behaviour change |
| `perf` | measured speed or memory work, quote the numbers |
| `build` | Gradle, dependencies, packaging |
| `ci` | workflows |
| `docs` | README, this file, anything under docs/ |
| `test` | tests only |
| `chore` | the rest, sparingly |

Add a scope when it narrows things usefully: `fix(auto):`, `perf(player):`.

**Name the change plainly. No figures of speech.** The subject says what was added, fixed or
changed, in the most ordinary words available. "add output device indicator" not "show where the
audio is going"; "improve mini player readability" not "calm the mini player down"; "reduce bleed
through the translucent bar" not "quiet the bar". Someone scanning `git log` is looking for the
change, not for a turn of phrase, and a subject that has to be decoded is a subject that gets
skipped. Start with a verb, name the thing, and stop.

**Body: as long as the change, and no longer.** A one line subject is the whole commit for a one
line fix, and padding it out with bullets invents significance the change does not have. Add a body
when there is something a reader could not get from the diff: why it was wrong, what else it
touched, what was deliberately left alone. When a body earns its place, one line of context first
if the subject needs it, then bullets, each one change or one reason written as a full sentence.
Six bullets is a ceiling for a commit that genuinely did six things, not a target.

Never cite upstream issue numbers or link the upstream tracker, in commit messages or in code
comments. Describe the defect and its symptom on their own terms; a reader here cannot open
that tracker anyway, and the description is what stays useful.

**Never put raw identifiers or captured data in a commit message.** Channel ids, video ids, rows
read out of the database, log lines from a debugging session: these are the evidence, not the
explanation. State the rule they proved, not the values that proved it. "YouTube issues more than
one channel id for the same artist" is the finding; the two ids that revealed it belong to the
investigation and not to permanent history. They are also read from the author's own library, and
a repository is a worse place for that than a scratch file.

One commit, one idea. A refactor and the bug fix it uncovered are two commits, not one.

---

## Comments

**Comment sparingly.** Most code needs none. Reach for one when a reader would otherwise ask "why
is it like this", and leave the rest to the code. A file where every block carries a paragraph is
one nobody finishes reading, and the comments stop being noticed exactly when one of them matters.

**Two or three lines is a normal comment.** A paragraph needs to earn itself: a trap that has
already cost time, a constraint that is not visible from here, a deliberate choice that looks wrong.
If it is only restating the line below it, delete it.

**Never write the conversation into the code.** A comment is read by someone who was not there. It
must not mention the chat that produced it, a review, a request, a previous attempt, what a value
used to be before it was changed, or who decided anything. No "as discussed", no "the user asked
for", no "this was briefly X", no "reverted from Y", no session or date references.

Describe the code as it stands and why it stands that way. Where the old behaviour genuinely
explains the shape, state it as a property of the problem, not as a story about editing it: "a Box
takes the height of its tallest child, so a fixed height here would floor the section" rather than
"this used to be 300dp and caused a gap". History belongs in git, and git is where to look for it.

---

## Do not touch these

These look like leftover branding. They are not. Renaming any of them breaks the build or a
feature, and the failure is not always at compile time.

| What | Where | Why |
|---|---|---|
| `com.github.maxrave-dev:BravePipeExtractor` | `gradle/libs.versions.toml` | published package; renaming breaks resolution and kills playback |
| `com.github.maxrave-dev:PipePipeExtractor` | same | same |
| `io.github.maxrave-dev:ffmpeg-kit-audio` | same | same |
| `org.simpmusic.gemini-kotlin:openai-client` | same | published package |
| `org.simpmusic:nowplayingcenter`, `org.simpmusic:jmtc` | same | published packages |
| `dev.maxrave.pipepipe.*` | `*/proguard-rules.pro` | keep rules for the extractor's real package |
| `https://api-lyrics.simpmusic.org` | `SonoraLyrics.kt` | live upstream service; changing it breaks lyrics |
| `maxrave-dev/simpmusic-files` | `composeApp/build.gradle.kts` | TIDAL remote config host |
| GPL attribution | `LICENSE`, README `## Credits` | legally required, GPL-3.0 §4/§5 |

The `copyright` string is `©2026 Vanshaj Pahwa`, set deliberately.

Upstream attribution lives in `LICENSE` and the README's `## Credits` section, not in the UI.
`credit_app` is ordinary app copy and carries no legal weight, so it is free to change. This was
written down because the note here previously claimed `credit_app` held the attribution when it
never did, and that sent a compliance check chasing a string that was fine all along. GPL-3.0
§5(a) asks for prominent notices on the modified work; for a source distribution the README is
that place, and it names the upstream author, links the project and states the licence.

---

## What this project is now

- **Android only.** The desktop app, Conveyor packaging, mpv natives and the `scripts/` folder
  were removed. `composeApp` still declares a `jvm()` target with a `jvmMain` source set, and
  ~39 mpv-related Gradle tasks remain in `composeApp/build.gradle.kts` pointing at a deleted
  `scripts/mpv-linux`. They never run for Android. Removing them means removing the whole jvm
  target, which is a large change; it has deliberately been left alone.
- **Namespace:** everything is `app.vanshajpahwa.*`. `applicationId` is `app.vanshajpahwa.sonora`
  (`.dev` suffix on debug). Gradle `rootProject.name = "Sonora"`.
- **`core/` is no longer a git submodule.** It used to point at `maxrave-dev/core`; the copy
  dissolved it into ordinary directories so its 430 files are committable.
- **Deep link scheme is `sonora://`** (watch, playlist, channel, notification). The Last.fm
  integration and its `wordbyword://` auth callback were removed.
- **Update checking is disabled** in `UpdateRepositoryImpl`. Both checks were broken beyond
  branding: the GitHub one offered a build with a different applicationId (installs alongside
  rather than updating), and the F-Droid one asked for an unpublished package.

## Layout

```
app/            Android entry point, widget (Glance), notification, backup workers
composeApp/     all UI (Compose Multiplatform), view models, navigation
core/common     constants, Config.kt
core/domain     entities, repository interfaces, DataStoreManager interface
core/data       repository impls, Room, DataStore
core/media/media3        ExoPlayer, crossfade, Android Auto (carapp/)
core/service/*  kotlin-ytmusic-scraper, spotify, ai-service, lyrics-service, kizzy, ktor-ext
cast            no-op stub; Google Cast is not shipped
                paired modules selected by the isFullBuild Gradle property
```

Dependency rule: UI depends on domain depends on data depends on service. Never upward.

## Build and install

```bash
export JAVA_HOME="C:/Program Files/Microsoft/jdk-21.0.12.8-hotspot"
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-universal-debug.apk
adb shell am start -S -n app.vanshajpahwa.sonora.dev/app.vanshajpahwa.sonora.MainActivity
```

Before any screenshot or frame measurement, check `topResumedActivity` contains `sonora` **and**
that `dumpsys power` reports `mWakefulness=Awake`. A dozing screen still reports a resumed
activity while rendering nothing, so `gfxinfo` returns zero frames and a 4950ms percentile that
looks like data and is not.

---

## Traps that have already cost time here

**Nested `collect` on two Room flows deadlocks the outer one.** `getPlaylistFavorite()` collected
liked albums and started a second `collect` on liked playlists inside it. Room flows never
complete, so the outer collector parked on its first emission forever and favouriting an album was
written but never read again. Use `combine`.

**Compose Resources package is pinned.** `compose.resources { packageOfResClass = ... }` in
`composeApp/build.gradle.kts` sets it to `app.vanshajpahwa.sonora.resources`. Without it the
package derives from `rootProject.name`. This is why renaming the root project is now safe.

**Room schemas are keyed on the database class FQN.** `core/data/schemas/app.vanshajpahwa.data.db.*`
must match the package. Rename the package without moving these folders and `@AutoMigration`
fails at compile time. No IDE refactor moves them; they are data files.

**Android Auto search resolves from the database only, historically.** `onSetMediaItems` used to
look a tapped song up with `getSongById` alone, so a fresh search result resolved to null, the
callback returned an empty list, the queue was never replaced, and `play()` resumed the previous
song. It now falls through DB, then the in-memory search list, then the network.

**Notification channel ids strand.** Changing `NOTIFICATION_CHANNEL_ID` leaves the old channel in
system settings until it is explicitly deleted. The id has never changed under this
applicationId, so nothing is deleted at startup. If it ever does change, delete the old id, never
the live one: Android throws while the playback notification is up, and the app crashes on launch.

**Liquid glass must be a sibling of its backdrop source.** Glass nested inside the element carrying
`layerBackdrop` is a render feedback loop that crashes the RuntimeShader. Home and Library each
build a local backdrop for this reason.

**Glance widgets cannot read `MaterialTheme`.** `MainAppWidget` renders outside the app's
composition, so its colours are literals by necessity. It uses `SizeMode.Responsive` with two
breakpoints; the compact 4x1 strip drops shuffle and repeat.

**Media notification styling is not ours.** It is a standard MediaStyle notification via
`DefaultMediaNotificationProvider`. From Android 13 the system draws media controls and extracts
the background colour from album art. Only the button set is controllable, via `setSlots`.

**Crossfade skips video tracks**, and beat-matching only runs in DJ mode. The TIDAL lookup that
feeds it (BPM, key) is gated on `crossfadeDjMode`, so with DJ mode off there is no remote config
fetch and no per-track lookup.

## Adding an icon

Icons are Material Symbols `ImageVector`s under
`composeApp/src/commonMain/kotlin/app/vanshajpahwa/sonora/ui/icon/`, addressed as
`SonoraIcons.<Name>`. There is no `material-icons-extended` dependency and no XML icon drawable; do
not add either back. See `.claude/skills/sonora-icons/SKILL.md` for the generator recipe and the
axis settings. Each icon needs its own import, because `val SonoraIcons.X` is an extension property.

## Before changing anything

Search for usages first and prove a thing is unreferenced before deleting it. Two separate
dead-code scans in this project produced false results: one missed `import ...generated.resources.X`
because it only looked for `Res.string.X`, and another flagged 29 drawables as dangling. The
`customactivityoncrash_*` strings look unused and are not: a library resolves them by resource
name at runtime.

Build after every change. Verify playback, the database and lyrics on device after anything
touching packages, DI or the manifest, because those fail at runtime rather than at compile.
