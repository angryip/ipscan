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

// ipscanDlg.h : header file
//

#if !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
#define AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "resource.h"
#include "Scanner.h"
#include "ScanListCtrl.h"
#include "AbstractIPFeedDlg.h"
#include "AbstractIPFeed.h"

#define SCAN_MODE_NOT_SCANNING	0
#define SCAN_MODE_SCANNING		1
#define SCAN_MODE_FINISHING		2
#define SCAN_MODE_KILLING		3


/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg dialog

class CIpscanDlg : public CDialog
{
// Construction
public:	
	void RefreshFavouritesMenu();
	void RefreshOpenersMenu();
	void KillAllRunningThreads();
	CString m_szCompleteInformation;
	int m_nCmdLineFileFormat;
	void EnableMenuItems(BOOL bEnable);
	void RecreateIPFeed();
	CBitmap m_bmpShowAdvanced;
	int m_nCmdLineOptions;
	CString *m_szDefaultFileName;			
	BOOL m_portondead;	
	UINT m_timeout;
	CBitmap m_bmpStop;
	CBitmap m_bmpStart;
	CBitmap m_bmpKill;
	int m_menucuritem;
	BOOL m_bScanningAborted;
	CMenu * m_menuContext;
	unsigned long m_tickcount;	
	int m_nScanMode;
	CAbstractIPFeedDlg *m_dlgIPFeed;
	
	CIpscanDlg(CWnd* pParent = NULL);	// standard constructor

	void status(LPCSTR str);

// Dialog Data
	//{{AFX_DATA(CIpscanDlg)
	enum { IDD = IDD_IPSCAN_DIALOG };
	CComboBox	m_ctIPFeed;
	CButton	m_btnAdvancedMode;
	CButton	m_ctScanPorts;
	CStatic	m_ctWhatPorts;
	CScanListCtrl	m_list;
	CStatic m_numthreads;
	CProgressCtrl	m_progress;
	CStatic	m_statusctl;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIpscanDlg)
	public:
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	virtual BOOL OnCommand(WPARAM wParam, LPARAM lParam);
	//}}AFX_VIRTUAL

// Implementation
protected:		
	CToolTipCtrl * m_pToolTips;
	CBitmap m_bmpSelectColumns;
	BOOL m_bSysCommand;
	CBitmap m_bmpHideAdvanced;
	void HandleResizing(int cx, int cy);	
	bool m_bAdvancedMode;
	HACCEL hAccel;
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CIpscanDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnIpExit();
	afx_msg void OnButtonScan();
	afx_msg void OnHelpAbout();
	afx_msg void OnOptionsOptions();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnScanSavetotxt();
	afx_msg void OnRclickList(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnIPToClipboard();	
	afx_msg void OnScanSaveselection();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg void OnOptionsSaveoptions();
	afx_msg void OnShowNetBIOSInfo();
	afx_msg void OnHelpAngryipscannerwebpage();
	afx_msg void OnHelpAngryzibersoftware();
	afx_msg void OnRescanIP();
	afx_msg void OnGotoNextalive();
	afx_msg void OnGotoNextdead();
	afx_msg void OnGotoNextopenport();
	afx_msg void OnGotoNextclosedport();
	afx_msg void OnGotoHostname();	
	afx_msg void OnHelpCommandline();
	afx_msg void OnHelpForum();
	afx_msg void OnOptionsInstallProgram();
	afx_msg void OnDestroy();
	afx_msg void OnDrawItem(int nIDCtl, LPDRAWITEMSTRUCT lpDrawItemStruct);
	afx_msg void OnButtonToAdvanced();
	afx_msg void OnScanPortsClicked();
	afx_msg void OnSelectPortsClicked();
	afx_msg void OnCommandsShowIPdetails();
	afx_msg void OnSelectColumns();
	afx_msg void OnOptionsSavedimensions();
	afx_msg void OnUtilsDeletefromlistDeadhosts();
	afx_msg void OnUtilsDeletefromlistAlivehosts();
	afx_msg void OnUtilsDeletefromlistClosedports();
	afx_msg void OnUtilsDeletefromlistOpenports();
	afx_msg void OnCommandsDeleteIP();
	afx_msg void ShowCompleteInformation();
	afx_msg void OnClose();
	afx_msg void OnFavouritesAddcurrentrange();
	afx_msg void OnFavouritesDeleteFavourite();
	afx_msg void OnUtilsRemoveSettingsFromRegistry();
	afx_msg void OnCommandsOpencomputerConfigure();
	afx_msg void OnHelpDonationPage();
	afx_msg void OnHelpCheckForNewerVersion();
	afx_msg void OnHelpDownloadplugins();
	afx_msg void UpdateCurrentIPFeedDialog();
	//}}AFX_MSG
	
	afx_msg void OnExecuteShowMenu(UINT nID);
	afx_msg void OnExecuteOpenMenu(UINT nID);
	afx_msg void OnExecuteFavouritesMenu(UINT nID);

	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
