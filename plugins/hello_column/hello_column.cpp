// hello_column.cpp : Defines the entry point for the DLL application.
//

#include "stdio.h"
#include "winsock.h"
#include "windows.h"
#include "..\..\plugin.h"

#define PLUGIN_NAME			"Hello!"
#define	PLUGIN_DESCRIPTION	"Hello World Example Plugin\nIt doesn't do anything useful :-)\n\nEnjoy!"
#define	PLUGIN_AUTHOR		"Angryziber"
#define	PLUGIN_WEBSITE		"http://www.angryziber.com/ipscan/"

// Info function
extern "C" __declspec(dllexport) BOOL Info(TInfoStruct *pInfoStruct)
{	
	// Check that the struct is not older than we are expecting
	if (pInfoStruct->nStructSize < sizeof(pInfoStruct))
		return FALSE;

	// Initialize structure
	pInfoStruct->nAngryIPScannerVersion = 216;		// Minimum version for this plugin to work
	pInfoStruct->nPluginType = PLUGIN_TYPE_COLUMN;	// This plugin will appear as a new column for scanning
	strncpy((char*)&pInfoStruct->szPluginName, PLUGIN_NAME, sizeof(pInfoStruct->szPluginName)); // Initialize column name
	strncpy((char*)&pInfoStruct->szDescription, PLUGIN_DESCRIPTION, sizeof(pInfoStruct->szDescription)); // Initialize description
	strncpy((char*)&pInfoStruct->szAuthorName, PLUGIN_AUTHOR, sizeof(pInfoStruct->szAuthorName)); 
	strncpy((char*)&pInfoStruct->szPluginWebsite, PLUGIN_WEBSITE, sizeof(pInfoStruct->szPluginWebsite)); 

	return TRUE;	// We have initialized structure successfully
}

// Options function
extern "C" __declspec(dllexport) BOOL Options(HWND hwndParent)
{	
	// This function must show a dialog box to user with
	// some options. As we don't have any options in this plugin
	// we could just omit this function, but we will show a MessageBox
	// instead to remind that this function could exist

	MessageBox(hwndParent, "This plugin doesn't have any options, sorry!", "Hello World Plugin", MB_ICONHAND | MB_OK);

	return TRUE;
}

// Init function
extern "C" __declspec(dllexport) BOOL Init()
{
	// This is a initialization function
	// It must be used to allocate internal memory
	// or do any other initialization stuff.
	// We don't need any initialization

	return TRUE;	// Plugin will be rejected if it returns FALSE on initialization
}

// Finalize function
extern "C" __declspec(dllexport) BOOL Finalize()
{
	// This is a finalize function
	// It must be used to free any previously 
	// allocated memory or do any other finalization	
	// We don't need any finalization either

	return TRUE;
}

// The Scan function - the most important
// It must be thread-safe!!! Be careful!
extern "C" __declspec(dllexport) BOOL Scan(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	// This function does the actual scanning
	// In our case we just return a string greeting the IP address we should scan
	in_addr in;
	in.S_un.S_addr = nIP;
	_snprintf(szReturn, nBufferLen, "Hello, %s!", inet_ntoa(in));
	return TRUE;
}

// That's all! :-)