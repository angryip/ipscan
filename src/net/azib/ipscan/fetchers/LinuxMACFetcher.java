package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.azib.ipscan.fetchers.UnixMACFetcher.getLocalMAC;

public class LinuxMACFetcher extends MACFetcher {
	private static final Path ARP_TABLE = Path.of("/proc/net/arp");
	private int flagsIndex;
	private int macIndex;
	private int macLength = 17;

	public LinuxMACFetcher() {
		try (var lines = arpLines()) {
			var line = lines.findFirst().orElse(null);
			if (line != null) {
				flagsIndex = line.indexOf("Flags");
				macIndex = line.indexOf("HW addr");
			}
		}
	}

	private static Stream<String> arpLines() {
		try {
			return Files.lines(ARP_TABLE);
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	@Override public String resolveMAC(ScanningSubject subject) {
		if (macIndex == 0) return null;
		try (var lines = arpLines()) {
			var ip = subject.getAddress().getHostAddress();
			return lines
				.filter(line -> line.startsWith(ip + " ") && !line.substring(flagsIndex, flagsIndex + 3).equals("0x0")).findFirst()
				.map(line -> line.substring(macIndex, macIndex + macLength).toUpperCase())
				.orElse(getLocalMAC(subject));
		}
		catch (Exception e) {
			return null;
		}
	}
}
