// InstallDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "InstallDlg.h"
#include <shlobj.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CInstallDlg dialog


CInstallDlg::CInstallDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CInstallDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CInstallDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CInstallDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CInstallDlg)
	DDX_Control(pDX, IDC_INSTALL_PATH, m_ctlInstallPath);
	DDX_Control(pDX, IDC_CREATE_GROUP, m_ctlCreateGroup);
	DDX_Control(pDX, IDC_CREATE_DESKTOP_SHORTCUT, m_ctlCreateDesktopShortcut);
	DDX_Control(pDX, IDC_COPY_PROGRAM, m_ctlCopyProgram);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CInstallDlg, CDialog)
	//{{AFX_MSG_MAP(CInstallDlg)
	ON_BN_CLICKED(IDOK, OnInstall)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CInstallDlg message handlers

BOOL CInstallDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	m_ctlInstallPath.SetWindowText("C:\\Program Files\\Angry IP Scanner");	
	m_ctlCopyProgram.SetCheck(TRUE);
	m_ctlCreateDesktopShortcut.SetCheck(TRUE);
	m_ctlCreateGroup.SetCheck(TRUE);
	
	return TRUE;  // return TRUE unless you set the focus to a control
	              
}

bool CreateShortCut(LPCSTR lpszSourceFile, LPCSTR lpszDestination, LPCSTR lpszDesc) 
{ 
	HRESULT     hResult    = NULL; 
	IShellLink* pShellLink = NULL; 
	bool	      rc	 = false;

	hResult = CoInitialize(NULL);
	// Initialize the IShellLink interface. 
    
	hResult = CoCreateInstance(CLSID_ShellLink, NULL, 
			     CLSCTX_INPROC_SERVER, IID_IShellLink, 
			     (void**) &pShellLink); 
	// Get a pointer to the IShellLink Interface

	if (SUCCEEDED(hResult)) 
	{ 
		IPersistFile* ppf = NULL; 
		pShellLink->SetPath(lpszSourceFile); 
		// Set the path to the shortcut target
		
		hResult = pShellLink->SetDescription(lpszDesc); 
		// Add Description

		hResult = pShellLink->QueryInterface(IID_IPersistFile, (void**) &ppf); 
		// Query IShellLink for the IPersistFile interface for saving the
		// shortcut in persistent storage. 
 
		if (SUCCEEDED(hResult)) 
		{ 
	WORD wszWideString[MAX_PATH]; 
	
	// Ensure that the string is ANSI. 
				MultiByteToWideChar(CP_ACP, 0, lpszDestination, -1,
						wszWideString, MAX_PATH); 

				hResult = ppf->Save(wszWideString, TRUE); 
				// Save the link by calling IPersistFile::Save. 
						
	ppf->Release(); 
	rc = true;
		} 
		pShellLink->Release(); 
		CoUninitialize();
		// Clean up
	} 
	return rc; 
}

void CInstallDlg::OnInstall() 
{
	CString szPath;	
	if (m_ctlCopyProgram.GetCheck())
	{
		m_ctlInstallPath.GetWindowText(szPath);
		CreateDirectory(szPath, NULL);
		szPath += "\\ipscan.exe";
		CopyFile(__targv[0], szPath, FALSE);
	}
	else
	{
		szPath = __targv[0];
	}
	// szPath now has a full path to the executable
	
	char szFolderPath[MAX_PATH];
	
	if (m_ctlCreateDesktopShortcut.GetCheck())
	{
		SHGetSpecialFolderPath(this->m_hWnd, (char*) &szFolderPath, CSIDL_DESKTOPDIRECTORY, FALSE);				
		strcat((char*) &szFolderPath, "\\Angry IP Scanner.lnk");
		CreateShortCut(szPath, szFolderPath, "");
	}

	if (m_ctlCreateGroup.GetCheck())
	{
		SHGetSpecialFolderPath(this->m_hWnd, (char*) &szFolderPath, CSIDL_PROGRAMS, FALSE);		
		strcat((char*) &szFolderPath, "\\Angryziber");
		CreateDirectory((char*) &szFolderPath, NULL);
		strcat((char*) &szFolderPath, "\\Angry IP Scanner.lnk");
		CreateShortCut(szPath, szFolderPath, "");
	}

	if (m_ctlCopyProgram.GetCheck() && MessageBox("Do you want to run program from the new location? (Recommended)", NULL, MB_ICONQUESTION | MB_YESNO) == IDYES)
	{
		// Run program from new location
		WinExec(szPath, 0);
		
		// Terminate this instance
		exit(0);
	}

	CDialog::OnOK();
}
