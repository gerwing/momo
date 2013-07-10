package be.glorieuxnet.www.indexing;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Gerwin Glorieux
 * This class is a Serializable Configuration class. An object of this class should be saved to the resources folder.
 * This class sets the Configuration
 */
public class Configuration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Folder> folders;
	private boolean showUnIdentified = false;
	
	/**
	 * Construct a new Configuration object
	 */
	public Configuration() {
		folders = new ArrayList<Folder>();
	}
	
	/**
	 * Add a Folder to the Configuration
	 * @param f Folder object to be added
	 */
	public void addFolder(Folder f) {
		folders.add(f);
	}
	
	/**
	 * Remove a Folder from the Configuration
	 * @param s Folder name
	 */
	public void removeFolder(String s){
		Folder remove = null;
		for(Folder f:folders){
			if(f.getFolder().equals(s)) remove = f;
		}
		folders.remove(remove);
	}
	
	/**
	 * Get all folders in this Configuration
	 * @return List of Folders
	 */
	public ArrayList<Folder> getFolders() {
		return folders;
	}
	
	/**
	 * Set all Folders in this Configuration
	 * @param folders List of Folders
	 */
	public void setFolders(ArrayList<Folder> folders) {
		this.folders = folders;
	}
	
	/**
	 * Get the Serial version UID
	 * @return Serial version UID
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Check the ShowUnidentified setting
	 * @return Boolean that is true if unidentified songs should be added to the collection and false otherwise
	 */
	public boolean isShowUnIdentified() {
		return showUnIdentified;
	}
	
	/**
	 * Set the ShowUnidentified Setting
	 * @param showUnIdentified Set this to true if Unidentified songs should be added to the All music collection
	 */
	public void setShowUnIdentified(boolean showUnIdentified) {
		this.showUnIdentified = showUnIdentified;
	}
	
}
