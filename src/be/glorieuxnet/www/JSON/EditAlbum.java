package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * Class that is used when editing an Album
 */
public class EditAlbum {
	String title;
	String ID;
	String albumArtist;
	String cover_filepath;
	String year;
	String no_oftracks;
	String disc_no;
	boolean compilation;
	List<Song> songs;
	
	/**
	 * Get the Album title
	 * @return Album title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the album title
	 * @param title Album title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the album's cover filepath
	 * @return Cover filepath
	 */
	public String getCover_filepath() {
		return cover_filepath;
	}
	
	/**
	 * Set the albums' cover filepathj
	 * @param cover_filepath Cover filepath
	 */
	public void setCover_filepath(String cover_filepath) {
		if(cover_filepath == null) this.cover_filepath = "";
		else this.cover_filepath = cover_filepath;
	}
	
	/**
	 * Get the songs in this album
	 * @return List of Song objects
	 */
	public List<Song> getSongs() {
		return songs;
	}
	
	/** 
	 * Set the songs in this album
	 * @param songs List of Song objects
	 */
	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}
	
	/**
	 * Get the album ID
	 * @return Album id
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * Set the album ID
	 * @param iD Album id
	 */
	public void setID(String iD) {
		ID = iD;
	}
	
	/**
	 * Get the Album artist
	 * @return Album artist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}
	
	/**
	 * Set the Album artist
	 * @param albumArtist Album Artist
	 */
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	
	/**
	 * Returns true if the album is a compilation 
	 * @return true if compilation
	 */
	public boolean isCompilation() {
		return compilation;
	}
	
	/**
	 * Set true if this album is a compilation
	 * @param compilation Compilation boolean
	 */
	public void setCompilation(boolean compilation) {
		this.compilation = compilation;
	}
	
	/**
	 * Get the year the album was made
	 * @return Album year
	 */
	public String getYear() {
		return year;
	}
	
	/**
	 * Set the year the album was made
	 * @param year Album year
	 */
	public void setYear(String year) {
		this.year = year;
	}
	
	/**
	 * Get the number of tracks in this album
	 * @return Number of tracks
	 */
	public String getNo_oftracks() {
		return no_oftracks;
	}
	
	/**
	 * Set the number of tracks in this album
	 * @param no_oftracks Number of tracks
	 */
	public void setNo_oftracks(String no_oftracks) {
		this.no_oftracks = no_oftracks;
	}
	
	/**
	 * Get the disc number of this album
	 * @return Disc number
	 */
	public String getDisc_no() {
		return disc_no;
	}
	
	/**
	 * Set the disc number of this album
	 * @param disc_no Album disc number
	 */
	public void setDisc_no(String disc_no) {
		this.disc_no = disc_no;
	}
}
