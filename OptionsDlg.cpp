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
	m_port = 0;
	m_delay = 0;
	m_resolve = FALSE;
	m_scanport = FALSE;
	m_retrifdead = FALSE;
	m_maxthreads = 0;
	m_timeout = 0;
	m_portondead = FALSE;
	m_portondead = FALSE;
	m_display = 0;
	//}}AFX_DATA_INIT
}


void COptionsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(COptionsDlg)
	DDX_Control(pDX, IDC_CHECKPORTONDEAD, m_ondeadctl);
	DDX_Control(pDX, IDC_CHECK1, m_resolvectl);
	DDX_Control(pDX, IDC_CHECK2, m_scanportctl);
	DDX_Control(pDX, IDC_EDIT1, m_portctl);
	DDX_Text(pDX, IDC_EDIT1, m_port);
	DDV_MinMaxUInt(pDX, m_port, 1, 65535);
	DDX_Text(pDX, IDC_EDIT2, m_delay);
	DDV_MinMaxInt(pDX, m_delay, 5, 10000);
	DDX_Check(pDX, IDC_CHECK1, m_resolve);
	DDX_Check(pDX, IDC_CHECK2, m_scanport);
	DDX_Check(pDX, IDC_CHECK3, m_retrifdead);
	DDX_Text(pDX, IDC_THREADS, m_maxthreads);
	DDV_MinMaxUInt(pDX, m_maxthreads, 1, 1000);
	DDX_Text(pDX, IDC_TIMEOUT, m_timeout);
	DDV_MinMaxUInt(pDX, m_timeout, 500, 60000);
	DDX_Check(pDX, IDC_CHECKPORTONDEAD, m_portondead);
	DDX_Radio(pDX, IDC_RADIO1, m_display);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(COptionsDlg, CDialog)
	//{{AFX_MSG_MAP(COptionsDlg)
	ON_BN_CLICKED(IDC_CHECK2, OnCheck2)
	ON_WM_SHOWWINDOW()
	ON_BN_CLICKED(IDC_CHECK1, OnCheck1)
	ON_BN_CLICKED(IDC_HELPBTN, OnHelpbtn)
	ON_EN_CHANGE(IDC_EDIT1, OnChangeEdit1)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// COptionsDlg message handlers

void COptionsDlg::OnOK() 
{
	// TODO: Add extra validation here
	
	CDialog::OnOK();
}


int COptionsDlg::DoModal() 
{
	// TODO: Add your specialized code here and/or call the base class
	
	return CDialog::DoModal();
}

void COptionsDlg::OnCheck2() 
{
	// TODO: Add your control notification handler code here
	if (m_scanportctl.GetCheck()) {
		m_portctl.EnableWindow(TRUE); 
		m_ondeadctl.EnableWindow(TRUE);
	} else {
		m_portctl.EnableWindow(FALSE);
		m_ondeadctl.EnableWindow(FALSE);
	}
}

void COptionsDlg::OnShowWindow(BOOL bShow, UINT nStatus) 
{
	CDialog::OnShowWindow(bShow, nStatus);
	
	// TODO: Add your message handler code here
	OnCheck2();	
	OnCheck1();	
}

void COptionsDlg::OnCheck1() 
{
	// TODO: Add your control notification handler code here
	if (m_resolvectl.GetCheck()) 
		GetDlgItem(IDC_CHECK3)->EnableWindow(TRUE); 
	else {
		((CButton*)GetDlgItem(IDC_CHECK3))->SetCheck(FALSE);
		GetDlgItem(IDC_CHECK3)->EnableWindow(FALSE);
	}	
}

