// Link.cpp : implementation file
//

#include "stdafx.h"
#include "Link.h"
#include "resource.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

BEGIN_MESSAGE_MAP(CLink, CStatic)
	//{{AFX_MSG_MAP(CLink)
	ON_WM_PAINT()
	ON_WM_LBUTTONDOWN()
	ON_WM_SETCURSOR()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CLink

CLink::CLink()
{
	m_crText = RGB(0, 0, 0xFF);
	m_crClicked = RGB(0xFF, 0, 0);

	m_hCursor = AfxGetApp()->LoadCursor(IDC_HAND);
}

CLink::~CLink()
{
}

/////////////////////////////////////////////////////////////////////////////
// CLink message handlers

void CLink::OnPaint() 
{
	CPaintDC dc(this); // device context for painting
	
	RECT rc;
	GetClientRect(&rc);

	dc.SetTextColor(m_crText);
	dc.SetBkMode(TRANSPARENT);

	CFont fnt;
	fnt.CreateFontIndirect(&m_fnText);
	
	dc.SelectObject(fnt);
	
	CString szText;

	GetWindowText(szText);

	dc.DrawText(szText, &rc, DT_CENTER);
	
}

void CLink::PreSubclassWindow() 
{
	CStatic::PreSubclassWindow();
	
	GetFont()->GetObject(sizeof(m_fnText), &m_fnText);
	m_fnText.lfUnderline = TRUE;
}

void CLink::OnLButtonDown(UINT nFlags, CPoint point) 
{
	CString szText;
	GetWindowText(szText);

	// TODO: change color here
	
	CStatic::OnLButtonDown(nFlags, point);
}

BOOL CLink::OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message) 
{
	if (m_hCursor)
	{
		::SetCursor(m_hCursor);
		return TRUE;
	}
	
	return CStatic::OnSetCursor(pWnd, nHitTest, message);
}
