// SearchDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "SearchDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSearchDlg dialog


CSearchDlg::CSearchDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSearchDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSearchDlg)
	m_case = FALSE;
	m_search = _T("");
	m_beginning = FALSE;
	//}}AFX_DATA_INIT
}


void CSearchDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSearchDlg)
	DDX_Check(pDX, IDC_CASESENSITIVE, m_case);
	DDX_Text(pDX, IDC_SEARCHEDIT, m_search);
	DDX_Check(pDX, IDC_CHECK1, m_beginning);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSearchDlg, CDialog)
	//{{AFX_MSG_MAP(CSearchDlg)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSearchDlg message handlers

void CSearchDlg::OnOK() 
{
	CDialog::OnOK();
}
