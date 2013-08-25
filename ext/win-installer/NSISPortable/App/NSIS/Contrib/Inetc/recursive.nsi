Name "Inetc Recursive Dir Upload Test"
OutFile "recursive.exe"

!include "MUI.nsh"
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_LANGUAGE "English"
!include "FileFunc.nsh"
!insertmacro GetFileAttributes

var url
var path

Function dirul

  Push $0 ; search handle
  Push $1 ; file name
  Push $2 ; attributes

  FindFirst $0 $1 "$path\*"
loop:
  StrCmp $1 "" done
  ${GetFileAttributes} "$path\$1" DIRECTORY $2
  IntCmp $2 1 isdir
retry:
  Inetc::put $url/$1 "$path\$1" /end
  Pop $2
  DetailPrint "$2 $path\$1"
  StrCmp $2 "OK" cont
  MessageBox MB_YESNO "$path\$1 file upload failed. Retry?" IDYES retry
  Abort "terminated by user"
  Goto cont
isdir:
  StrCmp $1 . cont
  StrCmp $1 .. cont
  Push $path
  Push $url
  StrCpy $path "$path\$1"
  StrCpy $url "$url/$1"
  Call dirul
  Pop $url
  Pop $path
cont:
  FindNext $0 $1
  Goto loop
done:
  FindClose $0

  Pop $2
  Pop $1
  Pop $0

FunctionEnd


Section "Dummy Section" SecDummy

  SetDetailsView hide
  StrCpy $path "$EXEDIR"
; put is dir in the user's ftp home, use //put for root-relative path
  StrCpy $url ftp://takhir:pwd@localhost/put
  Call dirul
  SetDetailsView show

SectionEnd
