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
	ON_WM_MOUSEMOVE()
	//}}AFX_MSG_MAP
	ON_MESSAGE(WM_MOUSELEAVE,OnMouseLeave)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CLink

#define COLOR_INACTIVE	RGB(0, 0, 0xAA)
#define COLOR_ACTIVE	RGB(0, 0, 0xFF)

CLink::CLink()
{
	m_crText = COLOR_INACTIVE;
	m_crClicked = COLOR_ACTIVE;

	m_hCursor = AfxGetApp()->LoadCursor(IDC_HAND);
	m_bTrackLeave = FALSE;
}

CLink::~CLink()
{
}

void CLink::PaintLink(CString &szText, COLORREF crColor)
{
	CPaintDC dc(this); // device context for painting

	RECT rc;
	GetClientRect(&rc);

	dc.SetTextColor(crColor);
	dc.SetBkMode(TRANSPARENT);

	CFont fnt;
	fnt.CreateFontIndirect(&m_fnText);
	
	dc.SelectObject(fnt);
	
	dc.DrawText(szText, &rc, DT_CENTER);
}

/////////////////////////////////////////////////////////////////////////////
// CLink message handlers

void CLink::OnPaint() 
{	
	
	CString szText;
	GetWindowText(szText);

	PaintLink(szText, m_crText);	
	
}

void CLink::PreSubclassWindow() 
{
	CStatic::PreSubclassWindow();
	
	GetFont()->GetObject(sizeof(m_fnText), &m_fnText);
	m_fnText.lfUnderline = TRUE;
}

void CLink::OnLButtonDown(UINT nFlags, CPoint point) 
{	
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

void CLink::OnMouseMove(UINT nFlags, CPoint point) 
{
	if (!m_bTrackLeave) 
	{
        // First time since mouse entered my window: request leave notification
        TRACKMOUSEEVENT tme;
        tme.cbSize = sizeof(tme);
        tme.hwndTrack = m_hWnd;
        tme.dwFlags = TME_LEAVE;
        _TrackMouseEvent(&tme);
        m_bTrackLeave = TRUE;
	
		m_crText = COLOR_ACTIVE;
		Invalidate(FALSE);
		UpdateWindow();
	}
	CStatic::OnMouseMove(nFlags, point);
}


LPARAM CLink::OnMouseLeave(WPARAM wp, LPARAM lp)
{    
	m_crText = COLOR_INACTIVE;
	m_bTrackLeave = FALSE;
	Invalidate(FALSE);
	UpdateWindow();
    return 0;
}

void CLink::goToScannerHomepage()
{
	CString szURL;
	szURL.LoadString(IDS_SCAN_HOMEPAGE);
	ShellExecute(0, NULL, szURL, NULL, NULL, SW_SHOWNORMAL);
}

void CLink::goToHomepage()
{
	CString szURL;
	szURL.LoadString(IDS_HOMEPAGE);
	ShellExecute(0, NULL, szURL, NULL, NULL, SW_SHOWNORMAL);
}

void CLink::goToWriteMail()
{
	CString szMail;
	szMail.LoadString(IDS_MAIL);
	szMail = "mailto:" + szMail;
	ShellExecute(0, NULL, szMail, NULL, NULL, SW_SHOWNORMAL);	
}

