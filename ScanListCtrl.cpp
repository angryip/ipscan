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
	m_bHeaderClicksDisabled = FALSE;
}

CScanListCtrl::~CScanListCtrl()
{
	
}

// This method is called in the OnInitDialog of the main window
void CScanListCtrl::InitPostCreateStuff()
{
	// Create image list for the listbox
	m_imglist.Create(IDB_IMAGELIST, 16, 2, 0xFFFFFF);		

	// Add image list to the listbox (in case is not added yet)
	SetImageList(&m_imglist, LVSIL_SMALL);	
}

BEGIN_MESSAGE_MAP(CScanListCtrl, CListCtrl)
	//{{AFX_MSG_MAP(CScanListCtrl)	
	ON_WM_LBUTTONDBLCLK()
	ON_WM_PAINT()
	ON_WM_SHOWWINDOW()
	//}}AFX_MSG_MAP
	ON_MESSAGE(WM_MEASUREITEM, MeasureItem)
	ON_WM_MEASUREITEM_REFLECT()
	ON_NOTIFY_REFLECT(LVN_COLUMNCLICK, OnItemClickListHeader)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CScanListCtrl message handlers

void CScanListCtrl::PreSubclassWindow() 
{
	// the list control must have the report style.
	ASSERT(GetStyle() & LVS_REPORT);

	CListCtrl::PreSubclassWindow();
	VERIFY(m_ctlHeader.SubclassWindow(GetHeaderCtrl()->GetSafeHwnd()));
	
	CListCtrl::PreSubclassWindow();
}

