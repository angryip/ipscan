/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.exporters.ExportProcessor;
import net.azib.ipscan.exporters.Exporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.feeders.FeederCreator;

/**
 * CommandLineProcessor
 *
 * @author Anton Keks
 */
public class CommandLineProcessor implements StateTransitionListener {
	
	private final FeederCreator[] feederCreators;
	private final ExporterRegistry exporters;
	private StateMachine stateMachine;
	private ScanningResultList scanningResults;
	
	FeederCreator feederCreator;
	String[] feederArgs;
	Exporter exporter;
	String outputFilename;
	
	boolean autoStart;
	boolean autoExit;
	boolean appendToFile;
	
	CommandLineProcessor(FeederCreator[] feederCreators, ExporterRegistry exporters) {
		this.feederCreators = feederCreators;
		this.exporters = exporters;		
	}
	
	public CommandLineProcessor(FeederCreator[] feederCreators, ExporterRegistry exporters, StateMachine stateMachine, ScanningResultList scanningResults) {
		this(feederCreators, exporters);
		this.stateMachine = stateMachine;
		this.scanningResults = scanningResults;
		if (stateMachine != null)
			stateMachine.addTransitionListener(this);
	}
	
	public void parse(String ...args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.startsWith("-f:")) {
				if (feederCreator != null)
					throw new IllegalArgumentException("Only one feeder is allowed");
				feederCreator = findFeederCreator("feeder." + arg.substring(3));
				feederArgs = new String[feederCreator.serializePartsLabels().length];
				for (int j = 0; j < feederArgs.length; j++) {
					feederArgs[j] = args[++i];
					if (feederArgs[j].startsWith("-"))
						throw new IllegalArgumentException(feederCreator.getFeederName() + " requires " + feederArgs.length + " arguments");
				}
			}
			else
			if (arg.equals("-o")) {
				if (outputFilename != null)
					throw new IllegalArgumentException("Only one exporter is allowed");
				outputFilename = args[++i];
				if (outputFilename.startsWith("-")) 
					throw new IllegalArgumentException("Output filename missing");
				exporter = findExporter(outputFilename);
				// assume autoStart if exporting was specified
				autoStart = true;
			}
			else
			if (arg.startsWith("-")) {
				for (char option : arg.substring(1).toCharArray()) {
					switch (option) {
						case 's': autoStart = true; break;
						case 'e': autoExit = true; break;
						case 'a': appendToFile = true; break;
						default:
							throw new IllegalArgumentException("Unknown option: " + option);
					}
				}
			}
			else
				throw new IllegalArgumentException("Unknown argument: " + arg);
		}
		if (feederCreator == null)
			throw new IllegalArgumentException("Feeder missing");
		feederCreator.unserialize(feederArgs);
	}

	@Override
	public String toString() {
		// TODO: use labels!
		StringBuilder usage = new StringBuilder();
		usage.append("Pass the following arguments:\n");
		usage.append("[options] <feeder> <exporter>\n\n");
		usage.append("Where <feeder> is one of:\n");
		for (FeederCreator creator : feederCreators) {
			usage.append("-f:").append(shortId(creator.getFeederId()));
			for (String partLabel : creator.serializePartsLabels()) {
				usage.append(" <").append(Labels.getLabel(partLabel)).append(">");
			}
			usage.append('\n');
		}
		usage.append("\n<exporter> is one of:\n");
		for (Exporter exporter : exporters) {
			usage.append("-o filename.").append(shortId(exporter.getFilenameExtension())).append("\t\t").append(Labels.getLabel(exporter.getId())).append('\n');
		}
		usage.append("\nAnd possible [options] are (grouping allowed):\n");
		usage.append("-s\tstart scanning automatically\n");
		usage.append("-e\texit after saving\n");
		//usage.append("-a\tappend to the file, do not overwrite\n");
		return usage.toString();
	}

	private String shortId(String longId) {
		return longId.substring(longId.lastIndexOf('.')+1);
	}
	
	private FeederCreator findFeederCreator(String feederId) {
		for (FeederCreator creator : feederCreators) {
			if (feederId.equals(creator.getFeederId()))
				return creator;
		}
		throw new IllegalArgumentException("Feeder unknown: " + shortId(feederId));
	}
	
	private Exporter findExporter(String outputFilename) {
		return exporters.createExporter(outputFilename);
	}

	public void transitionTo(ScanningState state, Transition transition) {
		if (transition == Transition.INIT && autoStart) {
			// start scanning automatically
			stateMachine.transitionToNext();				
		}
		else
		if (transition == Transition.COMPLETE && state == ScanningState.IDLE && exporter != null) {
			// TODO: introduce SAVING state in order to show nice notification in the status bar
			ExportProcessor processor = new ExportProcessor(exporter, outputFilename);
			processor.process(scanningResults, null);
			if (autoExit) {
				System.err.println("Saved results to " + outputFilename);
				System.exit(0);
			}
		}
	}
}
