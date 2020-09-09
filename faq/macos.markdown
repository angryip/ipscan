---
title: Running on Mac
layout: default
---

# Bypassing Signing & Notarization on Mac

Apple now requires all Mac apps to be signed and sent to them to make it runnable normally.
This requires paying $100 to Apple and making many additional build/release steps that only work on macOS itself.

**Until this is done/automated, the following can be used:**

A user who has admin/sudo rights on the system can bypass the restrictions.

After unzipping on Catalina (and versions before), hold Control while right-clicking or using two-finger click and select Open. 
You will get a different message from just right click and open, where you are prompted that the item is not registered with Apple, 
and asks if you are still willing to open the application. 

Select Open and it will remember your choice when you run it again.

## Dark Mode

Angry IP Scanner support macOS dark mode if it runs on Java VM, which is itself compiled using a recent macOS SDK.

This is because Apple decides whether to allow the mode based on the SDK version that was used by the developer.
The binary that starts the app is actually Java, so this is what macOS checks.

Please use a recent Java version.
