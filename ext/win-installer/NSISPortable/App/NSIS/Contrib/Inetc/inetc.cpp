/*******************************************************
* FILE NAME: inetc.cpp
*
* Copyright 2004 - Present NSIS
*
* PURPOSE:
*    ftp/http file download plug-in
*    on the base of MS Inet API
*          4 GB limit (http support?)
*
* CHANGE HISTORY
*
* Author      Date  Modifications
* Takhir Bedertdinov
*     Nov 11, 2004  Original
*     Dec 17, 2004  Embedded edition -
*              NSISdl GUI style as default
*              (nsisdl.cpp code was partly used)
*     Dec 17, 2004  MSI Banner style
*     Feb 20, 2005  Resume download
*              feature for big files and bad connections
*     Mar 05, 2005  Proxy authentication
*              and /POPUP caption prefix option
*     Mar 25, 2005  Connect timeout option
*              and FTP switched to passive mode
*     Apr 18, 2005  Crack URL buffer size 
*              bug fixed (256->string_size)
*              HTTP POST added
*     Jun 06, 2005  IDOK on "Enter" key locked
*              POST HTTP header added 
*     Jun 22, 2005  non-interaptable mode /nocancel
*              and direct connect /noproxy
*     Jun 29, 2005  post.php written and tested
*     Jul 05, 2005  60 sec delay on WinInet detach problem
*              solved (not fine, but works including
*              installer exit and system reboot) 
*     Jul 08, 2005  'set foreground' finally removed
*     Jul 26, 2005  POPUP translate option
*     Aug 23, 2005  https service type in InternetConnect
*              and "ignore certificate" flags
*     Sep 30, 2005  https with bad certificate from old OS;
*              Forbidden handling
*     Dec 23, 2005  'put' entry point, new names, 12003 
*              ftp error handling (on ftp write permission)
*              405 http error (method not allowed)
*     Mar 12, 2006  Internal authorization via InternetErrorDlg()
*              and Unauthorized (401) handling.
*     Jun 10, 2006 Caption text option for Resume download
*              MessageBox
*     Jun 24, 2006 HEAD method, silent mode clean up
*     Sep 05, 2006 Center dialog code from Backland
*     Sep 07, 2006 NSISdl crash fix /Backland idea/
*     Sep 08, 2006 POST as dll entry point.
*     Sep 21, 2006 parent dlg progr.bar style and font, 
*              nocancel via ws_sysmenu
*     Sep 22, 2006 current lang IDCANCEL text, /canceltext
*              and /useragent options
*     Sep 24, 2006 .onInit improvements and defaults
*     Nov 11, 2006 FTP path creation, root|current dir mgmt
*     Jan 01, 2007 Global char[] cleanup, GetLastError() to 
*              status string on ERR_DIALOG, few MSVCRT replaces
*     Jan 13, 2007 /HEADER option added
*     Jan 28, 2007 _open -> CreateFile and related
*     Feb 18, 2007 Speed calculating improved (pauses),
*              /popup text parameter to hide URL
*     Jun 07, 2007 Local file truncation added for download
*              (CREATE_ALWAYS)
*     Jun 11, 2007 FTP download permitted even if server rejects
*              SIZE request (ProFTPD). 
*     Aug 11, 2007 Backland' fix for progress bar redraw/style
*              issue in NSISdl display mode.
*     Jan 09, 2008 {_trueparuex^}' fix - InternetSetFilePointer()
*              returns -1 on error.
*              /question option added for cancel question.
*     Feb 15, 2008 PUT content-length file size fix
*     Feb 17, 2008 char -> TCHAR replace for UNICODE option
*     Feb 19, 2008 janekschwarz fix for HTTP PUT with auth
*              CreateFile INVALID_HANDLE_VALUE on error fix
*     Feb 20, 2008 base64 encoder update for unicode
*     Feb 27, 2008 Unicode configurations added to VS 6 dsp
*     Mar 20, 2008 HTTP PUT with proxy auth finally fixed
*              FTP errors handling improved.
*              HEAD bug fixed
*     Mar 27, 2008 Details window hide/show in NSISdl mode
*     Apr 10, 2008 Auth test method changed to HEAD for
*              old proxy's
*     Apr 30, 2008 InternetErrorDlg() ERROR_SUCESS on cancel
*              click patched
*              3xx errors added to status list.
*     May 20, 2008 InternetReadFile on cable disconnect patched
*     May 20, 2008 Reply status "0" patch (name resolution?)
*     Jul 15, 2008 HTTP 304 parsing. Incorrect size reported fix.
*     Aug 21, 2009 Escape sequence convertion removed (caused 
*              error in signature with %2b requests)
*              Marqueue progess bar style for unknown file size.
*     Feb 04, 2010 Unicode POST patch - body converted to multibyte
*     Jul 11, 2010 /FILE POST option added
*     Nov 04, 2010 Disabled cookies and cache for cleanliness
*     Feb 14, 2011 Fixed reget bug introduced in previous commit
*     Feb 18, 2011 /NOCOOKIES option added
*     Mar 02, 2011 User-agent buffer increased. Small memory leak fix
*	  Mar 23, 2011 Use caption on embedded progressbar - zenpoy
*     Apr 05, 2011 reget fix - INTERNET_FLAG_RELOAD for first connect only
*     Apr 27, 2011 /receivetimeout option added for big files and antivirus
*     Jun 15, 2011 Stack clean up fix on cancel - zenpoy
*     Oct 19, 2011 FTP PUT error parsing fix - tperquin
*******************************************************/


#define _WIN32_WINNT 0x0500

#include <windows.h>
#include <tchar.h>
#include <wininet.h>
#include <commctrl.h>
#include "exdll.h"
#include "resource.h"

#ifndef PBM_SETMARQUEE
#define PBM_SETMARQUEE  (WM_USER + 10)
#define PBS_MARQUEE  0x08
#endif

// IE 4 safety and VS 6 compatibility
typedef BOOL (__stdcall *FTP_CMD)(HINTERNET,BOOL,DWORD,LPCTSTR,DWORD,HINTERNET *);
FTP_CMD myFtpCommand;

#define PLUGIN_NAME _T("Inetc plug-in")
#define INETC_USERAGENT _T("NSIS_Inetc (Mozilla)")
#define PB_RANGE 400 // progress bar values range
#define PAUSE1_SEC 2 // transfer error indication time, for reget only
#define PAUSE2_SEC 3 // paused state time, increase this if need (60?)
#define PAUSE3_SEC 1 // pause after resume button pressed
#define NOT_AVAILABLE 0xffffffff
#define POST_HEADER _T("Content-Type: application/x-www-form-urlencoded")
#define PUT_HEADER _T("Content-Type: octet-stream\nContent-Length: %d")
#define INTERNAL_OK 0xFFEE
#define PROGRESS_MS 1000 // screen values update interval
#define DEF_QUESTION _T("Are you sure that you want to stop download?")
#define HOST_AUTH_HDR  _T("Authorization: basic %s")
#define PROXY_AUTH_HDR _T("Proxy-authorization: basic %s")

//#define MY_CERT_FLAGS SECURITY_FLAG_IGNORE_UNKNOWN_CA | SECURITY_FLAG_IGNORE_REVOCATION | SECURITY_FLAG_IGNORE_CERT_DATE_INVALID | SECURITY_FLAG_IGNORE_CERT_CN_INVALID
#define MY_CERT_FLAGS SECURITY_FLAG_IGNORE_UNKNOWN_CA | SECURITY_FLAG_IGNORE_REVOCATION
#define MY_REDIR_FLAGS INTERNET_FLAG_IGNORE_REDIRECT_TO_HTTP | INTERNET_FLAG_IGNORE_REDIRECT_TO_HTTPS
#define MY_HTTPS_FLAGS (MY_CERT_FLAGS | MY_REDIR_FLAGS | INTERNET_FLAG_SECURE)

