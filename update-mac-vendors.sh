#!/bin/bash
# This scripts downloads and optimizes IEEE MAC vendor list

curl 'http://standards-oui.ieee.org/oui.txt' |\
#cat oui.txt |\
fgrep '(base 16)' | sed -r '
	s/\r//g; s/     \(base 16\)\t\t//;
	s/,? ?(Inc|INC|inc)\.?$//;
	s/,? ?(Ltd|LTD|ltd|Limited|LIMITED|GmbH|GMBH|LLC|A\/S|AB|AS|SAS|AG|KG|plc|PLC|SRL|srl|OY|Oy)\.?$//;
	s/,? ?(Co|CO|CORP|Corp|co|Corporation|CORPORATION|COMPANY)\.?$//;
	s/\(.+\)//;
	s/ ?(Electronics?|ELECTRONICS?|Technology|technology|TECHNOLOGY|Technologies|TECHNOLOGIES|Telecommunication|TELECOMMUNICATION|COMMUNICATION|Communication|Communications|Corporation|CORPORATION|corporation|Systems|Solutions) ?//g
	s/ Registration Authority//
' | sort \
> resources/mac-vendors.txt

wc -l resources/mac-vendors.txt
