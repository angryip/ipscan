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


// RandomIPFeed.h: interface for the CRandomIPFeed class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_RandomIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_)
#define AFX_RandomIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "AbstractIPFeed.h"

class CRandomIPFeed : public CAbstractIPFeed  
{
public:
	// Constructor and destructor
	CRandomIPFeed(IPAddress nIPMask, int nIPs);
	virtual ~CRandomIPFeed();

protected:
	IPAddress m_nIPOriginalMask;
	IPAddress m_nIPBitMask;
	IPAddress m_nIPBitAddition;

	IPAddress m_nCurrentIP;
	int m_nIPs;
	int m_nIPCounter;

public:	
	virtual void startFeeding();

	virtual void finishFeeding();

	virtual BOOL isNextIPAvailable();

	virtual int getPercentComplete();

	virtual IPAddress getNextIP();

	virtual CString getScanSummary();

};

#endif // !defined(AFX_RandomIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_)