enum STATUS_CODES {
	ST_OK = 0,
	ST_CONNECTING,
	ST_DOWNLOAD,
	ST_CANCELLED,
	ST_URLOPEN,
	// ST_OPENING,
	ST_PAUSE,
	ERR_TERMINATED,
	ERR_DIALOG,
	ERR_INETOPEN,
	ERR_URLOPEN,
	ERR_TRANSFER,
	ERR_FILEOPEN,
	ERR_FILEWRITE,
	ERR_FILEREAD,
	ERR_REGET,
	ERR_CONNECT,
	ERR_OPENREQUEST,
	ERR_SENDREQUEST,
	ERR_CRACKURL,
	ERR_NOTFOUND,
	ERR_THREAD,
	ERR_PROXY,
	ERR_FORBIDDEN,
	ERR_NOTALLOWED,
	ERR_REQUEST,
	ERR_SERVER,
	ERR_AUTH,
	ERR_CREATEDIR,
	ERR_PATH,
	ERR_NOTMODIFIED,
	ERR_REDIRECTION
};


static TCHAR szStatus[][32] = {
	_T("OK"),_T("Connecting"),_T("Downloading"),_T("Cancelled"),_T("Connecting"), //_T("Opening URL")),
	_T("Reconnect Pause"),_T("Terminated"),_T("Dialog Error"),_T("Open Internet Error"),
	_T("Open URL Error"),_T("Transfer Error"),_T("File Open Error"),_T("File Write Error"),_T("File Read Error"),
	_T("Reget Error"),_T("Connection Error"),_T("OpenRequest Error"),_T("SendRequest Error"),
	_T("URL Parts Error"),_T("File Not Found (404)"),_T("CreateThread Error"),_T("Proxy Error (407)"),
	_T("Access Forbidden (403)"),_T("Not Allowed (405)"),_T("Request Error"),_T("Server Error"),
	_T("Unauthorized (401)"),_T("FtpCreateDir failed (550)"),_T("Error FTP path (550)"),_T("Not Modified"), 
	_T("Redirection")
};

HINSTANCE g_hInstance;
TCHAR fn[MAX_PATH]=_T(""),
*url = NULL,
*szAlias = NULL,
*szProxy = NULL,
*szHeader = NULL,
*szBanner = NULL,
*szQuestion = NULL,
szCancel[64]=_T(""),
szCaption[128]=_T(""),
szUserAgent[256]=_T(""),
szResume[256] = _T("Your internet connection seems to be not permitted or dropped out!\nPlease reconnect and click Retry to resume installation.");
CHAR *szPost = NULL,
post_fname[MAX_PATH] = "";
DWORD fSize = 0;

int status;
DWORD cnt = 0,
fs = 0,
timeout = 0,
receivetimeout = 0;
DWORD startTime, transfStart, openType;
bool silent, popup, resume, nocancel, noproxy, nocookies;

HWND childwnd;
HWND hDlg;
bool fput = false, fhead = false;


/*****************************************************
* FUNCTION NAME: sf(HWND)
* PURPOSE: 
*    moves HWND to top and activates it
* SPECIAL CONSIDERATIONS:
*    commented because annoying
*****************************************************/
/*
void sf(HWND hw)
{
DWORD ctid = GetCurrentThreadId();
DWORD ftid = GetWindowThreadProcessId(GetForegroundWindow(), NULL);
AttachThreadInput(ftid, ctid, TRUE);
SetForegroundWindow(hw);
AttachThreadInput(ftid, ctid, FALSE);
}
*/

static TCHAR szUrl[64] = _T("");
static TCHAR szDownloading[64] = _T("Downloading %s");
static TCHAR szConnecting[64] = _T("Connecting ...");
static TCHAR szSecond[64] = _T("second");
static TCHAR szMinute[32] = _T("minute");
static TCHAR szHour[32] = _T("hour");
static TCHAR szPlural[32] = _T("s");
static TCHAR szProgress[128] = _T("%dkB (%d%%) of %dkB @ %d.%01dkB/s");
static TCHAR szRemaining[64] = _T(" (%d %s%s remaining)");
static TCHAR szBasic[128] = _T("");
static TCHAR szAuth[128] = _T("");

// is it possible to make it working with unicode strings?

/* Base64 encode one byte */
static TCHAR encode(unsigned char u) {

	if(u < 26)  return _T('A')+u;
	if(u < 52)  return _T('a')+(u-26);
	if(u < 62)  return _T('0')+(u-52);
	if(u == 62) return _T('+');
	return _T('/');
}

TCHAR *encode_base64(int size, TCHAR *src, TCHAR *dst) {

	int i;
	TCHAR *p;

	if(!src)
		return NULL;

	if(!size)
		size= lstrlen(src);

	p = dst;

	for(i=0; i<size; i+=3) {

		unsigned char b1=0, b2=0, b3=0, b4=0, b5=0, b6=0, b7=0;

		b1 = (unsigned char)src[i];

		if(i+1<size)
			b2 = (unsigned char)src[i+1];

		if(i+2<size)
			b3 = (unsigned char)src[i+2];

		b4= b1>>2;
		b5= ((b1&0x3)<<4)|(b2>>4);
		b6= ((b2&0xf)<<2)|(b3>>6);
		b7= b3&0x3f;

		*p++= encode(b4);
		*p++= encode(b5);

		if(i+1<size) {
			*p++= encode(b6);
		} else {
			*p++= _T('=');
		}

		if(i+2<size) {
			*p++= encode(b7);
		} else {
			*p++= _T('=');
		}

	}

	return dst;
}

