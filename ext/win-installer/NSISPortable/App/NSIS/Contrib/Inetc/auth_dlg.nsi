
;--------------------------------
; General Attributes

Name "Inetc http auth Test"
OutFile "auth_dlg.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; Displays IE auth dialog.
; Both server and proxy auth.
; Please test this with your own link.

    inetc::get "http://www.cnt.ru/personal" "$EXEDIR\auth.html"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd
