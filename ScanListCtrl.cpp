// ScanListCtrl.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "ScanListCtrl.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CScanListCtrl

CScanListCtrl::CScanListCtrl()
{
	m_bShowPorts = TRUE;	
}

CScanListCtrl::~CScanListCtrl()
{
}


BEGIN_MESSAGE_MAP(CScanListCtrl, CListCtrl)
	//{{AFX_MSG_MAP(CScanListCtrl)
		// NOTE - the ClassWizard will add and remove mapping macros here.
	//}}AFX_MSG_MAP
	ON_MESSAGE(WM_MEASUREITEM, MeasureItem)
	ON_WM_MEASUREITEM_REFLECT()
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CScanListCtrl message handlers

void CScanListCtrl::DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct)
{	
	CDC* pDC = CDC::FromHandle(lpDrawItemStruct->hDC);
	CRect rcItem(lpDrawItemStruct->rcItem);
	int nItem = lpDrawItemStruct->itemID;
	CImageList* pImageList;

	// Save dc state
	int nSavedDC = pDC->SaveDC();

	// Get item image and state info
	LV_ITEM lvi;
	lvi.mask = LVIF_IMAGE | LVIF_STATE;
	lvi.iItem = nItem;
	lvi.iSubItem = 0;
	lvi.stateMask = 0xFFFF;		// get all state flags
	GetItem(&lvi);

	// Should the item be highlighted
	BOOL bHighlight =((lvi.state & LVIS_DROPHILITED)
				|| ( (lvi.state & LVIS_SELECTED)
					&& ((GetFocus() == this)
						|| (GetStyle() & LVS_SHOWSELALWAYS)
						)
					)
				);


	// Get rectangles for drawing
	CRect rcBounds, rcLabel, rcIcon;
	GetItemRect(nItem, rcBounds, LVIR_BOUNDS);
	GetItemRect(nItem, rcLabel, LVIR_LABEL);
	GetItemRect(nItem, rcIcon, LVIR_ICON);
	CRect rcCol;

	CString sLabel = GetItemText(nItem, 0);

	// Labels are offset by a certain amount  
	// This offset is related to the width of a space character
	int offset = pDC->GetTextExtent(_T(" "), 1 ).cx*2;

	CRect rcHighlight;
	CRect rcWnd;	
	
	rcHighlight = rcBounds;
	rcHighlight.left = rcLabel.left;
	
	// Draw the background color
	if( bHighlight )
	{		
		pDC->SetBkColor(::GetSysColor(COLOR_HIGHLIGHT));

		pDC->FillRect(rcHighlight, &CBrush(::GetSysColor(COLOR_HIGHLIGHT)));
	}
	else
	{
		if (nItem % 2 == 1)
		{
			// Even
			pDC->FillRect(rcHighlight, &CBrush(::GetSysColor(COLOR_INFOBK)));			
		}
		else
		{
			// Odd
			pDC->FillRect(rcHighlight, &CBrush(::GetSysColor(COLOR_WINDOW)));
		}
	}

	// Draw port scan results
	if (m_bShowPorts)
	{
		pDC->SetTextColor(::GetSysColor(COLOR_GRAYTEXT));
		rcCol = rcHighlight;
		rcCol.bottom -= 2;	rcCol.left = rcLabel.right;
		
		CString *pOpenPorts = (CString *) GetItemData(nItem);

		CString szOpenPorts = "Open ports: ";
		if (pOpenPorts != NULL)
			szOpenPorts += *pOpenPorts;
		else
			szOpenPorts += "N/A";

		pDC->DrawText(szOpenPorts, -1, rcCol, DT_LEFT | DT_SINGLELINE | DT_NOPREFIX | DT_NOCLIP 
					| DT_BOTTOM | DT_END_ELLIPSIS);
	}

	if (bHighlight)
	{
		pDC->SetTextColor(::GetSysColor(COLOR_HIGHLIGHTTEXT));
	}
	else
	{
		pDC->SetTextColor(::GetSysColor(COLOR_WINDOWTEXT));
	}
	
	// Set clip region
	rcCol = rcBounds;
	rcCol.right = rcCol.left + GetColumnWidth(0);
	CRgn rgn;
	rgn.CreateRectRgnIndirect(&rcCol);
	pDC->SelectClipRgn(&rgn);
	rgn.DeleteObject();

	// Draw state icon
	/*if (lvi.state & LVIS_STATEIMAGEMASK)
	{
		int nImage = ((lvi.state & LVIS_STATEIMAGEMASK) >> 12) - 1;
		pImageList = GetImageList(LVSIL_STATE);
		if (pImageList)
		{
			pImageList->Draw(pDC, nImage,
				CPoint(rcCol.left, rcCol.top), ILD_TRANSPARENT);
		}
	}*/
	
	// Draw normal and overlay icon
	pImageList = GetImageList(LVSIL_SMALL);
	if (pImageList)
	{
		UINT nOvlImageMask=lvi.state & LVIS_OVERLAYMASK;
		pImageList->Draw(pDC, lvi.iImage, 
			CPoint(rcIcon.left, rcIcon.top),
			(bHighlight ? ILD_BLEND25 : 0) | ILD_TRANSPARENT | nOvlImageMask );
	}

	
	// Draw item label - Column 0
	rcLabel.left += offset/2;
	rcLabel.right -= offset;

	// Draw an IP address
	rcLabel.top += 2;
	pDC->DrawText(sLabel,-1,rcLabel,DT_LEFT | DT_SINGLELINE | DT_NOPREFIX | DT_NOCLIP 
				| DT_TOP | DT_END_ELLIPSIS);
	
	// Draw labels for remaining columns
	
	LV_COLUMN lvc;
	lvc.mask = LVCF_FMT | LVCF_WIDTH;

	rcBounds.right = rcHighlight.right > rcBounds.right ? rcHighlight.right :
							rcBounds.right;
	rgn.CreateRectRgnIndirect(&rcBounds);
	pDC->SelectClipRgn(&rgn);
	rgn.DeleteObject();
			   	
	for(int nColumn = 1; GetColumn(nColumn, &lvc); nColumn++)
	{
		rcCol.left = rcCol.right;
		rcCol.right += lvc.cx;

		sLabel = GetItemText(nItem, nColumn);
		if (sLabel.GetLength() == 0)
			continue;

		// Get the text justification
		UINT nJustify = DT_LEFT;
		switch(lvc.fmt & LVCFMT_JUSTIFYMASK)
		{
		case LVCFMT_RIGHT:
			nJustify = DT_RIGHT;
			break;
		case LVCFMT_CENTER:
			nJustify = DT_CENTER;
			break;
		default:
			break;
		}

		rcLabel = rcCol;
		rcLabel.top += 2;
		rcLabel.left += offset;
		rcLabel.right -= offset;

		pDC->DrawText(sLabel, -1, rcLabel, nJustify | DT_SINGLELINE | 
					DT_NOPREFIX | DT_TOP | DT_END_ELLIPSIS);
	}	

	// Draw focus rectangle if item has focus
	if (lvi.state & LVIS_FOCUSED && (GetFocus() == this))
		pDC->DrawFocusRect(rcHighlight);

	
	// Restore dc
	pDC->RestoreDC( nSavedDC );
}

