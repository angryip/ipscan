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

// Options.h: interface for the COptions class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_)
#define AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

typedef int teDisplayOptions;
#define DISPLAY_ALL		0
#define DISPLAY_ALIVE	1
#define DISPLAY_OPEN	2

typedef struct {u_short nStartPort; u_short nEndPort; } tPortRange;

typedef	struct {CString szName; u_long nIP1; u_long nIP2; } tFavourite;

typedef	struct {CString szName; CString szExecute; CString szWorkDir; } tOpener;

class COptions  
{
public:
	BOOL m_bSkipBroadcast;
	BOOL m_bAutoSave;
	void removeSettingsFromRegistry();
	void saveFavourites();
	void saveOpeners();
	void deleteFavourite();
	void addFavourite();
	int m_nPortCount;
	BOOL m_bOptimizePorts;
	int m_nLanaNumber;
	CString getCurrentDate();
	int m_nPingCount;
	void saveDimensions();
	BOOL m_bShowPortsBelow;
	CString m_szExecutablePath;
	int m_nPortTimeout;
	BOOL m_bScanPorts;
	BOOL m_bScanHostIfDead;
	void setWindowPos();
	teDisplayOptions m_neDisplayOptions;
	int m_nPingTimeout;
	int m_nMaxThreads;
	int m_nTimerDelay;
	tPortRange *m_aParsedPorts;	// Array
	tFavourite m_aFavourites[250];
	tOpener m_aOpeners[100];
	BOOL setPortString(LPCSTR szPortString);
	CString m_szPorts;
	COptions();
	virtual ~COptions();
	void load();
	void save();
	BOOL parsePortString();
	void initFavouritesMenu(CMenu *pMenu);
	void initOpenersMenu(CMenu *pMenu);
	
protected:
	void loadFavourites();
	void loadOpeners();
};

#endif // !defined(AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_)
