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

TScanFunction ScanIntDoHostname;	
TInfoFunction ScanIntInfoHostname;

TInitFunction ScanIntInitNetBIOS;
TFinalizeFunction ScanIntFinalizeNetBIOS;
TSetupFunction ScanIntSetupNetBIOS;

TScanFunction ScanIntDoNetBIOSComputerName;
TInfoFunction ScanIntInfoNetBIOSComputerName;

TScanFunction ScanIntDoNetBIOSGroupName;
TInfoFunction ScanIntInfoNetBIOSGroupName;

TScanFunction ScanIntDoNetBIOSUserName;
TInfoFunction ScanIntInfoNetBIOSUserName;

TScanFunction ScanIntDoNetBIOSMacAddress;
TInfoFunction ScanIntInfoNetBIOSMacAddress;


#endif // !defined(AFX_SCANUTILSINTERNAL_H__5E681616_ED94_40B6_9DE0_29332AC01E60__INCLUDED_)
