---
title: Hostnames
layout: default
---

{{ page.title }}
================

Angry IP Scanners displays hostnames returned by your _DNS_ (name) server, by doing a reverse lookup. The server is provided the IP address and returns the hostname if it knows it.

If some computer knows its own name, it doesn't mean that it has provided it the the network's DNS server. In other words, the name of the host as it knows it itself (the _local name_) and the name attached to the IP address (the _global name_) as it known by the _DNS_ server may not always match.

Very often, DNS queries will return some generic names, e.g. ''dhcp-12-13.superisp.com'', especially in ISP networks. 

The names match in either of these cases:
* Your computer has sent the name to the DHCP server from which it obtained the IP address and DHCP server has provided it to the local DNS server
* You have a static IP address and your local hostname is configured according to the rules that match global naming convention in your network
* Your host's TCP stack returns the local name if the local IP address is queried, not asking the global name
