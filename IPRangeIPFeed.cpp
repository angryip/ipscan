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


// IPRangeIPFeed.cpp: implementation of the CIPRangeIPFeed class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "AbstractIPFeed.h"
#include "IPRangeIPFeed.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CIPRangeIPFeed::CIPRangeIPFeed(IPAddress nStartIP, IPAddress nEndIP)
{
	m_nStartIP = nStartIP;
	m_nEndIP = nEndIP;	
}

CIPRangeIPFeed::~CIPRangeIPFeed()
{

}

void CIPRangeIPFeed::startFeeding()
{
	// Initialize current IP
	m_nCurrentIP = m_nStartIP;
}

void CIPRangeIPFeed::finishFeeding()
{
	// no finishing necessary
}

BOOL CIPRangeIPFeed::isNextIPAvailable()
{
	return m_nCurrentIP <= m_nEndIP;
}

int CIPRangeIPFeed::getPercentComplete()
{	
	return (m_nCurrentIP - m_nStartIP) * 100L / (m_nEndIP - m_nStartIP + 1);
}

IPAddress CIPRangeIPFeed::getNextIP()
{
	// Return current IP and then increase it
	m_nCurrentIP++;
	return m_nCurrentIP-1;
}

CString CIPRangeIPFeed::getScanSummary()
{
	CString szResult;

	// Convert IPs to strings
	char *ipp;

	in_addr in;
	in.S_un.S_addr = htonl(m_nStartIP);
	ipp = inet_ntoa(in);
	szResult += ipp;
		
	szResult += " - ";
	
	in.S_un.S_addr = htonl(m_nEndIP);
	ipp = inet_ntoa(in);
	szResult += ipp;

	return szResult;
}

CString CIPRangeIPFeed::serialize()
{
	CString szResult;

	szResult.Format("%u|%u", m_nStartIP, m_nEndIP);

	return szResult;
}
	
BOOL CIPRangeIPFeed::unserialize(CString& szSettings)
{
	return FALSE;
}
