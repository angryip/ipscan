// PortDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "PortDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CPortDlg dialog


CPortDlg::CPortDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CPortDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CPortDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CPortDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CPortDlg)
	DDX_Control(pDX, IDC_PORT_STRING, m_ctPortString);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CPortDlg, CDialog)
	//{{AFX_MSG_MAP(CPortDlg)
	ON_EN_CHANGE(IDC_PORT_STRING, OnChangePortString)
	ON_EN_UPDATE(IDC_PORT_STRING, OnUpdatePortString)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CPortDlg message handlers

int g_nOldPortsLength = 0;

void CPortDlg::OnChangePortString() 
{
	CString szOld, szNew = "";
	m_ctPortString.GetWindowText(szOld);

	if (g_nOldPortsLength == szOld.GetLength())
		return;

	TCHAR chCur, chPrev = ',';

	// Validate
	for (int i=0; i < szOld.GetLength(); i++)
	{
		chCur = szOld.GetAt(i);

		if (chCur == ';') chCur = ',';

		if (chCur > '0' && chCur < '9' && (chPrev == ',' || chPrev == '-')) 
			szNew += chCur;
		else 
		if (chCur == ',' && (chPrev > '0' && chPrev < '9'))
			szNew += chCur;
		else
		if (chCur == '-' && (chPrev > '0' && chPrev < '9'))
			szNew += chCur;
	}

	g_nOldPortsLength = szNew.GetLength();

	m_ctPortString.SetWindowText(szNew);

}

void CPortDlg::OnUpdatePortString() 
{
	
}
