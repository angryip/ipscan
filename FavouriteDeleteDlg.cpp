// FavouriteDeleteDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "FavouriteDeleteDlg.h"
#include "Options.h"
#include "IpscanDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CFavouriteDeleteDlg dialog


CFavouriteDeleteDlg::CFavouriteDeleteDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CFavouriteDeleteDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CFavouriteDeleteDlg)
	m_nFavouriteIndex = -1;
	//}}AFX_DATA_INIT
}


void CFavouriteDeleteDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CFavouriteDeleteDlg)
	DDX_Control(pDX, IDC_FAV_LIST, m_ctrlList);
	DDX_CBIndex(pDX, IDC_FAV_LIST, m_nFavouriteIndex);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CFavouriteDeleteDlg, CDialog)
	//{{AFX_MSG_MAP(CFavouriteDeleteDlg)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CFavouriteDeleteDlg message handlers

BOOL CFavouriteDeleteDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	for (int i=0; i < 99; i++)
	{
		if (g_options->m_aFavourites[i].szName.GetLength() == 0)
			break;

		m_ctrlList.AddString(g_options->m_aFavourites[i].szName);
	}

	m_ctrlList.SetCurSel(0);
	
	return TRUE;  
}
