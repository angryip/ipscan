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

// plugin.h: structures and definitions for Angry IP Scanner plugins
//
//////////////////////////////////////////////////////////////////////

// Possible plugin types (specify these in nPluginType field of TInfoStruct)

#define PLUGIN_TYPE_COLUMN			0		// New List Column (for scanning)
#define PLUGIN_TYPE_OUTPUT			1		// New output format (currently not supported)
#define PLUGIN_TYPE_IP_RANGE		2		// New possibility to specify IPs for scanning (currently not supported)

// Structure that is filled by Info plugin function

typedef struct
{	
	int	 nStructSize;				// Size of this structure in bytes, this is preset
	int	 nAngryIPScannerVersion;	// ex 216 instead of 2.16
	int	 nPluginType;				// Type of the plugin, see PLUGIN_TYPE_* constants
	char szPluginName[32];			// Column name in the list (plugin idenificator)
	char szDescription[1024];		// Description of plugin
	char szAuthorName[32];			// Author's name
	char szPluginWebsite[64];		// URL of plugin on the web
} 
TInfoStruct;


// Exported functions from plugins


// Common functions for all types of plugins

// Mandatory function
typedef BOOL (__cdecl TInfoFunction)(TInfoStruct *pInfoStruct);

// Optional functions (for all types of plugins)
// If they exist, they will be called
typedef BOOL (__cdecl TOptionsFunction)(HWND hwndParent);
typedef BOOL (__cdecl TInitFunction)();
typedef BOOL (__cdecl TFinalizeFunction)();

// Function for PLUGIN_TYPE_COLUMN
typedef BOOL (__cdecl TScanFunction)(DWORD nIP, LPSTR szReturn, int nBufferLen);
