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
#include "IPRangeDlg.h"
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


CIPRangeDlg::CIPRangeDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CIPRangeDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CIPRange)
	m_szHostname = _T("");
	//}}AFX_DATA_INIT
}


void CIPRangeDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CIPRange)
	DDX_Control(pDX, IDC_IPADDRESS2, m_ctIPEnd);
	DDX_Control(pDX, IDC_IPADDRESS1, m_ctIPStart);
	DDX_Control(pDX, IDC_BUTTONIPUP, m_btnIPUp);
	DDX_Text(pDX, IDC_HOSTNAME, m_szHostname);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CIPRangeDlg, CDialog)
	//{{AFX_MSG_MAP(CIPRangeDlg)
	ON_BN_CLICKED(IDC_BUTTONIPUP, OnButtonipup)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS1, OnFieldchangedIpaddress1)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS2, OnFieldchangedIpaddress2)
	ON_BN_CLICKED(IDC_CLASS_C, OnClassC)
	ON_BN_CLICKED(IDC_CLASS_D, OnClassD)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIPRange message handlers

BOOL CIPRangeDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	// Initialize controls
	m_bIp2Virgin = TRUE;
	m_ctIPStart.SetWindowText("0.0.0.0");

	// Init hostname
	char szHostname[256];
	gethostname((char *)&szHostname, 100);
	SetDlgItemText(IDC_HOSTNAME, szHostname);

	// Set button bitmaps
	CBitmap *tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_UPARROW);
	((CButton*)GetDlgItem(IDC_BUTTONIPUP))->SetBitmap((HBITMAP) tmpbmp->m_hObject);

	tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_PASTE);
	((CButton*)GetDlgItem(IDC_BUTTONPASTE))->SetBitmap((HBITMAP) tmpbmp->m_hObject);

	tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_C_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_C))->SetBitmap((HBITMAP) tmpbmp->m_hObject);

	tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_D_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_D))->SetBitmap((HBITMAP) tmpbmp->m_hObject);

	// Process change event
	OnFieldchangedIpaddress1(NULL, NULL);
	
	return TRUE;  
}

void CIPRangeDlg::OnButtonipup() 
{
	((CIpscanDlg*)g_dlg)->status("Getting IP...");

	hostent *he;
	char str[100];
	char *addr;
	in_addr in;
	GetDlgItemText(IDC_HOSTNAME,str,100);
	he = gethostbyname(str);

	((CIpscanDlg*)g_dlg)->status(NULL);

	if (!he) 
	{
		MessageBox("No DNS entry",NULL,MB_OK | MB_ICONHAND);
		return;
	}
	memcpy(&in.S_un.S_addr,*he->h_addr_list,sizeof(long));
	addr = inet_ntoa(in);
	m_ctIPStart.SetWindowText(addr);
	m_ctIPEnd.SetWindowText(addr);
	m_bIp2Virgin = TRUE;	
}

void CIPRangeDlg::OnFieldchangedIpaddress1(NMHDR* pNMHDR, LRESULT* pResult) 
{	
	if (m_bIp2Virgin) 
	{
		char str[16];	
		m_ctIPStart.GetWindowText((char*)&str,sizeof(str));
		m_ctIPEnd.SetWindowText((char*)&str);
	}
	
	if (pResult)
		*pResult = 0;
}

void CIPRangeDlg::OnFieldchangedIpaddress2(NMHDR* pNMHDR, LRESULT* pResult) 
{
	m_bIp2Virgin = FALSE;
	
	if (pResult)
		*pResult = 0;
}

void CIPRangeDlg::OnClassC() 
{
	DWORD ip;
	char *ipc = (char*)&ip;
	m_ctIPStart.GetAddress(ip); ipc[0] = (char) 1; m_ctIPStart.SetAddress(ip);
	m_ctIPEnd.GetAddress(ip); ipc[0] = (char) 255; m_ctIPEnd.SetAddress(ip);
	m_bIp2Virgin=FALSE;
}

void CIPRangeDlg::OnClassD() 
{
	DWORD ip;
	char *ipc = (char*)&ip;
	m_ctIPStart.GetAddress(ip); ipc[0] = (char) 1; ipc[1] = (char) 0; m_ctIPStart.SetAddress(ip);
	m_ctIPEnd.GetAddress(ip); ipc[0] = (char) 255; ipc[1] = (char) 255; m_ctIPEnd.SetAddress(ip);
	m_bIp2Virgin=FALSE;
}


// Creates an IP feed object
CAbstractIPFeed * CIPRangeDlg::createIPFeed()
{
	char str[16];
	
	m_ctIPStart.GetWindowText((char *)&str,16);
	IPAddress nStartIP = ntohl(inet_addr((char *)&str));

	m_ctIPEnd.GetWindowText((char *)&str,16);
	IPAddress nEndIP = ntohl(inet_addr((char *)&str));

	if (nEndIP < nStartIP) 
	{
		MessageBox("Ending IP address is lower than starting.", NULL, MB_OK | MB_ICONHAND);
		return NULL;
	}

	return new CIPRangeIPFeed(nStartIP, nEndIP);
}

CWnd * CIPRangeDlg::SetFocus()
{
	return m_ctIPStart.SetFocus();
}