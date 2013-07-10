package be.glorieuxnet.www.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;

import be.glorieuxnet.www.JSON.Album;
import be.glorieuxnet.www.JSON.AlbumSort_Album;
import be.glorieuxnet.www.JSON.Artist;
import be.glorieuxnet.www.JSON.EditAlbum;
import be.glorieuxnet.www.JSON.Song;
import be.glorieuxnet.www.JSON.SongSort_Song;

/**
 * @author Gerwin Glorieux
 * ManagementDAO Class
 * This class provides methods to access and change the database. The methods in this class are related to
 * the management specific data in the database
 */
public class ManagementDAO {

	/*
	 * Database params
	 */
	private final static String URL = "jdbc:hsqldb:hsqldb/musicdb";
	private final static String USERNAME = "SA";
	private final static String PASSWORD = "";
	
	/*
	 * Prepared SQL Statements
	 */
	private Connection connection;
	
	private PreparedStatement selectAllIdentifiedArtists;
	private PreparedStatement selectAllUnIdentifiedArtists;
	private PreparedStatement selectAllNoCoverArtists;
	private PreparedStatement selectIdentifiedAlbumsByArtistID;
	private PreparedStatement selectUnIdentifiedAlbumsByArtistID;
	private PreparedStatement selectNoCoverAlbumsByArtistID;
	private PreparedStatement selectIdentifiedSongsByAlbumID;
	private PreparedStatement selectUnIdentifiedSongsByAlbumID;
	private PreparedStatement selectNoCoverSongsByAlbumID;
	private PreparedStatement selectAllIdentifiedAlbums;
	private PreparedStatement selectAllUnIdentifiedAlbums;
	private PreparedStatement selectAllNoCoverAlbums;
	private PreparedStatement selectAllIdentifiedSongs;
	private PreparedStatement selectAllUnIdentifiedSongs;
	private PreparedStatement selectAllNoCoverSongs;
	//EDIT album statements
	private PreparedStatement selectAlbumByID;
	private PreparedStatement updateAlbumByID;
	private PreparedStatement updateSongByID;
	private PreparedStatement updateAlbumCoverByID;
	