/*****************************************************
* FUNCTION NAME: fileTransfer()
* PURPOSE: 
*    http/ftp file transfer itself
*    for any protocol and both directions I guess
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
void fileTransfer(HANDLE localFile,
									HINTERNET hFile)
{
	byte data_buf[1024*8];
	byte *dw;
	DWORD rslt = 0;
	DWORD bytesDone;

	status = ST_DOWNLOAD;
	while(status == ST_DOWNLOAD)
	{
		if(fput)
		{
			if(!ReadFile(localFile, data_buf, rslt = sizeof(data_buf), &bytesDone, NULL))
			{
				status = ERR_FILEREAD;
				break;
			}
			if(bytesDone == 0) // EOF reached
			{
				status = ST_OK;
				break;
			}
			while(bytesDone > 0)
			{
				dw = data_buf;
				if(!InternetWriteFile(hFile, dw, bytesDone, &rslt) || rslt == 0)
				{
					status = ERR_TRANSFER;
					break;
				}
				dw += rslt;
				cnt += rslt;
				bytesDone -= rslt;
			}
		}
		else
		{
			if(!InternetReadFile(hFile, data_buf, sizeof(data_buf), &rslt))
			{
				status = ERR_TRANSFER;
				break;
			}
			if(rslt == 0) // EOF reached or cable disconnect
			{
// on cable disconnect returns TRUE and 0 bytes. is cnt == 0 OK (zero file size)?
// cannot check this if reply is chunked (no content-length, http 1.1)
				status = (fs != NOT_AVAILABLE && cnt < fs) ? ERR_TRANSFER : ST_OK;
				break;
			}
			if(!WriteFile(localFile, data_buf, rslt, &bytesDone, NULL) ||
				rslt != bytesDone)
			{
				status = ERR_FILEWRITE;
				break;
			}
			cnt += rslt;
		}
	}
}

/*****************************************************
* FUNCTION NAME: mySendRequest()
* PURPOSE: 
*    HttpSendRequestEx() sends headers only - for PUT
*    We also can use InetWriteFile for POST body I guess
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
int mySendRequest(HINTERNET hFile)
{
	INTERNET_BUFFERS BufferIn = {0};
	if(fput)
	{
		BufferIn.dwStructSize = sizeof( INTERNET_BUFFERS );
		BufferIn.dwBufferTotal = fs;
		return HttpSendRequestEx( hFile, &BufferIn, NULL, HSR_INITIATE, 0);
	}
	return HttpSendRequest(hFile, NULL, 0, szPost, fSize);
}

/*****************************************************
* FUNCTION NAME: queryStatus()
* PURPOSE: 
*    http status code comes before download (get) and
*    after upload (put), so this is called from 2 places
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
bool queryStatus(HINTERNET hFile)
{
	TCHAR buf[256] = _T("");
	DWORD rslt;
	if(HttpQueryInfo(hFile, HTTP_QUERY_STATUS_CODE,
		buf, &(rslt = sizeof(buf)), NULL))
	{
		buf[3] = 0;
		if(lstrcmp(buf, _T("0")) == 0 || *buf == 0)
			status = ERR_SENDREQUEST;
		else if(lstrcmp(buf, _T("401")) == 0)
			status = ERR_AUTH;
		else if(lstrcmp(buf, _T("403")) == 0)
			status = ERR_FORBIDDEN;
		else if(lstrcmp(buf, _T("404")) == 0)
			status = ERR_NOTFOUND;
		else if(lstrcmp(buf, _T("407")) == 0)
			status = ERR_PROXY;
		else if(lstrcmp(buf, _T("405")) == 0)
			status = ERR_NOTALLOWED;
		else if(lstrcmp(buf, _T("304")) == 0)
			status = ERR_NOTMODIFIED;
		else if(*buf == _T('3'))
		{
			status = ERR_REDIRECTION;
			wsprintf(szStatus[status] + lstrlen(szStatus[status]), _T(" (%s)"), buf);
		}
		else if(*buf == _T('4'))
		{
			status = ERR_REQUEST;
			wsprintf(szStatus[status] + lstrlen(szStatus[status]), _T(" (%s)"), buf);
		}
		else if(*buf == _T('5'))
		{
			status = ERR_SERVER;
			wsprintf(szStatus[status] + lstrlen(szStatus[status]), _T(" (%s)"), buf);
		}
		return true;
	}
	return false;
}

/*****************************************************
* FUNCTION NAME: openFtpFile()
* PURPOSE: 
*    control connection, size request, re-get lseek
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
HINTERNET openFtpFile(HINTERNET hConn,
											TCHAR *path)
{
	TCHAR buf[256] = _T(""), *movp;
	HINTERNET hFile;
	DWORD rslt, err, gle;
	bool https_req_ok = false;

	/* reads connection / auth responce info and cleares 'control' buffer this way */
	InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf)));
	if(cnt == 0)
	{
		if(!fput) // we know local file size already
		{
			/* too clever myFtpCommand returnes false on the valid _T("550 Not found/Not permitted" server answer,
			to read answer I had to ignory returned false (!= 999999) :-( 
			GetLastError also possible, but MSDN description of codes is very limited */
			wsprintf(buf, _T("SIZE %s"), path + 1);
			if(myFtpCommand != NULL &&
				myFtpCommand(hConn, false, FTP_TRANSFER_TYPE_ASCII, buf, 0, &hFile) != 9999 &&
				memset(buf, 0, sizeof(buf)) != NULL &&
				InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf))))
			{
				if(_tcsstr(buf, _T("213 ")))
				{
					fs = _tcstol(_tcschr(buf, _T(' ')) + 1, NULL, 0);
				}
				/* stupid ProFTPD returns error on SIZE request. let's continue without size.
				But IE knows some trick to get size from ProFTPD......
				else if(_tcsstr(buf, _T("550 _T("))
				{
				status = ERR_SIZE_NOT_PERMITTED;
				return NULL;
				}
				*/
			}
			if(fs == 0)
			{
				fs = NOT_AVAILABLE;
			}

		}
	}
	else
	{
		wsprintf(buf, _T("REST %d"), cnt);
		if(myFtpCommand == NULL ||
			!myFtpCommand(hConn, false, FTP_TRANSFER_TYPE_BINARY, buf, 0, &hFile) ||
			memset(buf, 0, sizeof(buf)) == NULL ||
			!InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf))) ||
			(_tcsstr(buf, _T("350")) == NULL && _tcsstr(buf, _T("110")) == NULL))
		{
			status = ERR_REGET;
			return NULL;
		}
	}
	if((hFile = FtpOpenFile(hConn, path + 1, fput ? GENERIC_WRITE : GENERIC_READ,
		FTP_TRANSFER_TYPE_BINARY|INTERNET_FLAG_RELOAD,0)) == NULL)
	{
		gle = GetLastError();
		*buf = 0;
		InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf)));
		// wrong path - dir may not exist or upload may be not allowed
		// we use ftp://host//path (double /) to define path from FS root
		if(fput && (_tcsstr(buf, _T("550")) != NULL ||  _tcsstr(buf, _T("553")) != NULL))
		{

			movp = path + 1;
			if(*movp == _T('/')) movp++; // don't need to creat root
			while(_tcschr(movp, _T('/')))
			{
				*_tcschr(movp,_T('/')) = 0;
				FtpCreateDirectory(hConn, path + 1);
				InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf)));
				*(movp + lstrlen(movp)) = _T('/');
				movp = _tcschr(movp, _T('/')) + 1;
			}
			if(status != ERR_CREATEDIR &&
				(hFile = FtpOpenFile(hConn, path + 1, GENERIC_WRITE,
				FTP_TRANSFER_TYPE_BINARY|INTERNET_FLAG_RELOAD,0)) == NULL)
			{
				status = ERR_PATH;
				if(InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf))))
					lstrcpyn(szStatus[status], _tcsstr(buf, _T("550")), sizeof(szStatus[0]) / sizeof(TCHAR));
			}         
		}
		// may be firewall related error, let's give user time to disable it
		else if(gle == 12003)
		{
			if(_tcsstr(buf, _T("550")))
			{
				status = ERR_NOTFOUND;
				lstrcpyn(szStatus[status], _tcsstr(buf, _T("550")), sizeof(szStatus[0]) / sizeof(TCHAR));
			}
			else
			{
				lstrcpyn(szStatus[status], buf, sizeof(szStatus[0]) / sizeof(TCHAR));
			}
		}
		// timeout (firewall or dropped connection problem)
		else if(gle == 12002)
		{
			if(!silent)
				resume = true;
			status = ERR_URLOPEN;
		}
	}
	else InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf)));
	return hFile;
}


