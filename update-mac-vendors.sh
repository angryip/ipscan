#!/bin/bash
# This scripts downloads and optimizes IEEE MAC vendor list

SED_APP='sed'
if [ "$(uname)" = "Darwin" ]; then
  # Mac users: bundled sed doesn't have -r
  brew install gnu-sed
  SED_APP='gsed'
fi

curl 'http://standards-oui.ieee.org/oui/oui.txt' |\
fgrep '(base 16)' | $SED_APP -r '
	s/\r//g; s/     \(base 16\)\t\t//
	s/,? ?(Inc)\.?$//I
	s/(,|, | )(Ltd|CO,\.LTD|Limited|GmbH|LLC|A\/S|AB|AS|SAS|AG|KG|PLC|SRL|OY|Oy|BV|Nederland BV|SAN VE TIC)\.?$//Ig
	s/(,|, | )(Co|Corp|Corporation|Company|Incorporated)\.?$//Ig
	s/\(.+\)//
	s/ (Electronics?|Technology|Technologies|Telecommunication|Communications?|Corporation|Systems|Solutions|International|Industry|Industries|Networks?|Holdings?) ?//Ig
	s/ (Registration Authority| MobilityCommunication)//
	s/SAMSUNG ELECTRO[- ]MECHANICS/Samsung/
' | cut -c -42 | sort \
> resources/mac-vendors.txt

wc -l resources/mac-vendors.txt
