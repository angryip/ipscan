// Scanner.cpp: implementation of the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "Scanner.h"
#include "ScanUtilsInternal.h"
#include "IpscanDlg.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif


UINT g_nThreadCount = 0;
HANDLE g_hThreads[10000];
CDialog *g_dlg;
CIpscanDlg *g_d; 
CScanner *g_scanner;
COptions *g_options;

//////////////////////////////////////////////////////////////////////
// Built-in plugins (scanner columns)
//////////////////////////////////////////////////////////////////////

TScannerColumn g_BuiltInScannerColumns[] = 
{
	// IP is always 0!!
	{
		/*pScanFunction*/ &ScanIntDoDummy,
		/*pInfoFunction*/ &ScanIntInfoDummy,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL
	},
	// Ping is always 1!!
	{
		/*pScanFunction*/ &ScanIntDoPing, 
		/*pInfoFunction*/ &ScanIntInfoPing,
		/*pInitFunction*/ &ScanIntInitPing,
		/*pFinaFunction*/ NULL
	},
	// Hostname
	{
		/*pScanFunction*/ &ScanIntDoHostname,
		/*pInfoFunction*/ &ScanIntInfoHostname,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL
	},
	// NetBIOS Computer Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSComputerName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSComputerName,
		/*pInitFunction*/ &ScanIntInitNetBIOS,
		/*pFinaFunction*/ &ScanIntFinalizeNetBIOS
	},
	// NetBIOS Group Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSGroupName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSGroupName,
		/*pInitFunction*/ &ScanIntInitNetBIOS,
		/*pFinaFunction*/ &ScanIntFinalizeNetBIOS
	},
	// NetBIOS User Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSUserName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSUserName,
		/*pInitFunction*/ &ScanIntInitNetBIOS,
		/*pFinaFunction*/ &ScanIntFinalizeNetBIOS
	},
	// Mac Address
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSMacAddress,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSMacAddress,
		/*pInitFunction*/ &ScanIntInitNetBIOS,
		/*pFinaFunction*/ &ScanIntFinalizeNetBIOS
	}
};


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CScanner::CScanner()
{
	m_app = AfxGetApp();
	m_nColumnCount = sizeof(g_BuiltInScannerColumns) / sizeof(TScannerColumn);

	TInfoStruct infoStruct;
	
	memcpy(&m_Columns, &g_BuiltInScannerColumns, sizeof(g_BuiltInScannerColumns));

	m_Columns[0].pszColumnName = new CString("IP");
	
	for (int i=1; i < m_nColumnCount; i++)
	{
		m_Columns[i].pInfoFunction(&infoStruct);		
		
		m_Columns[i].pszColumnName = new CString(infoStruct.szColumnName);		
	}

}

CScanner::~CScanner()
{
	for (int i = 0; i < m_nColumnCount; i++)
	{
		delete m_Columns[i].pszColumnName;
	}
}

int CScanner::getColumnCount()
{
	return m_nColumnCount;
}

BOOL CScanner::getColumnName(int nIndex, CString &szColumnHeader)
{
	szColumnHeader = *m_Columns[nIndex].pszColumnName;
	return TRUE;
}

int CScanner::getColumnWidth(int nIndex)
{
	CString str;
	str.Format("Col %s Width", m_Columns[nIndex].pszColumnName);
	int nWidth = m_app->GetProfileInt("",str,-1);
		
	if (nWidth == -1) 
		nWidth = 80;

	return nWidth;
}

void CScanner::initListColumns(CListCtrl *pListCtrl)
{
	int nCol, nWidth;	

	pListCtrl->DeleteAllItems();
	for (nCol=0; nCol < pListCtrl->GetHeaderCtrl()->GetItemCount(); nCol++)
	{
		pListCtrl->DeleteColumn(nCol);
	}	
	
	for (nCol=0; nCol < m_nColumnCount; nCol++) 
	{					
		nWidth = getColumnWidth(nCol);
		pListCtrl->InsertColumn(nCol, *m_Columns[nCol].pszColumnName, LVCFMT_LEFT, nWidth, nCol);
	}
	
}

void CScanner::initMenuWithColumns(CMenu *pMenu)
{
	for (int nCol=2; nCol < m_nColumnCount; nCol++) 
	{							
		pMenu->InsertMenu(nCol-2, MF_BYPOSITION, ID_MENU_SHOW_CMD_001 + nCol-2, *m_Columns[nCol].pszColumnName);		
		pMenu->EnableMenuItem(nCol-2, MF_BYPOSITION | MF_ENABLED);
	}
}

BOOL CScanner::initScanning()
{
	for (int i=0; i < m_nColumnCount; i++)
	{
		if (m_Columns[i].pInitFunction != NULL)
			m_Columns[i].pInitFunction();
	}

	m_nAliveHosts = 0;
	m_nOpenPorts = 0;

	return TRUE;
}

