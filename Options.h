// Options.h: interface for the COptions class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_)
#define AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class COptions  
{
public:
	void setPortString(LPCSTR szPortString);
	CString m_szPorts;
	COptions();
	virtual ~COptions();

};

#endif // !defined(AFX_OPTIONS_H__AC5DAD55_DC6A_4BD2_AE72_12C6AF55FCCA__INCLUDED_)
