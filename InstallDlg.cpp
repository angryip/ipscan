// InstallDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "InstallDlg.h"
#include <direct.h>

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

void CInstallDlg::OnInstall() 
{
	CString szPath;	
	if (m_ctlCopyProgram.GetCheck())
	{
		m_ctlInstallPath.GetWindowText(szPath);
		_mkdir(szPath);
		szPath += "\\ipscan.exe";
		CopyFile(__targv[0], szPath, TRUE);
	}
	else
	{
		szPath = __targv[0];
	}
	// szPath now has a full path to the executable
	
	if (m_ctlCreateDesktopShortcut.GetCheck())
	{
	}

	if (m_ctlCreateGroup.GetCheck())
	{
	}
}
