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

#if !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
#define AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ScanListCtrl.h : header file
//

#include "ScanListHeaderCtrl.h"

/////////////////////////////////////////////////////////////////////////////
// CScanListCtrl window

class CScanListCtrl : public CListCtrl
{
// Construction
public:
	CScanListCtrl();

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CScanListCtrl)
	protected:
	virtual void PreSubclassWindow();
	//}}AFX_VIRTUAL

// Implementation
public:
	void SetHeaderClicksDisabled(BOOL bHeaderClicksDisabled);
	void InitPostCreateStuff();
	void PrepareForScanning();
	void DeleteSelectedItems();
	int DeleteAllOpenPortsHosts();
	int DeleteAllClosedPortsHosts();
	int DeleteAllAliveHosts();
	int DeleteAllDeadHosts();
	int GetColumnCount();
	void SetScanPorts();
	int m_nPortsColumn;
	void GetOpenPorts(int nItemIndex, CString &szOpenPorts);
	DWORD GetNumericIP(int nItemIndex);
	void ZeroResultsForItem(int nItemIndex);
	CString m_szSearchFor;
	void GoToNextSearchIP();
	void GoToNextClosedPortIP();
	void GoToNextOpenPortIP();
	void GoToNextDeadIP();
	void SetSelectedItem(int nItem);
	void GoToNextAliveIP();
	void ShowNetBIOSInfo();
	void CopyIPToClipboard();
	int GetCurrentSelectedItem(BOOL bShowError = TRUE);
	void ShowIPDetails();	
	void SetOpenPorts(int nItemIndex, LPCSTR pNewStr, BOOL bSomeOpen);
	void SetShowPortsBelow(BOOL bShow);
	BOOL DeleteAllItems();
	int InsertColumn(int nCol, LPCTSTR lpszColumnHeading, int nFormat = LVCFMT_LEFT, int nWidth = -1, int nSubItem = -1);
	virtual ~CScanListCtrl();

protected:
	BOOL m_bHeaderClicksDisabled;
	BOOL m_bSearchCaseSensitive;
	CScanListHeaderCtrl m_ctlHeader;
	CImageList m_imglist;
	BOOL m_bShowPorts;		
	void DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct);
	void RepaintSelectedItems();	
	BOOL OnNotify(WPARAM wParam, LPARAM lParam, LRESULT* pResult);

	// Generated message map functions
protected:
	void ShowErrorNothingSelected();
	//{{AFX_MSG(CScanListCtrl)	
	afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
	afx_msg void OnPaint();
	//}}AFX_MSG
	afx_msg void MeasureItem(LPMEASUREITEMSTRUCT lpMeasureItemStruct);
	afx_msg void OnItemClickListHeader(NMHDR* pNMHDR, LRESULT* pResult);

	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
