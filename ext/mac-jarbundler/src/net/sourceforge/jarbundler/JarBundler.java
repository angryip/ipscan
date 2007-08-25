/*
 * A Mac OS X Jar Bundler Ant Task.
 *
 * Copyright (c) 2003, Seth J. Morabito <sethm@loomcom.com> All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See  the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.jarbundler;

// This package's imports
import net.sourceforge.jarbundler.AppBundleProperties;
import net.sourceforge.jarbundler.DocumentType;
import net.sourceforge.jarbundler.JavaProperty;
import net.sourceforge.jarbundler.PropertyListWriter;

// Java I/O
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

// Java Utility
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// Apache Jakarta
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Delete;

import org.apache.tools.ant.util.FileUtils;


// Java language imports
import java.lang.Boolean;
import java.lang.Process;
import java.lang.Runtime;
import java.lang.String;
import java.lang.System;

/**
 * <p>
 * An ant task which creates a Mac OS X Application Bundle for a Java
 * application.
 * </p>
 * 
 * <dl>
 * <dt>dir</dt>
 * <dd>The directory into which to put the new application bundle.</dd>
 * <dt>name</dt>
 * <dd>The name of the application bundle. Note that the maximum length of this
 * name is 16 characters, and it will be silently cropped if it is longer than
 * this.</dd>
 * <dt>mainclass</dt>
 * <dd>The main Java class to call when running the application.</dd>
 * </dl>
 * 
 * <p>
 * One of the following three MUST be used:
 * 
 * <ol>
 * <li>jars Space or comma-separated list of JAR files to include.; OR</li>
 * <li>One or more nested &lt;jarfileset&gt;s. These are normal ANT FileSets;
 * OR </li>
 * <li>One or more nested &lt;jarfilelist&gt;s. These are standard ANT
 * FileLists. </li>
 * </ol>
 * 
 * <p>
 * Optional attributes:
 * 
 * <p>
 * The following attributes are not required, but you can use them to override
 * default behavior.
 * 
 * <dl>
 * <dt>verbose
 * <dd>If true, show more verbose output while running the task
 * 
 * <dt>version
 * <dd>Version information about your application (e.g., "1.0")
 * 
 * <dt>infostring
 * <dd>String to show in the "Get Info" dialog
 * </dl>
 * 
 * These attributes control the fine-tuning of the "Mac OS X" look and feel.
 * 
 * <dl>
 * <dt>arguments
 * <dd>Command line arguments. (no default)
 * 
 * <dt>smalltabs
 * <dd>Use small tabs. (default "false") Deprecated under JVM 1.4.1
 * 
 * <dt>antialiasedgraphics
 * <dd>Use anti-aliased graphics (default "false")
 * 
 * <dt>antialiasedtext
 * <dd>Use anti-aliased text (default "false")
 * 
 * <dt>bundleid
 * <dd>Unique identifier for this bundle, in the form of a Java package. No
 * default.
 * 
 * <dt>buildnumber
 * <dd>Unique identifier for this build
 * 
 * <dt>developmentregion
 * <dd>Development Region. Default "English".
 * 
 * <dt>execs
 * <dd>Files to be copied into "Resources/MacOS" and made executable
 * 
 * <dt>liveresize
 * <dd>Use "Live resizing" (default "false") Deprecated under JVM 1.4.1
 * 
 * 
 * <dt>growbox
 * <dd>Show growbox (default "true")
 * 
 * <dt>growboxintrudes
 * <dd>Intruding growbox (default "false") Deprecated under JVM 1.4.1
 * 
 * <dt>screenmenu
 * <dd>Put swing menu into Mac OS X menu bar.
 * 
 * <dt>type
 * <dd>Bundle type (default "APPL")
 * 
 * <dt>signature
 * <dd>Bundle Signature (default "????")
 * 
 * <dt>stubfile
 * <dd>The Java Application Stub file to copy for your application (default
 * MacOS system stub file)
 * </dl>
 * 
 * <p>
 * Rarely used optional attributes.
 * <dl>
 * <dt>chmod
 * <dd>Full path to the chmod command. This almost certainly does NOT need to
 * be set.
 * </dl>
 * 
 * <p>
 * The task also supports nested &lt;execfileset&gt; and/or &lt;execfilelist&gt;
 * elements, and &lt;resourcefileset&gt; and/or &lt;resourcefilelist&gt;
 * elements, which are standard Ant FileSet and FileList elements. In the first
 * case, the referenced files are copied to the <code>Contents/MacOS</code>
 * directory and made executable, and in the second they are copied to the
 * <code>Contents/Resources</code> directory and not made executable. If you
 * winrces, note that in fact the files are installed in locations which have
 * the same relation to the <code>Contents/Resources</code> directory as the
 * files in the FileSet or FileList have to the 'dir' attribute. Thus in the
 * case:
 * 
 * <pre>
 *   &lt;resourcefileset dir=&quot;builddir/architectures&quot;
 *                       includes=&quot;ppc/*.jnilib&quot;/&gt;
 * </pre>
 * 
 * <p>
 * the <code>*.jnilib</code> files will be installed in
 * <code>Contents/Resources/ppc</code>.
 * 
 * <p>
 * The task supports a nested &lt;javaproperty&gt; element, which allows you to
 * specify further properties which are set for the JVM when the application is
 * launched. This takes a required <code>key</code> attribute, giving the
 * property key, plus an attribute giving the property value, which may be one
 * of <code>value</code>, giving the string value of the property,
 * <code>file</code>, setting the value of the property to be the absolute
 * path of the given file, or <code>path</code>, which sets the value to the
 * given path. If you are setting paths here, recall that, within the bundle,
 * <code>$APP_PACKAGE</code> is set to the root directory of the bundle (ie,
 * the path to the <code>foo.app</code> directory), and <code>$JAVAROOT</code>
 * to the directory <code>Contents/Resources/Java</code>.
 * 
 * <p>
 * Minimum example:
 * 
 * <pre>
 *  
 *    &lt;jarbundler dir=&quot;release&quot; name=&quot;Bar Project&quot; mainclass=&quot;org.bar.Main&quot;
 *        jars=&quot;bin/Bar.jar&quot; /&gt;
 * </pre>
 * 
 * <p>
 * Using Filesets
 * 
 * <pre>
 *    &lt;jarbundler dir=&quot;release&quot; name=&quot;Bar Project&quot; mainclass=&quot;org.bar.Main&quot;&gt;
 *      &lt;jarfileset dir=&quot;bin&quot;&gt;
 *        &lt;include name=&quot;*.jar&quot; /&gt;
 *        &lt;exclude name=&quot;test.jar&quot; /&gt;
 *      &lt;/jarfileset&gt;
 *      &lt;execfileset dir=&quot;execs&quot;&gt;
 *        &lt;include name=&quot;**&quot; /&gt;
 *      &lt;/execfileset&gt;
 *    &lt;/jarbundler&gt;
 * </pre>
 * 
 * <p>
 * Much Longer example:
 * </p>
 * 
 * <pre>
 *    &lt;jarbundler dir=&quot;release&quot;
 *                name=&quot;Foo Project&quot;
 *                mainclass=&quot;org.bar.Main&quot;
 *                version=&quot;1.0 b 1&quot;
 *                infostring=&quot;Foo Project (c) 2002&quot; 
 *                type=&quot;APPL&quot;
 *                jars=&quot;bin/foo.jar bin/bar.jar&quot;
 *                execs=&quot;exec/foobar&quot;
 *                signature=&quot;????&quot;
 *                workingdirectory=&quot;temp&quot;
 *                icon=&quot;resources/foo.icns&quot;
 *                jvmversion=&quot;1.4.1+&quot;
 *                vmoptions=&quot;-Xmx256m&quot;/&gt;
 * </pre>
 * 
 * http://developer.apple.com/documentation/MacOSX/Conceptual/BPRuntimeConfig/
 */
