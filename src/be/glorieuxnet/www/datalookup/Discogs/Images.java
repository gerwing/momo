package be.glorieuxnet.www.datalookup.Discogs;

/**
 * @author Gerwin Glorieux 
 * Images class representing an Image result from a Discogs lookup
 */
/**
 * @author Gerwin
 *
 */
public class Images {
	private String type;
	private String height;
	private String width;
	private String uri;
	private String uri150;
	private String resource_url;
	
	/**
	 * Returns the type of the image
	 * @return Image type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Set the type of the image
	 * @param type Image type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Get the height of the image
	 * @return Image height
	 */
	public String getHeight() {
		return height;
	}
	
	/**
	 * Set the height of the image
	 * @param height Image height
	 */
	public void setHeight(String height) {
		this.height = height;
	}
	
	/**
	 * Get the width of the image
	 * @return Image width
	 */
	public String getWidth() {
		return width;
	}
	
	/**
	 * Set the width of the image
	 * @param width Image width
	 */
	public void setWidth(String width) {
		this.width = width;
	}
	
	/**
	 * Get the Uri of the image
	 * @return Image URI
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Set the URI of the image
	 * @param uri Image URI
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**get The URI150 of the image
	 * @return Image URI 150
	 */
	public String getUri150() {
		return uri150;
	}
	
	/**
	 * Set the Image URI 150
	 * @param uri150 Image URI150
	 */
	public void setUri150(String uri150) {
		this.uri150 = uri150;
	}
	
	/**
	 * Get the Resource URL of the image
	 * @return Image resource url
	 */
	public String getResource_url() {
		return resource_url;
	}
	
	/**
	 * Set the Resource Url of the image
	 * @param resource_url Resource Url
	 */
	public void setResource_url(String resource_url) {
		this.resource_url = resource_url;
	}
}
