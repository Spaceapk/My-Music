# Total Music: GitLab → GitHub

Destination: Spaceapk/My-Music. GitLab remains a backup; no source branches were modified.

All 26 GitLab branches, 8 tags and 21 auxiliary references were imported with
create-only names under `archive/gitlab-20261022/` and `gitlab-backup-20261022/`.
The destination's 30 original branch/tag references were checked before and after
an atomic, non-forced push and remain unchanged. The complete local export and
repository bundles were checked with `git fsck`. The source backup contains
922 commits including auxiliary references; My-Music has 3 additional commits.

The 1.1.0 candidate b6eee178 is based on stable 1.0.2 and becomes
`integration/total-music-1.1.0`. The later 1.1.1 branch is also preserved. Its
vertical fader controls are reused, without its version change. The original
Space Music signing bridge is preserved but is not used for Total Music: its
certificate is not the pinned permanent Total Music certificate.

## Integration changes

- Exact independent 31, 62, 125, 250, 500, 1000, 2000, 4000, 8000 and 16000 Hz
  graphic filters, RBJ peaking biquads at octave-band Q=1.414. Not Android's
  device-dependent five-band equalizer. Existing 31-band mode and parametric
  filters remain available; switching modes preserves both gain banks.
- New installations use ten-band mode. Older `total_eq` JSON defaults to the
  original 31-band mode; no existing preference keys are deleted. Both banks,
  mode and custom profiles are serialized in the existing preferences.
- Vertical touch faders support accessibility adjustment and horizontal strip
  scrolling. Factory presets contain both ten- and 31-band curves.
- The recovered adaptive/monochrome icon and opt-in expressive Material theme
  are retained along with the Original theme and existing playback/library.
- GitHub Actions runs tests, lint and builds with `-PspaceSpatialEdition=true`.
  Artifacts record commit, package, version, checksums, test reports and whether
  signing/device testing actually occurred.

## Release gate

No new Android release key is created. The expected public SHA-256 certificate
remains `9ae145a703a85902fe6d6ab3d886f6801873d717929deaa3e3d0b0a81de829fa`.
The original encrypted keystore, separate password file and previous signed APK
are required by `scripts/release/sign_apk.py` before a final update is delivered.
CI outputs a TEST-ONLY debug APK and an unsigned release preparation, never a
stable release. Do not uninstall the user's existing app to install a test APK.
No GitHub Release is published automatically. Test success must be read from
the actual CI run, not inferred from the previous Codemagic release notes.
