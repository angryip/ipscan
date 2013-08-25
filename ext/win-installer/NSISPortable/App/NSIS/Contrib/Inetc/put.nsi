
;--------------------------------
; General Attributes

Name "Inetc Test"
OutFile "put.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; this is my LAN sample, use your own URL for tests. Login/pwd hidden from user. Sample put.php (for http request) included

    inetc::put "http://localhost/put.php" "$EXEDIR\test.jpg"
;    inetc::put /POPUP "ftp://localhost/" /CAPTION "my local ftp upload" "ftp://localhost/test.jpg" "$EXEDIR\test.jpg"
    Pop $0
    MessageBox MB_OK "Upload Status: $0"

SectionEnd
