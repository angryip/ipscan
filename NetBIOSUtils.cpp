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

// NetBIOSUtils.cpp: implementation of the CNetBIOSUtils class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "NetBIOSUtils.h"
#include "NetBIOSOptions.h"
#include "Scanner.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

#define NETBIOS_REQUEST "\xa2\x48\x00\x00\x00\x01\x00\x00\x00\x00\x00\x00\x20\x43\x4b\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x41\x00\x00\x21\x00\x01"

#pragma pack(push, 1)
typedef struct _node_status_resp 
{
	unsigned short trn_id;
	unsigned short flags1;
	unsigned short flags2;
	unsigned short flags3;
	unsigned short flags4;
	unsigned short flags5;
	char rr_name[0x22];
	unsigned short nbstat;  /* 0x0021 */
	unsigned short in;  /* 0x0001 */
	unsigned long zilch; /* 0 */
	unsigned short rd_length;
	unsigned char num_names;
	struct {
		unsigned char nb_name[16];
		unsigned short name_flags;
	} name_array[1];
} NS_RESP;
#pragma pack(pop)

#define GROUP_NAME_FLAG		128
#define NAME_TYPE_DOMAIN	0x00
#define NAME_TYPE_MESSENGER	0x03


CNetBIOSUtils::CNetBIOSUtils()
{	
	// Nothings here now
}

CNetBIOSUtils::~CNetBIOSUtils()
{
	// Nothing's here
}

void CNetBIOSUtils::setIP(LPCSTR szIP)
{
	m_szIP = szIP;
}

void CNetBIOSUtils::setIP(DWORD nIP)
{
	in_addr in;
	in.S_un.S_addr = nIP;
	LPSTR ipa = inet_ntoa(in);	
	m_szIP = ipa;
}

BOOL CNetBIOSUtils::GetNames(CString *szUserName, CString *szComputerName, CString *szGroupName, CString *szMacAddress)
{	    
	char buf[1000];    

	UINT nRetrievedLen = RetrieveData((char*) &buf, sizeof(buf));

	if (nRetrievedLen == 0)
		return FALSE;

	CString szTemp;

    NS_RESP *data = (NS_RESP *) &buf;    

    if (nRetrievedLen < sizeof(*data)) 
	{
		// response too small
        return FALSE;
    }
    
    if (nRetrievedLen < (sizeof (*data) + (data->num_names - 1) * sizeof (data->name_array[0]))) 
	{
        // response too small for num_names
        return FALSE;
    }

    
	/*for (int i = 0; i < data->num_names; i++) 
	{
        szTemp.Format("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c<0x%02x> %s",
                r->name_array[i].nb_name[0],
                r->name_array[i].nb_name[1],
                r->name_array[i].nb_name[2],
                r->name_array[i].nb_name[3],
                r->name_array[i].nb_name[4],
                r->name_array[i].nb_name[5],
                r->name_array[i].nb_name[6],
                r->name_array[i].nb_name[7],
                r->name_array[i].nb_name[8],
                r->name_array[i].nb_name[9],
                r->name_array[i].nb_name[10],
                r->name_array[i].nb_name[11],
                r->name_array[i].nb_name[12],
                r->name_array[i].nb_name[13],
                r->name_array[i].nb_name[14],
                r->name_array[i].nb_name[15],
                (r->name_array[i].name_flags & 128) ? "Group " : "Unique");
		szOutput += szTemp;

        for (j=0; j<NUM_SUFFIX; j++) {
            if ((r->name_array[i].nb_name[15] == Suffixes[j].suff)
                && ((r->name_array[i].name_flags & Suffixes[j].nf_mask)
                    == Suffixes[j].nf_pattern)) {
                szTemp.Format("  %s", Suffixes[j].usage);
				szOutput += szTemp;
                break;
            }
        }
        szOutput += "\n";
    }*/

	//////////////////////////////////////////////////////////////////////////////////////////////////////    
	char szName[16];

	// get computer name
	if (szComputerName != NULL)
	{
		*szComputerName = "";
		memcpy(&szName, data->name_array[0].nb_name, 15); szName[15] = 0;
		*szComputerName = szName;
		szComputerName->TrimRight(' ');
	}

	// get group name
	if (szGroupName != NULL)
	{
		*szGroupName = "";
		for (int i = 0; i < data->num_names; i++)
		{			
			if ((data->name_array[i].name_flags & GROUP_NAME_FLAG) && data->name_array[i].nb_name[15] == NAME_TYPE_DOMAIN)
			{
				memcpy(&szName, data->name_array[i].nb_name, 15); szName[15] = 0;
				*szGroupName = szName;
				break;
			}		
		}
		szGroupName->TrimRight(' ');
	}

	// get user name
	if (szUserName != NULL)
	{
		*szUserName = "";
		for (int i = data->num_names - 1; i >= 0; i--)
		{
			if (data->name_array[i].nb_name[15] == NAME_TYPE_MESSENGER)
			{
				memcpy(&szName, data->name_array[i].nb_name, 15); szName[15] = 0;
				*szUserName = szName;
				break;
			}
		}
		int nDollarIndex = szUserName->Find('$'); 
		if (nDollarIndex >= 0)
			szUserName->Delete(nDollarIndex);

		szUserName->TrimRight(' ');
	}
	
	// get mac address
	if (szMacAddress != NULL)
	{
   		szMacAddress->Format(_T("%02X-%02X-%02X-%02X-%02X-%02X"),
			data->name_array[data->num_names].nb_name[0],
			data->name_array[data->num_names].nb_name[1],
			data->name_array[data->num_names].nb_name[2],
			data->name_array[data->num_names].nb_name[3],
			data->name_array[data->num_names].nb_name[4],
			data->name_array[data->num_names].nb_name[5]);
	}


    return TRUE;

}

