package be.glorieuxnet.www.datalookup.MusicBrainz;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * This class represents the Coverart results of a Musicbrainz lookup
 */
public class CoverArt {
	private List<Image> images;
	private String release;
	
	/**
	 * Get a list with Image objects
	 * @return List containing Image objects
	 */
	public List<Image> getImages() {
		return images;
	}
	/**
	 * Set the list of image objects
	 * @param images List containing Image objects
	 */
	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	/**
	 * Get the release of the lookup
	 * @return Release as String
	 */
	public String getRelease() {
		return release;
	}
	
	/**
	 * Set the release of the lookup
	 * @param release Release as String
	 */
	public void setRelease(String release) {
		this.release = release;
	}
	
}
