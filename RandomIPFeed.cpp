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


// RandomIPFeed.cpp: implementation of the CRandomIPFeed class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include <stdlib.h>
#include <time.h>
#include "AbstractIPFeed.h"
#include "RandomIPFeed.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CRandomIPFeed::CRandomIPFeed(IPAddress nIPMask, int nIPs)
{
	// Remember the mask
	m_nIPOriginalMask = nIPMask;
	
	// "Parse" the mask (bytewise)
	m_nIPBitAddition = 0;
	m_nIPBitMask = 0;
	
	// Note: 32bit IPv4 is hardcoded here
	for (DWORD nCurBitMask = 0xFF; nCurBitMask; nCurBitMask <<= 8)
	{
		if (m_nIPOriginalMask & nCurBitMask)
		{			
			m_nIPBitAddition |= m_nIPOriginalMask & nCurBitMask;
		}
		else
		{
			m_nIPBitMask |= nCurBitMask;
		}
	}

	// Remember number of runs
	m_nIPs = nIPs;
}

CRandomIPFeed::~CRandomIPFeed()
{

}

void CRandomIPFeed::startFeeding()
{
	// Initialize IP counter
	m_nIPCounter = 0;

	// Init random seed
	srand(time(NULL));
}

void CRandomIPFeed::finishFeeding()
{
	// no finishing necessary
}

BOOL CRandomIPFeed::isNextIPAvailable()
{
	return m_nIPCounter < m_nIPs;
}

int CRandomIPFeed::getPercentComplete()
{	
	return m_nIPCounter * 100L / m_nIPs;
}

IPAddress CRandomIPFeed::getNextIP()
{
	// Generate new random IP byte-wise
	// Note: this is hardcoded for 32bit IPv4
	int nRnd1 = (rand() * 0xFF / RAND_MAX);
	int nRnd2 = (rand() * 0xFF / RAND_MAX);
	int nRnd3 = (rand() * 0xFF / RAND_MAX);
	int nRnd4 = (rand() * 0xFF / RAND_MAX);
	m_nCurrentIP = nRnd1 + nRnd2 * 0x100 + nRnd3 * 0x10000 + nRnd4 * 0x1000000;

	// Now mask the IP	
	m_nCurrentIP = m_nCurrentIP & m_nIPBitMask | m_nIPBitAddition;

	m_nIPCounter++;

	return m_nCurrentIP;
}

CString CRandomIPFeed::getScanSummary()
{
	CString szResult;

	// Output something like: Rnd 100x (192.0.0.0)

	char *ipp;	

	in_addr in;
	in.S_un.S_addr = htonl(m_nIPOriginalMask);
	ipp = inet_ntoa(in);
	
	szResult.Format("Rnd %dx (%s)", m_nIPs, ipp);

	return szResult;
}

