// ipscanDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "ipscanDlg.h"
#include "OptionsDlg.h"
#include "SearchDlg.h"
#include "ms_icmp.h"
#include "link.h"
#include "CommandLine.h"
#include "SaveToFile.h"
#include <winbase.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

UINT numthreads;
HANDLE threads[10000];
//HANDLE *threads[];
UINT numalive,numopen;
UINT listofs,statusheight;
CIpscanDlg* d;
CWinApp *app;

int bSortAscending = 1;
int nSortedCol = -1;

int ThreadProcRescanThisIP = -1;


class CAboutDlg : public CDialog
{
public:
	int nextfree;	
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
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
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
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
	DDX_Control(pDX, IDC_HTTP, m_linkHomepage);
	DDX_Control(pDX, IDC_EMAIL, m_linkEmail);
	DDX_Control(pDX, IDC_TXTFREE, m_free);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
	ON_BN_CLICKED(IDOK, OnAboutOK)
	ON_WM_TIMER()
	ON_BN_CLICKED(IDC_EMAIL, OnGoemail)
	ON_BN_CLICKED(IDC_HTTP, OnGohttp)
	ON_WM_MOUSEMOVE()
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
	DDX_Control(pDX, IDC_BUTTONIPUP, m_ipup);
	DDX_Control(pDX, IDC_NUMTHREADS, m_numthreads);
	DDX_Control(pDX, IDC_PROGRESS, m_progress);
	DDX_Control(pDX, IDC_IPADDRESS2, m_ip2);
	DDX_Control(pDX, IDC_IPADDRESS1, m_ip1);
	DDX_Control(pDX, IDC_STATUS, m_statusctl);
	DDX_Control(pDX, IDC_LIST, m_list);
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
	ON_NOTIFY(HDN_ITEMCLICKW, 0, OnItemclickListHeader)
	ON_COMMAND(ID_HELP_COMMANDLINE, OnHelpCommandline)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()


FARPROC lpfnIcmpCreateFile;
typedef BOOL (FAR WINAPI *TIcmpCloseHandle)(HANDLE IcmpHandle);
TIcmpCloseHandle lpfnIcmpCloseHandle;
typedef DWORD (FAR WINAPI *TIcmpSendEcho)(
	HANDLE IcmpHandle, 	/* handle returned from IcmpCreateFile() */
    u_long DestAddress, /* destination IP address (in network order) */
    LPVOID RequestData, /* pointer to buffer to send */
    WORD RequestSize,	/* length of data in buffer */
    LPIPINFO RequestOptns,  /* see Note 2 */
    LPVOID ReplyBuffer, /* see Note 1 */
    DWORD ReplySize, 	/* length of reply (must allow at least 1 reply) */
    DWORD Timeout 	/* time in milliseconds to wait for reply */
);
TIcmpSendEcho lpfnIcmpSendEcho;
DWORD tmpproc;

char DataBuf[32];

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

	COptionsDlg::loadOptions(d);

	ThreadProcRescanThisIP = -1;
	
	// Add image list to the listbox
	m_imglist.Create(IDB_IMAGELIST,16,2,0xFFFFFF);
	m_list.SetImageList(&m_imglist,LVSIL_SMALL);
	
	// Add columns to the listbox
	CString str;
	int iCol;
	long w;
	for (iCol=0; iCol<C_COLUMNS; iCol++) {
		str.Format("Col%d",iCol);
		w = app->GetProfileInt("",str,-1);
		if (w==-1) {
			str.LoadString(IDS_FIRSTCOLUMN+iCol*2);
			w = strtol(str,NULL,0);
		}
		str.LoadString(IDS_FIRSTCOLUMN+iCol*2+1);
		m_list.InsertColumn(iCol,str,LVCFMT_LEFT,w,iCol);
	}

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
	
	// Set window size
	RECT rc;
	m_ipup.GetWindowRect(&rc); listofs = rc.bottom;
	m_ip1.GetWindowRect(&rc); listofs -= (rc.top-5);
	m_progress.GetWindowRect(&rc); statusheight = rc.bottom-rc.top-2;

