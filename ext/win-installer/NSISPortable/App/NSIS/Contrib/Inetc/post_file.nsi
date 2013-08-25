
;--------------------------------
; General Attributes

Name "Inetc Post Test"
OutFile "post_file.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; this is my LAN sample, use your own URL for tests. Sample post.php included

    inetc::post "$EXEDIR\inetc.cpp" /file "http://localhost/post_file.php" "$EXEDIR\post_file.htm"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd


