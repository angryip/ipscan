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

;File properties shown by Windows Explorer
VIProductVersion "${AppVersionFull}"
VIAddVersionKey "ProductName" "${ApplicationName}"
VIAddVersionKey "FileDescription" "${ApplicationName} Installer"
VIAddVersionKey "CompanyName" "angryip.org"
VIAddVersionKey "LegalCopyright" "(C) 2026 Anton Keks and contributors"
VIAddVersionKey "FileVersion" "${AppVersionFriendly}"
VIAddVersionKey "ProductVersion" "${AppVersionFriendly}"
VIAddVersionKey "InternalName" "ipscan"
VIAddVersionKey "OriginalFilename" "ipscan-${AppVersionFriendly}-setup.exe"


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

; NsisMultiUser only has English placeholders for Italian and Spanish, so skip those blocks and define ours below
!define IPScanItalianLangId ${LANG_ITALIAN}
!define IPScanSpanishLangId ${LANG_SPANISH}
!undef LANG_ITALIAN
!undef LANG_SPANISH
!insertmacro MULTIUSER_LANGUAGE_INIT
!define LANG_ITALIAN ${IPScanItalianLangId}
!define LANG_SPANISH ${IPScanSpanishLangId}

; Italian translations for the NsisMultiUser pages
LangString MULTIUSER_PAGE_TITLE ${LANG_ITALIAN} "Scegli utenti"
LangString MULTIUSER_INSTALL_PAGE_SUBTITLE ${LANG_ITALIAN} "Scegli per quali utenti installare $(^NameDA)."
LangString MULTIUSER_UNINSTALL_PAGE_SUBTITLE ${LANG_ITALIAN} "Scegli per quali utenti rimuovere $(^NameDA)."
LangString MULTIUSER_INSTALL_HEADER ${LANG_ITALIAN} "Seleziona se installare $(^NameDA) per tutti gli utenti o solo per l'utente corrente."
LangString MULTIUSER_UNINSTALL_HEADER ${LANG_ITALIAN} "$(^NameDA) è installato sia per tutti gli utenti sia per l'utente corrente.$\r$\nSeleziona quale installazione rimuovere."
LangString MULTIUSER_ALL_USERS ${LANG_ITALIAN} "Per &chiunque utilizzi questo computer (tutti gli utenti)"
LangString MULTIUSER_CURRENT_USER ${LANG_ITALIAN} "Per &me ({USER})"
LangString MULTIUSER_ALL_USERS_UMUI ${LANG_ITALIAN} "Per chiunque utilizzi questo computer (tutti gli utenti)"
LangString MULTIUSER_CURRENT_USER_UMUI ${LANG_ITALIAN} "Per me ({USER})"
LangString MULTIUSER_NEW_INSTALLATION_ALL_USERS ${LANG_ITALIAN} "Nuova installazione per tutti gli utenti."
LangString MULTIUSER_NEW_INSTALLATION_CURRENT_USER ${LANG_ITALIAN} "Nuova installazione per l'utente corrente."
LangString MULTIUSER_INSTALLED_ALL_USERS ${LANG_ITALIAN} "La versione {VERSION} è installata per tutti gli utenti in $\"{FOLDER}$\"."
LangString MULTIUSER_INSTALLED_CURRENT_USER ${LANG_ITALIAN} "La versione {VERSION} è installata per l'utente corrente in $\"{FOLDER}$\"."
LangString MULTIUSER_REINSTALL_SAME_VERSION_ALL_USERS ${LANG_ITALIAN} "Reinstalla la versione {VERSION} per tutti gli utenti."
LangString MULTIUSER_REINSTALL_SAME_VERSION_CURRENT_USER ${LANG_ITALIAN} "Reinstalla la versione {VERSION} per l'utente corrente."
LangString MULTIUSER_REINSTALL_DIFF_VERSION_ALL_USERS ${LANG_ITALIAN} "Disinstalla la versione {OLD_VERSION} e installa la versione {VERSION} per tutti gli utenti."
LangString MULTIUSER_REINSTALL_DIFF_VERSION_CURRENT_USER ${LANG_ITALIAN} "Disinstalla la versione {OLD_VERSION} e installa la versione {VERSION} per l'utente corrente."
LangString MULTIUSER_RUN_AS_ADMIN ${LANG_ITALIAN} "Devi eseguire questo programma come amministratore."
LangString MULTIUSER_ADMIN_CREDENTIALS_REQUIRED ${LANG_ITALIAN} "Sono richieste credenziali di amministratore."
LangString MULTIUSER_ADMIN_UNINSTALL_CREDENTIALS_REQUIRED ${LANG_ITALIAN} "Sono richieste credenziali di amministratore per la disinstallazione."
LangString MULTIUSER_INVALID_PARAMS ${LANG_ITALIAN} "Combinazione di parametri non valida."
LangString MULTIUSER_NOT_INSTALLED ${LANG_ITALIAN} "Non è presente alcuna installazione di $(^NameDA)."
LangString MULTIUSER_INSTALLATION_FOR_CURRENT_USER_NOT_SUPPORTED ${LANG_ITALIAN} "Il sistema operativo non supporta l'installazione per l'utente corrente."
LangString MULTIUSER_ADMIN_ACCOUNT_LOGIN_REQUIRED ${LANG_ITALIAN} "Devi accedere con un account membro del gruppo Administrators per continuare."
LangString MULTIUSER_ELEVATION_NOT_SUPPORTED ${LANG_ITALIAN} "Il sistema operativo non supporta l'elevazione dei privilegi."
LangString MULTIUSER_LOGON_SERVICE_NOT_RUNNING ${LANG_ITALIAN} "Impossibile elevare i privilegi: il servizio Accesso secondario non è in esecuzione."
LangString MULTIUSER_ELEVATION_ERROR ${LANG_ITALIAN} "Impossibile elevare i privilegi, errore {ERROR}."

