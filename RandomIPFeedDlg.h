#if !defined(AFX_RANDOMIPFEEDDLG_H__10129F19_F2D4_4C19_BF1A_DDD938A9BFDD__INCLUDED_)
#define AFX_RANDOMIPFEEDDLG_H__10129F19_F2D4_4C19_BF1A_DDD938A9BFDD__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// RandomIPFeedDlg.h : header file
//

#include "AbstractIPFeedDlg.h"

/////////////////////////////////////////////////////////////////////////////
// CRandomIPFeedDlg dialog

class CRandomIPFeedDlg : public CAbstractIPFeedDlg
{
// Construction
public:
	CRandomIPFeedDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CRandomIPFeedDlg)
	enum { IDD = IDD_IP_FEED_RANDOM };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRandomIPFeedDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CRandomIPFeedDlg)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

public:

	virtual CString serialize();
	
	virtual BOOL unserialize(const CString& szSettings);

	CAbstractIPFeed * createIPFeed();

};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_RANDOMIPFEEDDLG_H__10129F19_F2D4_4C19_BF1A_DDD938A9BFDD__INCLUDED_)
