# Total Music project entry point

The active Total Music maintenance line is the `total-music-stable` branch of
`nexlibre-group/total-music`. This branch preserves the imported source.

Before changing application code or compiling Total Music, fetch and check out
`total-music-stable`, then read its AGENTS.md and docs/TOTAL_MUSIC_RELEASES.md.
Do not rebuild the app from scratch, force-push, or replace Space Music history.
Keep `org.nexlibre.totalmusic` and the permanent signing certificate unchanged.
The private signing key and password are stored separately in the owner's
private Library; never create replacement keys or put secrets in git or CI.
Use the documented version increment, build, local signing and validation steps.
