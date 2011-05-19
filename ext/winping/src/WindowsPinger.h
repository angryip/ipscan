/*
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 *
 * Windows JNI pinger using Microsoft's ICMP.DLL
 * Author: Anton Keks 
 */

#include <jni.h>
/* Header for class net_azib_ipscan_core_net_WindowsPinger */

#ifndef _Included_net_azib_ipscan_core_net_WindowsPinger
#define _Included_net_azib_ipscan_core_net_WindowsPinger
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCreateFile
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCreateFile
  (JNIEnv *, jclass);

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpSendEcho
 * Signature: (I[B[B[BI)I
 */
JNIEXPORT jint JNICALL Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpSendEcho
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray, jbyteArray, jint);

/*
 * Class:     net_azib_ipscan_core_net_WindowsPinger
 * Method:    nativeIcmpCloseHandle
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_azib_ipscan_core_net_WindowsPinger_nativeIcmpCloseHandle
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
