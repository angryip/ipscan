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

#if !defined(AFX_INSTALLDLG_H__55F09AE3_ACBB_47B1_87CC_8A258E663119__INCLUDED_)
#define AFX_INSTALLDLG_H__55F09AE3_ACBB_47B1_87CC_8A258E663119__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// InstallDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CInstallDlg dialog

class CInstallDlg : public CDialog
{
// Construction
public:
	CInstallDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CInstallDlg)
	enum { IDD = IDD_INSTALL_DLG };
	CEdit	m_ctlInstallPath;
	CButton	m_ctlCreateGroup;
	CButton	m_ctlCreateDesktopShortcut;
	CButton	m_ctlCopyProgram;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CInstallDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CInstallDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnInstall();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_INSTALLDLG_H__55F09AE3_ACBB_47B1_87CC_8A258E663119__INCLUDED_)
