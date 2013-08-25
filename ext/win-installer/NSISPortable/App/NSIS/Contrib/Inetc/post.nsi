
;--------------------------------
; General Attributes

Name "Inetc Post Test"
OutFile "post.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; this is my LAN sample, use your own URL for tests. Sample post.php included

    inetc::post "login=ami&passwd=333" "http://localhost/post.php?lg=iam&pw=44" "$EXEDIR\post_reply.htm"
    Pop $0 # return value = exit code, "OK" if OK
    MessageBox MB_OK "Download Status: $0"

SectionEnd


