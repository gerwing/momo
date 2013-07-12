package be.glorieuxnet.www.JSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import be.glorieuxnet.www.dao.CollectionDAO;
import be.glorieuxnet.www.dao.ManagementDAO;
import be.glorieuxnet.www.indexing.Configuration;
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
			File file = new File(JSONPATH + path); 
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF8"));
            out.write(json);
            out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

