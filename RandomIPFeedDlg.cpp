// RandomIPFeedDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "RandomIPFeedDlg.h"
#include "RandomIPFeed.h"

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
	DDX_Control(pDX, IDC_RANDOM_IP_COUNT, m_ctIPCount);
	DDX_Control(pDX, IDC_BASE_IPADDRESS, m_ctBaseIP);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CRandomIPFeedDlg, CDialog)
	//{{AFX_MSG_MAP(CRandomIPFeedDlg)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRandomIPFeedDlg message handlers

BOOL CRandomIPFeedDlg::OnInitDialog() 
{
	CAbstractIPFeedDlg::OnInitDialog(); 
	
	// Default values
	m_ctBaseIP.SetWindowText("0.0.0.0");
	m_ctIPCount.SetWindowText("100");

	// Initialize tooltips
	m_pToolTips->AddTool(GetDlgItem(IDC_BASE_IPADDRESS), "Base IP address for generation of random IPs");
	m_pToolTips->AddTool(GetDlgItem(IDC_RANDOM_IP_COUNT), "Number of random IPs to generate.");
	m_pToolTips->Activate(TRUE);
	
	return FALSE;
}

CString CRandomIPFeedDlg::serialize()
{
	CString szResult = "N/A";

	return szResult;
}
	
BOOL CRandomIPFeedDlg::unserialize(const CString& szSettings)
{
	return FALSE;
}

BOOL CRandomIPFeedDlg::processCommandLine(const CString& szCommandLine)
{
	
	return FALSE;
}

// Creates an IP feed object
CAbstractIPFeed * CRandomIPFeedDlg::createIPFeed()
{
	char str[16];
	
	m_ctBaseIP.GetWindowText((char *)&str,16);
	IPAddress nBaseIP = ntohl(inet_addr((char *)&str));	

	m_ctIPCount.GetWindowText((char *)&str,16);

	return new CRandomIPFeed(nBaseIP, atoi((char*)&str));
}
