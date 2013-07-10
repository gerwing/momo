package be.glorieuxnet.www.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;

import be.glorieuxnet.www.JSON.Album;
import be.glorieuxnet.www.JSON.AlbumSort_Album;
import be.glorieuxnet.www.JSON.Artist;
import be.glorieuxnet.www.JSON.Song;
import be.glorieuxnet.www.JSON.SongSort_Song;

/**
 * @author Gerwin Glorieux
 * CollectionDAO Class
 * This class provides methods to access and change the database. The database used in this class is 
 * the HSQLDB included in the hsqldb folder
 */
public class CollectionDAO {

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
	private PreparedStatement selectArtistByName;
	private PreparedStatement selectAlbumByTitleArtistDiscNo;
	private PreparedStatement selectSongByFilepath;
	private PreparedStatement selectAllArtists;
	private PreparedStatement selectAlbumsByArtistID;
	private PreparedStatement selectSongsByAlbumID;
	private PreparedStatement selectSongFilepathByID;
	private PreparedStatement selectAllAlbums;
	private PreparedStatement selectAllSongs;
	private PreparedStatement selectCompArtists;
	private PreparedStatement selectCompAlbums;
	private PreparedStatement selectCompAlbumsByArtistID;
	private PreparedStatement selectCompSongs;
	private PreparedStatement selectIdentifiedCompArtists;
	private PreparedStatement selectIdentifiedCompAlbums;
	private PreparedStatement selectIdentifiedCompAlbumsByArtistID;
	private PreparedStatement selectIdentifiedCompSongs;
	private PreparedStatement selectAllSongsIDFromFolder;
	private PreparedStatement selectEmptyAlbums;
	private PreparedStatement selectEmptyArtists;
	private PreparedStatement selectAllSongFilepaths;
	private PreparedStatement insertArtist;
	private PreparedStatement insertAlbum;
	private PreparedStatement insertSong;
	private PreparedStatement deleteSongByID;
	private PreparedStatement deleteAlbumByID;
	private PreparedStatement deleteArtistByID;
	private PreparedStatement deleteAllArtists;
	private PreparedStatement deleteAllAlbums;
	private PreparedStatement deleteAllSongs;
	
