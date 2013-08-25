
;--------------------------------
; General Attributes

Name "Headers Test"
OutFile "headers.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"



;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

; additional headers. Sample php returns raw headers
    inetc::get /useragent "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1)" /header "SOAPAction: urn:anonOutInOpe" "http://localhost/headers.php" "$EXEDIR\headers.html"
    Pop $0

    MessageBox MB_OK "Download Status: $0"

SectionEnd


