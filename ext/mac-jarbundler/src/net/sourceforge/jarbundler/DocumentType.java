package net.sourceforge.jarbundler;

import java.lang.String;

import java.io.File;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Represents an Info.plist DocumentType used for associating a document with
 * the application
 * 
 * The Document Types allows you to specify which documents your finished
 * product can handle. You should list the application's primary document type
 * first because the document controller uses that type by default when the user
 * requests a new document.
 * 
 * Name - The name of the document type.
 * 
 * UTI - A list of Uniform Type Identifier (UTI) strings for the document. UTIs
 * are strings that uniquely identify abstract types. They can be used to
 * describe a file format or data type but can also be used to describe type
 * information for other sorts of entities, such as directories, volumes, or
 * packages. For more information on UTIs, see the header file UTType.h,
 * available as part of LaunchServices.framework in Mac OS X v10.3 and later.
 *
 *  
 * Extensions - A list of the filename extensions for this document type. Don't
 * include the period in the extension.
 * 
 * 
 * OS Types - A list of four-letter codes for the document. These codes are
 * stored in the document's resources or information property list files.
 * 
 * 
 * MIME Types - A list of the Multipurpose Internet Mail Extensions (MIME) types
 * for the document. MIME types identify content types for Internet
 * applications.
 * 
 * 
 * Icon File - The name of the file that contains the document type's icon.
 * 
 * 
 * Role - A description of how the application uses the documents of this type.
 * 
 * Editor - The application can display, edit, and save documents of this type.
 * 
 * Viewer - The application can display, but not edit, documents of this type.
 * 
 * Shell - The application provides runtime services for other processes for
 * example, a Java applet viewer.
 * 
 * None - The application can neither display nor edit documents of this type
 * but instead uses them in some other way. For example, Sketch uses this role
 * to declare types it can export but not read.
 * 
 * 
 * Bundle - Specifies whether the document is a single file or a file bundle,
 * that is, a directory that is treated as a single document by certain
 * applications, such as the Finder.
 * 
 * 
 * <documenttype> name="Scan Project" extensions="scansort scanproj"
 * ostypes="fold disk fdrp" iconfile="document.icns" mimetypes="text/html
 * image/jpeg" role="editor" bundle="true" />
 * 
 */


public class DocumentType {

	private static final List EMPTYLIST = new ArrayList(0);

	/** Name. The name of the document type. */
	public String name = null;

	/**
	 * Extensions. A list of the filename extensions for this document type.
	 * Don't include the period in the extension.
	 */

	public String[] extensions = null;
	/**
	 * OS Types. A list of four-letter codes for the document. These codes are
	 * stored in the document's resources or information property list files.
	 */

	public String[] osTypes = null;
	/**
	 * MIME Types. A list of the Multipurpose Internet Mail Extensions (MIME)
	 * types for the document. MIME types identify content types for Internet
	 * applications.
	 */

	public String[] mimeTypes = null;

    /**
	 * UTI. A list of Uniform Type Identifier (UTI) strings for the document.
	 * UTIs are strings that uniquely identify abstract types. They can be used
	 * to describe a file format or data type but can also be used to describe
	 * type information for other sorts of entities, such as directories,
	 * volumes, or packages. For more information on UTIs, see the header file
	 * UTType.h, available as part of LaunchServices.framework in Mac OS X v10.3
	 * and later.
	 */
    public String[] UTIs = null;
	
	/**
	 * Icon File. The name of the file that contains the document types icon.
	 */

	public File iconFile = null;
	/**
	 * Role. A description of how the application uses the documents of this
	 * type. You can choose from four values:
	 * <p>
	 * Editor. The application can display, edit, and save documents of this
	 * type.
	 * <p>
	 * Viewer. The application can display, but not edit, documents of this
	 * type.
	 * <p>
	 * Shell. The application provides runtime services for other processesfor
	 * example, a Java applet viewer.
	 * <p>
	 * None. The application can neither display nor edit documents of this type
	 * but instead uses them in some other way. For example, Sketch uses this
	 * role to declare types it can export but not read.
	 */

	public String role = null;

	/**
	 * Bundle. Specifies whether the document is a single file document or a
	 * document bundle, that is, a directory that is treated as a single
	 * document by certain applications, such as the Finder.
	 */

	public boolean isBundle = false;

	// Document type name
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// Extensions
	public void setExtensions(String extensions) {
		this.extensions = extensions.split("[\\s,]");
	}

	public List getExtensions() {
		return (extensions == null) ? EMPTYLIST : Arrays.asList(extensions);
	}

	// OS Types
	public void setOSTypes(String osTypes) {
		this.osTypes = osTypes.split("[\\s,]");
	}

	public List getOSTypes() {
		return (osTypes == null) ? EMPTYLIST : Arrays.asList(osTypes);
	}

	// mime-types
	public void setMimeTypes(String mimeTypes) {
		this.mimeTypes = mimeTypes.split("[\\s,]");
	}

	public List getMimeTypes() {
		return (mimeTypes == null) ? EMPTYLIST : Arrays.asList(this.mimeTypes);
	}

	// Uniform Type Identifiers
	public void setUTIs(String UTIs) {
		this.UTIs = UTIs.split("[\\s,]");
	}

	public List getUTIs() {
		return this.UTIs == null ? EMPTYLIST : Arrays.asList(this.UTIs);
	}
	
	// Document icon file
	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}

	public File getIconFile() {
		return iconFile;
	}

	// Document role
	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	// Is this document represented as a bundle
	public void setBundle(boolean isBundle) {
		this.isBundle = isBundle;
	}

	public boolean isBundle() {
		return isBundle;
	}

}
