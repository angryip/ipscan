package net.azib.ipscan.fetchers;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.azib.ipscan.fetchers.UnixMACFetcher.getLocalMAC;

public class LinuxMACFetcher extends MACFetcher {
	private static final Path ARP_TABLE = Path.of("/proc/net/arp");
	private int macIndex;
	private int macLength = 17;
	private String unavailableMac = "00:00:00:00:00:00";

	public LinuxMACFetcher() {
		macIndex = arpLines().findFirst().get().indexOf("HW addr");
	}

	private static Stream<String> arpLines() {
		try {
			return Files.lines(ARP_TABLE);
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	@Override public String resolveMAC(InetAddress address) {
		try {
			String ip = address.getHostAddress();
			return arpLines().filter(line -> line.startsWith(ip + " ")).findFirst()
				.map(line -> line.substring(macIndex, macIndex + macLength).toUpperCase())
				.filter(mac -> !unavailableMac.equals(mac))
				.orElse(getLocalMAC(address));
		}
		catch (Exception e) {
			return null;
		}
	}
}
