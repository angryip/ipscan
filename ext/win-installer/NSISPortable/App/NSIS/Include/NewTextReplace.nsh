/*****************************************************************
 *              NewTextReplace NSIS plugin v0.4                  *
 *                  by Gringoloco023, 2010                       *
 *            http://portableapps.com/node/21840                 *
 *                          Based on:                            *
 *                TextReplace NSIS plugin v1.5                   *
 *                                                               *
 * 2006 Shengalts Aleksander aka Instructor (Shengalts@mail.ru)  *
 *****************************************************************/


!define ReplaceInFileUTF16LECS '!insertmacro "ReplaceInFileUTF16LECS"' ;${ReplaceInFileUTF16LECS} SOURCE_FILE SEARCH_TEXT REPLACEMENT
!define ReplaceInFileUTF16LE '!insertmacro "ReplaceInFileUTF16LE"' ;${ReplaceInFileUTF16LE} SOURCE_FILE SEARCH_TEXT REPLACEMENT


!define textreplace::FindInFile `!insertmacro textreplace::FindInFile`

!macro textreplace::FindInFile _INPUTFILE _FINDIT _OPTIONS _COUNT
	newtextreplace::_FindInFile /NOUNLOAD `${_INPUTFILE}` `${_FINDIT}` `${_OPTIONS}`
	Pop ${_COUNT}
!macroend


!define textreplace::ReplaceInFile `!insertmacro textreplace::ReplaceInFile`

!macro textreplace::ReplaceInFile _INPUTFILE _OUTPUTFILE _REPLACEIT _REPLACEWITH _OPTIONS _COUNT
	newtextreplace::_ReplaceInFile /NOUNLOAD `${_INPUTFILE}` `${_OUTPUTFILE}` `${_REPLACEIT}` `${_REPLACEWITH}` `${_OPTIONS}`
	Pop ${_COUNT}
!macroend


!define textreplace::FillReadBuffer `!insertmacro textreplace::FillReadBuffer`

!macro textreplace::FillReadBuffer _FILE _POINTER
	newtextreplace::_FillReadBuffer /NOUNLOAD `${_FILE}`
	Pop ${_POINTER}
!macroend



!define textreplace::FreeReadBuffer `!insertmacro textreplace::FreeReadBuffer`

!macro textreplace::FreeReadBuffer _POINTER
	newtextreplace::_FreeReadBuffer /NOUNLOAD `${_POINTER}`
!macroend



!define textreplace::Unload `!insertmacro textreplace::Unload`

!macro textreplace::Unload
	newtextreplace::_Unload
!macroend

/*****************************************************************
 ***                The following is meant to                  ***
 ***               be used in combination with                 ***
 ***            ReplaceInFileWithTextReplace.nsh               ***
 *****************************************************************/

!macro ReplaceInFileUTF16LECS SOURCE_FILE SEARCH_TEXT REPLACEMENT
	Push `/U=1 /S=1`
	Push `${SOURCE_FILE}`
	Push `${SEARCH_TEXT}`
	Push `${REPLACEMENT}`
	Call ReplaceInFile

!macroend

!macro ReplaceInFileUTF16LE SOURCE_FILE SEARCH_TEXT REPLACEMENT
	Push `/U=1 /S=0`
	Push `${SOURCE_FILE}`
	Push `${SEARCH_TEXT}`
	Push `${REPLACEMENT}`
	Call ReplaceInFile
!macroend

