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

// IPFeedDlgFactory.cpp: implementation of the CIPFeedDlgFactory class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "IPFeedDlgFactory.h"
#include "IPRangeDlg.h"
#include "RandomIPFeedDlg.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CIPFeedDlgFactory::CIPFeedDlgFactory()
{
	// Specify number of feeders
	m_nIPFeeds = 2;
	
	// Create all possible feeders (currently they are all built-in)
	m_paIPFeeds[0] = new CIPRangeDlg;
	ASSERT(m_paIPFeeds[0]->Create(CIPRangeDlg::IDD));
	m_szIPFeedNames[0] = "IP Range";

	m_paIPFeeds[1] = new CRandomIPFeedDlg;
	ASSERT(m_paIPFeeds[1]->Create(CRandomIPFeedDlg::IDD));
	m_szIPFeedNames[1] = "Random IPs";
}

CIPFeedDlgFactory::~CIPFeedDlgFactory()
{
	for (int i = 0; i < m_nIPFeeds; i++)
	{
		delete m_paIPFeeds[i];
	}
}
