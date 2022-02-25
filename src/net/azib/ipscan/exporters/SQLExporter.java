package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;

/**
 * SQL Exporter
 * <p/>
 * Exports results as an SQL inserts, suitable for sqlite, mysql, etc,
 * optionally preceding by a 'create table'.
 * TODO: implement SQLExporter
 *
 * @author Anton Keks
 */
public class SQLExporter extends AbstractExporter {

	static final char DELIMETER = ':';
	static final String TABLE_NAME = "scan";
	static final char COMMA = ',';

	public String getId() {
		return "exporter.sql";
	}

	public String getFilenameExtension() {
		return "sql";
	}

	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		super.start(outputStream, feederInfo);

		if (!append) {
			output.println("DROP TABLE IF EXISTS scan;");
		}
	}

	public void setFetchers(String[] fetcherNames) throws IOException {
		if (!append) {
			output.write("CREATE TABLE " + TABLE_NAME + " (`" + fetcherNames[0] + "` varchar(20)");
			for (int i = 1; i < fetcherNames.length; i++) {
				output.write(COMMA);
				output.print(" `" + fetcherNames[i] + "` ");
				output.write("varchar(20)"); //Default type
			}
			output.println(");");
		}
	}

	public void nextAddressResults(Object[] results) throws IOException {
		output.print("INSERT INTO " + TABLE_NAME + " VALUES ('" + results[0]+"'");
		for (int i = 1; i < results.length; i++) {
			Object result = results[i];
			output.print(COMMA);
			output.print(" ");
			output.print("'" + result + "'");
		}
		output.println(");");
	}


}
