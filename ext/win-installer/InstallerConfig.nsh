;Installer configuration options for name, version and other items are below

;ApplicationName is the full application name shown in the installer
!define ApplicationName "Angry IP Scanner"				
	
;The name of the applications EXE file.  Used to create a shortcut and ensure it is not running during install
!define ApplicationEXEName "ipscan.exe"

;FooterText appears in the lower left of the installer pages
!define FooterText "Fast and Friendly Network Scanner"	
	
;DefaultDirectory is the default directory to install to within Program Files
!define DefaultDirectory "Angry IP Scanner"
	
;InstallerFileName is the start of the generated installer file. No spaces or invalid Windows file/directory characters.
!define InstallerFileName "ipscan"
	
;AppVersionFull is a full 4 number dotted version number.  No letters, spaces, etc, dots only ex: 1.2.3.4
!define AppVersionFull "3.8.0.0"

;AppVersionFriendly is the 'official' version number shown in the installer and installer file name.
;	Do not included spaces or invalid Windows file/directory characters
!define AppVersionFriendly "3.8.1-1-g10e6337.dirty"

;AppVersionMajor and AppVersionMinor would be the first two digits in the full 4 number dotted version
;   Only use a single number for each.  Windows uses this for add/remove programs
!define AppVersionMajor "3"
!define AppVersionMinor "8"

;InstallSize is how big the program is when installed in KB.  Defining it allows us to show it in add/remove programs
!define InstallSize 1736

	
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
