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

#if !defined(AFX_NETBIOSOPTIONS_H__E409BC5B_1BF7_4F90_909B_38BB3539EC72__INCLUDED_)
#define AFX_NETBIOSOPTIONS_H__E409BC5B_1BF7_4F90_909B_38BB3539EC72__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// NetBIOSOptions.h : header file
//

#include "resource.h"

/////////////////////////////////////////////////////////////////////////////
// CNetBIOSOptions dialog

class CNetBIOSOptions : public CDialog
{
// Construction
public:
	BOOL m_bCalledOnInit;
	void CalledOnInit();
	CNetBIOSOptions(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CNetBIOSOptions)
	enum { IDD = IDD_NETBIOS_OPTIONS };
	CComboBox	m_ctlLanaList;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CNetBIOSOptions)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CNetBIOSOptions)
	virtual BOOL OnInitDialog();
	virtual void OnOK();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_NETBIOSOPTIONS_H__E409BC5B_1BF7_4F90_909B_38BB3539EC72__INCLUDED_)
