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

#ifndef SCANLISTHEADERCTRL_H
#define SCANLISTHEADERCTRL_H

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CScanListHeaderCtrl : public CHeaderCtrl
{
// Construction
public:
	CScanListHeaderCtrl();

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(ScanListHeaderCtrl)
	public:
	//}}AFX_VIRTUAL

// Implementation
public:
	int GetSortedColumn();
	BOOL IsSortingAscending();
	void SetSortingAllowed(BOOL bAllowed);
	virtual ~CScanListHeaderCtrl();

	void SetSortArrow( const int nColumn, const BOOL bAscending );
	void OnItemClickListHeader(NMHDR* pNMHDR, LRESULT* pResult);

	// Generated message map functions
protected:		
	int m_nSortColumn;
	BOOL m_bSortAscending;

	//{{AFX_MSG(ScanListHeaderCtrl)
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()

	virtual void DrawItem( LPDRAWITEMSTRUCT lpDrawItemStruct );	
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // SORTHEADERCTRL_H
