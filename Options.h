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

class COptions  
{
public:
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
	void setPortString(LPCSTR szPortString);
	CString m_szPorts;
	COptions();
	virtual ~COptions();
	void load();
	void save();
	BOOL parsePortString();
	
};

#endif // !defined(AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_)
