// ipscanDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "ipscanDlg.h"
#include "OptionsDlg.h"
#include "ms_icmp.h"
//#include "ipscan.cpp"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

int numthreads;
CIpscanDlg* d;

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	CStatic	m_email;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	public:
	virtual BOOL Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext = NULL);
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	virtual BOOL OnInitDialog();
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
	DDX_Control(pDX, IDC_EMAIL, m_email);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
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
}

void CIpscanDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CIpscanDlg)
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
	
	// TODO: Add extra initialization here

	// Load default options
	m_delay=20;
	m_port=139;
	m_resolve=TRUE;
	m_scanport=FALSE;
	m_retrifdead=FALSE;

	// Add image list to the listbox
	m_imglist.Create(IDB_IMAGELIST,16,2,0xFFFFFF);
	m_list.SetImageList(&m_imglist,LVSIL_SMALL);
	
	// Add columns to the listbox
	CString str;
	int iCol;
	long w;
	for (iCol=0; iCol<C_COLUMNS; iCol++) {
		str.LoadString(IDS_FIRSTCOLUMN+iCol*2);
		w = strtol(str,NULL,0);
		str.LoadString(IDS_FIRSTCOLUMN+iCol*2+1);
		m_list.InsertColumn(iCol,str,LVCFMT_LEFT,w,iCol);
	}

	// Set window size
	SetWindowPos(NULL,0,0,500,350,SWP_NOMOVE | SWP_NOZORDER);
	status("Ready");

	char hn[100];
	gethostname((char *)&hn,100);
	SetDlgItemText(IDC_HOSTNAME,hn);

	m_scanning=FALSE;
	numthreads=0;

	HMODULE hICMP = LoadLibrary("ICMP.DLL");
	if (!hICMP) {
		MessageBox("ICMP.DLL is not found. Program will not work.","Error",MB_OK | MB_ICONHAND);
		exit(1);
	}

	lpfnIcmpCreateFile  = (FARPROC)GetProcAddress(hICMP,"IcmpCreateFile");
    lpfnIcmpCloseHandle = (TIcmpCloseHandle)GetProcAddress(hICMP,"IcmpCloseHandle");
    lpfnIcmpSendEcho    = (TIcmpSendEcho)GetProcAddress(hICMP,"IcmpSendEcho");
	//tmpproc = (DWORD)lpfnIcmpSendEcho;

	//app = AfxGetApp();
	d = (CIpscanDlg*)AfxGetApp()->m_pMainWnd;

	mnu.LoadMenu(IDR_CTX);
	ctx_item = mnu.GetSubMenu(0);

	for (int i=0; i<=32; i++) DataBuf[i]=i+65;
	
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

void CIpscanDlg::status(LPCSTR str) {
	SetDlgItemText(IDC_STATUS,str);
}

void CIpscanDlg::OnSize(UINT nType, int cx, int cy) 
{
	CDialog::OnSize(nType, cx, cy);
	
	// TODO: Add your message handler code here
	if (m_list.m_hWnd!=NULL) {
		m_list.MoveWindow(0, 50, cx, cy-50-20, TRUE);
		m_statusctl.MoveWindow(0, cy-18, cx/2, 18, TRUE);
		m_progress.MoveWindow(cx/2+1,cy-18,cx/2-1,18,TRUE);
	}
}

void CIpscanDlg::OnIpExit() 
{
	// TODO: Add your command handler code here
	SendMessage(WM_CLOSE,0,0);
	
}

void CIpscanDlg::OnButton1() 
{
	// TODO: Add your control notification handler code here
	
	if (!m_scanning) {
		char str[16];
		m_ip1.GetWindowText((char *)&str,16);
		m_startip = ntohl(inet_addr((char*)&str));
		m_ip2.GetWindowText((char *)&str,16);
		m_endip = ntohl(inet_addr((char*)&str));

		if (m_endip<m_startip) {
			MessageBox("Ending IP address is lower than starting.",NULL,MB_OK | MB_ICONHAND);
			return;
		}

		m_curip = m_startip;
		m_progress.SetRange(0,100);
		m_progress.SetPos(0);
		m_tickcount = GetTickCount()/1000;

		m_scanning=TRUE;
		SetDlgItemText(IDC_BUTTON1,"Stop scan");
		m_list.DeleteAllItems();

		CMenu *tmp = GetMenu();
		tmp = tmp->GetSubMenu(1);
		tmp->EnableMenuItem(0,MF_GRAYED);

		SetTimer(1,m_delay,NULL);

	} else {
		KillTimer(1);
		m_scanning=FALSE;
		SetDlgItemText(IDC_BUTTON1,"Start scan");
		status("Ready");
		
		CMenu *tmp = GetMenu();
		tmp = tmp->GetSubMenu(1);
		tmp->EnableMenuItem(0,MF_ENABLED);

		m_progress.SetPos(0);
	}
}

