// ipscanDlg.h : header file
//

#if !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
#define AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Scanner.h"
#include "ScanListCtrl.h"

#define SCAN_MODE_NOT_SCANNING	0
#define SCAN_MODE_SCANNING		1
#define SCAN_MODE_FINISHING		2


/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg dialog

class CIpscanDlg : public CDialog
{
// Construction
public:
	CBitmap m_bmpShowAdvanced;
	int m_nCmdLineOptions;
	CString *m_szDefaultFileName;			
	BOOL m_portondead;
	BOOL m_ip2_virgin;
	UINT m_timeout;
	CBitmap m_bmpStop;
	CBitmap m_bmpStart;
	CBitmap m_bmpPaste;
	CBitmap m_bmpUpArrow;		
	CBitmap m_bmpKill;
	int m_menucuritem;
	CMenu * m_menuContext;
	unsigned long m_tickcount;
	unsigned long m_endip;
	unsigned long m_startip;
	unsigned long m_curip;
	int m_nScanMode;
	CImageList m_imglist;
	CIpscanDlg(CWnd* pParent = NULL);	// standard constructor

	void status(LPCSTR str);

// Dialog Data
	//{{AFX_DATA(CIpscanDlg)
	enum { IDD = IDD_IPSCAN_DIALOG };
	CButton	m_ctScanPorts;
	CStatic	m_ctWhatPorts;
	CScanListCtrl	m_list;
	CButton	m_ipup;
	CStatic m_numthreads;
	CProgressCtrl	m_progress;
	CIPAddressCtrl	m_ip2;
	CIPAddressCtrl	m_ip1;
	CStatic	m_statusctl;
	CString	m_hostname;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIpscanDlg)
	public:
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:		
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
	afx_msg void OnButtonipup();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnScanSavetotxt();
	afx_msg void OnRclickList(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnOpencomputerinexplorer();	
	afx_msg void OnIPToClipboard();	
	afx_msg void OnScanSaveselection();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg void OnOptionsSaveoptions();
	afx_msg void OnFieldchangedIpaddress1(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnFieldchangedIpaddress2(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnClassC();
	afx_msg void OnClassD();
	afx_msg void OnShowNetBIOSInfo();
	afx_msg void OnHelpAngryipscannerwebpage();
	afx_msg void OnHelpAngryzibersoftware();
	afx_msg void OnRescanIP();
	afx_msg void OnGotoNextalive();
	afx_msg void OnGotoNextdead();
	afx_msg void OnGotoNextopenport();
	afx_msg void OnGotoNextclosedport();
	afx_msg void OnGotoHostname();
	afx_msg void OnItemclickListHeader(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnCommandsOpencomputerAsftp();
	afx_msg void OnCommandsOpencomputerAswebsite();
	afx_msg void OnCommandsOpencomputerTelnet();
	afx_msg void OnCommandsOpencomputerTelnettospecifiedport();
	afx_msg void OnCommandsOpencomputerHint();
	afx_msg void OnButtonpaste();
	afx_msg void OnHelpCommandline();
	afx_msg void OnHelpForum();
	afx_msg void OnOptionsInstallProgram();
	afx_msg void OnDestroy();
	afx_msg void OnDrawItem(int nIDCtl, LPDRAWITEMSTRUCT lpDrawItemStruct);
	afx_msg void OnButtonToAdvanced();
	afx_msg void OnScanPortsClicked();
	afx_msg void OnSelectPortsClicked();
	afx_msg void OnCommandsShowIPdetails();
	//}}AFX_MSG
	
	afx_msg void OnExecuteShowMenu(UINT nID);

	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
