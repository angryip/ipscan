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

// Options.cpp: implementation of the COptions class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "Options.h"
#include "IpscanDlg.h"
#include "QueryDlg.h"
#include "FavouriteDeleteDlg.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

COptions::COptions()
{
	m_aParsedPorts = 0;
}

COptions::~COptions()
{
	if (m_aParsedPorts != NULL)
		delete m_aParsedPorts;
}

void COptions::setPortString(LPCSTR szPortString)
{
	m_szPorts = szPortString;
	parsePortString();
}

BOOL COptions::parsePortString()
{
	
	if (m_aParsedPorts != NULL)
		delete m_aParsedPorts;

	// Pre-process
	int nCommas = 0;
	m_szPorts += ',';
	LPCSTR szPorts = m_szPorts;
	for (int i=0; szPorts[i] != 0; i++)
	{
		if (szPorts[i] == ',') nCommas++;
	}

	m_aParsedPorts = new tPortRange[nCommas + 10];
	memset(m_aParsedPorts, 0, (nCommas + 10) * sizeof(tPortRange));

	m_nPortCount = 0;

	// Process!!!
	char szCurPort[6];
	int nCurPortLen = 0;
	int nCurPortIndex = 0;
	
	for (i=0; szPorts[i] != 0; i++)
	{
		if (szPorts[i] >= '0' && szPorts[i] <= '9')
		{			
			if (nCurPortLen >= 5)
			{			
				AfxMessageBox("A port number cannot be greater than 65535", MB_ICONHAND | MB_OK, 0);
				return FALSE;
			}

			szCurPort[nCurPortLen] = szPorts[i];
			nCurPortLen++;
		}
		else
		{
			szCurPort[nCurPortLen] = 0;
			u_short nCurPort = (u_short) atoi(szCurPort);
			nCurPortLen = 0;

			if (szPorts[i] == '-')
			{
				if (m_aParsedPorts[nCurPortIndex].nStartPort != 0)
				{
					AfxMessageBox("Unexpected \"-\" in the port string", MB_ICONHAND | MB_OK, 0);
					return FALSE;
				}

				m_aParsedPorts[nCurPortIndex].nStartPort = nCurPort;
			}
			else
			if (szPorts[i] == ',')
			{
				if (szPorts[i+1] != 0 && szCurPort[0] == 0)
				{
					AfxMessageBox("Port cannot be 0 or unexpected comma in the port string", MB_ICONHAND | MB_OK, 0);
					return FALSE;
				}							

				if (m_aParsedPorts[nCurPortIndex].nStartPort == 0)
				{
					m_aParsedPorts[nCurPortIndex].nStartPort = nCurPort;
				}
				
				m_aParsedPorts[nCurPortIndex].nEndPort = nCurPort;

				m_nPortCount += m_aParsedPorts[nCurPortIndex].nEndPort - m_aParsedPorts[nCurPortIndex].nStartPort + 1;

				nCurPortIndex++;				
			}
		}
	}	

	// Delete the comma added above
	m_szPorts.Delete(m_szPorts.GetLength()-1);

	return TRUE;	
}


void COptions::save()
{
	CWinApp *app = AfxGetApp();

	CString szURL; szURL.LoadString(IDS_HOMEPAGE);
	app->WriteProfileString("", "URL", szURL);	
	app->WriteProfileInt("","Delay",m_nTimerDelay);
	app->WriteProfileInt("","MaxThreads",m_nMaxThreads);
	app->WriteProfileInt("","Timeout",m_nPingTimeout);	
	app->WriteProfileInt("","PingCount",m_nPingCount);	
	app->WriteProfileInt("","PortTimeout",m_nPortTimeout);	
	app->WriteProfileInt("","DisplayOptions",m_neDisplayOptions);	
	app->WriteProfileString("", "PortString", m_szPorts);
	app->WriteProfileInt("", "ScanHostIfDead", m_bScanHostIfDead);
	app->WriteProfileInt("", "ScanPorts", m_bScanPorts);
	app->WriteProfileInt("", "ShowPortsBelow", m_bShowPortsBelow);
	app->WriteProfileInt("", "OptimizePorts", m_bOptimizePorts);
	app->WriteProfileInt("", "AutoSave", m_bAutoSave);
	app->WriteProfileInt("", "SkipBroadcast", m_bSkipBroadcast);

	g_scanner->saveSelectedColumns();
}

