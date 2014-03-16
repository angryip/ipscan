package net.azib.ipscan.core.net;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

/**
 * JNA binding for iphlpapi.dll for ICMP and ARP support under Windows
 */
public interface WinIpHlpDll extends Library {
	public static WinIpHlpDll dll = Loader.load();
	static class Loader {
		public static WinIpHlpDll load() {
			try {
				return (WinIpHlpDll) Native.loadLibrary("iphlpapi", WinIpHlpDll.class);
			}
			catch (UnsatisfiedLinkError e) {
				return (WinIpHlpDll) Native.loadLibrary("icmp", WinIpHlpDll.class);
			}
		}
	}

	public static class AutoOrderedStructure extends Structure {
		// this is a requirement of newer JNA, possibly it won't work on some JVM, but probability is quite small
		@Override protected List<String> getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			for (Field field : getClass().getFields()) {
				if (!isStatic(field.getModifiers()))
					fields.add(field.getName());
			}
			return fields;
		}
	}

	/**
	 * Wrapper for Microsoft's <a href="http://msdn.microsoft.com/en-US/library/aa366045.aspx">IcmpCreateFile</a>
	 */
	public Pointer IcmpCreateFile();

	/**
	 * Wrapper for Microsoft's <a href="http://msdn.microsoft.com/en-us/library/aa366043.aspx">IcmpCloseHandle</a>
	 */
	public boolean IcmpCloseHandle(Pointer hIcmp);

	/**
	 * Wrapper for Microsoft's <a href="http://msdn.microsoft.com/EN-US/library/aa366050.aspx">IcmpSendEcho</a>
	 */
	public int IcmpSendEcho(
			Pointer hIcmp,
			IpAddrByVal destinationAddress,
			Pointer requestData,
			short requestSize,
			IpOptionInformationByRef requestOptions,
			Pointer replyBuffer,
			int replySize,
			int timeout
	);

	/**
	 * Wrapper for Microsoft's <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa366358(v=vs.85).aspx">SendARP</a>
	 */
	public int SendARP(
			IpAddrByVal destIP,
			int srcIP,
			Pointer pMacAddr,
			Pointer pPhyAddrLen
	);

	public static class IpAddr extends AutoOrderedStructure {
		public byte[] bytes = new byte[4];
	}

	public static class IpAddrByVal extends IpAddr implements Structure.ByValue {
	}

	public static class IpOptionInformation extends AutoOrderedStructure {
		public byte ttl;
		public byte tos;
		public byte flags;
		public byte optionsSize;
		public Pointer optionsData;
	}

	public static class IpOptionInformationByVal
			extends IpOptionInformation implements Structure.ByValue {
	}

	public static class IpOptionInformationByRef
			extends IpOptionInformation implements Structure.ByReference {
	}

	public static class IcmpEchoReply extends AutoOrderedStructure {
		public IpAddrByVal address;
		public int status;
		public int roundTripTime;
		public short dataSize;
		public short reserved;
		public Pointer data;
		public IpOptionInformationByVal options;

		public IcmpEchoReply() {
		}

		public IcmpEchoReply(Pointer p) {
			useMemory(p);
			read();
		}
	}
}
