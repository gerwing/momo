package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * JSON List of Artists 
 */
public class ArtistList {
	List<Artist> list;

	/**
	 * Get the list of artists
	 * @return List of artists
	 */
	public List<Artist> getList() {
		return list;
	}

	/**
	 * Set the list of artists
	 * @param list List of artists
	 */
	public void setList(List<Artist> list) {
		this.list = list;
	}
}