/*****************************************************
* FUNCTION NAME: openHttpFile()
* PURPOSE: 
*    file open, size request, re-get lseek
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
HINTERNET openHttpFile(HINTERNET hConn,
											 INTERNET_SCHEME nScheme,
											 TCHAR *path)
{
	TCHAR buf[256] = _T("");
	HINTERNET hFile;
	DWORD rslt, err;
	bool first_attempt = true;;

// test connection for PUT, the only way to do this before sending data
// OPTIONS fails on HttpOpenRequest step for HTTPS
// but works for HEAD I guess
	if(fput)// && nScheme != INTERNET_SCHEME_HTTPS)
	{
// old proxy's may not support OPTIONS request, so changed to HEAD....
		if((hFile = HttpOpenRequest(hConn, _T("HEAD"), path, NULL, NULL, NULL,
//		if((hFile = HttpOpenRequest(hConn, _T("OPTIONS"), path, NULL, NULL, NULL,
			INTERNET_FLAG_RELOAD | INTERNET_FLAG_KEEP_CONNECTION |
			(nocookies ? (INTERNET_FLAG_NO_CACHE_WRITE | INTERNET_FLAG_NO_COOKIES) : 0), 0)) != NULL)
		{
			if(*szAuth)
			{
				wsprintf(buf, PROXY_AUTH_HDR, szAuth);
				HttpAddRequestHeaders(hFile, buf, -1,
					HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			}
resend_proxy1:
			if(*szBasic)
			{
				wsprintf(buf, HOST_AUTH_HDR, szBasic);
				HttpAddRequestHeaders(hFile, buf, -1,
					HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			}
resend_auth1:
			if(HttpSendRequest(hFile, NULL, 0, NULL, 0))
			{
				queryStatus(hFile);
// may be don't need to read all from socket, but this looks safer
				while(InternetReadFile(hFile, buf, sizeof(buf), &rslt) && rslt > 0) {}
				if(!silent && (status == ERR_PROXY || status == ERR_AUTH))// ||  status == ERR_FORBIDDEN))
				{
					rslt = InternetErrorDlg(hDlg, hFile,
						ERROR_INTERNET_INCORRECT_PASSWORD,
						FLAGS_ERROR_UI_FILTER_FOR_ERRORS | 
						FLAGS_ERROR_UI_FLAGS_GENERATE_DATA |
						FLAGS_ERROR_UI_FLAGS_CHANGE_OPTIONS,
						NULL);
					if (rslt == ERROR_INTERNET_FORCE_RETRY)
					{
						status = ST_URLOPEN;
						if(status == ERR_PROXY) goto resend_proxy1;
						else goto resend_auth1;
					}
					else
					{
						status = ST_CANCELLED;
					}
					
				}
				// no such file is OK for PUT. server first checks authentication
				if(status == ERR_NOTFOUND || status == ERR_FORBIDDEN || status == ERR_NOTALLOWED)
				{
//					MessageBox(childwnd, _T("NOT_FOUND"), "", 0);
					status = ST_URLOPEN;
				}
				// parameters might be updated during dialog popup
				if(status == ST_URLOPEN)
				{
					*buf = 0;
					if(HttpQueryInfo(hFile, HTTP_QUERY_AUTHORIZATION, buf, &(rslt = sizeof(buf)), NULL) && *buf)
						lstrcpyn(szBasic, buf, rslt);
					*buf = 0;
					if(HttpQueryInfo(hFile, HTTP_QUERY_PROXY_AUTHORIZATION, buf, &(rslt = sizeof(buf)), NULL) && *buf)
						lstrcpyn(szAuth, buf, rslt);
				}
			}
			else status = ERR_SENDREQUEST;
			InternetCloseHandle(hFile);
		}
		else status = ERR_OPENREQUEST;
	}
// request itself
	if(status == ST_URLOPEN)
	{
		if((hFile = HttpOpenRequest(hConn, fput ? _T("PUT") : (fhead ? _T("HEAD") : (szPost ? _T("POST") : NULL)),
			path, NULL, NULL, NULL,
			// INTERNET_FLAG_RELOAD conflicts with reget - hidden re-read from beginning has place
			// INTERNET_FLAG_RESYNCHRONIZE // note - sync may not work with some http servers
			// reload on first connect (and any req. except GET), just continue on resume.
			// HTTP Proxy still is a problem for reget
			(cnt ? 0 : INTERNET_FLAG_RELOAD)
			| INTERNET_FLAG_KEEP_CONNECTION |
			(nocookies ? (INTERNET_FLAG_NO_CACHE_WRITE | INTERNET_FLAG_NO_COOKIES) : 0) | 
			(nScheme == INTERNET_SCHEME_HTTPS ? MY_HTTPS_FLAGS : 0), 0)) != NULL)
		{
			if(*szAuth)
			{
				wsprintf(buf, PROXY_AUTH_HDR, szAuth);
				HttpAddRequestHeaders(hFile, buf, -1,
					HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			}
resend_proxy2:
			if(szPost != NULL)
				HttpAddRequestHeaders(hFile, POST_HEADER,
				-1, HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			if(*post_fname)
				HttpAddRequestHeadersA(hFile, post_fname,
				-1, HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			if(szHeader != NULL)
				HttpAddRequestHeaders(hFile, szHeader, -1,
				HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			if(*szBasic)
			{
				wsprintf(buf, HOST_AUTH_HDR, szBasic);
				HttpAddRequestHeaders(hFile, buf, -1,
					HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			}
			if(fput)
			{
				wsprintf(buf, PUT_HEADER, fs);
				HttpAddRequestHeaders(hFile, buf, -1,
					HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
			}
resend_auth2:
			first_attempt = true;
			if(nScheme == INTERNET_SCHEME_HTTPS)
			{
				if(!mySendRequest(hFile))
				{
					InternetQueryOption (hFile, INTERNET_OPTION_SECURITY_FLAGS,
						(LPVOID)&rslt, &(err = sizeof(rslt)));
					rslt |= MY_CERT_FLAGS;
					InternetSetOption (hFile, INTERNET_OPTION_SECURITY_FLAGS,
						&rslt, sizeof(rslt) );
				}
				else first_attempt = false;
			}
// https Request answer may be after optional second Send only on Win98
			if(!first_attempt || mySendRequest(hFile))
			{
// no status for PUT - headers were sent only. And not need to get size / set position
				if(!fput)
				{
					queryStatus(hFile);
					if(!silent && (status == ERR_PROXY || status == ERR_AUTH))
					{
						rslt = InternetErrorDlg(hDlg, hFile,
							ERROR_INTERNET_INCORRECT_PASSWORD,
							FLAGS_ERROR_UI_FILTER_FOR_ERRORS | 
							FLAGS_ERROR_UI_FLAGS_GENERATE_DATA |
							FLAGS_ERROR_UI_FLAGS_CHANGE_OPTIONS,
							NULL);
						if (rslt == ERROR_INTERNET_FORCE_RETRY)
						{
							status = ST_URLOPEN;
							if(status == ERR_PROXY) goto resend_proxy2;
							else goto resend_auth2;
						}
						else
							status = ST_CANCELLED;
						
					}
// get size / set position
					if(status == ST_URLOPEN)
					{
						if(cnt == 0)
						{
							if(HttpQueryInfo(hFile, HTTP_QUERY_CONTENT_LENGTH, buf,
								&(rslt = sizeof(buf)), NULL))
								fs = _tcstoul(buf, NULL, 0);
							else fs = NOT_AVAILABLE;
						}
						else
						{
							if((int)InternetSetFilePointer(hFile, cnt, NULL, FILE_BEGIN, 0) == -1)
								status = ERR_REGET;
						}
					}
				}
				
			}
			else
			{
				if(!queryStatus(hFile))
					status = ERR_SENDREQUEST;
			}
		}
		else status = ERR_OPENREQUEST;
	}
	return hFile;
}

/*****************************************************
* FUNCTION NAME: inetTransfer()
* PURPOSE: 
*    http/ftp file transfer
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
DWORD __stdcall inetTransfer(void *hw)
{
	HINTERNET hSes, hConn, hFile;
	HINSTANCE hInstance = NULL;
	HANDLE localFile = NULL;
	HWND hDlg = (HWND)hw;
	DWORD lastCnt, rslt, err;
	TCHAR hdr[2048];
	TCHAR *host = (TCHAR*)GlobalAlloc(GPTR, g_stringsize * sizeof(TCHAR)),
		*path = (TCHAR*)GlobalAlloc(GPTR, g_stringsize * sizeof(TCHAR)),
		*params = (TCHAR*)GlobalAlloc(GPTR, g_stringsize * sizeof(TCHAR)),
		*user = (TCHAR*)GlobalAlloc(GPTR, g_stringsize * sizeof(TCHAR)),
		*passwd = (TCHAR*)GlobalAlloc(GPTR, g_stringsize * sizeof(TCHAR));

	URL_COMPONENTS uc = {sizeof(URL_COMPONENTS), NULL, 0,
		(INTERNET_SCHEME)0, host, g_stringsize, 0 , user, g_stringsize,
		passwd, g_stringsize, path, g_stringsize, params, g_stringsize};

	if((hSes = InternetOpen(szUserAgent, openType, szProxy, NULL, 0)) != NULL)
	{
		if(InternetQueryOption(hSes, INTERNET_OPTION_CONNECTED_STATE, &(rslt=0),
			&(lastCnt=sizeof(DWORD))) &&
			(rslt & INTERNET_STATE_DISCONNECTED_BY_USER))
		{
			INTERNET_CONNECTED_INFO ci = {INTERNET_STATE_CONNECTED, 0};
			InternetSetOption(hSes, 
				INTERNET_OPTION_CONNECTED_STATE, &ci, sizeof(ci));
		}
		if(timeout > 0)
			lastCnt = InternetSetOption(hSes, INTERNET_OPTION_CONNECT_TIMEOUT, &timeout, sizeof(timeout));
		if(receivetimeout > 0)
			InternetSetOption(hSes, INTERNET_OPTION_RECEIVE_TIMEOUT, &receivetimeout, sizeof(receivetimeout));
		// 60 sec WinInet.dll detach delay on socket time_wait fix
		//      if(hInstance = GetModuleHandle("wininet.dll"))
		if(hInstance = LoadLibrary(_T("wininet.dll")))
			myFtpCommand = (FTP_CMD)GetProcAddress(hInstance,
#ifdef UNICODE
			"FtpCommandW"
#else
			"FtpCommandA"
#endif
			);
		while(!popstring(url) && lstrcmpi(url, _T("/end")) != 0)
		{
			// too many customers requested not to do this
			//         sf(hDlg);
			if(popstring(fn) != 0 || lstrcmpi(url, _T("/end")) == 0) break;
			status = ST_CONNECTING;
			cnt = fs = *host = *user = *passwd = *path = *params = 0;
			PostMessage(hDlg, WM_TIMER, 1, 0); // show url & fn, do it sync
			if((localFile = CreateFile(fn, fput ? GENERIC_READ : GENERIC_WRITE, FILE_SHARE_READ,
				NULL, fput ? OPEN_EXISTING : CREATE_ALWAYS, 0, NULL)) != INVALID_HANDLE_VALUE)
			{
				uc.dwHostNameLength = uc.dwUserNameLength = uc.dwPasswordLength =
					uc.dwUrlPathLength = uc.dwExtraInfoLength = g_stringsize;
				if(fput)
				{
					fs = GetFileSize(localFile, NULL);
				}
				if(InternetCrackUrl(url, 0, 0/*ICU_ESCAPE*/ , &uc))
				{
					// auth headers for HTTP PUT seems to be lost, preparing encoded login:password
					if(*user && *passwd)
					{
						wsprintf(hdr, _T("%s:%s"), user, passwd);
						// does unicode version of encoding works correct?
						// are user and passwd ascii only?
						encode_base64(lstrlen(hdr), hdr, szBasic);
						*hdr = 0;
					}
					lstrcat(path, params);
					transfStart = GetTickCount();
					do
					{
						// re-PUT to already deleted tmp file on http server is not possible.
						// the same with POST - must re-send data to server. for 'resume' loop
						if((fput && uc.nScheme != INTERNET_SCHEME_FTP) || szPost)
						{
							cnt = 0;
							SetFilePointer(localFile, 0, NULL, SEEK_SET);
						}
						status = ST_CONNECTING;
						lastCnt = cnt;
						if((hConn = InternetConnect(hSes, host, uc.nPort,
							lstrlen(user) > 0 ? user : NULL,
							lstrlen(passwd) > 0 ? passwd : NULL,
							uc.nScheme == INTERNET_SCHEME_FTP ? INTERNET_SERVICE_FTP : INTERNET_SERVICE_HTTP,
							uc.nScheme == INTERNET_SCHEME_FTP ? INTERNET_FLAG_PASSIVE : 0, 0)) != NULL)
						{
							status = ST_URLOPEN;
							hFile = uc.nScheme == INTERNET_SCHEME_FTP ?
								openFtpFile(hConn, path) : openHttpFile(hConn, uc.nScheme, path);
							if(status != ST_URLOPEN && hFile != NULL)
							{
								InternetCloseHandle(hFile);
								hFile = NULL;
							}
							if(hFile != NULL)
							{
								if(fhead)
								{// repeating calls clear headers..
									if(HttpQueryInfo(hFile, HTTP_QUERY_RAW_HEADERS_CRLF, hdr, &(rslt=2048), NULL))
										WriteFile(localFile, hdr, rslt, &lastCnt, NULL);
									status = ST_OK;
								}
								else
								{
									HWND hBar = GetDlgItem(hDlg, IDC_PROGRESS1);
									SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETPOS, 0, 0);
									SetWindowText(GetDlgItem(hDlg, IDC_STATIC5), fs == NOT_AVAILABLE ? _T("Not Available") : _T(""));
									SetWindowText(GetDlgItem(hDlg, IDC_STATIC4), fs == NOT_AVAILABLE ? _T("Unknown") : _T(""));
									SetWindowLong(hBar, GWL_STYLE, fs == NOT_AVAILABLE ?
										(GetWindowLong(hBar, GWL_STYLE) | PBS_MARQUEE) : (GetWindowLong(hBar, GWL_STYLE) & ~PBS_MARQUEE)); 
									SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETMARQUEE, (WPARAM)(fs == NOT_AVAILABLE ? 1 : 0), (LPARAM)50 );
									fileTransfer(localFile, hFile);
									if(fput && uc.nScheme != INTERNET_SCHEME_FTP)
									{
										rslt = HttpEndRequest(hFile, NULL, 0, 0);
										queryStatus(hFile);
									}
								}
								InternetCloseHandle(hFile);
							}
							InternetCloseHandle(hConn);
						}
						else
						{
							status = ERR_CONNECT;
							if(uc.nScheme == INTERNET_SCHEME_FTP &&
								InternetGetLastResponseInfo(&err, hdr, &(rslt = sizeof(hdr))) &&
								_tcsstr(hdr, _T("530")))
							{
								lstrcpyn(szStatus[status], _tcsstr(hdr, _T("530")), sizeof(szStatus[0]) / sizeof(TCHAR));
							}
							else
							{
								rslt = GetLastError();
								if((rslt == 12003 || rslt == 12002) && !silent)
									resume = true;
							}
						}
					} while(((!fput || uc.nScheme == INTERNET_SCHEME_FTP) &&
						cnt > lastCnt &&
						status == ERR_TRANSFER &&
						SleepEx(PAUSE1_SEC * 1000, false) == 0 &&
						(status = ST_PAUSE) != ST_OK &&
						SleepEx(PAUSE2_SEC * 1000, false) == 0)
						|| (resume &&
						status != ST_OK &&
						status != ST_CANCELLED &&
						status != ERR_NOTFOUND &&
						ShowWindow(hDlg, SW_HIDE) != -1 &&
						MessageBox(GetParent(hDlg), szResume, *szCaption ? szCaption : PLUGIN_NAME, MB_RETRYCANCEL|MB_ICONWARNING) == IDRETRY &&
						(status = ST_PAUSE) != ST_OK &&
						ShowWindow(hDlg, silent ? SW_HIDE : SW_SHOW) == false &&
						SleepEx(PAUSE3_SEC * 1000, false) == 0));
				}
				else status = ERR_CRACKURL;
				CloseHandle(localFile);
				if(!fput && status != ST_OK)
				{
					rslt = DeleteFile(fn);
					break;
				}
			}
			else status = ERR_FILEOPEN;
		}
		InternetCloseHandle(hSes);
		if (lstrcmpi(url, _T("/end"))==0)
			pushstring(url);
	}
	else status = ERR_INETOPEN;
	GlobalFree(host);
	GlobalFree(path);
	GlobalFree(user);
	GlobalFree(passwd);
	GlobalFree(params);
	if(IsWindow(hDlg))
		PostMessage(hDlg, WM_COMMAND, MAKELONG(IDOK, INTERNAL_OK), 0);
	return status;
}

