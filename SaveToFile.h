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

#define FILE_TYPE_TXT 0
#define FILE_TYPE_CSV 1

class CSaveToFile  
{
public:
	BOOL m_saveselection;
	CString m_filename;
	int m_filetype;	
	CIpscanDlg *m_dlg;

	void saveToCSV();
	void saveToTXT();
	BOOL queryFilename();

	CSaveToFile(CIpscanDlg *dlg, BOOL bSaveSelection = FALSE, LPSTR szFileName = NULL, BOOL bCSV = FALSE);
	virtual ~CSaveToFile();

};

#endif // !defined(AFX_SAVETOFILE_H__3E06FBF6_2ECE_4F8E_B057_D8F7C16D4E6F__INCLUDED_)
