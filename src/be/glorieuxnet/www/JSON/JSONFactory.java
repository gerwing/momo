package be.glorieuxnet.www.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import be.glorieuxnet.www.dao.CollectionDAO;
import be.glorieuxnet.www.dao.ManagementDAO;
import be.glorieuxnet.www.indexing.Configuration;
import be.glorieuxnet.www.indexing.Indexer;

import com.google.gson.Gson;

/**
 * @author Gerwin Glorieux
 * This class contains the methods that will create all the necassary JSON files to be used on
 * the Client side
 *
 */
public class JSONFactory {
	
	private final static String JSONPATH = "web" + File.separator + "resources" + File.separator + "json" + File.separator;
	private static CollectionDAO collection = new CollectionDAO();
	private static ManagementDAO mgmCollection = new ManagementDAO();
	
	/**
	 * Create all the JSON files from the database and overwrite existing JSON files
	 */
	public static void createJSONFromDatabase() {
		//Get Configuration
		Configuration c = Configuration.getConfiguration();
		
		//Create Artists JSONs
		
		//All Artists
		ArtistList artists = new ArtistList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			artists.setList(collection.selectAllArtists_ReturnJSONObjects());		
			for(Artist a:artists.getList()) {
				a.setAlbums(collection.selectAlbumsByArtistID_ReturnJSONObjects(a.getID()));
				for(Album ab: a.getAlbums()) {
					ab.setSongs(collection.selectSongsByAlbumID_ReturnJSONObjects(ab.getID()));
				}
			}
		}
		//do not show unidentified music
		else {
			artists.setList(mgmCollection.selectAllIdentifiedArtists_ReturnJSONObjects());		
			for(Artist a:artists.getList()) {
				a.setAlbums(mgmCollection.selectIdentifiedAlbumsByArtistID_ReturnJSONObjects(a.getID()));
				for(Album ab: a.getAlbums()) {
					ab.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(ab.getID()));
				}
			}
		}
		String json = new Gson().toJson(artists);
		saveJSON("AllArtists.json", json);
		System.out.println("AllArtists JSON saved");
		
		//CompArtists JSON
		artists = new ArtistList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			artists.setList(collection.selectCompArtists_ReturnJSONObjects());
			for(Artist a:artists.getList()) {
				a.setAlbums(collection.selectCompAlbumsByArtistID_ReturnJSONObjects(a.getID()));
				for(Album ab: a.getAlbums()) {
					ab.setSongs(collection.selectSongsByAlbumID_ReturnJSONObjects(ab.getID()));
				}
			}
		}
		//do not show unidentified
		else {
			artists.setList(collection.selectIdentifiedCompArtists_ReturnJSONObjects());
			for(Artist a:artists.getList()) {
				a.setAlbums(collection.selectIdentifiedCompAlbumsByArtistID_ReturnJSONObjects(a.getID()));
				for(Album ab: a.getAlbums()) {
					ab.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(ab.getID()));
				}
			}
		}
		json = new Gson().toJson(artists);
		saveJSON("CompArtists.json", json);
		System.out.println("CompArtists JSON saved");
		
		//Identified Artists
		artists = new ArtistList();
		artists.setList(mgmCollection.selectAllIdentifiedArtists_ReturnJSONObjects());		
		for(Artist a:artists.getList()) {
			a.setAlbums(mgmCollection.selectIdentifiedAlbumsByArtistID_ReturnJSONObjects(a.getID()));
			for(Album ab: a.getAlbums()) {
				ab.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(ab.getID()));
			}
		}
		json = new Gson().toJson(artists);
		saveJSON("IdentifiedArtists.json", json);
		System.out.println("IdentifiedArtists JSON saved");
		
		//UnIdentified Artists
		artists = new ArtistList();
		artists.setList(mgmCollection.selectAllUnIdentifiedArtists_ReturnJSONObjects());		
		for(Artist a:artists.getList()) {
			a.setAlbums(mgmCollection.selectUnIdentifiedAlbumsByArtistID_ReturnJSONObjects(a.getID()));
			for(Album ab: a.getAlbums()) {
				ab.setSongs(mgmCollection.selectUnIdentifiedSongsByAlbumID_ReturnJSONObjects(ab.getID()));
			}
		}
		json = new Gson().toJson(artists);
		saveJSON("UnIdentifiedArtists.json", json);
		System.out.println("UnIdentifiedArtists JSON saved");
		
		//No Cover Artists
		artists = new ArtistList();
		artists.setList(mgmCollection.selectAllNoCoverArtists_ReturnJSONObjects());		
		for(Artist a:artists.getList()) {
			a.setAlbums(mgmCollection.selectNoCoverAlbumsByArtistID_ReturnJSONObjects(a.getID()));
			for(Album ab: a.getAlbums()) {
				ab.setSongs(mgmCollection.selectNoCoverSongsByAlbumID_ReturnJSONObjects(ab.getID()));
			}
		}
		json = new Gson().toJson(artists);
		saveJSON("NoCoverArtists.json", json);
		System.out.println("NoCoverArtists JSON saved");
		
		//Create Albums JSONs
		
		//All Albums
		AlbumList albums = new AlbumList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			albums.setList(collection.selectAllAlbums_ReturnJSONObjects());
			for(AlbumSort_Album a:albums.getList()) {
				a.setSongs(collection.selectSongsByAlbumID_ReturnJSONObjects(a.getID()));
			}
		}
		//do not show unidentified music
		else {
			albums.setList(mgmCollection.selectAllIdentifiedAlbums_ReturnJSONObjects());
			for(AlbumSort_Album a:albums.getList()) {
				a.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(a.getID()));
			}
		}
		json = new Gson().toJson(albums);
		saveJSON("AllAlbums.json", json);
		System.out.println("AllAlbums JSON saved");
		
		//CompAlbums JSON
		albums = new AlbumList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			albums.setList(collection.selectCompAlbums_ReturnJSONObjects());
			for(AlbumSort_Album a:albums.getList()) {
				a.setSongs(collection.selectSongsByAlbumID_ReturnJSONObjects(a.getID()));
			}
		}
		//do not show unidentified music
		else {
			albums.setList(collection.selectIdentifiedCompAlbums_ReturnJSONObjects());
			for(AlbumSort_Album a:albums.getList()) {
				a.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(a.getID()));
			}
		}
		json = new Gson().toJson(albums);
		saveJSON("CompAlbums.json", json);
		System.out.println("CompAlbums JSON saved");
		
		//Identified Albums
		albums = new AlbumList();
		albums.setList(mgmCollection.selectAllIdentifiedAlbums_ReturnJSONObjects());
		for(AlbumSort_Album a:albums.getList()) {
			a.setSongs(mgmCollection.selectIdentifiedSongsByAlbumID_ReturnJSONObjects(a.getID()));
		}
		json = new Gson().toJson(albums);
		saveJSON("IdentifiedAlbums.json", json);
		System.out.println("IdentifiedAlbums JSON saved");
		
		//UnIdentified Albums
		albums = new AlbumList();
		albums.setList(mgmCollection.selectAllUnIdentifiedAlbums_ReturnJSONObjects());
		for(AlbumSort_Album a:albums.getList()) {
			a.setSongs(mgmCollection.selectUnIdentifiedSongsByAlbumID_ReturnJSONObjects(a.getID()));
		}
		json = new Gson().toJson(albums);
		saveJSON("UnIdentifiedAlbums.json", json);
		System.out.println("UnIdentifiedAlbums JSON saved");
		
		//NoCover Albums
		albums = new AlbumList();
		albums.setList(mgmCollection.selectAllNoCoverAlbums_ReturnJSONObjects());
		for(AlbumSort_Album a:albums.getList()) {
			a.setSongs(mgmCollection.selectNoCoverSongsByAlbumID_ReturnJSONObjects(a.getID()));
		}
		json = new Gson().toJson(albums);
		saveJSON("NoCoverAlbums.json", json);
		System.out.println("NoCoverAlbums JSON saved");
		
		//Create Songs JSONs
		//All Songs
		SongList songs = new SongList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			songs.setList(collection.selectAllSongs_ReturnJSONObjects());
		}
		//do not show unidentified music
		else {
			songs.setList(mgmCollection.selectAllIdentifiedSongs_ReturnJSONObjects());
		}
		json = new Gson().toJson(songs);
		saveJSON("AllSongs.json", json);
		System.out.println("AllSongs JSON saved");
		//CompSongs JSON
		songs = new SongList();
		//show unidentified music
		if(c.isShowUnIdentified()) {
			songs.setList(collection.selectCompSongs_ReturnJSONObjects());
		}
		//do not show unidentified music
		else {
			songs.setList(collection.selectIdentifiedCompSongs_ReturnJSONObjects());
		}
		json = new Gson().toJson(songs);
		saveJSON("CompSongs.json", json);
		System.out.println("CompSongs JSON saved");
		//Identified Songs
		songs = new SongList();
		songs.setList(mgmCollection.selectAllIdentifiedSongs_ReturnJSONObjects());
		json = new Gson().toJson(songs);
		saveJSON("IdentifiedSongs.json", json);
		System.out.println("IdentifiedSongs JSON saved");
		//UnIdentified Songs
		songs = new SongList();
		songs.setList(mgmCollection.selectAllUnIdentifiedSongs_ReturnJSONObjects());
		json = new Gson().toJson(songs);
		saveJSON("UnIdentifiedSongs.json", json);
		System.out.println("UnIdentifiedSongs JSON saved");
		//No Cover Songs
		songs = new SongList();
		songs.setList(mgmCollection.selectAllNoCoverSongs_ReturnJSONObjects());
		json = new Gson().toJson(songs);
		saveJSON("NoCoverSongs.json", json);
		System.out.println("NoCoverSongs JSON saved");
	}
	
	/**
	 * Get an Artistlist with All the Artists
	 * @return Artistlist object
	 */
	public static ArtistList getAllArtists() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "AllArtists.json"));
			ArtistList artists = new Gson().fromJson(br, ArtistList.class);
			return artists;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArtistList();
		}
	}
	
	/**
	 * Get an AlbumList with all the Albums
	 * @return AlbumList object
	 */
	public static AlbumList getAllAlbums() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "AllAlbums.json"));
			AlbumList albums = new Gson().fromJson(br, AlbumList.class);
			return albums;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new AlbumList();
		}
	}
	
	/**
	 * Get a Songlist with all the songs
	 * @return Songlist object
	 */
	public static SongList getAllSongs() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "AllSongs.json"));
			SongList songs = new Gson().fromJson(br, SongList.class);
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SongList();
		}
	}
	
	/**
	 * Get a Songlist with all the songs from compilation albums
	 * @return Songlist object
	 */
	public static SongList getCompSongs() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "CompSongs.json"));
			SongList songs = new Gson().fromJson(br, SongList.class);
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SongList();
		}
	}
	
	/**
	 * Get an ArtistList object with all the artists from compilation albums
	 * @return ArtistList object
	 */
	public static ArtistList getCompArtists() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "CompArtists.json"));
			ArtistList artists = new Gson().fromJson(br, ArtistList.class);
			return artists;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArtistList();
		}
	}
	
	/**
	 * Get an AlbumList object with all the compilation albums
	 * @return Albumlist object
	 */
	public static AlbumList getCompAlbums() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "CompAlbums.json"));
			AlbumList albums = new Gson().fromJson(br, AlbumList.class);
			return albums;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new AlbumList();
		}
	}
	
	/**
	 * Get an ArtistList object with all the identified artists 
	 * @return Artistlist Object
	 */
	public static ArtistList getIdentifiedArtists() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "IdentifiedArtists.json"));
			ArtistList artists = new Gson().fromJson(br, ArtistList.class);
			return artists;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArtistList();
		}
	}
	
	/**
	 * Get an AlbumList object with all the identified albums
	 * @return AlbumList object
	 */
	public static AlbumList getIdentifiedAlbums() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "IdentifiedAlbums.json"));
			AlbumList albums = new Gson().fromJson(br, AlbumList.class);
			return albums;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new AlbumList();
		}
	}
	
	/**
	 * Get a Songlist object with all the identified songs 
	 * @return Songlist object
	 */
	public static SongList getIdentifiedSongs() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "IdentifiedSongs.json"));
			SongList songs = new Gson().fromJson(br, SongList.class);
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SongList();
		}
	}
	
	/**
	 * Get an ArtistList object with all the artists that have unidentified objects
	 * @return ArtistList object
	 */
	public static ArtistList getUnIdentifiedArtists() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "UnIdentifiedArtists.json"));
			ArtistList artists = new Gson().fromJson(br, ArtistList.class);
			return artists;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArtistList();
		}
	}
	
	/**
	 * Get an AlbumList object with all the undifentified Albums
	 * @return AlbumList object
	 */
	public static AlbumList getUnIdentifiedAlbums() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "UnIdentifiedAlbums.json"));
			AlbumList albums = new Gson().fromJson(br, AlbumList.class);
			return albums;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new AlbumList();
		}
	}
	
	/**
	 * Get a Songlist object with all the unidentified songs
	 * @return Songlist object
	 */
	public static SongList getUnIdentifiedSongs() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "UnIdentifiedSongs.json"));
			SongList songs = new Gson().fromJson(br, SongList.class);
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SongList();
		}
	}
	
	/**
	 * Get an ArtistList object with all the artist that have an album with a missing cover
	 * @return ArtistList object
	 */
	public static ArtistList getNoCoverArtists() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "NoCoverArtists.json"));
			ArtistList artists = new Gson().fromJson(br, ArtistList.class);
			return artists;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArtistList();
		}
	}
	
	/**
	 * Get an AlbumList object with all the albums with a missing cover
	 * @return AlbumList object
	 */
	public static AlbumList getNoCoverAlbums() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "NoCoverAlbums.json"));
			AlbumList albums = new Gson().fromJson(br, AlbumList.class);
			return albums;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new AlbumList();
		}
	}
	
	/**
	 * Get a SongList object with all the songs from albums with a missing cover
	 * @return Songlist Object
	 */
	public static SongList getNoCoverSongs() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(JSONPATH + "NoCoverSongs.json"));
			SongList songs = new Gson().fromJson(br, SongList.class);
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SongList();
		}
	}
	
	/** Get an EditAlbum object for a given Album ID
	 * @param id Album ID
	 * @return EditAlbum object containing data of found album
	 */
	public static EditAlbum getAlbumByID(String id) {
		EditAlbum album = mgmCollection.selectAlbumByID_ReturnJSONObject(id);
		album.setSongs(collection.selectSongsByAlbumID_ReturnJSONObjects(id));
		return album;
	}
	
	private static void saveJSON (String path, String json) {
		try {
			FileWriter writer = new FileWriter(JSONPATH + path);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

