#!/bin/bash
# This scripts downloads and optimizes IEEE MAC vendor list
#
# FILE: update-mac-vendors.sh
# Mac users: Install Gnu-Sed via brew install gnu-sed then change sed into gsed (why? Option -r did not exist on OSX)(cheers Zwilla)

SED_APP='sed'
if [ "$(uname)" = "Darwin" ]; then
  brew install gnu-sed;
  SED_APP='gsed';
fi

curl 'http://standards-oui.ieee.org/oui.txt' |\
#cat oui.txt |\
fgrep '(base 16)' | $SED_APP -r '
	s/\r//g; s/     \(base 16\)\t\t//
	s/,? ?(Inc)\.?$//I
	s/(,|, | )(Ltd|Limited|GmbH|LLC|A\/S|AB|AS|SAS|AG|KG|PLC|SRL|OY|Oy|BV|Nederland BV|SAN VE TIC)\.?$//Ig
	s/(,|, | )(Co|Corp|Corporation|Company|Incorporated)\.?$//Ig
	s/\(.+\)//
	s/ (Electronics?|Technology|Technologies|Telecommunication|Communications?|Corporation|Systems|Solutions|International|Industry|Industries|Networks?|Holdings?) ?//Ig
	s/ (Registration Authority| MobilityCommunication)//
	s/SAMSUNG ELECTRO[- ]MECHANICS/Samsung/
' | cut -c -42 | sort \
> resources/mac-vendors.txt

wc -l resources/mac-vendors.txt
