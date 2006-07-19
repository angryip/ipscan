#
# $Id: Makefile,v 1.2 2005/11/20 21:48:31 angryziber Exp $
#
# Copyright 2004-2005 Daniel F. Savarese
# Contact Information: http://www.savarese.org/contact.html
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.savarese.org/software/ApacheLicense-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

UNAME  := $(shell uname)
CYGWIN := $(findstring CYGWIN,$(UNAME))

ifeq ($(CYGWIN),CYGWIN)
  JDK_HOME := $(shell cygpath $(JDK_HOME))
endif

JAVA_INCDIR      = $(JDK_HOME)/include
JAVA_INCDIR_PLAF = $(dir $(wildcard $(JAVA_INCDIR)/*/jni_md.h))

CC        = gcc
CFLAGS    = -ansi -Wall -O2 -pipe
CPPFLAGS  = -I$(JAVA_INCDIR) -I$(JAVA_INCDIR_PLAF)
WINSOCK   = ws2_32
LDFLAGS   =

ifeq ($(CYGWIN),CYGWIN)
  override CC += -mno-cygwin
  CPPFLAGS += -D__int64="long long"
  LIBNAME      = rocksaw
  LIBEXTENSION = dll
  LDFLAGS += -Wl,--kill-at -l$(WINSOCK)
else
  CFLAGS += -fpic
  LIBNAME      = librocksaw
  LIBEXTENSION = so
endif

SRC     := $(shell find . -name "*.c" -print)
OBJ     := $(SRC:%.c=%.o)

CLEAN_EXTENSIONS = o $(LIBEXTENSION)

LIBROCKSAW = $(LIBNAME).$(LIBEXTENSION)

all: $(LIBROCKSAW)

%.o: %.c
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@

$(LIBROCKSAW): $(OBJ)
	$(CC) -shared -o $@ $^ $(LDFLAGS)

clean:
	for extension in $(CLEAN_EXTENSIONS); do \
		find . -name "*.$$extension" | xargs rm -f ; \
	done
	find . -name "*~" | xargs rm -f
