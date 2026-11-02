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
!define MULTIUSER_INSTALLMODE_INSTALL_REGISTRY_KEY "${ApplicationName}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUENAME ""
!define MULTIUSER_INSTALLMODE_ALLOW_ELEVATION 1
!define MULTIUSER_INSTALLMODE_ALLOW_ELEVATION_IF_SILENT 1
!define MULTIUSER_INSTALLMODE_ALLOW_BOTH_INSTALLATIONS 0

!include MUI2.nsh
!include UAC.nsh
!include NsisMultiUser.nsh

!include x64.nsh

;Variables
Var PreviousInstallMode ; install mode of an existing copy that has to be removed before installing
Var PreviousInstallFolder
Var PreviousUninstallAttempts
Var LegacyInstallFolder
Var CleanupFolder

!macro RemoveLegacyInstallInfo
	${If} ${RunningX64}
		SetRegView 32
		DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
		DeleteRegKey HKLM "Software\${ApplicationName}"
		SetRegView 64
	${EndIf}
!macroend


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
	${GetParameters} $R0
	${GetOptions} $R0 "/removeprevious" $R1
	${IfNot} ${Errors}
		Call RemovePreviousAllUsersInstallation
		SetErrorLevel 0
		Quit
	${EndIf}
	ClearErrors

	!insertmacro MULTIUSER_INIT
	
	SectionSetSize ${MAINSECTIONIDX} ${InstallSize}
FunctionEnd

;Check whether a copy installed in the other mode has to be removed first
Function CheckPreviousInstallation
	StrCpy $PreviousInstallMode ""
	${If} $MultiUser.InstallMode == "AllUsers"
	${AndIf} $HasPerUserInstallation = 1
		StrCpy $PreviousInstallMode "CurrentUser"
	${ElseIf} $MultiUser.InstallMode == "CurrentUser"
	${AndIf} $HasPerMachineInstallation = 1
		StrCpy $PreviousInstallMode "AllUsers"
	${EndIf}

	${If} $PreviousInstallMode == "AllUsers"
		StrCpy $PreviousInstallFolder "$PerMachineInstallationFolder"
	${ElseIf} $PreviousInstallMode == "CurrentUser"
		StrCpy $PreviousInstallFolder "$PerUserInstallationFolder"
	${EndIf}
FunctionEnd

Function RemovePreviousAllUsersInstallation
	SetShellVarContext all
	ExecWait "TaskKill /IM ${ApplicationEXEName} /T /F"
	${If} ${RunningX64}
		SetRegView 64
	${EndIf}
	ReadRegStr $CleanupFolder HKLM "Software\${ApplicationName}" ""
	${If} $CleanupFolder != ""
		RMDir /r "$CleanupFolder"
	${EndIf}
	Delete "$SMPROGRAMS\${ApplicationName}.lnk"
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
	DeleteRegKey HKLM "Software\${ApplicationName}"
	${If} ${RunningX64}
		SetRegView 32
		ReadRegStr $CleanupFolder HKLM "Software\${ApplicationName}" ""
		SetRegView 64
		${If} $CleanupFolder != ""
			RMDir /r "$CleanupFolder"
		${EndIf}
	${EndIf}
	!insertmacro RemoveLegacyInstallInfo
	${If} $MultiUser.InstallMode == "AllUsers"
		SetShellVarContext all
	${Else}
		SetShellVarContext current
	${EndIf}
FunctionEnd

Function RemovePreviousCurrentUserInstallation
	SetShellVarContext current
	ExecWait "TaskKill /IM ${ApplicationEXEName} /T /F"
	ReadRegStr $CleanupFolder HKCU "Software\${ApplicationName}" ""
	${If} $CleanupFolder != ""
		RMDir /r "$CleanupFolder"
	${EndIf}
	Delete "$SMPROGRAMS\${ApplicationName}.lnk"
	DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
	DeleteRegKey HKCU "Software\${ApplicationName}"
	${If} $MultiUser.InstallMode == "AllUsers"
		SetShellVarContext all
	${Else}
		SetShellVarContext current
	${EndIf}
FunctionEnd

Function CheckPreviousInstallationRemoved
	${If} $PreviousInstallMode == "AllUsers"
		ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "InstallLocation"
	${Else}
		ReadRegStr $0 HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "InstallLocation"
	${EndIf}
FunctionEnd

Function CheckLegacyInstallation
	StrCpy $LegacyInstallFolder ""
	${If} ${RunningX64}
		SetRegView 32
		ReadRegStr $LegacyInstallFolder HKLM "Software\${ApplicationName}" ""
		SetRegView 64
	${EndIf}
FunctionEnd

