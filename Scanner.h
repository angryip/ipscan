// Scanner.h: interface for the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
#define AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CScanner  
{
public:
	void loadSettings();
	int getColumnWidth(int nIndex);
	BOOL getColumnName(int nIndex, CString &szColumnHeader);
	int getColumnCount();
	CScanner();
	virtual ~CScanner();

protected:
	CWinApp * m_app;
	CString * m_ColumnNames[100];
	int m_nColumnCount;
};

#endif // !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
