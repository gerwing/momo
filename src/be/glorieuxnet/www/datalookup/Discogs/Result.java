package be.glorieuxnet.www.datalookup.Discogs;

/**
 * @author Gerwin Glorieux
 * This class represents a Search result of a Discogs lookup
 */
public class Result {
	private String id;

	/**
	 * Get the ID of the result
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the ID of the result
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}
}