/*****************************************************
* FUNCTION NAME: fsFormat()
* PURPOSE: 
*    formats DWORD (max 4 GB) file size for dialog, big MB
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
void fsFormat(DWORD bfs,
							TCHAR *b)
{
	if(bfs == NOT_AVAILABLE)
		lstrcpy(b, _T("???"));
	else if(bfs == 0)
		lstrcpy(b, _T("0"));
	else if(bfs < 10 * 1024)
		wsprintf(b, _T("%u bytes"), bfs);
	else if(bfs < 10 * 1024 * 1024)
		wsprintf(b, _T("%u kB"), bfs / 1024);
	else wsprintf(b, _T("%u MB"), (bfs / 1024 / 1024));
}


/*****************************************************
* FUNCTION NAME: progress_callback
* PURPOSE: 
*    old-style progress bar text updates
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/

void progress_callback(void)
{
	TCHAR buf[1024] = _T(""), b[1024] = _T("");
	int time_sofar = max(1, (GetTickCount() - transfStart) / 1000);
	int bps = cnt / time_sofar;
	int remain = (cnt > 0 && fs != NOT_AVAILABLE) ? (MulDiv(time_sofar, fs, cnt) - time_sofar) : 0;
	TCHAR *rtext=szSecond;
	if(remain < 0) remain = 0;
	if (remain >= 60)
	{
		remain/=60;
		rtext=szMinute;
		if (remain >= 60)
		{
			remain/=60;
			rtext=szHour;
		}
	}
	wsprintf(buf,
		szProgress,
		cnt/1024,
		fs > 0 && fs != NOT_AVAILABLE ? MulDiv(100, cnt, fs) : 0,
		fs != NOT_AVAILABLE ? fs/1024 : 0,
		bps/1024,((bps*10)/1024)%10
		);
	if (remain) wsprintf(buf + lstrlen(buf),
		szRemaining,
		remain,
		rtext,
		remain==1?_T(""):szPlural
		);
	SetDlgItemText(hDlg, IDC_STATIC1, (cnt == 0 || status == ST_CONNECTING) ? szConnecting : buf);
	if(fs > 0 && fs != NOT_AVAILABLE)
		SendMessage(GetDlgItem(hDlg, IDC_PROGRESS1), PBM_SETPOS, MulDiv(cnt, PB_RANGE, fs), 0);
	if (*szCaption == 0)
		wsprintf(buf, szDownloading,
			_tcschr(fn, _T('\\')) ? _tcsrchr(fn, _T('\\')) + 1 : fn);
	else wsprintf(buf, _T("%s"),szCaption);
	HWND hwndS = GetDlgItem(childwnd, 1006);
	if(!silent && hwndS != NULL && IsWindow(hwndS))
	{
		GetWindowText(hwndS, b, sizeof(b));
		if(lstrcmp(b, buf) != 0)
			SetWindowText(hwndS, buf);
	}
}

/*****************************************************
* FUNCTION NAME: onTimer()
* PURPOSE: 
*    updates text fields every second
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
void onTimer(HWND hDlg)
{
	TCHAR b[128];
	DWORD ct = (GetTickCount() - transfStart) / 1000,
		tt = (GetTickCount() - startTime) / 1000;
	// dialog window caption
	wsprintf(b,  _T("%s - %s"), *szCaption ? szCaption : PLUGIN_NAME, szStatus[status]);
	if(fs > 0 && fs != NOT_AVAILABLE && status == ST_DOWNLOAD)
	{
		wsprintf(b + lstrlen(b), _T(" %d%%"), MulDiv(100, cnt, fs));
	}
	if(szBanner == NULL) SetWindowText(hDlg, b);
	// current file and url
	SetDlgItemText(hDlg, IDC_STATIC1, (szAlias && *szAlias) ? szAlias : url);
	SetDlgItemText(hDlg, IDC_STATIC2, /*_tcschr(fn, '\\') ? _tcsrchr(fn, '\\') + 1 : */fn);
	// bytes done and rate
	if(cnt > 0)
	{
		fsFormat(cnt, b);
		if(ct > 1 && status == ST_DOWNLOAD)
		{
			lstrcat(b, _T("   ( "));
			fsFormat(cnt / ct, b + lstrlen(b));
			lstrcat(b, _T("/sec )"));
		}
	}
	else *b = 0;
	SetDlgItemText(hDlg, IDC_STATIC3, b);
	// total download time
	wsprintf(b, _T("%d:%02d:%02d"), tt / 3600, (tt / 60) % 60, tt % 60);
	SetDlgItemText(hDlg, IDC_STATIC6, b);
	// file size, time remaining, progress bar
	if(fs > 0 && fs != NOT_AVAILABLE)
	{
		fsFormat(fs, b);
		SetDlgItemText(hDlg, IDC_STATIC5, b);
		SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETPOS, MulDiv(cnt, PB_RANGE, fs), 0);
		if(cnt > 5000)
		{
			ct = MulDiv(fs - cnt, ct, cnt);
			wsprintf(b, _T("%d:%02d:%02d"), ct / 3600, (ct / 60) % 60, ct % 60);
		}
		else *b = 0;
		SetWindowText(GetDlgItem(hDlg, IDC_STATIC4), b);
	}
}

