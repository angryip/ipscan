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

// NetBIOSOptions.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "NetBIOSOptions.h"
#include "NetBIOSUtils.h"
#include <nb30.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CNetBIOSOptions dialog


CNetBIOSOptions::CNetBIOSOptions(CWnd* pParent /*=NULL*/)
	: CDialog(CNetBIOSOptions::IDD, pParent)
{
	//{{AFX_DATA_INIT(CNetBIOSOptions)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CNetBIOSOptions::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CNetBIOSOptions)
	DDX_Control(pDX, IDC_LANA, m_ctlLanaList);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CNetBIOSOptions, CDialog)
	//{{AFX_MSG_MAP(CNetBIOSOptions)
	ON_WM_SHOWWINDOW()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CNetBIOSOptions message handlers

BOOL CNetBIOSOptions::OnInitDialog() 
{
	CDialog::OnInitDialog();	

	m_bCalledOnInit = FALSE;
	
	return TRUE;  
}

void CNetBIOSOptions::OnOK() 
{
	AfxGetApp()->WriteProfileInt("", "LanaNumber", m_ctlLanaList.GetItemData(m_ctlLanaList.GetCurSel()));
	
	CDialog::OnOK();
}

void CNetBIOSOptions::CalledOnInit()
{
	GetDlgItem(IDCANCEL)->EnableWindow(FALSE);

	m_bCalledOnInit = TRUE;
}

void CNetBIOSOptions::OnShowWindow(BOOL bShow, UINT nStatus) 
{
	CDialog::OnShowWindow(bShow, nStatus);
	
	// Load LANA numbers		
	CNetBIOSUtils cNetBIOSUtils(FALSE);

	LANA_ENUM lanaEnum;

	int nCurrentLana = AfxGetApp()->GetProfileInt("", "LanaNumber", -1);

	cNetBIOSUtils.GetLanaNumbers(&lanaEnum);	
	CString szNumber;

	for (int i=0; i < lanaEnum.length; i++)
	{
		szNumber.Format("%d", lanaEnum.lana[i]);
		m_ctlLanaList.AddString(szNumber);
		m_ctlLanaList.SetItemData(i, lanaEnum.lana[i]);
		
		if (nCurrentLana == lanaEnum.lana[i])
			m_ctlLanaList.SetCurSel(i);
	}

	if (nCurrentLana < 0)
		m_ctlLanaList.SetCurSel(0);
	
}