; Spanish translations for the NsisMultiUser pages
LangString MULTIUSER_PAGE_TITLE ${LANG_SPANISH} "Elegir usuarios"
LangString MULTIUSER_INSTALL_PAGE_SUBTITLE ${LANG_SPANISH} "Elige para qué usuarios instalar $(^NameDA)."
LangString MULTIUSER_UNINSTALL_PAGE_SUBTITLE ${LANG_SPANISH} "Elige para qué usuarios quitar $(^NameDA)."
LangString MULTIUSER_INSTALL_HEADER ${LANG_SPANISH} "Selecciona si instalar $(^NameDA) para todos los usuarios o solo para el usuario actual."
LangString MULTIUSER_UNINSTALL_HEADER ${LANG_SPANISH} "$(^NameDA) está instalado tanto para todos los usuarios como para el usuario actual.$\r$\nSelecciona qué instalación deseas quitar."
LangString MULTIUSER_ALL_USERS ${LANG_SPANISH} "Para &cualquiera que use este equipo (todos los usuarios)"
LangString MULTIUSER_CURRENT_USER ${LANG_SPANISH} "Para &mí ({USER})"
LangString MULTIUSER_ALL_USERS_UMUI ${LANG_SPANISH} "Para cualquiera que use este equipo (todos los usuarios)"
LangString MULTIUSER_CURRENT_USER_UMUI ${LANG_SPANISH} "Para mí ({USER})"
LangString MULTIUSER_NEW_INSTALLATION_ALL_USERS ${LANG_SPANISH} "Instalación nueva para todos los usuarios."
LangString MULTIUSER_NEW_INSTALLATION_CURRENT_USER ${LANG_SPANISH} "Instalación nueva para el usuario actual."
LangString MULTIUSER_INSTALLED_ALL_USERS ${LANG_SPANISH} "La versión {VERSION} está instalada para todos los usuarios en $\"{FOLDER}$\"."
LangString MULTIUSER_INSTALLED_CURRENT_USER ${LANG_SPANISH} "La versión {VERSION} está instalada para el usuario actual en $\"{FOLDER}$\"."
LangString MULTIUSER_REINSTALL_SAME_VERSION_ALL_USERS ${LANG_SPANISH} "Reinstalar la versión {VERSION} para todos los usuarios."
LangString MULTIUSER_REINSTALL_SAME_VERSION_CURRENT_USER ${LANG_SPANISH} "Reinstalar la versión {VERSION} para el usuario actual."
LangString MULTIUSER_REINSTALL_DIFF_VERSION_ALL_USERS ${LANG_SPANISH} "Desinstalar la versión {OLD_VERSION} e instalar la versión {VERSION} para todos los usuarios."
LangString MULTIUSER_REINSTALL_DIFF_VERSION_CURRENT_USER ${LANG_SPANISH} "Desinstalar la versión {OLD_VERSION} e instalar la versión {VERSION} para el usuario actual."
LangString MULTIUSER_RUN_AS_ADMIN ${LANG_SPANISH} "Debes ejecutar este programa como administrador."
LangString MULTIUSER_ADMIN_CREDENTIALS_REQUIRED ${LANG_SPANISH} "Se requieren credenciales de administrador."
LangString MULTIUSER_ADMIN_UNINSTALL_CREDENTIALS_REQUIRED ${LANG_SPANISH} "Se requieren credenciales de administrador para desinstalar."
LangString MULTIUSER_INVALID_PARAMS ${LANG_SPANISH} "Combinación de parámetros no válida."
LangString MULTIUSER_NOT_INSTALLED ${LANG_SPANISH} "No hay ninguna instalación de $(^NameDA)."
LangString MULTIUSER_INSTALLATION_FOR_CURRENT_USER_NOT_SUPPORTED ${LANG_SPANISH} "El sistema operativo no admite instalaciones para el usuario actual."
LangString MULTIUSER_ADMIN_ACCOUNT_LOGIN_REQUIRED ${LANG_SPANISH} "Debes iniciar sesión con una cuenta que pertenezca al grupo de administradores para continuar."
LangString MULTIUSER_ELEVATION_NOT_SUPPORTED ${LANG_SPANISH} "El sistema operativo no admite la elevación de privilegios."
LangString MULTIUSER_LOGON_SERVICE_NOT_RUNNING ${LANG_SPANISH} "No se pueden elevar los privilegios: el servicio Inicio de sesión secundario no está en ejecución."
LangString MULTIUSER_ELEVATION_ERROR ${LANG_SPANISH} "No se pueden elevar los privilegios, error {ERROR}."

