package net.azib.ipscan.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.Labels;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class LabelsTest extends TestCase {

	protected void setUp() throws Exception {
		// Labels should initialize themselves on first load
	}
	
	public void testReinitialize() {
		Labels.initialize(new Locale("en"));
		Object oldInternalInstance = Labels.getInstance();
		Labels.initialize(new Locale("en"));
		assertTrue(oldInternalInstance == Labels.getInstance());
	}
	
	public void testInitialize() {
		Labels.initialize(new Locale("en"));
		Object oldInternalInstance = Labels.getInstance();
		Labels.initialize(new Locale("ee"));
		assertFalse(oldInternalInstance == Labels.getInstance());
	}

	public void testSimpleLabel() {
		assertEquals("&File", Labels.getInstance().getString("menu.file"));
	}
	
	public void testInexistentLabel() {
		try {
			Labels.getInstance().getString("abra-cadabra");
			fail();
		}
		catch (Exception e) {
			// exception is good
		}
	}
	
	public void testImageAsStream() throws IOException {
		InputStream stream = Labels.getInstance().getImageAsStream("button.start.img");
		// Now check the first bytes of PNG image header
		assertEquals('G', stream.read());
		assertEquals('I', stream.read());
		assertEquals('F', stream.read());
		stream.close();
	}
	
	/**
	 * This test recursively processes all source files and tries
	 * to resolve every label it finds. 
	 */
	public void testAllLabels() throws IOException {
		File srcDir = new File("src");
		recurseAndTestLabels(srcDir);
	}

	private void recurseAndTestLabels(File dir) throws IOException {
		String files[] = dir.list();
		for (int i = 0; i < files.length; i++) {
			File file = new File(dir, files[i]);
			if (file.isDirectory()) {
				recurseAndTestLabels(file);
			}
			else 
			if (file.getName().endsWith(".java")) {
				findAndTestLabels(file);
			}
		}
	}

	private void findAndTestLabels(File file) throws IOException {
		// TODO: tune these regexps
		final Pattern LABELS_REGEX = Pattern.compile("Label.{1,60}\"([a-z]\\w+?\\.[a-z][\\w.]+?)\"");
		final Pattern EXCEPTION_REGEX = Pattern.compile("new\\s+?(\\w+?Exception)\\(\"([\\w.]+?)\"");
		
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();
		String fileLine;
		while ((fileLine = fileReader.readLine()) != null) {
			sb.append(fileLine);
		}
		fileReader.close();
		String fileContent = sb.toString();
		
		String key = null;
//		String value = null;
		try {
//			System.out.println(file.getPath());

			Matcher matcher = LABELS_REGEX.matcher(fileContent);
			while (matcher.find()) {
				// try to load the label
				key = matcher.group(1);
//				value = 
				Labels.getInstance().getString(key);
//				System.out.println(key + "=" + value);
			}

			matcher = EXCEPTION_REGEX.matcher(fileContent);
			while (matcher.find()) {
				// try to load the label
				key = "exception." + matcher.group(1) + "." + matcher.group(2);
//				value = 
				Labels.getInstance().getString(key);
//				System.out.println(key + "=" + value);
			}
		}
		catch (MissingResourceException e) {
			throw new AssertionFailedError("Label not found: " + key + ", in file: " + file.getPath());
		}
	}
}
