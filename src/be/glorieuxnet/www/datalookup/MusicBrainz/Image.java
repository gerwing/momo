package be.glorieuxnet.www.datalookup.MusicBrainz;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * This class represents an Image object obtained from a Musicbrainz lookup
 */
public class Image {
	private List<String> types;
	private String front;
	private String back;
	private String edit;
	private String image;
	private String comment;
	private String approved;
	private Thumbnail thumbnails;
	private String id;
	
	/**
	 * Get image types
	 * @return List with image types
	 */
	public List<String> getTypes() {
		return types;
	}
	
	/**
	 * Set Image types list
	 * @param types List with image types
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	/**
	 * Get front of image
	 * @return Image front
	 */
	public String getFront() {
		return front;
	}
	
	/**
	 * Set front of image
	 * @param front Image front
	 */
	public void setFront(String front) {
		this.front = front;
	}
	
	/**
	 * Get back of image
	 * @return Image back
	 */
	public String getBack() {
		return back;
	}
	
	/**
	 * Set back of Image
	 * @param back Image back
	 */
	public void setBack(String back) {
		this.back = back;
	}
	
	/**
	 * Get edit String
	 * @return Edit String
	 */
	public String getEdit() {
		return edit;
	}
	
	/**
	 * Set Edit string
	 * @param edit Edit string
 	 */
	public void setEdit(String edit) {
		this.edit = edit;
	}
	
	/**
	 * Get image String
	 * @return Image String
	 */
	public String getImage() {
		return image;
	}
	
	/**
	 * Set Image string
	 * @param image Image string
	 */
	public void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * Get comment String
	 * @return Comment String
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Set comment String
	 * @param comment Comment String
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * get Approved String
	 * @return Approved String
	 */
	public String getApproved() {
		return approved;
	}
	
	/**
	 * set Approved string
	 * @param approved Approved String
	 */
	public void setApproved(String approved) {
		this.approved = approved;
	}
	
	/**
	 * Get Image Id as string
	 * @return Image ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set Image ID
	 * @param id Image Id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/** Set the Thumbnails for this image object
	 * @param thumbnails Thumbnail object
	 */
	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}
	
	/**
	 * Get the Thumbnails for this image object
	 * @return Thumnails
	 */
	public Thumbnail getThumbnails() {
		return thumbnails;
	}
}
