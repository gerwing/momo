package be.glorieuxnet.www.indexing;

import java.io.Serializable;

/**
 * @author Gerwin Glorieux
 * This class represents a Configured Folder
 */
public class Folder implements Serializable {
	String folder;
	
	/**
	 * Construct a new Folder object that takes a path parameter
	 * @param s Path of the folder
	 */
	public Folder(String s) {
		folder = s;
	}
	
	/**
	 * Get the path of the folder
	 * @return Folder path
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * Set the path of the folder
	 * @param folder Folder path
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}
}
