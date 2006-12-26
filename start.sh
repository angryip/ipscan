#!/bin/bash

echo "Executing built Angry IP Scanner under root"
su -c 'java -Djava.library.path=../swt/lib:ext/rocksaw/lib -jar dist/ipscan.jar'
