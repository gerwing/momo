package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * Album JSON Object to be used for the Album view 
 */
public class AlbumSort_Album {
	String title;
	String ID;
	String albumArtist;
	String cover_filepath;
	List<Song> songs;
	
	/**
	 * Get the Album title
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the Album title
	 * @param title Album title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the album cover's filepath
	 * @return Cover filepath
	 */
	public String getCover_filepath() {
		return cover_filepath;
	}
	
	/**
	 * Set the album cover's filepath
	 * @param cover_filepath Cover filepath
	 */
	public void setCover_filepath(String cover_filepath) {
		if(cover_filepath == null) this.cover_filepath = "";
		else this.cover_filepath = cover_filepath;
	}
	
	/**
	 * Get the list of songs in this album
	 * @return List of Song objects
	 */
	public List<Song> getSongs() {
		return songs;
	}
	
	/**
	 * Set the list of song in this album
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
	 * Set the album ID
	 * @param iD Album ID
	 */
	public void setID(String iD) {
		ID = iD;
	}
	
	/**
	 * Get the Album Artist
	 * @return Album Artist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}
	
	/**
	 * Set the Album Artis
	 * @param albumArtist Album Artist
	 */
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
}
