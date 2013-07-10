package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * Artist JSON object
 */
public class Artist {
	String name;
	List<Album> albums;
	String ID;
	
	/**
	 * Returns the name of the artist
	 * @return Artist name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the artists' name
	 * @param name Artist name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the albums from the artist
	 * @return List of Album objects
	 */
	public List<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Set the list of Albums for this artist
	 * @param albums List of Album objects
	 */
	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
	
	/**
	 * Get the artist ID
	 * @return Artist ID
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * Set the Artist ID
	 * @param iD Artist ID
	 */
	public void setID(String iD) {
		ID = iD;
	}
}
