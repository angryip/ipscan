#!/bin/bash

# Note: on Mac, the -XstartOnFirstThread JVM options is mandatory!

echo "Executing built Angry IP Scanner under root"
su -c 'java -Djava.library.path=ext/rocksaw/lib -jar dist/ipscan.jar'
