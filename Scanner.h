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

#include "plugin.h"		// Structures and defines for plugins

// Well-known columns
#define CL_STATIC_COUNT			2
#define CL_IP					0
#define CL_PING					1

// Second parameter to ThreadProc
#define IP_IS_GIVEN		TRUE
#define	INDEX_IS_GIVEN	FALSE


// Structure to hold all functions of a service (plugin)

typedef struct
{
	TScanFunction *pScanFunction;
	TInfoFunction *pInfoFunction;
	TOptionsFunction *pOptionsFunction;
	
	TInitFunction *pInitFunction;
	TFinalizeFunction *pFinalizeFunction;

	BOOL bBuiltinColumn;

	CString *pszPluginName;		
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
	BOOL runScanFunction(DWORD nIP, int nIndex, char *szBuffer, int nBufferLength, BOOL bGlobal=FALSE);
	void initMenuWithColumns(CMenu *pMenu);
	int m_nAliveHosts;
	int m_nOpenPorts;
	BOOL doScanPorts(DWORD nIP, CString &szResults, int nPingTime, int nThreadIndex);
	BOOL finalizeScanning();
	BOOL initScanning();
	void initListColumns(CScanListCtrl *cListCtrl);	
	int getColumnWidth(int nIndex);
	BOOL getColumnName(int nIndex, CString &szColumnHeader);
	int getColumnCount();
	BOOL doScanIP(DWORD nParam, BOOL bParameterIsIP, int nThreadIndex);
	CScanner();
	virtual ~CScanner();

	int m_Columns[256];		// It is updated by Select columns dialog box
	int m_nColumns;
	CArray<TScannerColumn, TScannerColumn&> m_AllColumns;			
	int m_nAllColumns;
protected:		
	
	CWinApp * m_app;
};

#endif // !defined(AFX_SCANNER_H__F6305E28_F29C_45F5_8073_26591A6C68D1__INCLUDED_)