int CNetBIOSUtils::RetrieveData(char *buf, int nBufSize)
{	
	struct sockaddr_in caddr;
	SOCKET hSocket;    	

	// Set socket to Non-Blocking mode
	u_long nNonBlocking = 1;	
	fd_set fd_read, fd_error;
	timeval timeout;	

	int nRetBufSize;

    if ((hSocket = socket (AF_INET, SOCK_DGRAM, 0)) < 0) 
	{
		// TODO: retry should be made if no more sockets can be opened
        return 0;
    }

	ioctlsocket(hSocket, FIONBIO, &nNonBlocking);	

    memset((char *) &caddr, 0, sizeof(caddr));
    caddr.sin_family = AF_INET;
	caddr.sin_addr.S_un.S_addr = inet_addr(m_szIP);
    caddr.sin_port = htons(137);

	nRetBufSize = sizeof(NETBIOS_REQUEST) - 1;	
	memcpy(buf, NETBIOS_REQUEST, nRetBufSize);
   
	int nSendToRet = 0;

    if ((nSendToRet = sendto(hSocket, buf, nRetBufSize, 0, (sockaddr *) &caddr, sizeof(caddr))) < 0) 
	{
		closesocket(hSocket);
        return 0;
    }    

	fd_read.fd_array[0] = hSocket; fd_read.fd_count = 1;
	fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;
	timeout.tv_sec = 0; 
	timeout.tv_usec = g_options->m_nPingTimeout * 1000;	

	if (select(0, &fd_read, 0, &fd_error, &timeout) > 0) 
	{
		if (fd_read.fd_count == 1)
		{
			nRetBufSize = recvfrom(hSocket, buf, nBufSize, 0, (struct sockaddr *) 0, (int *)0);

			closesocket(hSocket);

			if (nRetBufSize < 0) 
			{				
				return 0;
			} 
			else 
			{
				return nRetBufSize;
			} 			
		}
	}	

	closesocket(hSocket);
	return 0;
}
