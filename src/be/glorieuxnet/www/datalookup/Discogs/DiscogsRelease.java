package be.glorieuxnet.www.datalookup.Discogs;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * DiscogsRelease class
 * A class representing a Release from the Discogs Web Service
 */
public class DiscogsRelease {
	List<Images> images;

	/**
	 * Get the list of images for this release
	 * @return List containing Images objects
	 */
	public List<Images> getImages() {
		return images;
	}

	/**
	 * Set the list of Images objects
	 * @param images List containing Images objects
	 */
	public void setImages(List<Images> images) {
		this.images = images;
	}
}
