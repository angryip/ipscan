// Scanner.h: interface for the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
#define AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

// Function type definitions

typedef BOOL (__cdecl TScanFunction)(DWORD nIP, LPSTR szReturn, int nBufferLen);

typedef BOOL (__cdecl TInitFunction)();


class CScanner  
{
public:
	void initListColumns(CListCtrl *cListCtrl);
	void loadSettings();
	int getColumnWidth(int nIndex);
	BOOL getColumnName(int nIndex, CString &szColumnHeader);
	int getColumnCount();
	CScanner();
	virtual ~CScanner();

protected:
	TInitFunction * m_pInitFunctions[100];
	TScanFunction * m_pScanFunctions[100];
	CWinApp * m_app;
	CString * m_pszColumnNames[100];
	int m_nColumnCount;
};

// ordinary function
UINT ScanningThread(LPVOID cur_ip);

extern int g_nThreadCount;
extern HANDLE g_hThreads[10000];
extern CDialog * g_dlg;

#endif // !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