public class JarBundler extends MatchingTask {

	private static final String DEFAULT_STUB = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";

	private static final String ABOUTMENU_KEY = "com.apple.mrj.application.apple.menu.about.name";
	private static final Set menuItems = new HashSet();
	private File mAppIcon;

	private File mRootDir;

	private final List mJavaFileLists = new ArrayList();
	private final List mJarFileSets = new ArrayList();

	private final List mExecFileLists = new ArrayList();
	private final List mExecFileSets = new ArrayList();

	private final List mResourceFileLists = new ArrayList();
	private final List mResourceFileSets = new ArrayList();

	private final List mJarFileLists = new ArrayList();
	private final List mJavaFileSets = new ArrayList();

	private final List mExtraClassPathFileLists = new ArrayList();
	private final List mExtraClassPathFileSets = new ArrayList();

	private final List mJarAttrs = new ArrayList();

	private final List mExecAttrs = new ArrayList();

	private final List mExtraClassPathAttrs = new ArrayList();
	
	private final List mHelpBooks = new ArrayList();

	private boolean mVerbose = false;
	private boolean mShowPlist = false;

	// Java properties used by Mac OS X Java applications

	private File mStubFile = new File(DEFAULT_STUB);

	private Boolean mAntiAliasedGraphics = null;

	private Boolean mAntiAliasedText = null;

	private Boolean mLiveResize = null;

	private Boolean mScreenMenuBar = null;

	private Boolean mGrowbox = null;

	private Boolean mGrowboxIntrudes = null;

	// The root of the application bundle
	private File bundleDir;

	// "Contents" directory
	private File mContentsDir;

	// "Contents/MacOS" directory
	private File mMacOsDir;

	// "Contents/Resources" directory
	private File mResourcesDir;

	// "Contents/Resources/Java" directory
	private File mJavaDir;

	// Full path to the 'chmod' command. Can be overridden
	// with the 'chmod' attribute. Won't cause any harm if
	// not set, or if this executable doesn't exist.


	private AppBundleProperties bundleProperties = new AppBundleProperties();

	// Ant file utilities

	private FileUtils mFileUtils = FileUtils.getFileUtils();

	/***************************************************************************
	 * Retreive task attributes
	 **************************************************************************/

	/**
	 * Arguments to the
	 * 
	 * @param s
	 *            The arguments to pass to the application being launched.
	 */
	public void setArguments(String s) {
		bundleProperties.setArguments(s);
	}

	/**
	 * Override the stub file path to build on non-MacOS platforms
	 * 
	 * @param file
	 *            the path to the stub file
	 */
	public void setStubFile(File file) {
		mStubFile = (file.exists()) ? file : new File(DEFAULT_STUB);
		bundleProperties.setCFBundleExecutable(file.getName());
	}

	/**
	 * Setter for the "dir" attribute (required)
	 */
	public void setDir(File f) {
		mRootDir = f;
	}

	/**
	 * Setter for the "name" attribute (required) This attribute names the
	 * output application bundle and asks as the CFBundleName if 'bundlename' is
	 * not specified
	 */
	public void setName(String s) {
		bundleProperties.setApplicationName(s);
	}

	/**
	 * Setter for the "shortname" attribute (optional) This key identifies the
	 * short name of the bundle. This name should be less than 16 characters
	 * long and be suitable for displaying in the menu and the About box. The
	 * name is (silently) cropped to this if necessary.
	 */
	public void setShortName(String s) {
		bundleProperties.setCFBundleName(s);
	}

