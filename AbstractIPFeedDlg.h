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

#if !defined(AFX_ABSTRACTIPRANGEDLG_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_)
#define AFX_ABSTRACTIPRANGEDLG_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// IPRangeDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CIPRangeDlg dialog

class CAbstractIPFeedDlg : public CDialog
{
// Construction
public:
	virtual CAbstractIPFeed * createIPFeed() = 0;
	
	CAbstractIPFeedDlg(int nIDD, CWnd* pParent = NULL);   // standard constructor

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIPRangeDlg)
	protected:
	public:
	virtual CWnd * SetFocus();
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	//}}AFX_VIRTUAL

// Implementation
protected:

	CToolTipCtrl * m_pToolTips;

	// Generated message map functions
	//{{AFX_MSG(CIPRangeDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnDestroy();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

public:
	// Returns serialized settings as a string (for implementing Favorites)
	virtual CString serialize() = 0;

	// Imports previously serialized data and restores it's state
	virtual BOOL unserialize(const CString& szSettings) = 0;

	// Imports command-line options, to initialize options from command-line
	virtual BOOL processCommandLine(CString& szCommandLine) = 0;

};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_ABSTRACTIPRANGEDLG_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_)