void COptions::saveDimensions()
{
	CWinApp *app = AfxGetApp();

	// Save window pos
	RECT rc;
	app->GetMainWnd()->GetWindowRect(&rc);
	app->WriteProfileInt("","Left",rc.left);
	app->WriteProfileInt("","Top",rc.top);
	app->WriteProfileInt("","Bottom",rc.bottom);
	app->WriteProfileInt("","Right",rc.right);
	
	// Save column widths
	CString szTmp;
	CIpscanDlg *cDlg = (CIpscanDlg *) app->GetMainWnd();	
	for (int i=0; i < g_scanner->getColumnCount(); i++) 
	{
		g_scanner->getColumnName(i, szTmp);
		szTmp = "Col_" + szTmp;
		app->WriteProfileInt("", szTmp, cDlg->m_list.GetColumnWidth(i));
	}
	if (m_bScanPorts)
	{
		// Save extra column width (Open ports)
		szTmp = "Col_!OP!";
		app->WriteProfileInt("", szTmp, cDlg->m_list.GetColumnWidth(cDlg->m_list.GetColumnCount()-1));		
	}
}


void COptions::load()
{
	CWinApp *app = AfxGetApp();
	
	m_nTimerDelay = app->GetProfileInt("","Delay",20);
	m_nMaxThreads = app->GetProfileInt("","MaxThreads",64);
	m_nPingTimeout = app->GetProfileInt("","Timeout",3000);
	m_nPingCount = app->GetProfileInt("","PingCount",3);
	m_nPortTimeout = app->GetProfileInt("","PortTimeout",3000);
	m_neDisplayOptions = app->GetProfileInt("","DisplayOptions",0);
	m_bScanHostIfDead = app->GetProfileInt("", "ScanHostIfDead", FALSE);
	m_bScanPorts = app->GetProfileInt("", "ScanPorts", FALSE);
	m_bShowPortsBelow = app->GetProfileInt("", "ShowPortsBelow", TRUE);
	m_bOptimizePorts = app->GetProfileInt("", "OptimizePorts", TRUE);
	m_bAutoSave = app->GetProfileInt("", "AutoSave", FALSE);
	m_bSkipBroadcast = app->GetProfileInt("", "SkipBroadcast", TRUE);
	
	setPortString(app->GetProfileString("", "PortString", ""));	// also parses it

	// Load Favourites
	loadFavourites();

	// Load Openers
	loadOpeners();

	// Path, where the Angry IP Scanner resides
	m_szExecutablePath = __targv[0];
	int nTmp = m_szExecutablePath.ReverseFind('\\');
	m_szExecutablePath.Delete(nTmp, m_szExecutablePath.GetLength() - nTmp);	
}

void COptions::loadFavourites()
{
	CWinApp *app = AfxGetApp();

	CString szKey;

	for (int i=0; i < 99; i++)
	{
		szKey.Format("FavouriteName_%d", i);
		m_aFavourites[i].szName = app->GetProfileString("", szKey);
		
		if (m_aFavourites[i].szName.GetLength() == 0)
			break;

		
		szKey.Format("FavouriteIP1_%d", i);
		m_aFavourites[i].nIP1 = app->GetProfileInt("", szKey, 0);
		szKey.Format("FavouriteIP2_%d", i);
		m_aFavourites[i].nIP2 = app->GetProfileInt("", szKey, 0);
	}

}