	/**
	 * Setter for the "mainclass" attribute (required)
	 */
	public void setMainClass(String s) {
		bundleProperties.setMainClass(s);
	}

	/**
	 * Setter for the "WorkingDirectory" attribute (optional)
	 */
	public void setWorkingDirectory(String s) {
		bundleProperties.setWorkingDirectory(s);
	}

	/**
	 * Setter for the "icon" attribute (optional)
	 */

	public void setIcon(File f) {
		mAppIcon = f;
		bundleProperties.setCFBundleIconFile(f.getName());
	}

	/**
	 * Setter for the "bundleid" attribute (optional) This key specifies a
	 * unique identifier string for the bundle. This identifier should be in the
	 * form of a Java-style package name, for example com.mycompany.myapp. The
	 * bundle identifier can be used to locate the bundle at runtime. The
	 * preferences system uses this string to identify applications uniquely.
	 * 
	 * No default.
	 */
	public void setBundleid(String s) {
		bundleProperties.setCFBundleIdentifier(s);
	}

	/**
	 * Setter for the "developmentregion" attribute(optional) Default "English".
	 */
	public void setDevelopmentregion(String s) {
		bundleProperties.setCFBundleDevelopmentRegion(s);
	}

	/**
	 * Setter for the "aboutmenuname" attribute (optional)
	 */
	public void setAboutmenuname(String s) {
		bundleProperties.setCFBundleName(s);
	}

	/**
	 * Setter for the "smalltabs" attribute (optional)
	 */
	public void setSmallTabs(boolean b) {
		bundleProperties.addJavaProperty("com.apple.smallTabs", new Boolean(b)
				.toString());
	}

	/**
	 * Setter for the "vmoptions" attribute (optional)
	 */
	public void setVmoptions(String s) {
		bundleProperties.setVMOptions(s);
	}

	/**
	 * Setter for the "antialiasedgraphics" attribute (optional)
	 */
	public void setAntialiasedgraphics(boolean b) {
		mAntiAliasedGraphics = new Boolean(b);
	}

	/**
	 * Setter for the "antialiasedtext" attribute (optional)
	 */
	public void setAntialiasedtext(boolean b) {
		mAntiAliasedText = new Boolean(b);
	}

	/**
	 * Setter for the "screenmenu" attribute (optional)
	 */
	public void setScreenmenu(boolean b) {
		mScreenMenuBar = new Boolean(b);
	}

	/**
	 * Setter for the "growbox" attribute (optional)
	 */
	public void setGrowbox(boolean b) {
		mGrowbox = new Boolean(b);
	}

	/**
	 * Setter for the "growboxintrudes" attribute (optional)
	 */
	public void setGrowboxintrudes(boolean b) {
		mGrowboxIntrudes = new Boolean(b);
	}

	/**
	 * Setter for the "liveresize" attribute (optional)
	 */
	public void setLiveresize(boolean b) {
		mLiveResize = new Boolean(b);
	}

	/**
	 * Setter for the "type" attribute (optional)
	 */
	public void setType(String s) {
		bundleProperties.setCFBundlePackageType(s);
	}

	/**
	 * Setter for the "signature" attribute (optional)
	 */
	public void setSignature(String s) {
		bundleProperties.setCFBundleSignature(s);
	}

	/**
	 * Setter for the "jvmversion" attribute (optional)
	 */
	public void setJvmversion(String s) {
		bundleProperties.setJVMVersion(s);
	}

	/**
	 * Setter for the "infostring" attribute (optional) This key identifies a
	 * human-readable plain text string displaying the copyright information for
	 * the bundle. The Finder displays this information in the Info window of
	 * the bundle. (This string was also known as the long version string in Mac
	 * OS 9). The format of the key should be of the following format: "&copy;
	 * Great Software, Inc, 1999". You can localize this string by including it
	 * in the InfoPlist.strings file of the appropriate .lproj directory.
	 */

	public void setInfoString(String s) {
		bundleProperties.setCFBundleGetInfoString(s);
	}

	/**
	 * Setter for the "shortinfostring" attribute (optional) This key identifies
	 * the marketing version of the bundle. The marketing version is a string
	 * that usually displays the major and minor version of the bundle. This
	 * string is usually of the form n.n.n where n is a number. The first number
	 * is the major version number of the bundle. The second and third numbers
	 * are minor revision numbers. You may omit minor revision numbers as
	 * appropriate. The value of this key is displayed in the default About box
	 * for Cocoa applications.
	 * 
	 * The value for this key differs from the value for "CFBundleVersion",
	 * which identifies a specific build number. The CFBundleShortVersionString
	 * value represents a more formal version that does not change with every
	 * build.
	 */
	public void setShortInfoString(String s) {
		setVersion(s);
	}

	/**
	 * Setter for the "verbose" attribute (optional)
	 */
	public void setVerbose(boolean verbose) {
		this.mVerbose = verbose;
	}
	public void setShowPlist(boolean showPlist) {
		this.mShowPlist = showPlist;
	}




	/**
	 * Setter for the "buildnumber" attribute (optional) This key specifies the
	 * exact build version of the bundle. This string is usually of the form
	 * nn.n.nxnnn where n is a digit and x is a character from the set [abdf].
	 * The first number is the major version number of the bundle and can
	 * contain one or two digits to represent a number in the range 0-99. The
	 * second and third numbers are minor revision numbers and must be a single
	 * numeric digit. The fourth set of digits is the specific build number for
	 * the release.
	 * 
	 * You may omit minor revision and build number information as appropriate.
	 * You may also omit major and minor revision information and specify only a
	 * build number. For example, valid version numbers include: 1.0.1,
	 * 1.2.1b10, 1.2d200, d125, 101, and 1.0.
	 * 
	 * The value of this key typically changes between builds and is displayed
	 * in the Cocoa About panel in parenthesis. To specify the version
	 * information of a released bundle, use the CFBundleShortVersionString key.
	 */
	public void setBuild(String s) {
		bundleProperties.setCFBundleVersion(s);
	}

