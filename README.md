# Angry IP Scanner

This is the source code of Angry IP Scanner, licensed with GPL v2. [Official site](https://angryip.org/)

The code is written mostly in Java (currently, source level 11).
[SWT library from Eclipse project](https://eclipse.org/swt/) is used for GUI that provides native components for each supported platform.

The project runs on Linux, Windows and macOS. 

## Helping / Contributing

As there are millions of different networks, configurations and devices, please help with submitting a **Pull Request** if something
doesn't work as you expect (especially macOS users). Any problem is easy to fix if you have an environment to reproduce it 😀

For that, download [Intellij IDEA community edition](https://www.jetbrains.com/idea/download/) and open the cloned project.
Then, you can run Angry IP Scanner in Debug mode and put a breakpoint into the [desired Fetcher class](src/net/azib/ipscan/fetchers).

## Building [![Actions Status](https://github.com/angryip/ipscan/workflows/CI/badge.svg)](https://github.com/angryip/ipscan/actions)

Use Gradle for building a package for your desired platform:

`./gradlew` or `make` in the project dir for the list of available targets.

`./gradlew current` would build the app for your current platform

The resulting binaries will be put into the `build/libs` directory.
Run jar files with `java -jar <jar-file>`.

Deb and rpm packages can be built only on Linux (tested on Ubuntu). 
Windows installer can be built on Windows only.

`./gradlew all` will build packages for all OS (tested on Ubuntu only, see dependencies below).

### Dependencies

On Ubuntu install the following packages:
```
sudo apt install openjdk-21-jdk rpm fakeroot
```

Install OpenJDK on other platforms as you usually do it.