	/**
	 * Constructor Method
	 * This method will create a new managementDAO object and load all the SQL statements in this class 
	 * as Prepared Statement objects
	 */
	public ManagementDAO() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			connection = 
					DriverManager.getConnection(URL,USERNAME,PASSWORD);
			//SQL STATEMENTS
			//SELECTS
			selectAllIdentifiedArtists = connection.prepareStatement(
					"SELECT DISTINCT tblArtists.ID, tblArtists.name " +
					"FROM tblArtists " +
					"INNER JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID  " +
					"WHERE tblSongs.identified = TRUE " +
					"ORDER BY tblArtists.name");
			selectAllUnIdentifiedArtists = connection.prepareStatement(
					"SELECT DISTINCT tblArtists.ID, tblArtists.name " +
					"FROM tblArtists " +
					"INNER JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID  " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID  " +
					"WHERE tblSongs.identified = FALSE " +
					"ORDER BY tblArtists.name");
			selectAllNoCoverArtists = connection.prepareStatement(
					"SELECT DISTINCT tblArtists.ID, tblArtists.name " +
					"FROM tblArtists " +
					"INNER JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID " +
					"WHERE tblAlbums.cover_filepath = '" + AudioFile.NO_COVER_PATH + "' "  +
					"ORDER BY tblArtists.name");
			selectIdentifiedAlbumsByArtistID = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath " +
					"FROM tblAlbums " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblAlbums.tblArtists_ID = ? AND tblSongs.identified = TRUE " +
					"ORDER BY tblAlbums.title");
			selectUnIdentifiedAlbumsByArtistID = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath " +
					"FROM tblAlbums " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblAlbums.tblArtists_ID = ? AND tblSongs.identified = FALSE " +
					"ORDER BY tblAlbums.title");
			selectNoCoverAlbumsByArtistID = connection.prepareStatement(
					"SELECT DISTINCT ID, title, cover_filepath " +
					"FROM tblAlbums " +
					"WHERE tblArtists_ID = ? AND cover_filepath = '" + AudioFile.NO_COVER_PATH + "' " +
					"ORDER BY title");
			selectIdentifiedSongsByAlbumID = connection.prepareStatement(
					"SELECT ID, title, artist, tracknumber, length " +
					"FROM tblSongs " +
					"WHERE tblAlbums_ID = ? AND identified = TRUE " +
					"ORDER BY tracknumber");
			selectUnIdentifiedSongsByAlbumID = connection.prepareStatement(
					"SELECT ID, title, artist, tracknumber, length " +
					"FROM tblSongs " +
					"WHERE tblAlbums_ID = ? AND identified = FALSE " +
					"ORDER BY tracknumber");
			selectNoCoverSongsByAlbumID = connection.prepareStatement(
					"SELECT ID, title, artist, tracknumber, length " +
					"FROM tblSongs " +
					"WHERE tblAlbums_ID = ? " +
					"ORDER BY tracknumber");
			selectAllIdentifiedAlbums = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblSongs.identified = TRUE " +
					"ORDER BY tblAlbums.title");
			selectAllUnIdentifiedAlbums = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblSongs.identified = FALSE " +
					"ORDER BY tblAlbums.title");
			selectAllNoCoverAlbums = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"WHERE tblAlbums.cover_filepath = '" + AudioFile.NO_COVER_PATH + "' " +
					"ORDER BY tblAlbums.title");
			selectAllIdentifiedSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"WHERE tblSongs.identified = TRUE " +
					"ORDER BY tblSongs.title");
			selectAllUnIdentifiedSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"WHERE tblSongs.identified = FALSE " +
					"ORDER BY tblSongs.title");
			selectAllNoCoverSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"WHERE tblAlbums.cover_filepath = '" + AudioFile.NO_COVER_PATH + "' " +
					"ORDER BY tblSongs.title");
			selectAlbumByID = connection.prepareStatement(
					"SELECT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblAlbums.compilation, " +
					"tblAlbums.no_oftracks, tblAlbums.disc_no, tblAlbums.year, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"WHERE tblAlbums.ID = ?");
			
			//Edit statements
			updateAlbumByID = connection.prepareStatement(
					"UPDATE tblAlbums " +
					"SET title = ?, year = ?, no_oftracks = ?, disc_no = ?, cover_filepath = ?, compilation = ?, tblArtists_ID = ? " +
					"WHERE ID = ?");
			updateSongByID = connection.prepareStatement(
					"UPDATE tblSongs " +
					"SET title = ?, artist = ?, tracknumber = ?, identified = ?, tblAlbums_ID = ? " +
					"WHERE ID = ?");
			updateAlbumCoverByID = connection.prepareStatement(
					"UPDATE tblAlbums " +
					"SET cover_filepath = ? " +
					"WHERE ID = ?");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Select all Artists with identified songs and return them as a list of JSON package Artist objects
	 * @return List of JSON package Artist objects
	 */
	public List<Artist> selectAllIdentifiedArtists_ReturnJSONObjects () {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectAllIdentifiedArtists.executeQuery();
			
			while(rs.next()) {
				Artist a = new Artist();
				a.setID(rs.getString("ID"));
				a.setName(rs.getString("name"));
				artists.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return artists;
	}
	
	/**
	 * Select all Artists with unidentified songs and return them as a list of JSON package Artist objects
	 * @return List of JSON package Artist objects
	 */
	public List<Artist> selectAllUnIdentifiedArtists_ReturnJSONObjects () {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectAllUnIdentifiedArtists.executeQuery();
			
			while(rs.next()) {
				Artist a = new Artist();
				a.setID(rs.getString("ID"));
				a.setName(rs.getString("name"));
				artists.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return artists;
	}
	
	/**
	 * Select all Artists of albums without a cover and return them as a list of JSON package Artist objects
	 * @return List of JSON package Artist objects
	 */
	public List<Artist> selectAllNoCoverArtists_ReturnJSONObjects () {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectAllNoCoverArtists.executeQuery();
			
			while(rs.next()) {
				Artist a = new Artist();
				a.setID(rs.getString("ID"));
				a.setName(rs.getString("name"));
				artists.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return artists;
	}
	
	/**
	 * Select all albums with identified songs given an artist ID
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectIdentifiedAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectIdentifiedAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectIdentifiedAlbumsByArtistID.executeQuery();
			while(rs.next()) {
				Album a = new Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all albums with unidentified songs given an artist ID
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectUnIdentifiedAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectUnIdentifiedAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectUnIdentifiedAlbumsByArtistID.executeQuery();
			while(rs.next()) {
				Album a = new Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all albums without cover given an artist ID
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectNoCoverAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectNoCoverAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectNoCoverAlbumsByArtistID.executeQuery();
			while(rs.next()) {
				Album a = new Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all identified songs given an Album ID
	 * @param id Album ID
	 * @return List of JSON package Song objects.
	 */
	public List<Song> selectIdentifiedSongsByAlbumID_ReturnJSONObjects (String id) {
		List<Song> songs = new ArrayList<Song>();
		try {
			selectIdentifiedSongsByAlbumID.setString(1, id);
			ResultSet rs = selectIdentifiedSongsByAlbumID.executeQuery();
			while(rs.next()) {
				Song s = new Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("title"));
				s.setArtist(rs.getString("artist"));
				s.setTracknumber(rs.getString("tracknumber"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select all unidentified songs given an Album ID
	 * @param id Album ID
	 * @return List of JSON package Song objects.
	 */
	public List<Song> selectUnIdentifiedSongsByAlbumID_ReturnJSONObjects (String id) {
		List<Song> songs = new ArrayList<Song>();
		try {
			selectUnIdentifiedSongsByAlbumID.setString(1, id);
			ResultSet rs = selectUnIdentifiedSongsByAlbumID.executeQuery();
			while(rs.next()) {
				Song s = new Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("title"));
				s.setArtist(rs.getString("artist"));
				s.setTracknumber(rs.getString("tracknumber"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select all songs from an album with missing cover given an Album ID
	 * @param id Album ID
	 * @return List of JSON package Song objects.
	 */
	public List<Song> selectNoCoverSongsByAlbumID_ReturnJSONObjects (String id) {
		List<Song> songs = new ArrayList<Song>();
		try {
			selectNoCoverSongsByAlbumID.setString(1, id);
			ResultSet rs = selectNoCoverSongsByAlbumID.executeQuery();
			while(rs.next()) {
				Song s = new Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("title"));
				s.setArtist(rs.getString("artist"));
				s.setTracknumber(rs.getString("tracknumber"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select all albums with identified songs and return them as a list of AlbumSort_Album objects
	 * @return Arraylist containing AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectAllIdentifiedAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectAllIdentifiedAlbums.executeQuery();
			while(rs.next()) {
				AlbumSort_Album a = new AlbumSort_Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				a.setAlbumArtist(rs.getString("name"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all the albums with unidentified songs and return them as a list of AlbumSort_Album objects
	 * @return Arraylist containing AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectAllUnIdentifiedAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectAllUnIdentifiedAlbums.executeQuery();
			while(rs.next()) {
				AlbumSort_Album a = new AlbumSort_Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				a.setAlbumArtist(rs.getString("name"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all Albums that have no Album cover and return them as a list of AlbumSort_Album objects
	 * @return Arraylist containing AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectAllNoCoverAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectAllNoCoverAlbums.executeQuery();
			while(rs.next()) {
				AlbumSort_Album a = new AlbumSort_Album();
				a.setID(rs.getString("ID"));
				a.setTitle(rs.getString("title"));
				a.setCover_filepath(rs.getString("cover_filepath"));
				a.setAlbumArtist(rs.getString("name"));
				albums.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	/**
	 * Select all identified songs from DB and return them as a list of SongSort_Song ojbects
	 * @return Arraylist containing SongSort_Song objects
	 */
	public List<SongSort_Song> selectAllIdentifiedSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectAllIdentifiedSongs.executeQuery();
			while(rs.next()) {
				SongSort_Song s = new SongSort_Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("tblSongs.title"));
				s.setArtist(rs.getString("artist"));
				s.setAlbum(rs.getString("tblAlbums.title"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select all Unidentified songs and return them as a list of SongSort_Song objects
	 * @return List of SongSort_Song objects
	 */
	public List<SongSort_Song> selectAllUnIdentifiedSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectAllUnIdentifiedSongs.executeQuery();
			while(rs.next()) {
				SongSort_Song s = new SongSort_Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("tblSongs.title"));
				s.setArtist(rs.getString("artist"));
				s.setAlbum(rs.getString("tblAlbums.title"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select all songs from albums that have a no cover and return them as a list of SongSort_Song objects
	 * @return Arraylist containing SongSort_Song objects
	 */
	public List<SongSort_Song> selectAllNoCoverSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectAllNoCoverSongs.executeQuery();
			while(rs.next()) {
				SongSort_Song s = new SongSort_Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath("PlayerService?action=play&id="+rs.getString("ID"));
				s.setTitle(rs.getString("tblSongs.title"));
				s.setArtist(rs.getString("artist"));
				s.setAlbum(rs.getString("tblAlbums.title"));
				//calculate length
				int length = rs.getInt("length");
				int l1 = length/60;
				String l2 = Integer.toString(length%60);
				if(l2.equals("0")) l2+="0";
				else if(Integer.parseInt(l2)<10) l2 = "0" + l2;
				s.setLength(l1+":"+l2);
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * Select an Album by the given ID and return it as a EditAlbum object
	 * @param id Id of the album
	 * @return EditAlbum object containing the album with the given ID
	 */
	public EditAlbum selectAlbumByID_ReturnJSONObject(String id) {
		EditAlbum a = new EditAlbum();
		try {
			selectAlbumByID.setString(1, id);
			ResultSet rs = selectAlbumByID.executeQuery();
			rs.next();
			a.setID(rs.getString("ID"));
			a.setTitle(rs.getString("title"));
			a.setCover_filepath(rs.getString("cover_filepath"));
			a.setAlbumArtist(rs.getString("name"));
			a.setCompilation(rs.getBoolean("compilation"));
			a.setYear(rs.getString("year"));
			a.setNo_oftracks(rs.getString("no_oftracks"));
			a.setDisc_no(rs.getString("disc_no"));
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return a;
	}
	
	/**
	 * Update an album in the database
	 * @param album EditAlbum ojbect containing the Album data
	 * @param artistID Id of the artist 
	 * @return Amount of updated albums
	 */
	public int updateAlbum(EditAlbum album, int artistID) {
		try {
			updateAlbumByID.setString(1, album.getTitle());
			updateAlbumByID.setString(2, album.getYear());
			updateAlbumByID.setString(3, album.getNo_oftracks());
			updateAlbumByID.setString(4, album.getDisc_no());
			updateAlbumByID.setString(5, album.getCover_filepath());
			updateAlbumByID.setBoolean(6, album.isCompilation());
			updateAlbumByID.setInt(7, artistID);
			updateAlbumByID.setString(8, album.getID());
			return updateAlbumByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Update a song in the database
	 * @param song Song object containing the song data that should be updated
	 * @param identified Boolean that is true if the song was identified either it should be false
	 * @param albumID Id of the album the song belongs to
	 * @return Amount of songs that were updated
	 */
	public int updateSong(Song song, boolean identified, int albumID){
		try {
			updateSongByID.setString(1, song.getTitle());
			updateSongByID.setString(2, song.getArtist());
			updateSongByID.setString(3, song.getTracknumber());
			updateSongByID.setBoolean(4, identified);
			updateSongByID.setInt(5, albumID);
			updateSongByID.setString(6, song.getId());
			return updateSongByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Update the cover of an album
	 * @param id Id of the album
	 * @param filepath absolute filepath of the cover on the local filesystem
	 * @return Amount of albums that were updated
	 */
	public int updateAlbumCoverByID(String id, String filepath) {
		try {
			updateAlbumCoverByID.setString(1, filepath);
			updateAlbumCoverByID.setString(2, id);
			return updateAlbumCoverByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
}
