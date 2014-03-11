---
title: About
layout: default
priority: 1.0
---

About
=====

Angry IP scanner is a very fast IP address and port scanner.

It can scan IP addresses in any range as well as any their ports. It is cross-platform and lightweight. Not requiring any installations, it can be freely copied and used anywhere. 

Angry IP scanner simply pings each IP address to check if it's alive, then optionally it is resolving its hostname, determines the MAC address, scans ports, etc. The amount of gathered data about each host can be extended with plugins.

It also has additional features, like NetBIOS information (computer name, workgroup name, and currently logged in Windows user), favorite IP address ranges, web server detection, customizable openers, etc.

Scanning results can be saved to CSV, TXT, XML or IP-Port list files. With help of plugins, Angry IP Scanner can gather any information about scanned IPs. Anybody who can write Java code is able to write plugins and extend functionality of Angry IP Scanner.

In order to increase scanning speed, it uses multithreaded approach: a separate scanning thread is created for each scanned IP address. The full source code is available, see the [download](/download/) page.

Presentations
-------------

- [Writing of cross-platform desktops apps](http://prezi.com/k1i5lmcdl8cy/angry-ip-scanner/) - presentation from Jokerconf.
- [Is scanning of computer networks dangerous?](Baltic_DB&IS.pdf) - presentation from the [Baltic DB & IS](http://www.cs.ioc.ee/balt2008/) conference.

Documentation
-------------

Read the [longer essay](/documentation/) with theory of network scanning and the reasoning behind the project.

License
-------

Angry IP Scanner is free and open-source software, so use it at your own risk. 
The license is [GPLv2](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)

Author
------

The program is written and maintained by Anton Keks ([tech blog](http://blog.azib.net/), [photography](http://photos.azib.net/)), who is a software craftsman and co-founder of [Codeborne](http://codeborne.com/), an agile software development company.

Notice
------

This program is mostly useful for network administrators to monitor and manage their networks.

For more information about IP and port scanning in general, you can see [the corresponding article](http://en.wikipedia.org/wiki/Port_scanner) on Wikipedia. 

Please note that while theoretically Angry IP Scanner can be used by crackers, in fact it was not intended for doing so, thus the lack of stealth scanning methods. Please do not consider Angry IP Scanner as a 'hacktool' or something similar.