void CScanListCtrl::RepaintSelectedItems()
{
	CRect rcBounds, rcLabel;

	// Invalidate focused item so it can repaint 
	int nItem = GetNextItem(-1, LVNI_FOCUSED);

	if(nItem != -1)
	{
		GetItemRect(nItem, rcBounds, LVIR_BOUNDS);
		GetItemRect(nItem, rcLabel, LVIR_LABEL);
		rcBounds.left = rcLabel.left;

		InvalidateRect(rcBounds, FALSE);
	}

	// Invalidate selected items depending on LVS_SHOWSELALWAYS
	if(!(GetStyle() & LVS_SHOWSELALWAYS))
	{
		for(nItem = GetNextItem(-1, LVNI_SELECTED);
			nItem != -1; nItem = GetNextItem(nItem, LVNI_SELECTED))
		{
			GetItemRect(nItem, rcBounds, LVIR_BOUNDS);
			GetItemRect(nItem, rcLabel, LVIR_LABEL);
			rcBounds.left = rcLabel.left;

			InvalidateRect(rcBounds, FALSE);
		}
	}

	UpdateWindow();
}

void CScanListCtrl::MeasureItem(LPMEASUREITEMSTRUCT lpMeasureItemStruct)
{
	LOGFONT lf;
	
	GetFont()->GetLogFont( &lf );
	
	if (m_bShowPorts)
		lf.lfHeight = lf.lfHeight * 28 / 10;
	else
		lf.lfHeight = lf.lfHeight * 16 / 10;

	if( lf.lfHeight < 0 )
		lpMeasureItemStruct->itemHeight = -lf.lfHeight; 
	else
		lpMeasureItemStruct->itemHeight = lf.lfHeight; 
}

BOOL CScanListCtrl::OnNotify(WPARAM wParam, LPARAM lParam, LRESULT* pResult) 
{
	HD_NOTIFY	*pHDN = (HD_NOTIFY*)lParam;

	if(m_bShowPorts && (pHDN->hdr.code == HDN_ITEMCHANGINGW || pHDN->hdr.code == HDN_ITEMCHANGINGA))
	{
		CRect rcClient;
		GetClientRect( &rcClient );
		DWORD dwPos = GetMessagePos();
		CPoint pt( LOWORD(dwPos), HIWORD(dwPos) );
		ScreenToClient( &pt );
		rcClient.left = pt.x;
		InvalidateRect( &rcClient );
	}
	return CListCtrl::OnNotify(wParam, lParam, pResult);
}


void CScanListCtrl::SetShowPorts(BOOL bShow)
{
	m_bShowPorts = bShow;

	CRect rc;
	GetWindowRect( &rc );

	WINDOWPOS wp;
	wp.hwnd = m_hWnd;
	wp.cx = rc.Width();
	wp.cy = rc.Height();
	wp.flags = SWP_NOACTIVATE | SWP_NOMOVE | SWP_NOOWNERZORDER | SWP_NOZORDER;
	SendMessage( WM_WINDOWPOSCHANGED, 0, (LPARAM)&wp );
}

BOOL CScanListCtrl::DeleteAllItems()
{
	for (int i=0; i < GetItemCount(); i++)
	{
		DeleteOpenPorts(i);
	}

	return CListCtrl::DeleteAllItems();
}

void CScanListCtrl::SetOpenPorts(int nItemIndex, LPCSTR pNewStr)
{
	DeleteOpenPorts(nItemIndex);

	CString *pStr = new CString(pNewStr);

	// Set ports string
	SetItemData(nItemIndex, (DWORD) pStr);

	// Set image to green
	SetItem(nItemIndex,0,LVIF_IMAGE,NULL,3,0,0,0);	
}

void CScanListCtrl::DeleteOpenPorts(int nItemIndex)
{
	CString *pStr = (CString*) GetItemData(nItemIndex);
	
	if (pStr != NULL)
		delete pStr;
}
