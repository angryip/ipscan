// Options.cpp: implementation of the COptions class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "Options.h"
#include "IpscanDlg.h"

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
	app->WriteProfileInt("","PortTimeout",m_nPortTimeout);	
	app->WriteProfileInt("","DisplayOptions",m_neDisplayOptions);	
	app->WriteProfileString("", "PortString", m_szPorts);
	app->WriteProfileInt("", "ScanHostIfDead", m_bScanHostIfDead);
	app->WriteProfileInt("", "ScanPorts", m_bScanPorts);
	app->WriteProfileInt("", "ShowPortsBelow", m_bShowPortsBelow);
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
	m_nMaxThreads = app->GetProfileInt("","MaxThreads",100);	 			
	m_nPingTimeout = app->GetProfileInt("","Timeout",3000);
	m_nPortTimeout = app->GetProfileInt("","PortTimeout",3000);
	m_neDisplayOptions = app->GetProfileInt("","DisplayOptions",0);
	m_bScanHostIfDead = app->GetProfileInt("", "ScanHostIfDead", FALSE);
	m_bScanPorts = app->GetProfileInt("", "ScanPorts", FALSE);
	m_bShowPortsBelow = app->GetProfileInt("", "ShowPortsBelow", TRUE);
	
	setPortString(app->GetProfileString("", "PortString", ""));	// also parses it

	// Path, where the Angry IP Scanner resides
	m_szExecutablePath = __targv[0];
	int nTmp = m_szExecutablePath.ReverseFind('\\');
	m_szExecutablePath.Delete(nTmp, m_szExecutablePath.GetLength() - nTmp);	
}


void COptions::setWindowPos()
{
	CWinApp *app = AfxGetApp();

	RECT rc;

	rc.left = app->GetProfileInt("","Left",0);
	rc.top = app->GetProfileInt("","Top",0);
	rc.bottom = app->GetProfileInt("","Bottom",0);
	rc.right = app->GetProfileInt("","Right",0);

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


