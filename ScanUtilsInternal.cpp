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

int nNumAlive = 0;

// For NetBIOS

CNetBIOSUtils *g_NetBIOSUtils = NULL;

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
			MessageBox(0, szTmp,"Error",MB_OK | MB_ICONHAND);
			exit(666);
		}

		lpfnIcmpCreateFile  = (FARPROC)GetProcAddress(hICMP,"IcmpCreateFile");
		lpfnIcmpCloseHandle = (TIcmpCloseHandle)GetProcAddress(hICMP,"IcmpCloseHandle");
		lpfnIcmpSendEcho    = (TIcmpSendEcho)GetProcAddress(hICMP,"IcmpSendEcho");

		// Fill data buffer for pinging
		for (int i=0; i < sizeof(aPingDataBuf); i++) aPingDataBuf[i]=i+65;
	}

	nNumAlive = 0;

	return TRUE;
}

BOOL ScanIntDoPing(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	BOOL bAlive = false;

	HANDLE hICMP = (HANDLE) lpfnIcmpCreateFile();

	unsigned char RepData[sizeof(ICMPECHO)+100];
	IPINFO IPInfo;
	IPInfo.Ttl = 64;
    IPInfo.Tos = 0;
    IPInfo.Flags = 0;
    IPInfo.OptionsSize = 0;
    IPInfo.OptionsData = NULL;
	DWORD ReplyCount;
	ReplyCount = lpfnIcmpSendEcho(hICMP, nIP, &aPingDataBuf, sizeof(aPingDataBuf), 
		&IPInfo, RepData, sizeof(RepData), 1000/*TODO!!! Timeout*/);

	lpfnIcmpCloseHandle(hICMP);

	if (ReplyCount) 	
	{
		ReplyCount = RepData[4]+RepData[5]*256+RepData[6]*65536+RepData[7]*256*65536;
		if (ReplyCount <= 0) 
		{
			bAlive = true;
			nNumAlive++;
		}
	}

	if (nBufferLen > 10)
	{
		if (bAlive)
		{
			sprintf(szReturn,"%d ms",*(u_long *) &(RepData[8]));
			bAlive = TRUE;
		}
		else
		{
			strcpy(szReturn, "Dead");
		}
	}

	return bAlive;

}

BOOL ScanIntInfoPing(TInfoStruct *pInfoStruct)
{
	strcpy((char*)&pInfoStruct->szColumnName, "Ping");
	strcpy((char*)&pInfoStruct->szPluginName, "Ping");
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
		if (nBufferLen > 0 && strlen(he->h_name) > nBufferLen)
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
	strcpy(pInfoStruct->szColumnName, "Hostname");
	strcpy(pInfoStruct->szPluginName, "Resolve hostname");
	return TRUE;
}


/////////////////////////////////////////////////////////////////////////////////
// NETBIOS
/////////////////////////////////////////////////////////////////////////////////

// Init / Finalization

BOOL ScanIntInitNetBIOS()
{
	if (g_NetBIOSUtils == NULL)
		g_NetBIOSUtils = new CNetBIOSUtils();
	return TRUE;
}

BOOL ScanIntFinalizeNetBIOS()
{
	if (g_NetBIOSUtils != NULL)
	{
		delete g_NetBIOSUtils;
		g_NetBIOSUtils = NULL;
	}
	return TRUE;
}

// Computer Name

BOOL ScanIntDoNetBIOSComputerName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szComputerName;
	g_NetBIOSUtils->setIP(nIP);
	g_NetBIOSUtils->GetNames(NULL, &szComputerName, NULL, NULL);
	if (szComputerName.GetLength() > nBufferLen)
		szComputerName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szComputerName);
	return TRUE;
}

BOOL ScanIntInfoNetBIOSComputerName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szColumnName, "Comp. Name");
	strcpy(pInfoStruct->szPluginName, "NetBIOS Computer Name");
	return TRUE;	
}

// Group Name

BOOL ScanIntDoNetBIOSGroupName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szGroupName;
	g_NetBIOSUtils->setIP(nIP);
	g_NetBIOSUtils->GetNames(NULL, NULL, &szGroupName, NULL);
	if (szGroupName.GetLength() > nBufferLen)
		szGroupName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szGroupName);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSGroupName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szColumnName, "Group Name");
	strcpy(pInfoStruct->szPluginName, "NetBIOS Group Name");
	return TRUE;		
}

// User Name

BOOL ScanIntDoNetBIOSUserName(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szUserName;
	g_NetBIOSUtils->setIP(nIP);
	g_NetBIOSUtils->GetNames(&szUserName, NULL, NULL, NULL);
	if (szUserName.GetLength() > nBufferLen)
		szUserName.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szUserName);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSUserName(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szColumnName, "User Name");
	strcpy(pInfoStruct->szPluginName, "NetBIOS User Name");
	return TRUE;		
}

// Mac Address

BOOL ScanIntDoNetBIOSMacAddress(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	CString szMacAddress;
	g_NetBIOSUtils->setIP(nIP);
	g_NetBIOSUtils->GetNames(NULL, NULL, NULL, &szMacAddress);
	if (szMacAddress.GetLength() > nBufferLen)
		szMacAddress.SetAt(nBufferLen - 1, 0);
	strcpy(szReturn, szMacAddress);
	return TRUE;	
}

BOOL ScanIntInfoNetBIOSMacAddress(TInfoStruct *pInfoStruct)
{
	strcpy(pInfoStruct->szColumnName, "Mac Address");
	strcpy(pInfoStruct->szPluginName, "Mac Address");
	return TRUE;		
}


