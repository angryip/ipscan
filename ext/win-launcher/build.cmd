:: MinGW C compiler & tools must be installed and added to PATH
windres version.rc -O coff -o version.res
gcc -g0 -s launcher.c version.res -o launcher.exe