BOOL CScanner::finalizeScanning()
{	
	/*for (int i=0; i < m_nColumnCount; i++)
	{
		if (m_Columns[i].pFinalizeFunction != NULL)
			m_Columns[i].pFinalizeFunction();
	}*/

	return TRUE;
}

BOOL CScanner::doScanIP(DWORD nItemIndex)
{
	// get IP address
	DWORD nIP;
	char szIP[16];
	g_d->m_list.GetItemText(nItemIndex, 0, (char*) &szIP, sizeof(szIP));
	nIP = inet_addr((char*)&szIP);

	char szTmp[512];

	// Ping it! (column number 1)
	BOOL bAlive = m_Columns[1].pScanFunction(nIP, (char*) &szTmp, sizeof(szTmp));
	g_d->m_list.SetItemText(nItemIndex, 1, (char*) &szTmp);
	
	if (bAlive)
	{
		// Change image to Alive
		g_d->m_list.SetItem(nItemIndex, 0, LVIF_IMAGE, NULL, 0, 0, 0, 0);
		
		// Increment open hosts
		m_nAliveHosts++;
	}
	else
	{
		// Change image to Dead
		g_d->m_list.SetItem(nItemIndex, 0, LVIF_IMAGE, NULL, 1, 0, 0, 0);
	}
	
	
	bool bScan = g_options->m_bScanHostIfDead || bAlive;

	// Run other scans
	for (int i=2; i < m_nColumnCount; i++)
	{
		if (bScan)
		{
			if (m_Columns[i].pInfoFunction != NULL)
			{
				szTmp[0] = 0;
				m_Columns[i].pScanFunction(nIP, (char*) &szTmp, sizeof(szTmp));
				
				// Returned empty string
				if (szTmp[0] == 0)
					strcpy((char*)&szTmp, "N/A");
			}
			else
			{
				strcpy((char*)&szTmp, "ERR!");
			}
		}
		else
		{
			// Dead host, not scanned
			strcpy((char*) &szTmp, "N/S");
		}
		g_d->m_list.SetItemText(nItemIndex, i, (char*) &szTmp);
	}

	if (bScan && g_options->m_bScanPorts)
	{		
		// Scan ports
		CString szOpenPorts;
		int nOpenPorts = doScanPorts(nIP, szOpenPorts);
		if (nOpenPorts == OPEN_PORTS_STATUS_OPEN)
		{
			// Increment open ports
			m_nOpenPorts++;

			g_d->m_list.SetOpenPorts(nItemIndex, szOpenPorts);			
		}
		else
		{
			g_d->m_list.SetOpenPorts(nItemIndex, (LPCSTR) nOpenPorts);
		}
	}
	else
	{
		g_d->m_list.SetOpenPorts(nItemIndex, (LPCSTR) OPEN_PORTS_STATUS_NOT_SCANNED);
	}

	return TRUE;
}

int CScanner::doScanPorts(DWORD nIP, CString &szResult)
{
	szResult = "";

	// Scan port
	tPortRange *aPorts = g_options->m_aParsedPorts;

	SOCKET hSocket; 
	
	CString szPort;

	// Set socket to Non-Blocking mode
	u_long nNonBlocking = 1;	
	fd_set fd_write, fd_error;
	
	timeval timeout;
	timeout.tv_sec = 0; timeout.tv_usec = g_options->m_nPortTimeout * 1000;
	
	for (int nCurPortIndex = 0; aPorts[nCurPortIndex].nStartPort != 0; nCurPortIndex++)
	{		
		for (int nPort = aPorts[nCurPortIndex].nStartPort; nPort <= aPorts[nCurPortIndex].nEndPort; nPort++)
		{			
			hSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_IP);
			// TODO: maximum number of sockets in the system
			sockaddr_in sin;
			sin.sin_addr.S_un.S_addr = nIP;
			sin.sin_family = PF_INET;
			ioctlsocket(hSocket, FIONBIO, &nNonBlocking);

			sin.sin_port = htons(nPort);
			connect(hSocket, (sockaddr*)&sin, sizeof(sin));

			fd_write.fd_array[0] = hSocket; fd_write.fd_count = 1;			
			fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;
			if (select(0, 0, &fd_write, &fd_error, &timeout) > 0) 
			{
				if (fd_write.fd_count == 1)
				{
					// Connection successfull
					szPort.Format("%d", nPort);
					szResult += szPort + ",";				
				}
			}
			
			closesocket(hSocket);
		}
	}
	
	
	int nResult = OPEN_PORTS_STATUS_NONE;

	if (szResult.GetLength() > 0)	// TRUE if any ports were open
	{
		nResult = OPEN_PORTS_STATUS_OPEN;

		// Strip comma from the end
		szResult.Delete(szResult.GetLength()-1);
	}

	return nResult;
}


////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////


