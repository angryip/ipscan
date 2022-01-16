# Angry IP Scanner

This is the source code of Angry IP Scanner, licensed with GPL v2. [Official site](https://angryip.org/)

The code is written mostly in Java (currently, source level 11).
IntelliJ IDEA is recommended for coding (Community Edition is fine): Import as Gradle project.

The project supports building for Linux, Windows and Mac OS.

## Building [![Actions Status](https://github.com/angryip/ipscan/workflows/CI/badge.svg)](https://github.com/angryip/ipscan/actions)

Use Gradle for building a package for your desired platform:

`./gradlew` or `make` in the project dir for the list of available targets.

`./gradlew current` would build the app for your current platform

The resulting binaries will be put into the `build/libs` directory.
Run jar files with `java -jar <jar-file>`.

Deb and rpm packages can only be built on Linux (tested on Ubuntu). 
Building of Windows installer can be done on Linux as well.

`./gradlew all` will build packages for all OS (tested on Ubuntu only, see dependencies below).

### Dependencies

On Ubuntu install the following packages:
```
sudo apt install openjdk-11-jdk rpm fakeroot wine-stable
```
Note: *wine* is needed for building of Windows installer.

Install OpenJDK on other platforms as you usually do it.

## Helping

If you have an obscure issue in your network that most likely the author will not be able to reproduce, you
can help with debugging of it.

For that, download [Intellij IDEA community edition](https://www.jetbrains.com/idea/download/) and open the cloned project.
Then, you can run Angry IP Scanner in Debug mode and put a breakpoint into the [desired Fetcher class](src/net/azib/ipscan/fetchers).

Pull requests are welcome!
