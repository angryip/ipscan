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

#include "stdafx.h"
#include "ScanListHeaderCtrl.h"
#include "globals.h"
#include "IpscanDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

CScanListHeaderCtrl::CScanListHeaderCtrl()
	: m_nSortColumn(-1)
	, m_bSortAscending(TRUE)
{
}

CScanListHeaderCtrl::~CScanListHeaderCtrl()
{
}


BEGIN_MESSAGE_MAP(CScanListHeaderCtrl, CHeaderCtrl)
	//{{AFX_MSG_MAP(CScanListHeaderCtrl)
		// NOTE - the ClassWizard will add and remove mapping macros here.
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CScanListHeaderCtrl message handlers

void CScanListHeaderCtrl::SetSortArrow(const int nSortColumn, const BOOL bSortAscending)
{
	m_nSortColumn = nSortColumn;
	m_bSortAscending = bSortAscending;

	// invalidate the header control so it gets redrawn
	Invalidate();
}


void CScanListHeaderCtrl::DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct)
{
	// attath to the device context.
	CDC dc;
	VERIFY(dc.Attach(lpDrawItemStruct->hDC));

	// save the device context.
	const int nSavedDC = dc.SaveDC();

	// get the column rect.
	CRect rc(lpDrawItemStruct->rcItem);

	// set the clipping region to limit drawing within the column.
	CRgn rgn;
	VERIFY(rgn.CreateRectRgnIndirect(&rc));
	(void)dc.SelectObject(&rgn);
	VERIFY(rgn.DeleteObject());

	// draw the background,
	CBrush brush(GetSysColor(COLOR_3DFACE));
	dc.FillRect(rc, &brush);

	// get the column text and format.
	TCHAR szText[256];
	HD_ITEM hditem;

	hditem.mask = HDI_TEXT | HDI_FORMAT;
	hditem.pszText = szText;
	hditem.cchTextMax = 255;

	VERIFY(GetItem(lpDrawItemStruct->itemID, &hditem));

	// determine the format for drawing the column label.
	UINT uFormat = DT_SINGLELINE | DT_NOPREFIX | DT_NOCLIP | DT_VCENTER | DT_END_ELLIPSIS ;	

	if(hditem.fmt & HDF_CENTER)
		uFormat |= DT_CENTER;
	else if(hditem.fmt & HDF_RIGHT)
		uFormat |= DT_RIGHT;
	else
		uFormat |= DT_LEFT;

	// adjust the rect if the mouse button is pressed on it.
	if(lpDrawItemStruct->itemState == ODS_SELECTED)
	{
		rc.left++;
		rc.top += 2;
		rc.right++;
	}

	CRect rcStaticIcons(lpDrawItemStruct->rcItem);
	CRect rcSortIcon(lpDrawItemStruct->rcItem);
	const int nOffset = (rcSortIcon.bottom - rcSortIcon.top) / 4;

	// adjust the rect further if the sort arrow is to be displayed.
	if(lpDrawItemStruct->itemID == (UINT)m_nSortColumn)
		rc.right -= 3 * nOffset;

	// adjust the rect for static icons
	rc.right -= 34;

	rc.left += nOffset;
	rc.right -= nOffset;

	// draw the column label.
	if(rc.left < rc.right)
		(void)dc.DrawText(szText, -1, rc, uFormat);
	
	UINT uIconTextFormat = DT_SINGLELINE | DT_NOPREFIX | DT_NOCLIP | DT_VCENTER | DT_RIGHT;
	dc.SetBkMode(TRANSPARENT);

	/* Enable this to make static icons go down on mouse click
	if(lpDrawItemStruct->itemState == ODS_SELECTED)
	{
		rcStaticIcons.top += 2;
		rcStaticIcons.right += 2;
	}*/

	// Draw info icon	
	rcStaticIcons.right -= 5;
	CFont fontInfo;
	fontInfo.CreateFont(rcSortIcon.bottom - rcSortIcon.top - 2, 10, 0, 0, 50, 0, 0, 0, 0, 0, 0, 10, 10, "Times");
	dc.SelectObject(&fontInfo);
	dc.SetTextColor(GetSysColor(COLOR_3DSHADOW));
	dc.DrawText("i", -1, rcStaticIcons, uIconTextFormat);
	rcStaticIcons.top -= 2; rcStaticIcons.right -= 2;
	dc.SetTextColor(0x00DD0000);	
	dc.DrawText("i", -1, rcStaticIcons, uIconTextFormat);

	// Draw options icon
	rcStaticIcons.right -= 10;
	CFont fontSetup;
	fontSetup.CreateFont(rcSortIcon.bottom - rcSortIcon.top - 2, 8, 0, 0, 50, 0, 0, 0, 0, 0, 0, 10, 10, "Times");
	dc.SelectObject(&fontSetup);
	dc.SetTextColor(GetSysColor(COLOR_3DSHADOW));
	rcStaticIcons.top += 2; rcStaticIcons.right += 2;
	dc.DrawText("o", -1, rcStaticIcons, uIconTextFormat);
	rcStaticIcons.top -= 2; rcStaticIcons.right -= 2;
	dc.SetTextColor(0x0000CC00);	
	dc.DrawText("o", -1, rcStaticIcons, uIconTextFormat);

	// Move sort icon to the left of static icons
	rcSortIcon.right -= 25;

	// Amount of movement when header is clicked
	int nMoveClickX = 0;
	int nMoveClickY = 0;

	/* Enable this to make sort icon go down on mouse click
	if(lpDrawItemStruct->itemState == ODS_SELECTED)
	{
		nMoveClickX = 2;
		nMoveClickY = 1;
	}*/

	// draw the sort arrow.
	if(lpDrawItemStruct->itemID == (UINT)m_nSortColumn)
	{
		// set up the pens to use for drawing the arrow.		
		CPen penLight(PS_SOLID, 1, GetSysColor(COLOR_3DHILIGHT));
		CPen penShadow(PS_SOLID, 1, GetSysColor(COLOR_3DSHADOW));
		CPen* pOldPen = dc.SelectObject(&penLight);		

		if(m_bSortAscending)
		{
			// draw the arrow pointing upwards.
			dc.MoveTo(rcSortIcon.right - 2 * nOffset + nMoveClickX, nOffset + nMoveClickY);
			dc.LineTo(rcSortIcon.right - nOffset + nMoveClickX, rcSortIcon.bottom - nOffset - 1 + nMoveClickY);
			dc.LineTo(rcSortIcon.right - 3 * nOffset - 2 + nMoveClickX, rcSortIcon.bottom - nOffset - 1 + nMoveClickY);
			(void)dc.SelectObject(&penShadow);
			dc.MoveTo(rcSortIcon.right - 3 * nOffset - 1 + nMoveClickX, rcSortIcon.bottom - nOffset - 1 + nMoveClickY);
			dc.LineTo(rcSortIcon.right - 2 * nOffset + nMoveClickX, nOffset - 1 + nMoveClickY);		
		}
		else
		{
			// draw the arrow pointing downwards.
			dc.MoveTo(rcSortIcon.right - nOffset - 1 + nMoveClickX, nOffset + nMoveClickY);
			dc.LineTo(rcSortIcon.right - 2 * nOffset - 1 + nMoveClickX, rcSortIcon.bottom - nOffset + nMoveClickY);
			(void)dc.SelectObject(&penShadow);
			dc.MoveTo(rcSortIcon.right - 2 * nOffset - 2 + nMoveClickX, rcSortIcon.bottom - nOffset + nMoveClickY);
			dc.LineTo(rcSortIcon.right - 3 * nOffset - 1 + nMoveClickX, nOffset + nMoveClickY);
			dc.LineTo(rcSortIcon.right - nOffset - 1 + nMoveClickX, nOffset + nMoveClickY);		
		}

		// restore the pen.
		(void)dc.SelectObject(pOldPen);
	}

	// restore the previous device context.
	VERIFY(dc.RestoreDC(nSavedDC));

	// detach the device context before returning.
	(void)dc.Detach();
}

BOOL CScanListHeaderCtrl::IsSortingAscending()
{
	return m_bSortAscending;
}

int CScanListHeaderCtrl::GetSortedColumn()
{
	return m_nSortColumn;
}