	rc.left = app->GetProfileInt("","Left",0);
	rc.top = app->GetProfileInt("","Top",0);
	rc.bottom = app->GetProfileInt("","Bottom",0);
	rc.right = app->GetProfileInt("","Right",0);
    if (rc.right!=0) {
		SetWindowPos(NULL,rc.left,rc.top,rc.right-rc.left,rc.bottom-rc.top,SWP_NOZORDER);
	} else {
		SetWindowPos(NULL,0,0,502,350,SWP_NOMOVE | SWP_NOZORDER);
	}
	status("Ready");

	char hn[100];
	gethostname((char *)&hn,100);
	SetDlgItemText(IDC_HOSTNAME,hn);

	m_scanning=FALSE;
	numthreads=0;

	HMODULE hICMP = LoadLibrary("ICMP.DLL");
	if (!hICMP) {
		CString szTmp;
		szTmp.LoadString(IDS_SCAN_HOMEPAGE);
		szTmp = "ICMP.DLL is not found. Program will not work.\n"
		    	"You can find this DLL on Angry IP Scanner homepage:" + szTmp;
		MessageBox(szTmp,"Error",MB_OK | MB_ICONHAND);
		exit(666);
	}

	lpfnIcmpCreateFile  = (FARPROC)GetProcAddress(hICMP,"IcmpCreateFile");
    lpfnIcmpCloseHandle = (TIcmpCloseHandle)GetProcAddress(hICMP,"IcmpCloseHandle");
    lpfnIcmpSendEcho    = (TIcmpSendEcho)GetProcAddress(hICMP,"IcmpSendEcho");
	//tmpproc = (DWORD)lpfnIcmpSendEcho;

	mnu.LoadMenu(IDR_MENU1);
	ctx_item = mnu.GetSubMenu(2);
	ctx_noitem = mnu.GetSubMenu(1);

	for (int i=0; i<=32; i++) DataBuf[i]=i+65;

	str.LoadString(IDS_VERSION);
	SetWindowText("Angry IP Scanner "+str);

	m_ip2_virgin = TRUE;
	m_ip1.SetWindowText("0.0.0.0");

	hAccel = LoadAccelerators(AfxGetResourceHandle(), MAKEINTRESOURCE(IDR_MENU1));
	//m_menucuritem = -1;

	CCommandLine *cCmdLine = new CCommandLine();
	if (cCmdLine->process())
	{		
		m_ip1.SetWindowText(cCmdLine->m_szStartIP);
		m_ip2.SetWindowText(cCmdLine->m_szEndIP);
		m_ip2_virgin = FALSE;
		if (cCmdLine->m_szFilename.GetLength() > 0)
		{
			m_szDefaultFileName = new CString(cCmdLine->m_szFilename);
		}
		CIpscanDlg::OnButton1();
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
		m_list.MoveWindow(0, listofs, cx, cy-listofs-(statusheight+2), TRUE);
		m_statusctl.MoveWindow(0, cy-statusheight/*18*/, cx/2, /*18*/statusheight, TRUE);
		m_progress.MoveWindow(cx/2+1,cy-statusheight,cx/2-1,statusheight,TRUE);
	}
}

void CIpscanDlg::OnIpExit() 
{	
	SendMessage(WM_CLOSE,0,0);	
}

