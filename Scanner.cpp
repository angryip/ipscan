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


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CScanner::CScanner()
{
	m_app = AfxGetApp();
	m_nColumnCount = 3;

	TInfoStruct infoStruct;

	m_pInfoFunctions[0] = &ScanIntInfoDummy;
	m_pInitFunctions[0] = &ScanIntInitDummy;
	m_pScanFunctions[0] = &ScanIntDoDummy;
	m_pszColumnNames[0] = new CString("IP");

	m_pScanFunctions[1] = &ScanIntDoPing;
	m_pInitFunctions[1] = &ScanIntInitPing;	
	m_pInfoFunctions[1] = &ScanIntInfoPing;

	m_pScanFunctions[2] = &ScanIntDoHostname;
	m_pInitFunctions[2] = NULL;
	m_pInfoFunctions[2] = &ScanIntInfoHostname;

	for (int i=1; i < m_nColumnCount; i++)
	{
		m_pInfoFunctions[i](&infoStruct);
		
		m_pszColumnNames[i] = new CString(infoStruct.szColumnName);
	}
}

CScanner::~CScanner()
{
	for (int i = 0; i < m_nColumnCount; i++)
	{
		delete m_pszColumnNames[i];
	}
}

int CScanner::getColumnCount()
{
	return m_nColumnCount;
}

BOOL CScanner::getColumnName(int nIndex, CString &szColumnHeader)
{
	szColumnHeader = *m_pszColumnNames[nIndex];
	return TRUE;
}

int CScanner::getColumnWidth(int nIndex)
{
	CString str;
	str.Format("Col %s Width", m_pszColumnNames[nIndex]);
	int nWidth = m_app->GetProfileInt("",str,-1);
		
	if (nWidth == -1) 
		nWidth = 80;

	return nWidth;
}

void CScanner::loadSettings()
{


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
		pListCtrl->InsertColumn(nCol, *m_pszColumnNames[nCol], LVCFMT_LEFT, nWidth, nCol);
	}
	
}

BOOL CScanner::initScanning()
{
	for (int i=0; i < m_nColumnCount; i++)
	{
		if (m_pInitFunctions[i] != NULL)
			m_pInitFunctions[i]();
	}
	return TRUE;
}

BOOL CScanner::doScanIP(DWORD nItemIndex)
{
	// get IP address
	int nIP;
	char szIP[16];
	g_d->m_list.GetItemText(nItemIndex, 0, (char*) &szIP, sizeof(szIP));
	nIP = inet_addr((char*)&szIP);

	char szTmp[512];

	// Ping it!
	BOOL bAlive = m_pScanFunctions[1](nIP, (char*) &szTmp, sizeof(szTmp));
	g_d->m_list.SetItemText(nItemIndex, 1, (char*) &szTmp);
	
	if (bAlive)
	{
		g_d->m_list.SetItem(nItemIndex, 0, LVIF_IMAGE, NULL, 0, 0, 0, 0);
	}
	else
	{
		g_d->m_list.SetItem(nItemIndex, 0, LVIF_IMAGE, NULL, 1, 0, 0, 0);
	}

	// Run other scans
	for (int i=2; i < m_nColumnCount; i++)
	{
		if (m_pInfoFunctions[i] != NULL)
		{
			m_pScanFunctions[i](nIP, (char*) &szTmp, sizeof(szTmp));
		}
		else
		{
			strcpy((char*)&szTmp, "ERR!");
		}
		g_d->m_list.SetItemText(nItemIndex, i, (char*) &szTmp);
	}	

	return TRUE;
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
	
	g_nThreadCount--;

	// Remove thread's handle	
	if (g_nThreadCount >=0) 
	{
		szTmp.Format("%d",g_nThreadCount);		
		g_d->m_numthreads.SetWindowText(szTmp);
	}

	CloseHandle(g_hThreads[nIndex]);

	g_hThreads[nIndex]=0;

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


