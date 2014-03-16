---
title: Crippled Windows
layout: default
---

Why is scanning slow on Windows?
================================

Sometimes Angry IP Scanner can not detect _open_ ports and will consider them as _filtered_.

### Timeouts

This problem can always be 'fixed' by changing some scanning preferences, like timeouts and number of scanning threads.

The cause of the problem is that Angry IP Scanner doesn't wait for responses from the hosts to arrive long enough:
if the network is congested with packets or hosts just reply slowly for any reason, the roundrip time of TCP handshake
can exceed the configured port timeout (see _Preferences_ dialog, _Ports_ tab). The default waiting time is 3 seconds,
but it is decreased automatically for each host if ping packets went through quickly enough and timeout adaptation is
enabled (see the corresponding check box).

### Rate limiting on Windows

However, especially on _Windows_ platforms, the problem can also be caused by TCP connection rate limiting.

Starting from Windows XP SP2 and on through Vista SP1, Microsoft has crippled down consumer versions of Windows,
in order to limit the possibilities of insecure Windows machines to act as hosts for Internet attacks
executed by worms and trojans. Unfortunately, these changes also made non-server editions of Windows a lot less capable
for doing network administrations tasks, such as scanning.

Windows implementation of _TCP connection attempt rate limiting_ limits the number of simultaneous connection attempts
to 10. You can check if you reach this limit by examining the _Event Log_ after scanning: look for the _Event ID 4226_,
which corresponds to this problem.

For scanning purposes, that means you can have at most this number of scanning threads if you want to get reliable results.
The number of scanning threads affects the maximum number of hosts scanned simultaneously and therefore the maximum number
of connections made at each moment.

_This limitation has been removed in Vista SP2 and later releases (Server 2008 SP2 and Windows 7)._

#### Windows XP

Read [more information about the problem](http://www.speedguide.net/read_articles.php?id=1497), which describes how to
patch manually. The automatic patcher is available from [this site](http://www.lvllord.de/).

#### Windows Vista

See [information about patching Vista](http://torrentfreak.com/optimize-vista-for-bittorrent-emule-p2ptv/).
The site is about increasing the performance of BitTorrent downloads, but the same patch will dramatically
improve scanning speed as well - just don't forget to increase the number of threads in Angry IP Scanner's
preferences after applying the patch.

#### Windows 7 and beyond

This limitation is [removed in Vista SP2](http://www.mydigitallife.info/half-open-outbound-tcp-connections-limit-removed-in-windows-7-and-vista-sp2-no-patch-required/),
so no patching is required.
