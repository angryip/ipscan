#if !defined(AFX_FAVOURITEDELETEDLG_H__C7EDFE45_7082_4B0A_833A_F9EDA2293388__INCLUDED_)
#define AFX_FAVOURITEDELETEDLG_H__C7EDFE45_7082_4B0A_833A_F9EDA2293388__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// FavouriteDeleteDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CFavouriteDeleteDlg dialog

class CFavouriteDeleteDlg : public CDialog
{
// Construction
public:
	CFavouriteDeleteDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CFavouriteDeleteDlg)
	enum { IDD = IDD_FAVOURITE_DELETE };
	CComboBox	m_ctrlList;
	int		m_nFavouriteIndex;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CFavouriteDeleteDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CFavouriteDeleteDlg)
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_FAVOURITEDELETEDLG_H__C7EDFE45_7082_4B0A_833A_F9EDA2293388__INCLUDED_)
