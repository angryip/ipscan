#if !defined(AFX_OPTIONSDLG_H__DD7FF820_D58E_11D3_83C7_A196C701772A__INCLUDED_)
#define AFX_OPTIONSDLG_H__DD7FF820_D58E_11D3_83C7_A196C701772A__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "IpscanDlg.h"

// OptionsDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// COptionsDlg dialog

class COptionsDlg : public CDialog
{
// Construction
public:
	static void loadOptions(CIpscanDlg *d);
	static void saveOptions(CIpscanDlg *d);
	COptionsDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(COptionsDlg)
	enum { IDD = IDD_OPTIONS };
	CButton	m_ondeadctl;
	CButton	m_resolvectl;
	CButton	m_scanportctl;
	CEdit	m_portctl;
	UINT	m_port;
	int		m_delay;
	BOOL	m_resolve;
	BOOL	m_scanport;
	BOOL	m_retrifdead;
	UINT	m_maxthreads;
	UINT	m_timeout;
	BOOL	m_portondead;
	int		m_display;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(COptionsDlg)
	public:
	virtual int DoModal();
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(COptionsDlg)
	virtual void OnOK();
	afx_msg void OnCheck2();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg void OnCheck1();
	afx_msg void OnHelpbtn();
	afx_msg void OnChangeEdit1();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_OPTIONSDLG_H__DD7FF820_D58E_11D3_83C7_A196C701772A__INCLUDED_)
