// ScanListCtrl.cpp : implementation file
//

#include "stdafx.h"
#include "ipscan.h"
#include "ScanListCtrl.h"
#include "DetailsDlg.h"
#include "SearchDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#include "MessageDlg.h"
#include "NetBIOSUtils.h"
#include "Scanner.h"

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
	ON_WM_LBUTTONDBLCLK()
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

	// Draw port scan results below other results for each item
	if (m_bShowPorts)
	{
		pDC->SetTextColor(::GetSysColor(COLOR_GRAYTEXT));
		rcCol = rcHighlight;
		rcCol.bottom -= 2;	rcCol.left = rcLabel.right;
		
		CString szOpenPorts;
		
		GetOpenPorts(nItem, szOpenPorts);

		szOpenPorts = "Open ports: " + szOpenPorts;		

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

void CScanListCtrl::SetScanPorts()
{
	if (g_options->m_bScanPorts)
	{
		if (GetColumnCount() == g_scanner->getColumnCount())
			InsertColumn(g_scanner->getColumnCount(), "Open ports");
	}
	else
	{
		if (GetColumnCount() > g_scanner->getColumnCount())
			DeleteColumn(g_scanner->getColumnCount());
	}
	
	SetShowPortsBelow(g_options->m_bShowPortsBelow && g_options->m_bScanPorts);	// Update this status
}

void CScanListCtrl::SetShowPortsBelow(BOOL bShow)
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
	// This is called before each scanning
	// So we may use this method for initialization

	if (g_options->m_bScanPorts)
		m_nPortsColumn = g_scanner->getColumnCount();	// the last column
	else
		m_nPortsColumn = -1;

	return CListCtrl::DeleteAllItems();
}

void CScanListCtrl::SetOpenPorts(int nItemIndex, LPCSTR pNewStr, BOOL bSomeOpen)
{
	if (!g_options->m_bScanPorts)
		return;	

	CString *pStr = new CString(pNewStr);

	// Set ports string				
	SetItem(nItemIndex, m_nPortsColumn, LVIF_TEXT, pNewStr, 0, 0, 0, 0);			

	if (bSomeOpen)
	{
		// Set image to green
		SetItem(nItemIndex,0,LVIF_IMAGE,NULL,3,0,0,0);	
	}

	Update(nItemIndex);
}

void CScanListCtrl::GetOpenPorts(int nItemIndex, CString &szOpenPorts)
{	
	if (g_options->m_bScanPorts)
		szOpenPorts = GetItemText(nItemIndex, m_nPortsColumn);
}

void CScanListCtrl::OnLButtonDblClk(UINT nFlags, CPoint point) 
{
	ShowIPDetails();
	
	CListCtrl::OnLButtonDblClk(nFlags, point);
}

void CScanListCtrl::ShowErrorNothingSelected()
{
	MessageBox("You must select an IP first","Error",MB_OK | MB_ICONERROR);
}

int CScanListCtrl::GetCurrentSelectedItem(BOOL bShowError)
{
	POSITION pos = GetFirstSelectedItemPosition();
	int nCurrentItem = GetNextSelectedItem(pos);
	if (nCurrentItem < 0 && bShowError) 
	{ 
		ShowErrorNothingSelected(); 		
	}
	
	return nCurrentItem;
}

void CScanListCtrl::CopyIPToClipboard()
{
	int nCurrentItem = GetCurrentSelectedItem();
	
	if (nCurrentItem < 0)
		return;
	
	CString szIP = GetItemText(nCurrentItem, CL_IP);
	
	HGLOBAL hglbCopy = GlobalAlloc(GMEM_DDESHARE, szIP.GetLength() + 1); 
	LPTSTR lp;
	lp = (char*)GlobalLock(hglbCopy);	
	memcpy(lp, szIP, szIP.GetLength() + 1);	
	GlobalUnlock(lp);

	OpenClipboard();
	EmptyClipboard();
	SetClipboardData(CF_TEXT,hglbCopy);
	CloseClipboard();
}

void CScanListCtrl::ShowNetBIOSInfo()
{
	int nCurrentItem = GetCurrentSelectedItem();
	
	if (nCurrentItem < 0)
		return;

	CString szMessage;
	char ipstr[16];	
	CString szUserName, szComputerName, szGroupName, szMacAddress;
	CMessageDlg cMessageDlg(this);	
	
	GetItemText(nCurrentItem, CL_IP, (char*)&ipstr, 16);

	CNetBIOSUtils cNetBIOS;
	cNetBIOS.setIP((char*)&ipstr);
	if (!cNetBIOS.GetNames(&szUserName, &szComputerName, &szGroupName, &szMacAddress))
	{
		MessageBox("Cannot get NetBIOS information","Error",MB_OK | MB_ICONERROR);
		return;
	}
	
	szMessage.Format(
		"NetBIOS information for %s\r\n\r\n"
		"Computer Name:\t%s\r\n"
		"Workgroup Name:\t%s\r\n"
		"Username:\t%s\r\n"
		"\r\n"
		"MAC Address:\r\n%s",
		(char*) &ipstr, szComputerName, szGroupName, szUserName, szMacAddress
	);

	cMessageDlg.setMessageText(szMessage);
	cMessageDlg.DoModal();
}

