// ipscanDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "ipscanDlg.h"
#include "OptionsDlg.h"
#include "SearchDlg.h"
#include "InstallDlg.h"
#include "link.h"
#include "CommandLine.h"
#include "SaveToFile.h"
#include <winbase.h>
#include "MessageDlg.h"
#include "NetBIOSUtils.h"
#include "ScanUtilsInternal.h"
#include "Scanner.h"
#include "PortDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

UINT g_nListOffset,g_nStatusHeight, g_nAdvancedOffset;
CIpscanDlg* d;
CWinApp *app;

int bSortAscending = 1;
int nSortedCol = -1;

class CAboutDlg : public CDialog
{
public:	
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	CLink	m_linkForum;
	CLink	m_linkHomepage;
	CLink	m_linkEmail;
	CStatic	m_free;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	afx_msg void OnGoemail();
	afx_msg void OnGohttp();
	virtual BOOL OnInitDialog();
	afx_msg void OnAboutOK();		
	afx_msg void OnHttpForum();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	DDX_Control(pDX, IDC_HTTP_FORUM, m_linkForum);
	DDX_Control(pDX, IDC_HTTP, m_linkHomepage);
	DDX_Control(pDX, IDC_EMAIL, m_linkEmail);
	DDX_Control(pDX, IDC_TXTFREE, m_free);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
	ON_BN_CLICKED(IDC_EMAIL, OnGoemail)
	ON_BN_CLICKED(IDC_HTTP, OnGohttp)
	ON_BN_CLICKED(IDOK, OnAboutOK)
	ON_BN_CLICKED(IDC_HTTP_FORUM, OnHttpForum)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg dialog

CIpscanDlg::CIpscanDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CIpscanDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CIpscanDlg)
	m_hostname = _T("");
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_szDefaultFileName = NULL;
}

void CIpscanDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CIpscanDlg)
	DDX_Control(pDX, IDC_SCAN_PORTS, m_ctScanPorts);
	DDX_Control(pDX, IDC_WHATPORTS, m_ctWhatPorts);
	DDX_Control(pDX, IDC_LIST, m_list);
	DDX_Control(pDX, IDC_BUTTONIPUP, m_ipup);
	DDX_Control(pDX, IDC_NUMTHREADS, m_numthreads);
	DDX_Control(pDX, IDC_PROGRESS, m_progress);
	DDX_Control(pDX, IDC_IPADDRESS2, m_ip2);
	DDX_Control(pDX, IDC_IPADDRESS1, m_ip1);
	DDX_Control(pDX, IDC_STATUS, m_statusctl);
	DDX_Text(pDX, IDC_HOSTNAME, m_hostname);
	DDV_MaxChars(pDX, m_hostname, 100);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CIpscanDlg, CDialog)
	//{{AFX_MSG_MAP(CIpscanDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_SIZE()
	ON_COMMAND(ID_IP_EXIT, OnIpExit)
	ON_BN_CLICKED(IDC_BUTTON1, OnButton1)
	ON_COMMAND(ID_HELP_ABOUT, OnHelpAbout)
	ON_COMMAND(ID_OPTIONS_OPTIONS, OnOptionsOptions)
	ON_BN_CLICKED(IDC_BUTTONIPUP, OnButtonipup)
	ON_WM_TIMER()
	ON_COMMAND(ID_SCAN_SAVETOTXT, OnScanSavetotxt)
	ON_NOTIFY(NM_RCLICK, IDC_LIST, OnRclickList)
	ON_COMMAND(ID__OPENCOMPUTERINEXPLORER, OnOpencomputerinexplorer)
	ON_COMMAND(ID__SHOWERRORDESCRIPTION, OnShowerrordescription)
	ON_COMMAND(ID_WINDOZESUCKS_IPCLIPBOARD, OnWindozesucksIpclipboard)
	ON_COMMAND(ID_WINDOZESUCKS_HOSTNAMECLIPBOARD, OnWindozesucksHostnameclipboard)
	ON_COMMAND(ID_SCAN_SAVESELECTION, OnScanSaveselection)
	ON_WM_SHOWWINDOW()
	ON_COMMAND(ID_OPTIONS_SAVEOPTIONS, OnOptionsSaveoptions)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS1, OnFieldchangedIpaddress1)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS2, OnFieldchangedIpaddress2)
	ON_BN_CLICKED(IDC_CLASS_C, OnClassC)
	ON_BN_CLICKED(IDC_CLASS_D, OnClassD)
	ON_COMMAND(ID_WINDOZESUCKS_SHOWNETBIOSINFO, OnWindozesucksShownetbiosinfo)
	ON_COMMAND(ID_HELP_ANGRYIPSCANNERWEBPAGE, OnHelpAngryipscannerwebpage)
	ON_COMMAND(ID_HELP_ANGRYZIBERSOFTWARE, OnHelpAngryzibersoftware)
	ON_COMMAND(ID_WINDOZESUCKS_RESCANIP, OnWindozesucksRescanip)
	ON_COMMAND(ID_GOTO_NEXTALIVE, OnGotoNextalive)
	ON_COMMAND(ID_GOTO_NEXTDEAD, OnGotoNextdead)
	ON_COMMAND(ID_GOTO_NEXTOPENPORT, OnGotoNextopenport)
	ON_COMMAND(ID_GOTO_NEXTCLOSEDPORT, OnGotoNextclosedport)
	ON_COMMAND(ID_GOTO_HOSTNAME, OnGotoHostname)
	ON_NOTIFY(HDN_ITEMCLICKA, 0, OnItemclickListHeader)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_ASFTP, OnCommandsOpencomputerAsftp)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_ASWEBSITE, OnCommandsOpencomputerAswebsite)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_TELNET, OnCommandsOpencomputerTelnet)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_TELNETTOSPECIFIEDPORT, OnCommandsOpencomputerTelnettospecifiedport)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_HINT, OnCommandsOpencomputerHint)
	ON_BN_CLICKED(IDC_BUTTONPASTE, OnButtonpaste)
	ON_COMMAND(ID_HELP_COMMANDLINE, OnHelpCommandline)
	ON_COMMAND(ID_HELP_FORUM, OnHelpForum)
	ON_COMMAND(ID_OPTIONS_INSTALL_PROGRAM, OnOptionsInstallProgram)
	ON_WM_DESTROY()
	ON_WM_DRAWITEM()
	ON_BN_CLICKED(IDC_BUTTON_TO_ADVANCED, OnButtonToAdvanced)
	ON_BN_CLICKED(IDC_SCAN_PORTS, OnScanPortsClicked)
	ON_NOTIFY(HDN_ITEMCLICKW, 0, OnItemclickListHeader)
	ON_BN_CLICKED(IDC_SELECT_PORTS, OnSelectPortsClicked)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

