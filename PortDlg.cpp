// PortDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "PortDlg.h"
#include "Scanner.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CPortDlg dialog


CPortDlg::CPortDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CPortDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CPortDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CPortDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CPortDlg)
	DDX_Control(pDX, IDC_PORT_LISTBOX, m_ctPortListBox);
	DDX_Control(pDX, IDC_PORT_STRING, m_ctPortString);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CPortDlg, CDialog)
	//{{AFX_MSG_MAP(CPortDlg)
	ON_EN_CHANGE(IDC_PORT_STRING, OnChangePortString)
	ON_BN_CLICKED(IDC_BUTTON_ADD_PORT, OnButtonAddSinglePort)
	ON_BN_CLICKED(IDC_BUTTON_ADD_PORT_RANGE, OnButtonAddPortRange)
	ON_BN_CLICKED(IDC_BUTTON_TIP, OnButtonTip)
	ON_CBN_SELCHANGE(IDC_PORT_LISTBOX, OnSelchangePortListbox)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CPortDlg message handlers

int g_nOldPortsLength = 0;

void CPortDlg::OnChangePortString() 
{
	CString szOld, szNew = "";
	m_ctPortString.GetWindowText(szOld);

	DWORD nCursorPos = m_ctPortString.GetSel();

	if (g_nOldPortsLength == szOld.GetLength())
		return;

	TCHAR chCur, chPrev = ',';

	// Validate
	for (int i=0; i < szOld.GetLength(); i++)
	{
		chCur = szOld.GetAt(i);

		if (chCur == ';') chCur = ',';

		if (chPrev >= '0' && chPrev <= '9')
		{
			if (chCur == ',')
				szNew += chCur;
			else
			if (chCur == '-')
				szNew += chCur;
			else
			if (chCur >= '0' && chCur <= '9')
				szNew += chCur;
		}
		else
		if ((chPrev == ',' || chPrev == '-') && chCur >= '0' && chCur <= '9') 
			szNew += chCur;
		
		chPrev = chCur;
	}

	g_nOldPortsLength = szNew.GetLength();

	m_ctPortString.SetWindowText(szNew);

	m_ctPortString.SetSel(nCursorPos);

}


void CPortDlg::OnButtonAddSinglePort() 
{
	CString szPort, szString;
	GetDlgItemText(IDC_SINGLE_PORT, szPort);
	m_ctPortString.GetWindowText(szString);	

	int nLastPos = szString.GetLength() - 1;
	
	if (nLastPos >= 0 && szString.GetAt(nLastPos) == '-')
		szString.Delete(nLastPos);

	/*if (szString.Find(szPort + ',') >= 0 || szString.Mid(nLastPos - szPort.GetLength() + 1) == szPort)
	{
		MessageBox("This port already exists in the port string");
		return;
	}*/

	m_ctPortString.SetWindowText(szString + "," + szPort);
	
}

void CPortDlg::OnButtonAddPortRange() 
{
	CString szFrom, szTo, szString;	

	if (GetDlgItemInt(IDC_FROM_PORT) > GetDlgItemInt(IDC_TO_PORT))
	{
		MessageBox("Starting port is greater than ending");
		return;
	}

	GetDlgItemText(IDC_FROM_PORT, szFrom);
	GetDlgItemText(IDC_TO_PORT, szTo);
	m_ctPortString.GetWindowText(szString);	

	int nLastPos = szString.GetLength() - 1;
	
	if (nLastPos >= 0 && szString.GetAt(nLastPos) == '-')
		szString.Delete(nLastPos);	

	m_ctPortString.SetWindowText(szString + ',' + szFrom + '-' + szTo);
}

void CPortDlg::OnButtonTip() 
{
	MessageBox(
		"Port String format:\n\n"
		"It is a comma-separated (\",\") list of port numbers or ranges.\n"
		"Ranges should include a minus character (\"-\") between starting and ending ports\n\n"
		"Example: \"1,5,21,80,100-115,125,1024-2000,6660\""
	);	
}

BOOL CPortDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	m_ctPortString.SetWindowText(g_options->m_szPorts);
	
	return TRUE;  	              
}

void CPortDlg::OnSelchangePortListbox() 
{
	CString szPortName;
	GetDlgItemText(IDC_PORT_LISTBOX, szPortName);
	servent *pServent = getservbyname(szPortName, NULL);
	
	u_short nPort = 0;

	if (pServent)
	{
		nPort = ntohs(pServent->s_port);		
	}
	else
	{
		if (szPortName.Compare("ssh") == 0)
			nPort = 22;
		else if (szPortName.Compare("x11") == 0)
			nPort = 6000;
		else if (szPortName.Compare("r3c") == 0)
			nPort = 9870;
		else if (szPortName.Compare("netbus") == 0)
			nPort = 12345;
		else if (szPortName.Compare("mysql") == 0)
			nPort = 3306;
		else if (szPortName.Compare("oracle") == 0)
			nPort = 1521;
		else if (szPortName.Compare("cvs") == 0)
			nPort = 2401;		
		else if (szPortName.Compare("http-proxy") == 0)
			nPort = 8080;		
		else if (szPortName.Compare("squid") == 0)
			nPort = 3128;		
	}

	SetDlgItemInt(IDC_SINGLE_PORT, nPort);
}

void CPortDlg::OnOK() 
{
	CString szPortString, szOldPortString;
	m_ctPortString.GetWindowText(szPortString);

	szOldPortString = g_options->m_szPorts;
	g_options->setPortString(szPortString);
	
	if (g_options->parsePortString())
	{
		CDialog::OnOK();
	}
	else
	{
		g_options->setPortString(szOldPortString);
	}
}

void CPortDlg::OnCancel() 
{
	// Parse old string, as it may have broken by unsuccessful OnOK()
	g_options->parsePortString();	
	CDialog::OnCancel();
}
