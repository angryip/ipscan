---
title: TTL
layout: default
---

## Meaning of TTL

TTL means Time To Live.

This is the value present in every IP (Internet Protocol) packet's header (TCP, UDP, ICMP - all have it). TTL field's size is one byte, so its value is 0-255.

Originally, the value of TTL meant for how long the packet is supposed to be traveling in the network (in seconds) in order to prevent 'lost' or unroutable packets traveling in the network forever.

### How it works

Every router that forwards the packet is supposed to decrease its TTL value before sending it further either by 1 or the number of seconds it took the packet to reach it from the previous node, so as the networks are generally fast, TTL actually means the maximum number of nodes the packet is allowed to travel.

Different platforms set initial TTL value to different values, however the most common ones are 64, 128, and 255. Windows machines usually set it to 128, Linux ones to 64.

### TTL column in scanning results

Angry IP Scanner shows the TTL value of received ping packets. From its value you can have the idea of 'how far' the scanned host is from you, in number of routers/nodes.

For example, if TTL column shows 119, then it means that most probably:
* Initial value was 128
* Scanned host is a Windows box
* The host is 9 routers away from you

Note: not all pinging methods are capable of displaying the TTL value, see [Pinging](pinging.html).