int botot;

/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg message handlers

BOOL CIpscanDlg::OnInitDialog()
{
	CDialog::OnInitDialog();	
	
	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon	

	// Load default options

	app = AfxGetApp();
	d = (CIpscanDlg*)app->m_pMainWnd;
	g_dlg = (CIpscanDlg*)app->m_pMainWnd;

	g_options = new COptions();
	g_options->load();
	g_options->setWindowPos();	

	// Add image list to the listbox
	m_imglist.Create(IDB_IMAGELIST,16,2,0xFFFFFF);
	m_list.SetImageList(&m_imglist,LVSIL_SMALL);

	// Create the scanner object
	g_scanner = new CScanner();
	
	// Add columns to the listbox
	g_scanner->initListColumns(&m_list);

	// Set button's bitmaps
	m_bmpuparrow.LoadMappedBitmap(IDB_UPARROW);
	((CButton*)GetDlgItem(IDC_BUTTONIPUP))->SetBitmap((HBITMAP)m_bmpuparrow.m_hObject);
	pastebmp.LoadMappedBitmap(IDB_PASTE);
	((CButton*)GetDlgItem(IDC_BUTTONPASTE))->SetBitmap((HBITMAP)pastebmp.m_hObject);
	startbmp.LoadMappedBitmap(IDB_BMPSTART);
	stopbmp.LoadMappedBitmap(IDB_BMPSTOP);
	killbmp.LoadMappedBitmap(IDB_BMPKILL);
	((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)startbmp.m_hObject);
	CBitmap *tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_C_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_C))->SetBitmap((HBITMAP)tmpbmp->m_hObject);
	tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_D_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_D))->SetBitmap((HBITMAP)tmpbmp->m_hObject);
	// Load bitmaps for advanced mode button
	m_bmpHideAdvanced.LoadMappedBitmap(IDB_HIDE_ADVANCED);
	m_bmpShowAdvanced.LoadMappedBitmap(IDB_SHOW_ADVANCED);
	
	// Set window size
	RECT rc;
	m_ipup.GetWindowRect(&rc); g_nListOffset = rc.bottom;
	m_ctScanPorts.GetWindowRect(&rc); g_nAdvancedOffset = rc.bottom - g_nListOffset + 5;
	m_ip1.GetWindowRect(&rc); g_nListOffset -= (rc.top-5);
	m_progress.GetWindowRect(&rc); g_nStatusHeight = rc.bottom-rc.top-2;	
	
	m_bAdvancedMode = true;	// OnButtonToAdvanced() will change this to false
	g_nListOffset += g_nAdvancedOffset; // OnButtonToAdvanced() will substract this
	OnButtonToAdvanced(); // Hide advanced controls by default
	OnScanPortsClicked();	
	
	status("Ready");

	// Init hostname
	char hn[100];
	gethostname((char *)&hn,100);
	SetDlgItemText(IDC_HOSTNAME,hn);

	m_scanning=FALSE;

	// Load menu
	mnu.LoadMenu(IDR_MENU1);
	ctx_item = mnu.GetSubMenu(2);
	ctx_noitem = mnu.GetSubMenu(1);	
	hAccel = LoadAccelerators(AfxGetResourceHandle(), MAKEINTRESOURCE(IDR_MENU1));
	
	// Set title
	CString str;
	str.LoadString(IDS_VERSION);
	SetWindowText("Angry IP Scanner "+str);

	m_ip2_virgin = TRUE;
	m_ip1.SetWindowText("0.0.0.0");

	// Process command-line
	CCommandLine *cCmdLine = new CCommandLine();
	if (cCmdLine->process())
	{		
		m_ip1.SetWindowText(cCmdLine->m_szStartIP);
		m_ip2.SetWindowText(cCmdLine->m_szEndIP);
		m_ip2_virgin = FALSE;

		m_nCmdLineOptions = cCmdLine->m_nOptions;

		if (m_nCmdLineOptions & CMDO_SAVE_TO_FILE)
		{
			m_szDefaultFileName = new CString(cCmdLine->m_szFilename);
		}
		
		if (m_nCmdLineOptions & CMDO_START_SCAN)
		{
			CIpscanDlg::OnButton1();
		}
	}
	delete cCmdLine;
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CIpscanDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CIpscanDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
	
		CDialog::OnPaint();
		
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CIpscanDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CIpscanDlg::status(LPCSTR str) 
{
	SetDlgItemText(IDC_STATUS,str);
}

