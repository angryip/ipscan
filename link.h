#if !defined(AFX_LINK_H__60CE25A7_C674_4FC4_A4CF_B579C8113495__INCLUDED_)
#define AFX_LINK_H__60CE25A7_C674_4FC4_A4CF_B579C8113495__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// Link.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CLink window

class CLink : public CStatic
{
// Construction
public:
	CLink();

// Attributes
protected:
	COLORREF	m_crText;
	LOGFONT		m_fnText;
	HCURSOR		m_hCursor;
	COLORREF	m_crClicked;

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CLink)
	protected:
	virtual void PreSubclassWindow();
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CLink();

	// Generated message map functions
protected:		
	//{{AFX_MSG(CLink)
	afx_msg void OnPaint();
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_LINK_H__60CE25A7_C674_4FC4_A4CF_B579C8113495__INCLUDED_)
