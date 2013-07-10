package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * List of Album objects
 */
public class AlbumList {
	List<AlbumSort_Album> list;

	/**
	 * Get the List of album objects
	 * @return List of Album objects
	 */
	public List<AlbumSort_Album> getList() {
		return list;
	}

	/**
	 * Set the list of Album objects
	 * @param list List of Album objects
	 */
	public void setList(List<AlbumSort_Album> list) {
		this.list = list;
	}
}
