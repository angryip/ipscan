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

// IPRangeDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "AbstractIPFeedDlg.h"
#include "IpscanDlg.h"
#include "AbstractIPFeed.h"
#include "IPRangeIPFeed.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CIPRangeDlg dialog


CAbstractIPFeedDlg::CAbstractIPFeedDlg(int nIDD, CWnd* pParent /*=NULL*/)
	: CDialog(nIDD, pParent)
{
	//{{AFX_DATA_INIT(CAbstractIPFeedDlg)
	//}}AFX_DATA_INIT
}

BEGIN_MESSAGE_MAP(CAbstractIPFeedDlg, CDialog)
	//{{AFX_MSG_MAP(CAbstractIPFeedDlg)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIPRange message handlers

BOOL CAbstractIPFeedDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	// Initialize tooltips
	m_pToolTips = new CToolTipCtrl;
	m_pToolTips->Create(this);
	
	return TRUE;  
}

void CAbstractIPFeedDlg::OnDestroy() 
{
	CDialog::OnDestroy();	
	
	if (m_pToolTips != NULL)
		delete(m_pToolTips);
}

BOOL CAbstractIPFeedDlg::PreTranslateMessage(MSG* pMsg) 
{
	if (m_pToolTips != NULL)
		m_pToolTips->RelayEvent(pMsg);

	return CDialog::PreTranslateMessage(pMsg);
}

CWnd * CAbstractIPFeedDlg::SetFocus()
{
	// Set focus to the first control
	GetNextDlgTabItem(this)->SetFocus();
	return this;
}
