// Scanner.h: interface for the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
#define AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ScanListCtrl.h"

// Function type definitions

typedef struct
{
	char szPluginName[96];	
	char szColumnName[32];	
} TInfoStruct;

typedef BOOL (__cdecl TScanFunction)(DWORD nIP, LPSTR szReturn, int nBufferLen);

typedef BOOL (__cdecl TInitFunction)();
typedef BOOL (__cdecl TFinalizeFunction)();

typedef BOOL (__cdecl TInfoFunction)(TInfoStruct *pInfoStruct);

// Structure to hold all functions of a service (plugin)

typedef struct
{
	TScanFunction *pScanFunction;
	TInfoFunction *pInfoFunction;
	
	TInitFunction *pInitFunction;
	TFinalizeFunction *pFinalizeFunction;

	CString *pszColumnName;
} TScannerColumn;


class CScanner  
{
public:	
	int m_nAliveHosts;
	int m_nOpenPorts;
	int doScanPorts(DWORD nIP, CString &szResults);
	BOOL finalizeScanning();
	BOOL initScanning();
	void initListColumns(CListCtrl *cListCtrl);	
	int getColumnWidth(int nIndex);
	BOOL getColumnName(int nIndex, CString &szColumnHeader);
	int getColumnCount();
	BOOL doScanIP(DWORD nItemIndex);
	CScanner();
	virtual ~CScanner();

protected:
	TScannerColumn m_Columns[100];
	int m_nColumnCount;

	CWinApp * m_app;
};

// ordinary function
UINT ScanningThread(LPVOID nItemIndex);

#include "Options.h"

extern UINT g_nThreadCount;
extern HANDLE g_hThreads[10000];
extern CDialog * g_dlg;
extern CScanner * g_scanner;
extern COptions * g_options;

#endif // !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