void COptions::saveFavourites()
{
	CWinApp *app = AfxGetApp();

	CString szKey;

	for (int i=0; i < 99; i++)
	{
		szKey.Format("FavouriteName_%d", i);
		app->WriteProfileString("", szKey, m_aFavourites[i].szName);

		if (m_aFavourites[i].szName.GetLength() == 0)
			break;

		szKey.Format("FavouriteIP1_%d", i);
		app->WriteProfileInt("", szKey, m_aFavourites[i].nIP1);

		szKey.Format("FavouriteIP2_%d", i);
		app->WriteProfileInt("", szKey, m_aFavourites[i].nIP2);	
	}
}

void COptions::loadOpeners()
{
	CWinApp *app = AfxGetApp();

	CString szKey;

	for (int i=0; i < 99; i++)
	{
		szKey.Format("OpenerName_%d", i);
		m_aOpeners[i].szName = app->GetProfileString("", szKey);
		
		if (m_aOpeners[i].szName.GetLength() == 0)
			break;
		
		szKey.Format("OpenerString_%d", i);
		m_aOpeners[i].szExecute = app->GetProfileString("", szKey);

		szKey.Format("OpenerDir_%d", i);
		m_aOpeners[i].szWorkDir = app->GetProfileString("", szKey);
	}

	// Add default openers, if no openers were loaded
	if (m_aOpeners[0].szName.GetLength() == 0)
	{
		m_aOpeners[0].szName = "In explorer";
		m_aOpeners[0].szExecute = "\\\\%s";

		m_aOpeners[1].szName = "Web Browser (HTTP)";
		m_aOpeners[1].szExecute = "http://%s/";

		m_aOpeners[2].szName = "FTP";
		m_aOpeners[2].szExecute = "ftp://%s/";

		m_aOpeners[3].szName = "Telnet";
		m_aOpeners[3].szExecute = "telnet://%s/";

		m_aOpeners[4].szName = "Ping";
		m_aOpeners[4].szExecute = "ping %s";

		m_aOpeners[4].szName = "Traceroute";
		m_aOpeners[4].szExecute = "tracert %s";
	}

}

void COptions::saveOpeners()
{
	CWinApp *app = AfxGetApp();

	CString szKey;

	for (int i=0; i < 99; i++)
	{
		szKey.Format("OpenerName_%d", i);
		app->WriteProfileString("", szKey, m_aOpeners[i].szName);

		if (m_aOpeners[i].szName.GetLength() == 0)
			break;

		szKey.Format("OpenerString_%d", i);
		app->WriteProfileString("", szKey, m_aOpeners[i].szExecute);

		szKey.Format("OpenerDir_%d", i);
		app->WriteProfileString("", szKey, m_aOpeners[i].szWorkDir);
	}
}

void COptions::setWindowPos()
{
	CWinApp *app = AfxGetApp();

	RECT rc;

	rc.left = app->GetProfileInt("","Left",0);
	rc.top = app->GetProfileInt("","Top",0);
	rc.bottom = app->GetProfileInt("","Bottom",0);
	rc.right = app->GetProfileInt("","Right",0);

	// Fix restoring from saved maximized state

	if (rc.left < 0)
	{
		rc.right += rc.left;
		rc.left = 0;		
	}

	if (rc.top < 0)
	{
		rc.bottom += rc.top;
		rc.top = 0;
	}

	CWnd *d = app->GetMainWnd();

    if (rc.right!=0) 
	{
		d->SetWindowPos(NULL,rc.left,rc.top,rc.right-rc.left,rc.bottom-rc.top,SWP_NOZORDER);
	} 
	else 
	{
		d->SetWindowPos(NULL,0,0,502,350,SWP_NOMOVE | SWP_NOZORDER);
	}

	// Column widths are restored in CScanner::initListColumns()
}

CString COptions::getCurrentDate()
{
	TCHAR szTime[128];

	CString szDateTime;

	GetDateFormat(LOCALE_USER_DEFAULT, 0, NULL, NULL, (LPSTR)&szTime, sizeof(szTime) / sizeof(TCHAR));
	szDateTime = szTime;

	szDateTime += TCHAR(' ');	
	GetTimeFormat(LOCALE_USER_DEFAULT, 0, NULL, NULL, (LPSTR)&szTime, sizeof(szTime) / sizeof(TCHAR));
	szDateTime += szTime;	 
	
	return szDateTime;
}

