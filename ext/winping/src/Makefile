#
# This file is a part of Angry IP Scanner source code,
# see http://www.azib.net/ for more information.
# Licensed under GPLv2.
#
# Windows JNI pinger using Microsoft's ICMP.DLL
# Author: Anton Keks 
#

DEST = ..\lib

JAVA_INCDIR      = $(JDK_HOME)\include
JAVA_INCDIR_PLAF = $(JAVA_INCDIR)\win32

CC        = cl
CFLAGS    = -TC
CPPFLAGS  = -I$(JAVA_INCDIR) -I$(JAVA_INCDIR_PLAF)

SRC = WindowsPinger.c
OBJ = $(SRC:.c=.obj)

LIBNAME          = winping
LIBEXTENSION     = dll
LIBWINPING       = $(LIBNAME).$(LIBEXTENSION)
CLEAN_EXTENSIONS = *.obj *.$(LIBEXTENSION) *.lib *.exp

all: $(LIBWINPING)

.c.obj:
	$(CC) -nologo $(CFLAGS) $(CPPFLAGS) -c $< -o $(DEST)\$@

$(LIBWINPING): $(OBJ)
	$(CC) -nologo -MD -LD -o $(DEST)\$@ $** 

clean:
	del $(CLEAN_EXTENSIONS)
