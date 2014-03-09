---
title: Pinging
layout: default
---

{{ page.title }}
================

Angry IP Scanner implements several different methods of detecting _alive_ hosts (pinging).

As a rule, if hosts don't respond to pings, they are considered _dead_ and therefore not scanned further. This behavior can be changed in the _Preferences_ dialog, _Scanning_ tab. In the same place you can also select the pinging method.

### ICMP Echo pinging

This is the same method used by the _ping_ program. 

However, as it involves sending of ICMP (Internet Control Message Protocol) packets, it requires administrative (or root) privileges. If Angry IP Scanner runs without these privileges, this method can't be used.

Angry IP Scanner implements this using the Raw Sockets. However, starting with Windows XP SP2, Microsoft has removed Raw Socket support from consumer versions of Windows (Server editions still have them), so this method will not work on Windows anymore.

### ICMP.DLL pinging

This is Windows-only pinging method to compensate for absence of Raw Sockets (see above).

Angry IP Scanner can now use the previously undocumented ICMP.DLL library to send ICMP Echo packets from Windows machines. This should provide similar performance to pure ICMP Echo pinging on other platforms.

### UDP packet pinging

This pinging method is preferred when you don't have administrative privileges. Angry IP Scanner will detect the absence of privileges and use this method automatically.

The method works by sending out UDP packets to some UDP port very unlikely to be open. If the port is closed, the host must send the ICMP packet back informing of the fact. If the packet is reseived, Angry IP Scanner knows that the host is actually alive and records the roundtrip time. No response can mean that the UDP port is open (very unlikely) or the host is dead.

Note:
* Some network devices (such as home routers) don't implement UDP protocol at all and therefore appear as dead.
* For computers, this method can actually sometimes work even better than ICMP Echo, because some administrators/firewalls tend to block ICMP Echo packets, but allow UDP packets to travel freely.

### TCP port probe

This method tries to connect to some TCP port that is unlikely to be filtered (e.g. 80). If either the connection can be established or TCP RST packet is received (meaning that port is closed), Angry IP Scanner knows that host actually responds and can be considered as alive. If the port is filtered (no response to connection attempt), then the host is considered to be dead.
