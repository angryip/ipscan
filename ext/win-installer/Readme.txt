This installer creator is designed to make creating a fully functional
dual-mode (32-bit and 64-bit) Windows installer as easy as possible.

To use, follow these instructions:

1. Place the 32-bit and 64-bit files that will be installed for the
   application within the AppFiles32 and AppFiles64 directories,
   respectively.
   
   Note: Do note remove the icon.ico files from either directory as
   these are used by the uninstaller
   
2. Edit the InstallerLicense.txt file if you would like to change 
   anything about the license shown to the end user as they install.
   
3. Edit the InstallerConfig.nsh file to set the name, version number,
   and any other information appropriate for the new release of your
   software.  Note that you will need to set the version in multiple
   places.  Also be sure to update the install size manually.
   
4. Once you are happy with your settings, run _build.bat.  Your installer
   will be created within this directory.
   
   
Prepared on 2013-08-21 by John T. Haller of PortableApps.com