void CIpscanDlg::OnButton1() 
{
	//m_menucuritem = -1;
	if (!m_scanning) {
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
		//SetDlgItemText(IDC_BUTTON1,"Stop scan");
		((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)stopbmp.m_hObject);
		m_list.DeleteAllItems();

		CMenu *tmp = GetMenu();
		tmp->GetSubMenu(1)->EnableMenuItem(ID_OPTIONS_OPTIONS,MF_GRAYED);
		tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVETOTXT,MF_GRAYED);
		tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVESELECTION,MF_GRAYED);

		numthreads = 0;
		memset(&threads,0,sizeof(threads));
		numalive = 0;
		numopen = 0;

		SetTimer(1,m_delay,NULL);

	} else {

		if (numthreads!=0) {
			
			if (m_scanning==2) {
				if (MessageBox("Are you sure you want to interrupt scanning by killing all the threads?\nScanning results will be incomplete.",NULL,MB_YESNO | MB_ICONQUESTION)==IDNO) return;
			
				for (UINT i=0; i<=10000; i++) {
					if (threads[i]!=0) {
						TerminateThread(threads[i],0);
						CloseHandle(threads[i]);
						threads[i]=0;
					}
				}
				m_numthreads.SetWindowText("0");
				numthreads=0;
				goto finish_all;
			}

			m_endip = m_curip;
			m_progress.SetPos(100);
			m_scanning = 2;
		} else {
finish_all:
			KillTimer(1);
			m_scanning=FALSE;
			//SetDlgItemText(IDC_BUTTON1,"Start scan");
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)startbmp.m_hObject);
			status("Ready");
			//GlobalFree(threads);

			CMenu *tmp = GetMenu();
			tmp->GetSubMenu(1)->EnableMenuItem(ID_OPTIONS_OPTIONS,MF_ENABLED);
			tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVETOTXT,MF_ENABLED);
			tmp->GetSubMenu(0)->EnableMenuItem(ID_SCAN_SAVESELECTION,MF_ENABLED);

			m_progress.SetPos(0);

			char str[140],ipa[16],ipa2[16],*ipp;
			in_addr in;
			in.S_un.S_addr = htonl(m_startip);
			ipp = inet_ntoa(in);
			strcpy((char*)&ipa,ipp);
			in.S_un.S_addr = htonl(m_endip);
			ipp = inet_ntoa(in);
			strcpy((char*)&ipa2,ipp);
			sprintf((char*)&str,"Scan complete\t\t\n\n%s - %s\n%u second(s)\n\nIPs scanned: %u\nAlive hosts: %u\nOpen ports: %u",&ipa,(char*)&ipa2,GetTickCount()/1000-m_tickcount+1,m_endip-m_startip+1,numalive,numopen);
			MessageBox((char*)&str,"Info",MB_OK | MB_ICONINFORMATION);
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
	dlgOpt.m_delay=m_delay;
	dlgOpt.m_port=m_port;
	dlgOpt.m_resolve=m_resolve;
	dlgOpt.m_scanport=m_scanport;
	dlgOpt.m_retrifdead=m_retrifdead;
	dlgOpt.m_portondead=m_portondead;
	dlgOpt.m_maxthreads=m_maxthreads;
	dlgOpt.m_timeout=m_timeout;
	dlgOpt.m_display=m_display;
	dlgOpt.DoModal();
	m_delay=dlgOpt.m_delay;
	m_port=dlgOpt.m_port;
	m_resolve=dlgOpt.m_resolve;
	m_scanport=dlgOpt.m_scanport;
	m_retrifdead=dlgOpt.m_retrifdead;
	m_portondead=dlgOpt.m_portondead;
	m_maxthreads=dlgOpt.m_maxthreads;
	m_timeout=dlgOpt.m_timeout;
	m_display=dlgOpt.m_display;
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

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////