void CIpscanDlg::OnSize(UINT nType, int cx, int cy) 
{
	CDialog::OnSize(nType, cx, cy);
		
	if (m_list.m_hWnd!=NULL) {
		HandleResizing(cx, cy);
	}
}

void CIpscanDlg::OnIpExit() 
{	
	SendMessage(WM_CLOSE,0,0);	
}

void CIpscanDlg::OnButton1() 
{	
	if (!m_scanning) 
	{
		char str[16];
		m_ip1.GetWindowText((char *)&str,16);
		m_startip = ntohl(inet_addr((char*)&str));
		m_ip2.GetWindowText((char *)&str,16);
		m_endip = ntohl(inet_addr((char*)&str));
		m_endip++;

		if (m_endip<m_startip) 
		{
			MessageBox("Ending IP address is lower than starting.",NULL,MB_OK | MB_ICONHAND);
			return;
		}

		m_curip = m_startip;
		m_progress.SetRange(0,100);
		m_progress.SetPos(0);
		m_tickcount = GetTickCount()/1000;

		m_scanning=TRUE;
		
		((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)stopbmp.m_hObject); // stop scanning button

		m_list.DeleteAllItems();		

		CMenu *tmp = GetMenu();
		tmp->GetSubMenu(3)->EnableMenuItem(ID_OPTIONS_OPTIONS,MF_GRAYED);
		tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVETOTXT,MF_GRAYED);
		tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVESELECTION,MF_GRAYED);

		g_nThreadCount = 0;
		
		// Initialize scanning engine
		status("Initializing...");
		g_scanner->initScanning();

		SetTimer(1, g_options->m_nTimerDelay, NULL);

	} 
	else 
	{
		if (g_nThreadCount!=0) 
		{
			
			if (m_scanning==2) 
			{
				if (MessageBox("Are you sure you want to interrupt scanning by killing all the threads?\nScanning results will be incomplete.",NULL,MB_YESNO | MB_ICONQUESTION)==IDNO) return;
			
				for (UINT i=0; i<=10000; i++) 
				{
					if (g_hThreads[i]!=0) 
					{
						TerminateThread(g_hThreads[i],0);
						CloseHandle(g_hThreads[i]);
						g_hThreads[i]=0;
					}
				}
				m_numthreads.SetWindowText("0");
				g_nThreadCount = 0;
				goto finish_all;
			}

			m_endip = m_curip;
			m_progress.SetPos(100);
			m_scanning = 2;
		} 
		else 
		{
finish_all:
			KillTimer(1);
			m_scanning=FALSE;

			status("Finalizing...");
			g_scanner->finalizeScanning();
			
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)startbmp.m_hObject); // start scan bitmap
			status("Ready");
			
			CMenu *tmpMenu = GetMenu();
			tmpMenu->GetSubMenu(3)->EnableMenuItem(ID_OPTIONS_OPTIONS,MF_ENABLED);
			tmpMenu->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVETOTXT,MF_ENABLED);
			tmpMenu->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVESELECTION,MF_ENABLED);

			m_progress.SetPos(0);

			if (m_szDefaultFileName)
			{
				// Program was invoked via command-line, so save data to file & exit

				CSaveToFile tmp(d, FALSE, m_szDefaultFileName->GetBuffer(255), m_nCmdLineOptions & CMDO_SAVE_CSV, m_nCmdLineOptions & CMDO_APPEND_FILE);
				
				if (!(m_nCmdLineOptions & CMDO_NOT_EXIT))
					ExitProcess(0);
			}
			else
			{
				// Display final message box with statistics

				char str[140],ipa[16],ipa2[16],*ipp;
				in_addr in;
				in.S_un.S_addr = htonl(m_startip);
				ipp = inet_ntoa(in);
				strcpy((char*)&ipa,ipp);
				in.S_un.S_addr = htonl(m_endip);
				ipp = inet_ntoa(in);
				strcpy((char*)&ipa2,ipp);
				sprintf((char*)&str,"Scan complete\r\n\r\n%s - %s\r\n%u second(s)\r\n\r\nIPs scanned: %u\r\nAlive hosts: %u\r\nWith open ports: %u",&ipa,(char*)&ipa2,GetTickCount()/1000-m_tickcount+1, m_endip-m_startip+1, g_scanner->m_nAliveHosts, g_scanner->m_nOpenPorts);

				CMessageDlg cMsgDlg;
				cMsgDlg.setMessageText((char*)&str);
				cMsgDlg.DoModal();
			}
		}
	}
}

