#!/bin/bash
# This scripts downloads and optimizes Wireshark's MAC vendor database

curl 'https://code.wireshark.org/review/gitweb?p=wireshark.git;a=blob_plain;f=manuf;hb=HEAD' |\
grep -P '^[0-9A-F:]{8}\t' | awk '{gsub(":","",$1); print $1 $2}' \
> resources/mac-vendors.txt

wc -l resources/mac-vendors.txt
