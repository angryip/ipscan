// web_detect.cpp : Defines the entry point for the DLL application.
//

#include "stdio.h"
#include "winsock.h"
#include "windows.h"
#include "..\..\plugin.h"

#define PLUGIN_NAME			"Web Detect"
#define	PLUGIN_DESCRIPTION	"Web Detect 1.0\nThis plugin detects which Web Server is runnning on remote machines."
#define	PLUGIN_AUTHOR		"Angryziber"
#define	PLUGIN_WEBSITE		"http://www.angryziber.com/ipscan/"

// HTTP request string that does all the job :-)
#define HTTP_REQUEST_STRING	"HEAD /robots.txt HTTP/1.0\nHost: %s\n\n"
#define HTTP_PORT			80
#define HTTP_SERVER_HEADER	"Server:"
#define UNKNOWN_SERVER		"UNKNOWN"
#define RESPONSE_NO_CONNECTION		"N/A"

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

	MessageBox(hwndParent, "This plugin doesn't have any options yet!", "Web Detect Plugin", MB_ICONHAND | MB_OK);

	return TRUE;
}

// Init function
extern "C" __declspec(dllexport) BOOL Init()
{
	// This is a initialization function
	// It must be used to allocate internal memory
	// or do any other initialization stuff.
	
	// Winsock is initialized by Angry IP Scanner itself,
	// so we don't need to do this here

	return TRUE;	// Plugin will be rejected if it returns FALSE on initialization
}

// Finalize function
extern "C" __declspec(dllexport) BOOL Finalize()
{
	// This is a finalize function
	// It must be used to free any previously 
	// allocated memory or do any other finalization	
	
	// We don't need any finalization 

	return TRUE;
}

// Case-insensitive strstr()
char *stristr(char *str1, char *str2)
{
	char *ptr1, *ptr2;

	if (str1 == NULL)
		return NULL;
	if (str2 == NULL)
		return str1;

	while (*(ptr1 = str1)) 
	{
		for (ptr2=str2; *ptr1 && *ptr2 && tolower(*ptr1)==tolower(*ptr2); ptr1++, ptr2++);

		if (*ptr2 == 0)
			return str1;

		str1++;
	}
	return NULL;
}

// This function parses the server name from the response
void parseServerName(LPSTR szResponse, int nLength)
{
	char *szHeaderStart, *szHeaderEnd;

	// Find needed HTTP header 
	szHeaderStart = stristr(szResponse, HTTP_SERVER_HEADER);

	if (!szHeaderStart)
	{
		// Header not found
		strncpy(szResponse, UNKNOWN_SERVER, nLength);
		return;
	}

	// Skip whitespace symbol
	if (szHeaderStart[0] == ' ' || szHeaderStart[0] == '\t')
		szHeaderStart++;

	szHeaderEnd = min(strchr(szHeaderStart, '\n'), strchr(szHeaderStart, '\r'));
	
	szHeaderEnd[0] = NULL;	// Make it end

	strcpy(szResponse, (char *)(szHeaderStart + strlen(HTTP_SERVER_HEADER)));	// Skip header name
}

// The Scan function
extern "C" __declspec(dllexport) BOOL Scan(DWORD nIP, LPSTR szReturn, int nBufferLen)
{
	// This function does the actual scanning
	
	SOCKET hSocket; 

	// Initialize the socket
	hSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_IP);

	if (hSocket == INVALID_SOCKET)
	{
		// Return FALSE in case of an error
		return FALSE;
	}

	sockaddr_in sin;
	sin.sin_addr.S_un.S_addr = nIP;
	sin.sin_family = PF_INET;
	
	sin.sin_port = htons(HTTP_PORT);

	fd_set fd_read, fd_write, fd_error;
	timeval timeout;
	timeout.tv_sec = 3;		// TODO: Hardcoded, should be configurable
	timeout.tv_usec = 0;
	u_long nNonBlocking = 1;	

	// Set socket to non-blocking mode
	ioctlsocket(hSocket, FIONBIO, &nNonBlocking);

	BOOL bConnected = FALSE;

	// Estabilish a TCP connection
	connect(hSocket, (sockaddr*)&sin, sizeof(sin));

	fd_write.fd_array[0] = hSocket; fd_write.fd_count = 1;
	fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;
	if (select(0, 0, &fd_write, &fd_error, &timeout) > 0) 
	{
		if (fd_write.fd_count > 0)
		{
			// Connection successful
			bConnected = TRUE;
		}
	}			
	
	if (!bConnected)
	{
		strncpy(szReturn, RESPONSE_NO_CONNECTION, sizeof(RESPONSE_NO_CONNECTION));
	}
	else
	{
		// Send the magic http request
		if (send(hSocket, HTTP_REQUEST_STRING, strlen(HTTP_REQUEST_STRING), 0) == SOCKET_ERROR)
			return FALSE;

		// Receive http headers back
		char szResponse[512];

		fd_read.fd_array[0] = hSocket; fd_read.fd_count = 1;
		fd_error.fd_array[0] = hSocket; fd_error.fd_count = 1;

		char *szBufferPointer = (char*) &szResponse;

		while (select(0, &fd_read, 0, &fd_error, &timeout) > 0)
		{
			if (fd_read.fd_count > 0)
			{
				int nNumRead = recv(hSocket, szBufferPointer, sizeof(szResponse) - (szBufferPointer - (char*)&szResponse), 0);
				
				if (nNumRead <= 0)
					break;
				else
					szBufferPointer += nNumRead;
			}
			else
			{
				break;
			}
		}

		// Get the name of web server from response
		parseServerName((char*) &szResponse, sizeof(szResponse));
	
		_snprintf(szReturn, nBufferLen, szResponse);
	}

	closesocket(hSocket);	

	return TRUE;
}
