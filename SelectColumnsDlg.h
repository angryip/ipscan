#if !defined(AFX_SELECTCOLUMNSDLG_H__45FC584F_C97D_42C7_9346_2F20302F5973__INCLUDED_)
#define AFX_SELECTCOLUMNSDLG_H__45FC584F_C97D_42C7_9346_2F20302F5973__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// SelectColumnsDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CSelectColumnsDlg dialog

class CSelectColumnsDlg : public CDialog
{
// Construction
public:
	CSelectColumnsDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CSelectColumnsDlg)
	enum { IDD = IDD_SELECT_COLUMNS_DLG };
	CListBox	m_ctSelectedColumns;
	CListBox	m_ctAllColumns;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSelectColumnsDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CSelectColumnsDlg)
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SELECTCOLUMNSDLG_H__45FC584F_C97D_42C7_9346_2F20302F5973__INCLUDED_)
