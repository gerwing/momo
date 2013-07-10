package be.glorieuxnet.www.JSON;

/**
 * @author Gerwin Glorieux
 * Class representing a JSON Song from the song view
 */
public class SongSort_Song {
	String title;
	String artist;
	String length;
	String id;
	String requestPath;
	String album;
	
	/**
	 * Get the song title
	 * @return Song title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the song title
	 * @param title Song title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/** Get the song's artist
	 * @return Song artist
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * Set the song's artist
	 * @param artist Song artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * Get the song's length as an Integer
	 * @return Song length
	 */
	public String getLength() {
		return length;
	}
	
	/**
	 * Set the song's length
	 * @param length Song length
	 */
	public void setLength(String length) {
		this.length = length;
	}
	
	/**
	 * Get the Song ID
	 * @return Song ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the Song ID
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
	
	/**
	 * Get the album name
	 * @return Album name
	 */
	public String getAlbum() {
		return album;
	}
	
	/**
	 * Set the album name
	 * @param album Album name
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
}
