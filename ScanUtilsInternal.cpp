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

// ScanUtilsInternal.cpp: implementation of the CScanUtilsInternal class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "ScanUtilsInternal.h"
#include "Scanner.h"
#include "ms_icmp.h"
#include "NetBIOSUtils.h"


#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

// For pinging

FARPROC lpfnIcmpCreateFile;
typedef BOOL (FAR WINAPI *TIcmpCloseHandle)(HANDLE IcmpHandle);
TIcmpCloseHandle lpfnIcmpCloseHandle;
typedef DWORD (FAR WINAPI *TIcmpSendEcho)(
	HANDLE IcmpHandle, 	/* handle returned from IcmpCreateFile() */
    u_long DestAddress, /* destination IP address (in network order) */
    LPVOID RequestData, /* pointer to buffer to send */
    WORD RequestSize,	/* length of data in buffer */
    LPIPINFO RequestOptns,  /* see Note 2 */
    LPVOID ReplyBuffer, /* see Note 1 */
    DWORD ReplySize, 	/* length of reply (must allow at least 1 reply) */
    DWORD Timeout 	/* time in milliseconds to wait for reply */
);
TIcmpSendEcho lpfnIcmpSendEcho;

char aPingDataBuf[32];

//////////////////////////////////////////////////////////////////////////////////
// PING
//////////////////////////////////////////////////////////////////////////////////

BOOL ScanIntInitPing()
{
	if (!lpfnIcmpCloseHandle) // if not already done
	{
		HMODULE hICMP = LoadLibrary("ICMP.DLL");
		if (!hICMP) 
		{
			CString szTmp;
			szTmp.LoadString(IDS_SCAN_HOMEPAGE);
			szTmp = "ICMP.DLL is not found. Program will not work.\n"
		    		"You can find this DLL on Angry IP Scanner homepage: " + szTmp;
			MessageBox(0, szTmp,"Fatal Error",MB_OK | MB_ICONHAND);
			exit(666);
		}

		lpfnIcmpCreateFile  = (FARPROC)GetProcAddress(hICMP,"IcmpCreateFile");
		lpfnIcmpCloseHandle = (TIcmpCloseHandle)GetProcAddress(hICMP,"IcmpCloseHandle");
		lpfnIcmpSendEcho    = (TIcmpSendEcho)GetProcAddress(hICMP,"IcmpSendEcho");

		// Fill data buffer for pinging
		for (int i=0; i < sizeof(aPingDataBuf); i++) aPingDataBuf[i]=i+65;
	}	

	return TRUE;
}

// Return values:
//  -1: dead
// >=0: alive (ping time)
int ScanIntDoPing(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	int nAlive = -1;	// Dead

	int nPingTime = 1000000; // A kind of infinity

	HANDLE hICMP = (HANDLE) lpfnIcmpCreateFile();

	unsigned char RepData[sizeof(ICMPECHO)+100];

	// Ping a few times
	for (int nPingCount = 1; nPingCount <= g_options->m_nPingCount; nPingCount++)
	{
		IPINFO IPInfo;
		IPInfo.Ttl = 64;
		IPInfo.Tos = 0;
		IPInfo.Flags = 0;
		IPInfo.OptionsSize = 0;
		IPInfo.OptionsData = NULL;
		DWORD ReplyCount;
		ReplyCount = lpfnIcmpSendEcho(hICMP, nIP, &aPingDataBuf, sizeof(aPingDataBuf), 
			&IPInfo, RepData, sizeof(RepData), g_options->m_nPingTimeout);	

		if (ReplyCount) 	
		{
			ReplyCount = RepData[4]+RepData[5]*256+RepData[6]*65536+RepData[7]*256*65536;
			if (ReplyCount <= 0) 
			{					
				if (nPingTime < 1000000)	// 1000 secs, a kind of infinity
				{
					nPingTime = (nPingTime + *(u_long *) &(RepData[8])) / 2;	// Arithmetics average					
				}
				else
				{
					nPingTime = *(u_long *) &(RepData[8]);
				}
				
				nAlive = nPingTime;
			}
		}
	}

	if (nBufferLen > 10)	// Check to not overflow the string buffer
	{
		if (nAlive >= 0)
		{
			sprintf(szReturn,"%d ms", nPingTime);
		}
		else
		{
			strcpy(szReturn, "Dead");
		}
	}

	lpfnIcmpCloseHandle(hICMP);

	return nAlive;	// -1: dead, >=0: alive
}