	/**
	 * Setter for the version attribute (optional). It is this property, not
	 * CFBundleVersion, which should receive the `short' version string. See for
	 * example
	 * <http://developer.apple.com/documentation/MacOSX/Conceptual/BPRuntimeConfig/>
	 */
	public void setVersion(String s) {
		bundleProperties.setCFBundleShortVersionString(s);
	}

	public void setHelpBookFolder(String s) {
		bundleProperties.setCFBundleHelpBookFolder(s);
	}

	public void setHelpBookName(String s) {
		bundleProperties.setCFBundleHelpBookName(s);
	}

	/**
	 * Setter for the "jars" attribute (required if no "jarfileset" is present)
	 */
	public void setJars(String s) {
		PatternSet patset = new PatternSet();
		patset.setIncludes(s);

		String[] jarNames = patset.getIncludePatterns(getProject());

		for (int i = 0; i < jarNames.length; i++)
			mJarAttrs.add(getProject().resolveFile(jarNames[i]));
	}

	/**
	 * Setter for the "jar" attribute (required if no "jarfileset" is present)
	 */
	public void setJar(File s) {
		mJarAttrs.add(s);
	}

	/**
	 * Setter for the "execs" attribute (optional)
	 */
	public void setExecs(String s) {
		PatternSet patset = new PatternSet();
		patset.setIncludes(s);

		String[] execNames = patset.getIncludePatterns(getProject());

		for (int i = 0; i < execNames.length; i++) {
			File f = new File(execNames[i]);
			mExecAttrs.add(f);
		}
	}

	/**
	 * Setter for the "extraclasspath" attribute (optional)
	 */
	public void setExtraclasspath(String s) {
		PatternSet patset = new PatternSet();
		patset.setIncludes(s);

		String[] cpNames = patset.getIncludePatterns(getProject());

		for (int i = 0; i < cpNames.length; i++) {
			File f = new File(cpNames[i]);
			mExtraClassPathAttrs.add(f);
		}
	}

	/**
	 * Set the 'chmod' executable.
	 */
	public void setChmod(String s) {
		log("The \"chmod\" attribute has deprecaited, using the ANT Chmod task internally");
	}

	/***************************************************************************
	 * Nested tasks - derived from FileList and FileSet
	 **************************************************************************/

	public void addJarfileset(FileSet fs) {
		mJarFileSets.add(fs);
	}

	public void addJarfilelist(FileList fl) {
		mJarFileLists.add(fl);
	}

	public void addExecfileset(FileSet fs) {
		mExecFileSets.add(fs);
	}

	public void addExecfilelist(FileList fl) {
		mExecFileLists.add(fl);
	}

	public void addResourcefileset(FileSet fs) {
		mResourceFileSets.add(fs);
	}

	public void addResourcefilelist(FileList fl) {
		mResourceFileLists.add(fl);
	}

	public void addJavafileset(FileSet fs) {
		mJavaFileSets.add(fs);
	}

	public void addJavafilelist(FileList fl) {
		mJavaFileLists.add(fl);
	}

	public void addExtraclasspathfileset(FileSet fs) {
		mExtraClassPathFileSets.add(fs);
	}

	public void addExtraclasspathfilelist(FileList fl) {
		mExtraClassPathFileLists.add(fl);
	}


	/***************************************************************************
	 * Nested tasks - new tasks with custom attributes
	 **************************************************************************/


	public void addConfiguredJavaProperty(JavaProperty javaProperty)
			throws BuildException {

		String name = javaProperty.getName();
		String value = javaProperty.getValue();

		if ((name == null) || (value == null))
			throw new BuildException(
					"'<javaproperty>' must have both 'name' and 'value' attibutes");

		bundleProperties.addJavaProperty(name, value);
	}

	public void addConfiguredDocumentType(DocumentType documentType) throws BuildException {

		String name = documentType.getName();
		String role = documentType.getRole();
		List osTypes = documentType.getOSTypes();
		List extensions = documentType.getExtensions();
		List mimeTypes = documentType.getMimeTypes();

		if ((name == null) || (role == null))
			throw new BuildException(
					"'<documenttype>' must have both a 'name' and a 'role' attibute");

		if ((osTypes.isEmpty()) && (extensions.isEmpty()) && (mimeTypes.isEmpty()))
			throw new BuildException(
					"'<documenttype>' of \""
							+ name
							+ "\" must have 'osTypes' or 'extensions' or 'mimeTypes'");

		bundleProperties.addDocumentType(documentType);
	}

	public void addConfiguredService(Service service) {
	
		//if (service.getPortName() == null)
		//	throw new BuildException("\"<service>\" must have a \"portName\" attribute");
		
		if (service.getMessage() == null)
			throw new BuildException("\"<service>\" must have a \"message\" attribute");
		
		String menuItem = service.getMenuItem();
		if (menuItem == null)
			throw new BuildException("\"<service>\" must have a \"menuItem\" attribute");
		if (!menuItems.add(menuItem))
			throw new BuildException("\"<service>\" \"menuItem\" value must be unique");
		
		if (service.getSendTypes().isEmpty() && service.getReturnTypes().isEmpty())
			throw new BuildException("\"<service>\" must have either a \"sendTypes\" attribute, a \"returnTypes\" attribute or both");
		
		String keyEquivalent = service.getKeyEquivalent();
		if ((keyEquivalent != null) && (1 != keyEquivalent.length()))
			throw new BuildException("\"<service>\" \"keyEquivalent\" must be one character if present");
		
		String timeoutString = service.getTimeout();
		if (timeoutString != null) {
			long timeout = -1;
			try {
				timeout = Long.parseLong(timeoutString);
			} catch (NumberFormatException nfe) {
				throw new BuildException("\"<service>\" \"timeout\" must be a positive integral number");
			}
			if (timeout < 0)
				throw new BuildException("\"<service>\" \"timeout\" must not be negative");
		}
		
		bundleProperties.addService(service);
	}
	
