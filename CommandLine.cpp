// CommandLine.cpp: implementation of the CCommandLine class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ipscan.h"
#include "CommandLine.h"

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
			if (__targv[i][0] == '-')
			{
				// this is an option
				for (int j=1; __targv[i][j] != 0; j++)
				{
					switch (__targv[i][j])
					{
						case 's': 
							m_nOptions |= CMDO_START_SCAN; break;
						case 'c': 
							m_nOptions |= CMDO_SAVE_CSV; break;
						case 'e': 
							m_nOptions |= CMDO_NOT_EXIT; break;
						case 'a': 
							m_nOptions |= CMDO_APPEND_FILE; break;
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
				"\t-c\tfile format is CSV, not TXT\n"
				"\t-e\tdo not exit after saving data\n"
				"\t-a\tappend to the file, do not overwrite\n"
			   ,"Angry IP Scanner Help",MB_OK | MB_ICONINFORMATION);
}
