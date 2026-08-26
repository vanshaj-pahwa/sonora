<div align="center">

# Sonora

An ad-free YouTube Music client for Android, built with Jetpack Compose.

</div>

## Install

Download `sonora-<version>.apk` from the
[latest release](https://github.com/vanshaj-pahwa/sonora/releases/latest) and install it.

Sonora needs Android 8.0 or newer on a 64-bit
device.

Android shows a warning when installing apps from outside the Play Store. This is expected for any
sideloaded app.

## Features

**Playback**

- Stream music and music videos from YouTube Music, ad-free, with background playback
- Offline downloads, and playback of local files already on the device
- An endless queue that carries on past the end of a playlist, choosing what follows from what you
  have just been listening to
- Crossfade between tracks, with optional beat-matched transitions
- Equalizer, sleep timer, and SponsorBlock segment skipping
- Podcasts

**Lyrics and metadata**

- Synced, word-by-word lyrics from multiple providers
- AI-assisted lyric translation
- Spotify Canvas artwork

**Library**

- Local playlists, with optional two-way sync to YouTube playlists
- Multi-select for batch liking and downloading
- Listening analytics, kept on device
- Backup, restore and import

**Interface**

- Liquid glass theming, searchable settings, and a shared design system
- Shows which device the sound is going to, so a dropped pair of earbuds announces itself

## Build

Requires JDK 21 and the Android SDK.

```bash
./gradlew :app:assembleDebug
```

The APK lands in `app/build/outputs/apk/debug/`.

There is one build. It needs no API keys, no Google Play Services and no `local.properties`
entries: clone it and it compiles.

Release builds land in `app/build/outputs/apk/release/`. They are unsigned unless the signing
variables are present in the environment, so `build-and-sign-apk.sh` is the way to produce a signed
APK locally. Tagged releases are signed on CI from repository secrets.

## Credits

Sonora is a fork of [SimpMusic](https://github.com/maxrave-dev/SimpMusic) by Tuan Minh Nguyen Duc
([maxrave-dev](https://github.com/maxrave-dev)), licensed under GPL-3.0. The original project is
the foundation this is built on, and the work of its author and contributors is gratefully
acknowledged.

Sonora also builds on:

- [PipePipeExtractor](https://github.com/maxrave-dev/PipePipeExtractor) and BravePipeExtractor,
  for stream extraction
- [NewPipe Extractor](https://github.com/TeamNewPipe/NewPipeExtractor), upstream of the above
- [SponsorBlock](https://sponsor.ajay.app/), for skipping non-music segments
- [LRCLIB](https://lrclib.net/) and BetterLyrics, as lyrics providers
- [Auxio](https://github.com/OxygenCobalt/Auxio), for its shuffle order implementation

## License

Sonora is licensed under the [GNU General Public License v3.0](LICENSE), inherited from SimpMusic.

You are free to use, modify and redistribute it under the same terms. Any distributed derivative
must remain GPL-3.0, keep the source available, and preserve the copyright notices of prior
authors.

Copyright (C) 2026 Vanshaj Pahwa, for changes made in this fork.

## Disclaimer

Sonora is not affiliated with, endorsed by, or sponsored by YouTube, Google LLC, Spotify, TIDAL or
Last.fm. All trademarks belong to their respective owners. This project is for personal and
educational use.