UINT ThreadProc(LPVOID cur_ip)
{
	numthreads++;
	int index;
	int n;

	for (index=0; index<=10000; index++) {
		if (threads[index]==0) { 
			HANDLE tmp;
			DuplicateHandle(GetCurrentProcess(),GetCurrentThread(),GetCurrentProcess(),&tmp,NULL,TRUE,DUPLICATE_SAME_ACCESS);
			threads[index] = tmp;  
			break; 
		}
	}
	
	char err[20];
	sprintf((char*)&err,"%d",numthreads);
	d->m_numthreads.SetWindowText((char*)&err);
	
	char *ipa;
	in_addr in;
	in.S_un.S_addr = htonl((UINT)cur_ip);
	ipa = inet_ntoa(in);
	//d->status(ipa);
	if (ThreadProcRescanThisIP >= 0) {
		n = ThreadProcRescanThisIP; 
	} else {
		n = (UINT)cur_ip - d->m_startip;// - 1; //d->m_list.GetItemCount();
	}
	
	HANDLE hICMP = (HANDLE) lpfnIcmpCreateFile();

	unsigned char RepData[sizeof(ICMPECHO)+100];
	IPINFO IPInfo;
	IPInfo.Ttl = 64;
    IPInfo.Tos = 0;
    IPInfo.Flags = 0;
    IPInfo.OptionsSize = 0;
    IPInfo.OptionsData = NULL;
	DWORD ReplyCount;
	ReplyCount = lpfnIcmpSendEcho(hICMP, in.S_un.S_addr, DataBuf, 32, 
		&IPInfo, RepData, sizeof(RepData), d->m_timeout);
	if (!ReplyCount) {
		sprintf((char*)&err,"%u",WSAGetLastError());
dead_host:
		if (d->m_display!=DO_ALL) {
			//d->m_list.DeleteItem(n);
			goto exit_thread;
		} 
		//d->m_list.InsertItem(n,ipa,1); 
		d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,1,0,0,0);
		d->m_list.SetItem(n,CL_STATE,LVIF_TEXT,"Dead",0,0,0,0);
		if (d->m_retrifdead) {
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) {
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} else {
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}
		} else {
			d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0);
			d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
		}
		d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/A",0,0,0,0);
		d->m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,"N/A",0,0,0,0);
		if (d->m_portondead) goto scan_port;
	} else {
		// Alive
		ReplyCount = RepData[4]+RepData[5]*256+RepData[6]*65536+RepData[7]*256*65536;
		if (ReplyCount>0) {
			sprintf((char*)&err,"%u",ReplyCount);
			goto dead_host;
		}
		if (d->m_display!=DO_ALL && ThreadProcRescanThisIP == -1) {
			n = d->m_list.InsertItem(n,ipa,0); 
			//d->m_list.SetItemData(n, n);
		}
		numalive++;
		d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,0,0,0,0);
		d->m_list.SetItem(n,CL_STATE,LVIF_TEXT,"Alive",0,0,0,0);
		sprintf((char*)&err,"%d ms",*(u_long *) &(RepData[8]));
		d->m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,(char*)&err,0,0,0,0);
		if (d->m_resolve) {
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) {
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} else {
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}
		} else {
			d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/S",0,0,0,0); 
			//d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
		}
scan_port:
		if (d->m_scanport) {
			// Scan port
			SOCKET skt = socket(PF_INET,SOCK_STREAM,IPPROTO_IP);
			sockaddr_in sin;
			sin.sin_addr.S_un.S_addr = in.S_un.S_addr;
			sin.sin_family = PF_INET;
			sin.sin_port = htons(d->m_port);
			int se = connect(skt,(sockaddr*)&sin,sizeof(sin));
			if (se!=0) {
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
				sprintf((char*)&err,"%u: closed",d->m_port);
				d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
			} else {
				numopen++;
				sprintf((char*)&err,"%u: open",d->m_port);
				d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
				d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,3,0,0,0);				
			}
			closesocket(skt);

		} else d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/S",0,0,0,0);
	}

exit_thread:

	numthreads--;
	if (numthreads>=0) {
		sprintf((char*)&err,"%d",numthreads);
		d->m_numthreads.SetWindowText((char*)&err);
	}

	threads[index]=0;

	return 0;
}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////


