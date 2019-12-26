# Angry IP Scanner

This is the source code of Angry IP Scanner, licensed with GPL v2. [Official site](https://angryip.org/)

The code is written mostly in Java (currently, source level 1.8).
IntelliJ IDEA is recommended for coding (Community Edition is fine).

Projects supports building for Linux, Windows and Mac OS X.

## Building [![Actions Status](https://github.com/angryip/ipscan/workflows/CI/badge.svg)](https://github.com/angryip/ipscan/actions)

Use Gradle for building a package for your desired platform:

`./gradlew` or `make` in the project dir for the list of available targets.

`./gradlew current` would build the app for your current platform

The resulting binaries will be put into the `build/libs` directory.
Run jar files with `java -jar <jar-file>`.

Deb and rpm packages can only be built on Linux (tested on Ubuntu). 
Building of Windows installer can be done on Linux as well.

### Dependencies

On Ubuntu install the following packages:
```
sudo apt-get install openjdk-11-jdk rpm fakeroot wine-stable
```
Note: *wine* is needed for building of Windows installer.

Install OpenJDK on other platforms as you usually do it.
