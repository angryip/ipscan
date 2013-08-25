
;--------------------------------
; General Attributes

Name "Inetc plug-in Test"
OutFile "inetc.exe"
;SilentInstall silent


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"

;SetFont 14

;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

    SetDetailsView hide

; two files download, popup mode
    inetc::get /caption "2003-2004 reports" /popup "" "http://ineum.narod.ru/spr_2003.htm" "$EXEDIR\spr3.htm" "http://ineum.narod.ru/spr_2004.htm" "$EXEDIR\spr4.htm" /end
    Pop $0 # return value = exit code, "OK" means OK

; single file, NSISdl-style embedded progress bar with specific cancel button text
    inetc::get /caption "2005 report" /canceltext "interrupt!" "http://ineum.narod.ru/spr_2005.htm" "$EXEDIR\spr5.htm" /end
    Pop $1 # return value = exit code, "OK" means OK

; banner with 2 text lines and disabled Cancel button
    inetc::get /caption "2006 report" /banner "Banner mode with /nocancel option setten$\nSecond Line" /nocancel "http://ineum.narod.ru/spr_2006.htm"  "$EXEDIR\spr6.htm" /end
    Pop $2 # return value = exit code, "OK" means OK

    MessageBox MB_OK "Download Status: $0, $1, $2"
SectionEnd


;--------------------------------
;Installer Functions

Function .onInit

; plug-in auto-recognizes 'no parent dlg' in onInit and works accordingly
;    inetc::head /RESUME "Network error. Retry?" "http://ineum.narod.ru/spr_2003.htm" "$EXEDIR\spr3.txt"
;    Pop $4

FunctionEnd