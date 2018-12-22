# Angry IP Scanner

This is the source code of Angry IP Scanner, licensed with GPL v2. [Official site](https://angryip.org/)

The code is written mostly in Java (currently, source level 1.7).
IntelliJ IDEA is recommended for coding (Community Edition is fine).

**Important:** after loading the project in IDEA, make sure you select the appropriate for your platform **lib_xxx library in module dependencies**
*(File -> Project Structure -> Modules -> ipscan -> Dependencies - move the correct lib_xxx to the top)*.

## Building [![Build Status](https://travis-ci.org/angryip/ipscan.svg?branch=master)](https://travis-ci.org/angryip/ipscan)

Using these tools you can build on any platform. The binaries are in the form of
`.jar` files and can be run with `java -jar <jar-file>`. Deb and rpm packages can
only be built on Linux. Building of Windows installer can be done on Linux as well.

On Ubuntu install the following packages:
```
sudo apt-get install openjdk-8-jdk rpm fakeroot wine
```
Note: *wine* is needed for building of Windows installer.

Install JDK on other platforms as you usually do it.

Then use Gradle for building a package for your desired platform:
just type `./gradlew` or `make` in the project dir for the list of available targets.

`./gradlew current` would build the app for your current OS.

The resulting binaries will be put into the `build/lib` directory.
Run jar files with `java -jar <jar-file>`.
