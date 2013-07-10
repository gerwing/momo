package be.glorieuxnet.www.JSON;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * This class represent a list of JSON Song objects
 */
public class SongList {
	List<SongSort_Song> list;

	/**
	 * Get the list of songs
	 * @return List of SongSort_Song objects
	 */
	public List<SongSort_Song> getList() {
		return list;
	}

	/**
	 * Set the list of songs
	 * @param list List of SongSort_Song objects
	 */
	public void setList(List<SongSort_Song> list) {
		this.list = list;
	}
}