UINT ScanningThread(LPVOID nItemIndex)
{
	// Initialize thread //////////////////////////////////////////////////////

	CString szTmp;
	int nIndex;
	
	g_nThreadCount++;
	
	// Put thread's handle into global array (and find it's index)
	for (nIndex=0; nIndex<=10000; nIndex++) 
	{
		if (g_hThreads[nIndex]==0) 
		{ 
			HANDLE hTmp;
			DuplicateHandle(GetCurrentProcess(),GetCurrentThread(),GetCurrentProcess(),&hTmp,0,FALSE,DUPLICATE_SAME_ACCESS);
			g_hThreads[nIndex] = hTmp;
			break; 
		}
	}

	g_d = (CIpscanDlg *) g_dlg;	

	// Display current number of threads
	szTmp.Format("%d", g_nThreadCount);
	g_d->m_numthreads.SetWindowText(szTmp);

	// Process scan /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	g_scanner->doScanIP((DWORD)nItemIndex);
	
	/////////////////////////////////////////////////////////////////////////////
	// Shutdown thread //////////////////////////////////////////////////////////	

	// Remove thread's handle	
	if (g_nThreadCount >=0) 
	{
		szTmp.Format("%d",g_nThreadCount);		
		g_d->m_numthreads.SetWindowText(szTmp);
	}

	CloseHandle(g_hThreads[nIndex]);

	g_hThreads[nIndex]=0;

	g_nThreadCount--;

	// Display current number of threads
	szTmp.Format("%d", g_nThreadCount);
	g_d->m_numthreads.SetWindowText(szTmp);
	
	return 0;


	/*
	
		if (ThreadProcRescanThisIP >= 0) 
	{
		n = ThreadProcRescanThisIP; 
	} 
	else 
	{
		n = (UINT)cur_ip - d->m_startip;
	}

	// PING!!!
	BOOL bAlive = ScanIntDoPing(in.S_un.S_addr, NULL, 0);	
	
	if (!bAlive) 
	{
		sprintf((char*)&err,"%u",WSAGetLastError());

		if (d->m_display!=DO_ALL) 
		{	
			goto exit_thread;
		} 
		
		d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,1,0,0,0);
		d->m_list.SetItem(n,CL_STATE,LVIF_TEXT,"Dead",0,0,0,0);
		
		if (d->m_retrifdead) 
		{
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) 
			{
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} 
			else 
			{
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}

		} 
		else 
		{
			d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0);
			d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
		}
		
		d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/A",0,0,0,0);
		d->m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,"N/A",0,0,0,0);
		if (d->m_portondead) goto scan_port;

	} 
	else 
	{
		// Alive
		if (d->m_display!=DO_ALL && ThreadProcRescanThisIP == -1) 
		{
			n = d->m_list.InsertItem(n,ipa,0); 
			//d->m_list.SetItemData(n, n);
		}
		numalive++;
		d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,0,0,0,0);
		
		d->m_list.SetItem(n,CL_PINGTIME,LVIF_TEXT,(char*)&err,0,0,0,0);
		
		if (d->m_resolve) 
		{
			hostent *he = gethostbyaddr((char*)&in.S_un.S_addr,4,0);
			if (he) 
			{
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,he->h_name,0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
			} 
			else 
			{
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/A",0,0,0,0); 
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);	
			}
		} 
		else 
		{
			d->m_list.SetItem(n,CL_HOSTNAME,LVIF_TEXT,"N/S",0,0,0,0); 
			//d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,"None",0,0,0,0);	
		}
scan_port:
		if (d->m_scanport) 
		{
			// Scan port
			SOCKET skt = socket(PF_INET,SOCK_STREAM,IPPROTO_IP);
			sockaddr_in sin;
			sin.sin_addr.S_un.S_addr = in.S_un.S_addr;
			sin.sin_family = PF_INET;
			sin.sin_port = htons(d->m_port);
			int se = connect(skt,(sockaddr*)&sin,sizeof(sin));
			if (se!=0) 
			{
				sprintf((char*)&err,"%u",WSAGetLastError());
				d->m_list.SetItem(n,CL_ERROR,LVIF_TEXT,(char*)&err,0,0,0,0);
				sprintf((char*)&err,"%u: closed",d->m_port);
				d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
			} 
			else 
			{
				numopen++;
				sprintf((char*)&err,"%u: open",d->m_port);
				d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,(char*)&err,0,0,0,0);
				d->m_list.SetItem(n,0,LVIF_IMAGE,NULL,3,0,0,0);				
			}
			closesocket(skt);

		} 
		else d->m_list.SetItem(n,CL_PORT,LVIF_TEXT,"N/S",0,0,0,0);
	}

exit_thread:

	numthreads--;
	if (numthreads>=0) 
	{
		sprintf((char*)&err,"%d",numthreads);
		d->m_numthreads.SetWindowText((char*)&err);
	}

	CloseHandle(threads[index]);

	threads[index]=0;

	return 0;*/
}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////






