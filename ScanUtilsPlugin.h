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

// ScanUtilsPlugin.h: interface for the CScanUtilsPlugin class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SCANUTILSPLUGIN_H__C22D204C_B1B9_4D78_A3AB_659A7A21EA9D__INCLUDED_)
#define AFX_SCANUTILSPLUGIN_H__C22D204C_B1B9_4D78_A3AB_659A7A21EA9D__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

// Directory, where to search for plugins
#define PLUGINS_DIR		"plugins"

class CScanUtilsPlugin  
{
public:
	CScanUtilsPlugin();
	virtual ~CScanUtilsPlugin();
	void load(CArray<TScannerColumn, TScannerColumn&> &columns, int &nColumnCount);
};

#endif // !defined(AFX_SCANUTILSPLUGIN_H__C22D204C_B1B9_4D78_A3AB_659A7A21EA9D__INCLUDED_)
