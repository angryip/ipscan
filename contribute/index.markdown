---
title: Contribute
layout: default
redirect_from: /w/Development/
---

Contribute
==========

The easiest way to extend the functionality of Angry IP Scanner is to [write a plugin](plugins.html).

Angry IP Scanner's source code is hosted on [Github](https://github.com/angryip/ipscan).
Forking and pull-requests are very welcome! If you want to get an idea of what to do, check the [bug reports](https://sourceforge.net/p/ipscan/bugs/).

In order to get the source code, the following command must be run (make sure you have git installed):

    git clone git://github.com/angryip/ipscan.git

This command will fetch the current source code of the program with full history into local directory named 'ipscan'.
Or just [browse the code repository on Github](https://github.com/angryip/ipscan).

If the source of particular release is required, then you can later switch to particular tag using:

    git checkout tag-name

where tag-name is the released version number (eg {{ site.version }}), for full list of available tags use:

    git tag

Building
--------

In order to build the binaries, you need only to run './gradlew' in the 'ipscan' directory.
Gradle will use the standard build.gradle script there and compile, test, and package the program for all platforms.
Note: some packaging features were tested only on Linux.

The source code tree also includes the preconfigured Intellij IDEA project for convenience.
Then, it can be run using the net.azib.ipscan.Main class.

Translations
------------

If you know some language other than English well, then please help translating Angry IP Scanner into that language.

For that, you need to take the latest [resources/messages.properties](https://github.com/angryip/ipscan/blob/master/resources/messages.properties) file,
copy it with the ISO 2-letter language suffix (eg messages_et.properties), translate all the messages and make a pull request or attach to an issue.

Note:
* ISO language codes sometimes are different than country codes
* All messages files and other sources are in UTF-8 encoding that supports all languages of the World

To test your translations, run Angry IP Scanner from the command-line after putting the translated file is in the same directory as the original jar or exe:

    java -jar ipscan*.jar

Source structure
----------------

### Directories

* src - the main subdirectory of the project tree, which contains the Java package structure of the source (net.azib.ipscan and its sub packages). All the source code, needed for running the program is stored there.
* test - contains exactly the same Java package structure as src does, but is intended for unit tests and other classes, which are not needed for running Angry IP Scanner itself. Each test case Java class should have the same name as the original class under test, but with the Test suffix, for example, Feeder and FeederTest.
* lib - bundled versions of SWT library. Note that versions may be slightly different for each supported platform.
* ext - for external tools. The contents of each library's directory will depend on the original structure of that library, however, not all the original files may be included.
* resources - textual and graphical resources to be packaged with the program, including translations.

### Java packages

Angry IP Scanner source code is in the net.azib.ipscan Java package.

* [net.azib.ipscan.config](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/config) - classes, related to configuration
* [net.azib.ipscan.core](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/core) - core scanner classes
* [net.azib.ipscan.core.state](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/core/state) - scanner's state machine implementation
* [net.azib.ipscan.feeders](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/feeders) - all [feeder](plugins.html) implementations
* [net.azib.ipscan.fetchers](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/fetchers) - all [fetcher](plugins.html) implementations
* [net.azib.ipscan.exporters](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/exporters) - all [exporter](plugins.html) implementations
* [net.azib.ipscan.gui](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/gui) - for GUI packages and classes (which are separate from the core functionality)
* [net.azib.ipscan.gui.actions](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/gui/actions) - most GUI listeners, such as menu item and button handlers

### Dependency injection

Angry IP Scanner uses [dependency injection pattern](http://en.wikipedia.org/wiki/Dependency_injection) in its design.
Dependent classes are created automatically using the constructor injection by the [Injector](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/di/Injector.java) class.

Injector creates instances recursively, so classes themselves don't have to worry where their dependencies come from. 
This eases both development and unit testing of the code. No frameworks nor annotations are used for this anymore.

Classes that cannot be auto-created or found are registered in the [ComponentRegistry](https://github.com/angryip/ipscan/blob/master/src/net/azib/ipscan/core/ComponentRegistry.java) class,
including implementations of Feeders, Fetchers, and Exporters.
