// ipscan.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "ipscan.h"
#include "ipscanDlg.h"

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
	
	return CWinApp::Run();
}
