
;--------------------------------
; General Attributes

Name "Timeout Test"
OutFile "to.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"



;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; additional headers. Sample php returns raw headers
    inetc::get /receivetimeout 12 "http://localhost/to.php" "$EXEDIR\to.html"
    Pop $0

    MessageBox MB_OK "Download Status: $0"

SectionEnd


