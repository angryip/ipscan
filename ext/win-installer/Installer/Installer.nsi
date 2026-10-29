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
BrandingText "${FooterText}"


;Runtime switches
SetCompress Auto
SetCompressor /SOLID lzma
SetCompressorDictSize 32
SetDatablockOptimize On
CRCCheck on


;Includes
!include LogicLib.nsh

; MultiUser Setup
!addplugindir "..\NsisMultiUser\Plugins\x86-unicode"
!addincludedir "..\NsisMultiUser\Include"

!define PRODUCT_NAME "${ApplicationName}"
!define VERSION "${AppVersionFriendly}"
!define PROGEXE "${ApplicationEXEName}"
!define MULTIUSER_INSTALLMODE_64_BIT 1
!define MULTIUSER_INSTALLMODE_DISPLAYNAME "${ApplicationName}"
!define MULTIUSER_INSTALLMODE_INSTDIR "${DefaultDirectory}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_KEY "Software\${ApplicationName}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUENAME ""
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_KEY "Software\${ApplicationName}"
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_VALUENAME ""
!define MULTIUSER_INSTALLMODE_ALLOW_ELEVATION 1

!include MUI2.nsh
!include UAC.nsh
!include NsisMultiUser.nsh

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
!insertmacro MULTIUSER_PAGE_INSTALLMODE
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH


;Pages (Uninstaller)
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MULTIUSER_UNPAGE_INSTALLMODE
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH


;Languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Russian"
!insertmacro MUI_LANGUAGE "German"
!insertmacro MUI_LANGUAGE "Hungarian"
!insertmacro MUI_LANGUAGE "Lithuanian"
!insertmacro MUI_LANGUAGE "Spanish"
!insertmacro MUI_LANGUAGE "Finnish"
!insertmacro MUI_LANGUAGE "French"
!insertmacro MUI_LANGUAGE "Italian"
!insertmacro MUI_LANGUAGE "Japanese"
!insertmacro MUI_LANGUAGE "Turkish"
!insertmacro MUI_LANGUAGE "Greek"
!insertmacro MUI_LANGUAGE "PortugueseBR"
!insertmacro MUI_LANGUAGE "SimpChinese"
!insertmacro MUI_LANGUAGE "TradChinese"

!insertmacro MULTIUSER_LANGUAGE_INIT


;Macro for verifying admin on Windows 2000/XP
;Removed because MultiUser handles this now

;Installer initialization
Function .onInit
	!insertmacro MULTIUSER_INIT
	
	SectionSetSize ${MAINSECTIONIDX} ${InstallSize}
FunctionEnd

;Installer section


Section Main
	SetOutPath "$INSTDIR"
	File /r "..\AppFiles\*"

	;Remember the install location for uninstalls, upgrades and reinstalls
	WriteRegStr SHCTX "Software\${ApplicationName}" "" $INSTDIR

	;Create uninstaller
	WriteUninstaller "$INSTDIR\uninstall.exe"
	
	;Start menu
	CreateShortCut "$SMPROGRAMS\${ApplicationName}.lnk" "$INSTDIR\${ApplicationEXEName}"
	
	;Add/remove programs
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayName" "${ApplicationName}"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "UninstallString" "$\"$INSTDIR\uninstall.exe$\""
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "QuietUninstallString" "$\"$INSTDIR\uninstall.exe$\" /S"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "InstallLocation" "$\"$INSTDIR$\""
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayIcon" "$\"$INSTDIR\icon.ico$\""
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "Publisher" "${ApplicationName}"
	;WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "HelpLink" "URLHERE"
	;WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "URLUpdateInfo" "URLHERE"
	;WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "URLInfoAbout" "URLHERE"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayVersion" "${AppVersionFriendly}"
	WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "VersionMajor" ${AppVersionMajor}
	WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "VersionMinor" ${AppVersionMinor}
	WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "NoModify" 1
	WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "NoRepair" 1
	WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "EstimatedSize" ${InstallSize}
SectionEnd


;Uninstaller initialization
Function un.onInit
	!insertmacro MULTIUSER_UNINIT
FunctionEnd

;Uninstaller section
Section "Uninstall"
	ExecWait "TaskKill /IM ${ApplicationEXEName} /T /F"
	RMDir /r "$INSTDIR"
	Delete "$SMPROGRAMS\${ApplicationName}.lnk"

	DeleteRegKey SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
	DeleteRegKey /ifempty SHCTX "Software\${ApplicationName}"
	DeleteRegKey HKCU "Software\JavaSoft\Prefs\${InstallerFileName}"
SectionEnd
