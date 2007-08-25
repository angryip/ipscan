package net.sourceforge.jarbundler;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

import java.lang.String;



public class HelpBook extends MatchingTask {

	private String folderName = null;
	private String name = null;
	private String locale = null;

	private final List fileLists = new ArrayList();
	private final List fileSets = new ArrayList();


	// Help Book name
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	// Help Book folder name
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderName() {
		return folderName;
	}


	// Help Book locale
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLocale() {
		return locale;
	}

	// Help Book files as a ANT FileList	
	public void addFileList(FileList fileList) {
		fileLists.add(fileList);
	}

	public List getFileLists() {
		return fileLists;
	}

	// Help Book files as a ANT FileSet	
	public void addFileSet(FileSet fileSet) {
		fileSets.add(fileSet);
	}

	public List getFileSets() {
		return fileSets;
	}

}
