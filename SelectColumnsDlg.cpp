// SelectColumnsDlg.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "SelectColumnsDlg.h"
#include "Scanner.h"
#include "IpscanDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSelectColumnsDlg dialog


CSelectColumnsDlg::CSelectColumnsDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSelectColumnsDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSelectColumnsDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CSelectColumnsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSelectColumnsDlg)
	DDX_Control(pDX, IDC_SELECTED_COLUMNS, m_ctSelectedColumns);
	DDX_Control(pDX, IDC_ALL_COLUMNS, m_ctAllColumns);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSelectColumnsDlg, CDialog)
	//{{AFX_MSG_MAP(CSelectColumnsDlg)
	ON_BN_CLICKED(IDC_SELECT_ALL, OnSelectAll)
	ON_BN_CLICKED(IDC_DESELECT_ALL, OnDeselectAll)
	ON_BN_CLICKED(IDC_MOVE_UP, OnMoveUp)
	ON_BN_CLICKED(IDC_MOVE_DOWN, OnMoveDown)
	ON_BN_CLICKED(IDC_DESELECT, OnDeselect)
	ON_BN_CLICKED(IDC_SELECT, OnSelect)
	ON_BN_CLICKED(IDC_SELECT_APPEND, OnSelectAppend)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSelectColumnsDlg message handlers

BOOL CSelectColumnsDlg::OnInitDialog() 
{
	CDialog::OnInitDialog();
	
	memset(&m_naSelColumns, 0, sizeof(m_naSelColumns));

	CString szTmp;
	int i;
	int nAllColumnCount = g_scanner->getAllColumnsCount();
	m_nSelectedColumns = g_scanner->getColumnCount();

	// Init list boxes	
	for (i=CL_STATIC_COUNT; i < nAllColumnCount; i++)
	{
		g_scanner->getAllColumnName(i, szTmp);
		m_ctAllColumns.AddString(szTmp);
	}

	for (i=CL_STATIC_COUNT; i < m_nSelectedColumns; i++)
	{
		m_naSelColumns[i] = g_scanner->getColumnReference(i);
	}

	RepopulateSelectedColumns();
	
	return TRUE;  
}

void CSelectColumnsDlg::RepopulateSelectedColumns()
{
	m_ctSelectedColumns.ResetContent();
	CString szTmp;
	
	for (int i = CL_STATIC_COUNT; i < m_nSelectedColumns; i++)
	{
		g_scanner->getAllColumnName(m_naSelColumns[i], szTmp);
		m_ctSelectedColumns.AddString(szTmp);
	}
}

void CSelectColumnsDlg::OnSelectAll() 
{
	m_nSelectedColumns = g_scanner->getAllColumnsCount();
	
	for (int i=0; i < m_nSelectedColumns; i++)
	{
		m_naSelColumns[i] = i;
	}

	RepopulateSelectedColumns();
}

void CSelectColumnsDlg::OnDeselectAll() 
{
	m_nSelectedColumns = CL_STATIC_COUNT;
	
	RepopulateSelectedColumns();
}

void CSelectColumnsDlg::OnMoveUp() 
{
	BOOL baSelection[128];
	memset(&baSelection, FALSE, sizeof(baSelection));

	for (int nItem = CL_STATIC_COUNT; nItem < m_nSelectedColumns; nItem++)
	{
		// If selected, then process it
		if (m_ctSelectedColumns.GetSel(nItem - CL_STATIC_COUNT))
		{
			if (nItem <= CL_STATIC_COUNT)
				return;	// Don't move items, if one of them is first item

			int nTmp = m_naSelColumns[nItem - 1];
			m_naSelColumns[nItem - 1] = m_naSelColumns[nItem];
			m_naSelColumns[nItem] = nTmp;

			baSelection[nItem-1] = TRUE;
		}		
	}

	RepopulateSelectedColumns();

	for (nItem = CL_STATIC_COUNT; nItem < m_nSelectedColumns; nItem++)
	{
		if (baSelection[nItem])
		{
			m_ctSelectedColumns.SetSel(nItem - CL_STATIC_COUNT);
		}
	}
}

