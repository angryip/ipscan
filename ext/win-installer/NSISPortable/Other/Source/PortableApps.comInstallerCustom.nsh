!macro CustomCodePreInstall
	${If} ${FileExists} "$INSTDIR\App\AppInfo\AppInfo.ini"
		ReadINIStr $0 "$INSTDIR\App\AppInfo\appinfo.ini" "Version" "PackageVersion"
		${VersionCompare} "$0" "2.46.2.2" $1
		${If} $1 == "2"  ;$0 is older than
			Delete "$INSTDIR\Data\settings\nsisconf.nsh"
			Delete "$INSTDIR\App\NSIS\nsisconf.nsh"
			CopyFiles "$INSTDIR\App\DefaultData\settings\nsisconf.nsh" "$INSTDIR\Data\settings\"
		${EndIf}
	${EndIf}
!macroend