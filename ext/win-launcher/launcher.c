// A simple Windows launcher that will run javaw.exe passing own file as -jar parameter.
// This compiled exe file must be prepended to the built jar file.
#include "windows.h"
#include "stdio.h"

PROCESS_INFORMATION processInformation;
DWORD processPriority;

void closeProcessHandles() {
	CloseHandle(processInformation.hThread);
	CloseHandle(processInformation.hProcess);
}

BOOL execute(LPSTR cmdline, DWORD *exitCode) {
	STARTUPINFO si;

    memset(&processInformation, 0, sizeof(processInformation));
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);

	if (CreateProcess(NULL, cmdline, NULL, NULL, TRUE, processPriority, NULL, NULL, &si, &processInformation)) {
		WaitForSingleObject(processInformation.hProcess, INFINITE);
		GetExitCodeProcess(processInformation.hProcess, exitCode);
		closeProcessHandles();
		return TRUE;
	}

	*exitCode = -1;
	return FALSE;
}

void buildCmdLine(LPSTR buf, LPCSTR ownFilename, LPSTR args) {
	strncat(buf, " -jar \"", MAX_PATH - strlen(buf));
	strncat(buf, ownFilename, MAX_PATH - strlen(buf));
	strncat(buf, "\" ", MAX_PATH - strlen(buf));
	strncat(buf, args, MAX_PATH - strlen(buf));
}

char *caption = "Angry IP Scanner";
char *javaHomeCmd = "\"%JAVA_HOME%\\bin\\javaw\"";
char *cmd = "javaw";

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR args, int nCmdShow) {
	DWORD exitCode;
	char cmdLine[MAX_PATH], ownFilename[MAX_PATH];
	GetModuleFileName(NULL, ownFilename, MAX_PATH);
	ExpandEnvironmentStrings(javaHomeCmd, cmdLine, MAX_PATH);
	buildCmdLine(cmdLine, ownFilename, args);
	if (!execute(cmdLine, &exitCode)) {
		strcpy(cmdLine, cmd);
		buildCmdLine(cmdLine, ownFilename, args);
		if (!execute(cmdLine, &exitCode)) {
			char error[1000];
			strcpy(error, "Failed to execute:\n");
			strncat(error, cmdLine, 1000 - strlen(error));
			strncat(error, "\n\nJava/OpenJDK is required to run this program, but was not found.\n\nDo you want to open AdoptOpenJDK page to download it?", 1000 - strlen(error));
			if (MessageBox(0, error, caption, MB_YESNO | MB_ICONERROR) == IDYES) {
				ShellExecute(NULL, "open", "https://adoptium.net/", NULL, NULL, SW_SHOWNORMAL);
			}
		}
	}
	return exitCode;
}