void CIpscanDlg::OnHelpAbout() 
{	
	CAboutDlg dlgAbout;
	dlgAbout.DoModal();
}

void CIpscanDlg::OnOptionsOptions() 
{	
	COptionsDlg dlgOpt;
	dlgOpt.DoModal();	// It will get and put options using g_options
}

void CIpscanDlg::OnButtonipup() 
{
	hostent *he;
	char str[100];
	char *addr;
	in_addr in;
	GetDlgItemText(IDC_HOSTNAME,str,100);
	he = gethostbyname(str);
	if (!he) {
		MessageBox("No DNS entry",NULL,MB_OK | MB_ICONHAND);
		return;
	}
	memcpy(&in.S_un.S_addr,*he->h_addr_list,sizeof(long));
	addr = inet_ntoa(in);
	m_ip1.SetWindowText(addr);
	m_ip2.SetWindowText(addr);
	m_ip2_virgin = TRUE;
}


void CIpscanDlg::OnTimer(UINT nIDEvent) 
{	
	 	
	int nIndex;
	
	if (m_curip<m_endip) 
	{
		if (g_nThreadCount >= g_options->m_nMaxThreads - 1) return;
		in_addr in;
		char *ipa;
		in.S_un.S_addr = htonl(m_curip);
		ipa = inet_ntoa(in);
		status(ipa);
		/*if (m_display==DO_ALL) 
		{*/
			nIndex = m_list.InsertItem(m_list.GetItemCount(),ipa,2);
			//m_list.SetItemData(i, i);
		//}
		CWinThread *thr = AfxBeginThread(ScanningThread,(void*)nIndex);
		if (m_startip < m_endip) 
		{
			m_curip++;
			m_progress.SetPos((m_curip-m_startip)*100/(m_endip-m_startip));
		}
	} 
	else 
	{
	
		if (g_nThreadCount == 0) 
		{
			m_endip--;
			OnButton1();
			
			return;
		} 
		else 
		{
			status("Wait for all threads to terminate");
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)killbmp.m_hObject);
			m_scanning = 2; // waiting can be interrupted
		}
	}
	
	CDialog::OnTimer(nIDEvent);
}


void CIpscanDlg::OnScanSavetotxt() 
{	
	CSaveToFile tmp(d);
}

void CIpscanDlg::OnScanSaveselection() 
{
	CSaveToFile tmp(d, TRUE);	
}

void CIpscanDlg::OnRclickList(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NMLISTVIEW *lw = (NMLISTVIEW*)pNMHDR;
	POINT p;
	GetCursorPos(&p);
	m_menucuritem = lw->iItem;
	if (m_menucuritem == 0xFFFFFFFF) {
		//TrackPopupMenu(ctx_noitem->m_hMenu,TPM_LEFTALIGN | TPM_RIGHTBUTTON,p.x,p.y,0,m_hWnd,NULL);
	} else {
		TrackPopupMenu(ctx_item->m_hMenu,TPM_LEFTALIGN | TPM_RIGHTBUTTON,p.x,p.y,0,m_hWnd,NULL);
	}
	*pResult = 0;
}

void CIpscanDlg::OnShowerrordescription() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected(); return; }
	CString err = m_list.GetItemText(m_menucuritem,CL_ERROR);
	CString str;
	int ern;
	if (strcmp(err,"None")==0) str = "No error"; else {
		ern = strtol(err,NULL,10);
		switch (ern) {
			case WSAENETDOWN: str = "Network is down"; break;
			case WSAHOST_NOT_FOUND: str = "Authoritative Host not found"; break;
// 11002		case WSATRY_AGAIN: str = "Non-Authoritative Host not found, or server failed"; break;
			case WSANO_DATA: str = "No DNS record"; break;
			case WSAEADDRNOTAVAIL: str = "Address is not available from this machine"; break;
			case WSAECONNREFUSED: str = "Connection was refused"; break;
			case WSAENETUNREACH: str = "The network cannot be reached"; break;
			case WSAETIMEDOUT: str = "Connection request timed out"; break;
			case 11002: str = "The network is unreachable"; break;
			case 11003: str = "The Host is unreachable"; break;
// WSANO_DATA		case 11004: str = "The protocol is unreachable"; break;
			case 11005: str = "The port is unreachable"; break;
			case 11010: str = "Request timed out"; break;
			case 11018: str = "Bad destination"; break;
			case 10055: str = "No buffer space.\nTry to reload the program or windows."; break;
			default: str = "Unknown error.\nFor description you may look in WSA help file or send email to me (see About)"; break;
		}
	}
	MessageBox(str,NULL,MB_OK | MB_ICONINFORMATION);
}

