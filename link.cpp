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
