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

// ipscan.h : main header file for the IPSCAN application
//

//#/define DEBUG_MESSAGES

#if !defined(AFX_IPSCAN_H__F45ACDE4_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
#define AFX_IPSCAN_H__F45ACDE4_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CIpscanApp:
// See ipscan.cpp for the implementation of this class
//


class CIpscanApp : public CWinApp
{
public:
	CIpscanApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIpscanApp)
	public:
	virtual BOOL InitInstance();
	virtual int Run();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CIpscanApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPSCAN_H__F45ACDE4_D4E4_11D3_83C7_9932CC7AF305__INCLUDED_)
