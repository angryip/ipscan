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

#if !defined(AFX_IPRANGE_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_)
#define AFX_IPRANGE_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// IPRangeDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CIPRangeDlg dialog

class CIPRangeDlg : public CDialog
{
// Construction
public:
	CIPRangeDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CIPRangeDlg)
	enum { IDD = IDD_IP_FEED_IP_RANGE };
	CIPAddressCtrl	m_ctIPEnd;
	CIPAddressCtrl	m_ctIPStart;
	CButton	m_btnIPUp;
	CString	m_szHostname;
	//}}AFX_DATA

	BOOL m_bIp2Virgin;


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIPRangeDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CIPRangeDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnButtonipup();
	afx_msg void OnFieldchangedIpaddress1(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnFieldchangedIpaddress2(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnClassC();
	afx_msg void OnClassD();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPRANGE_H__4E9AAB13_1D19_41DA_9A00_971A87F8F6A5__INCLUDED_)