/*****************************************************
* FUNCTION NAME: centerDlg()
* PURPOSE: 
*    centers dlg on NSIS parent 
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
void centerDlg(HWND hDlg)
{
	HWND hwndParent = GetParent(hDlg);
	RECT nsisRect, dlgRect, waRect;
	int dlgX, dlgY, dlgWidth, dlgHeight;

	if(hwndParent == NULL || silent)
		return;
	if(popup)
		GetWindowRect(hwndParent, &nsisRect);
	else GetClientRect(hwndParent, &nsisRect);
	GetWindowRect(hDlg, &dlgRect);

	dlgWidth = dlgRect.right - dlgRect.left;
	dlgHeight = dlgRect.bottom - dlgRect.top;
	dlgX = (nsisRect.left + nsisRect.right - dlgWidth) / 2;
	dlgY = (nsisRect.top + nsisRect.bottom - dlgHeight) / 2;

	if(popup)
	{
		SystemParametersInfo(SPI_GETWORKAREA, 0, &waRect, 0);
		if(dlgX > waRect.right - dlgWidth)
			dlgX = waRect.right - dlgWidth;
		if(dlgX < waRect.left) dlgX = waRect.left;
		if(dlgY > waRect.bottom - dlgHeight)
			dlgY = waRect.bottom - dlgHeight;
		if(dlgY < waRect.top) dlgY = waRect.top;
	}
	else dlgY += 20;

	SetWindowPos(hDlg, HWND_TOP, dlgX, dlgY, 0, 0, SWP_NOSIZE);
}

/*****************************************************
* FUNCTION NAME: onInitDlg()
* PURPOSE: 
*    dlg init
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
void onInitDlg(HWND hDlg)
{
	HFONT hFont;
	HWND hPrbNew;
	HWND hPrbOld;
	HWND hCan = GetDlgItem(hDlg, IDCANCEL);

	if(childwnd)
	{
		hPrbNew = GetDlgItem(hDlg, IDC_PROGRESS1);
		hPrbOld = GetDlgItem(childwnd, 0x3ec);

		// Backland' fix for progress bar redraw/style issue.
		// Original bar may be hidden because of interfernce with other plug-ins.
		LONG prbStyle = WS_VISIBLE | WS_CHILD | WS_CLIPSIBLINGS | WS_CLIPCHILDREN;
		if(hPrbOld != NULL)
		{ 
			prbStyle |= GetWindowLong(hPrbOld, GWL_STYLE); 
		} 
		SetWindowLong(hPrbNew, GWL_STYLE, prbStyle); 

		if(!popup)
		{
			if((hFont = (HFONT)SendMessage(childwnd, WM_GETFONT, 0, 0)) != NULL)
			{
				SendDlgItemMessage(hDlg, IDC_STATIC1, WM_SETFONT, (WPARAM)hFont, 0);
				SendDlgItemMessage(hDlg, IDCANCEL, WM_SETFONT, (WPARAM)hFont, 0);
			}
			if(*szCancel == 0)
				GetWindowText(GetDlgItem(GetParent(childwnd), IDCANCEL), szCancel, sizeof(szCancel));
			SetWindowText(hCan, szCancel);
			SetWindowPos(hPrbNew, HWND_TOP, 0, 0, 0, 0, SWP_NOSIZE | SWP_NOMOVE);
		}
	}

	if(nocancel)
	{
		if(hCan != NULL)
			ShowWindow(hCan, SW_HIDE);
		if(popup)
			SetWindowLong(hDlg, GWL_STYLE, GetWindowLong(hDlg, GWL_STYLE) ^ WS_SYSMENU);
	}
	SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETRANGE,
		0, MAKELPARAM(0, PB_RANGE));
	if(szBanner != NULL)
	{
		SendDlgItemMessage(hDlg, IDC_STATIC13, STM_SETICON,
			(WPARAM)LoadIcon(GetModuleHandle(NULL), MAKEINTRESOURCE(103)), 0);
		SetDlgItemText(hDlg, IDC_STATIC12, szBanner);
		SetWindowText(hDlg, *szCaption ? szCaption : PLUGIN_NAME);
	}
	SetTimer(hDlg, 1, 1000, NULL);
	if(*szUrl != 0)
	{
		SetDlgItemText(hDlg, IDC_STATIC20, szUrl);
		SetDlgItemText(hDlg, IDC_STATIC21, szDownloading);
		SetDlgItemText(hDlg, IDC_STATIC22, szConnecting);
		SetDlgItemText(hDlg, IDC_STATIC23, szProgress);
		SetDlgItemText(hDlg, IDC_STATIC24, szSecond);
		SetDlgItemText(hDlg, IDC_STATIC25, szRemaining);
	}
}

/*****************************************************
* FUNCTION NAME: dlgProc()
* PURPOSE: 
*    dlg message handling procedure
* SPECIAL CONSIDERATIONS:
*    todo: better dialog design
*****************************************************/
BOOL WINAPI dlgProc(HWND hDlg,
										UINT message,
										WPARAM wParam,
										LPARAM lParam )  {
											switch(message)    {
	 case WM_INITDIALOG:
		 onInitDlg(hDlg);
		 centerDlg(hDlg);
		 break;
	 case WM_PAINT:
		 // child dialog redraw problem. return false is important
		 RedrawWindow(GetDlgItem(hDlg, IDC_STATIC1), NULL, NULL, RDW_INVALIDATE);
		 RedrawWindow(GetDlgItem(hDlg, IDCANCEL), NULL, NULL, RDW_INVALIDATE);
		 RedrawWindow(GetDlgItem(hDlg, IDC_PROGRESS1), NULL, NULL, RDW_INVALIDATE);
		 UpdateWindow(GetDlgItem(hDlg, IDC_STATIC1));
		 UpdateWindow(GetDlgItem(hDlg, IDCANCEL));
		 UpdateWindow(GetDlgItem(hDlg, IDC_PROGRESS1));
		 return false;
	 case WM_TIMER:
		 if(!silent && IsWindow(hDlg))
		 {
			 //  long connection period and paused state updates
			 if(status != ST_DOWNLOAD && GetTickCount() - transfStart > PROGRESS_MS)
				 transfStart += PROGRESS_MS;
			 if(popup) onTimer(hDlg);
			 else progress_callback();
			 RedrawWindow(GetDlgItem(hDlg, IDC_STATIC1), NULL, NULL, RDW_INVALIDATE);
			 RedrawWindow(GetDlgItem(hDlg, IDCANCEL), NULL, NULL, RDW_INVALIDATE);
			 RedrawWindow(GetDlgItem(hDlg, IDC_PROGRESS1), NULL, NULL, RDW_INVALIDATE);
		 }
		 break;
	 case WM_COMMAND:
		 switch(LOWORD(wParam))
		 {
		 case IDCANCEL:
			 if(nocancel) break;
			 if(szQuestion &&
				 MessageBox(hDlg, szQuestion, *szCaption ? szCaption : PLUGIN_NAME, MB_ICONWARNING|MB_YESNO) == IDNO)
				 break;
			 status = ST_CANCELLED;
		 case IDOK:
			 if(status != ST_CANCELLED && HIWORD(wParam) != INTERNAL_OK) break;
			 // otherwise in the silent mode next banner windows may go to background
			 //         if(silent) sf(hDlg);
			 KillTimer(hDlg, 1);
			 DestroyWindow(hDlg);
			 break;
		 }
	 default: return false;
											}
											return true;
}

