// Scanner.h: interface for the CScanner class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
#define AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ScanListCtrl.h"
#include <afxtempl.h>	// For CArray

// Well-known columns
#define CL_STATIC_COUNT			2
#define CL_IP					0
#define CL_PING					1

// Diplay Options
#define DO_ALL		0
#define DO_ALIVE	1
#define DO_OPENPORT	2


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
	int getColumnReference(int nItemIndex);
	BOOL getAllColumnName(int nIndex, CString &szColumnHeader);
	int getAllColumnsCount();
	void runScanFunction(DWORD nIP, int nIndex, char *szBuffer, int nBufferLength, BOOL bGlobal=FALSE);
	void initMenuWithColumns(CMenu *pMenu);
	int m_nAliveHosts;
	int m_nOpenPorts;
	BOOL doScanPorts(DWORD nIP, CString &szResults);
	BOOL finalizeScanning();
	BOOL initScanning();
	void initListColumns(CScanListCtrl *cListCtrl);	
	int getColumnWidth(int nIndex);
	BOOL getColumnName(int nIndex, CString &szColumnHeader);
	int getColumnCount();
	BOOL doScanIP(DWORD nItemIndex);
	CScanner();
	virtual ~CScanner();

	int m_Columns[256];		// It is updated by Select columns dialog box
	int m_nColumns;
protected:	
	CArray<TScannerColumn, TScannerColumn&> m_AllColumns;			
	int m_nAllColumns;
	
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