// This method draws a single item in the list
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
	int offset = pDC->GetTextExtent(_T(" "), 1 ).cx * 2;

	CRect rcHighlight;
	CRect rcWnd;	
	
	rcHighlight = rcBounds;
	rcHighlight.left = rcLabel.left;
	
	// Draw the background color
	DWORD nColor;

	if( bHighlight )
	{		
		pDC->SetBkColor(::GetSysColor(COLOR_HIGHLIGHT));

		nColor = ::GetSysColor(COLOR_HIGHLIGHT);		
	}
	else
	{
		nColor = ::GetSysColor(COLOR_WINDOW);		

		if (nItem & 1 == 1) // Even
		{
			nColor -= 0x200000;		// subtract 0x20 from Blue byte to make it more "yellow"
		}				
	}

	pDC->FillRect(rcHighlight, &CBrush(nColor));

	// Draw port scan results below other results for each item
	if (m_bShowPorts)
	{
		pDC->SetTextColor(::GetSysColor(COLOR_GRAYTEXT));
		rcCol = rcHighlight;
		rcCol.bottom -= 2;	rcCol.left = rcLabel.right;
		
		CString szOpenPorts;
		
		GetOpenPorts(nItem, szOpenPorts);

		if (szOpenPorts.GetLength() == 0)
			szOpenPorts = "  Open ports: ???";
		else
			szOpenPorts = "  Open ports: " + szOpenPorts;		

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
	int nCol = g_scanner->getColumnCount();	// Last column index

	if (g_options->m_bScanPorts)
	{
		if (GetColumnCount() == g_scanner->getColumnCount())
		{
			// Insert a special column
			InsertColumn(nCol, "Open ports");

			// Set saved width for the column			
			SetColumnWidth(nCol, AfxGetApp()->GetProfileInt("", "Col_!OP!", 8));
		}
	}
	else
	{
		if (GetColumnCount() > nCol)
		{
			// Remember the width
			AfxGetApp()->WriteProfileInt("", "Col_!OP!", GetColumnWidth(nCol));
			
			// Now remove
			DeleteColumn(nCol);	// Delete the special column
		}
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
	// Reset sorting arrows
	m_ctlHeader.SetSortArrow(-1, TRUE);
	// Clear items
	return CListCtrl::DeleteAllItems();
}

void CScanListCtrl::PrepareForScanning()
{
	// Some initialization before scanning

	if (g_options->m_bScanPorts)
		m_nPortsColumn = g_scanner->getColumnCount();	// the last column
	else
		m_nPortsColumn = -1;
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

	if (!CNetBIOSUtils::GetNames((char*)&ipstr, &szUserName, &szComputerName, &szGroupName, &szMacAddress))
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
	int nColumns = g_scanner->getColumnCount();
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
		cDlg.setPorts(szOpenPorts);

		// Add ports to the main window as well
		szInfoLine = "Open ports:\t";
		szInfoLine += szOpenPorts;
		cDlg.addScannedInfo(szInfoLine);
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
	
	for (i++; i < GetItemCount(); i++) 
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
	
	for (i++; i < GetItemCount(); i++) 
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
	if (!g_options->m_bScanPorts)
	{
		MessageBox("Ports were not scanned", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (i++; i < GetItemCount(); i++) 
	{
		CString szPorts = GetItemText(i, m_nPortsColumn);
		if (szPorts.GetLength() == 0 || szPorts.GetAt(0) != 'N')		// "N/A" or "N/S"
		{
			SetSelectedItem(i);
			break;
		}
	}
}

void CScanListCtrl::GoToNextClosedPortIP()
{
	if (!g_options->m_bScanPorts)
	{
		MessageBox("Ports were not scanned", NULL, MB_OK | MB_ICONHAND);
		return;
	}

	SetFocus();	
	int i = GetCurrentSelectedItem();
	if (i == -1)
		return;
	
	for (i++; i < GetItemCount(); i++) 
	{
		if (!(GetItemText(i, CL_PING) == "Dead"))
		{
			CString szPorts = GetItemText(i, m_nPortsColumn);
			if (szPorts.GetLength() == 0 || szPorts.GetAt(0) == 'N')		// Alive but closed port ("N/A" or "N/S")
			{
				SetSelectedItem(i);
				break;
			}
		}
	}
}

void CScanListCtrl::GoToNextSearchIP()
{
	CSearchDlg cSearchDlg;

	cSearchDlg.m_search = m_szSearchFor;
	cSearchDlg.m_case = m_bSearchCaseSensitive;
	
	if (cSearchDlg.DoModal()==IDCANCEL) 
		return;

	m_szSearchFor = cSearchDlg.m_search;
	m_bSearchCaseSensitive = cSearchDlg.m_case;

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

	if (m_bSearchCaseSensitive) 
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
	return m_ctlHeader.GetItemCount();
}

int CScanListCtrl::DeleteAllDeadHosts()
{
	int nDeleted = 0;
	int nItems = GetItemCount();

	SetRedraw(FALSE);

	for (int i=0; i < nItems; i++)
	{
		CString szText = GetItemText(i, CL_PING);
		
		if (szText.GetAt(0) == 'D')
		{
			DeleteItem(i);
			i--;	// Because number of items has decreased
			nItems--;
			nDeleted++;
		}
	}

	SetRedraw();

	return nDeleted;
}

int CScanListCtrl::DeleteAllAliveHosts()
{
	int nDeleted = 0;
	int nItems = GetItemCount();

	SetRedraw(FALSE);

	for (int i=0; i < nItems; i++)
	{
		CString szText = GetItemText(i, CL_PING);
		
		if (szText.GetAt(0) != 'D')
		{
			DeleteItem(i);
			i--;	// Because number of items has decreased
			nItems--;
			nDeleted++;
		}
	}

	SetRedraw(TRUE);

	return nDeleted;
}

int CScanListCtrl::DeleteAllClosedPortsHosts()
{
	if (!g_options->m_bScanPorts)
		return 0;

	int nDeleted = 0;
	int nItems = GetItemCount();
	int nPortsColumn = g_scanner->getColumnCount();	// The last column

	SetRedraw(FALSE);

	for (int i=0; i < nItems; i++)
	{
		CString szText = GetItemText(i, nPortsColumn);
		
		if (szText.GetLength() == 0 || szText.GetAt(0) == 'N')
		{
			DeleteItem(i);
			i--;	// Because number of items has decreased
			nItems--;
			nDeleted++;
		}
	}

	SetRedraw(TRUE);

	return nDeleted;
}

int CScanListCtrl::DeleteAllOpenPortsHosts()
{
	if (!g_options->m_bScanPorts)
		return 0;

	int nDeleted = 0;
	int nItems = GetItemCount();
	int nPortsColumn = g_scanner->getColumnCount();	// The last column

	SetRedraw(FALSE);

	for (int i=0; i < nItems; i++)
	{
		CString szText = GetItemText(i, nPortsColumn);
		
		if (szText.GetLength() > 0 && szText.GetAt(0) != 'N')
		{
			DeleteItem(i);
			i--;	// Because number of items has decreased
			nItems--;
			nDeleted++;
		}
	}

	SetRedraw(TRUE);

	return nDeleted;
}

void CScanListCtrl::DeleteSelectedItems()
{		
	int nItemIndex, nFirstItemIndex;
	POSITION pos;

	pos = GetFirstSelectedItemPosition();
	nFirstItemIndex = GetNextSelectedItem(pos);

	SetRedraw(FALSE);
	
	do 
	{
		pos = GetFirstSelectedItemPosition();
		nItemIndex = GetNextSelectedItem(pos);
		DeleteItem(nItemIndex);		
	}
	while (nItemIndex >= 0);

	SetSelectedItem(nFirstItemIndex);

	SetRedraw(TRUE);

	Invalidate();
	UpdateWindow();
}


void CScanListCtrl::OnPaint() 
{	
	// Call the default painter	
	Default();

	// Draw the notification in case the list is empty
	if (GetItemCount() <= 0)
	{
		COLORREF clrText = ::GetSysColor(COLOR_WINDOWTEXT);
		COLORREF clrTextBk = ::GetSysColor(COLOR_WINDOW);

		clrText = (clrText + clrTextBk) / 2;

		CDC* pDC = GetDC();
		// Save dc state
		int nSavedDC = pDC->SaveDC();

		CRect rc;
		GetWindowRect(&rc);
		ScreenToClient(&rc);

		int nAllItemsWidth = 0;		// The combined width of all the items will be stored here

		CRect rcH;

		for (int i = 0; i < m_ctlHeader.GetItemCount(); i++)
		{
			m_ctlHeader.GetItemRect(i, &rcH);
			nAllItemsWidth += rcH.Width();
		}

		rc.top += rcH.bottom + 10;		

		pDC->SetTextColor(clrText);
		pDC->SetBkColor(clrTextBk);
		pDC->FillRect(rc, &CBrush(clrTextBk));
		pDC->SelectStockObject(ANSI_VAR_FONT);

		// Let the text will be centered by headers, not the window
		if (nAllItemsWidth > 0)
		{
			rc.right = rc.left + nAllItemsWidth;
		}

		pDC->DrawText("The list is empty. Set the IP range and click Start to start scanning.", 
			-1, rc, DT_CENTER | DT_WORDBREAK | DT_NOPREFIX | DT_NOCLIP);

		// Restore dc
		pDC->RestoreDC(nSavedDC);
		ReleaseDC(pDC);
	}
	
	// Do not call CListCtrl::OnPaint() for painting messages
}

// Redefine this method to add owner draw capability to header items
int CScanListCtrl::InsertColumn(int nCol, LPCTSTR lpszColumnHeading, int nFormat, int nWidth, int nSubItem)
{
	return CListCtrl::InsertColumn(nCol, lpszColumnHeading, nFormat | HDF_OWNERDRAW, nWidth, nSubItem);
}

void CScanListCtrl::SetHeaderClicksDisabled(BOOL bHeaderClicksDisabled)
{
	m_bHeaderClicksDisabled = bHeaderClicksDisabled;

	if (!m_bHeaderClicksDisabled)
		m_ctlHeader.SetSortArrow(-1, 0);	// Disable showing of sorting arrows
}

int CALLBACK SortCompareFunc(LPARAM lParam1, LPARAM lParam2, LPARAM lParamSort) 
{
	CScanListCtrl *pList = (CScanListCtrl*) lParamSort;
	CScanListHeaderCtrl *pListHeader = (CScanListHeaderCtrl*) pList->GetHeaderCtrl();
	int nSortedColumn = pListHeader->GetSortedColumn();

	CString strItem1 = pList->GetItemText(lParam1, nSortedColumn);
	CString strItem2 = pList->GetItemText(lParam2, nSortedColumn);   

	int nRet, n1, n2;

	switch (nSortedColumn) 
	{
		case CL_IP:
			n1 = ntohl(inet_addr(strItem1));
			n2 = ntohl(inet_addr(strItem2));
			if (n1 > n2) nRet = 1; else if (n1 < n2) nRet = -1; else nRet = 0;
			break;
		case CL_PING:
			if (strItem1.GetAt(0) == 'D')	// Dead
				nRet = 1;
			else 
			if (strItem2.GetAt(0) == 'D') // Dead
				nRet = -1;
			else
			{
				n1 = atoi(strItem1);
				n2 = atoi(strItem2);
				if (n1 > n2) nRet = 1; else if (n1 < n2) nRet = -1; else nRet = 0;
			}
			break;
		default:

			if (strItem1 == "N/A" || strItem1 == "N/S")
				strItem1 = "\xFF";	// Move it to the end

			if (strItem2 == "N/A" || strItem2 == "N/S")
				strItem2 = "\xFF";  // Move it to the end

			nRet = strItem1.CompareNoCase(strItem2);
			break;
	}
	
	return nRet * (pListHeader->IsSortingAscending() ? 1 : -1);
}

void CScanListCtrl::OnItemClickListHeader(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NMLISTVIEW *phdn = (NMLISTVIEW *) pNMHDR;	 		

	if (m_bHeaderClicksDisabled)
		return;		// Do not show anything during scanning

	// Get the current on-screen mouse position
	POINT mousePos;
	GetCursorPos(&mousePos);

	// Translate mouse pos to the header pos
	RECT rcWin;
	m_ctlHeader.GetWindowRect(&rcWin);
	mousePos.x -= rcWin.left;	// Only horizontal coordinate is interesting for us

	// Get the rect of clicked item
	m_ctlHeader.GetItemRect(phdn->iSubItem, &rcWin);	

	// Detect which icon was clicked
	if (rcWin.right - 28 < mousePos.x)
	{
		// Icon area

		if (rcWin.right - 15 > mousePos.x)
		{
			// Options icon			
			g_scanner->showColumnOptions(phdn->iSubItem);
		}
		else
		{
			// Info icon
			g_scanner->showColumnInfo(phdn->iSubItem);			
		}

		return;	// Do not proceed with sorting stuff
	}	

	// Quit there is no items to sort
	if (GetItemCount() <= 0)
	{
		m_ctlHeader.SetSortArrow(-1, 0);	// Ensure that this is switched off
		return;
	}

	BOOL bSortAscending = m_ctlHeader.IsSortingAscending();
	int nSortedColumn = m_ctlHeader.GetSortedColumn();
	
	if(phdn->iSubItem == nSortedColumn)
	    bSortAscending = !bSortAscending;
    else
        bSortAscending = TRUE;

    nSortedColumn = phdn->iSubItem;

    for (int i=0;i < GetItemCount();i++) 
	{
		SetItemData(i, i);
	}		

	m_ctlHeader.SetSortArrow(nSortedColumn, bSortAscending);

	SortItems(&SortCompareFunc, (DWORD) this);
		
	*pResult = 0;
}


