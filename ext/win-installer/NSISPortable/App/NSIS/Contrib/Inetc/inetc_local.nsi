
;--------------------------------
; General Attributes

Name "Inetc Local Test"
OutFile "inetc_local.exe"


;--------------------------------
;Interface Settings

  !include "MUI.nsh"
  !define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy


;                     PUT test

; FTP requires anonymous access in sample below.
; HTTP sample put.php included to package. Stores test.jpg as m2.bmp
; check server files present after upload

    inetc::put "http://localhost/put.php" "$EXEDIR\test.jpg"
    Pop $0

    inetc::put "ftp://localhost/test.jpg" "$EXEDIR\test.jpg"
; not anonymous format
;    inetc::put "ftp://login:password@localhost/test.jpg" "$EXEDIR\test.jpg"
    Pop $1

	DetailPrint "PUT: HTTP $0, FTP $1 (verify server files)"


;                    POST test

; HTTP sample post.php and post_form.htm (to compare results) included

    inetc::post "login=ami&passwd=333" "http://localhost/post.php?lg=iam&pw=44" "$EXEDIR\post_reply.htm"
    Pop $2

	DetailPrint "POST: $2 (post_reply.htm)"


;                   HEAD test

; uses uploaded earlier test.jpg

    inetc::head /silent "http://localhost/m2.bmp" "$EXEDIR\head.txt"
    Pop $3

	DetailPrint "HEAD: $3 (head.txt)"


;                   GET test

; 2 files download in nsisdl mode 
    inetc::get "http://localhost/m2.bmp" "$EXEDIR\get1.jpg" "http://localhost/m2.bmp" "$EXEDIR\get2.jpg"
    Pop $4

    inetc::get /popup "Localhost:GET with Popup" "http://localhost/m2.bmp" "$EXEDIR\get3.jpg"
    Pop $5

    inetc::get /banner "Local Test GET with Banner" "http://localhost/m2.bmp" "$EXEDIR\get4.jpg"
    Pop $6

    inetc::get /silent "ftp://localhost/test.jpg" "$EXEDIR\get5.jpg"
    Pop $7

	DetailPrint "GET: NSISDL $4, POPUP $5, BANNER $6, FTP $7 (get1-5.jpg)"

	SetDetailsView show

SectionEnd
