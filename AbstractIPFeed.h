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


// AbstractIPFeed.h: interface for the CAbstractIPFeed class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ABSTRACTIPFEED_H__244405A1_8724_41F4_BC26_89EE68A4DB22__INCLUDED_)
#define AFX_ABSTRACTIPFEED_H__244405A1_8724_41F4_BC26_89EE68A4DB22__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

typedef DWORD IPAddress;

class CAbstractIPFeed  
{
public:
	CAbstractIPFeed();
	virtual ~CAbstractIPFeed();	

	// Start feeding of IP addresses
	virtual void startFeeding() = 0;

	// Finish feeding of IP addresses
	virtual void finishFeeding() = 0;

	// Whether we should continue scanning
	virtual BOOL isNextIPAvailable() = 0;

	// Returns status for a progress bar (from 0 to 100)
	virtual int getPercentComplete() = 0;

	// Returns the next IP address
	virtual IPAddress getNextIP() = 0;

	// Returns the IP feed settings short human-readable summary
	virtual CString getScanSummary() = 0;

};

//
//
//
//
//
//
//
//stuff command line should also be here!!!
//
//
//
//

#endif // !defined(AFX_ABSTRACTIPFEED_H__244405A1_8724_41F4_BC26_89EE68A4DB22__INCLUDED_)