/*****************************************************
* FUNCTION NAME: get()
* PURPOSE: 
*    http/https/ftp file download entry point
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
extern "C"
void __declspec(dllexport) get(HWND hwndParent,
															 int string_size,
															 TCHAR *variables,
															 stack_t **stacktop,
															 extra_parameters *extra
															 )
{
	HANDLE hThread;
	DWORD dwThreadId;
	MSG msg;
	TCHAR szUsername[64]=_T(""), // proxy params
		szPassword[64]=_T("");


	EXDLL_INIT();

// for repeating /nounload plug-un calls - global vars clean up
	silent = popup = resume = nocancel = noproxy = nocookies = false;
	myFtpCommand = NULL;
	openType = INTERNET_OPEN_TYPE_PRECONFIG;
	status = ST_CONNECTING;
	*szCaption = *szCancel = *szUserAgent = *szBasic = *szAuth = 0;

	url = (TCHAR*)GlobalAlloc(GPTR, string_size * sizeof(TCHAR));
	if(szPost)
	{
		popstring(url);
#ifdef UNICODE
		WideCharToMultiByte(CP_ACP, 0, url, -1, szPost, string_size, NULL, NULL);
#else
		lstrcpy(szPost, url);
#endif
		fSize = strlen(szPost);
	}
	// global silent option
	if(extra->exec_flags->silent != 0)
		silent = true;
	// we must take this from stack, or push url back
	while(!popstring(url) && *url == _T('/'))
	{
		if(lstrcmpi(url, _T("/silent")) == 0)
			silent = true;
		else if(lstrcmpi(url, _T("/caption")) == 0)
			popstring(szCaption);
		else if(lstrcmpi(url, _T("/username")) == 0)
			popstring(szUsername);
		else if(lstrcmpi(url, _T("/password")) == 0)
			popstring(szPassword);
		else if(lstrcmpi(url, _T("/nocancel")) == 0)
			nocancel = true;
		else if(lstrcmpi(url, _T("/nocookies")) == 0)
			nocookies = true;
		else if(lstrcmpi(url, _T("/noproxy")) == 0)
			openType = INTERNET_OPEN_TYPE_DIRECT;
		else if(lstrcmpi(url, _T("/popup")) == 0)
		{
			popup = true;
			szAlias = (TCHAR*)GlobalAlloc(GPTR, string_size);
			popstring(szAlias);
		}
		else if(lstrcmpi(url, _T("/resume")) == 0)
		{
			popstring(url);
			if(lstrlen(url) > 0)
				lstrcpy(szResume, url);
			resume = true;
		}
		else if(lstrcmpi(url, _T("/translate")) == 0)
		{
			if(popup)
			{
				popstring(szUrl);
				popstring(szStatus[ST_DOWNLOAD]); // Downloading
				popstring(szStatus[ST_CONNECTING]); // Connecting
				lstrcpy(szStatus[ST_URLOPEN], szStatus[ST_CONNECTING]);
				popstring(szDownloading);// file name
				popstring(szConnecting);// received
				popstring(szProgress);// file size
				popstring(szSecond);// remaining time
				popstring(szRemaining);// total time
			}
			else
			{
				popstring(szDownloading);
				popstring(szConnecting);
				popstring(szSecond);
				popstring(szMinute);
				popstring(szHour);
				popstring(szPlural);
				popstring(szProgress);
				popstring(szRemaining);
			}
		}
		else if(lstrcmpi(url, _T("/banner")) == 0)
		{
			popup = true;
			szBanner = (TCHAR*)GlobalAlloc(GPTR, string_size);
			popstring(szBanner);
		}
		else if(lstrcmpi(url, _T("/canceltext")) == 0)
		{
			popstring(szCancel);
		}
		else if(lstrcmpi(url, _T("/question")) == 0)
		{
			szQuestion = (TCHAR*)GlobalAlloc(GPTR, string_size);
			popstring(szQuestion);
			if(*szQuestion == 0) lstrcpy(szQuestion, DEF_QUESTION);
		}
		else if(lstrcmpi(url, _T("/useragent")) == 0)
		{
			popstring(szUserAgent);
		}
		else if(lstrcmpi(url, _T("/proxy")) == 0)
		{
			szProxy = (TCHAR*)GlobalAlloc(GPTR, string_size * sizeof(TCHAR));
			popstring(szProxy);
			openType = INTERNET_OPEN_TYPE_PROXY;
		}
		else if(lstrcmpi(url, _T("/connecttimeout")) == 0)
		{
			popstring(url);
			timeout = _tcstol(url, NULL, 10) * 1000;
		}
		else if(lstrcmpi(url, _T("/receivetimeout")) == 0)
			
		{
			popstring(url);
			receivetimeout = _tcstol(url, NULL, 10) * 1000;
		}
		else if(lstrcmpi(url, _T("/header")) == 0)
		{
			szHeader = (TCHAR*)GlobalAlloc(GPTR, string_size);
			popstring(szHeader);
		}
		else if(lstrcmpi(url, _T("/file")) == 0)
		{
			HANDLE hFile = CreateFileA(szPost, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);
			DWORD rslt;
			if(hFile == INVALID_HANDLE_VALUE)
			{
				status = ERR_FILEOPEN;
				goto cleanup;
			}
			if((fSize = GetFileSize(hFile, NULL)) == 0)
			{
				CloseHandle(hFile);
				status = ERR_FILEREAD;
				goto cleanup;
			}
			wsprintfA(post_fname, "Filename: %s",
				strchr(szPost, '\\') ? strrchr(szPost, '\\') + 1 : szPost);
			GlobalFree(szPost);
			szPost = (char*)GlobalAlloc(GPTR, fSize);
			if(ReadFile(hFile, szPost, fSize, &rslt, NULL) == 0 || rslt != fSize)
			{
				CloseHandle(hFile);
				status = ERR_FILEREAD;
				goto cleanup;
			}
			CloseHandle(hFile);
		}
	}
	pushstring(url);
//	if(*szCaption == 0) lstrcpy(szCaption, PLUGIN_NAME);
	if(*szUserAgent == 0) lstrcpy(szUserAgent, INETC_USERAGENT);
	if(*szPassword && *szUsername)
	{
		wsprintf(url, _T("%s:%s"), szUsername, szPassword);
		encode_base64(lstrlen(url), url, szAuth);
	}
	// may be silent for plug-in, but not so for installer itself - let's try to define 'progress text'
	if(hwndParent != NULL &&
		(childwnd = FindWindowEx(hwndParent, NULL, _T("#32770"), NULL)) != NULL &&
		!silent)
		SetDlgItemText(childwnd, 1006, *szCaption ? szCaption : PLUGIN_NAME);
	else InitCommonControls(); // or NSIS do this before .onInit?
	// cannot embed child dialog to non-existing parent. Using 'silent' to hide it
	if(childwnd == NULL && !popup) silent = true;
	// let's use hidden popup dlg in the silent mode - works both on .onInit and Page
	if(silent) { resume = false; popup = true; }
	// google says WS_CLIPSIBLINGS helps to redraw... not in my tests...
	if(!popup)
	{
		unsigned int wstyle = GetWindowLong(childwnd, GWL_STYLE);
		wstyle |= WS_CLIPSIBLINGS;
		SetWindowLong(childwnd, GWL_STYLE, wstyle);
	}
	startTime = GetTickCount();
	if((hDlg = CreateDialog(g_hInstance,
		MAKEINTRESOURCE(szBanner ? IDD_DIALOG2 : (popup ? IDD_DIALOG1 : IDD_DIALOG3)),
		(popup ? hwndParent : childwnd), dlgProc)) != NULL)
	{

		if((hThread = CreateThread(NULL, 0, inetTransfer, (LPVOID)hDlg, 0,
			&dwThreadId)) != NULL)
		{
			HWND hButton = GetDlgItem(childwnd, 0x403);
			HWND hList = GetDlgItem(childwnd, 0x3f8);
			DWORD dwStyleButton = 0;
			BOOL fVisibleList = false;
			if(!silent)
			{
				ShowWindow(hDlg, SW_NORMAL);
				if(childwnd && !popup)
				{
					if(hButton)
					{
						dwStyleButton = GetWindowLong(hButton, GWL_STYLE);
						EnableWindow(hButton, false);
					}
					if(hList)
					{
						fVisibleList = IsWindowVisible(hList);
						ShowWindow(hList, SW_HIDE);
					}
				}
			}

			while(IsWindow(hDlg) &&
				GetMessage(&msg, NULL, 0, 0) > 0)
			{
				if(!IsDialogMessage(hDlg, &msg) &&
					!IsDialogMessage(hwndParent, &msg) &&
					!TranslateMessage(&msg))
					DispatchMessage(&msg);
			}

			if(WaitForSingleObject(hThread, 3000) == WAIT_TIMEOUT)
			{
				TerminateThread(hThread, 1);
				status = ERR_TERMINATED;
			}
			CloseHandle(hThread);
			if(!silent && childwnd)
			{
				SetDlgItemText(childwnd, 1006, _T(""));
				if(!popup)
				{
					if(hButton)
						SetWindowLong(hButton, GWL_STYLE, dwStyleButton);
					if(hList && fVisibleList)
						ShowWindow(hList, SW_SHOW);
				}
				//            RedrawWindow(childwnd, NULL, NULL, RDW_INVALIDATE|RDW_ERASE);
			}
		}
		else
		{
			status = ERR_THREAD;
			DestroyWindow(hDlg);
		}
	}
	else {
		status = ERR_DIALOG;
		wsprintf(szStatus[status] + lstrlen(szStatus[status]), _T(" (Err=%d)"), GetLastError());
	}
cleanup:
	// we need to clean up stack from remaining url/file pairs.
	// this multiple files download head pain and may be not safe
	while(!popstring(url) && lstrcmpi(url, _T("/end")) != 0)
	{
		/* nothing MessageBox(NULL, url, _T(""), 0);*/
	}
	GlobalFree(url);
	if(szAlias) GlobalFree(szAlias);
	if(szBanner) GlobalFree(szAlias);
	if(szQuestion) GlobalFree(szQuestion);
	if(szProxy) GlobalFree(szProxy);
	if(szPost) GlobalFree(szPost);
	if(szHeader) GlobalFree(szHeader);

	url = szProxy = szHeader = szAlias = szQuestion = NULL;
	szPost = NULL;
	fput = fhead = false;

	pushstring(szStatus[status]);
}

