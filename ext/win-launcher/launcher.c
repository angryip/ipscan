// A simple Windows launcher that will run javaw.exe passing either a jar file with the same
// name next to this exe, or (if there is none) own file as -jar parameter.
// This compiled exe file must be prepended to the built jar file when used without a separate jar.
#include "windows.h"
#include "stdio.h"

PROCESS_INFORMATION processInformation;
DWORD processPriority;

void closeProcessHandles() {
	CloseHandle(processInformation.hThread);
	CloseHandle(processInformation.hProcess);
}

BOOL execute(LPWSTR cmdline, DWORD *exitCode) {
	STARTUPINFOW si;

	memset(&processInformation, 0, sizeof(processInformation));
	memset(&si, 0, sizeof(si));
	si.cb = sizeof(si);

	if (CreateProcessW(NULL, cmdline, NULL, NULL, TRUE, processPriority, NULL, NULL, &si, &processInformation)) {
		WaitForSingleObject(processInformation.hProcess, INFINITE);
		GetExitCodeProcess(processInformation.hProcess, exitCode);
		closeProcessHandles();
		return TRUE;
	}

	*exitCode = -1;
	return FALSE;
}

void append(LPWSTR buf, DWORD size, LPCWSTR str) {
	wcsncat(buf, str, size - wcslen(buf) - 1);
}

void buildCmdLine(LPWSTR buf, LPCWSTR ownFilename, LPWSTR args) {
	append(buf, MAX_PATH, L" -jar \"");
	append(buf, MAX_PATH, ownFilename);
	append(buf, MAX_PATH, L"\" ");
	append(buf, MAX_PATH, args);
}

// Prefer a jar file with the same name next to this exe (installer layout),
// falling back to own file with an appended jar (portable single-file layout).
void resolveJar(LPWSTR jarName, LPCWSTR ownFilename) {
	wcscpy(jarName, ownFilename);
	LPWSTR ext = wcsrchr(jarName, L'.');
	if (ext && !wcschr(ext, L'\\'))
		wcscpy(ext, L".jar");
	else
		append(jarName, MAX_PATH, L".jar");
	if (GetFileAttributesW(jarName) == INVALID_FILE_ATTRIBUTES)
		wcscpy(jarName, ownFilename);
}

LPWSTR caption = L"Angry IP Scanner";
LPWSTR localCmd = L"\\jre\\bin\\javaw";
LPWSTR javaHomeCmd = L"\"%JAVA_HOME%\\bin\\javaw\"";
LPWSTR cmd = L"javaw";

int APIENTRY wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR args, int nCmdShow) {
	DWORD exitCode;
	WCHAR cmdLine[MAX_PATH], ownFilename[MAX_PATH], jarName[MAX_PATH], ownDir[MAX_PATH];
	GetModuleFileNameW(NULL, ownFilename, MAX_PATH);
	resolveJar(jarName, ownFilename);

	wcscpy(ownDir, ownFilename);
	LPWSTR lastSlash = wcsrchr(ownDir, L'\\');
	*lastSlash = L'\0';
	wcscpy(cmdLine, ownDir);
	append(cmdLine, MAX_PATH, localCmd);

	buildCmdLine(cmdLine, jarName, args);
	if (!execute(cmdLine, &exitCode)) {
		ExpandEnvironmentStringsW(javaHomeCmd, cmdLine, MAX_PATH);

		buildCmdLine(cmdLine, jarName, args);
		if (!execute(cmdLine, &exitCode)) {
			wcscpy(cmdLine, cmd);

			buildCmdLine(cmdLine, jarName, args);
			if (!execute(cmdLine, &exitCode)) {
				WCHAR error[1000];
				wcscpy(error, L"Failed to execute:\n");
				append(error, 1000, cmdLine);
				append(error, 1000, L"\n\nJava/OpenJDK is required to run this program, but was not found.\n\nDo you want to open a download page?");
				if (MessageBoxW(0, error, caption, MB_YESNO | MB_ICONERROR) == IDYES) {
					ShellExecuteW(NULL, L"open", L"https://adoptium.net/", NULL, NULL, SW_SHOWNORMAL);
				}
			}
		}
	}
	return exitCode;
}
