#if !defined(AFX_SEARCHDLG_H__C433B5E2_5FF0_11D4_9CD8_00C0DFF58A53__INCLUDED_)
#define AFX_SEARCHDLG_H__C433B5E2_5FF0_11D4_9CD8_00C0DFF58A53__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// SearchDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CSearchDlg dialog

class CSearchDlg : public CDialog
{
// Construction
public:
	CSearchDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CSearchDlg)
	enum { IDD = IDD_SEARCH };
	BOOL	m_case;
	CString	m_search;
	BOOL	m_beginning;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSearchDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CSearchDlg)
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SEARCHDLG_H__C433B5E2_5FF0_11D4_9CD8_00C0DFF58A53__INCLUDED_)