;Explain how to recover when a previous installation cannot be removed
Function AbortPreviousInstallationRemoval
	SetErrorLevel 2
	BringToFront
	StrCpy $0 "Could not remove the previous Angry IP Scanner installation in$\r$\n$\"$PreviousInstallFolder$\".$\r$\n$\r$\nMake sure Angry IP Scanner is not running and that you allow the installer to make changes to your computer, then run this installer again.$\r$\n$\r$\nIf the problem persists, remove the folder and the Angry IP Scanner registry entries manually."
	Abort $0
FunctionEnd

;Installer section


Section Main
	;Only one installation mode is allowed, so remove a copy installed in the other mode first
	Call CheckPreviousInstallation
	${If} $PreviousInstallMode != ""
		DetailPrint "Removing previous installation for $PreviousInstallMode..."
		HideWindow
		ClearErrors
		${If} $PreviousInstallMode == "AllUsers"
			${If} ${UAC_IsAdmin}
				Call RemovePreviousAllUsersInstallation
			${Else}
				ExecShellWait "runas" "$EXEPATH" "/removeprevious"
			${EndIf}
		${Else}
			!insertmacro UAC_AsUser_Call Function RemovePreviousCurrentUserInstallation ${UAC_SYNCREGISTERS}
		${EndIf}
		${If} ${Errors}
			Call AbortPreviousInstallationRemoval
		${EndIf}
		BringToFront
		ClearErrors
		StrCpy $PreviousUninstallAttempts 0
		${Do}
			Sleep 500
			!insertmacro UAC_AsUser_Call Function CheckPreviousInstallationRemoved ${UAC_SYNCREGISTERS}
			${If} $0 == ""
				${ExitDo}
			${EndIf}
			IntOp $PreviousUninstallAttempts $PreviousUninstallAttempts + 1
		${LoopUntil} $PreviousUninstallAttempts >= 20
		${If} $0 != ""
			Call AbortPreviousInstallationRemoval
		${EndIf}
		${If} $PreviousInstallMode == "AllUsers"
			DeleteRegKey HKLM "Software\${ApplicationName}"
		${Else}
			DeleteRegKey HKCU "Software\${ApplicationName}"
		${EndIf}
	${EndIf}

	Call CheckLegacyInstallation
	${If} $LegacyInstallFolder != ""
		${If} ${UAC_IsAdmin}
			!insertmacro RemoveLegacyInstallInfo
			${If} $LegacyInstallFolder != $INSTDIR
				RMDir /r "$LegacyInstallFolder"
			${EndIf}
		${Else}
			StrCpy $PreviousInstallFolder "$LegacyInstallFolder"
			ClearErrors
			ExecShellWait "runas" "$EXEPATH" "/removeprevious"
			${If} ${Errors}
				Call AbortPreviousInstallationRemoval
			${EndIf}
		${EndIf}
	${EndIf}

	SetOutPath "$INSTDIR"
	File /r "..\AppFiles\*"

	;Remember the install location for uninstalls, upgrades and reinstalls
	WriteRegStr SHCTX "Software\${ApplicationName}" "" $INSTDIR

	;Create uninstaller
	WriteUninstaller "$INSTDIR\uninstall.exe"
	
	;Start menu
	CreateShortCut "$SMPROGRAMS\${ApplicationName}.lnk" "$INSTDIR\${ApplicationEXEName}"
	
	;Add/remove programs
	${If} $MultiUser.InstallMode == "AllUsers"
		StrCpy $0 "/allusers"
	${Else}
		StrCpy $0 "/currentuser"
	${EndIf}
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "DisplayName" "${ApplicationName}"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "UninstallString" "$\"$INSTDIR\uninstall.exe$\" $0"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "QuietUninstallString" "$\"$INSTDIR\uninstall.exe$\" $0 /S"
	WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}" "InstallLocation" "$INSTDIR"
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

	;The uninstaller cannot delete itself when $INSTDIR was passed via _?=
	${If} ${FileExists} "$INSTDIR\uninstall.exe"
		Exec 'cmd.exe /c (del /f /q "$INSTDIR\uninstall.exe") && (rmdir "$INSTDIR")'
	${EndIf}

	Delete "$SMPROGRAMS\${ApplicationName}.lnk"

	DeleteRegKey SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ApplicationName}"
	DeleteRegKey SHCTX "Software\${ApplicationName}"
	!insertmacro RemoveLegacyInstallInfo

	;Keep the user's settings when the installer removes the old copy to switch the install mode
	${GetParameters} $R0
	${GetOptions} $R0 "/keepsettings" $R1
	${If} ${Errors}
		DeleteRegKey HKCU "Software\JavaSoft\Prefs\${InstallerFileName}"
	${EndIf}
SectionEnd
