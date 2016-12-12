---
title: Plugins
layout: default
redirect_from: /w/FAQ%3A_Plugins/
---

Plugins
=======

Plugins for Angry IP Scanner are distributed as standard .jar files and are also written in Java to be cross-platform.

Please let me know if you have an interesting plugin you would like to share here.

Angry IP Scanner looks for plugin .jar files on start in:
- The same directory where ipscan binary is located (.jar or .exe)
- User-specific $HOME/.ipscan directory

For more info on how plugin loading works, see [PluginLoader.java](https://github.com/angryziber/ipscan/blob/master/src/net/azib/ipscan/core/PluginLoader.java).

Writing plugins
---------------

The plugin's jar file must have a META-INF/MANIFEST.MF entry 'IPScan-Plugins' that lists full Java class names of all plugins
contained in the jar file.

A plugin is an implementation of one of the following interfaces:

- Fetcher - corresponds to a column in the result list, fetches data from scanned IP addresses
- Pinger - these guys detect whether an IP is dead or alive
- Exporter - used for exporting the scanning results
- Feeder - these guys generate IP address sequence to scan (feed the scanner)

Plugin classes should generally have a non-arg constructor. 

Look at [the source code](https://github.com/angryziber/ipscan) for examples of how the core plugins are implemented.
