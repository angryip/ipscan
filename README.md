This is the source code of Angry IP Scanner, licensed with GPL v2.

[Official site - AngryIP.org](http://angryip.org/)

The code is written mostly in Java.
IntelliJ IDEA is recommended for coding, but Eclipse would do as well.

After loading the project in IDEA, make sure you select the appropriate for your platform lib_xxx library in module dependencies.

Building
========

JDK 1.6+ as well as Ant are required for building.

Using these tools you can build on any platform. The binaries are in the form of
`.jar` files and can be run with `java -jar <jar-file>`. Deb and rpm packages can
only be built on Linux. Building of Windows exe can be done on Linux as well.

On Ubuntu install the following packages:
```
sudo apt-get install openjdk-8-jdk ant rpm wine
```
Note: *wine* is needed for building of Windows installer.

Install JDK and Ant on other platforms as you usually do it.

Then use Ant for building a package for your desired platform:
just type `ant` or `make` in the project dir for the list of available targets.

`ant current` would build the app for your current OS.

The resulting binaries will be put into the `dist` directory.
Run jar files with `java -jar <jar-file>`.
