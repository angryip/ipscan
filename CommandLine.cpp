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

// CommandLine.cpp: implementation of the CCommandLine class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "CommandLine.h"
#include "SaveToFile.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CCommandLine::CCommandLine()
{
	m_nOptions = 0;
}

CCommandLine::~CCommandLine()
{

}

BOOL CCommandLine::process()
{	
	if (__argc!=1) 
	{
	
		// too few or too many parameters
		if (__argc<3) 
		{
			displayHelp();
			exit(1);
		}

		m_nOptions = 0;
		int nParameter = 0;

		// parse parameters
		for (int i=1; i < __argc; i++)
		{
			if (__targv[i][0] == '-' || __targv[i][0] == '/')
			{
				// this is an option
				for (int j=1; __targv[i][j] != 0; j++)
				{
					switch (__targv[i][j])
					{
						case 's': 
							m_nOptions |= CMDO_START_SCAN; 
							break;						
						case 'e': 
							m_nOptions |= CMDO_NOT_EXIT; 
							break;
						case 'a': 
							m_nOptions |= CMDO_APPEND_FILE; 
							break;
						case 'h':
							m_nOptions |= CMDO_HIDE_WINDOW;
							break;
						case 'f': 
							if (j == 1)	// Accept "f" only as a first character
							{
								switch (__targv[i][j+2])	// "-f:X" - check the X character
								{
									case 'c':
										m_nFileFormat = FILE_TYPE_CSV; break;
									case 'h': 
										m_nFileFormat = FILE_TYPE_HTML; break;
									case 'x': 
										m_nFileFormat = FILE_TYPE_XML; break;
									case 'l': 
										m_nFileFormat = FILE_TYPE_IPPORT_LIST; break;
									default:
										m_nFileFormat = FILE_TYPE_TXT; break;
								}
								__targv[i][j+1] = 0;	// To exit from for loop
							}							
							break;
						default:
							CString err = "Unknown option: ";
							err += __targv[i][j];
							+ __targv[i][j];
							MessageBox(0, err, NULL, MB_OK | MB_ICONHAND);
							break;
					}
				}
			}
			else
			{
				// this is a parameter
				nParameter++;
				
				switch (nParameter)
				{
					case 1: m_szStartIP = (CString)__targv[i]; break;
					case 2: m_szEndIP = (CString)__targv[i]; break;
					case 3: m_szFilename = (CString)__targv[i]; break;
				}
			}
		}		
		
		if (m_szFilename.GetLength() > 0) 
		{			
			m_nOptions |= CMDO_SAVE_TO_FILE;
			m_nOptions |= CMDO_START_SCAN;
		}
		else
		{
			if (m_nOptions & CMDO_HIDE_WINDOW)
			{
				MessageBox(0, "Filename is not given, will not hide the window", "Warning", MB_OK | MB_ICONWARNING);
			}
		}

		return TRUE;
	}
	
	return FALSE;

}

void CCommandLine::displayHelp()
{
	MessageBox(0, "Command-line parameters:\n\n"
				"<start_ip> <end_ip> [filename]\n"
				"\tstart_ip\t- starting IP address\n"
				"\tend_ip\t- ending IP address\n"
				"\tfilename\t- filename to save listing to (optional)\n\n"
				"Note: if 3rd parameter is given, then the program will\n"
				"close after saving data to a file\n\n"
				"Additional options:\n"
				"\t-s\tautomatically start scanning (if filename is not given)\n"				
				"\t-e\tdo not exit after saving data\n"
				"\t-a\tappend to the file, do not overwrite\n"
				"\t-h\tHide main window while scanning. Be careful with this!\n"
				"\t  \tRun only from batch files with this option.\n"
				"\t-f:X\tFile format. X can be 'csv', 'html', 'txt', 'xml' or 'lst'.\n"
			   ,"Angry IP Scanner Help",MB_OK | MB_ICONINFORMATION);
}