void CIpscanDlg::OnWindozesucksIpclipboard() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;	}
	char str[16];
	m_list.GetItemText(m_menucuritem,CL_IP,(char*)&str,16);
	HGLOBAL hglbCopy = GlobalAlloc(GMEM_DDESHARE, strlen(str)+1); 
	LPTSTR lp;
	lp = (char*)GlobalLock(hglbCopy);
	memcpy(lp,str,strlen(str)+1);
	GlobalUnlock(lp);
	OpenClipboard();
	EmptyClipboard();
	SetClipboardData(CF_TEXT,hglbCopy);
	CloseClipboard();
	
}

void CAboutDlg::OnGoemail() 
{
	CLink::goToWriteMail();
}

void CAboutDlg::OnGohttp() 
{
	CLink::goToScannerHomepage();
}

void CAboutDlg::OnHttpForum() 
{
	CLink::goToHomepageForum();
}

void CIpscanDlg::OnWindozesucksHostnameclipboard() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str[100];
	m_list.GetItemText(m_menucuritem,CL_HOSTNAME,(char*)&str,100);
	HGLOBAL hglbCopy = GlobalAlloc(GMEM_DDESHARE, strlen(str)+1); 
	LPTSTR lp;
	lp = (char*)GlobalLock(hglbCopy);
	memcpy(lp,str,strlen(str)+1);
	GlobalUnlock(lp);
	OpenClipboard();
	EmptyClipboard();
	SetClipboardData(CF_TEXT,hglbCopy);
	CloseClipboard();
}

BOOL CAboutDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	CString ver;
	ver.LoadString(IDS_VERSION);
	SetDlgItemText(IDC_VERSION,ver);	

	return TRUE;  
}

void CAboutDlg::OnAboutOK() 
{
	EndDialog(IDOK);
}

void CIpscanDlg::OnShowWindow(BOOL bShow, UINT nStatus) 
{
	CDialog::OnShowWindow(bShow, nStatus);
}

void CIpscanDlg::OnOptionsSaveoptions() 
{	
	g_options->save();	
}

void CIpscanDlg::OnFieldchangedIpaddress1(NMHDR* pNMHDR, LRESULT* pResult) 
{
	char str[16];
	m_ip2.GetWindowText((char*)&str,sizeof(str));
	if (m_ip2_virgin) {
		m_ip1.GetWindowText((char*)&str,sizeof(str));
		m_ip2.SetWindowText((char*)&str);
	}
	*pResult = 0;
}

void CIpscanDlg::OnFieldchangedIpaddress2(NMHDR* pNMHDR, LRESULT* pResult) 
{
	m_ip2_virgin = FALSE;	
	*pResult = 0;
}

void CIpscanDlg::OnClassC() 
{
	DWORD ip;
	char *ipc = (char*)&ip;
	m_ip1.GetAddress(ip); ipc[0] = (char) 1; m_ip1.SetAddress(ip);
	m_ip2.GetAddress(ip); ipc[0] = (char) 255; m_ip2.SetAddress(ip);
	m_ip2_virgin=FALSE;
}

void CIpscanDlg::OnClassD() 
{
	DWORD ip;
	char *ipc = (char*)&ip;
	m_ip1.GetAddress(ip); ipc[0] = (char) 1; ipc[1] = (char) 0; m_ip1.SetAddress(ip);
	m_ip2.GetAddress(ip); ipc[0] = (char) 255; ipc[1] = (char) 255; m_ip2.SetAddress(ip);
	m_ip2_virgin=FALSE;
}

void CIpscanDlg::OnWindozesucksShownetbiosinfo() 
{
	CString szMessage;
	char ipstr[16];	
	CString szUserName, szComputerName, szGroupName, szMacAddress;
	CMessageDlg cMessageDlg(this);
	
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	
	status("Getting info...");
	m_list.GetItemText(m_menucuritem,CL_IP,(char*)&ipstr,16);

	CNetBIOSUtils cNetBIOS;
	cNetBIOS.setIP((char*)&ipstr);
	if (!cNetBIOS.GetNames(&szUserName, &szComputerName, &szGroupName, &szMacAddress))
	{
		MessageBox("Cannot get NetBIOS information.","Error",MB_OK | MB_ICONERROR);
		goto exit_func;
	}
	
	szMessage.Format(
		"NetBIOS information for %s\r\n\r\n"
		"Computer Name:\t%s\r\n"
		"Workgroup Name:\t%s\r\n"
		"Username:\t%s\r\n"
		"\r\n"
		"MAC Address:\r\n%s",
		(char*) &ipstr, szComputerName, szGroupName, szUserName, szMacAddress
	);

	cMessageDlg.setMessageText(szMessage);
	cMessageDlg.DoModal();

exit_func:
	status("Ready");
}

