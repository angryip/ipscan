// CommandLine.h: interface for the CCommandLine class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_)
#define AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

// Command-line options
#define CMDO_START_SCAN		1
#define CMDO_NOT_EXIT		2
#define CMDO_SAVE_TO_FILE	4
#define CMDO_APPEND_FILE	16


class CCommandLine 
{
public:
	int m_nFileFormat;
	int m_nOptions;
	CString m_szFilename;
	CString m_szEndIP;
	CString m_szStartIP;
	static void displayHelp();
	BOOL process();
	CCommandLine();
	virtual ~CCommandLine();

};

#endif // !defined(AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_)
