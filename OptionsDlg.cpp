// OptionsDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "OptionsDlg.h"
#include "SelectColumnsDlg.h"

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
	m_nPortTimeout = 0;
	m_bShowPortsBelow = FALSE;
	m_bScanPorts = FALSE;
	m_nPingCount = 0;
	//}}AFX_DATA_INIT
}


void COptionsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(COptionsDlg)
	DDX_Control(pDX, IDC_COLUMN_TYPE, m_statColumnType);
	DDX_Control(pDX, IDC_COLUMN_OPTIONS_BUTTON, m_btnOptionsColumn);
	DDX_Control(pDX, IDC_COLUMN_ABOUT_BUTTON, m_btnAboutColumn);
	DDX_Control(pDX, IDC_PLUGIN_OPTIONS_GROUP, m_ctPluginOptionsGroup);
	DDX_Control(pDX, IDC_PLUGIN_LIST, m_ctPluginList);
	DDX_Text(pDX, IDC_EDIT2, m_nTimerDelay);
	DDV_MinMaxInt(pDX, m_nTimerDelay, 5, 10000);
	DDX_Text(pDX, IDC_THREADS, m_nMaxThreads);
	DDV_MinMaxUInt(pDX, m_nMaxThreads, 1, 1000);
	DDX_Text(pDX, IDC_TIMEOUT, m_nPingTimeout);
	DDV_MinMaxUInt(pDX, m_nPingTimeout, 500, 60000);
	DDX_Radio(pDX, IDC_RADIO1, m_nDisplayOptions);
	DDX_Check(pDX, IDC_SCAN_HOST_IF_DEAD, m_bScanHostIfDead);
	DDX_Text(pDX, IDC_PORTTIMEOUT, m_nPortTimeout);
	DDV_MinMaxInt(pDX, m_nPortTimeout, 500, 60000);
	DDX_Check(pDX, IDC_SHOW_PORTS_BELOW, m_bShowPortsBelow);
	DDX_Check(pDX, IDC_SCAN_PORTS, m_bScanPorts);
	DDX_Text(pDX, IDC_PINGCOUNT, m_nPingCount);
	DDV_MinMaxInt(pDX, m_nPingCount, 1, 10);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(COptionsDlg, CDialog)
	//{{AFX_MSG_MAP(COptionsDlg)	
	ON_BN_CLICKED(IDC_HELPBTN, OnHelpbtn)	
	ON_LBN_SELCHANGE(IDC_PLUGIN_LIST, OnSelchangePluginList)
	ON_BN_CLICKED(IDC_SELECT_COLUMNS_BTN, OnSelectColumnsBtn)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// COptionsDlg message handlers

void COptionsDlg::OnOK() 
{
	CDialog::OnOK();

	g_options->m_nMaxThreads = m_nMaxThreads;
	g_options->m_nPingTimeout = m_nPingTimeout;
	g_options->m_nPingCount = m_nPingCount;
	g_options->m_nPortTimeout = m_nPortTimeout;
	g_options->m_nTimerDelay = m_nTimerDelay;
	g_options->m_neDisplayOptions = m_nDisplayOptions;
	g_options->m_bScanHostIfDead = m_bScanHostIfDead;
	g_options->m_bShowPortsBelow = m_bShowPortsBelow;
	g_options->m_bScanPorts = m_bScanPorts;	
}


int COptionsDlg::DoModal() 
{
	m_nMaxThreads = g_options->m_nMaxThreads;
	m_nPingTimeout = g_options->m_nPingTimeout;
	m_nPingCount = g_options->m_nPingCount;
	m_nPortTimeout = g_options->m_nPortTimeout;
	m_nTimerDelay = g_options->m_nTimerDelay;
	m_nDisplayOptions = g_options->m_neDisplayOptions;
	m_bScanHostIfDead = g_options->m_bScanHostIfDead;
	m_bShowPortsBelow = g_options->m_bShowPortsBelow;
	m_bScanPorts = g_options->m_bScanPorts;

	return CDialog::DoModal();
}


void COptionsDlg::OnHelpbtn() 
{	
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
		"Ping count:\n"
		"\tHost is pinged this number of times and results are averaged.\n"		
		"\tNote that maximum pinging time will be Ping timeout * Ping count\n"
		"Port timeout:\n"
		"\tIf this timeout has elapsed and connection is not yet estabilished,\n"		
		"\tthen the port is considered \"closed\"\n"
		"Display options:\n"
		"\tSelect what addresses you want to be displayed in the window:\n"
		"\tAll IPs, only alive IPs or only those with the open port.\n"		
		"Ports on 2nd row:\n"
		"\tIf enabled (and port scanning also enabled), then scanned ports will\n"
		"\tbe displayed below each IP address in the list\n"
		"Continue scanning...:\n"
		"\tSome hosts don't respond to ICMP queries, so host is\n"
		"\t illegally considered \"dead\". This option will scan it anyway\n"
		"\t(and all other dead hosts)\n"
		,NULL,
		MB_OK | MB_ICONINFORMATION
	);
	
}


BOOL COptionsDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();

	int nColumnCount = g_scanner->getAllColumnsCount();
	CString szPluginName;

	for (int i=CL_STATIC_COUNT; i < nColumnCount; i++)
	{
		g_scanner->getAllColumnName(i, szPluginName);
		m_ctPluginList.AddString(szPluginName);
		m_ctPluginList.SetItemData(i - CL_STATIC_COUNT, i);
	}
	
	return TRUE;  
}

void COptionsDlg::OnSelchangePluginList() 
{
	// Change group box caption
	CString szPluginName;	
	int nPluginIndex = m_ctPluginList.GetItemData(m_ctPluginList.GetCurSel());
	g_scanner->getAllColumnName(nPluginIndex, szPluginName);

	m_ctPluginOptionsGroup.SetWindowText(szPluginName);

	// Enable needed controls	
	/*if (g_scanner->m_AllColumns[nPluginIndex].pInfoFunction != NULL)
		m_btnAboutColumn.EnableWindow(TRUE);
	else
		m_btnAboutColumn.EnableWindow(FALSE);*/

	m_statColumnType.SetWindowText("Built-in");	
}

void COptionsDlg::OnSelectColumnsBtn() 
{
	CSelectColumnsDlg cDlg;
	cDlg.DoModal();	
}
