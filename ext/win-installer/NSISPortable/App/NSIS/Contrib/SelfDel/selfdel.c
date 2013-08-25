/***************************************************
* FILE NAME: selfdel.c
*
* PURPOSE:
*    NSIS plug-in for self deleting uninstaller for
*    Win9x/WinNT (all versions)
*
* CONSIDERATIONS
* 
*  MSVC6: works with Release built only, because source
*  file must be compiled with /GZ turned OFF, but in
*  Debug builds it is always on (basically, disable 
*  run-time stack checks)
*
* CHANGE HISTORY
*
* James Brown - Oct 01 2003
*   - Original http://www.catch22.net/tuts/selfdel.asp
*
* Takhir Bedertdinov - Jan 21 2006
*   - Converted to NSIS plug-in, rmdir implementation, MSVCRT
*     dependencies removed
*
* Stuart Welch - Jul 17 2011
*   - Fixed for x64 by specifying full path to explorer.exe
*   - Ensures WOW64 file system redirection is enabled
*   - Reduced deletion retry to 500ms
*   - Built with VS2010
*   - Added Unicode build
*   - Added version information resource
*   - Calls MoveFileEx with MOVEFILE_DELAY_UNTIL_REBOOT on failure
*
* Stuart Welch - Aug 10 2011
*   - Added /REBOOT
*                                   
**************************************************/

#define WINVER 0x0400
#define _WIN32_WINNT 0x0400
#include <windows.h>

#ifdef UNICODE
#include "nsis_unicode\pluginapi.h"
#else
#include "nsis_ansi\pluginapi.h"
#endif

#ifdef MoveFileEx
#undef MoveFileEx
#endif

#ifndef EWX_FORCEIFHUNG
#define EWX_FORCEIFHUNG 0x00000010
#endif

#pragma pack(push, 1)

#define CODESIZE 0x200
#define SWITCH_RMDIR TEXT("/RMDIR")
#define SWITCH_REBOOT TEXT("/REBOOT")

//
//  Structure to inject into remote process. Contains 
//  function pointers and code to execute.
//
typedef struct _SELFDEL
{
  struct _SELFDEL *Arg0; // pointer to self

  BYTE opCodes[CODESIZE]; // code 

  HANDLE hParent; // parent process handle

  FARPROC fnWaitForSingleObject;
  FARPROC fnCloseHandle;
  FARPROC fnDeleteFile;
  FARPROC fnSleep;
  FARPROC fnExitProcess;
  FARPROC fnRemoveDirectory;
  FARPROC fnGetLastError;
  FARPROC fnExitWindowsEx;

  TCHAR szFileName[MAX_PATH];  // file to delete
  BOOL fRemDir;
  BOOL fReboot;

} SELFDEL;

#pragma pack(pop)

#define NSISFUNC(name) void __declspec(dllexport) name(HWND hWndParent, int string_size, TCHAR* variables, stack_t** stacktop, extra_parameters* extra)
#define DLL_INIT() EXDLL_INIT();

typedef BOOLEAN (WINAPI* PWow64EnableWow64FsRedirection)(BOOLEAN Wow64FsEnableRedirection);
typedef BOOL (WINAPI* PMoveFileEx)(LPCTSTR lpExistingFileName, LPCTSTR lpNewFileName, DWORD dwFlags);

#ifdef _DEBUG
#define FUNC_ADDR(func) (PVOID)(*(DWORD *)((BYTE *)func + 1) + (DWORD)((BYTE *)func + 5))
#else
#define FUNC_ADDR(func) func
#endif

/*****************************************************
 * FUNCTION NAME: remote_thread()
 * PURPOSE: 
 *    Routine to execute in remote process
 * SPECIAL CONSIDERATIONS:
 *    Takhir: I hope it still less then CODESIZE after 
 *    I added rmdir
 *****************************************************/
static void remote_thread(SELFDEL *remote)
{
  TCHAR *p = remote->szFileName, *e;

  // Wait for parent process to terminate
  remote->fnWaitForSingleObject(remote->hParent, INFINITE);
  remote->fnCloseHandle(remote->hParent);

  // Try to delete the executable file 
  while(!remote->fnDeleteFile(remote->szFileName))
  {
    // Failed - try again in a bit
    remote->fnSleep(500);
  }

  // Takhir: my rmdir add-on :)
  // Do we have at least one back slash in full path-name
  // strrchr() implementation
  if(remote->fRemDir)
  {
    while(*++p != 0)
    {
      if(*p == '\\')
        e = p;
    }

    *e = 0;

    // Root install safe, rmdir on Wins doesn't delete 'c:'
    remote->fnRemoveDirectory(remote->szFileName);
  }

  // Afrow UK: reboot add-on
  if (remote->fReboot)
  {
    remote->fnExitWindowsEx(EWX_REBOOT|EWX_FORCEIFHUNG, 0);
  }

  // Finished! Exit so that we don't execute garbage code
  remote->fnExitProcess(0);
}

