// RandomIPFeedDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "RandomIPFeedDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CRandomIPFeedDlg dialog


CRandomIPFeedDlg::CRandomIPFeedDlg(CWnd* pParent /*=NULL*/)
	: CAbstractIPFeedDlg(CRandomIPFeedDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CRandomIPFeedDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CRandomIPFeedDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CRandomIPFeedDlg)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CRandomIPFeedDlg, CDialog)
	//{{AFX_MSG_MAP(CRandomIPFeedDlg)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRandomIPFeedDlg message handlers

CString CRandomIPFeedDlg::serialize()
{
	CString szResult = "N/A";

	return szResult;
}
	
BOOL CRandomIPFeedDlg::unserialize(const CString& szSettings)
{
	return FALSE;
}

// Creates an IP feed object
CAbstractIPFeed * CRandomIPFeedDlg::createIPFeed()
{
	return NULL;
}
