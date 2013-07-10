package be.glorieuxnet.www.datalookup.MusicBrainz;

/**
 * @author Gerwin Glorieux
 * This class represents the Thumbnails of a Image object
 */
public class Thumbnail {
	private String large;
	private String small;
	
	/**
	 * Get the large thumbnail
	 * @return Large thumbnail URL
	 */
	public String getLarge() {
		return large;
	}
	
	/**
	 * Set the large thumbnail
	 * @param large Large thumbnail URL
	 */
	public void setLarge(String large) {
		this.large = large;
	}
	
	/**
	 * Get the small Thumbnail
	 * @return Small Thumbnail URL
	 */
	public String getSmall() {
		return small;
	}
	
	/**
	 * Set the small Thumbnail URL
	 * @param small Small Thumbnail URL
	 */
	public void setSmall(String small) {
		this.small = small;
	}
}
