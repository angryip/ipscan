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

#if !defined(AFX_DETAILSDLG_H__CDEBF581_DD65_4485_8F97_0AB74CB13495__INCLUDED_)
#define AFX_DETAILSDLG_H__CDEBF581_DD65_4485_8F97_0AB74CB13495__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// DetailsDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CDetailsDlg dialog

class CDetailsDlg : public CDialog
{
// Construction
public:
	void setPorts(LPCSTR szPort);
	void addScannedInfo(LPCSTR szInfo);
	CDetailsDlg(CWnd* pParent = NULL);   // standard constructor	

// Dialog Data
	//{{AFX_DATA(CDetailsDlg)
	enum { IDD = IDD_DETAILS_DLG };
	CString	m_szScannedInfo;
	CString	m_szPortList;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDetailsDlg)
	public:
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:	

	// Generated message map functions
	//{{AFX_MSG(CDetailsDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_DETAILSDLG_H__CDEBF581_DD65_4485_8F97_0AB74CB13495__INCLUDED_)
