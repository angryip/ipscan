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
#include "SelectColumnsDlg.h"

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

int g_bSortAscending = 1;
int g_nSortedCol = -1;

unsigned long g_nEndIP;
unsigned long g_nStartIP;
unsigned long g_nCurrentIP;

unsigned long g_nStartItemIndex;
unsigned long g_nEndItemIndex;
unsigned long g_nCurrentItemIndex;

BOOL g_bScanExistingItems = FALSE;

#define INDEX_CONTEXT_MENU	2
#define	INDEX_SHOW_MENU		4

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

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("Dlg constructor()", 0, 0);
	#endif

	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_szDefaultFileName = NULL;
	m_pToolTips = NULL;
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
	ON_BN_CLICKED(IDC_BUTTON1, OnButtonScan)
	ON_COMMAND(ID_HELP_ABOUT, OnHelpAbout)
	ON_COMMAND(ID_OPTIONS_OPTIONS, OnOptionsOptions)
	ON_BN_CLICKED(IDC_BUTTONIPUP, OnButtonipup)
	ON_WM_TIMER()
	ON_COMMAND(ID_SCAN_SAVETOTXT, OnScanSavetotxt)
	ON_NOTIFY(NM_RCLICK, IDC_LIST, OnRclickList)
	ON_COMMAND(ID__OPENCOMPUTERINEXPLORER, OnOpencomputerinexplorer)	
	ON_COMMAND(ID_COMMANDS_IPCLIPBOARD, OnIPToClipboard)	
	ON_COMMAND(ID_SCAN_SAVESELECTION, OnScanSaveselection)
	ON_WM_SHOWWINDOW()
	ON_COMMAND(ID_OPTIONS_SAVEOPTIONS, OnOptionsSaveoptions)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS1, OnFieldchangedIpaddress1)
	ON_NOTIFY(IPN_FIELDCHANGED, IDC_IPADDRESS2, OnFieldchangedIpaddress2)
	ON_BN_CLICKED(IDC_CLASS_C, OnClassC)
	ON_BN_CLICKED(IDC_CLASS_D, OnClassD)
	ON_COMMAND(ID_SHOWNETBIOSINFO, OnShowNetBIOSInfo)
	ON_COMMAND(ID_HELP_ANGRYIPSCANNERWEBPAGE, OnHelpAngryipscannerwebpage)
	ON_COMMAND(ID_HELP_ANGRYZIBERSOFTWARE, OnHelpAngryzibersoftware)
	ON_COMMAND(ID_COMMANDS_RESCANIP, OnRescanIP)
	ON_COMMAND(ID_GOTO_NEXTALIVE, OnGotoNextalive)
	ON_COMMAND(ID_GOTO_NEXTDEAD, OnGotoNextdead)
	ON_COMMAND(ID_GOTO_NEXTOPENPORT, OnGotoNextopenport)
	ON_COMMAND(ID_GOTO_NEXTCLOSEDPORT, OnGotoNextclosedport)
	ON_COMMAND(ID_GOTO_HOSTNAME, OnGotoHostname)
	ON_NOTIFY(HDN_ITEMCLICKA, 0, OnItemclickListHeader)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_ASFTP, OnCommandsOpencomputerAsftp)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_ASWEBSITE, OnCommandsOpencomputerAswebsite)
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_TELNET, OnCommandsOpencomputerTelnet)	
	ON_COMMAND(ID_COMMANDS_OPENCOMPUTER_HINT, OnCommandsOpencomputerHint)
	ON_BN_CLICKED(IDC_BUTTONPASTE, OnButtonpaste)
	ON_COMMAND(ID_HELP_COMMANDLINE, OnHelpCommandline)
	ON_COMMAND(ID_HELP_FORUM, OnHelpForum)
	ON_COMMAND(ID_OPTIONS_INSTALL_PROGRAM, OnOptionsInstallProgram)
	ON_WM_DESTROY()
	ON_WM_DRAWITEM()
	ON_BN_CLICKED(IDC_BUTTON_TO_ADVANCED, OnButtonToAdvanced)
	ON_BN_CLICKED(IDC_SCAN_PORTS, OnScanPortsClicked)
	ON_BN_CLICKED(IDC_SELECT_PORTS, OnSelectPortsClicked)
	ON_COMMAND(ID_COMMANDS_SHOWDETAILS, OnCommandsShowIPdetails)
	ON_BN_CLICKED(IDC_SELECT_COLUMNS, OnSelectColumns)
	ON_COMMAND(ID_OPTIONS_SAVEDIMENSIONS, OnOptionsSavedimensions)
	ON_COMMAND(ID_UTILS_DELETEFROMLIST_DEADHOSTS, OnUtilsDeletefromlistDeadhosts)
	ON_COMMAND(ID_UTILS_DELETEFROMLIST_ALIVEHOSTS, OnUtilsDeletefromlistAlivehosts)
	ON_COMMAND(ID_UTILS_DELETEFROMLIST_CLOSEDPORTS, OnUtilsDeletefromlistClosedports)
	ON_COMMAND(ID_UTILS_DELETEFROMLIST_OPENPORTS, OnUtilsDeletefromlistOpenports)
	ON_COMMAND(ID_COMMANDS_DELETEIP, OnCommandsDeleteIP)
	ON_COMMAND(ID_OPTIONS_SHOWLASTSCANINFO, ShowCompleteInformation)
	ON_NOTIFY(HDN_ITEMCLICKW, 0, OnItemclickListHeader)
	ON_COMMAND(ID_OPTIONS_SELECT_COLUMNS, OnSelectColumns)
	ON_COMMAND(ID_OPTIONS_SELECTPORTS, OnSelectPortsClicked)
	ON_WM_CLOSE()
	//}}AFX_MSG_MAP

	ON_COMMAND_RANGE(ID_MENU_SHOW_CMD_001, ID_MENU_SHOW_CMD_099, OnExecuteShowMenu)