void CIpscanDlg::OnTimer(UINT nIDEvent) 
{	
	 	
	int i;
	
	if (m_curip<m_endip) {
		if (numthreads>=m_maxthreads-1) return;
		in_addr in;
		char *ipa;
		in.S_un.S_addr = htonl(m_curip);
		ipa = inet_ntoa(in);
		status(ipa);
		if (m_display==DO_ALL) {
			i = m_list.InsertItem(m_list.GetItemCount(),ipa,2);
			//m_list.SetItemData(i, i);
		}
		CWinThread *thr = AfxBeginThread(ThreadProc,(void*)m_curip);
		if (m_startip < m_endip) {
			m_curip++;
			m_progress.SetPos((m_curip-m_startip)*100/(m_endip-m_startip));
		}
	} else {
	
		if (numthreads==0) {
			m_endip--;
			OnButton1();
			
			return;
		} else {
			status("Wait for all threads to terminate");
			((CButton*)GetDlgItem(IDC_BUTTON1))->SetBitmap((HBITMAP)killbmp.m_hObject);
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

	SetTimer(0,15000,NULL);
	
	return TRUE;  
}

void CAboutDlg::OnAboutOK() 
{
	KillTimer(0); KillTimer(1);
	EndDialog(IDOK);
}

void CAboutDlg::OnTimer(UINT nIDEvent) 
{
	if (nIDEvent==0) {
		KillTimer(0);
		SetTimer(1,500,NULL);
		nextfree=1;
		
	} else {
		CString tmp;
		switch (nextfree) {
			case 0: tmp = "free"; break;
			case 1: tmp = "FREE"; break;
			case 2: tmp = "F R E E"; break;
			case 3: tmp = "FREE"; break;
			case 4: tmp = "<FREE>"; break;
			case 5: tmp = "[FREE]"; break;
			case 6: tmp = "*FREE*"; break;
			case 7: tmp = "FREE"; break;
			case 8: tmp = "FRE E"; break;
			case 9: tmp = "FR E E"; break;
			case 10: tmp = "F R E E"; break;
			case 11: tmp = "F R EE"; break;
			case 12: tmp = "F REE"; break;
			case 13: tmp = "FREE"; break;
			case 14: tmp = "FREEE"; break;
			case 15: tmp = "FREEEE"; break;
			case 16: tmp = "FREEEEE"; break;
			case 17: tmp = "FREEEEEE"; break;
			case 18: tmp = "COOOOOOL"; break;
			case 19: tmp = "COOOOOL"; break;
			case 20: tmp = "COOOOL"; break;
			case 21: tmp = "COOOL"; break;
			case 22: tmp = "COOL"; break;
			case 23: tmp = "FREE"; break;
			case 24: tmp = " FREE"; break;
			case 25: tmp = "> FREE"; break;
			case 26: tmp = "=> FREE"; break;
			case 27: tmp = "==> FREE"; break;
			case 28: tmp = "===> FREE"; break;
			case 29: tmp = "====> FREE"; break;
			case 30: tmp = "=====> FREE"; break;
			case 31: tmp = "=====< FREE"; break;
			case 32: tmp = "====< FREE"; break;
			case 33: tmp = "===< FREE"; break;
			case 34: tmp = "==< FREE"; break;
			case 35: tmp = "=< FREE"; break;
			case 36: tmp = "< FREE"; break;
			case 37: tmp = "FREE!!!"; nextfree=-1; break;
		}
		nextfree++;
		m_free.SetWindowText(tmp);
	}
	
	CDialog::OnTimer(nIDEvent);
}

void CIpscanDlg::OnShowWindow(BOOL bShow, UINT nStatus) 
{
	CDialog::OnShowWindow(bShow, nStatus);
}

void CIpscanDlg::OnOptionsSaveoptions() 
{	
	COptionsDlg::saveOptions(d);
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
	m_ip1.GetAddress(ip); ipc[0]=1; m_ip1.SetAddress(ip);
	m_ip2.GetAddress(ip); ipc[0]=255; m_ip2.SetAddress(ip);
	m_ip2_virgin=FALSE;
}

void CIpscanDlg::OnClassD() 
{
	DWORD ip;
	char *ipc = (char*)&ip;
	m_ip1.GetAddress(ip); ipc[0]=1; ipc[1]=0; m_ip1.SetAddress(ip);
	m_ip2.GetAddress(ip); ipc[0]=255; ipc[1]=255; m_ip2.SetAddress(ip);
	m_ip2_virgin=FALSE;
}

void CIpscanDlg::OnWindozesucksShownetbiosinfo() 
{
	POSITION pos = m_list.GetFirstSelectedItemPosition();
	m_menucuritem = m_list.GetNextSelectedItem(pos);
	if (m_menucuritem<0) { ErrorNotSelected();return;}
	char ipstr[16],str[600],comspec[600],tempdir[60];
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	FILE *f;
	char *tmp;

	status("Getting info...");
	m_list.GetItemText(m_menucuritem,CL_IP,(char*)&ipstr,16);
	GetEnvironmentVariable("COMSPEC",(char*)&comspec,sizeof(comspec));
	GetTempPath(sizeof(tempdir),(char*)&tempdir);
	strcat((char*)&tempdir,"ipsnbt.tmp");
	DeleteFile((char*)&tempdir);
	sprintf((char*)&str,"%s /c nbtstat.exe -A %s >%s",(char*)comspec,(char*)&ipstr,(char*)&tempdir);
	FillMemory(&si,sizeof(si),0);
	si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;
	if (!CreateProcess(NULL,(char*)&str,NULL,NULL,TRUE,0,NULL,NULL,&si,&pi)) {
		MessageBox("Windows networking is not installed","Error",MB_OK | MB_ICONERROR);
		goto exit_func;
	}
	WaitForSingleObject(pi.hProcess,3000);
	f = fopen((char*)&tempdir,"rt");
	if (!f) {
		MessageBox("Unknown error or windows networking is not installed","Error",MB_OK | MB_ICONERROR);
		goto exit_func;
	}
//	while (str[0]!='-' && !feof(f)) fgets((char*)&str,sizeof(str),f);
//	if (feof(f)) { 
//		MessageBox("NetBIOS service is not running on the specified host or host is dead","Error",MB_OK | MB_ICONERROR);
//		goto exit_func;
//	}
	/*int tmp=1;
	while (!foef(f)) {
		fgets((char*)&str,sizeof(str),f);
		if strstr((char*)&str,">  UNIQUE") {
			if
		}
	}*/

	int charindex;
	
	while (!strstr((char*)&str,">  UNIQUE") && !feof(f)) fgets((char*)&str,sizeof(str),f);
	if (feof(f)) { 
		MessageBox("NetBIOS service is not running on the specified host or host is dead","Error",MB_OK | MB_ICONERROR);
		goto exit_func;
	}
	for (charindex = 0; str[charindex] != 0 && str[charindex] != '<'; charindex++);
	str[charindex]=0; sprintf((char*)&comspec,"NetBIOS info for %s\n\nName:\t\t%s\nWorkgroup:\t",(char*)&ipstr,(char*)&str);
	while (!strstr((char*)&str,">  GROUP")) fgets((char*)&str,sizeof(str),f);
	
	str[charindex]=0; strcat((char*)&comspec,(char*)&str);
	ipstr[0]=0;
	for(;;) {
		while ((strstr((char*)&str,"<03>  UNIQUE")==0 && (str[0]!=10 && str[0]!=13)) || strchr((char*)&str,'$')!=0) {
			fgets((char*)&str,sizeof(str),f);
		}
		if (str[0]==10 || str[0]==13) break;
		str[charindex]=0; strcpy((char*)&ipstr,(char*)&str);
	} 
	strcat((char*)&comspec,"\nUsername:\t");
	if (ipstr[0]==0) 
		strcat((char*)&comspec,"<Unknown>");
	else
		strcat((char*)&comspec,(char*)&ipstr);
	fseek(f,0,0);
	while (!(tmp = strstr((char*)&str,"= ")) && !feof(f)) fgets((char*)&str,sizeof(str),f);
	tmp+=2;
	strcat((char*)&comspec,"\nMAC Address:\t");
	strcat((char*)&comspec,tmp);
	fclose(f);
	DeleteFile((char*)&tempdir);
	MessageBox((char*)&comspec,"NetBIOS",MB_OK | MB_ICONINFORMATION);
exit_func:
	status("Ready");
}

void CAboutDlg::OnMouseMove(UINT nFlags, CPoint point) 
{	
	CDialog::OnMouseMove(nFlags, point);
}

void CIpscanDlg::OnHelpAngryipscannerwebpage() 
{	
	CLink::goToScannerHomepage();	
}

void CIpscanDlg::OnHelpAngryzibersoftware() 
{	
	CLink::goToHomepage();
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
		
		numthreads = 0;
		
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
		ThreadProcRescanThisIP = m_menucuritem;
		ThreadProc((void*)m_curip);
		ThreadProcRescanThisIP = -1;
		
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
    MessageBox("Note: these commands are provided for your convenience only. They are not "
		       "guaranteed to work. They just try to execute specified commands using "
			   "Windows Shell API to see if any other program is assotsiated with "
			   "that action. Please don't mail me with questions, why these don't "
			   "work. If you know what they should do, then you can setup your "
			   "system yourself to handle URL requests.",NULL,MB_OK | MB_ICONWARNING);	
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
