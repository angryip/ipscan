// Scanner.cpp: implementation of the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "Scanner.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CScanner::CScanner()
{
	m_app = AfxGetApp();
	m_nColumnCount = 5;
	m_ColumnNames[0] = new CString("Test0");
	m_ColumnNames[1] = new CString("Test1");
	m_ColumnNames[2] = new CString("Test2");
	m_ColumnNames[3] = new CString("Test3");
	m_ColumnNames[4] = new CString("Test4");
}

CScanner::~CScanner()
{
	for (int i = 0; i < m_nColumnCount; i++)
	{
		delete m_ColumnNames[i];
	}
}

int CScanner::getColumnCount()
{
	return m_nColumnCount;
}

BOOL CScanner::getColumnName(int nIndex, CString &szColumnHeader)
{
	szColumnHeader = *m_ColumnNames[nIndex];
	return TRUE;
}

int CScanner::getColumnWidth(int nIndex)
{
	CString str;
	str.Format("Col %s Width", m_ColumnNames[nIndex]);
	int nWidth = m_app->GetProfileInt("",str,-1);
		
	if (nWidth == -1) 
		nWidth = 80;

	return nWidth;
}

void CScanner::loadSettings()
{


}	
