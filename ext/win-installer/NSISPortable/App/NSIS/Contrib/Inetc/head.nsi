
;--------------------------------
; General Attributes

Name "Inetc Head Test"
OutFile "head.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

    DetailPrint "New version check out (internet connection)"
    inetc::head /silent "http://ineum.narod.ru/spr_2006.htm" "$EXEDIR\head.txt"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd


