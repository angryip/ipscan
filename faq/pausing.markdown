---
title: Pausing
layout: default
---

{{ page.title }}
================

Angry IP Scanner uses multiple threads for scanning. Each host/address is scanned in its own thread.

The maximum number of threads running in parallel is limited by the _Maximum number of threads_ preference (see Tools->Preferences menu).

The reason for that is not to consume all OS resources and keep the system responsive. Especially Windows can start behaving badly if there are too many active threads running in the system.

When Angry IP Scanner reaches the maximum number of threads, it pauses until some threads are finished, and then continues scanning using the available threads. If your network doesn't respond fast enough, then these pauses can become noticeable to the user. If you feel that the default number of threads of _64_ is too low for your system, you may increase it in the Preferences dialog resulting in faster overall scanning speed.