/*****************************************************
* FUNCTION NAME: put()
* PURPOSE: 
*    http/ftp file upload entry point
* SPECIAL CONSIDERATIONS:
*    re-put not works with http, but ftp REST - may be.
*****************************************************/
extern "C"
void __declspec(dllexport) put(HWND hwndParent,
															 int string_size,
															 TCHAR *variables,
															 stack_t **stacktop,
															 extra_parameters *extra
															 )
{
	fput = true;
	lstrcpy(szDownloading, _T("Uploading %s"));
	lstrcpy(szStatus[2], _T("Uploading"));
	get(hwndParent, string_size, variables, stacktop, extra);
}

/*****************************************************
* FUNCTION NAME: post()
* PURPOSE: 
*    http post entry point
* SPECIAL CONSIDERATIONS:
*
*****************************************************/
extern "C"
void __declspec(dllexport) post(HWND hwndParent,
																int string_size,
																TCHAR *variables,
																stack_t **stacktop,
																extra_parameters *extra
																)
{
	szPost = (CHAR*)GlobalAlloc(GPTR, string_size);
	get(hwndParent, string_size, variables, stacktop, extra);
}

/*****************************************************
* FUNCTION NAME: head()
* PURPOSE: 
*    http/ftp file upload entry point
* SPECIAL CONSIDERATIONS:
*    re-put not works with http, but ftp REST - may be.
*****************************************************/
extern "C"
void __declspec(dllexport) head(HWND hwndParent,
																int string_size,
																TCHAR *variables,
																stack_t **stacktop,
																extra_parameters *extra
																)
{
	fhead = true;
	get(hwndParent, string_size, variables, stacktop, extra);
}

/*****************************************************
* FUNCTION NAME: DllMain()
* PURPOSE: 
*    Dll main (initialization) entry point
* SPECIAL CONSIDERATIONS:
*    
*****************************************************/
BOOL WINAPI DllMain(HANDLE hInst,
										ULONG ul_reason_for_call,
										LPVOID lpReserved)
{
	g_hInstance=(HINSTANCE)hInst;
	return TRUE;
}