	/**
	 * Constructor Method
	 * This method creates a new object of the CollectionDAO class and loads all the SQL statements as
	 * preparedStatement objects
	 */
	public CollectionDAO() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			connection = 
					DriverManager.getConnection(URL,USERNAME,PASSWORD);
			//SQL STATEMENTS
			//SELECTS
			selectArtistByName = connection.prepareStatement(
					"SELECT ID " +
					"FROM tblArtists " +
					"WHERE name = ?");
			selectAlbumByTitleArtistDiscNo = connection.prepareStatement(
					"SELECT tblAlbums.ID " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"WHERE tblAlbums.title = ? AND tblArtists.name = ? AND tblAlbums.disc_no = ?");
			selectSongByFilepath = connection.prepareStatement(
					"SELECT ID " +
					"FROM tblSongs " +
					"WHERE filepath = ?");
			selectAllArtists = connection.prepareStatement(
					"SELECT ID, name " +
					"FROM tblArtists " +
					"ORDER BY name");
			selectAlbumsByArtistID = connection.prepareStatement(
					"SELECT ID, title, cover_filepath " +
					"FROM tblAlbums " +
					"WHERE tblArtists_ID = ? " +
					"ORDER BY title");
			selectSongsByAlbumID = connection.prepareStatement(
					"SELECT ID, title, artist, tracknumber, length " +
					"FROM tblSongs " +
					"WHERE tblAlbums_ID = ? " +
					"ORDER BY tracknumber");
			selectSongFilepathByID = connection.prepareStatement(
					"SELECT filepath " +
					"FROM tblSongs " +
					"WHERE ID = ?");
			selectAllAlbums = connection.prepareStatement(
					"SELECT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"ORDER BY tblAlbums.title");
			selectAllSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"ORDER BY tblSongs.title");
			selectCompArtists = connection.prepareStatement(
					"SELECT DISTINCT tblArtists.ID, tblArtists.name " +
					"FROM tblArtists " +
					"INNER JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID " +
					"WHERE tblAlbums.compilation = TRUE " +
					"ORDER BY tblArtists.name");
			selectCompAlbumsByArtistID = connection.prepareStatement(
					"SELECT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"WHERE tblAlbums.compilation = TRUE AND tblAlbums.tblArtists_ID = ? " +
					"ORDER BY tblAlbums.title");
			selectCompAlbums = connection.prepareStatement(
					"SELECT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"WHERE tblAlbums.compilation = TRUE " +
					"ORDER BY tblAlbums.title");
			selectCompSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"WHERE tblAlbums.compilation = TRUE " +
					"ORDER BY tblSongs.title");
			selectIdentifiedCompArtists = connection.prepareStatement(
					"SELECT DISTINCT tblArtists.ID, tblArtists.name " +
					"FROM tblArtists " +
					"INNER JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID  " +
					"WHERE tblAlbums.compilation = TRUE AND tblSongs.identified = TRUE " +
					"ORDER BY tblArtists.name");
			selectIdentifiedCompAlbumsByArtistID = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblAlbums.compilation = TRUE AND tblSongs.identified = TRUE AND tblAlbums.tblArtists_ID = ? " +
					"ORDER BY tblAlbums.title");
			selectIdentifiedCompAlbums = connection.prepareStatement(
					"SELECT DISTINCT tblAlbums.ID, tblAlbums.title, tblAlbums.cover_filepath, tblArtists.name " +
					"FROM tblAlbums " +
					"INNER JOIN tblArtists ON tblAlbums.tblArtists_ID = tblArtists.ID " +
					"INNER JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblAlbums.compilation = TRUE AND tblSongs.identified = TRUE " +
					"ORDER BY tblAlbums.title");
			selectIdentifiedCompSongs = connection.prepareStatement(
					"SELECT tblSongs.ID, tblSongs.title, tblSongs.artist, tblSongs.length, tblAlbums.title " +
					"FROM tblSongs " +
					"INNER JOIN tblAlbums ON tblSongs.tblAlbums_ID = tblAlbums.ID " +
					"WHERE tblAlbums.compilation = TRUE AND tblSongs.identified = TRUE " +
					"ORDER BY tblSongs.title");
			selectAllSongsIDFromFolder = connection.prepareStatement(
					"SELECT ID " +
					"FROM tblSongs " +
					"WHERE filepath LIKE ?");
			selectEmptyAlbums = connection.prepareStatement(
					"SELECT tblAlbums.ID " +
					"FROM tblAlbums " +
					"LEFT JOIN tblSongs ON tblAlbums.ID = tblSongs.tblAlbums_ID " +
					"WHERE tblSongs.ID IS NULL");
			selectEmptyArtists = connection.prepareStatement(
					"SELECT tblArtists.ID " +
					"FROM tblArtists " +
					"LEFT JOIN tblAlbums ON tblArtists.ID = tblAlbums.tblArtists_ID " +
					"WHERE tblAlbums.ID IS NULL");
			selectAllSongFilepaths = connection.prepareStatement(
					"SELECT ID, filepath " +
					"FROM tblSongs");
			
			//INSERTS
			insertArtist = connection.prepareStatement(
					"INSERT INTO tblArtists (name, cover_filepath) " +
					"VALUES (?, ?);",java.sql.Statement.RETURN_GENERATED_KEYS);
			insertAlbum = connection.prepareStatement(
					"INSERT INTO tblAlbums (title, year, no_oftracks, disc_no, cover_filepath, compilation, tblArtists_ID) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?);", java.sql.Statement.RETURN_GENERATED_KEYS);
			insertSong = connection.prepareStatement(
					"INSERT INTO tblSongs (title, artist, tracknumber, length, format, filepath, identified, tblAlbums_ID) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?);", java.sql.Statement.RETURN_GENERATED_KEYS);
			
			//DELETE
			deleteSongByID = connection.prepareStatement(
					"DELETE FROM tblSongs " +
					"WHERE ID = ?");
			deleteAlbumByID = connection.prepareStatement(
					"DELETE FROM tblAlbums " +
					"WHERE ID = ?");
			deleteArtistByID = connection.prepareStatement(
					"DELETE FROM tblArtists " +
					"WHERE ID = ?");
			deleteAllArtists = connection.prepareStatement(
					"DELETE FROM tblArtists");
			deleteAllAlbums = connection.prepareStatement(
					"DELETE FROM tblAlbums");
			deleteAllSongs = connection.prepareStatement(
					"DELETE FROM tblSongs");
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * InsertSong Method. Use this method to insert a song by passing an Object from the JAudiotagger 
	 * AudioFile class that represents an Audio File. The file will then be inserted divided over the Artist
	 * Album and Song table.
	 * This method will make sure that no Artist or Album are duplicated in the database
	 * @param file JAudiotagger AudioFile Object
	 */
	public void insertSong(AudioFile file) {
		//Variables
		Tag t = file.getTag();
		AudioHeader h = file.getAudioHeader();
		ResultSet ids;
		int artist_id;
		int album_id;
		int completeLevel = file.getCompleteLevel();
		
		try {
			//Check if song is already in database, if so do nothing
			if(selectSongByFilepath(file.getFile().getAbsolutePath())==-1)
			{
				//determine album artist
				String albumArtist = file.getAlbumArtist();
				//insert artist info
				artist_id = selectArtistByName(albumArtist);
				if(artist_id==-1)//check if artist is already in database
				{
					insertArtist.setString(1, albumArtist);
					insertArtist.setString(2, null);//artist cover
					insertArtist.executeUpdate();
					ids = insertArtist.getGeneratedKeys();
					ids.next();
					artist_id = ids.getInt(1);
				}
				//insert album info
				String discno = (AudioFile.isEmpty(t.getFirst(FieldKey.DISC_NO))) ? "1" : t.getFirst(FieldKey.DISC_NO);
				album_id = selectAlbumByTitleArtistDiscNo(t.getFirst(FieldKey.ALBUM),albumArtist,discno);
				if(album_id==-1)//check if album is already in database
				{
					insertAlbum.setString(1, t.getFirst(FieldKey.ALBUM));
					insertAlbum.setString(2, t.getFirst(FieldKey.YEAR));
					String tracktotal = AudioFile.isEmpty(t.getFirst(FieldKey.TRACK_TOTAL)) ? "" : t.getFirst(FieldKey.TRACK_TOTAL);
					insertAlbum.setString(3, tracktotal);
					insertAlbum.setString(4, discno);
					//Determine if cover exists and get webcoverpath
					File cover = new File(file.getCoverPath());
					String coverpath = (cover.exists()) ? file.getWebCoverPath():"resources/covers/no_cover.jpg";//add standard cover
					insertAlbum.setString(5, coverpath);
					boolean comp = (t.getFirst(FieldKey.IS_COMPILATION).equals("1")) ? true : false;
					insertAlbum.setBoolean(6, comp);
					insertAlbum.setInt(7, artist_id);
					insertAlbum.executeUpdate();
					ids = insertAlbum.getGeneratedKeys();
					ids.next();
					album_id = ids.getInt(1);
				}
				//insert song info
				insertSong.setString(1, t.getFirst(FieldKey.TITLE));
				insertSong.setString(2, t.getFirst(FieldKey.ARTIST));
				String tracknumber = AudioFile.isEmpty(t.getFirst(FieldKey.TRACK)) ? "1":t.getFirst(FieldKey.TRACK);
				insertSong.setString(3, tracknumber);
				insertSong.setInt(4, h.getTrackLength());
				insertSong.setString(5, h.getFormat());
				insertSong.setString(6, file.getFile().getAbsolutePath());
				boolean id = (completeLevel==AudioFile.ALL_DATA_AND_COVER||
						completeLevel==AudioFile.ALL_DATA_NO_COVER) ? true:false;
				insertSong.setBoolean(7, id);
				insertSong.setInt(8, album_id);
				insertSong.executeUpdate();
			}
		} catch (KeyNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * InsertArtist Method
	 * Use this method to insert a new artist in to the databse
	 * @param albumArtist Name of the artist
	 * @return the ID of the newly created artist in the database
	 */
	public int insertArtist(String albumArtist) {
		try {
			insertArtist.setString(1, albumArtist);
			insertArtist.setString(2, null);//artist cover
			insertArtist.executeUpdate();
			ResultSet ids = insertArtist.getGeneratedKeys();
			ids.next();
			return ids.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Select Artist by name method. Returns the Id of an artist given an artist name
	 * @param name Name of the artist
	 * @return Id of the resulting artist. If nothing is found this method will return -1.
	 */
	public int selectArtistByName(String name) {
		int resultID=-1;
		try {
			selectArtistByName.setString(1, name);
			ResultSet rs = selectArtistByName.executeQuery();
			while(rs.next()) 
			{resultID =rs.getInt(1);}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultID;
	}
	
	/**
	 * Select an Album by a given title, artist and disc number
	 * @param title Title of the album
	 * @param artist Artist of the album
	 * @param discno Disc number of the album
	 * @return Album ID of the album in the database if it is found, else this method will return -1.
	 */
	public int selectAlbumByTitleArtistDiscNo(String title, String artist, String discno) {
		int resultID=-1;
		try {
			selectAlbumByTitleArtistDiscNo.setString(1, title);
			selectAlbumByTitleArtistDiscNo.setString(2, artist);
			selectAlbumByTitleArtistDiscNo.setString(3, discno);
			ResultSet rs = selectAlbumByTitleArtistDiscNo.executeQuery();
			while(rs.next()) 
				{resultID =rs.getInt(1);}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultID;
	}
	
	/**
	 * Select a Song by a given filepath
	 * @param path Absolute path of the song on the local file system
	 * @return Returns the ID of the song if found, else this method will return -1.
	 */
	public int selectSongByFilepath(String path) {
		int resultID=-1;
		try {
			selectSongByFilepath.setString(1, path);
			ResultSet rs = selectSongByFilepath.executeQuery();
			while(rs.next()) 
				{resultID =rs.getInt(1);}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultID;
	}
	
	/**
	 * Selects all Artists and returns them as a list of JSON package Artist objects
	 * @return List of JSON package Artist objects
	 */
	public List<Artist> selectAllArtists_ReturnJSONObjects () {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectAllArtists.executeQuery();
			
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
	 * Select all albums given an artist ID
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectAlbumsByArtistID.executeQuery();
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
	 * Select all songs given an Album ID
	 * @param id Album ID
	 * @return List of JSON package Song objects.
	 */
	public List<Song> selectSongsByAlbumID_ReturnJSONObjects (String id) {
		List<Song> songs = new ArrayList<Song>();
		try {
			selectSongsByAlbumID.setString(1, id);
			ResultSet rs = selectSongsByAlbumID.executeQuery();
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
	 * Returns the filepath of a song for a given song ID
	 * @param id ID of the song
	 * @return Filepath of the song on the server
	 */
	public String selectSongFilepathByID (String id) {
		String filepath = "";
		try {
			selectSongFilepathByID.setString(1, id);
			ResultSet rs = selectSongFilepathByID.executeQuery();
			rs.next();
			filepath = rs.getString("filepath");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return filepath;
	}
	
	/**
	 * Select All Albums and return them as a list of AlbumSort_Album objects
	 * @return ArrayList containing AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectAllAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectAllAlbums.executeQuery();
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
	 * Select All songs and return them as a List of SongSort_Song objects
	 * @return Arraylist containing SongSort_Song objects
	 */
	public List<SongSort_Song> selectAllSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectAllSongs.executeQuery();
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
	 * Select all Compilation artists and return them as a list of Artist objects
	 * @return Arraylist containing Artist objects
	 */
	public List<Artist> selectCompArtists_ReturnJSONObjects() {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectCompArtists.executeQuery();
			
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
	 * Select all albums given an artist ID that are compilations
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectCompAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectCompAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectCompAlbumsByArtistID.executeQuery();
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
	 * Select All compilation albums and return them as a list of AlbumSort_Album objects
	 * @return Arraylist containing AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectCompAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectCompAlbums.executeQuery();
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
	 * Select all Songs that belong to a compilation album and return them as a list of SongSort_Song objects
	 * @return Arraylist containing SongSort_Song objects
	 */
	public List<SongSort_Song> selectCompSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectCompSongs.executeQuery();
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
	 * Select all artists from identified compilation albums and return them as a list of Artist objects
	 * @return Arraylist containing Artist objects
	 */
	public List<Artist> selectIdentifiedCompArtists_ReturnJSONObjects() {
		List<Artist> artists = new ArrayList<Artist>();
		try {
			ResultSet rs = selectIdentifiedCompArtists.executeQuery();
			
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
	 * Select all identified albums given an artist ID that are compilations
	 * @param id Artist ID (foreign key)
	 * @return List of JSON package Album objects.
	 */
	public List<Album> selectIdentifiedCompAlbumsByArtistID_ReturnJSONObjects (String id) {
		List<Album> albums = new ArrayList<Album>();
		try {
			selectIdentifiedCompAlbumsByArtistID.setString(1, id);
			ResultSet rs = selectIdentifiedCompAlbumsByArtistID.executeQuery();
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
	 * Select all identified compilation albums and return them as a list of AlbumSort_Album objects
	 * @return List of AlbumSort_Album objects
	 */
	public List<AlbumSort_Album> selectIdentifiedCompAlbums_ReturnJSONObjects() {
		List<AlbumSort_Album> albums = new ArrayList<AlbumSort_Album>();
		try {
			ResultSet rs = selectIdentifiedCompAlbums.executeQuery();
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
	 * Select all identified songs coming from a compilation album and return them as a list of SongSort_Song objects
	 * @return Arraylist containing SongSort_Song objects
	 */
	public List<SongSort_Song> selectIdentifiedCompSongs_ReturnJSONObjects() {
		List<SongSort_Song> songs = new ArrayList<SongSort_Song>();
		try {
			ResultSet rs = selectIdentifiedCompSongs.executeQuery();
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
	 * Selects all the IDs of the songs in a configured folder
	 * @param folder Absolute path of the configured folder on the file system
	 * @return Arraylist of song IDs found in the folder
	 */
	public ArrayList<Integer> selectAllSongIDsFromFolder (String folder) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		try {
			selectAllSongsIDFromFolder.setString(1,"%"+folder+"%");
			ResultSet rs = selectAllSongsIDFromFolder.executeQuery();
			while(rs.next()) {
				results.add(rs.getInt("ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Get all the empty albums in the database
	 * @return Arraylist containing IDs for all the empty albums in the DB
	 */
	public ArrayList<Integer> selectEmptyAlbums () {
		ArrayList<Integer> results = new ArrayList<Integer>();
		try {
			ResultSet rs = selectEmptyAlbums.executeQuery();
			while(rs.next()) {
				results.add(rs.getInt("ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Get all the empty artists in the database
	 * @return Arraylist containing IDs of all the empty artists in the DB
	 */
	public ArrayList<Integer> selectEmptyArtists () {
		ArrayList<Integer> results = new ArrayList<Integer>();
		try {
			ResultSet rs = selectEmptyArtists.executeQuery();
			while(rs.next()) {
				results.add(rs.getInt("ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Get a list of all the song filepaths in the database
	 * @return Arraylist containing all song filepaths in the database as String
	 */
	public ArrayList<Song> selectAllSongFilepaths() {
		ArrayList<Song> songs = new ArrayList<Song>();
		try {
			ResultSet rs = selectAllSongFilepaths.executeQuery();
			while(rs.next()) {
				Song s = new Song();
				s.setId(rs.getString("ID"));
				s.setRequestPath(rs.getString("filepath"));
				songs.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	/**
	 * This method will delete the song with the given ID in the database
	 * @param ID ID of the song that should be deleted
	 * @return Amount of songs that have been deleted
	 */
	public int deleteSongByID (int ID) {
		try {
			deleteSongByID.setInt(1, ID);
			return deleteSongByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * This method will delete the album with the given ID in the database
	 * @param ID ID of the album that should be deleted
	 * @return Amount of albums that have been deleted
	 */
	public int deleteAlbumByID (int ID) {
		try {
			deleteAlbumByID.setInt(1, ID);
			return deleteAlbumByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * This method will delete the artist with the given ID in the database
	 * @param ID ID of the artist that should be deleted
	 * @return Amount of artists that have been deleted
	 */
	public int deleteArtistByID (int ID) {
		try {
			deleteArtistByID.setInt(1, ID);
			return deleteArtistByID.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * This method will delete all artist in the tblArtists table in the database
	 * @return amount of deleted artists
	 */
	public int deleteAllArtists() {
		try {
			return deleteAllArtists.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * This method will delete all albums in the tblAlbums table in the database
	 * @return amount of deleted albums
	 */
	public int deleteAllAlbums() {
		try {
			return deleteAllAlbums.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * This method will delete all songs in the tblSongs table in the databse
	 * @return amount of deleted songs
	 */
	public int deleteAllSongs() {
		try {
			return deleteAllSongs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
