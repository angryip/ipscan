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

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CScanner::CScanner()
{
	m_app = AfxGetApp();
	m_nColumnCount = 5;
	m_pszColumnNames[0] = new CString("Test0");
	m_pszColumnNames[1] = new CString("Test1");
	m_pszColumnNames[2] = new CString("Test2");
	m_pszColumnNames[3] = new CString("Test3");
	m_pszColumnNames[4] = new CString("Test4");

	m_pInitFunctions[0] = &ScanIntInitPing;
	m_pInitFunctions[1] = &ScanIntInitPing;
	m_pInitFunctions[2] = &ScanIntInitPing;
	m_pInitFunctions[3] = &ScanIntInitPing;
	m_pInitFunctions[4] = &ScanIntInitPing;

	m_pScanFunctions[0] = &ScanIntDoPing;
	m_pScanFunctions[1] = &ScanIntDoPing;
	m_pScanFunctions[2] = &ScanIntDoPing;
	m_pScanFunctions[3] = &ScanIntDoPing;
	m_pScanFunctions[4] = &ScanIntDoPing;
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

	pListCtrl->SetExtendedStyle(LVS_EX_FULLROWSELECT);
}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////

int g_nThreadCount = 0;
HANDLE g_hThreads[10000];
CDialog *g_dlg;

UINT ScanningThread(LPVOID cur_ip)
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

	CIpscanDlg *d = (CIpscanDlg *) g_dlg;


	// Shutdown thread //////////////////////////////////////////////////////////
	g_nThreadCount--;

	// Remove thread's handle	
	if (g_nThreadCount >=0) 
	{
		szTmp.Format("%d",g_nThreadCount);		
		d->m_numthreads.SetWindowText(szTmp);
	}

	CloseHandle(g_hThreads[nIndex]);

	g_hThreads[nIndex]=0;

	MessageBox(0, "test", "", 0);
	return 0;


	/*
	
	char err[20];
	sprintf((char*)&err,"%d",numthreads);
	d->m_numthreads.SetWindowText((char*)&err);
	
	char *ipa;
	in_addr in;
	in.S_un.S_addr = htonl((UINT)cur_ip);
	ipa = inet_ntoa(in);
	
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

