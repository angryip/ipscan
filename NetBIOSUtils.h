// NetBIOSUtils.h: interface for the CScanFunc class.
//
//////////////////////////////////////////////////////////////////////

#include <nb30.h>

#if !defined(AFX_SCANFUNC_H__15CE10D2_FC18_44BA_A9CA_52346AC446E3__INCLUDED_)
#define AFX_SCANFUNC_H__15CE10D2_FC18_44BA_A9CA_52346AC446E3__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

typedef struct _ASTAT_
{
	ADAPTER_STATUS adapt;
	NAME_BUFFER    NameBuff[30];
} ASTAT, * PASTAT;

class CNetBIOSUtils
{
protected:
	void GetLanaNumber();
	int m_nLana;
	BOOL DeleteName(int nLana, LPCSTR szName);
	BOOL AddName(int nLana, LPCSTR szName);
	void MakeName (char *achDest, LPCSTR szSrc);
	BOOL Reset(int nLana, int nSessions, int nNames);
	BOOL AdapterStatus(int nLana, PVOID pBuffer, int cbBuffer, LPCSTR szName);
	CString m_szIP;
	

public:	
	CNetBIOSUtils(CString szIPAddress);
	virtual ~CNetBIOSUtils();
	BOOL GetNames(CString &szUserName, CString &szComputerName, CString &szGroupName, CString &szMacAddress);	
	
};

#endif // !defined(AFX_SCANFUNC_H__15CE10D2_FC18_44BA_A9CA_52346AC446E3__INCLUDED_)
