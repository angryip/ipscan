
;--------------------------------
; General Attributes

Name "Inetc https Test"
OutFile "https.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

    inetc::get /POPUP "" /CAPTION "bending_property_demo.zip" "https://secure.codeproject.com/cs/miscctrl/bending_property/bending_property_src.zip" "$EXEDIR\bending_property_src.zip"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd
