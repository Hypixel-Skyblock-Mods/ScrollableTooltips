# Scrollable Tooltips agent guidance

Scrollable Tooltips is a client-side Kotlin/Fabric mod that lets players scroll
oversized item tooltips vertically with the mouse wheel. It supports Minecraft
26.1.2 and 26.2 from shared sources plus version-specific compatibility code
under `src/26.1.2` and `src/26.2` when the APIs actually differ.

## Multi-version architecture

- Keep every actively supported Minecraft version on `main`. Do not create a
  permanent branch per Minecraft version; use a legacy maintenance branch only
  after a target has been removed from `main` and still needs an exceptional
  fix.
- Treat `gradle/targets.properties` as the source of truth for supported target
  names, Minecraft versions, and target-specific dependency coordinates.
  `settings.gradle.kts` includes the corresponding `versions/<target>` Gradle
  projects from this catalog, and the release workflow consumes a manifest
  generated from the same data.
- Keep scrolling state, input handling, tooltip positioning, tests, resources,
  and other behavior shared under `src/main` whenever the APIs compile for all
  targets.
- Put only compile-time Minecraft/Fabric differences under
  `src/<minecraft-version>/kotlin`. Version folders should expose matching
  classes and method contracts so shared code can use a stable compatibility
  boundary without runtime version checks.
- Each `versions/<target>` directory is a Gradle build target, not a copy of the
  mod. Its JAR combines `src/main/kotlin`, `src/main/resources`, and the matching
  `src/<minecraft-version>/kotlin` source set.
- All active targets share one `mod_version` from `gradle.properties`. A release
  tag `v<mod_version>` must build every target. GitHub receives one release with
  one clearly named JAR per Minecraft version; Modrinth receives one version
  record per JAR with an exact `gameVersions` value.

## Adding or removing a Minecraft target

1. Add or remove the target and its coordinates in `gradle/targets.properties`.
2. Add or remove its marker build file under `versions/<target>/build.gradle.kts`.
3. Add `src/<minecraft-version>/kotlin` implementations only for APIs that
   differ from shared code. Do not copy the entire shared source tree.
4. Run `releaseManifest` and inspect `build/release/manifest.json` when changing
   release targets. The workflow must remain target-agnostic and must not
   hardcode individual Minecraft versions or JAR paths.
5. Run the complete build and do not release while any active target fails.

## Dependency policy

- Declare compile/runtime dependencies in the root `subprojects` block so every
  target receives the same baseline unless a target-specific coordinate is
  genuinely required.
- Keep `fabric.mod.json` requirements and Minotaur's Modrinth dependency block
  synchronized with Gradle. Fabric Language Kotlin is required; Fabric API,
  Mod Menu, MoulConfig, Essential, Vigilance, and SkyblockAPI are not used.
- Never commit Modrinth credentials. The `release` GitHub environment owns the
  `MODRINTH_TOKEN` secret and `MODRINTH_PROJECT_ID` variable.

## Tooltip behavior

- Activate only in an `AbstractContainerScreen` while a non-empty slot is
  hovered and an oversized or off-screen item tooltip is being rendered.
- Preserve vanilla tooltip positioning, then apply only the clamped vertical
  offset needed for scrolling. Do not add horizontal scrolling or zoom.
- Start a new scrollable tooltip at its top, move by 10 pixels per wheel event,
  and consume the wheel event while that tooltip is active, including at the
  bounds.
- Reset when the hovered slot or item changes, the tooltip layout changes, or
  the screen is removed. When no scrollable tooltip is active, leave wheel input
  untouched.
- Keep the implementation independent. Do not copy source, assets, build logic,
  or bundled libraries from older GPL tooltip-scrolling projects.

## Release policy

- Releases are synchronized across all active Minecraft targets. Do not publish
  only a subset under a shared mod version.
- Only pushed `v*` tags invoke `.github/workflows/release.yml`. The tag must
  exactly equal `v<mod_version>`; normal pushes and pull requests do not build or
  publish anything in GitHub Actions.
- The release job runs on labels `self-hosted`, `Linux`, `X64`,
  `wicked-game-01`, and `scrollabletooltips`. Two ephemeral containers in the
  existing `/srv/runners` stack provide the repository-scoped pool.
- The workflow generates the target list with `./gradlew releaseManifest`,
  builds all targets, skips existing Modrinth versions, publishes each missing
  target, and creates or updates one GitHub Release with all production JARs.

## Working expectations

- Commit locally at natural checkpoints with clear conventional commit
  messages. Do not push unless the user explicitly asks.
- Before considering a change complete, run `./gradlew build` (or
  `.\gradlew.bat build` on Windows) and fix every failure. This must build every
  target in `gradle/targets.properties` and produce its JAR under
  `versions/*/build/libs`.
