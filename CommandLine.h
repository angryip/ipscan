// CommandLine.h: interface for the CCommandLine class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_)
#define AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CCommandLine 
{
public:
	CString m_szFilename;
	CString m_szEndIP;
	CString m_szStartIP;
	static void displayHelp();
	BOOL process();
	CCommandLine();
	virtual ~CCommandLine();

};

#endif // !defined(AFX_COMMANDLINE_H__EF4B749C_E040_4A94_96F5_BDB578B61725__INCLUDED_)
