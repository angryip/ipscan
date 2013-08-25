# "Dialogs header file by Joel Almeida García"

#include once this header file
!ifndef DIALOGS_NSH
        !define DIALOGS_NSH
        !verbose push
        !verbose 3

        !ifndef LOGICLIB
                #Add logic library
                !include "LogicLib.nsh"
        !endif

        # Global stuff
        !define ISTRUE 1
        !define ISFALSE 0
        !define NULL ""

        # Returning Vars
        !define VAR_0 0 # $0
        !define VAR_1 1 # $1
        !define VAR_2 2 # $2
        !define VAR_3 3 # $3
        !define VAR_4 4 # $4
        !define VAR_5 5 # $5
        !define VAR_6 6 # $6
        !define VAR_7 7 # $7
        !define VAR_8 8 # $8
        !define VAR_9 9 # $9
        !define VAR_R0 10 # $R0
        !define VAR_R1 11 # $R1
        !define VAR_R2 12 # $R2
        !define VAR_R3 13 # $R3
        !define VAR_R4 14 # $R4
        !define VAR_R5 15 # $R5
        !define VAR_R6 16 # $R6
        !define VAR_R7 17 # $R7
        !define VAR_R8 18 # $R8
        !define VAR_R9 19 # $R9
        !define VAR_CMDLINE 20 # $CMDLINE
        !define VAR_INSTDIR 21 # $INSTDIR
        !define VAR_OUTDIR 22 # $OUTDIR
        !define VAR_EXEDIR 23 # $EXEDIR
        !define VAR_LANG 24 # $LANGUAGE

        # Function prototypes
        !define OpenBox 'dialogsEx::FileBox ""'
        !define SaveBox 'dialogsEx::FileBox "1"'
        !define ClassicFolderBox 'dialogsEx::FolderBox ""'
        !define ModernFolderBox 'dialogsEx::FolderBox "1"'
        !define InputTextBox 'dialogsEx::InputBox ""'
        !define InputPwdBox 'dialogsEx::InputBox "1"'
        !define InputRegBox 'dialogsEx::InputRegBox'

        !verbose pop
!endif