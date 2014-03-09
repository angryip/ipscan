---
title: NetBIOS
layout: default
---

## What is NetBIOS?

[NetBIOS](http://en.wikipedia.org/wiki/NetBIOS) is a protocol commonly used by Windows machines to communicate to each other, provide shares, etc. 
Read more on [Wikipedia](http://en.wikipedia.org/wiki/NetBIOS).

The list of available NetBIOS names on a Windows machine is returned by the _nbtstat_ command-line program (Windows only):

    nbtstat -A IP-ADDRESS

### The fetcher

NetBIOS Info fetcher retrieves the NetBIOS information provided by the Windows machines about themselves. Info is queried using the 137 UDP port.

The response has the following format:

    DOMAIN\\USER@COMPUTER [MAC]

Where:
* DOMAIN - Windows domain or workgroup
* USER - currently logged in user
* COMPUTER - Windows computer name (may be different from DNS name)
* MAC - MAC address of the network interface, as reported by the machine (may be different of the actual one accessed using ARP)

Some parts may be absent, depending on what info the actual machine provides about itself. Angry IP Scanner determines the types of the returned names by guessing (e.g. which is of them is username or domain).

Note: Angry IP Scanner 2.x used to provide separate columns for all of the tokens.

### Availability

NetBIOS info cannot be retrieved from the machines that have _firewall_ enabled (which are most modern installations of Windows starting from WinXP SP2).

Moreover, Windows XP machines usually don't provide the _username_ information at all by default, even if the firewall is disabled. In order to see the _usernames_ from Angry IP Scanner, the Messenger Service must be enabled on scanned machines.

See [this Microsoft's article](http://www.microsoft.com/windowsxp/using/security/learnmore/stopspam.mspx) for more information. Do the opposite in order to enable the service.