	public void addConfiguredHelpBook(HelpBook helpBook) {
	
		// Validity check on 'foldername'
		if (helpBook.getFolderName() == null) {
			if (bundleProperties.getCFBundleHelpBookFolder() == null)
				throw new BuildException("Either the '<helpbook>' attribute 'foldername' or the '<jarbundler>' attribute 'helpbookfolder' must be defined");
			helpBook.setFolderName(bundleProperties.getCFBundleHelpBookFolder());
		}

		// Validity check on 'title'
		if (helpBook.getName() == null) {
			if (bundleProperties.getCFBundleHelpBookName() == null)
				throw new BuildException("Either the '<helpbook>' attribute 'name' or the '<jarbundler>' attribute 'helpbookname' must be defined");
			helpBook.setName(bundleProperties.getCFBundleHelpBookName());
		}

		// Make sure some file were selected...
		List fileLists = helpBook.getFileLists();
		List fileSets = helpBook.getFileSets();

		if ( fileLists.isEmpty() && fileSets.isEmpty() )
			throw new BuildException("The '<helpbook>' task must have either " +
			                         "'<fileset>' or  '<filelist>' nested tags");


		mHelpBooks.add(helpBook);
	}



	/***************************************************************************
	 * Execute the task
	 **************************************************************************/

	/**
	 * The method executing the task
	 */

