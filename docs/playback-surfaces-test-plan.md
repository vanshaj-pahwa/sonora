# Playback surfaces test plan

Manual test matrix for the three surfaces that control playback from outside the app: the media
notification, the home screen widget, and Android Auto. These are the surfaces a new user touches
first and the ones least covered by anything automatic, so they are worth walking before a release.

Each case lists what to do and what counts as a pass. Anything else is a failure, including a
surface that recovers after a delay: coming back is not the same as never leaving.

---

## A. Home screen widget

The widget had a self feeding rebuild loop: `provideGlance` launched collectors that called
`update()`, and `update()` re-invokes `provideGlance`. A track change emits on two flows at once, so
one skip started several overlapping rebuilds and each rebuild threw the RemoteViews away. The
symptom was the widget vanishing on skip and returning seconds later.

| # | Steps | Pass |
|---|---|---|
| A1 | Play a track, press next on the widget | Widget stays on screen the whole time. Title and art change in place. No blank frame, no flicker |
| A2 | Press next five times quickly | Widget never disappears. Ends on the fifth track, not an earlier one |
| A3 | Press previous within 3 seconds of a track starting | Goes to the previous track, widget stays put |
| A4 | Press previous after 10 seconds | Restarts the current track, widget stays put |
| A5 | Play/pause from the widget repeatedly | Icon flips each time and matches actual audio. No rebuild |
| A6 | Skip past the last track in the queue | Behaves per repeat mode, widget survives |
| A7 | Skip with the phone locked, then unlock | Widget shows the correct track, not a stale one |
| A8 | Resize the widget between 4x1 and larger | Compact layout drops shuffle and repeat, both layouts render fully |
| A9 | Leave the widget alone for 10 minutes of playback | Still tracking the current song, no drift, no disappearance |
| A10 | Force stop the app, look at the widget | Degrades to an idle state rather than showing a stale track as playing |

## B. Media notification

A skip builds a new ExoPlayer and swaps it under the MediaSession. A fresh player reports
`STATE_IDLE` with `playWhenReady` false, and Media3 responds to idle-and-paused by dropping the
notification and leaving the foreground service. The swap is now masked while it is in flight.

| # | Steps | Pass |
|---|---|---|
| B1 | Play, then next from the notification | Notification stays posted continuously. Metadata updates in place |
| B2 | Next five times rapidly | Notification never drops. No duplicate notifications appear |
| B3 | Pause, wait 30 seconds, resume | One notification throughout. Foreground state correct |
| B4 | Skip during a crossfade (enable crossfade first) | Notification survives, lands on the right track |
| B5 | Skip the last track in the queue | Notification reflects the end state rather than vanishing |
| B6 | Skip with the screen off, then wake | Correct track, single notification |
| B7 | Enable "keep service alive", play for 5 minutes, skip several times | Exactly one notification. Previously each posted notification started its own repeating `startForeground` timer |
| B8 | Swipe the notification away while paused | Dismisses cleanly, service stops, no ghost notification |
| B9 | Play, then kill the app from recents | Notification behaves per the keep-alive setting, no crash |
| B10 | Change output device mid playback (unplug headphones) | Playback pauses, notification stays consistent with that |

## C. Android Auto

Not yet verified on real hardware. The browse tree, not the Car App Library, is what renders here.

| # | Steps | Pass |
|---|---|---|
| C1 | Connect to Auto, open Sonora | Tabs appear: Home, Recently played, Favourites, Songs. No "No items" |
| C2 | Open each tab | Content loads in every one. Browsable items render as a grid, playable ones as a list |
| C3 | Tap a song from search results | That song plays. Historically resolved only from the database, so a fresh search result played the previous song instead |
| C4 | Skip from the car controls | Track changes, metadata on the head unit follows |
| C5 | Voice search for an artist | Returns results and plays |
| C6 | Disconnect and reconnect | Recovers without a restart |

---

## Notes for whoever runs this

- Run A and B with the app both foregrounded and fully backgrounded. The bugs above only showed
  themselves in one of those states.
- Rapid repeated input is the useful case, not single presses. Both of the fixed bugs needed
  overlapping operations to appear, and a single unhurried skip can look fine while the surface is
  still broken.
- Watch for recovery-after-a-delay and treat it as a failure. Both original reports were "it comes
  back after a few seconds", which is what a rebuild storm looks like from the outside.
