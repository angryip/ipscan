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

	virtual void startFeeding() = 0;

	virtual void finishFeeding() = 0;

	virtual BOOL isNextIPAvailable() = 0;

	virtual int getPercentComplete() = 0;

	virtual IPAddress getNextIP() = 0;

	virtual CString getScanSummary() = 0;

};

//
//
//
//
//
//
//
//stuff about favorites, command line should also be here!!!
//
//
//
//

#endif // !defined(AFX_ABSTRACTIPFEED_H__244405A1_8724_41F4_BC26_89EE68A4DB22__INCLUDED_)