	public void execute() throws BuildException {

		// Delete any existing Application bundle directory structure

		bundleDir = new File(mRootDir, bundleProperties.getApplicationName() + ".app");

		if (bundleDir.exists()) {
			Delete deleteTask = new Delete();
            deleteTask.setProject(getProject());
			deleteTask.setDir(bundleDir);
			deleteTask.execute();
		}

		// Validate - look for required attributes
		// ///////////////////////////////////////////

		if (mRootDir == null)
			throw new BuildException("Required attribute \"dir\" is not set.");

		if (mJarAttrs.isEmpty() && mJarFileSets.isEmpty()
				&& mJarFileLists.isEmpty())
			throw new BuildException("Either the attribute \"jar\" must "
					+ "be set, or one or more jarfilelists or "
					+ "jarfilesets must be added.");

		if (!mJarAttrs.isEmpty()
				&& (!mJarFileSets.isEmpty() || !mJarFileLists.isEmpty()))
			throw new BuildException(
					"Cannot set both the attribute "
							+ "\"jars\" and use jar filesets/filelists.  Use only one or the other.");

		if (bundleProperties.getApplicationName() == null)
			throw new BuildException("Required attribute \"name\" is not set.");

		if (bundleProperties.getMainClass() == null)
			throw new BuildException(
					"Required attribute \"mainclass\" is not set.");

		// /////////////////////////////////////////////////////////////////////////////////////

		// Set up some Java properties

		// About Menu, deprecated under 1.4+
		if (useOldPropertyNames())
			bundleProperties.addJavaProperty(ABOUTMENU_KEY, bundleProperties
					.getCFBundleName());

		// Anti Aliased Graphics, renamed in 1.4+
		String antiAliasedProperty = useOldPropertyNames()
				? "com.apple.macosx.AntiAliasedGraphicsOn"
				: "apple.awt.antialiasing";

		if (mAntiAliasedGraphics != null)
			bundleProperties.addJavaProperty(antiAliasedProperty,
					mAntiAliasedGraphics.toString());

		// Anti Aliased Text, renamed in 1.4+
		String antiAliasedTextProperty = useOldPropertyNames()
				? "com.apple.macosx.AntiAliasedTextOn"
				: "apple.awt.textantialiasing";

		if (mAntiAliasedText != null)
			bundleProperties.addJavaProperty(antiAliasedTextProperty,
					mAntiAliasedText.toString());

		// Live Resize, deprecated under 1.4+
		if (useOldPropertyNames() && (mLiveResize != null))
			bundleProperties.addJavaProperty(
					"com.apple.mrj.application.live-resize", mLiveResize
							.toString());

		// Screen Menu Bar, renamed in 1.4+
		String screenMenuBarProperty = useOldPropertyNames()
				? "com.apple.macos.useScreenMenuBar"
				: "apple.laf.useScreenMenuBar";

		if (mScreenMenuBar != null)
			bundleProperties.addJavaProperty(screenMenuBarProperty,
					mScreenMenuBar.toString());

		// Growbox, added with 1.4+
		if ((useOldPropertyNames() == false) && (mGrowbox != null))
			bundleProperties.addJavaProperty("apple.awt.showGrowBox", mGrowbox
					.toString());

		// Growbox Intrudes, deprecated under 1.4+
		if (useOldPropertyNames() && (mGrowboxIntrudes != null))
			bundleProperties.addJavaProperty(
					"com.apple.mrj.application.growbox.intrudes",
					mGrowboxIntrudes.toString());

		if (!mRootDir.exists()
				|| (mRootDir.exists() && !mRootDir.isDirectory()))
			throw new BuildException(
					"Destination directory specified by \"dir\" "
							+ "attribute must already exist.");

		if (bundleDir.exists())
			throw new BuildException("The directory/bundle \""
					+ bundleDir.getName()
					+ "\" already exists, cannot continue.");

		// Status message
		log("Creating application bundle: " + bundleDir);

		if (!bundleDir.mkdir())
			throw new BuildException("Unable to create bundle: " + bundleDir);

		// Make the Contents directory
		mContentsDir = new File(bundleDir, "Contents");

		if (!mContentsDir.mkdir())
			throw new BuildException("Unable to create directory "
					+ mContentsDir);

		// Make the "MacOS" directory
		mMacOsDir = new File(mContentsDir, "MacOS");

		if (!mMacOsDir.mkdir())
			throw new BuildException("Unable to create directory " + mMacOsDir);

		// Make the Resources directory
		mResourcesDir = new File(mContentsDir, "Resources");

		if (!mResourcesDir.mkdir())
			throw new BuildException("Unable to create directory "
					+ mResourcesDir);

		// Make the Resources/Java directory
		mJavaDir = new File(mResourcesDir, "Java");

		if (!mJavaDir.mkdir())
			throw new BuildException("Unable to create directory " + mJavaDir);

		// Copy icon file to resource dir. If no icon parameter
		// is supplied, the default icon will be used.

		if (mAppIcon != null) {
		

			try {
				File dest = new File(mResourcesDir, mAppIcon.getName());

				if(mVerbose)
					log("Copying application icon file to \"" + bundlePath(dest) + "\"");

				mFileUtils.copyFile(mAppIcon, dest);
			} catch (IOException ex) {
				throw new BuildException("Cannot copy icon file: " + ex);
			}
		}

		// Copy document type icons, if any, to the resource dir
		try {
			Iterator itor = bundleProperties.getDocumentTypes().iterator();

			while (itor.hasNext()) {
				DocumentType documentType = (DocumentType) itor.next();
				File iconFile = documentType.getIconFile();
				if (iconFile != null) {
					File dest = new File(mResourcesDir, iconFile.getName());
					if(mVerbose)
						log("Copying document icon file to \"" + bundlePath(dest) + "\"");
					mFileUtils.copyFile(iconFile, dest);
				}
			}
		} catch (IOException ex) {
			throw new BuildException("Cannot copy document icon file: " + ex);
		}

		// Copy application jar(s) from the "jars" attribute (if any)
		processJarAttrs();

		// Copy application jar(s) from the nested jarfileset element(s)
		processJarFileSets();

		// Copy application jar(s) from the nested jarfilelist element(s)
		processJarFileLists();

		// Copy executable(s) from the "execs" attribute (if any)
		processExecAttrs();

		// Copy executable(s) from the nested execfileset element(s)
		processExecFileSets();

		// Copy executable(s) from the nested execfilelist element(s)
		processExecFileLists();

		// Copy resource(s) from the nested resourcefileset element(s)
		processResourceFileSets();

		// Copy resource(s) from the nested javafileset element(s)
		processJavaFileSets();

		// Copy resource(s) from the nested resourcefilelist element(s)
		processResourceFileLists();

		// Copy resource(s) from the nested javafilelist element(s)
		processJavaFileLists();

		// Add external classpath references from the extraclasspath attributes
		processExtraClassPathAttrs();

		// Add external classpath references from the nested
		// extraclasspathfileset element(s)
		processExtraClassPathFileSets();

		// Add external classpath references from the nested
		// extraclasspathfilelist attributes
		processExtraClassPathFileLists();

		// Copy HelpBooks into place
		copyHelpBooks();

		// Copy the JavaApplicationStub file from the Java system directory to
		// the MacOS directory
		copyApplicationStub();

		// Create the Info.plist file
		writeInfoPlist();

		// Create the PkgInfo file
		writePkgInfo();

		// Done!
	}

	/***************************************************************************
	 * Private utility methods.
	 **************************************************************************/

	private void setExecutable(File f) {

		Chmod chmodTask = new Chmod();
		chmodTask.setProject(getProject());
		chmodTask.setFile(f);
		chmodTask.setPerm("ugo+rx");

		if (mVerbose)
			log("Setting \"" + bundlePath(f) + "\" to executable");

		chmodTask.execute();

	}

	/**
	 * Utility method to determine whether this app bundle is targeting a 1.3 or
	 * 1.4 VM. The Mac OS X 1.3 VM uses different Java property names from the
	 * 1.4 VM to hint at native Mac OS X look and feel options. For example, on
	 * 1.3 the Java property to tell the VM to display Swing menu bars as screen
	 * menus is "com.apple.macos.useScreenMenuBar". Under 1.4, it becomes
	 * "apple.laf.useScreenMenuBar". Such is the price of progress, I suppose.
	 * 
	 * Obviously, this logic may need refactoring in the future.
	 */

	private boolean useOldPropertyNames() {
		return (bundleProperties.getJVMVersion().startsWith("1.3"));
	}

	private void processJarAttrs() throws BuildException {

		try {

			for (Iterator jarIter = mJarAttrs.iterator(); jarIter.hasNext();) {
				File src = (File) jarIter.next();
				File dest = new File(mJavaDir, src.getName());

				if (mVerbose) 
					log("Copying JAR file to \"" + bundlePath(dest) + "\"");
				

				mFileUtils.copyFile(src, dest);
				bundleProperties.addToClassPath(dest.getName());
			}
		} catch (IOException ex) {
			throw new BuildException("Cannot copy jar file: " + ex);
		}
	}