void CIpscanDlg::OnHelpAbout() 
{
	// TODO: Add your command handler code here
	CAboutDlg dlgAbout;
	dlgAbout.DoModal();
}

void CIpscanDlg::OnOptionsOptions() 
{
	// TODO: Add your command handler code here
	COptionsDlg dlgOpt;
	dlgOpt.m_delay=m_delay;
	dlgOpt.m_port=m_port;
	dlgOpt.m_resolve=m_resolve;
	dlgOpt.m_scanport=m_scanport;
	dlgOpt.m_retrifdead=m_retrifdead;
	dlgOpt.DoModal();
	m_delay=dlgOpt.m_delay;
	m_port=dlgOpt.m_port;
	m_resolve=dlgOpt.m_resolve;
	m_scanport=dlgOpt.m_scanport;
	m_retrifdead=dlgOpt.m_retrifdead;
}

void CIpscanDlg::OnButtonipup() 
{
	// TODO: Add your control notification handler code here
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
}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////

UINT ThreadProc(LPVOID pParam)
{
	numthreads++;
	//dlg->m_list.InsertItem(dlg->m_list.GetItemCount(),"thread",0);
	numthreads--;
	return 0;
}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////


void CIpscanDlg::OnTimer(UINT nIDEvent) 
{
	// TODO: Add your message handler code here and/or call default
	 
	CWinThread *thr = AfxBeginThread(ThreadProc,0);
	char *ipa;
	in_addr in;
	in.S_un.S_addr = htonl(m_curip);
	ipa = inet_ntoa(in);
	status(ipa);
	int n=m_list.GetItemCount();
	char err[10];
	
	HANDLE hICMP = (HANDLE) lpfnIcmpCreateFile();

	char RepData[sizeof(ICMPECHO)+100];
	IPINFO IPInfo;
	IPInfo.Ttl = 64;
    IPInfo.Tos = 0;
    IPInfo.Flags = 0;
    IPInfo.OptionsSize = 0;
    IPInfo.OptionsData = NULL;
	DWORD ReplyCount;
	ReplyCount = lpfnIcmpSendEcho(hICMP, in.S_un.S_addr, DataBuf, 32, 
		&IPInfo, RepData, sizeof(RepData), 3000);
	if (!ReplyCount) {
		sprintf((char*)&err,"%u",WSAGetLastError());
		m_list.InsertItem(n,ipa,1); 
		m_list.SetItem(n,CL_STATE,LVIF_TEXT,"Dead",0,0,0,0);
		if (m_retrifdead) {
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) {
				m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} else {
				sprintf((char*)&err,"%u",WSAGetLastError());
				m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}
		} else {
			m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0);
			m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
		}
		m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/A",0,0,0,0);
		m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,"N/A",0,0,0,0);
	} else {
		m_list.InsertItem(n,ipa,0); 
		m_list.SetItem(n,CL_STATE,LVIF_TEXT,"Alive",0,0,0,0);
		sprintf((char*)&err,"%d ms",*(u_long *) &(RepData[8]));
		m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,(char*)&err,0,0,0,0);
		if (m_resolve) {
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) {
				m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} else {
				sprintf((char*)&err,"%u",WSAGetLastError());
				m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}
		} else {
			m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/S",0,0,0,0); 
			//m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
		}
		if (m_scanport) {
			// Scan port
			SOCKET skt = socket(PF_INET,SOCK_STREAM,IPPROTO_IP);
			sockaddr_in sin;
			sin.sin_addr.S_un.S_addr = in.S_un.S_addr;
			sin.sin_family = PF_INET;
			sin.sin_port = htons(m_port);
			int se = connect(skt,(sockaddr*)&sin,sizeof(sin));
			if (se!=0) {
				sprintf((char*)&err,"%u",WSAGetLastError());
				m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
				sprintf((char*)&err,"%u: closed",m_port);
				m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
			} else {
				sprintf((char*)&err,"%u: open",m_port);
				m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
				closesocket(skt);
			}

		} else m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/S",0,0,0,0);
	}

	if (m_curip==m_endip) {
		OnButton1();
		char str[80],ipa2[16];
		strcpy((char*)&ipa2,ipa);
		in.S_un.S_addr = htonl(m_startip);
		ipa = inet_ntoa(in);
		sprintf((char*)&str,"Scan complete.\nScanned %u IP addresses\nfrom %s to %s\nin %u second(s)",m_endip-m_startip+1,ipa,(char*)&ipa2,GetTickCount()/1000-m_tickcount+1);
		MessageBox((char*)&str,NULL,MB_OK | MB_ICONINFORMATION);
		return;
	}
	
    m_curip++;
	m_progress.SetPos((m_curip-m_startip)*100/(m_endip-m_startip));

	CDialog::OnTimer(nIDEvent);
}


