// MessageDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "MessageDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CMessageDlg dialog


CMessageDlg::CMessageDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CMessageDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CMessageDlg)
	//}}AFX_DATA_INIT
}


void CMessageDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CMessageDlg)
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CMessageDlg, CDialog)
	//{{AFX_MSG_MAP(CMessageDlg)
	ON_WM_SHOWWINDOW()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CMessageDlg message handlers

void CMessageDlg::setMessageText(LPCSTR szMessage)
{
	m_szMessage = szMessage;
}

int CMessageDlg::DoModal() 
{		
	return CDialog::DoModal();
}

BOOL CMessageDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();

	SetDlgItemText(IDC_MESSAGE_TEXT, m_szMessage);	

	return TRUE;  // return TRUE unless you set the focus to a control
	              // EXCEPTION: OCX Property Pages should return FALSE
}

void CMessageDlg::OnShowWindow(BOOL bShow, UINT nStatus) 
{
	CDialog::OnShowWindow(bShow, nStatus);
	
	// The following 2 lines are the MFC(?) bug workaround
	// If this window appears when user is dragging a scrollbar
	// then it won't be in focus and won't capture keyboard/mouse events

	SetCapture();
	ReleaseCapture();
}
