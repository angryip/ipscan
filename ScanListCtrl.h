#if !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
#define AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ScanListCtrl.h : header file
//

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
	void SetShowPorts(BOOL bShow);
	virtual ~CScanListCtrl();

protected:
	BOOL m_bShowPorts;
	void DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct);
	void RepaintSelectedItems();	
	BOOL OnNotify(WPARAM wParam, LPARAM lParam, LRESULT* pResult);

	// Generated message map functions
protected:
	//{{AFX_MSG(CScanListCtrl)
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG
	afx_msg void MeasureItem(LPMEASUREITEMSTRUCT lpMeasureItemStruct);

	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SCANLISTCTRL_H__204B478B_DE3A_42DE_8A5B_F1665F484EAA__INCLUDED_)
