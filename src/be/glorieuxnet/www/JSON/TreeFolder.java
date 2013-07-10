package be.glorieuxnet.www.JSON;

import java.util.ArrayList;

/**
 * @author Gerwin Glorieux
 * This class is used to represent a folder on the Server in JSON format.
 * Use this class to send folder data to the client
 */
public class TreeFolder {
	String title;
	String key;
	ArrayList<TreeFolder> children;
	boolean isFolder = true;
	boolean isLazy = true;
	
	/**
	 * Construct a new Treefolder
	 */
	public TreeFolder() {
		children = new ArrayList<TreeFolder>();
	}
	
	/**
	 * Get the folder title
	 * @return Folder title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the folder title
	 * @param title Folder title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the Folders in this folder
	 * @return List of TreeFolder objects
	 */
	public ArrayList<TreeFolder> getChildren() {
		return children;
	}
	
	/**
	 * Set the list of folders in this folder
	 * @param children List of TreeFolder objects
	 */
	public void setChildren(ArrayList<TreeFolder> children) {
		this.children = children;
	}
	
	/**
	 * Returns true if this file is a folder
	 * @return Boolean is Folder
	 */
	public boolean isFolder() {
		return isFolder;
	}
	
	/**
	 * Set true if this file is a folder
	 * @param isFolder True if is folder
	 */
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	
	/**
	 * Get the folder key
	 * @return Folder key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Set the folder key
	 * @param key Folder key
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