void CScanListCtrl::ShowIPDetails()
{
	int nCurrentItem = GetCurrentSelectedItem(FALSE);

	if (nCurrentItem < 0)
		return;
	
	CDetailsDlg cDlg(this);

	// Set columns
	int nColumns = GetColumnCount();
	CString szInfoLine;

	for (int i=0; i < nColumns; i++)
	{
		g_scanner->getColumnName(i, szInfoLine);
		szInfoLine += ":\t";
		szInfoLine += GetItemText(nCurrentItem, i);

		cDlg.addScannedInfo(szInfoLine);
	}

	if (g_options->m_bScanPorts)
	{
		CString szOpenPorts;
		GetOpenPorts(nCurrentItem, szOpenPorts);

		// Add ports to the main window as well
		szInfoLine = "Open ports:\t";
		szInfoLine += szOpenPorts;
	}
	
	cDlg.DoModal();
}

void CScanListCtrl::SetSelectedItem(int nItem)
{
	SetItemState(-1, 0, LVIS_SELECTED);
	SetItemState(nItem, LVIS_SELECTED | LVIS_FOCUSED,LVIS_SELECTED | LVIS_FOCUSED);
	EnsureVisible(nItem, FALSE);
}

void CScanListCtrl::GoToNextAliveIP()
{
	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (; i < GetItemCount(); i++) 
	{
		if (!(GetItemText(i, CL_PING) == "Dead")) 
		{
			SetSelectedItem(i);
			break;
		}
	}
}


void CScanListCtrl::GoToNextDeadIP()
{
	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (; i < GetItemCount(); i++) 
	{
		if (GetItemText(i, CL_PING) == "Dead") 
		{
			SetSelectedItem(i);
			break;
		}
	}
}

void CScanListCtrl::GoToNextOpenPortIP()
{
	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (; i < GetItemCount(); i++) 
	{
		if (GetItemText(i, m_nPortsColumn).GetAt(0) != 'N')		// "N/A" or "N/S"
		{
			SetSelectedItem(i);
			break;
		}
	}
}

void CScanListCtrl::GoToNextClosedPortIP()
{
	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (; i < GetItemCount(); i++) 
	{
		if (GetItemText(i, m_nPortsColumn).GetAt(0) == 'N')		// "N/A" or "N/S"
		{
			SetSelectedItem(i);
			break;
		}
	}
}

void CScanListCtrl::GoToNextSearchIP()
{
	CSearchDlg cSearchDlg;

	cSearchDlg.m_search = m_szSearchFor;
	
	if (cSearchDlg.DoModal()==IDCANCEL) 
		return;

	m_szSearchFor = cSearchDlg.m_search;

	int i;

	if (cSearchDlg.m_beginning) 
	{		
		i = 0;
	}
	else
	{
		i = GetCurrentSelectedItem(FALSE) + 1;
	}
	
	SetFocus();	

	if (cSearchDlg.m_case) 
	{
		for (; i < GetItemCount(); i++) 
		{
			for (int nCol = 0; nCol < GetColumnCount(); nCol++)
			{
				if (GetItemText(i, nCol).Find(m_szSearchFor) != -1) 
				{
					SetSelectedItem(i);
					return;
				}
			}
		}
	} 
	else 
	{
		CString szLowerSearchFor(m_szSearchFor);
		szLowerSearchFor.MakeLower();

		CString szTmp;		
		for (; i < GetItemCount(); i++) 
		{
			for (int nCol = 0; nCol < GetColumnCount(); nCol++)
			{
				szTmp = GetItemText(i, nCol);
				szTmp.MakeLower();
				if (szTmp.Find(szLowerSearchFor) != -1) 
				{
					SetSelectedItem(i);
					return;
				}
			}
		}
	}

	AfxMessageBox("\"" + m_szSearchFor + "\" was not found", MB_OK | MB_ICONWARNING);
}

void CScanListCtrl::ZeroResultsForItem(int nItemIndex)
{
	for (int i=CL_PING; i < GetColumnCount(); i++)
	{
		SetItem(nItemIndex, i, LVIF_TEXT, "", 0, 0, 0, 0);
	}

	SetItem(nItemIndex, CL_IP, LVIF_IMAGE, NULL, 2, 0, 0, 0);	

	RedrawWindow();
}

DWORD CScanListCtrl::GetNumericIP(int nItemIndex)
{
	char szIP[16];
	GetItemText(nItemIndex, CL_IP, (char*) &szIP, sizeof(szIP));
	return inet_addr((char*)&szIP);
}


int CScanListCtrl::GetColumnCount()
{
	return GetHeaderCtrl()->GetItemCount();
}
