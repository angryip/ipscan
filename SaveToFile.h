/*********************************************************************
 * This is a part of Angry IP Scanner source code                    *
 * http://www.angryziber.com/ipscan/                                 *
 *                                                                   *
 * Written by Angryziber                                             *
 *                                                                   *
 * You may distribute this code as long as this message is not       *
 * removed and it is clear who has written it.                       *
 * You may not rename the program and distribute it.                 *
 *********************************************************************/

// SaveToFile.h: interface for the CSaveToFile class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SAVETOFILE_H__3E06FBF6_2ECE_4F8E_B057_D8F7C16D4E6F__INCLUDED_)
#define AFX_SAVETOFILE_H__3E06FBF6_2ECE_4F8E_B057_D8F7C16D4E6F__INCLUDED_

#include "ipscanDlg.h"	// Added by ClassView
#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

// File type constants

#define FILE_TYPE_TXT	0
#define FILE_TYPE_CSV	1
#define FILE_TYPE_XML	2
#define FILE_TYPE_HTML	3

class CSaveToFile  
{
public:	
	BOOL m_bAppend;
	BOOL m_saveselection;
	CString m_filename;
	int m_filetype;	
	CIpscanDlg *m_dlg;

	void saveToCSV(FILE *fileHandle);
	void saveToTXT(FILE *fileHandle);
	void saveToHTML(FILE *fileHandle);
	void saveToXML(FILE *fileHandle);
	BOOL queryFilename();

	CSaveToFile(CIpscanDlg *dlg, BOOL bSaveSelection = FALSE, LPSTR szFileName = NULL, int nFileFormat = -1, BOOL bAppend = FALSE);
	virtual ~CSaveToFile();

};

#endif // !defined(AFX_SAVETOFILE_H__3E06FBF6_2ECE_4F8E_B057_D8F7C16D4E6F__INCLUDED_)