	private void processJarFileSets() throws BuildException {

		for (Iterator jarIter = mJarFileSets.iterator(); jarIter.hasNext();) {

			FileSet fs = (FileSet) jarIter.next();

			Project p = fs.getProject();
			File srcDir = fs.getDir(p);
			FileScanner ds = fs.getDirectoryScanner(p);
			fs.setupDirectoryScanner(ds, p);
			ds.scan();

			String[] files = ds.getIncludedFiles();

			try {

				for (int i = 0; i < files.length; i++) {
					String fileName = files[i];
					File src = new File(srcDir, fileName);
					File dest = new File(mJavaDir, fileName);

					if (mVerbose)
						log("Copying JAR file to \"" + bundlePath(dest) + "\"");

					mFileUtils.copyFile(src, dest);
					bundleProperties.addToClassPath(fileName);
				}

			} catch (IOException ex) {
				throw new BuildException("Cannot copy jar file: " + ex);
			}
		}
	}

	private void processJarFileLists() throws BuildException {

		for (Iterator jarIter = mJarFileLists.iterator(); jarIter.hasNext();) {
			FileList fl = (FileList) jarIter.next();
			Project p = fl.getProject();
			File srcDir = fl.getDir(p);
			String[] files = fl.getFiles(p);

			try {

				for (int i = 0; i < files.length; i++) {
					String fileName = files[i];
					File src = new File(srcDir, fileName);
					File dest = new File(mJavaDir, fileName);

					if (mVerbose) 
						log("Copying JAR file to \"" + bundlePath(dest) + "\"");
					

					mFileUtils.copyFile(src, dest);
					bundleProperties.addToClassPath(fileName);
				}
			} catch (IOException ex) {
				throw new BuildException("Cannot copy jar file: " + ex);
			}
		}
	}

	private void processExtraClassPathAttrs() throws BuildException {

		for (Iterator jarIter = mExtraClassPathAttrs.iterator(); jarIter
				.hasNext();) {
			File src = (File) jarIter.next();
			bundleProperties.addToExtraClassPath(src.getPath());
		}
	}

	private void processExtraClassPathFileSets() throws BuildException {

		for (Iterator jarIter = mExtraClassPathFileSets.iterator(); jarIter
				.hasNext();) {
			FileSet fs = (FileSet) jarIter.next();
			Project p = fs.getProject();
			File srcDir = fs.getDir(p);
			FileScanner ds = fs.getDirectoryScanner(p);
			fs.setupDirectoryScanner(ds, p);
			ds.scan();

			String[] files = ds.getIncludedFiles();

			for (int i = 0; i < files.length; i++) {
				File f = new File(srcDir, files[i]);
				bundleProperties.addToExtraClassPath(f.getPath());
			}
		}
	}

	private void processExtraClassPathFileLists() throws BuildException {

		for (Iterator jarIter = mExtraClassPathFileLists.iterator(); jarIter
				.hasNext();) {
			FileList fl = (FileList) jarIter.next();
			Project p = fl.getProject();
			File srcDir = fl.getDir(p);
			String[] files = fl.getFiles(p);

			for (int i = 0; i < files.length; i++) {
				File f = new File(srcDir, files[i]);
				bundleProperties.addToExtraClassPath(f.getPath());
			}
		}
	}

	private void processExecAttrs() throws BuildException {

		try {

			for (Iterator execIter = mExecAttrs.iterator(); execIter.hasNext();) {
				File src = (File) execIter.next();
				File dest = new File(mMacOsDir, src.getName());

				if (mVerbose) 
					log("Copying exec file to \"" + bundlePath(dest) + "\"");
				

				mFileUtils.copyFile(src, dest);
				setExecutable(dest);
			}
		} catch (IOException ex) {
			throw new BuildException("Cannot copy exec file: " + ex);
		}
	}

	// Methods for copying FileSets into the application bundle ///////////////////////////////

	// Files for the Contents/MacOS directory
	private void processExecFileSets() {
		processCopyingFileSets(mExecFileSets, mMacOsDir, true);
	}

	// Files for the Contents/Resources directory
	private void processResourceFileSets() {
		processCopyingFileSets(mResourceFileSets, mResourcesDir, false);
	}

	// Files for the Contents/Resources/Java directory
	private void processJavaFileSets() {
		processCopyingFileSets(mJavaFileSets, mJavaDir, false);
	}

	private void processCopyingFileSets(List fileSets, File targetdir, boolean setExec) {

		for (Iterator execIter = fileSets.iterator(); execIter.hasNext();) {
			FileSet fs = (FileSet) execIter.next();
			Project p = fs.getProject();
			File srcDir = fs.getDir(p);
			FileScanner ds = fs.getDirectoryScanner(p);
			fs.setupDirectoryScanner(ds, p);
			ds.scan();

			String[] files = ds.getIncludedFiles();

			if (files.length == 0) {
				// this is probably an error -- warn about it
				System.err
						.println("WARNING: fileset for copying from directory "
								+ srcDir + ": no files found");
			} else {
				try {
					for (int i = 0; i < files.length; i++) {
						String fileName = files[i];
						File src = new File(srcDir, fileName);
						File dest = new File(targetdir, fileName);
						
						if (mVerbose) 
							log("Copying "
									+ (setExec ? "exec" : "resource")
									+ " file to \"" + bundlePath(dest) +"\"");
						
						mFileUtils.copyFile(src, dest);
						if (setExec)
							setExecutable(dest);
					}
				} catch (IOException ex) {
					throw new BuildException("Cannot copy file: " + ex);
				}
			}
		}
	}

	// Methods for copying FileLists into the application bundle /////////////////////////////

	// Files for the Contents/MacOS directory
	private void processExecFileLists() throws BuildException {
		processCopyingFileLists(mExecFileLists, mMacOsDir, true);
	}

