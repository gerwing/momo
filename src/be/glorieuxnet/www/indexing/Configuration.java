package be.glorieuxnet.www.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Gerwin Glorieux
 * This class is a Serializable Configuration class. An object of this class should be saved to the resources folder.
 * This class sets the Configuration
 */
public class Configuration implements Serializable{

	/**
	 * Class Variables 
	 */
	private static final long serialVersionUID = 1L;
	private static String CONFPATH = "resources" + File.separator + "conf.ser";
	private ArrayList<Folder> folders;
	private boolean showUnIdentified = false;
	
	/*
	 * METHODS FOR LOADING/SAVING CONFIGURATION
	 */
	
	/**
	 * This method returns the currently save Configuration. If no Configuration has been saved it will return a new
	 * Configuration object
	 * @return Configuration object
	 */
	public static Configuration getConfiguration() {
		File conffile = new File(CONFPATH);
		if(conffile.exists()) //load file
		{
			try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(conffile));
				Configuration configuration;
				configuration = (Configuration) input.readObject();
				input.close();
				return configuration;
			}
			catch (IOException ie) {
				System.err.println(ie.getMessage());
				removeConfiguration();
				return null;
			} 
			catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
				removeConfiguration();
				return null;
			}
		}
		else {
			Configuration c = new Configuration();
			setConfiguration(c);
			return c;
		}
	}
	
	/**
	 * This method will remove the current configuration
	 */
	public static void removeConfiguration () {
		setConfiguration(new Configuration());
	}
	
	/**
	 * Set the Configuration and save it to disk
	 * @param configuration New Configuration
	 */
	public static void setConfiguration(Configuration configuration) {
		File conffile = new File(CONFPATH);
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(conffile));	
			output.writeObject(configuration);
			output.close();
		}
		catch (IOException ie) {
			System.err.println(ie.getMessage());
		}
	}
	
	/**
	 * Construct a new Configuration object
	 */
	private Configuration() {
		folders = new ArrayList<Folder>();
	}
	
	/*
	 * CONFIGURATION OBJECT METHODS
	 */
	
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
