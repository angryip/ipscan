// NetBIOSUtils.cpp: implementation of the CNetBIOSUtils class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "NetBIOSUtils.h"
#include <nb30.h>

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

#define CONST_OWN_NETBIOS_NAME "ANGRYIPSCAN"

CNetBIOSUtils::CNetBIOSUtils(CString szIPAddress)
{
	m_szIP = szIPAddress;
	GetLanaNumber();
	Reset(m_nLana, 20, 30);
	AddName(m_nLana, CONST_OWN_NETBIOS_NAME);
}

CNetBIOSUtils::~CNetBIOSUtils()
{
	DeleteName(m_nLana, CONST_OWN_NETBIOS_NAME);
}

void CNetBIOSUtils::MakeName(char *achDest, LPCSTR szSrc)
{
	int cchSrc;

    cchSrc = lstrlen (szSrc);
    if (cchSrc > NCBNAMSZ)
        cchSrc = NCBNAMSZ;

    memset (achDest, ' ', NCBNAMSZ);
    memcpy (achDest, szSrc, cchSrc);
}

BOOL CNetBIOSUtils::Reset(int nLana, int nSessions, int nNames)
{
	NCB ncb;

    memset (&ncb, 0, sizeof (ncb));
    ncb.ncb_command = NCBRESET;
    ncb.ncb_lsn = 0;                // Allocate new lana_num resources 
    ncb.ncb_lana_num = nLana;
    ncb.ncb_callname[0] = nSessions;  // maximum sessions 
    ncb.ncb_callname[2] = nNames;   // maximum names 

    Netbios (&ncb);    

    return (NRC_GOODRET == ncb.ncb_retcode);
	
}

BOOL CNetBIOSUtils::AddName(int nLana, LPCSTR szName)
{

    NCB ncb;

    memset (&ncb, 0, sizeof (ncb));
    ncb.ncb_command = NCBADDNAME;
    ncb.ncb_lana_num = nLana;

    MakeName ((char*)ncb.ncb_name, szName);

    Netbios (&ncb);    

    return (NRC_GOODRET == ncb.ncb_retcode);
}


BOOL CNetBIOSUtils::AdapterStatus(int nLana, PVOID pBuffer, int cbBuffer, LPCSTR szName)
{
	NCB ncb;

    memset (&ncb, 0, sizeof (ncb));
    ncb.ncb_command = NCBASTAT;
    ncb.ncb_lana_num = nLana;

    ncb.ncb_buffer = (PUCHAR) pBuffer;
    ncb.ncb_length = cbBuffer;

    MakeName((char*)&ncb.ncb_callname, szName);

    Netbios (&ncb);    

    return (NRC_GOODRET == ncb.ncb_retcode);
}

BOOL CNetBIOSUtils::DeleteName(int nLana, LPCSTR szName)
{
    NCB ncb;

    memset (&ncb, 0, sizeof (ncb));
    ncb.ncb_command = NCBDELNAME;
    ncb.ncb_lana_num = nLana;

    MakeName ((char*)ncb.ncb_name, szName);

    Netbios (&ncb);

    return (NRC_GOODRET == ncb.ncb_retcode);
}

void CNetBIOSUtils::GetLanaNumber()
{
	LANA_ENUM lan_num;
	NCB ncb;

	memset(&ncb, 0, sizeof(ncb));
	ncb.ncb_command =  NCBENUM;
	ncb.ncb_buffer = (unsigned char *) &lan_num; 
	ncb.ncb_length = sizeof(lan_num);

	Netbios((NCB*) &ncb);

	m_nLana = lan_num.lana[0];
}

BOOL CNetBIOSUtils::GetNames(CString &szUserName, CString &szComputerName, CString &szGroupName, CString &szMacAddress)
{	
    int cbBuffer;
    ADAPTER_STATUS *pStatus;
    NAME_BUFFER *pNames;
    int i;
    HANDLE hHeap;

    hHeap = GetProcessHeap();

    // Allocate the largest buffer that might be needed. 
    cbBuffer = sizeof (ADAPTER_STATUS) + 255 * sizeof (NAME_BUFFER);
    pStatus = (ADAPTER_STATUS *) HeapAlloc (hHeap, 0, cbBuffer);
    if (NULL == pStatus)
        return FALSE;

    if (!AdapterStatus (m_nLana, (PVOID) pStatus, cbBuffer, m_szIP))
    {
        HeapFree (hHeap, 0, pStatus);
        return FALSE;
    }

    // The list of names follows the adapter status structure.
    pNames = (NAME_BUFFER *) (pStatus + 1);
	char szName[16];

	// get computer name
	memcpy(&szName, pNames[0].name, 15); szName[15] = 0;
	szComputerName = szName;

	// get group name
    for (i = 0; i < pStatus->name_count; i++)
	{
		if ((pNames[i].name_flags & GROUP_NAME) == GROUP_NAME)
		{
			memcpy(&szName, pNames[i].name, 15); szName[15] = 0;
			szGroupName = szName;
			break;
		}
	}

	// get user name
	for (i = pStatus->name_count-1; i >= 0; i--)
	{
		if (pNames[i].name[15] == 3)
		{
			memcpy(&szName, pNames[i].name, 15); szName[15] = 0;
			szUserName = szName;
			break;
		}
	}
	szUserName.Delete(szUserName.Find("$"));

	// get mac address
   	szMacAddress.Format(_T("%02X %02X %02X %02X %02X %02X"),
		pStatus->adapter_address[0],
		pStatus->adapter_address[1],
		pStatus->adapter_address[2],
		pStatus->adapter_address[3],
		pStatus->adapter_address[4],
		pStatus->adapter_address[5]);

    HeapFree (hHeap, 0, pStatus);

    return TRUE;

}

