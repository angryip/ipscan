// OptionsDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "OptionsDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// COptionsDlg dialog


COptionsDlg::COptionsDlg(CWnd* pParent /*=NULL*/)
	: CDialog(COptionsDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(COptionsDlg)
	m_nTimerDelay = 0;
	m_nMaxThreads = 0;
	m_nPingTimeout = 0;
	m_nDisplayOptions = 0;
	m_bScanHostIfDead = FALSE;
	//}}AFX_DATA_INIT
}


void COptionsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(COptionsDlg)
	DDX_Text(pDX, IDC_EDIT2, m_nTimerDelay);
	DDV_MinMaxInt(pDX, m_nTimerDelay, 5, 10000);
	DDX_Text(pDX, IDC_THREADS, m_nMaxThreads);
	DDV_MinMaxUInt(pDX, m_nMaxThreads, 1, 1000);
	DDX_Text(pDX, IDC_TIMEOUT, m_nPingTimeout);
	DDV_MinMaxUInt(pDX, m_nPingTimeout, 500, 60000);
	DDX_Radio(pDX, IDC_RADIO1, m_nDisplayOptions);
	DDX_Check(pDX, IDC_SCAN_HOST_IF_DEAD, m_bScanHostIfDead);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(COptionsDlg, CDialog)
	//{{AFX_MSG_MAP(COptionsDlg)	
	ON_BN_CLICKED(IDC_HELPBTN, OnHelpbtn)	
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// COptionsDlg message handlers

void COptionsDlg::OnOK() 
{
	CDialog::OnOK();

	g_options->m_nMaxThreads = m_nMaxThreads;
	g_options->m_nPingTimeout = m_nPingTimeout;
	g_options->m_nTimerDelay = m_nTimerDelay;
	g_options->m_neDisplayOptions = m_nDisplayOptions;
	g_options->m_bScanHostIfDead = m_bScanHostIfDead;
}


int COptionsDlg::DoModal() 
{
	m_nMaxThreads = g_options->m_nMaxThreads;
	m_nPingTimeout = g_options->m_nPingTimeout;
	m_nTimerDelay = g_options->m_nTimerDelay;
	m_nDisplayOptions = g_options->m_neDisplayOptions;
	m_bScanHostIfDead = g_options->m_bScanHostIfDead;

	return CDialog::DoModal();
}


void COptionsDlg::OnHelpbtn() 
{
	// TODO: Add your control notification handler code here
	MessageBox(
		"Options help:\n\n"
		"Timer delay:\n"
		"\tDelay between activating of two sequental threads.\n"
		"\tIn another words, delay between appearing of two IPs\n"
		"\tin the listbox\n"		
		"Max threads:\n"
		"\tMaximum number of active threads\n"
		"Ping timeout:\n"
		"\tIf this timeout is elapsed and host is not sent any\n"
		"\tdata back, it is considered \"dead\"\n"
		"Display options:\n"
		"\tSelect what addresses you want to be displayed in the window:\n"
		"\tAll IPs, only alive IPs or only those with the open port.\n"		
		"Continue scanning...:\n"
		"\tSome firewalls don't respond to ICMP queries, so host is\n"
		"\t illegally considered \"dead\". This option will scan it anyway\n"
		"\t(and all other dead hosts)\n"
		,NULL,
		MB_OK | MB_ICONINFORMATION
	);
	
}


BOOL COptionsDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();	
	
	return TRUE;  
}
