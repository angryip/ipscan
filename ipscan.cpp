// ipscan.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "ipscan.h"
#include "ipscanDlg.h"
#include "link.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CIpscanApp

BEGIN_MESSAGE_MAP(CIpscanApp, CWinApp)
	//{{AFX_MSG_MAP(CIpscanApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIpscanApp construction

CIpscanApp::CIpscanApp()
{	
	#ifdef DEBUG_MESSAGES
		AfxMessageBox("App constructor", 0, 0);
	#endif
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CIpscanApp object

CIpscanApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CIpscanApp initialization

BOOL CIpscanApp::InitInstance()
{
	if (!AfxSocketInit())
	{
		AfxMessageBox(IDP_SOCKETS_INIT_FAILED);
		return FALSE;
	}

#ifdef DEBUG_MESSAGES
	AfxMessageBox("InitInstance() start", 0, 0);
#endif

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	//  of your final executable, you should remove from the following
	//  the specific initialization routines you do not need.

#ifdef _AFXDLL
	Enable3dControls();			// Call this when using MFC in a shared DLL
#else
	Enable3dControlsStatic();	// Call this when linking to MFC statically
#endif

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("InitInstance(): 3D Controls enabled", 0, 0);
	#endif

	CIpscanDlg dlg;
	m_pMainWnd = &dlg;

	theApp.SetRegistryKey("Angryziber");	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("InitInstance(): Before DoModal()", 0, 0);
	#endif

	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		//  dismissed with OK
	}
	else if (nResponse == IDCANCEL)
	{
		//  dismissed with Cancel
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

int CIpscanApp::Run() 
{
	#ifdef DEBUG_MESSAGES
		AfxMessageBox("Run()", 0, 0);
	#endif
	
	int nReturn = CWinApp::Run();	

	return nReturn;
}

#define VERSION_CHECK_HOST		"www.angryziber.com"
#define VERSION_CHECK_PORT		80
#define VERSION_CHECK_REQUEST	"GET /ipscan/IPSCAN.VERSION HTTP/1.0\nHost: www.angryziber.com\n\n"
#define VERSION_CHECK_PREFIX	"Version: "

// This function checks for newer version of Angry IP Scanner
void CIpscanApp::CheckForNewerVersion()
{
	SOCKET hSocket; 

	CIpscanDlg *dlg = (CIpscanDlg*) AfxGetApp()->GetMainWnd();

	// Initialize the socket
	hSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_IP);

	if (hSocket == INVALID_SOCKET)
	{
		// Return FALSE in case of an error
		AfxMessageBox("Unable to create socket", MB_OK | MB_ICONHAND, 0);
		return;
	}

	dlg->status("Retrieving information...");

	hostent *pHostent = gethostbyname(VERSION_CHECK_HOST);

	if (!pHostent)
	{
		AfxMessageBox("Sorry, the server is not currently available.", MB_OK | MB_ICONINFORMATION, 0);
		dlg->status(NULL);
		return;
	}
		
	sockaddr_in sin;
	memcpy(&sin.sin_addr.S_un.S_addr, *pHostent->h_addr_list, sizeof(sin.sin_addr.S_un.S_addr));
	sin.sin_family = PF_INET;
	
	sin.sin_port = htons(VERSION_CHECK_PORT);

	fd_set fd_read, fd_write, fd_error;
	timeval timeout;
	timeout.tv_sec = 5;		// 5 seconds timeout
	timeout.tv_usec = 0;
	u_long nNonBlocking = 1;	

	// Set socket to non-blocking mode
	ioctlsocket(hSocket, FIONBIO, &nNonBlocking);

	BOOL bConnected = FALSE;

	// Estabilish a TCP connection
	connect(hSocket, (sockaddr*)&sin, sizeof(sin));

	fd_write.fd_array[0] = hSocket; fd_write.fd_count = 1;
	fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;
	if (select(0, 0, &fd_write, &fd_error, &timeout) > 0) 
	{
		if (fd_write.fd_count > 0)
		{
			// Connection successful
			bConnected = TRUE;
		}
	}			
	
	if (!bConnected)
	{
		AfxMessageBox("Sorry, the server is not currently available.", MB_ICONHAND | MB_OK, 0);
		closesocket(hSocket);	
		dlg->status(NULL);
		return;
	}
	else
	{
		// Send the magic http request
		send(hSocket, VERSION_CHECK_REQUEST, strlen(VERSION_CHECK_REQUEST), 0);

		// Receive http headers back
		char szResponse[512];

		fd_read.fd_array[0] = hSocket; fd_read.fd_count = 1;
		fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;

		char *szBufferPointer = (char*) &szResponse;

		while (select(0, &fd_read, 0, &fd_error, &timeout) > 0)
		{
			if (fd_read.fd_count > 0)
			{
				int nNumRead = recv(hSocket, szBufferPointer, sizeof(szResponse) - (szBufferPointer - (char*)&szResponse), 0);
				
				if (nNumRead <= 0)
					break;
				else
					szBufferPointer += nNumRead;
			}
			else
			{
				break;
			}
		}

		// Get the version
		char *szHeaderStart, *szHeaderEnd;

		// Find needed HTTP header 
		szHeaderStart = strstr(szResponse, VERSION_CHECK_PREFIX);

		if (!szHeaderStart)
		{
			// Header not found
			AfxMessageBox("Unable to retrieve version information. Try again later.", MB_OK | MB_ICONHAND, 0);
			closesocket(hSocket);	
			dlg->status(NULL);
			return;
		}

		// Skip whitespace symbol
		if (szHeaderStart[0] == ' ' || szHeaderStart[0] == '\t')
			szHeaderStart++;

		szHeaderEnd = min(strchr(szHeaderStart, '\n'), strchr(szHeaderStart, '\r'));
		
		szHeaderEnd[0] = NULL;	// Make it end

		char szLatestVersion[16];
		strcpy((char*)&szLatestVersion, (char *)(szHeaderStart + strlen(VERSION_CHECK_PREFIX)));	// Skip prefix

		// Load the current version
		CString szCurrentVersion;
		szCurrentVersion.LoadString(IDS_VERSION);

		if (szCurrentVersion.Compare((char*)&szLatestVersion) == 0)
		{
			AfxMessageBox("You are currently using the latest version of Angry IP Scanner.", MB_OK | MB_ICONINFORMATION, 0);
		}
		else
		{
			CString szUserMsg;
			szUserMsg.Format(
				"You are currently using version %s,\n"
				"but the latest version is %s\n\n"
				"Do you want to go to the website and download the latest version?", 
				szCurrentVersion, (char*)szLatestVersion);
		
			if (AfxMessageBox(szUserMsg, MB_YESNO | MB_ICONQUESTION, 0) == IDYES)
			{
				CLink::goToScannerHomepage();
			}
		}

	}

	dlg->status(NULL);
	closesocket(hSocket);	

}
