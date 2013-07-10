package be.glorieuxnet.www.JSON;

/**
 * @author Gerwin Glorieux
 * This class represents a JSON Song Object
 */
public class Song {
	String title;
	String artist;
	String length;
	String tracknumber;
	String id;
	String requestPath;
	
	/** 
	 * Get the Song title
	 * @return Song title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the Song title
	 * @param title Song title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the Artist Name
	 * @return Artist name
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * Set the Artist name
	 * @param artist Artist name
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * Get the song's length as an Integer
	 * @return The song's length
	 */
	public String getLength() {
		return length;
	}
	
	/**
	 * Set the song's length
	 * @param length Length as Integer
	 */
	public void setLength(String length) {
		this.length = length;
	}
	
	/**
	 * Get the song's tracknumber
	 * @return Tracknumber
	 */
	public String getTracknumber() {
		return tracknumber;
	}
	
	/**
	 * Set the song's tracknumber
 	 * @param tracknumber Tracknumber
	 */
	public void setTracknumber(String tracknumber) {
		if(tracknumber == null) this.tracknumber = "";
		else this.tracknumber = tracknumber;
	}
	
	/**
	 * Get the song's ID
	 * @return Song ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the song's ID
	 * @param id Song ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the song's request path
	 * @return Request path
	 */
	public String getRequestPath() {
		return requestPath;
	}
	
	/**
	 * Set the song's request path
	 * @param requestPath Request path
	 */
	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
}
