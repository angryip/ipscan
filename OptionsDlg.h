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
	COptionsDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(COptionsDlg)
	enum { IDD = IDD_OPTIONS };
	CStatic	m_statColumnSelected;
	CStatic	m_statColumnType;
	CButton	m_btnOptionsColumn;
	CButton	m_btnAboutColumn;
	CButton	m_ctPluginOptionsGroup;
	CListBox	m_ctPluginList;
	int		m_nTimerDelay;
	UINT	m_nMaxThreads;
	UINT	m_nPingTimeout;
	int		m_nDisplayOptions;
	BOOL	m_bScanHostIfDead;
	int		m_nPortTimeout;
	BOOL	m_bShowPortsBelow;
	BOOL	m_bScanPorts;
	int		m_nPingCount;
	BOOL	m_bOptimizePorts;
	BOOL	m_bAutoSave;
	BOOL	m_bSkipBroadcast;
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
	int m_nCurrentlySelectedColumn;

	// Generated message map functions
	//{{AFX_MSG(COptionsDlg)
	virtual void OnOK();			
	afx_msg void OnHelpbtn();	
	virtual BOOL OnInitDialog();
	afx_msg void OnSelchangePluginList();
	afx_msg void OnSelectColumnsBtn();
	afx_msg void OnColumnAboutButton();
	afx_msg void OnColumnOptionsButton();
	afx_msg void OnSave();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_OPTIONSDLG_H__DD7FF820_D58E_11D3_83C7_A196C701772A__INCLUDED_)
