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

// globals.h: Global variables and stuff
//
//////////////////////////////////////////////////////////////////////

#include "Options.h"
#include "Scanner.h"

// ordinary function
UINT ThreadProcCallback(LPVOID nParam);
UINT ThreadProcCallbackRescan(LPVOID nParam);
UINT ScanningThread(DWORD nParam, BOOL bParameterIsIP);

#define MAX_THREAD_COUNT	1000
extern UINT g_nThreadCount;
extern int g_threads[MAX_THREAD_COUNT + 1];	// +1 is for safety :-)

#define THREAD_DEAD			0		// constants to be used with g_threads array
#define THREAD_MUST_DIE		1
#define THREAD_ALIVE		2

extern CDialog * g_dlg;
extern CScanner * g_scanner;
extern COptions * g_options;
extern unsigned long g_nEndIP;
extern unsigned long g_nStartIP;
extern unsigned long g_nCurrentIP;

extern CRITICAL_SECTION g_criticalSection;
