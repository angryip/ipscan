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

#if !defined(AFX_EDITOPENERSDLG_H__0F185E75_CC4A_4A84_8877_BA985B26CC3A__INCLUDED_)
#define AFX_EDITOPENERSDLG_H__0F185E75_CC4A_4A84_8877_BA985B26CC3A__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// EditOpenersDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CEditOpenersDlg dialog

class CEditOpenersDlg : public CDialog
{
// Construction
public:
	CEditOpenersDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CEditOpenersDlg)
	enum { IDD = IDD_EDIT_OPENERS_DLG };
	CButton	m_ctrlCommandLine;
	CEdit	m_ctrlWorkingDirectory;
	CEdit	m_ctrlExecutionString;
	CEdit	m_ctrlTitle;
	CListBox	m_ctrlList;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CEditOpenersDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	BOOL m_bEdited;
	void RefreshList();

	// Generated message map functions
	//{{AFX_MSG(CEditOpenersDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnBtnEdit();
	afx_msg void OnBtnChange();
	afx_msg void OnBtnInsert();
	virtual void OnOK();
	afx_msg void OnBtnUp();
	afx_msg void OnBtnDown();
	afx_msg BOOL OnClose();
	afx_msg void OnBtnDelete();
	afx_msg void OnEditBoxChange();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_EDITOPENERSDLG_H__0F185E75_CC4A_4A84_8877_BA985B26CC3A__INCLUDED_)
