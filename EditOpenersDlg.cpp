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

// EditOpenersDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "EditOpenersDlg.h"
#include "Options.h"
#include "Scanner.h"
#include "IpscanDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CEditOpenersDlg dialog


CEditOpenersDlg::CEditOpenersDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CEditOpenersDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CEditOpenersDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT

	m_bEdited = FALSE;
}


void CEditOpenersDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CEditOpenersDlg)
	DDX_Control(pDX, IDC_WORKING_DIRECTORY, m_ctrlWorkingDirectory);
	DDX_Control(pDX, IDC_EXECUTION_STRING, m_ctrlExecutionString);
	DDX_Control(pDX, IDC_OPENER_TITLE, m_ctrlTitle);
	DDX_Control(pDX, IDC_OPENER_LIST, m_ctrlList);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CEditOpenersDlg, CDialog)
	//{{AFX_MSG_MAP(CEditOpenersDlg)
	ON_BN_CLICKED(IDC_BTN_EDIT, OnBtnEdit)
	ON_BN_CLICKED(IDC_BTN_CHANGE, OnBtnChange)
	ON_BN_CLICKED(IDC_BTN_INSERT, OnBtnInsert)
	ON_BN_CLICKED(IDC_BTN_UP, OnBtnUp)
	ON_BN_CLICKED(IDC_BTN_DOWN, OnBtnDown)
	ON_WM_CLOSE()
	ON_BN_CLICKED(IDC_BTN_DELETE, OnBtnDelete)
	ON_LBN_DBLCLK(IDC_OPENER_LIST, OnBtnEdit)
	ON_EN_CHANGE(IDC_WORKING_DIRECTORY, OnEditBoxChange)
	ON_EN_CHANGE(IDC_OPENER_TITLE, OnEditBoxChange)
	ON_EN_CHANGE(IDC_EXECUTION_STRING, OnEditBoxChange)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CEditOpenersDlg message handlers

BOOL CEditOpenersDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();

	RefreshList();
	
	return TRUE;  
}

void CEditOpenersDlg::OnBtnEdit() 
{
	if (m_ctrlList.GetCount() == 0)
		return; 

	int nSelectedOpener = m_ctrlList.GetCurSel();
	
	m_ctrlTitle.SetWindowText(g_options->m_aOpeners[nSelectedOpener].szName);
	m_ctrlExecutionString.SetWindowText(g_options->m_aOpeners[nSelectedOpener].szExecute);
	m_ctrlWorkingDirectory.SetWindowText(g_options->m_aOpeners[nSelectedOpener].szWorkDir);

	m_bEdited = FALSE;
}

void CEditOpenersDlg::OnBtnChange() 
{
	int nSelectedOpener = m_ctrlList.GetCurSel();

	CString szTmp;
	m_ctrlTitle.GetWindowText(szTmp); g_options->m_aOpeners[nSelectedOpener].szName = szTmp;
	m_ctrlExecutionString.GetWindowText(szTmp); g_options->m_aOpeners[nSelectedOpener].szExecute = szTmp;
	m_ctrlWorkingDirectory.GetWindowText(szTmp); g_options->m_aOpeners[nSelectedOpener].szWorkDir = szTmp;

	RefreshList();

	m_ctrlList.SetCurSel(nSelectedOpener);

	m_bEdited = FALSE;
}

void CEditOpenersDlg::OnBtnInsert() 
{
	int nNewOpener = m_ctrlList.GetCount(); // This is the index of the last item + 1

	CString szTmp;
	m_ctrlTitle.GetWindowText(szTmp); g_options->m_aOpeners[nNewOpener].szName = szTmp;
	m_ctrlExecutionString.GetWindowText(szTmp); g_options->m_aOpeners[nNewOpener].szExecute = szTmp;
	m_ctrlWorkingDirectory.GetWindowText(szTmp); g_options->m_aOpeners[nNewOpener].szWorkDir = szTmp;

	RefreshList();

	m_ctrlList.SetCurSel(nNewOpener);

	m_bEdited = FALSE;
}

void CEditOpenersDlg::RefreshList()
{
	m_ctrlList.ResetContent();
	
	for (int i=0; i < 99; i++)
	{
		if (g_options->m_aOpeners[i].szName.GetLength() == 0)
			break;

		m_ctrlList.AddString(g_options->m_aOpeners[i].szName);
	}

	m_ctrlList.SetCurSel(0);
}

void CEditOpenersDlg::OnOK() 
{
	if (OnClose())
		CDialog::OnOK();
}


void CEditOpenersDlg::OnBtnUp() 
{
	if (m_ctrlList.GetCount() == 0)
		return; 

	int nSelectedOpener = m_ctrlList.GetCurSel();
	
	if (nSelectedOpener == 0)
		return;

	tOpener opener;

	opener = g_options->m_aOpeners[nSelectedOpener - 1];
	g_options->m_aOpeners[nSelectedOpener - 1] = g_options->m_aOpeners[nSelectedOpener];
	g_options->m_aOpeners[nSelectedOpener] = opener;

	RefreshList();

	m_ctrlList.SetCurSel(nSelectedOpener - 1);
}

void CEditOpenersDlg::OnBtnDown() 
{
	if (m_ctrlList.GetCount() == 0)
		return; 

	int nSelectedOpener = m_ctrlList.GetCurSel();
	
	if (nSelectedOpener == m_ctrlList.GetCount() - 1)
		return;

	tOpener opener;

	opener = g_options->m_aOpeners[nSelectedOpener + 1];
	g_options->m_aOpeners[nSelectedOpener + 1] = g_options->m_aOpeners[nSelectedOpener];
	g_options->m_aOpeners[nSelectedOpener] = opener;

	RefreshList();

	m_ctrlList.SetCurSel(nSelectedOpener + 1);
}

BOOL CEditOpenersDlg::OnClose() 
{
	if (m_bEdited)
	{
		if (AfxMessageBox("Your changes were not saved.\nBe sure to use \"Change current\" or \"Insert new\" butons\nDiscard current changes?", MB_YESNO | MB_ICONWARNING, 0) == IDNO)
			return FALSE;
	}

	g_options->saveOpeners();

	((CIpscanDlg *)AfxGetApp()->GetMainWnd())->RefreshOpenersMenu();
	
	CDialog::OnClose();

	return TRUE;
}

void CEditOpenersDlg::OnBtnDelete() 
{
	if (m_ctrlList.GetCount() == 0)
		return; 

	int nSelectedOpener = m_ctrlList.GetCurSel();

	for (int i=nSelectedOpener+1; i < m_ctrlList.GetCount(); i++)
	{		
		g_options->m_aOpeners[i - 1] = g_options->m_aOpeners[i];		
	}

	int nLastOpener = m_ctrlList.GetCount() - 1;

	g_options->m_aOpeners[nLastOpener].szName = "";

	RefreshList();

	m_ctrlList.SetCurSel(nSelectedOpener == nLastOpener ? nLastOpener - 1 : nSelectedOpener);	
}

void CEditOpenersDlg::OnEditBoxChange() 
{
	// Set the edited flag
	m_bEdited = TRUE;
}
