/*********************************************************************
 * This is a part of Angry IP Scanner source code                    *
 * http://www.angryziber.com/ipscan/                                 *
 *                                                                   *
 * Written by Angryziber                                             *
 *                                                                   *
 * You may distribute this code as long as this message is not       *
 * removed and it is clear who has written it.                       *
 * You may not rename the program and distribute it.                 *
 *********************************************************************/

// QueryDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "QueryDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CQueryDlg dialog


CQueryDlg::CQueryDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CQueryDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CQueryDlg)
	//}}AFX_DATA_INIT
}


void CQueryDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CQueryDlg)
	DDX_Control(pDX, IDC_USER_TEXT, m_ctrlUserText);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CQueryDlg, CDialog)
	//{{AFX_MSG_MAP(CQueryDlg)
	ON_WM_SHOWWINDOW()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CQueryDlg message handlers

int CQueryDlg::DoModal() 
{		
	return CDialog::DoModal();
}

CString CQueryDlg::doQuery()
{
	CString szReturn;

	if (DoModal() == IDOK)
	{
		m_ctrlUserText.GetWindowText(szReturn);		
	}

	return szReturn;
}

BOOL CQueryDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();

	if (m_szCaption.GetLength() > 0)
		SetWindowText(m_szCaption);
	
	SetDlgItemText(IDC_QUERY_TEXT, m_szQueryText);

	m_ctrlUserText.SetWindowText(m_szDefaultUserText);
	
	return TRUE;  
}