BOOL ScanIntInfoPing(TInfoStruct *pInfoStruct)
{
	strcpy((char*)&pInfoStruct->szPluginName, "Ping");
	strcpy((char*)&pInfoStruct->szDescription, "Ping");
	return TRUE;
}

/////////////////////////////////////////////////////////////////////////////////
// DUMMY
/////////////////////////////////////////////////////////////////////////////////

BOOL ScanIntDoDummy(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	szReturn[0] = 0;
	return TRUE;
}

BOOL ScanIntInitDummy()
{
	return TRUE;
}

BOOL ScanIntInfoDummy(TInfoStruct *pInfoStruct)
{
	memset(pInfoStruct, 0, sizeof(TInfoStruct));
	return TRUE;
}

//////////////////////////////////////////////////////////////////////////////////
// HOSTNAME
//////////////////////////////////////////////////////////////////////////////////

BOOL ScanIntDoHostname(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	hostent *he = gethostbyaddr((char*)&nIP, 4, 0);
	if (he) 
	{
		if (nBufferLen > 0 && strlen(he->h_name) > (unsigned int) nBufferLen)
			he->h_name[nBufferLen-1] = 0;

		strcpy(szReturn, he->h_name);		
	} 
	else
	{
		strcpy(szReturn, "N/A");
	}
	return TRUE;
}

BOOL ScanIntInfoHostname(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szPluginName, "Hostname");
	strcpy(pInfoStruct->szDescription, "It is resolving hostnames of IP addresses using the DNS reverse lookup");
	return TRUE;
}


/////////////////////////////////////////////////////////////////////////////////
// NETBIOS
/////////////////////////////////////////////////////////////////////////////////

// Init / Finalization

// Computer Name

BOOL ScanIntDoNetBIOSComputerName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szComputerName;	
	CNetBIOSUtils::GetNames(nIP, NULL, &szComputerName, NULL, NULL);
	if (szComputerName.GetLength() > nBufferLen)
		szComputerName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szComputerName);
	return TRUE;
}

BOOL ScanIntInfoNetBIOSComputerName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szPluginName, "Comp. Name");
	strcpy(pInfoStruct->szDescription, "Gets the NetBIOS Computer Name (works mostly in LANs)");
	return TRUE;	
}

// Group Name

BOOL ScanIntDoNetBIOSGroupName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szGroupName;	
	CNetBIOSUtils::GetNames(nIP, NULL, NULL, &szGroupName, NULL);
	if (szGroupName.GetLength() > nBufferLen)
		szGroupName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szGroupName);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSGroupName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szPluginName, "Group Name");
	strcpy(pInfoStruct->szDescription, "Gets NetBIOS Group Name (works mostly in LANs)");
	return TRUE;		
}

// User Name

BOOL ScanIntDoNetBIOSUserName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szUserName;	
	CNetBIOSUtils::GetNames(nIP, &szUserName, NULL, NULL, NULL);
	if (szUserName.GetLength() > nBufferLen)
		szUserName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szUserName);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSUserName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szPluginName, "User Name");
	strcpy(pInfoStruct->szDescription, "Gets NetBIOS User Name (just a guess, works mostly in LANs)");
	return TRUE;		
}

// Mac Address

BOOL ScanIntDoNetBIOSMacAddress(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szMacAddress;	
	CNetBIOSUtils::GetNames(nIP, NULL, NULL, NULL, &szMacAddress);
	if (szMacAddress.GetLength() > nBufferLen)
		szMacAddress.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szMacAddress);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSMacAddress(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szPluginName, "MAC Address");
	strcpy(pInfoStruct->szDescription, "Gets Mac Address (works through NetBIOS and mostly in LANs)");
	return TRUE;		
}


