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


// IPRangeIPFeed.h: interface for the CIPRangeIPFeed class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IPRANGEIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_)
#define AFX_IPRANGEIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "AbstractIPFeed.h"

class CIPRangeIPFeed : public CAbstractIPFeed  
{
public:
	// Constructor and destructor
	CIPRangeIPFeed(IPAddress nStartIP, IPAddress nEndIP);
	virtual ~CIPRangeIPFeed();

protected:
	IPAddress m_nEndIP;
	IPAddress m_nStartIP;
	IPAddress m_nCurrentIP;

public:	
	virtual void startFeeding();

	virtual void finishFeeding();

	virtual BOOL isNextIPAvailable();

	virtual int getPercentComplete();

	virtual IPAddress getNextIP();

	virtual CString getScanSummary();

	virtual CString serialize();
	
	virtual BOOL unserialize(CString& szSettings);
};

#endif // !defined(AFX_IPRANGEIPFEED_H__407A3998_D78B_412C_9BB3_CC54DEE737CD__INCLUDED_)
