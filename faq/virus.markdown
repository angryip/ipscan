---
title: Is the program infected with a virus?
layout: default
---

{{ page.title }}
================

**No.** There are no trojans or viruses in Angry IP Scanner. It is open source (GPL v2), and its
[source code](https://github.com/angryip/ipscan) is publicly available for anyone to review.
Release binaries are built from this source by [GitHub Actions](https://github.com/angryip/ipscan/actions).

Why are antivirus programs flagging it?
---------------------------------------

Angry IP Scanner is a network scanning tool. The same features that make it useful to system
administrators and security professionals can also be abused by attackers, so many antivirus
products classify network scanners as a 'potentially unwanted program' (PUP/PUA), 'riskware' or
'hacktool' – even though the file contains no malware.

Many detections are **heuristic** rather than based on a known malware signature. Engines that use
machine learning or behavioral rules guess that a file *may* be dangerous based on its code,
packaging or capabilities. That is why legitimate tools are regularly flagged, often with generic
names such as `Trojan:Script/Wacatac.B!ml`, `GenericRXWS`, `TrojanDropper.Win32.Launch4j`,
`Riskware` or `Hacktool`. For example, the `!ml` suffix in a Microsoft Defender detection name means
it came from a machine-learning model, not from a signature of a specific virus.

Some engines also share signatures or feed on each other's results, so a single false positive can
appear under several names at once. Antivirus software may then quarantine or even delete the
downloaded file during scanning.

Note that the Windows installer is not signed with a commercial code-signing certificate, so
Windows SmartScreen may also show an 'Unknown publisher' warning. This is separate from virus
detection.

Check the file yourself with VirusTotal
---------------------------------------

[VirusTotal](https://www.virustotal.com/) runs a file through dozens of antivirus engines at once
and shows each engine's verdict. It is a convenient way to see whether a detection is a real threat
or a false positive:

1. Download Angry IP Scanner only from [this site](/download/) or the
   [official GitHub releases](https://github.com/angryip/ipscan/releases).
2. Upload the downloaded file on the
   [VirusTotal upload page](https://www.virustotal.com/gui/home/upload). Note that files submitted
   to VirusTotal are shared with security vendors and may become publicly available.
3. Open the report and look at the detection names and the number of engines that flagged the file.
   A few generic or heuristic detections out of dozens of engines – especially labels containing
   'generic', 'heur', 'PUA', 'riskware' or '!ml' – are typical of a false positive. A detection with
   a specific malware family name by many major engines would be a reason for concern.
4. If a vendor fixes its detection, use the *Reanalyse* button on VirusTotal to get updated results.

VirusTotal only aggregates results and cannot remove a detection itself. More details are in the
[VirusTotal false positive documentation](https://docs.virustotal.com/docs/false-positive).

Reporting a false positive
--------------------------

If your antivirus blocks Angry IP Scanner, you can help by reporting the false positive to the
vendor whose engine is flagging it:

* [Submit a file to Microsoft](https://www.microsoft.com/en-us/wdsi/filesubmission) if Windows
  Defender flags it.
* For other vendors, use the contact details in the
  [VirusTotal false positive contacts](https://docs.virustotal.com/docs/false-positive-contacts).
  Include the detection name and a link to the VirusTotal report.

Until the vendor updates its signatures, you can restore the file from quarantine and add an
exclusion for it – but only if you downloaded it from an official source and verified it with
VirusTotal.

Older versions
--------------

The old 2.x `ipscan.exe` was flagged by antivirus software much more often. Please use the current
3.x version, which is actively maintained and built from the published source code.
