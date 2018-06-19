package net.azib.ipscan.fetchers;

		import net.azib.ipscan.config.ScannerConfig;
		import net.azib.ipscan.core.ScanningResult;
		import net.azib.ipscan.core.ScanningSubject;
		import net.azib.ipscan.core.net.PingResult;
		import net.azib.ipscan.core.net.PingerRegistry;

		import javax.inject.Inject;

/**
 * PacketLossFetcher shares pinging results with PingFetcher
 * and returns the Package loss field of the received packet.
 *
 * @author Gustavo Pistore
 */
public class PacketLossFetcher extends PingFetcher {

	@Inject public PacketLossFetcher(PingerRegistry pingerRegistry, ScannerConfig scannerConfig) {
		super(pingerRegistry, scannerConfig);
	}

	public String getId() {
		return "fetcher.packetloss";
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ScanningResult.ResultType.ALIVE : ScanningResult.ResultType.DEAD);

		return result.getPacketLoss() + "/" + result.getPacketCount()+" ("+result.getPacketLossPercent()+"%)";
	}
}