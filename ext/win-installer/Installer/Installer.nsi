;Copyright 2007-2013 John T. Haller of PortableApps.com
;Website: http://PortableApps.com/

;This software is OSI Certified Open Source Software.
;OSI Certified is a certification mark of the Open Source Initiative.

;This program is free software; you can redistribute it and/or
;modify it under the terms of the GNU General Public License
;as published by the Free Software Foundation; either version 2
;of the License, or (at your option) any later version.

;This program is distributed in the hope that it will be useful,
;but WITHOUT ANY WARRANTY; without even the implied warranty of
;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;GNU General Public License for more details.

;You should have received a copy of the GNU General Public License
;along with this program; if not, write to the Free Software
;Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

;Get the installer configuration first
!include "..\InstallerConfig.nsh"

;General installer details
Name "${ApplicationName} ${AppVersionFriendly}"
OutFile "..\${InstallerFileName}-${AppVersionFriendly}-setup.exe"
InstallDir "$PROGRAMFILES\${DefaultDirectory}"
InstallDirRegKey HKLM "Software\${ApplicationName}" ""
RequestExecutionLevel admin
BrandingText "${FooterText}"


;Runtime switches
SetCompress Auto
SetCompressor /SOLID lzma
SetCompressorDictSize 32
SetDatablockOptimize On
CRCCheck on


;Includes
!include LogicLib.nsh
!include MUI2.nsh
!include x64.nsh


;Modern UI 2 details
!define MUI_ICON "InstallerGraphics\installer.ico"
!define MUI_UNICON "InstallerGraphics\uninstaller.ico"
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "InstallerGraphics\header-r.bmp"
!define MUI_HEADERIMAGE_BITMAP_RTL "InstallerGraphics\header.bmp"
!define MUI_HEADERIMAGE_RIGHT
!define MUI_HEADERIMAGE_UNBITMAP "InstallerGraphics\header-r.bmp"
!define MUI_HEADERIMAGE_UNBITMAP_RTL "InstallerGraphics\header.bmp"
!define MUI_WELCOMEFINISHPAGE_BITMAP "InstallerGraphics\welcomefinish.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "InstallerGraphics\welcomefinish-uninstall.bmp"
!define MUI_ABORTWARNING
!define MAINSECTIONIDX 0
!define MUI_FINISHPAGE_RUN_NOTCHECKED
!define MUI_FINISHPAGE_RUN "$INSTDIR\${ApplicationEXEName}"


;Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH


;Pages (Uninstaller)
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH


;Languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Italian"


;Macro for verifying admin on Windows 2000/XP
!macro VerifyUserIsAdmin
	UserInfo::GetAccountType
	Pop $0
	${If} $0 != "admin" ;Require admin rights on NT4+
		MessageBox MB_ICONSTOP "This installer must be run as an administrator."
        SetErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
        Quit
	${EndIf}
!macroend


;Installer initialization
Function .onInit
	SetShellVarContext all
	!insertmacro VerifyUserIsAdmin
	
	${If} ${RunningX64}
		StrCpy $INSTDIR "$PROGRAMFILES64\${DefaultDirectory}"
	${Else}
		StrCpy $INSTDIR "$PROGRAMFILES\${DefaultDirectory}"
	${EndIf}
	
	SectionSetSize ${MAINSECTIONIDX} ${InstallSize}
FunctionEnd

;Installer section


Section Main
	SetOutPath "$INSTDIR"
	File /r "..\AppFiles\*"

	;Remember the install location for uninstalls, upgrades and reinstalls
	WriteRegStr HKLM "Software\${ApplicationName}" "" $INSTDIR

	;Create uninstaller
	WriteUninstaller "$INSTDIR\uninstall.exe"
	
	;Start menu
	CreateShortCut "$SMPROGRAMS\${ApplicationName}.lnk" "$INSTDIR\${ApplicationEXEName}"
	
	;Add/remove programs
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayName" "${ApplicationName}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "UninstallString" "$\"$INSTDIR\uninstall.exe$\""
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "QuietUninstallString" "$\"$INSTDIR\uninstall.exe$\" /S"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "InstallLocation" "$\"$INSTDIR$\""
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayIcon" "$\"$INSTDIR\icon.ico$\""
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "Publisher" "${ApplicationName}"
	;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "HelpLink" "URLHERE"
	;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "URLUpdateInfo" "URLHERE"
	;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "URLInfoAbout" "URLHERE"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayVersion" "${AppVersionFriendly}"
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "VersionMajor" ${AppVersionMajor}
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "VersionMinor" ${AppVersionMinor}
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "NoModify" 1
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "NoRepair" 1
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "EstimatedSize" ${InstallSize}
SectionEnd


;Uninstaller initialization
Function un.onInit
	SetShellVarContext all
	!insertmacro VerifyUserIsAdmin
FunctionEnd

;Uninstaller section
Section "Uninstall"
	ExecWait 'TaskKill /IM ipscan.exe /T /F'
	RMDir /r "$INSTDIR"
	Delete "$SMPROGRAMS\${ApplicationName}.lnk"

	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
	DeleteRegKey /ifempty HKLM "Software\${ApplicationName}"
	DeleteRegKey HKCU "Software\JavaSoft\Prefs\${InstallerFileName}"
SectionEnd
