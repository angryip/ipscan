#if !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
#define AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ScanListCtrl.h : header file
//

#define OPEN_PORTS_STATUS_SCANNING		0
#define OPEN_PORTS_STATUS_NONE			1
#define OPEN_PORTS_STATUS_NOT_SCANNED	2
#define OPEN_PORTS_STATUS_OPEN			5


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
	//}}AFX_VIRTUAL

// Implementation
public:
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
	void DeleteOpenPorts(int nItemIndex);
	void SetOpenPorts(int nItemIndex, LPCSTR pNewStr);
	void SetShowPorts(BOOL bShow);
	BOOL DeleteAllItems();
	virtual ~CScanListCtrl();

protected:
	BOOL m_bShowPorts;	
	void DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct);
	void RepaintSelectedItems();	
	BOOL OnNotify(WPARAM wParam, LPARAM lParam, LRESULT* pResult);

	// Generated message map functions
protected:
	void ShowErrorNothingSelected();
	//{{AFX_MSG(CScanListCtrl)	
	afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
	//}}AFX_MSG
	afx_msg void MeasureItem(LPMEASUREITEMSTRUCT lpMeasureItemStruct);

	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