END_MESSAGE_MAP()

int botot;

/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg message handlers

BOOL CIpscanDlg::OnInitDialog()
{
	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog() start", 0, 0);
	#endif

	CDialog::OnInitDialog();

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): custom init dialog", 0, 0);
	#endif
	
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

	m_bSysCommand = FALSE;

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog() set icon ", 0, 0);
	#endif
	
	app = AfxGetApp();
	g_dlg = d = (CIpscanDlg*)app->m_pMainWnd;
	
	// Load default options
	g_options = new COptions();
	g_options->load();
	g_options->setWindowPos();	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): COptions created ", 0, 0);
	#endif

	// Add image list to the listbox
	m_imglist.Create(IDB_IMAGELIST,16,2,0xFFFFFF);
	m_list.SetImageList(&m_imglist,LVSIL_SMALL);

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): image list created ", 0, 0);
	#endif

	// Create the scanner object
	g_scanner = new CScanner();	
	
	// Add columns to the list control
	g_scanner->initListColumns(&m_list);

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): CScanner created ", 0, 0);
	#endif

	// Set button's bitmaps
	m_bmpUpArrow.LoadMappedBitmap(IDB_UPARROW);
	((CButton*)GetDlgItem(IDC_BUTTONIPUP))->SetBitmap((HBITMAP)m_bmpUpArrow.m_hObject);
	m_bmpPaste.LoadMappedBitmap(IDB_PASTE);
	((CButton*)GetDlgItem(IDC_BUTTONPASTE))->SetBitmap((HBITMAP)m_bmpPaste.m_hObject);
	m_bmpStart.LoadMappedBitmap(IDB_BMPSTART);
	m_bmpStop.LoadMappedBitmap(IDB_BMPSTOP);
	m_bmpKill.LoadMappedBitmap(IDB_BMPKILL);
	((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)m_bmpStart.m_hObject);
	CBitmap *tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_C_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_C))->SetBitmap((HBITMAP)tmpbmp->m_hObject);
	tmpbmp = new CBitmap; tmpbmp->LoadMappedBitmap(IDB_CLASS_D_PIC);
	((CButton*)GetDlgItem(IDC_CLASS_D))->SetBitmap((HBITMAP)tmpbmp->m_hObject);
	// Load bitmaps for advanced mode button
	m_bmpHideAdvanced.LoadMappedBitmap(IDB_HIDE_ADVANCED);
	m_bmpShowAdvanced.LoadMappedBitmap(IDB_SHOW_ADVANCED);
	m_bmpSelectColumns.LoadMappedBitmap(IDB_SELECT_COLUMNS);
	((CButton*)GetDlgItem(IDC_SELECT_COLUMNS))->SetBitmap((HBITMAP)m_bmpSelectColumns.m_hObject);

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): bitmaps loaded ", 0, 0);
	#endif

	// Add Tooltips
	m_pToolTips = new CToolTipCtrl;
	m_pToolTips->Create(this);
	m_pToolTips->AddTool(GetDlgItem(IDC_BUTTON1), "Start/Stop Scanning");
	m_pToolTips->AddTool(GetDlgItem(IDC_BUTTONIPUP), "Use the IP address of the specified hostname");
	m_pToolTips->AddTool(GetDlgItem(IDC_BUTTONPASTE), "Paste the IP address from clipboard");
	m_pToolTips->AddTool(GetDlgItem(IDC_CLASS_D), "Make a class B range from the above IP addresses");
	m_pToolTips->AddTool(GetDlgItem(IDC_CLASS_C), "Make a class C range from the above IP addresses");
	m_pToolTips->AddTool(GetDlgItem(IDC_BUTTON_TO_ADVANCED), "Show/Hide additional controls");
	m_pToolTips->AddTool(GetDlgItem(IDC_SELECT_COLUMNS), "Select columns to be scanned");	
	m_pToolTips->AddTool(GetDlgItem(IDC_SELECT_PORTS), "Select TCP ports to be scanned");	
	m_pToolTips->Activate(TRUE);

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): Tooltips added ", 0, 0);
	#endif
	
	// Set window size
	RECT rc;
	m_ipup.GetWindowRect(&rc); g_nListOffset = rc.bottom;
	m_ctScanPorts.GetWindowRect(&rc); g_nAdvancedOffset = rc.bottom - g_nListOffset + 1;
	m_ip1.GetWindowRect(&rc); g_nListOffset -= (rc.top-5);
	m_progress.GetWindowRect(&rc); g_nStatusHeight = rc.bottom-rc.top-2;	
	
	m_bAdvancedMode = true;	// OnButtonToAdvanced() will change this to false
	g_nListOffset += g_nAdvancedOffset; // OnButtonToAdvanced() will subtract this
	OnButtonToAdvanced(); // Hide advanced controls by default
	OnScanPortsClicked();	
	
	status(NULL);	// Ready

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): Window resized", 0, 0);
	#endif

	// Init hostname
	char szHostname[256];
	gethostname((char *)&szHostname, 100);
	SetDlgItemText(IDC_HOSTNAME, szHostname);

	m_nScanMode = SCAN_MODE_NOT_SCANNING;

	// Load menu			
	g_scanner->initMenuWithColumns(GetMenu()->GetSubMenu(INDEX_CONTEXT_MENU)->GetSubMenu(INDEX_SHOW_MENU));	// Show menu	
	m_menuContext = GetMenu()->GetSubMenu(INDEX_CONTEXT_MENU);	// TODO: Should not be stored!

	hAccel = LoadAccelerators(AfxGetResourceHandle(), MAKEINTRESOURCE(IDR_MENU1));

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): Menu loaded", 0, 0);
	#endif

	// Set title
	CString str;
	str.LoadString(IDS_VERSION);
	SetWindowText("Angry IP Scanner "+str);

	m_ip2_virgin = TRUE;
	m_ip1.SetWindowText("0.0.0.0");

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): Processing command-line", 0, 0);
	#endif

	// Process command-line
	CCommandLine cCmdLine;
	if (cCmdLine.process())
	{		
		m_ip1.SetWindowText(cCmdLine.m_szStartIP);
		m_ip2.SetWindowText(cCmdLine.m_szEndIP);
		m_ip2_virgin = FALSE;

		m_nCmdLineOptions = cCmdLine.m_nOptions;
		m_nCmdLineFileFormat = cCmdLine.m_nFileFormat;

		if (m_nCmdLineOptions & CMDO_SAVE_TO_FILE)
		{
			m_szDefaultFileName = new CString(cCmdLine.m_szFilename);
		}
		
		if (m_nCmdLineOptions & CMDO_START_SCAN)
		{
			CIpscanDlg::OnButtonScan();
		}
	}	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("OnInitDialog(): Finished", 0, 0);
	#endif

	// Initialize the critical section (used for synchronization of threads)
	InitializeCriticalSection(&g_criticalSection);	
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CIpscanDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	nID &= 0xFFF0;

	if (nID == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else 
	{
		if (nID == SC_CLOSE)
		{
			m_bSysCommand = TRUE;
		}
		
		CDialog::OnSysCommand(nID, lParam);		
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CIpscanDlg::OnPaint() 
{
	if (m_nCmdLineOptions & CMDO_HIDE_WINDOW)
	{
		ShowWindow(SW_HIDE);
	}

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
	if (str != NULL)
		SetDlgItemText(IDC_STATUS, str);
	else
		SetDlgItemText(IDC_STATUS, "Ready");
}

void CIpscanDlg::OnSize(UINT nType, int cx, int cy) 
{
	CDialog::OnSize(nType, cx, cy);
		
	if (m_list.m_hWnd != NULL) 
	{
		HandleResizing(cx, cy);
	}	
}

void CIpscanDlg::OnIpExit() 
{	
	m_bSysCommand = TRUE;
	SendMessage(WM_CLOSE,0,0);	
}

void CIpscanDlg::OnButtonScan() 
{	
	if (m_nScanMode == SCAN_MODE_NOT_SCANNING) 
	{
		if ((!g_options->m_bScanPorts) && (g_options->m_neDisplayOptions == DISPLAY_OPEN))
		{
			MessageBox("Conflicting options: Display only open ports selected, but port scanning is not enabled!", NULL, MB_OK | MB_ICONHAND);
			return;
		}

		if (!g_bScanExistingItems)
		{
			if (m_list.GetItemCount() > 0)
			{
				if (MessageBox("Are you sure you want to erase all previous scanning results?", NULL, MB_YESNO | MB_ICONQUESTION | MB_DEFBUTTON2) == IDNO)
				{
					// If user is unsure, then cancel scanning
					return;
				}
			}

			m_list.DeleteAllItems();		
		
			char str[16];
			m_ip1.GetWindowText((char *)&str,16);
			g_nStartIP = ntohl(inet_addr((char*)&str));
			m_ip2.GetWindowText((char *)&str,16);
			g_nEndIP = ntohl(inet_addr((char*)&str));	
		
			// Minor Bug workaround ;-)
			if (g_nEndIP == 0xFFFFFFFF)
			{
				g_nEndIP--;	// Scan to 255.255.255.254
			}
			
			g_nEndIP++;

			if (g_nEndIP < g_nStartIP) 
			{
				MessageBox("Ending IP address is lower than starting.",NULL,MB_OK | MB_ICONHAND);
				return;
			}

			g_nCurrentIP = g_nStartIP;
		}
		else
		{
			// g_nStartItemIndex is set outside before calling this method
			g_nCurrentItemIndex = g_nStartItemIndex;
		}

		m_progress.SetRange(0,100);
		m_progress.SetPos(0);
		m_tickcount = GetTickCount()/1000;

		m_nScanMode = SCAN_MODE_SCANNING;
		
		((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)m_bmpStop.m_hObject); // stop scanning button		

		g_nThreadCount = 0;

		EnableMenuItems(FALSE);
		
		// Initialize scanning engine
		status("Initializing...");
		g_scanner->initScanning();

		SetTimer(1, g_options->m_nTimerDelay, NULL);

	} 
	else // m_nScanMode is SCAN_MODE_SCANNING or SCAN_MODE_FINISHING)
	{
		if (g_nThreadCount != 0) 
		{
			
			if (m_nScanMode == SCAN_MODE_FINISHING) 
			{
				EnterCriticalSection(&g_criticalSection);
				if (MessageBox("Are you sure you want to interrupt scanning by killing all the threads?\nScanning results will be incomplete.",NULL,MB_YESNO | MB_ICONQUESTION)==IDNO) return;
				LeaveCriticalSection(&g_criticalSection);
			
				// Kill threads
				KillAllRunningThreads();	
			}
			else // SCAN_MODE_SCANNING
			{
				// Stop scanning (but wait for existing threads)

				if (g_bScanExistingItems)
				{
					g_nEndItemIndex = g_nCurrentItemIndex;
				}
				else
				{
					g_nEndIP = g_nCurrentIP;
				}
				m_progress.SetPos(100);
				m_nScanMode = SCAN_MODE_FINISHING;
			}
			
		} 
		else // g_nThreadCount == 0
		{
			KillTimer(1);
			m_nScanMode = SCAN_MODE_NOT_SCANNING;

			status("Finalizing...");
			g_scanner->finalizeScanning();			
			
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)m_bmpStart.m_hObject); // start scan bitmap			
			
			EnableMenuItems(TRUE);			

			m_progress.SetPos(0);

			status(NULL);	// Ready

			if (m_szDefaultFileName)
			{
				// Program was invoked via command-line, so save data to file & exit

				CSaveToFile tmp(d, FALSE, m_szDefaultFileName->GetBuffer(255), m_nCmdLineFileFormat, m_nCmdLineOptions & CMDO_APPEND_FILE);
				
				if (!(m_nCmdLineOptions & CMDO_NOT_EXIT))
					ExitProcess(0);
			}
			else
			{
				// Display final message box with statistics

				if (!g_bScanExistingItems)
				{
					char ipa[16],ipa2[16],*ipp;
					in_addr in;
					in.S_un.S_addr = htonl(g_nStartIP);
					ipp = inet_ntoa(in);
					strcpy((char*)&ipa,ipp);
					in.S_un.S_addr = htonl(g_nEndIP);
					ipp = inet_ntoa(in);
					strcpy((char*)&ipa2,ipp);

					m_szCompleteInformation.Format(
						"Scan complete\r\n\r\n%s - %s\r\n%u second(s)\r\n\r\n"
						"IPs scanned:\t%u\r\n"
						"Alive hosts:\t%u\r\n",
						&ipa,(char*)&ipa2,GetTickCount()/1000-m_tickcount+1, g_nEndIP-g_nStartIP+1, g_scanner->m_nAliveHosts);

					if (g_options->m_bScanPorts)
					{
						CString szPortInfo;
						szPortInfo.Format("With open ports:\t%u", g_scanner->m_nOpenPorts);
						m_szCompleteInformation += szPortInfo;
					}

					ShowCompleteInformation();					
				}
			}

			g_bScanExistingItems = FALSE;	// Reset this stuff
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

	// Update the main list
	m_list.SetScanPorts();
}

void CIpscanDlg::OnButtonipup() 
{
	status("Getting IP...");

	hostent *he;
	char str[100];
	char *addr;
	in_addr in;
	GetDlgItemText(IDC_HOSTNAME,str,100);
	he = gethostbyname(str);

	status(NULL);

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
	 	
	int nItemIndex = 0;

	BOOL bCurrentLessThanEnd;

	if (g_bScanExistingItems)	
		bCurrentLessThanEnd = g_nCurrentItemIndex < g_nEndItemIndex;
	else
		bCurrentLessThanEnd = g_nCurrentIP < g_nEndIP;
	
	if (bCurrentLessThanEnd) 
	{
		if ((int) g_nThreadCount >= g_options->m_nMaxThreads - 1) 
			return;

		in_addr in;
		char *szIP;
		in.S_un.S_addr = htonl(g_nCurrentIP);
		szIP = inet_ntoa(in);
		status(szIP);		

		if (g_bScanExistingItems)
		{
			AfxBeginThread(ThreadProcCallbackRescan, (LPVOID) g_nCurrentItemIndex);	
			g_nCurrentItemIndex++;

			if (g_nEndItemIndex != g_nStartItemIndex)	// To prevent division by 0 below
			{			
				m_progress.SetPos((g_nCurrentItemIndex - g_nStartItemIndex) * 100 / (g_nEndItemIndex - g_nStartItemIndex));
			}
		}
		else
		{
			AfxBeginThread(ThreadProcCallback, (LPVOID) g_nCurrentIP);	// Pass IP in Host byte order
			g_nCurrentIP++;

			if (g_nEndIP != g_nStartIP)	// To prevent division by 0 below
			{			
				m_progress.SetPos((g_nCurrentIP - g_nStartIP) * 100 / (g_nEndIP - g_nStartIP));
			}
		}			
		
	} 
	else 
	{
		if (g_nThreadCount == 0) 
		{
			if (g_bScanExistingItems)
				g_nEndItemIndex--;
			else
				g_nEndIP--;

			OnButtonScan();
			
			return;
		} 
		else 
		{
			status("Wait for all threads to terminate...");
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)m_bmpKill.m_hObject);
			m_nScanMode = SCAN_MODE_FINISHING; // waiting can be interrupted
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
	if (m_menucuritem != 0xFFFFFFFF) 
	{
		TrackPopupMenu(m_menuContext->m_hMenu,TPM_LEFTALIGN | TPM_RIGHTBUTTON,p.x,p.y,0,m_hWnd,NULL);
	}	
	*pResult = 0;
}

void CIpscanDlg::OnIPToClipboard() 
{
	m_list.CopyIPToClipboard();
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
	
	MessageBox("Options and selected columns are successfully saved.", NULL, MB_OK | MB_ICONINFORMATION);
}

void CIpscanDlg::OnOptionsSavedimensions() 
{
	g_options->saveDimensions();

	MessageBox("Window size, position and widths of currently selected columns are successfully saved.", NULL, MB_OK | MB_ICONINFORMATION);
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

void CIpscanDlg::OnShowNetBIOSInfo() 
{
	status("Getting info...");
	m_list.ShowNetBIOSInfo();
	status(NULL);
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

void CIpscanDlg::OnGotoNextalive() 
{
	m_list.GoToNextAliveIP();
}

void CIpscanDlg::OnGotoNextdead() 
{
	m_list.GoToNextDeadIP();
}

void CIpscanDlg::OnGotoNextopenport() 
{
	m_list.GoToNextOpenPortIP();
}

void CIpscanDlg::OnGotoNextclosedport() 
{
	m_list.GoToNextClosedPortIP();
}

void CIpscanDlg::OnGotoHostname() 
{
	m_list.GoToNextSearchIP();
}

BOOL CIpscanDlg::PreTranslateMessage(MSG* pMsg) 
{
	BOOL bDoAccelerator = TRUE;

	if (pMsg->message == WM_KEYDOWN && pMsg->wParam == VK_DELETE)
	{
		if (pMsg->hwnd != m_list.m_hWnd)
			bDoAccelerator = FALSE;	// Do not let accelerator capture Del key from editboxes
	}

	if (bDoAccelerator && TranslateAccelerator(m_hWnd, hAccel, pMsg)) 
		return TRUE;

	if (m_pToolTips != NULL)
		m_pToolTips->RelayEvent(pMsg);

	// Check for Enter key presses
	if (pMsg->message == WM_KEYDOWN && pMsg->wParam == VK_RETURN)
	{
		// If this is a hostname edit control
		if (pMsg->hwnd == GetDlgItem(IDC_HOSTNAME)->m_hWnd)
		{
			m_ipup.SetFocus();
			OnButtonipup();
			return TRUE;
		}
		else
		// If this is a list control
		if (pMsg->hwnd == m_list.m_hWnd)
		{
			if (m_list.GetCurrentSelectedItem(FALSE) >= 0)
			{
				OnCommandsShowIPdetails();
				return TRUE;
			}
		}
	}

	return CDialog::PreTranslateMessage(pMsg);
}

int CALLBACK SortCompareFunc(LPARAM lParam1, LPARAM lParam2, LPARAM lParamSort) 
{
	CString strItem1 = d->m_list.GetItemText(lParam1, g_nSortedCol);
	CString strItem2 = d->m_list.GetItemText(lParam2, g_nSortedCol);   

	int nRet, n1, n2;

	switch (g_nSortedCol) 
	{
		case CL_IP:
			n1 = ntohl(inet_addr(strItem1));
			n2 = ntohl(inet_addr(strItem2));
			if (n1 > n2) nRet = 1; else if (n1 < n2) nRet = -1; else nRet = 0;
			break;
		default:

			if (strItem1 == "N/A" || strItem1 == "N/S")
				strItem1 = "\xFF";	// Move it to the end

			if (strItem2 == "N/A" || strItem2 == "N/S")
				strItem2 = "\xFF";  // Move it to the end

			nRet = strItem1.CompareNoCase(strItem2);
			break;
	}
	
	return nRet * g_bSortAscending;
}

void CIpscanDlg::OnItemclickListHeader(NMHDR* pNMHDR, LRESULT* pResult) 
{
	HD_NOTIFY *phdn = (HD_NOTIFY *) pNMHDR;
	
	if (m_nScanMode != SCAN_MODE_NOT_SCANNING) 
		return;
	
	if (phdn->iButton == 0)   // left button
	{
		if( phdn->iItem == g_nSortedCol )
	        g_bSortAscending = -g_bSortAscending;
        else
            g_bSortAscending = 1;

        g_nSortedCol = phdn->iItem;

        for (int i=0;i < m_list.GetItemCount();i++) 
		{
			m_list.SetItemData(i, i);
		}

		m_list.SortItems(&SortCompareFunc,0);
	}
		
	*pResult = 0;
}

void CIpscanDlg::OnOpencomputerinexplorer() 
{
	int nItemIndex = m_list.GetCurrentSelectedItem();		
	if (nItemIndex == -1)
		return;

	status("Opening...");
	CString szIP = m_list.GetItemText(nItemIndex, CL_IP);
	szIP = "\\\\" + szIP;	
	if ((int)ShellExecute(0, "open", szIP, NULL, NULL, SW_SHOWNORMAL) <= 32) 
	{
		MessageBox("This host cannot be opened (ShellExecute " + szIP + " failed)\nIt may not have anything shared.", NULL, MB_OK | MB_ICONHAND);
	}	
	status(NULL);
}


void CIpscanDlg::OnCommandsOpencomputerAsftp() 
{
	int nItemIndex = m_list.GetCurrentSelectedItem();		
	if (nItemIndex == -1)
		return;

	status("Opening...");
	CString szIP = m_list.GetItemText(nItemIndex, CL_IP);
	szIP = "ftp://" + szIP + "/";	
	if ((int)ShellExecute(0, "open", szIP, NULL, NULL, SW_SHOWNORMAL) <= 32) 
	{
		MessageBox("This host cannot be opened (ShellExecute " + szIP + " failed)", NULL, MB_OK | MB_ICONHAND);
	}	
	status(NULL);
}

void CIpscanDlg::OnCommandsOpencomputerAswebsite() 
{
	int nItemIndex = m_list.GetCurrentSelectedItem();		
	if (nItemIndex == -1)
		return;

	status("Opening...");
	CString szIP = m_list.GetItemText(nItemIndex, CL_IP);
	szIP = "http://" + szIP + "/";	
	if ((int)ShellExecute(0, "open", szIP, NULL, NULL, SW_SHOWNORMAL) <= 32) 
	{
		MessageBox("This host cannot be opened (ShellExecute " + szIP + " failed)", NULL, MB_OK | MB_ICONHAND);
	}	
	status(NULL);
}

void CIpscanDlg::OnCommandsOpencomputerTelnet() 
{
	int nItemIndex = m_list.GetCurrentSelectedItem();		
	if (nItemIndex == -1)
		return;

	status("Opening...");
	CString szIP = m_list.GetItemText(nItemIndex, CL_IP);
	szIP = "telnet://" + szIP + "/";	
	if ((int)ShellExecute(0, "open", szIP, NULL, NULL, SW_SHOWNORMAL) <= 32) 
	{
		MessageBox("This host cannot be opened (ShellExecute " + szIP + " failed)", NULL, MB_OK | MB_ICONHAND);
	}	
	status(NULL);
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
	delete(m_pToolTips);

	// Delete the critical section object
	DeleteCriticalSection(&g_criticalSection);

	// Kill all threads of this process	
	// This will terminate the process for sure after closing the main window		
	ExitProcess(0);
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
		GetDlgItem(IDC_SELECT_COLUMNS)->ShowWindow(SW_HIDE);
		
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
		GetDlgItem(IDC_SELECT_COLUMNS)->ShowWindow(SW_SHOW);

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

	if (m_nScanMode != SCAN_MODE_NOT_SCANNING)
	{
		m_ctScanPorts.SetCheck(g_options->m_bScanPorts);
		return;
	}

	if (bChecked && g_options->m_aParsedPorts[0].nStartPort == 0)
	{
		AfxMessageBox("No ports selected", MB_ICONHAND | MB_OK, 0);
		m_ctScanPorts.SetCheck(FALSE);
		return;
	}

	g_options->m_bScanPorts = bChecked;

	m_list.SetScanPorts();	
}

void CIpscanDlg::OnSelectPortsClicked() 
{
	CPortDlg dlg;
	if (dlg.DoModal() == IDOK)
	{
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
}

void CIpscanDlg::OnCommandsShowIPdetails() 
{	
	m_list.ShowIPDetails();	
}

void CIpscanDlg::OnExecuteShowMenu(UINT nID)
{
	if (m_nScanMode != SCAN_MODE_NOT_SCANNING)
		return;

	m_menucuritem = m_list.GetCurrentSelectedItem();
	
	if (m_menucuritem == -1)
		return;

	int nFunctionIndex = nID - ID_MENU_SHOW_CMD_001 + CL_STATIC_COUNT;

	char szBuffer[256];

	CString szIP = m_list.GetItemText(m_menucuritem, CL_IP);

	status(szIP);

	g_scanner->runInitFunction(nFunctionIndex, TRUE);
	g_scanner->runScanFunction(m_list.GetNumericIP(m_menucuritem), nFunctionIndex, (char*) &szBuffer, sizeof(szBuffer), TRUE);
	g_scanner->runFinalizeFunction(nFunctionIndex, TRUE);

	status(NULL);

	CString szResult;	
	m_menuContext->GetSubMenu(INDEX_SHOW_MENU)->GetMenuString(nFunctionIndex - CL_STATIC_COUNT, szResult, MF_BYPOSITION);
		
	
	szResult = "Result for a single value scan:\r\n\r\nIP: " + szIP + "\r\n\r\n" + szResult + ":\r\n";	
	szResult += szBuffer;

	CMessageDlg cMsgDlg;
	cMsgDlg.setMessageText(szResult);

	cMsgDlg.DoModal();

}

BOOL CIpscanDlg::OnCommand(WPARAM wParam, LPARAM lParam) 
{
	// If ESC key was pressed, then do not exit!
	if ((wParam == 2) && (!m_bSysCommand))
	{
		return FALSE;
	}
	else
	{
		m_bSysCommand = FALSE;
		return CDialog::OnCommand(wParam, lParam);
	}
}

void CIpscanDlg::EnableMenuItems(BOOL bEnable)
{
	UINT nEnable = bEnable ? MF_ENABLED : MF_GRAYED;

	CMenu *tmpMnu = GetMenu();

	tmpMnu->EnableMenuItem(ID_OPTIONS_OPTIONS, nEnable);
	tmpMnu->EnableMenuItem(ID_OPTIONS_SELECT_COLUMNS, nEnable);
	tmpMnu->EnableMenuItem(ID_OPTIONS_SELECTPORTS, nEnable);	

	tmpMnu->EnableMenuItem(ID_SCAN_SAVETOTXT, nEnable);
	tmpMnu->EnableMenuItem(ID_SCAN_SAVESELECTION, nEnable);

	tmpMnu->EnableMenuItem(ID_UTILS_DELETEFROMLIST_DEADHOSTS, nEnable);
	tmpMnu->EnableMenuItem(ID_UTILS_DELETEFROMLIST_ALIVEHOSTS, nEnable);
	tmpMnu->EnableMenuItem(ID_UTILS_DELETEFROMLIST_CLOSEDPORTS, nEnable);
	tmpMnu->EnableMenuItem(ID_UTILS_DELETEFROMLIST_OPENPORTS, nEnable);

	tmpMnu->EnableMenuItem(ID_OPTIONS_INSTALL_PROGRAM, nEnable);
	
	tmpMnu->EnableMenuItem(ID_COMMANDS_RESCANIP, nEnable);	
	tmpMnu->EnableMenuItem(ID_COMMANDS_DELETEIP, nEnable);	

	tmpMnu->EnableMenuItem(ID_SHOWNETBIOSINFO, nEnable);
	
	for (UINT i=0; i < tmpMnu->GetMenuItemCount(); i++)
	{
		tmpMnu->EnableMenuItem(ID_MENU_SHOW_CMD_001 + i, nEnable);
	}

	GetDlgItem(IDC_SELECT_COLUMNS)->EnableWindow(bEnable);
	GetDlgItem(IDC_SELECT_PORTS)->EnableWindow(bEnable);
}

void CIpscanDlg::OnSelectColumns() 
{
	CSelectColumnsDlg cDlg;
	cDlg.DoModal();
}


void CIpscanDlg::OnUtilsDeletefromlistDeadhosts() 
{
	status("Deleting...");	
	CString szMessage;
	szMessage.Format("%d items were deleted.", m_list.DeleteAllDeadHosts());
	MessageBox(szMessage, NULL, MB_OK | MB_ICONINFORMATION);
	status(NULL);	// Ready
}

void CIpscanDlg::OnUtilsDeletefromlistAlivehosts() 
{	
	status("Deleting...");
	CString szMessage;
	szMessage.Format("%d items were deleted.", m_list.DeleteAllAliveHosts());
	MessageBox(szMessage, NULL, MB_OK | MB_ICONINFORMATION);	
	status(NULL);	// Ready
}

void CIpscanDlg::OnUtilsDeletefromlistClosedports() 
{
	if (!g_options->m_bScanPorts)
	{
		MessageBox("Port scanning is not enabled.", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	status("Deleting...");
	CString szMessage;
	szMessage.Format("%d items were deleted.", m_list.DeleteAllClosedPortsHosts());
	MessageBox(szMessage, NULL, MB_OK | MB_ICONINFORMATION);		
	status(NULL);	// Ready
}

void CIpscanDlg::OnUtilsDeletefromlistOpenports() 
{
	if (!g_options->m_bScanPorts)
	{
		MessageBox("Port scanning is not enabled.", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	status("Deleting...");
	CString szMessage;
	szMessage.Format("%d items were deleted.", m_list.DeleteAllOpenPortsHosts());
	MessageBox(szMessage, NULL, MB_OK | MB_ICONINFORMATION);		
	status(NULL);	// Ready	
}

void CIpscanDlg::OnRescanIP() 
{
	if (m_nScanMode != SCAN_MODE_NOT_SCANNING)
	{
		return;
	}

	if (m_list.GetCurrentSelectedItem(FALSE) == -1)
	{
		MessageBox("No items selected", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	POSITION pos = m_list.GetFirstSelectedItemPosition();
	int nItemIndex;	

	g_bScanExistingItems = TRUE;
	g_nStartItemIndex = 0;
	g_nEndItemIndex = 1;	// We will process items manually
	
	OnButtonScan();

	g_nEndItemIndex = g_nStartItemIndex;	// Timer will wait for completion only	

	while ((nItemIndex = m_list.GetNextSelectedItem(pos)) >= 0)
	{				
		CString szIP = m_list.GetItemText(nItemIndex, CL_IP);
		status(szIP);

		m_list.ZeroResultsForItem(nItemIndex);
						
		AfxBeginThread(ThreadProcCallbackRescan, (LPVOID) nItemIndex);

		Sleep(g_options->m_nTimerDelay);
	}		
	
}


void CIpscanDlg::OnCommandsDeleteIP() 
{
	if (m_nScanMode != SCAN_MODE_NOT_SCANNING)
	{
		MessageBox("Cannot delete while scanning", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	if (m_list.GetCurrentSelectedItem(FALSE) == -1)
	{
		MessageBox("No items selected", NULL, MB_OK | MB_ICONHAND);
		return;
	}	
	

	m_list.DeleteSelectedItems();
}

void CIpscanDlg::ShowCompleteInformation()
{
	// Show "Scanning complete" information box
	if (m_szCompleteInformation.GetLength() == 0)
		m_szCompleteInformation = "No last scan information yet.";

	CMessageDlg cMsgDlg;
	cMsgDlg.setMessageText(m_szCompleteInformation);
	cMsgDlg.DoModal();
}


void CIpscanDlg::KillAllRunningThreads()
{	
	EnterCriticalSection(&g_criticalSection);

	for (UINT i=0; i<=10000; i++) 
	{
		if (g_hThreads[i]!=0) 
		{
			TerminateThread(g_hThreads[i],0);
			CloseHandle(g_hThreads[i]);
			g_hThreads[i]=0;
		}
	}

	// All threads are dead now
	m_numthreads.SetWindowText("0");
	g_nThreadCount = 0;

	LeaveCriticalSection(&g_criticalSection);
}

void CIpscanDlg::OnClose() 
{
	// Terminate all threads
	if (m_nScanMode == SCAN_MODE_SCANNING)
		KillTimer(1);	// For safety

	KillAllRunningThreads();	

	CDialog::OnClose();
}
