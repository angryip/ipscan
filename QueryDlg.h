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

#if !defined(AFX_QUERYDLG_H__4E93C857_EA22_4073_9FE5_5590241C4317__INCLUDED_)
#define AFX_QUERYDLG_H__4E93C857_EA22_4073_9FE5_5590241C4317__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// QueryDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CQueryDlg dialog

class CQueryDlg : public CDialog
{
// Construction
public:	
	CQueryDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CQueryDlg)
	enum { IDD = IDD_QUERY_DLG };
	CEdit	m_ctrlUserText;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CQueryDlg)
	public:
	virtual int DoModal();
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:	
	CString m_szMessage;

	// Generated message map functions
	//{{AFX_MSG(CQueryDlg)
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

public:
	CString m_szCaption;
	CString m_szQueryText;
	CString	m_szDefaultUserText;

	CString doQuery();

};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_QUERYDLG_H__4E93C857_EA22_4073_9FE5_5590241C4317__INCLUDED_)
