#!/bin/bash

# Note: on Mac, the -XstartOnFirstThread JVM options is mandatory, -Xdock:name="Angry IP Scanner" is optional!

echo "Executing built Angry IP Scanner under root"
su -c 'java -Djava.library.path=ext/rocksaw/lib -jar dist/ipscan.jar'
