/* 
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 *
 * Windows JNI pinger using Microsoft's ICMP.DLL
 * Author: Anton Keks 
 */

#include <windows.h>

#include "WindowsPinger.h"


FARPROC IcmpCreateFile = NULL;

typedef BOOL (FAR WINAPI *TIcmpCloseHandle)(HANDLE IcmpHandle);
TIcmpCloseHandle IcmpCloseHandle = NULL;

typedef DWORD (FAR WINAPI *TIcmpSendEcho)(
  HANDLE IcmpHandle, 	/* handle returned from IcmpCreateFile() */  
  u_long DestAddress, /* destination IP address (in network order) */  
  LPVOID RequestData, /* pointer to buffer to send */  
  WORD RequestSize,	/* length of data in buffer */  
  LPVOID RequestOptns,  /* see Note 2 */  
  LPVOID ReplyBuffer, /* see Note 1 */  
  DWORD ReplySize, 	/* length of reply (must allow at least 1 reply) */  
  DWORD Timeout 	/* time in milliseconds to wait for reply */
);
TIcmpSendEcho IcmpSendEcho = NULL;

/* 
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCreateFile
 */
JNIEXPORT jint JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCreateFile
(JNIEnv *env, jclass cls)
{  
  // Initialize dlls on first use
  if (IcmpCreateFile == NULL) {
    HMODULE hICMP = LoadLibrary("icmp.dll");
    if (!hICMP) {
      // newer versions of Windows should include this one instead
      hICMP = LoadLibrary("iphlpapi.dll");
    }

    if (!hICMP) {
      return -1;
    }

    IcmpCreateFile  = (FARPROC) GetProcAddress(hICMP, "IcmpCreateFile");
    IcmpCloseHandle = (TIcmpCloseHandle) GetProcAddress(hICMP, "IcmpCloseHandle");
    IcmpSendEcho    = (TIcmpSendEcho) GetProcAddress(hICMP, "IcmpSendEcho");
  }

  return IcmpCreateFile();
}

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCloseHandle
 */
JNIEXPORT void JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCloseHandle
(JNIEnv *env, jclass cls, jint handle)
{
  return IcmpCloseHandle((HANDLE)handle);
}

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpSendEcho
 */

JNIEXPORT jint JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpSendEcho
(JNIEnv *env, jclass cls, jint handle, jbyteArray address, jbyteArray pingData, jbyteArray replyData, jint timeout)
{
  DWORD replyCount;
  jbyte *addrBuf, *pingDataBuf, *replyDataBuf;
  jint pingDataLen, replyDataLen;
  jclass replyClass;
  jfieldID fid;
  u_long ip;

  addrBuf = (*env)->GetByteArrayElements(env, address, NULL);
  pingDataBuf = (*env)->GetByteArrayElements(env, pingData, NULL);
  replyDataBuf = (*env)->GetByteArrayElements(env, replyData, NULL);

  pingDataLen = (*env)->GetArrayLength(env, pingData);
  replyDataLen = (*env)->GetArrayLength(env, replyData);

  ip = *((u_long*)addrBuf);
  replyCount = IcmpSendEcho((HANDLE)handle, ip, pingDataBuf, pingDataLen,
                            NULL, replyDataBuf, replyDataLen, timeout);
                            
  (*env)->ReleaseByteArrayElements(env, address, addrBuf, JNI_ABORT);
  (*env)->ReleaseByteArrayElements(env, pingData, pingDataBuf, JNI_ABORT);
  (*env)->ReleaseByteArrayElements(env, replyData, replyDataBuf, 0);
  
  return replyCount;
}