void CIpscanDlg::OnHelpAngryipscannerwebpage() 
{	
	CLink::goToScannerHomepage();	
}

void CIpscanDlg::OnHelpAngryzibersoftware() 
{	
	CLink::goToHomepage();
}

void CIpscanDlg::OnHelpForum() 
{
	CLink::goToHomepageForum();	
}

void CIpscanDlg::OnWindozesucksRescanip() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	if (!m_scanning) {
		char str[16];
		m_list.GetItemText(m_menucuritem,CL_IP,(char*)&str,16);
		m_curip = ntohl(inet_addr((char*)&str));
		
		m_scanning=TRUE;
		((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)stopbmp.m_hObject);
		
		g_nThreadCount = 0;
		
		status((char*)&str);
		
	//	int n = 0;
	//	n = (UINT)m_menucuritem; //(UINT)m_curip - d->m_startip;
		m_list.SetItem(m_menucuritem,0,LVIF_IMAGE,NULL,2,0,0,0);
		m_list.SetItem(m_menucuritem,CL_STATE,LVIF_TEXT,"",0,0,0,0);
		m_list.SetItem(m_menucuritem,CL_PORT,LVIF_TEXT,"",0,0,0,0);
		m_list.SetItem(m_menucuritem,CL_ERROR,LVIF_TEXT,"",0,0,0,0);
		m_list.SetItem(m_menucuritem,CL_HOSTNAME,LVIF_TEXT,"",0,0,0,0);
		m_list.SetItem(m_menucuritem,CL_PINGTIME,LVIF_TEXT,"",0,0,0,0);
		RedrawWindow();
		
		ScanningThread((void*)m_menucuritem);
		
		m_scanning=FALSE;
		((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)startbmp.m_hObject);
		
		status("Ready");
	}
}

void CIpscanDlg::OnGotoNextalive() 
{
	m_list.SetFocus();
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int i = m_list.GetNextSelectedItem(pos)+1;
	for (; i<m_list.GetItemCount(); i++) {
		if (m_list.GetItemText(i,CL_STATE)=="Alive") {
			m_list.SetItemState(-1,0,LVIS_SELECTED);
			m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
			m_list.EnsureVisible(i,FALSE);
			return;
		}
	}
}

void CIpscanDlg::OnGotoNextdead() 
{
	m_list.SetFocus();
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int i = m_list.GetNextSelectedItem(pos)+1;
	for (; i<m_list.GetItemCount(); i++) {
		if (m_list.GetItemText(i,CL_STATE)=="Dead") {
			m_list.SetItemState(-1,0,LVIS_SELECTED);
			m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
			m_list.EnsureVisible(i,FALSE);
			return;
		}
	}
}

void CIpscanDlg::OnGotoNextopenport() 
{
	m_list.SetFocus();
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int i = m_list.GetNextSelectedItem(pos)+1;
	for (; i<m_list.GetItemCount(); i++) {
		if (m_list.GetItemText(i,CL_PORT).Right(1)=="n") {
			m_list.SetItemState(-1,0,LVIS_SELECTED);
			m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
			m_list.EnsureVisible(i,FALSE);
			return;
		}
	}
}

void CIpscanDlg::OnGotoNextclosedport() 
{
	m_list.SetFocus();
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int i = m_list.GetNextSelectedItem(pos)+1;
	for (; i<m_list.GetItemCount(); i++) {
		if (m_list.GetItemText(i,CL_PORT).Right(1)=="d") {
			m_list.SetItemState(-1,0,LVIS_SELECTED);
			m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
			m_list.EnsureVisible(i,FALSE);
			return;
		}
	}	
}

BOOL CIpscanDlg::PreTranslateMessage(MSG* pMsg) 
{
	if (TranslateAccelerator(m_hWnd,hAccel,pMsg ) ) return TRUE;

	return CDialog::PreTranslateMessage(pMsg);
}

void CIpscanDlg::ErrorNotSelected()
{
	MessageBox("You must select an IP first","Error",MB_OK | MB_ICONERROR);
}

