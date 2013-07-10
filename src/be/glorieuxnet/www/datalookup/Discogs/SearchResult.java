package be.glorieuxnet.www.datalookup.Discogs;

import java.util.List;

/**
 * @author Gerwin Glorieux
 * This method represents a list of Search results from a Discogs lookup
 */
public class SearchResult {
	private List<Result> results;

	/**
	 * Returns a list of search results
	 * @return List with Result objects
	 */
	public List<Result> getResults() {
		return results;
	}

	/**
	 * Set a list of search objects
	 * @param results List of Result objects
	 */
	public void setResults(List<Result> results) {
		this.results = results;
	}
}
