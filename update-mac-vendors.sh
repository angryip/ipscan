#!/bin/bash
# This scripts downloads and optimizes Wireshark's MAC vendor database

curl http://anonsvn.wireshark.org/wireshark/trunk/manuf |\
grep -P '^[0-9A-F:]{8}\t' | awk '{print $1,$2}' \
> resources/mac-vendors.txt

wc -l resources/mac-vendors.txt`