void CIpscanDlg::OnGotoHostname() 
{
	CSearchDlg sd;
	sd.m_search = m_search;
	if (sd.DoModal()==IDCANCEL) return;
	m_search = sd.m_search;

	if (sd.m_beginning) m_list.SetItemState(-1,0,LVIS_SELECTED);
	
	m_list.SetFocus();
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int i = m_list.GetNextSelectedItem(pos)+1;

	if (sd.m_case) {
		for (; i<m_list.GetItemCount(); i++) {
			if (m_list.GetItemText(i,CL_HOSTNAME).Find(sd.m_search)!=-1) {
				m_list.SetItemState(-1,0,LVIS_SELECTED);
				m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
				m_list.EnsureVisible(i,FALSE);
				return;
			}
		}
	} else {
		CString tmp;
		sd.m_search.MakeUpper();
		for (; i<m_list.GetItemCount(); i++) {
			tmp = m_list.GetItemText(i,CL_HOSTNAME);
			tmp.MakeUpper();
			if (tmp.Find(sd.m_search)!=-1) {
				m_list.SetItemState(-1,0,LVIS_SELECTED);
				m_list.SetItemState(i,LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
				m_list.EnsureVisible(i,FALSE);
				return;
			}
		}
	}

	AfxMessageBox("\""+m_search+"\" was not found",MB_OK | MB_ICONWARNING);
}

int CALLBACK SortCompareFunc(LPARAM lParam1, LPARAM lParam2, LPARAM lParamSort) {
   CString    strItem1 = d->m_list.GetItemText(lParam1, nSortedCol);
   CString    strItem2 = d->m_list.GetItemText(lParam2, nSortedCol);

   int ret,ip1,ip2;
   
   switch (nSortedCol) {
	case CL_IP:
		ip1 = ntohl(inet_addr(strItem1));
		ip2 = ntohl(inet_addr(strItem2));
		if (ip1>ip2) ret=1; else if (ip1<ip2) ret=-1; else ret=0;
		break;
	case CL_PINGTIME:
		if (strItem1 == "N/A") ip1 = 0; else sscanf(strItem1,"%d",&ip1);
		if (strItem2 == "N/A") ip2 = 0; else sscanf(strItem2,"%d",&ip2);
		if (ip1>ip2) ret=1; else if (ip1<ip2) ret=-1; else ret=0;
		break;
	case CL_STATE:
	case CL_HOSTNAME:
	case CL_PORT:
	case CL_ERROR:
		ret = strcmp(strItem1, strItem2);
		break;
   }
   
   //MessageBoxA(0,strItem1+" "+strItem2,NULL,MB_OK);
   return ret*bSortAscending;

}



void CIpscanDlg::OnItemclickListHeader(NMHDR* pNMHDR, LRESULT* pResult) 
{
	HD_NOTIFY *phdn = (HD_NOTIFY *) pNMHDR;
	
	if (m_scanning) return;
	
	if (phdn->iButton == 0) {  // left button
		if( phdn->iItem == nSortedCol )
	        bSortAscending = -bSortAscending;
        else
            bSortAscending = 1;

        nSortedCol = phdn->iItem;

        for (int i=0;i < m_list.GetItemCount();i++) {
			m_list.SetItemData(i, i);
		}

		m_list.SortItems(&SortCompareFunc,0);

	}
		
	*pResult = 0;
}

void CIpscanDlg::OnOpencomputerinexplorer() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str[40],str2[16];
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	sprintf((char*)&str,"\\\\%s",(char*)&str2);
	if ((int)ShellExecute(0,"open",(char*)&str,NULL,NULL,SW_SHOWNORMAL)<=32) {
		MessageBox("Netbios is not accessible on this computer (no shares or port is closed) or probably you don't have windows networking installed.",NULL,MB_OK | MB_ICONHAND);
	}
}


void CIpscanDlg::OnCommandsOpencomputerAsftp() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str[40],str2[16];
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	sprintf((char*)&str,"ftp://%s/",(char*)&str2);
	if ((int)ShellExecute(0,"open",(char*)&str,NULL,NULL,SW_SHOWNORMAL)<=32) {
		MessageBox("No program is assotsiated to open FTP urls.",NULL,MB_OK | MB_ICONHAND);
	}
}

void CIpscanDlg::OnCommandsOpencomputerAswebsite() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str[40],str2[16];
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	sprintf((char*)&str,"http://%s/",(char*)&str2);
	if ((int)ShellExecute(0,"open",(char*)&str,NULL,NULL,SW_SHOWNORMAL)<=32) {
		MessageBox("No program is assotsiated to open HTTP urls.",NULL,MB_OK | MB_ICONHAND);
	}
}

void CIpscanDlg::OnCommandsOpencomputerTelnet() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str[40],str2[16];
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	sprintf((char*)&str,"telnet://%s/",(char*)&str2);
	if ((int)ShellExecute(0,"open",(char*)&str,NULL,NULL,SW_SHOWNORMAL)<=32) {
		MessageBox("No program is assotsiated to open TELNET urls.",NULL,MB_OK | MB_ICONHAND);
	}
}

void CIpscanDlg::OnCommandsOpencomputerTelnettospecifiedport() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char str2[16],portnum[10];
	CString str;
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	m_list.GetItemText(m_menucuritem,CL_PORT,portnum,10);
	str = portnum;
	strcpy((char*)&portnum,str.Mid(0,str.Find(":")));
	str.Format("%s %s",(char*)&str2,(char*)&portnum);		
	if ((int)ShellExecute(0,NULL,"telnet.exe",str,NULL,SW_SHOWNORMAL)<=32) {
		MessageBox("Error executing telnet program.",NULL,MB_OK | MB_ICONHAND);
	}
}