void CSelectColumnsDlg::OnMoveDown() 
{
	BOOL baSelection[128];
	memset(&baSelection, FALSE, sizeof(baSelection));

	for (int nItem = m_nSelectedColumns - 1; nItem >= CL_STATIC_COUNT; nItem--)
	{
		// If selected, then process it
		if (m_ctSelectedColumns.GetSel(nItem - CL_STATIC_COUNT))
		{
			if (nItem >= m_nSelectedColumns - 1)
				return;	// Don't move items, if one of them is first item

			int nTmp = m_naSelColumns[nItem + 1];
			m_naSelColumns[nItem + 1] = m_naSelColumns[nItem];
			m_naSelColumns[nItem] = nTmp;

			baSelection[nItem+1] = TRUE;
		}		
	}

	RepopulateSelectedColumns();

	for (nItem = CL_STATIC_COUNT; nItem < m_nSelectedColumns; nItem++)
	{
		if (baSelection[nItem])
		{
			m_ctSelectedColumns.SetSel(nItem - CL_STATIC_COUNT);
		}
	}
}

void CSelectColumnsDlg::OnSelect() 
{
	for (int i = CL_STATIC_COUNT; i < g_scanner->getAllColumnsCount(); i++)
	{
		if (m_ctAllColumns.GetSel(i - CL_STATIC_COUNT))
		{
			// check if it is already selected
			BOOL bFound = FALSE;
			for (int j = CL_STATIC_COUNT; j < m_nSelectedColumns; j++)
			{
				if (m_naSelColumns[j] == i)
				{
					bFound = TRUE;
					break;
				}
			}

			if (!bFound)
			{
				m_naSelColumns[m_nSelectedColumns] = i;
				m_nSelectedColumns++;
			}
		}
	}

	RepopulateSelectedColumns();
}

void CSelectColumnsDlg::OnSelectAppend() 
{
	for (int i = CL_STATIC_COUNT; i < g_scanner->getAllColumnsCount(); i++)
	{
		if (m_ctAllColumns.GetSel(i - CL_STATIC_COUNT))
		{
			m_naSelColumns[m_nSelectedColumns] = i;
			m_nSelectedColumns++;
		}
	}

	RepopulateSelectedColumns();	
}

void CSelectColumnsDlg::OnDeselect() 
{
	int naSelected[128];	

	for (int i = CL_STATIC_COUNT; i < m_nSelectedColumns; i++)
	{
		if (m_ctSelectedColumns.GetSel(i - CL_STATIC_COUNT))
			naSelected[i] = TRUE;
		else
			naSelected[i] = FALSE;
	}

	for (int nItem = CL_STATIC_COUNT; nItem < m_nSelectedColumns; nItem++)
	{
		// If selected, then process it
		if (naSelected[nItem])
		{			
			for (i = nItem; i < m_nSelectedColumns; i++)
			{
				m_naSelColumns[i] = m_naSelColumns[i+1];
				naSelected[i] = naSelected[i+1];
			}
			m_nSelectedColumns--;
			nItem--;
		}		
	}

	RepopulateSelectedColumns();
}



void CSelectColumnsDlg::OnOK() 
{
	// Save all the stuff to global scanner
	// Not a pretty nor object-oriented function :-(
	
	for (int i=CL_STATIC_COUNT; i < m_nSelectedColumns; i++)
	{
		g_scanner->m_Columns[i] = m_naSelColumns[i];
	}

	g_scanner->m_nColumns = m_nSelectedColumns;

	g_scanner->initListColumns(&((CIpscanDlg*) AfxGetApp()->GetMainWnd())->m_list);
	
	CDialog::OnOK();
}
