/*------------------------------------------------------------------    
 * Filename: MS_ICMP.H
 *
 * Description:
 *  Prototypes and typedefs Microsoft's ICMP.DLL functions & structs
 *  for access to Internet Control Message Protocol.  This is capable
 *  of doing "ping or "traceroute", although beware that Microsoft 
 *  discourages the use of these APIs.
 *
 * Some Background:
 *
 * The standard Berkeley Sockets SOCK_RAW socket type, is normally used
 * to create ping (echo request/reply), and sometimes traceroute applications
 * (the original traceroute application from Van Jacobson used UDP, rather
 * than ICMP). Microsoft's WinSock version 2 implementations for NT4 and 
 * Windows 95 support raw sockets, but none of their WinSock version 1.1
 * implementations (WFWG, NT3.x or standard Windows 95) did.
 *
 * Microsoft has their own API for an ICMP.DLL that their ping and tracert
 * applications use (by the way, they are both non-GUI text-based console
 * applications. This is a proprietary API, and all function calls that 
 * involve network functions operate in blocking mode. They still include 
 * it with WinSock 2 implementations.
 *
 * There is little documentation available (I first found it in the Win32
 * SDK in \MSTOOLS\ICMP, and it exists on the MS&nbsp;Developers' Network
 * CD-ROM now, also). Microsoft disclaims this API about as strongly as 
 * possible.  The README.TXT that accompanies it says:
 *
 * [DISCLAIMER]
 * 
 * We have had requests in the past to expose the functions exported from
 * icmp.dll. The files in this directory are provided for your convenience
 * in building applications which make use of ICMPSendEcho(). Notice that
 * the functions in icmp.dll are not considered part of the Win32 API and
 * will not be supported in future releases. Once we have a more complete
 * solution in the operating system, this DLL, and the functions it exports,
 * will be dropped.    
 *      
 * [DOCUMENTATION]     
 *
 * The ICMPSendEcho() function sends an ICMP echo request to the specified
 * destination IP address and returns any replies received within the timeout
 * specified. The API is synchronous, requiring the process to spawn a thread
 * before calling the API to avoid blocking. An open IcmpHandle is required
 * for the request to complete. IcmpCreateFile() and IcmpCloseHandle() 
 * functions are used to create and destroy the context handle.</P>
 */
#include <windows.h>

HANDLE WINAPI IcmpCreateFile(VOID); /* INVALID_HANDLE_VALUE on error */
BOOL WINAPI IcmpCloseHandle(HANDLE IcmpHandle); /* FALSE on error */

/* Note 2: For the most part, you can refer to RFC 791 for detials 
 * on how to fill in values for the IP option information structure. 
 */
typedef struct ip_option_information {
    u_char Ttl;		/* Time To Live (used for traceroute) */
    u_char Tos; 	/* Type Of Service (usually 0) */
    u_char Flags; 	/* IP header flags (usually 0) */
    u_char OptionsSize; /* Size of options data (usually 0, max 40) */
    u_char FAR *OptionsData;   /* Options data buffer */
} IPINFO, *PIPINFO, FAR *LPIPINFO;


/* Note 1: The Reply Buffer will have an array of ICMP_ECHO_REPLY
 * structures, followed by options and the data in ICMP echo reply
 * datagram received. You must have room for at least one ICMP
 * echo reply structure, plus 8 bytes for an ICMP header. 
 */
typedef struct icmp_echo_reply {
    u_long Address; 	/* source address *.
    u_long Status;	/* IP status value (see below) */
    u_long RTTime;	/* Round Trip Time in milliseconds */
    u_short DataSize; 	/* reply data size */
    u_short Reserved; 	/* */
    void FAR *Data; 	/* reply data buffer */
    struct ip_option_information Options; /* reply options */
} ICMPECHO, *PICMPECHO, FAR *LPICMPECHO;


DWORD WINAPI IcmpSendEcho(
    HANDLE IcmpHandle, 	/* handle returned from IcmpCreateFile() */
    u_long DestAddress, /* destination IP address (in network order) */
    LPVOID RequestData, /* pointer to buffer to send */
    WORD RequestSize,	/* length of data in buffer */
    LPIPINFO RequestOptns,  /* see Note 2 */
    LPVOID ReplyBuffer, /* see Note 1 */
    DWORD ReplySize, 	/* length of reply (must allow at least 1 reply) */
    DWORD Timeout 	/* time in milliseconds to wait for reply */
);


#define IP_STATUS_BASE 11000
#define IP_SUCCESS 0
#define IP_BUF_TOO_SMALL (IP_STATUS_BASE + 1)
#define IP_DEST_NET_UNREACHABLE (IP_STATUS_BASE + 2)
#define IP_DEST_HOST_UNREACHABLE (IP_STATUS_BASE + 3)
#define IP_DEST_PROT_UNREACHABLE (IP_STATUS_BASE + 4)
#define IP_DEST_PORT_UNREACHABLE (IP_STATUS_BASE + 5)
#define IP_NO_RESOURCES (IP_STATUS_BASE + 6)
#define IP_BAD_OPTION (IP_STATUS_BASE + 7)
#define IP_HW_ERROR (IP_STATUS_BASE + 8)
#define IP_PACKET_TOO_BIG (IP_STATUS_BASE + 9)
#define IP_REQ_TIMED_OUT (IP_STATUS_BASE + 10)
#define IP_BAD_REQ (IP_STATUS_BASE + 11)
#define IP_BAD_ROUTE (IP_STATUS_BASE + 12)
#define IP_TTL_EXPIRED_TRANSIT (IP_STATUS_BASE + 13)
#define IP_TTL_EXPIRED_REASSEM (IP_STATUS_BASE + 14)
#define IP_PARAM_PROBLEM (IP_STATUS_BASE + 15)
#define IP_SOURCE_QUENCH (IP_STATUS_BASE + 16)
#define IP_OPTION_TOO_BIG (IP_STATUS_BASE + 17)
#define IP_BAD_DESTINATION (IP_STATUS_BASE + 18)
#define IP_ADDR_DELETED (IP_STATUS_BASE + 19)
#define IP_SPEC_MTU_CHANGE (IP_STATUS_BASE + 20)
#define IP_MTU_CHANGE (IP_STATUS_BASE + 21)
#define IP_UNLOAD (IP_STATUS_BASE + 22)
#define IP_GENERAL_FAILURE (IP_STATUS_BASE + 50)
#define MAX_IP_STATUS IP_GENERAL_FAILURE
#define IP_PENDING (IP_STATUS_BASE + 255)