void COptionsDlg::OnHelpbtn() 
{
	// TODO: Add your control notification handler code here
	MessageBox(
		"Options help:\n\n"
		"Timer delay:\n"
		"\tDelay between activating of two sequental threads.\n"
		"\tIn another words, delay between appearing of two IPs\n"
		"\tin the listbox (with \"?\" icons, cause they are\n"
		"\tnot scanned yet, scanning job is done by threads)\n"
		"Max threads:\n"
		"\tMaximum number of active threads\n"
		"Ping timeout:\n"
		"\tIf this timeout is passed and host is not sent any\n"
		"\tdata back yet, it is considered \"dead\"\n"
		"Display options:\n"
		"\tSelect what addresses you want to be displayed in the window:\n"
		"\tAll IPs, only alive IPs or only those with the open port.\n"
		"Resolve host names:\n"
		"\tQuery DNS for the name of the computer being scanned\n"
		"Try to ... if dead:\n"
		"\tThis is possible because DNS server is always running,\n"
		"\tso you can get hostnames of machines in LAN even if \n"
		"\tall the computers are turned off at the moment.\n"
		"Check for open port:\n"
		"\tTry to connect to specified TCP port on scanned machine,\n"
		"\tfor example, 139 - NetBios session\n"
		"On dead hosts:\n"
		"\tSome firewalls don't respond to ICMP queries to prevent\n"
		"\tso-called Ping-Of-Death, but ports possibly can be scanned\n"
		,NULL,
		MB_OK | MB_ICONINFORMATION
	);
	
}

void COptionsDlg::OnChangeEdit1() 
{
	/*CString str; GetDlgItemText(IDC_EDIT1,str);
	SERVENT *se = getservbyport(atoi(str),0); 
	if (se==NULL) {
		char tmp[20]; itoa(WSAGetLastError(),(char*)&tmp,10);
		SetDlgItemText(IDC_EDIT3,(char*)&tmp);
	} else {
		SetDlgItemText(IDC_EDIT3,se->s_name);
	}*/
}


void COptionsDlg::saveOptions(CIpscanDlg *d)
{
	CWinApp *app = AfxGetApp();

	CString szURL; szURL.LoadString(IDS_HOMEPAGE);
	app->WriteProfileString("", "URL", szURL);	
	app->WriteProfileInt("","Delay",d->m_delay);
	app->WriteProfileInt("","MaxThreads",d->m_maxthreads);
	app->WriteProfileInt("","ScanPort",d->m_scanport);
	app->WriteProfileInt("","PortNum",d->m_port);
	app->WriteProfileInt("","RetrIfDead",d->m_retrifdead);
	app->WriteProfileInt("","PortOnDead",d->m_portondead);
	app->WriteProfileInt("","Timeout",d->m_timeout);
	app->WriteProfileInt("","GetHostname",d->m_resolve);	
	app->WriteProfileInt("","DisplayOptions",d->m_display);	
	RECT rc;
	d->GetWindowRect(&rc);
	app->WriteProfileInt("","Left",rc.left);
	app->WriteProfileInt("","Top",rc.top);
	app->WriteProfileInt("","Bottom",rc.bottom);
	app->WriteProfileInt("","Right",rc.right);
	CString str;
	for (int i=0; i < C_COLUMNS; i++) {
		str.Format("Col%d",i);
		app->WriteProfileInt("",str,d->m_list.GetColumnWidth(i));
	}
}

void COptionsDlg::loadOptions(CIpscanDlg *d)
{
	CWinApp *app = AfxGetApp();

	d->m_resolve = app->GetProfileInt("","GetHostName",TRUE);
	d->m_delay = app->GetProfileInt("","Delay",20);
	d->m_maxthreads = app->GetProfileInt("","MaxThreads",100);
	d->m_scanport = app->GetProfileInt("","ScanPort",FALSE);
	d->m_port = app->GetProfileInt("","PortNum",139);
	d->m_retrifdead = app->GetProfileInt("","RetrIfDead",FALSE);
	d->m_portondead = app->GetProfileInt("","PortOnDead",FALSE);
	d->m_timeout = app->GetProfileInt("","Timeout",5000);
	d->m_display = app->GetProfileInt("","DisplayOptions",0);
}
