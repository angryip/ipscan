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

// ScanUtilsInternal.h: interface for the CScanUtilsInternal class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANUTILSINTERNAL_H__5E681616_ED94_40B6_9DE0_29332AC01E60__INCLUDED_)
#define AFX_SCANUTILSINTERNAL_H__5E681616_ED94_40B6_9DE0_29332AC01E60__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Scanner.h"

// Define functions out of class

TScanFunction ScanIntDoDummy;	
TInitFunction ScanIntInitDummy;
TInfoFunction ScanIntInfoDummy;

TScanFunction ScanIntDoPing;	
TInitFunction ScanIntInitPing;
TInfoFunction ScanIntInfoPing;

TScanFunction ScanIntDoTTL;	
TInfoFunction ScanIntInfoTTL;

TScanFunction ScanIntDoHostname;	
TInfoFunction ScanIntInfoHostname;

TInitFunction ScanIntInitNetBIOS;
TFinalizeFunction ScanIntFinalizeNetBIOS;
TOptionsFunction ScanIntSetupNetBIOS;

TScanFunction ScanIntDoNetBIOSComputerName;
TInfoFunction ScanIntInfoNetBIOSComputerName;

TScanFunction ScanIntDoNetBIOSGroupName;
TInfoFunction ScanIntInfoNetBIOSGroupName;

TScanFunction ScanIntDoNetBIOSUserName;
TInfoFunction ScanIntInfoNetBIOSUserName;

TScanFunction ScanIntDoNetBIOSMacAddress;
TInfoFunction ScanIntInfoNetBIOSMacAddress;


#endif // !defined(AFX_SCANUTILSINTERNAL_H__5E681616_ED94_40B6_9DE0_29332AC01E60__INCLUDED_)