void COptions::initFavouritesMenu(CMenu *pMenu)
{	
	// Delete items first
	for (int i=0; i < 250; i++) 
	{
		if (!pMenu->DeleteMenu(3, MF_BYPOSITION))
			break;
	}

	// Add menu items for favourites
	for (i=0; i < 250; i++) 
	{
		if (m_aFavourites[i].szName.GetLength() == 0)
			break;

		pMenu->InsertMenu(i+3, MF_BYPOSITION, ID_MENU_FAVOURITES_001 + i, m_aFavourites[i].szName);				
	}
}

void COptions::initOpenersMenu(CMenu *pMenu)
{	
	// Delete items first
	for (int i=0; i < 99; i++) 
	{
		if (!pMenu->DeleteMenu(2, MF_BYPOSITION))
			break;
	}

	CString szTmp;

	// Add menu items for openers
	for (i=0; i < 99; i++) 
	{
		if (m_aOpeners[i].szName.GetLength() == 0)
			break;
		
		if (i <= 9)
			szTmp.Format("%s\tCtrl+%d", m_aOpeners[i].szName, i);
		else
			szTmp = m_aOpeners[i].szName;

		pMenu->InsertMenu(i+2, MF_BYPOSITION, ID_MENU_OPEN_CMD_001 + i, szTmp);
	}
}


void COptions::addFavourite()
{
	CIpscanDlg *pDlg = (CIpscanDlg *) AfxGetApp()->GetMainWnd();

	CQueryDlg dlg(pDlg);

	dlg.m_szCaption = "Add Favorite";
	dlg.m_szQueryText = "Enter the name of current favorite:";	

	CString szTmp;	

	pDlg->m_ip1.GetWindowText(szTmp);
	dlg.m_szDefaultUserText = szTmp + " - ";
	pDlg->m_ip2.GetWindowText(szTmp);
	dlg.m_szDefaultUserText += szTmp;

	szTmp = dlg.doQuery();
	
	if (szTmp.GetLength() == 0)
		return;

	// Find the empty slot and populate it
	for (int i=0; i < 250; i++)
	{
		if (m_aFavourites[i].szName.GetLength() == 0)
		{
			m_aFavourites[i].szName = szTmp;
			
			pDlg->m_ip1.GetWindowText(szTmp);			
			m_aFavourites[i].nIP1 = inet_addr(szTmp);

			pDlg->m_ip2.GetWindowText(szTmp);			
			m_aFavourites[i].nIP2 = inet_addr(szTmp);

			break;
		}
	}

	saveFavourites();
}

void COptions::deleteFavourite()
{
	CFavouriteDeleteDlg dlg;
	
	if (dlg.DoModal() == IDOK)
	{
		for (int i = dlg.m_nFavouriteIndex; i < 250; i++)
		{			
			m_aFavourites[i] = m_aFavourites[i+1];

			if (m_aFavourites[i].szName.GetLength() == 0)
				break;
		}

	}

	saveFavourites();
}


void COptions::removeSettingsFromRegistry()
{
	CWinApp *app = AfxGetApp();

	if (MessageBox(app->GetMainWnd()->m_hWnd, 
		"So, you want to wipe traces and remove Angry IP Scanner's settings from registry?\n\n"
		"Do you know that all your choosen options and settings will be lost?\n\n"
		"Are you sure?", "Wipe Traces", MB_ICONQUESTION | MB_YESNO | MB_DEFBUTTON2) == IDYES)
	{
		// Remove settings
		RegDeleteKey(HKEY_CURRENT_USER, "Software\\Angryziber\\ipscan");
		
		MessageBox(app->GetMainWnd()->m_hWnd, 
			"Settings are now deleted from registry.\n\n"
			"They will be reset next time you run Angry IP Scanner\n"
			"until that you can restore settings by selecting both\n"
			"\"Save\" menu items in Options menu.", "Info", MB_ICONINFORMATION | MB_OK);

		g_options->m_bAutoSave = FALSE;
	}
}