void CIpscanDlg::OnCommandsOpencomputerHint() 
{
	CMessageDlg cMsgDlg;
	cMsgDlg.setMessageText("Note: these commands are provided for your convenience only. They are not "
		       "guaranteed to work. They just try to execute specified commands using "
			   "Windows Shell API to see if any other program is assotsiated with "
			   "that action. Please don't mail me with questions, why these don't "
			   "work. If you know what they should do, then you can setup your "
			   "system yourself to handle URL requests.");	
	cMsgDlg.DoModal();
}

void CIpscanDlg::OnButtonpaste() 
{
	OpenClipboard();
	HGLOBAL hglbCopy = GetClipboardData(CF_TEXT);
	CloseClipboard();	

	if (hglbCopy==NULL) {
		MessageBox("Clipboard is empty","Error",MB_OK | MB_ICONHAND);
		return;
	}

	LPTSTR lp;
	lp = (char*)GlobalLock(hglbCopy);	
	
	m_ip1.SetWindowText(lp);
	m_ip2.SetWindowText(lp);
	m_ip2_virgin = TRUE;

	GlobalUnlock(lp);	
}

void CIpscanDlg::OnHelpCommandline() 
{
	CCommandLine::displayHelp();
	
}

void CIpscanDlg::OnOptionsInstallProgram() 
{
	CInstallDlg dlgInst;
	dlgInst.DoModal();
}

void CIpscanDlg::OnDestroy() 
{
	CDialog::OnDestroy();
	
	delete(g_scanner);
	delete(m_szDefaultFileName);
}

void CIpscanDlg::OnDrawItem(int nIDCtl, LPDRAWITEMSTRUCT lpDrawItemStruct) 
{
	
	CDialog::OnDrawItem(nIDCtl, lpDrawItemStruct);
}

void CIpscanDlg::OnButtonToAdvanced() 
{
	if (m_bAdvancedMode)
	{
		// Hide advanced controls
		m_ctScanPorts.ShowWindow(SW_HIDE);
		m_ctWhatPorts.ShowWindow(SW_HIDE);
		GetDlgItem(IDC_SELECT_PORTS)->ShowWindow(SW_HIDE);
		
		g_nListOffset -= g_nAdvancedOffset;

		// Change button image		
		((CButton*)GetDlgItem(IDC_BUTTON_TO_ADVANCED))->SetBitmap((HBITMAP)m_bmpShowAdvanced.m_hObject);
	}
	else
	{
		// Show advanced controls
		m_ctScanPorts.ShowWindow(SW_SHOW);
		m_ctWhatPorts.ShowWindow(SW_SHOW);
		GetDlgItem(IDC_SELECT_PORTS)->ShowWindow(SW_SHOW);

		g_nListOffset += g_nAdvancedOffset;
		
		// Change button image		
		((CButton*)GetDlgItem(IDC_BUTTON_TO_ADVANCED))->SetBitmap((HBITMAP)m_bmpHideAdvanced.m_hObject);

		// Display port string
		if (g_options->m_aParsedPorts[0].nStartPort != 0)
		{
			m_ctWhatPorts.SetWindowText(g_options->m_szPorts);
		}
	}
	m_bAdvancedMode = !m_bAdvancedMode;
	RECT rc;
	GetClientRect(&rc);
	HandleResizing(rc.right-rc.left, rc.bottom-rc.top);
	
	m_ip1.SetFocus();
}

void CIpscanDlg::HandleResizing(int cx, int cy)
{
	// Resize window and reposition controls
	if (g_nStatusHeight > 0)
	{
		m_list.MoveWindow(0, g_nListOffset, cx, cy-g_nListOffset-(g_nStatusHeight+2), TRUE);
		m_statusctl.MoveWindow(0, cy-g_nStatusHeight/*18*/, cx/2, /*18*/g_nStatusHeight, TRUE);
		m_progress.MoveWindow(cx/2+1,cy-g_nStatusHeight,cx/2-1,g_nStatusHeight,TRUE);
	}
}

void CIpscanDlg::OnScanPortsClicked() 
{
	BOOL bChecked = m_ctScanPorts.GetCheck();

	if (bChecked && g_options->m_aParsedPorts[0].nStartPort == 0)
	{
		AfxMessageBox("No ports selected", MB_ICONHAND | MB_OK, 0);
		m_ctScanPorts.SetCheck(FALSE);
		return;
	}

	
	m_list.SetShowPorts(bChecked);
	
	g_options->m_bScanPorts = bChecked;
}

void CIpscanDlg::OnSelectPortsClicked() 
{
	CPortDlg dlg;
	dlg.DoModal();

	LPCSTR szPortString = g_options->m_szPorts;

	if (szPortString[0] == 0)
	{
		m_ctWhatPorts.SetWindowText("N/A");
		m_ctScanPorts.SetCheck(FALSE);
	}
	else
	{
		m_ctWhatPorts.SetWindowText(g_options->m_szPorts);
		m_ctScanPorts.SetCheck(TRUE);
	}

	// To update properties
	OnScanPortsClicked();
}
