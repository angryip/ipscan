
;--------------------------------
; General Attributes

Name "Inetc ftp authentication Test"
OutFile "ftp_auth.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; use your own URL and login@pwd. Password hidden from user with /popup "ALIAS"

    inetc::get /caption "service pack download" /popup "ftp://localhost/" "ftp://login:pwd@localhost/W2Ksp3.exe" "$EXEDIR\sp3.exe"
;    inetc::put /caption "service pack upload" /popup "" "ftp://login:pwd@localhost/W2Ksp3.bu.exe" "$EXEDIR\sp3.exe"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd

