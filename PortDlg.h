#if !defined(AFX_PORTDLG_H__66BE02AD_DE28_4C7F_ACBB_485499EE8DED__INCLUDED_)
#define AFX_PORTDLG_H__66BE02AD_DE28_4C7F_ACBB_485499EE8DED__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// PortDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CPortDlg dialog

class CPortDlg : public CDialog
{
// Construction
public:
	CPortDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CPortDlg)
	enum { IDD = IDD_PORT_DLG };
	CComboBox	m_ctPortListBox;
	CEdit	m_ctPortString;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPortDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CPortDlg)
	afx_msg void OnChangePortString();
	afx_msg void OnButtonAddSinglePort();
	afx_msg void OnButtonAddPortRange();
	afx_msg void OnButtonTip();
	virtual BOOL OnInitDialog();
	afx_msg void OnSelchangePortListbox();
	virtual void OnOK();
	virtual void OnCancel();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PORTDLG_H__66BE02AD_DE28_4C7F_ACBB_485499EE8DED__INCLUDED_)
