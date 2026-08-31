# Angry IP Scanner

## Project overview

Angry IP Scanner (`ipscan`) is a cross-platform network scanner written in Java.
GUI uses SWT (Eclipse) for native widgets per platform. Licensed GPLv2.

- Entry point: `net.azib.ipscan.Main` (`src/net/azib/ipscan/Main.java`)
- Package root: `net.azib.ipscan` under `src/`
- Tests mirror the source tree under `test/`

## Build system

Gradle 9.5 wrapper (`./gradlew`). Java 21 source/target. CI uses Java 25 (Liberica).

**Source layout is non-standard** — no `src/main/java` convention:
- Main sources: `src/` (configured as `sourceSets.main.java.srcDirs`)
- Test sources: `test/` (configured as `sourceSets.test.java.srcDir`)
- Resources: `config/`, `src/`, `resources/` all merged into main resources

### Key commands

```bash
./gradlew current          # build jar for current OS/arch
./gradlew test             # run tests
./gradlew info             # list all build targets
./gradlew clean            # clean build dir
```

Platform-specific targets: `linux64`, `any` (platform-neutral), `win64`, `macX86`, `macArm64`, `mac`, `linux`.

Output goes to `build/libs/`. Run with `java -jar <jar-file>`.

### Build dependencies (Ubuntu)

```
sudo apt install openjdk-21-jdk rpm fakeroot
```

## Code style

- 4 space indents

## Testing

JUnit 4 + Mockito 2.x. Test classes use `@Test`, `@Before` annotations.

```bash
./gradlew test                           # all tests
./gradlew test --tests "net.azib.ipscan.core.net.*"  # single package
./gradlew test --tests "*PingFetcherTest"              # single class
```

**Mac caveat**: GUI-touching tests are excluded on macOS (Cocoa thread restrictions). Only `net.azib.ipscan.core.net.*` runs. CI uses `xvfb-run -a` on Linux for headless SWT tests.

**Pinger tests** require network access — they ping `127.0.0.1` for alive tests and `192.168.99.253` for dead tests.

**Test DI pattern**: Tests that need components use `new ComponentRegistry().init(false)` — the `false` skips GUI registration, giving a headless injector.

## Architecture

### Dependency injection

Custom lightweight DI in `net.azib.ipscan.di.Injector` — constructor-based, picks the constructor with the most parameters. No annotations needed. Wiring is in:
- `ComponentRegistry` — fetchers, exporters, pingers
- `ConfigModule` — config singletons
- `GUIRegistry` — SWT widgets and GUI components

`ComponentRegistry.init(boolean withGUI)` is the bootstrap. `false` = headless (used in tests).

### Platform-specific code

`Platform.java` has boolean constants (`MAC_OS`, `LINUX`, `WINDOWS`, `CRIPPLED_WINDOWS`).
Platform-specific fetchers: `WinMACFetcher`, `LinuxMACFetcher`, `UnixMACFetcher` — selected at DI time via reflection in `ComponentRegistry`.

Windows-specific pingers (`WindowsPinger`, `WinIpHlp`) use JNA for native calls.

### Key packages

| Package | Purpose |
|---------|---------|
| `config/` | Config, Labels (i18n), Platform detection, ComponentRegistry (DI wiring) |
| `core/` | Scanner engine, scanning state machine, results |
| `core/net/` | Pingers (ICMP, TCP, UDP, ARP, Windows-specific) |
| `di/` | Custom DI container |
| `exporters/` | Result export formats (CSV, TXT, XML, SQL, IPList) |
| `feeders/` | IP range input sources (range, random, file, rescan) |
| `fetchers/` | Data fetchers (ping, hostname, ports, MAC, web detect, etc.) |
| `gui/` | SWT GUI: MainWindow, dialogs, menus, feeder/fetcher panels |
| `util/` | Utilities |

### Plugin system

Plugins implement `net.azib.ipscan.core.Plugin`. Loaded from:
1. `-Dipscan.plugins=com.example.MyPlugin` system property
2. JARs next to the app binary with `IPScan-Plugin` manifest attribute
3. JARs in `$HOME/.ipscan/`

Plugin JARs can bundle their own `messages.properties` for i18n.

## I18n

Labels in `resources/messages.properties` (English fallback) + `messages_<lang>.properties` per locale.
Access via `Labels.getLabel("key")`. Supported languages listed in `Labels.LANGUAGES`.

When adding UI strings, add the key to `messages.properties` first.

## Versioning

Version is derived from `git describe --tags`. CI creates draft GitHub releases on tag push.

## ProGuard

JARs are minified with ProGuard after packaging. Config in `ext/swt.pro` and `ext/jna.pro`. The minified jar replaces the original.

## Packaging

- **Linux**: `.deb` (via `fakeroot dpkg-deb`) and `.rpm` (via `rpmbuild`), only on Ubuntu
- **Windows**: `.msi` installer via `jpackage` (bundled JRE, Start Menu shortcut, desktop shortcut)
- **Mac**: `.dmg` via `jpackage` (bundled JRE, `.app` bundle, optional Apple notarization via `APPLE_USER`/`APPLE_PASSWORD` env vars)

## MAC vendor database

`resources/mac-vendors.txt` is generated from IEEE OUI data. Update with:
```bash
./update-mac-vendors.sh
```
Requires `gnu-sed` on macOS (`brew install gnu-sed`).

## Preferences

User preferences stored via `java.util.prefs.Preferences` under node `ipscan`. Not in project files.

## Gradle quirks

- `gradle.properties` adds `--add-opens java.base/java.net=ALL-UNNAMED` (required for JNA/reflection)
- SWT dependency is excluded transitively (`all*.exclude module: 'org.eclipse.swt'`) — platform-specific SWT is selected via Gradle configurations
- Platform configurations (`linux64`, `win64`, `macX86`, `macArm64`, `jna`) control which native libs get bundled