void CIpscanDlg::OnScanSavetotxt() 
{
	// TODO: Add your command handler code here
	
}


void CIpscanDlg::OnButton2()  // Error Str...
{
	// TODO: Add your control notification handler code here
	if (m_list.GetSelectedCount()!=1) {
		MessageBox("Select only one item.",NULL,MB_OK | MB_ICONHAND);
		return;
	}
	for (UINT i=0; i<m_list.GetSelectedCount(); i++) {
		if (m_list.GetItemState(i,0)) {
			char str[50],text[6];
			m_list.GetItemText(i,CL_ERROR,(char*)&text,6);
			sprintf((char*)&str,"Error %s: %s",text,"bla-bla-bla");
			MessageBox((char*)&str,NULL,MB_OK | MB_ICONINFORMATION);
			return;
		}

	}
	MessageBox("You must select an item, to get error information.",NULL,MB_OK | MB_ICONHAND);
}


void CIpscanDlg::OnRclickList(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NMLISTVIEW *lw = (NMLISTVIEW*)pNMHDR;
	POINT p; 
	GetCursorPos(&p);
	TrackPopupMenu(ctx_item->m_hMenu,TPM_LEFTALIGN | TPM_RIGHTBUTTON,p.x,p.y,0,m_hWnd,NULL);
	m_menucuritem = lw->iItem;
	*pResult = 0;
}

void CIpscanDlg::OnOpencomputerinexplorer() 
{
	char str[22],str2[16];
	m_list.GetItemText(m_menucuritem,CL_IP,str2,16);
	sprintf((char*)&str,"\\\\%s\\",(char*)&str2);
	ShellExecute(0,NULL,(char*)&str,NULL,NULL,SW_SHOWNORMAL);
}

void CIpscanDlg::OnShowerrordescription() 
{
	// TODO: Add your command handler code here
	CString err = m_list.GetItemText(m_menucuritem,CL_ERROR);
	CString str;
	int ern;
	if (strcmp(err,"None")==0) str = "No error"; else {
		ern = strtol(err,NULL,10);
		switch (ern) {
			case WSAENETDOWN: str = "Network is down"; break;
			case WSAHOST_NOT_FOUND: str = "Authoritative Host not found"; break;
// 11002		case WSATRY_AGAIN: str = "Non-Authoritative Host not found, or server failed"; break;
			case WSANO_DATA: str = "Valid address, but no DNS record"; break;
			case WSAEADDRNOTAVAIL: str = "Address is not available from this machine"; break;
			case WSAECONNREFUSED: str = "Connection is refused"; break;
			case WSAENETUNREACH: str = "The network cannot be reached"; break;
			case WSAETIMEDOUT: str = "Connection request timed out"; break;
			case 11002: str = "The network is unreachable"; break;
			case 11003: str = "The Host is unreachable"; break;
// WSANO_DATA		case 11004: str = "The protocol is unreachable"; break;
			case 11005: str = "The port is unreachable"; break;
			case 11010: str = "Request timed out"; break;
			case 11018: str = "Bad destination"; break;
			default: str = "Unknown error.\nFor description you may look in WSA help file or send email to me (see About)"; break;
		}
	}
	MessageBox(str,NULL,MB_OK | MB_ICONINFORMATION);
}

void CIpscanDlg::OnWindozesucksIpclipboard() 
{
	// TODO: Add your command handler code here
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

BOOL CAboutDlg::Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext) 
{
	// TODO: Add your specialized code here and/or call the base class
	
	return CDialog::Create(IDD, pParentWnd);
}

BOOL CAboutDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	// TODO: Add extra initialization here
	

	return TRUE;  // return TRUE unless you set the focus to a control
	              // EXCEPTION: OCX Property Pages should return FALSE
}
