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

// Second parameter to ThreadProc
#define IP_IS_GIVEN		TRUE
#define	INDEX_IS_GIVEN	FALSE


// Function type definitions

typedef struct
{	
	char szColumnName[32];
	char szDescription[1024];
} 
TInfoStruct;

typedef BOOL (__cdecl TScanFunction)(DWORD nIP, LPSTR szReturn, int nBufferLen);
typedef BOOL (__cdecl TInfoFunction)(TInfoStruct *pInfoStruct);
typedef BOOL (__cdecl TSetupFunction)(HWND hwndParent);

typedef BOOL (__cdecl TInitFunction)();
typedef BOOL (__cdecl TFinalizeFunction)();

// Structure to hold all functions of a service (plugin)

typedef struct
{
	TScanFunction *pScanFunction;
	TInfoFunction *pInfoFunction;
	TSetupFunction *pSetupFunction;
	
	TInitFunction *pInitFunction;
	TFinalizeFunction *pFinalizeFunction;

	BOOL bBuiltinColumn;

	CString *pszColumnName;		
} 
TScannerColumn;


class CScanner  
{
public:	
	void runFinalizeFunction(int nIndex, BOOL bAllFunctions = FALSE);
	void runInitFunction(int nIndex, BOOL bAllFunctions = FALSE);
	void saveSelectedColumns();
	void loadSelectedColumns();
	void loadAllPossibleColumns();
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
	BOOL doScanIP(DWORD nParam, BOOL bParameterIsIP);
	CScanner();
	virtual ~CScanner();

	int m_Columns[256];		// It is updated by Select columns dialog box
	int m_nColumns;
	CArray<TScannerColumn, TScannerColumn&> m_AllColumns;			
	int m_nAllColumns;
protected:		
	
	CWinApp * m_app;
};

// ordinary function
UINT ThreadProcCallback(LPVOID nParam);
UINT ThreadProcCallbackRescan(LPVOID nParam);
UINT ScanningThread(DWORD nParam, BOOL bParameterIsIP);

#include "Options.h"

extern UINT g_nThreadCount;
extern HANDLE g_hThreads[10000];
extern CDialog * g_dlg;
extern CScanner * g_scanner;
extern COptions * g_options;
extern unsigned long g_nEndIP;
extern unsigned long g_nStartIP;
extern unsigned long g_nCurrentIP;

#endif // !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
