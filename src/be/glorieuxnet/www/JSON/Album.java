package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * Album JSON object
 */
public class Album {
	String title;
	String ID;
	String cover_filepath;
	List<Song> songs;
	
	/**
	 * Get album title
	 * @return Album title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set Album title
	 * @param title Album title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the cover's filepath
	 * @return Cover Filepath
	 */
	public String getCover_filepath() {
		return cover_filepath;
	}
	
	/**
	 * Set the cover's filepath
	 * @param cover_filepath Filepath on disk of cover Image
 	 */
	public void setCover_filepath(String cover_filepath) {
		if(cover_filepath == null) this.cover_filepath = "";
		else this.cover_filepath = cover_filepath;
	}
	
	/**
	 * Get the Songs in this album
	 * @return List of Song objects
	 */
	public List<Song> getSongs() {
		return songs;
	}
	
	/**
	 * Set the List of songs in this album
	 * @param songs List of Song objects
	 */
	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}
	
	/**
	 * Get the album ID
	 * @return Album ID
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * Set the Album ID
	 * @param iD Album ID
	 */
	public void setID(String iD) {
		ID = iD;
	}
}