/*****************************************************
 * FUNCTION NAME: my_memcpy()
 * PURPOSE: 
 *    msvcrt replacement
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
void my_memcpy(BYTE* dst, BYTE* src, int len)
{
  int i;
  for(i=0;i<len;i++)
    dst[i] = src[i];
}

/*****************************************************
 * FUNCTION NAME: Del()
 * PURPOSE: 
 *    Delete currently running executable and exit
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
NSISFUNC(Del)
{
  STARTUPINFO si;
  PROCESS_INFORMATION pi;
  CONTEXT context;
  DWORD oldProt;
  SELFDEL local;
  DWORD entrypoint;
  TCHAR* pszArg = (TCHAR*)LocalAlloc(LMEM_FIXED, sizeof(TCHAR) * string_size);
  HMODULE hModule;
  PWow64EnableWow64FsRedirection Wow64EnableWow64FsRedirection;

  DLL_INIT();

  // Switches?
  local.fRemDir = FALSE;
  local.fReboot = FALSE;
  while (popstring(pszArg) == 0)
  {
    if (lstrcmpi(pszArg, SWITCH_RMDIR) == 0)
    {
      local.fRemDir = TRUE;
    }
    else if (lstrcmpi(pszArg, SWITCH_REBOOT) == 0)
    {
      local.fReboot = TRUE;
    }
    else
    {
      pushstring(pszArg); // who forgot this string in the stack ? :)
      break;
    }
  }

  // Need SE_SHUTDOWN_NAME to reboot.
  if (local.fReboot)
  {
    HANDLE hToken;
    TOKEN_PRIVILEGES tkp;

    if (OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES|TOKEN_QUERY, &hToken))
    {
      if (LookupPrivilegeValue(NULL, SE_SHUTDOWN_NAME, &tkp.Privileges[0].Luid))
      {
        tkp.PrivilegeCount = 1;
        tkp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
      
        AdjustTokenPrivileges(hToken, FALSE, &tkp, 0, NULL, 0);
      }

      CloseHandle(hToken);
    }
  }

  si.cb = sizeof(STARTUPINFO);
  si.cbReserved2 = 0;
  si.lpDesktop = NULL;
  si.lpReserved = NULL;
  si.lpReserved2 = NULL;
  si.lpTitle = NULL;
  si.wShowWindow = 0;

  // Ensure WOW64 file system redirection is enabled so we always
  // execute the 32-bit copy of explorer.exe (as NSIS is 32-bit)
  hModule = GetModuleHandle(TEXT("kernel32.dll"));
  Wow64EnableWow64FsRedirection = (PWow64EnableWow64FsRedirection)GetProcAddress(hModule, "Wow64EnableWow64FsRedirection");
  if (Wow64EnableWow64FsRedirection != NULL)
    Wow64EnableWow64FsRedirection(TRUE);

  // Get full path to explorer.exe
  if (LOBYTE(LOWORD(GetVersion())) <= 5)
    GetEnvironmentVariable(TEXT("WINDIR"), pszArg, string_size);
  else
    GetSystemDirectory(pszArg, string_size);
  lstrcat(pszArg, TEXT("\\explorer.exe"));

  // Create executable suspended
  if(CreateProcess(0, pszArg, 0, 0, 0, CREATE_SUSPENDED|IDLE_PRIORITY_CLASS, 0, 0, &si, &pi))
  {
    local.fnWaitForSingleObject = (FARPROC)WaitForSingleObject;
    local.fnCloseHandle         = (FARPROC)CloseHandle;
    local.fnDeleteFile          = (FARPROC)DeleteFile;
    local.fnSleep               = (FARPROC)Sleep;
    local.fnExitProcess         = (FARPROC)ExitProcess;
    local.fnRemoveDirectory     = (FARPROC)RemoveDirectory;
    local.fnGetLastError        = (FARPROC)GetLastError;
    local.fnExitWindowsEx       = (FARPROC)ExitWindowsEx;

    // Give remote process a copy of our own process handle
    DuplicateHandle(GetCurrentProcess(), GetCurrentProcess(), pi.hProcess, &local.hParent, 0, FALSE, 0);

    GetModuleFileName(NULL, local.szFileName, MAX_PATH);

    // copy in binary code
    my_memcpy(local.opCodes, (BYTE*)FUNC_ADDR(remote_thread), CODESIZE);

    // Allocate some space on process's stack and place
    // our SELFDEL structure there. Then set the instruction pointer 
    // to this location and let the process resume
    context.ContextFlags = CONTEXT_INTEGER|CONTEXT_CONTROL;
    GetThreadContext(pi.hThread, &context);

    // Allocate space on stack (aligned to cache-line boundary)
    entrypoint = (context.Esp - sizeof(SELFDEL)) & ~0x1F;
    
    // Place a pointer to the structure at the bottom-of-stack 
    // this pointer is located in such a way that it becomes 
    // the remote_thread's first argument!!
    local.Arg0 = (SELFDEL *)entrypoint;

    context.Esp = entrypoint - 4;  // create dummy return address
    context.Eip = entrypoint + 4;  // offset of opCodes within structure

    // copy in our code+data at the exe's entry-point
    VirtualProtectEx(pi.hProcess, (PVOID)entrypoint, sizeof(local), PAGE_EXECUTE_READWRITE, &oldProt);
    WriteProcessMemory(pi.hProcess, (PVOID)entrypoint, &local, sizeof(local), 0);

    FlushInstructionCache(pi.hProcess, (PVOID)entrypoint, sizeof(local));

    SetThreadContext(pi.hThread, &context);

    // Let the process continue
    ResumeThread(pi.hThread);
    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
  }
  else
  {
#ifdef UNICODE
    PMoveFileEx MoveFileEx = (PMoveFileEx)GetProcAddress(hModule, "MoveFileExW");
#else
    PMoveFileEx MoveFileEx = (PMoveFileEx)GetProcAddress(hModule, "MoveFileExA");
#endif
    if (MoveFileEx != NULL)
      MoveFileEx(local.szFileName, NULL, MOVEFILE_DELAY_UNTIL_REBOOT);
  }
  
  LocalFree(pszArg);
}

/*****************************************************
 * FUNCTION NAME: DllMain()
 * PURPOSE: 
 *    Dll main entry point
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
BOOL WINAPI DllMain(HANDLE hInst, ULONG ul_reason_for_call, LPVOID lpReserved)
{
  return TRUE;
}
