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

// IPFeedDlgFactory.h: interface for the CIPFeedDlgFactory class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IPFEEDDLGFACTORY_H__08CA8002_A70F_43E2_B871_3177FAFAF9A1__INCLUDED_)
#define AFX_IPFEEDDLGFACTORY_H__08CA8002_A70F_43E2_B871_3177FAFAF9A1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "AbstractIPFeedDlg.h"

class CIPFeedDlgFactory  
{
protected:
	CAbstractIPFeedDlg * m_paIPFeeds[10];
	CString m_szIPFeedNames[10];
	int m_nIPFeeds;

public:
	CIPFeedDlgFactory();
	virtual ~CIPFeedDlgFactory();

};

#endif // !defined(AFX_IPFEEDDLGFACTORY_H__08CA8002_A70F_43E2_B871_3177FAFAF9A1__INCLUDED_)