	// Files for the Contents/Resources directory
	private void processResourceFileLists() throws BuildException {
		processCopyingFileLists(mResourceFileLists, mResourcesDir, false);
	}

	// Files for the Contents/Resources/Java directory
	private void processJavaFileLists() throws BuildException {
		processCopyingFileLists(mJavaFileLists, mJavaDir, false);
	}

	private void processCopyingFileLists(List fileLists, File targetDir, boolean setExec) throws BuildException {

		for (Iterator execIter = fileLists.iterator(); execIter.hasNext();) {

			FileList fl = (FileList) execIter.next();
			Project p = fl.getProject();
			File srcDir = fl.getDir(p);
			String[] files = fl.getFiles(p);

			if (files.length == 0) {
				// this is probably an error -- warn about it
				System.err.println("WARNING: filelist for copying from directory "
								+ srcDir + ": no files found");
			} else {
				try {
					for (int i = 0; i < files.length; i++) {
						String fileName = files[i];
						File src = new File(srcDir, fileName);
						File dest = new File(targetDir, fileName);
						
						if (mVerbose) 
							log("Copying "
									+ (setExec ? "exec" : "resource")
									+ " file to \"" + bundlePath(dest) +"\"");
						
						mFileUtils.copyFile(src, dest);
						if (setExec)
							setExecutable(dest);
					}
				} catch (IOException ex) {
					throw new BuildException("Cannot copy jar file: " + ex);
				}
			}
		}
	}



	private void copyHelpBooks() {

		for (Iterator itor = mHelpBooks.iterator(); itor.hasNext();) {

			HelpBook helpBook = (HelpBook)itor.next();
			
			String folderName = helpBook.getFolderName();
			String name = helpBook.getName();
			String locale = helpBook.getLocale();
			
			List fileLists = helpBook.getFileLists();
			List fileSets = helpBook.getFileSets();


			File helpBookDir = null;
			
			if (locale == null) {
			
				// Set the Bundle entries for a nonlocalized Help Book
				if (folderName != null)
					bundleProperties.setCFBundleHelpBookFolder(folderName);
				
				if (name != null)
					bundleProperties.setCFBundleHelpBookName(name);
				
				// The non-localized Help Book is top level "/Resources"
				helpBookDir = new File(mResourcesDir, folderName);
				helpBookDir.mkdir();

				if(mVerbose)
					log("Creating Help Book at \"" + 
					                    bundlePath(helpBookDir) + "\"");

				
			} else {

				// The localized Help Book is "/Resources/locale.lproj"

				File lproj = new File(mResourcesDir, locale + ".lproj");
				lproj.mkdir();
				helpBookDir = new File(lproj, folderName);
				helpBookDir.mkdir();

				if(mVerbose)
					log("Creating Help Book for \"" + locale +
					                    "\" at \"" + bundlePath(helpBookDir)  + "\"");

				// Create a local file to override the Bundle settings
				File infoPList = new File(lproj, "InfoPlist.strings");
				PrintWriter writer = null;
				try {
 					writer = new PrintWriter(new FileWriter(infoPList));
       				writer.println("CFBundleHelpBookFolder = \"" + folderName + "\";");
       				writer.println("CFBundleHelpBookName = \"" + name + "\";");
       				writer.println("CFBundleName = \"" + bundleProperties.getCFBundleName() + "\";");
       			} catch (IOException ioe) {
       				throw new BuildException("IOException in writing Help Book locale: " + locale);
       			} finally {
		        	mFileUtils.close(writer);
		        }
			}

			// Write the Help Book source files into the bundle

			processCopyingFileSets(fileSets, helpBookDir, false);
			processCopyingFileLists(fileLists, helpBookDir, false);

		}
	}




	// Copy the application stub into the bundle
	// /////////////////////////////////////////////

	private void copyApplicationStub() throws BuildException {

		File newStubFile = new File(mMacOsDir, bundleProperties.getCFBundleExecutable());

		if (mVerbose)
			log("Copying Java application stub to \"" + bundlePath(newStubFile) + "\"");

		try {
			mFileUtils.copyFile(mStubFile, newStubFile);
		} catch (IOException ex) {
			throw new BuildException("Cannot copy Java Application Stub: " + ex);
		}

		// Set the permissions on the stub file to executable

		setExecutable(newStubFile);
	}

	private void writeInfoPlist() throws BuildException {
		PropertyListWriter listWriter = new PropertyListWriter(bundleProperties);
		File infoPlist = new File(mContentsDir, "Info.plist");

		listWriter.writeFile(infoPlist);
		
		if (mVerbose) 
			log("Creating \"" + bundlePath(infoPlist) + "\" file");


		if (mShowPlist) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(infoPlist));
				String str;
				while ((str = in.readLine()) != null) 
					log(str);
				in.close();
    		} catch (IOException e) {
    			throw new BuildException(e);
    		}			
		}
	}


	//
	// Write the PkgInfo file into the application bundle
	//

	private void writePkgInfo() throws BuildException {
		File pkgInfo = new File(mContentsDir, "PkgInfo");
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(pkgInfo)));
			writer.print(bundleProperties.getCFBundlePackageType());
			writer.println(bundleProperties.getCFBundleSignature());
			writer.flush();
		} catch (IOException ex) {
			throw new BuildException("Cannot create PkgInfo file: " + ex);
		} finally {
			mFileUtils.close(writer);
		}
	}

	private String bundlePath(File bundleFile) {
	
		String rootPath = bundleDir.getAbsolutePath();
		String thisPath = bundleFile.getAbsolutePath();
	
		return thisPath.substring(rootPath.length());
	
	}
}
