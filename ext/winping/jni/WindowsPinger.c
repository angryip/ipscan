/*
 * Copyright 2007 Anton Keks
 */

#include <windows.h>

#include "WindowsPinger.h"

FARPROC IcmpCreateFile;

typedef BOOL (FAR WINAPI *TIcmpCloseHandle)(HANDLE IcmpHandle);
TIcmpCloseHandle IcmpCloseHandle;

typedef DWORD (FAR WINAPI *TIcmpSendEcho)(
  HANDLE IcmpHandle, 	/* handle returned from IcmpCreateFile() */
  u_long DestAddress, /* destination IP address (in network order) */
  LPVOID RequestData, /* pointer to buffer to send */
  WORD RequestSize,	/* length of data in buffer */
  LPIPINFO RequestOptns,  /* see Note 2 */
  LPVOID ReplyBuffer, /* see Note 1 */
  DWORD ReplySize, 	/* length of reply (must allow at least 1 reply) */
  DWORD Timeout 	/* time in milliseconds to wait for reply */
);
TIcmpSendEcho IcmpSendEcho;

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCreateFile
 */
JNIEXPORT jint JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCreateFile
(JNIEnv *env, jclass cls)
{
  HMODULE hICMP = LoadLibrary("icmp.dll");
  if (!hICMP) {
    // newer versions of Windows should include this one instead
    hICMP = LoadLibrary("iphlpapi.dll");
  }
  if (!hICMP) {
    return -1;
  }
  IcmpCreateFile  = (FARPROC)GetProcAddress(hICMP, "IcmpCreateFile");
  IcmpCloseHandle = (TIcmpCloseHandle)GetProcAddress(hICMP, "IcmpCloseHandle");
  IcmpSendEcho    = (TIcmpSendEcho)GetProcAddress(hICMP, "IcmpSendEcho");
  
  return IcmpCreateFile();
}

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCloseHandle
 */
JNIEXPORT jint JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCloseHandle
(JNIEnv *env, jclass cls, jint handle)
{
  return IcmpCloseHandle(handle);
}

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpSendEcho
 */
JNIEXPORT jint JNICALL
Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpSendEcho
(JNIEnv *env, jclass cls, jint handle,
 jbyteArray address, jbyteArray pingData, jbyteArray replyData, jint timeout)
{
  DWORD replyCount;
  IPINFO IPInfo;
  jbyte *addrBuf, *pingDataBuf, replyDataBuf;
  jclass replyClass;
  jfieldID fid;
  
  IPInfo.Ttl = 128;
  IPInfo.Tos = 0;
  IPInfo.Flags = 0;
  IPInfo.OptionsSize = 0;
  IPInfo.OptionsData = NULL;
  
  addrBuf = env->GetByteArrayElements(env, address, NULL);
  pingDataBuf = env->GetByteArrayElements(env, pingData, NULL);
  replyDataBuf = env->GetByteArrayElements(env, replyData, NULL);
  
  replyCount = IcmpSendEcho(handle, (DWORD)*addrBuf, pingDataBuf, env->GetArrayLength(pingData) * sizeof(jbyte),
                           &IPInfo, replyDataBuf, env->GetArrayLength(replyData), timeout);
                            
  env->ReleaseByteArrayElements(env, address, addrBuf, JNI_ABORT);
  env->ReleaseByteArrayElements(env, pingData, pingDataBuf, JNI_ABORT);
  env->ReleaseByteArrayElements(env, replyData, replyDataBuf, NULL);
  
  return replyCount;
}