; Footer text, translated only where available (English is the fallback for the rest)
LangString FooterText ${LANG_ENGLISH} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_RUSSIAN} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_GERMAN} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_HUNGARIAN} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_LITHUANIAN} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_SPANISH} "Escáner de red rápido y amigable"
LangString FooterText ${LANG_FINNISH} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_FRENCH} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_ITALIAN} "Scanner di rete veloce e amichevole"
LangString FooterText ${LANG_JAPANESE} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_TURKISH} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_GREEK} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_PORTUGUESEBR} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_SIMPCHINESE} "Fast and Friendly Network Scanner"
LangString FooterText ${LANG_TRADCHINESE} "Fast and Friendly Network Scanner"

BrandingText "$(FooterText)"


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

	; Default to a per-user installation in silent mode if no install mode was given.
	; NsisMultiUser requires an explicit mode in silent mode, so relaunch with /currentuser.
	${If} ${Silent}
		${GetOptions} $R0 "/allusers" $R1
		${If} ${Errors}
			${GetOptions} $R0 "/currentuser" $R2
			${If} ${Errors}
				ClearErrors
				StrCpy $0 0
				ExecWait '"$EXEPATH" /currentuser /S $R0' $0
				SetErrorLevel $0
				Quit
			${EndIf}
		${EndIf}
		ClearErrors
	${EndIf}

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
	; Default to a per-user uninstall in silent mode if no install mode was given.
	; NsisMultiUser requires an explicit mode in silent mode, so relaunch with /currentuser.
	${If} ${Silent}
		${GetParameters} $R0
		${GetOptions} $R0 "/allusers" $R1
		${If} ${Errors}
			${GetOptions} $R0 "/currentuser" $R2
			${If} ${Errors}
				ClearErrors
				StrCpy $1 ""
				${GetOptions} $R0 "/keepsettings" $2
				${IfNot} ${Errors}
					StrCpy $1 "/keepsettings"
				${EndIf}
				ClearErrors
				StrCpy $0 0
				ExecWait '"$EXEPATH" /currentuser /S $1' $0
				SetErrorLevel $0
				Quit
			${EndIf}
		${EndIf}
		ClearErrors
	${EndIf}

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
