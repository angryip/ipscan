// SelectColumnsDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "SelectColumnsDlg.h"
#include "Scanner.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSelectColumnsDlg dialog


CSelectColumnsDlg::CSelectColumnsDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSelectColumnsDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSelectColumnsDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CSelectColumnsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSelectColumnsDlg)
	DDX_Control(pDX, IDC_SELECTED_COLUMNS, m_ctSelectedColumns);
	DDX_Control(pDX, IDC_ALL_COLUMNS, m_ctAllColumns);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSelectColumnsDlg, CDialog)
	//{{AFX_MSG_MAP(CSelectColumnsDlg)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSelectColumnsDlg message handlers

BOOL CSelectColumnsDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();

	CString szTmp;
	
	// Init list boxes
	//m_ctAllColumns.Clear();
	for (int i=0; i < g_scanner->getAllColumnsCount(); i++)
	{
		g_scanner->getAllColumnName(i, szTmp);
		m_ctAllColumns.AddString(szTmp);
	}
	
	return TRUE;  
}
