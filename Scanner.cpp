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

// Scanner.cpp: implementation of the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "Scanner.h"
#include "ScanUtilsInternal.h"
#include "ScanUtilsPlugin.h"
#include "IpscanDlg.h"
#include "globals.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

// These are defined in globals.h
int g_threads[MAX_THREAD_COUNT + 1];
UINT g_nThreadCount = 0;

CDialog *g_dlg;
CIpscanDlg *g_d; 
CScanner *g_scanner;
COptions *g_options;
CRITICAL_SECTION g_criticalSection;

//////////////////////////////////////////////////////////////////////
// Built-in plugins (scanner columns)
//////////////////////////////////////////////////////////////////////

#define DEFAULT_LOADED_COLUMN_COUNT	3

TScannerColumn g_BuiltInScannerColumns[] = 
{
	// IP is always 0!!
	{
		/*pScanFunction*/ &ScanIntDoDummy,
		/*pInfoFunction*/ &ScanIntInfoDummy,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// Ping is always 1!!
	{
		/*pScanFunction*/ &ScanIntDoPing, 
		/*pInfoFunction*/ &ScanIntInfoPing,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ &ScanIntInitPing,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// Hostname
	{
		/*pScanFunction*/ &ScanIntDoHostname,
		/*pInfoFunction*/ &ScanIntInfoHostname,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// NetBIOS Computer Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSComputerName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSComputerName,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// NetBIOS Group Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSGroupName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSGroupName,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// NetBIOS User Name
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSUserName,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSUserName,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// Mac Address
	{
		/*pScanFunction*/ &ScanIntDoNetBIOSMacAddress,
		/*pInfoFunction*/ &ScanIntInfoNetBIOSMacAddress,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	},
	// TTL
	{
		/*pScanFunction*/ &ScanIntDoTTL, 
		/*pInfoFunction*/ &ScanIntInfoTTL,
		/*pSetuFunction*/ NULL,
		/*pInitFunction*/ NULL,
		/*pFinaFunction*/ NULL,
		/*bBuiltinColum*/ TRUE
	}
};


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CScanner::CScanner()
{
	m_app = AfxGetApp();	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("CScanner constructor: started ", 0, 0);
	#endif

	loadAllPossibleColumns();	

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("CScanner constructor: columns loaded ", 0, 0);
	#endif

	loadSelectedColumns();		

	#ifdef DEBUG_MESSAGES
		AfxMessageBox("CScanner constructor: selected columns loaded ", 0, 0);
	#endif
}

void CScanner::loadAllPossibleColumns()
{
	#ifdef DEBUG_MESSAGES
		AfxMessageBox("CScanner::loadAllPossibleColumns(): started ", 0, 0);
	#endif

	m_nAllColumns = sizeof(g_BuiltInScannerColumns) / sizeof(TScannerColumn);	

	m_AllColumns.SetSize(m_nAllColumns + 10, 10);
		
	// Load all possible columns
	for (int i=0; i < m_nAllColumns; i++)
	{
		m_AllColumns[i] = g_BuiltInScannerColumns[i];
	}

	// Load plugins
	CScanUtilsPlugin cPlugins;
	cPlugins.load(m_AllColumns, m_nAllColumns);

	TInfoStruct infoStruct;

	m_AllColumns[0].pszPluginName = new CString("IP");
	
	// Get names of all plugins/columns
	for (i=1; i < m_nAllColumns; i++)
	{
		// Initialize infoStruct
		memset(&infoStruct, 0, sizeof(infoStruct));
		infoStruct.nStructSize = sizeof(infoStruct);
		infoStruct.nUniqueIndex = 0;	// TODO: true index must be set here

		m_AllColumns[i].pInfoFunction(&infoStruct);
		
		m_AllColumns[i].pszPluginName = new CString(infoStruct.szPluginName);		
	}
}

void CScanner::loadSelectedColumns()
{
	// Load selected columns from Registry
	CString szColumns = m_app->GetProfileString("", "Columns", "!");	// "!" means that defaults must be loaded

	// add a non-digit to the end to make parsing easier
	szColumns += " ";	
	
	// Add the following columns - they always present
	m_nColumns = 0;	

	for (int i=0; i < CL_STATIC_COUNT; i++)
	{
		m_Columns[m_nColumns] = i;
		m_nColumns++;
	}

	if (szColumns.GetAt(0) == '!')
	{
		// Load defaults
		m_nColumns = CL_STATIC_COUNT;	

		for (int i=CL_STATIC_COUNT; i < DEFAULT_LOADED_COLUMN_COUNT; i++)
		{
			m_Columns[m_nColumns] = i;
			m_nColumns++;
		}
	}
	else
	{
		// Parse the string			
		char szCurCol[6];
		int nCurColLen = 0;	
		
		for (i=0; i < szColumns.GetLength(); i++)
		{
			char chCur = szColumns.GetAt(i);
			
			if (chCur >= '0' && chCur <= '9')
			{			
				szCurCol[nCurColLen] = chCur;
				nCurColLen++;
			}
			else
			{
				if (nCurColLen == 0)
					continue;	// skip illegal character

				szCurCol[nCurColLen] = 0;			
				nCurColLen = 0;

				int nCurColumn = atoi(szCurCol);

				if (nCurColumn < m_nAllColumns && nCurColumn >= CL_STATIC_COUNT) // Add only if there is a column with this index
				{			
					m_Columns[m_nColumns] = nCurColumn;
					m_nColumns++;
				}
			}
		}
	}
}

void CScanner::saveSelectedColumns()
{
	// Save selected columns to registry
	CString szColumns, szTmp;

	for (int i=CL_STATIC_COUNT; i < m_nColumns; i++)
	{
		szTmp.Format("%d ", m_Columns[i]);
		szColumns += szTmp;
	}

	m_app->WriteProfileString("", "Columns", szColumns);
}

CScanner::~CScanner()
{
	for (int i = 0; i < m_nColumns; i++)
	{
		delete m_AllColumns[i].pszPluginName;
	}
}

int CScanner::getColumnCount()
{
	return m_nColumns;
}

int CScanner::getAllColumnsCount()
{
	return m_nAllColumns;
}

BOOL CScanner::getColumnName(int nIndex, CString &szColumnHeader)
{
	szColumnHeader = *m_AllColumns[m_Columns[nIndex]].pszPluginName;
	return TRUE;
}

BOOL CScanner::getAllColumnName(int nIndex, CString &szColumnHeader)
{
	szColumnHeader = *m_AllColumns[nIndex].pszPluginName;
	return TRUE;
}

int CScanner::getColumnWidth(int nIndex)
{
	CString szName;
	getColumnName(nIndex, szName);
	szName = "Col_" + szName;	
	return m_app->GetProfileInt("", szName, 100);
}

int CScanner::getColumnReference(int nItemIndex)
{
	return m_Columns[nItemIndex];
}

// Shows the information of column to the User by the column index
void CScanner::showColumnInfo(int nColumn, BOOL bAllColumns/* = TRUE*/)
{
	BOOL bNoInfo = FALSE;
	TInfoStruct infoStruct;	

	if (!bAllColumns)
		nColumn = m_Columns[nColumn];

	if (!bAllColumns && g_options->m_bScanPorts && nColumn == getColumnCount())
	{
		// This is a Open Ports special column
		strcpy((char*)&infoStruct.szDescription, "Open ports (which were selected for scanning and appeared open)");
		strcpy((char*)&infoStruct.szPluginName, "Open ports");
	}
	else
	if (g_scanner->m_AllColumns[nColumn].pInfoFunction == NULL)
	{
		bNoInfo = TRUE;
	}
	else
	{		
		// Initialize infoStruct
		memset(&infoStruct, 0, sizeof(infoStruct));
		infoStruct.nStructSize = sizeof(infoStruct);
		infoStruct.nUniqueIndex = 0;	// TODO: a true index must be set here

		m_AllColumns[nColumn].pInfoFunction(&infoStruct);

		if (infoStruct.szDescription[0] == 0)
			bNoInfo = TRUE;
	}

	if (bNoInfo)
	{
		AfxMessageBox("No info about this column", MB_ICONINFORMATION | MB_OK, 0);
	}
	else
	{
		MessageBox(*AfxGetApp()->GetMainWnd(), infoStruct.szDescription, infoStruct.szPluginName, MB_OK | MB_ICONINFORMATION);
	}
}

void CScanner::showColumnOptions(int nColumn, BOOL bAllColumns/* = TRUE*/)
{
	if (!bAllColumns)
		nColumn = m_Columns[nColumn];

	if (!bAllColumns && g_options->m_bScanPorts && nColumn == getColumnCount())
	{
		// This is a special Open Ports column
		AfxMessageBox("See the main Options dialog box", MB_ICONINFORMATION | MB_OK, 0);
		return;
	}
	else
	if (m_AllColumns[nColumn].pOptionsFunction == NULL) 
	{
		// This column doesn't have options
		AfxMessageBox("This column doesn't have any options.", MB_ICONINFORMATION | MB_OK, 0);
		return;
	}

	g_scanner->m_AllColumns[nColumn].pOptionsFunction(*AfxGetApp()->GetMainWnd());
}

void CScanner::initListColumns(CScanListCtrl *pListCtrl)
{
	int nCol, nWidth;

	pListCtrl->DeleteAllItems();

	int nCurrentColumnCount = pListCtrl->GetColumnCount();	
	for (nCol=0; nCol < nCurrentColumnCount; nCol++)
	{
		pListCtrl->DeleteColumn(0);	// Delete the 1st column nCurrentColumnCount times
	}		
	
	for (nCol=0; nCol < m_nColumns; nCol++) 
	{					
		nWidth = getColumnWidth(nCol);
		pListCtrl->InsertColumn(nCol, *m_AllColumns[m_Columns[nCol]].pszPluginName, LVCFMT_LEFT, nWidth, nCol);
	}

	// MFC Bug workaround :-(
	// Make first column ownder-drawn
	HD_ITEM hditem;
	hditem.mask = HDI_FORMAT;
	VERIFY(pListCtrl->GetHeaderCtrl()->GetItem(0, &hditem));
	hditem.fmt |= HDF_OWNERDRAW;
	VERIFY(pListCtrl->GetHeaderCtrl()->SetItem(0, &hditem));

	pListCtrl->SetScanPorts();	// Add / remove last column with port scanning
}

void CScanner::initMenuWithColumns(CMenu *pMenu)
{
	for (int nCol=CL_STATIC_COUNT; nCol < m_nAllColumns; nCol++) 
	{							
		pMenu->InsertMenu(nCol-CL_STATIC_COUNT, MF_BYPOSITION, ID_MENU_SHOW_CMD_001 + nCol-CL_STATIC_COUNT, *m_AllColumns[nCol].pszPluginName);		
		pMenu->EnableMenuItem(nCol-CL_STATIC_COUNT, MF_BYPOSITION | MF_ENABLED);
	}
}

void CScanner::runInitFunction(int nIndex, BOOL bAllFunctions)
{
	if (!bAllFunctions)
		nIndex = m_Columns[nIndex];

	if (m_AllColumns[nIndex].pInitFunction != NULL)
			m_AllColumns[nIndex].pInitFunction();
}

void CScanner::runFinalizeFunction(int nIndex, BOOL bAllFunctions)
{
	if (!bAllFunctions)
		nIndex = m_Columns[nIndex];

	if (m_AllColumns[nIndex].pFinalizeFunction != NULL)
			m_AllColumns[nIndex].pFinalizeFunction();
}

BOOL CScanner::initScanning()
{
	// Initialize all scanners
	for (int i=0; i < m_nColumns; i++)
	{
		runInitFunction(i);
	}

	m_nAliveHosts = 0;
	m_nOpenPorts = 0;	

	// Initialize the global dialog pointer
	g_d = (CIpscanDlg *) g_dlg;	

	// Prepare the list for scanning
	g_d->m_list.PrepareForScanning();

	// Initialize the critical section (used for synchronization of threads)
	InitializeCriticalSection(&g_criticalSection);	

	return TRUE;
}

BOOL CScanner::finalizeScanning()
{	
	for (int i=0; i < m_nColumns; i++)
	{
		runFinalizeFunction(i);
	}

	// Delete the critical section (used for synchronization of threads)
	DeleteCriticalSection(&g_criticalSection);	

	return TRUE;
}


#define MAXINT	0x7FFFFFFF	// Index of newly inserted items

#define DEBUG_LOGS
#undef DEBUG_LOGS

BOOL CScanner::doScanIP(DWORD nParam, BOOL bParameterIsIP, int nThreadIndex)
{
	DWORD nItemIndex;
	DWORD nIP;
	CString szOpenPorts;			// Open ports string will be saved here
	BOOL bSomePortsOpen = FALSE;	// Will be TRUE if some ports are open

	// Known callers:
	// RescanIP() calls this function by item index, but
	// Thread is called by the IP address
	if (bParameterIsIP)
	{
		nIP = nParam;							// IP is passed as parameter
		nItemIndex = (DWORD)nIP - g_nStartIP;	// Item index is counted as sequential, but it may be updated further, if item is not yet inserted
		nIP = htonl(nIP);						// Convert an IP to Network Byte Order
	}
	else
	{
		nItemIndex = nParam;					// ItemIndex is passed as parameter
		nIP = g_d->m_list.GetNumericIP(nItemIndex);		// IP address is obtained from the list (knowing the item index)
	}

	if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die ------------------------------------------------
		return FALSE;

	// At this place, the nIP is known, so let's initialize some stuff for inserting items
	in_addr structInAddr;
	structInAddr.S_un.S_addr = nIP;
	char *szIP = inet_ntoa(structInAddr);

#ifdef DEBUG_LOGS
	FILE *fileHandle = fopen(szIP, "w");
#endif

	if (bParameterIsIP && g_options->m_neDisplayOptions == DISPLAY_ALL)
	{
		// Insert an item if all items should be inserted
		nItemIndex = g_d->m_list.InsertItem(MAXINT, szIP, 2);	// 2nd image - "?"
	}

	char szTmp[512];	// Temporary string. Scanning functions will return data using it.

	// Ping it! (column number 1), Check if it is alive
	int nPingTime = m_AllColumns[CL_PING].pScanFunction(nIP, (char*) &szTmp, sizeof(szTmp));	
	BOOL bAlive = nPingTime >= 0;	// Negative value means "Dead"	 

#ifdef DEBUG_LOGS
	fputs(bAlive? "Alive" : "Dead", fileHandle);
#endif

	if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die -------------------------------------
		return FALSE;
	
	if (bAlive)	// This If is needed to insert an item or finish scanning this IP, other processing will follow
	{
		if (bParameterIsIP && g_options->m_neDisplayOptions == DISPLAY_ALIVE)
		{
			// Insert an item if it is not RescanIP() who called us and scanning mode is DISPLAY_ALIVE			
			nItemIndex = g_d->m_list.InsertItem(MAXINT, szIP, 2);	// 2nd image - "?"
		}
	
		// Increment number of alive hosts
		m_nAliveHosts++;
	}
	else
	{
		if (g_options->m_neDisplayOptions != DISPLAY_ALL)
		{
			// If was chosen to display only alive IPs or only open ports, then we shouldn't do anything further
			return TRUE;
		}
	}

#ifdef DEBUG_LOGS
	fputs("After inserting", fileHandle);
#endif

	if (g_options->m_bScanPorts && g_options->m_neDisplayOptions == DISPLAY_OPEN)
	{
		// If display only open ports, then scan ports prior to scanning other columns
		bSomePortsOpen = doScanPorts(nIP, szOpenPorts, nPingTime, nThreadIndex);
		
		if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die -----------------------------------------
			return FALSE;

		if (bSomePortsOpen)	// This IF is needed only to insert an item or finish scanning this IP, other processing will follow
		{
			if (bParameterIsIP)
			{
				// Insert an item if it is not RescanIP() who called us and scanning mode is DISPLAY_OPENPORT				
				nItemIndex = g_d->m_list.InsertItem(MAXINT, szIP, 2);	// 2nd image - "?"
			}
		}
		else
		{
			// If was chosen to display only open ports, then we shouldn't do anything further
			return TRUE;
		}
	}

	if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die ------------------------------------------------
		return FALSE;

	// At this point, the item is inserted into the list in any case!!!
	// So we can update the item in the list	

	if (bAlive)	// Update the item according to alive/dead status
	{
		// Change image to Alive
		g_d->m_list.SetItem(nItemIndex, CL_IP, LVIF_IMAGE, NULL, 0, 0, 0, 0);
	}
	else
	{
		// Change image to Dead
		g_d->m_list.SetItem(nItemIndex, CL_IP, LVIF_IMAGE, NULL, 1, 0, 0, 0);
	}

#ifdef DEBUG_LOGS
	fputs("After image setting", fileHandle);
	CString szItemIndex;
	szItemIndex.Format("Item index: %d ", nItemIndex);
	fputs(szItemIndex, fileHandle);
#endif

	// Set item text returned from pinging (Dead or X ms)
	g_d->m_list.SetItemText(nItemIndex, CL_PING, (char*) &szTmp);

#ifdef DEBUG_LOGS
	fputs(szTmp, fileHandle);
	g_d->m_list.GetItemText(nItemIndex, CL_PING, (char*) &szTmp, sizeof(szTmp));
	fputs(szTmp, fileHandle);
	szItemIndex.Format("Item index: %d ", nItemIndex);
	fputs(szItemIndex, fileHandle);
	g_d->m_list.GetItemText(nItemIndex, CL_IP, (char*) &szTmp, sizeof(szTmp));
	fputs(szTmp, fileHandle);
#endif
	
	// Scan other columns if scanning of dead hosts is enabled or host is Alive
	bool bScan = g_options->m_bScanHostIfDead || bAlive; 

	// Run other scans (besides ping and port scanning)
	for (int i = CL_STATIC_COUNT; i < m_nColumns; i++)
	{
		if (bScan)
		{
			if (m_AllColumns[m_Columns[i]].pInfoFunction != NULL)
			{
				szTmp[0] = 0;
				runScanFunction(nIP, i, (char*) &szTmp, sizeof(szTmp));
				
				// Returned an empty string
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

		if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die ----------------------------------------------------
			return FALSE;
		
		// Update the list with scanned info
		g_d->m_list.SetItemText(nItemIndex, i, (char*) &szTmp);
	}

	if (g_options->m_bScanPorts)
	{	
		if (bScan)
		{
			// Scan ports if they weren't scanned already above
			if (g_options->m_neDisplayOptions != DISPLAY_OPEN)
			{
				bSomePortsOpen = doScanPorts(nIP, szOpenPorts, nPingTime, nThreadIndex);

				if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die -----------------------------------------------
					return FALSE;
			}
			
			if (bSomePortsOpen)	// bSomePortsOpen may be set already above
			{
				// Increment open ports
				m_nOpenPorts++;
			}
			
			// Update the list with open/closed ports
			g_d->m_list.SetOpenPorts(nItemIndex, szOpenPorts, bSomePortsOpen);	// szOpenPorts may be initialized above already
		}
		else
		{
			// Update the list and say that ports were not scanned
			g_d->m_list.SetOpenPorts(nItemIndex, "N/S", FALSE);
		}
	}


#ifdef DEBUG_LOGS
	fclose(fileHandle);
#endif

	return TRUE;
}

BOOL CScanner::doScanPorts(DWORD nIP, CString &szResult, int nPingTime, int nThreadIndex)
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
	timeout.tv_sec = 0; 
	
	if (nPingTime >= 0 && g_options->m_bOptimizePorts)
	{
		if (nPingTime < 20) nPingTime = 20;
		timeout.tv_usec = (nPingTime * 8) * 1000;		// Optimized port scanning prevents port filtering from making scanning slower		
	}
	else
	{
		timeout.tv_usec = g_options->m_nPortTimeout * 1000;	// If host wasn't pinged or optimized scanning switched off, then use default timeout
	}

	if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die --------------------------------------------------------
		return FALSE;

	for (int nCurPortIndex = 0; aPorts[nCurPortIndex].nStartPort != 0; nCurPortIndex++)
	{		
		for (int nPort = aPorts[nCurPortIndex].nStartPort; nPort <= aPorts[nCurPortIndex].nEndPort; nPort++)
		{									
			// Check that current timeout isn't too long
			if (timeout.tv_usec > g_options->m_nPortTimeout * 1000)
				timeout.tv_usec = g_options->m_nPortTimeout * 1000;			

			// Check that current timeout isn't too short
			if (timeout.tv_usec < 10000)
				timeout.tv_usec = 10000;
			
			// Create a new socket each time because there is no a function to reuse a socket
			hSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_IP);

			if (hSocket == INVALID_SOCKET)
			{
				int nError = WSAGetLastError();

				if (nError == WSAEMFILE) // No more socket handles
				{
					// TODO: we must do something here!!!
					MessageBox(0, "No more sockets left, please contact the author!", "", 0);
				}
				else
				{
					szResult.Format("WINSOCK ERROR: %d", nError);
					return FALSE;
				}
			}

			// Measure time between each port
			DWORD nPortStartTime = GetTickCount();

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
					szResult += szPort + ',';					
				}
			}			

			closesocket(hSocket);

			if (g_options->m_bOptimizePorts)
			{
				// Time for this port
				DWORD nPortScanTime = GetTickCount() - nPortStartTime;

				// If the port is not filtered
				if (nPortScanTime * 1000 < (unsigned)timeout.tv_usec + 2000)
				{
					// Set the new timeout
					timeout.tv_usec = (timeout.tv_usec + (nPortScanTime+5) * 1000) >> 1;	// make new timeout a mean
				}
			}

			if (g_threads[nThreadIndex] == THREAD_MUST_DIE)	// Program asked to die ------------------------------------------------
				return FALSE;
		}
	}	

	BOOL bResult;
		
	if (szResult.GetLength() > 0)	// TRUE if any ports were open
	{
		// Strip comma from the end
		szResult.Delete(szResult.GetLength()-1);
		bResult = TRUE;
	}
	else
	{
		szResult = "N/A";
		bResult = FALSE;
	}

	return bResult;
}

BOOL CScanner::runScanFunction(DWORD nIP, int nIndex, char *szBuffer, int nBufferLength, BOOL bGlobal /*=FALSE*/)
{
	TScanFunction *pScanFunction;

	if (bGlobal)
		pScanFunction = m_AllColumns[nIndex].pScanFunction;
	else
		pScanFunction = m_AllColumns[m_Columns[nIndex]].pScanFunction;

	return pScanFunction(nIP, szBuffer, nBufferLength);
}


////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////


UINT ThreadProcCallback(LPVOID nIP)
{	
	return ScanningThread((DWORD)nIP, IP_IS_GIVEN);
}

UINT ThreadProcCallbackRescan(LPVOID nItemIndex)
{	
	return ScanningThread((DWORD)nItemIndex, INDEX_IS_GIVEN);
}

UINT ScanningThread(DWORD nParam, BOOL bParameterIsIP)
{
	// Initialize thread //////////////////////////////////////////////////////	

	CString szTmp;	
		
	// Put thread's activity flag into global array (and find it's index)	
	int nThreadIndex;

	EnterCriticalSection(&g_criticalSection);	//////// BEGIN SYNCRONIZATION ////////////////////

	//g_nThreadCount++; This is incremented right before calling the thread

	for (nThreadIndex=0; nThreadIndex < MAX_THREAD_COUNT; nThreadIndex++) 
	{
		if (g_threads[nThreadIndex] == THREAD_DEAD) 
		{ 			
			g_threads[nThreadIndex] = THREAD_ALIVE;	// Thread is running
			break; 
		}
	}	

	// Display current number of threads
	szTmp.Format("%d", g_nThreadCount);
	g_d->m_numthreads.SetWindowText(szTmp);

	LeaveCriticalSection(&g_criticalSection);	//////// END SYNCRONIZATION ////////////////////

	/////////////////////////////////////////////////////////////////////////////
	// Process scan /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	// check if this IP is not broadcasting IP 
	// (just a guess right now, but in general, most X.X.X.0 and X.X.X.255 IPs are broadcasting)

	BOOL bProceed = TRUE;

	if (bParameterIsIP && g_options->m_bSkipBroadcast)
	{
		if (((nParam & 0xFF) == 0xFF) || ((nParam & 0xFF) == 0x00))
		{
			bProceed = FALSE;
		}
	}
	
	if (bProceed)
		g_scanner->doScanIP(nParam, bParameterIsIP, nThreadIndex);
	
	/////////////////////////////////////////////////////////////////////////////
	// Shutdown thread //////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	EnterCriticalSection(&g_criticalSection);	//////// BEGIN SYNCRONIZATION ////////////////////
	
	// Remove thread's handle	
	if (g_nThreadCount >=0) 
	{
		g_d->SetDlgItemInt(IDC_NUMTHREADS, g_nThreadCount, FALSE);
	}	

	g_threads[nThreadIndex] = THREAD_DEAD;	// Thread is dead now

	if (g_nThreadCount > 0)	// For safety
		g_nThreadCount--;

	// Display current number of threads
	szTmp.Format("%d", g_nThreadCount);
	g_d->m_numthreads.SetWindowText(szTmp);

	LeaveCriticalSection(&g_criticalSection);	//////// END SYNCRONIZATION ////////////////////
	
	return 0;

}

////////////////////////////////////////////////////////////////////////
//////////////////////////// THREAD ////////////////////////////////////
////////////////////////////////////////////////////////////////////////




