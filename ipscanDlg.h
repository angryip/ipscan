// ipscanDlg.h : header file
//

#if !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
#define AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#define C_COLUMNS		6
#define CL_IP			0
#define CL_STATE		1
#define CL_HOSTNAME		2
#define CL_PORT			3
#define CL_PINGTIME		4
#define CL_ERROR		5

// Diplay Options
#define DO_ALL		0
#define DO_ALIVE	1
#define DO_OPENPORT	2


/////////////////////////////////////////////////////////////////////////////
// CIpscanDlg dialog

class CIpscanDlg : public CDialog
{
// Construction
public:
	int m_nOptions;
	CString *m_szDefaultFileName;
	int m_display;
	CBitmap killbmp;
	CString m_search;
	void ErrorNotSelected();
	BOOL m_portondead;
	BOOL m_ip2_virgin;
	UINT m_timeout;
	CBitmap stopbmp;
	CBitmap startbmp;
	CBitmap pastebmp;
	CBitmap m_bmpuparrow;
	UINT m_maxthreads;
	BOOL m_retrifdead;
	int m_menucuritem;
	CMenu * ctx_item;
	CMenu * ctx_noitem;
	CMenu mnu;
	unsigned long m_tickcount;
	unsigned long m_endip;
	unsigned long m_startip;
	unsigned long m_curip;
	int m_scanning;
	int m_delay;
	UINT m_port;
	BOOL m_scanport;
	BOOL m_resolve;
	CImageList m_imglist;
	CIpscanDlg(CWnd* pParent = NULL);	// standard constructor

	void status(LPCSTR str);

// Dialog Data
	//{{AFX_DATA(CIpscanDlg)
	enum { IDD = IDD_IPSCAN_DIALOG };
	CButton	m_ipup;
	CStatic m_numthreads;
	CProgressCtrl	m_progress;
	CIPAddressCtrl	m_ip2;
	CIPAddressCtrl	m_ip1;
	CStatic	m_statusctl;
	CListCtrl	m_list;
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
	afx_msg void OnButton1();
	afx_msg void OnHelpAbout();
	afx_msg void OnOptionsOptions();
	afx_msg void OnButtonipup();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnScanSavetotxt();
	afx_msg void OnRclickList(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnOpencomputerinexplorer();
	afx_msg void OnShowerrordescription();
	afx_msg void OnWindozesucksIpclipboard();
	afx_msg void OnWindozesucksHostnameclipboard();
	afx_msg void OnScanSaveselection();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg void OnOptionsSaveoptions();
	afx_msg void OnFieldchangedIpaddress1(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnFieldchangedIpaddress2(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnClassC();
	afx_msg void OnClassD();
	afx_msg void OnWindozesucksShownetbiosinfo();
	afx_msg void OnHelpAngryipscannerwebpage();
	afx_msg void OnHelpAngryzibersoftware();
	afx_msg void OnWindozesucksRescanip();
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
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPSCANDLG_H__F45ACDE6_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
