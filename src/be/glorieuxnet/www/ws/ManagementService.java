package be.glorieuxnet.www.ws;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jaudiotagger.audio.AudioFile;

import com.google.gson.Gson;
import be.glorieuxnet.www.JSON.AlbumList;
import be.glorieuxnet.www.JSON.ArtistList;
import be.glorieuxnet.www.JSON.EditAlbum;
import be.glorieuxnet.www.JSON.JSONFactory;
import be.glorieuxnet.www.JSON.Song;
import be.glorieuxnet.www.JSON.SongList;
import be.glorieuxnet.www.JSON.TreeFolder;
import be.glorieuxnet.www.dao.CollectionDAO;
import be.glorieuxnet.www.dao.ManagementDAO;
import be.glorieuxnet.www.indexing.Configuration;
import be.glorieuxnet.www.indexing.Indexer;
import be.glorieuxnet.www.indexing.Folder;

/**
 * Servlet implementation class ManagementService
 * This Service accepts GET and POST request that perform functions related to managing the music collection
 */
public class ManagementService extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    private Configuration c;
    private ManagementDAO mgmCollection;
    private CollectionDAO collection;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManagementService() {
        super();
        c = Configuration.getConfiguration();
        mgmCollection = new ManagementDAO();
        collection = new CollectionDAO();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = (request.getParameter("action")==null) ? "":request.getParameter("action");
		String folder = (request.getParameter("folder")==null) ? "":request.getParameter("folder");
		String albumid = (request.getParameter("albumid")==null) ? "":request.getParameter("albumid");
		/*
		 * Configuration Requests
		 */
		//Request for configured folders
		if(action.equals("getConfiguration")) {
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(c));
		}
		//request folders in folder on system
		else if (action.equals("getFolders")&&!folder.equals("")) {
			File f = new File(folder);
			ArrayList<TreeFolder> folders = listFolders(f.listFiles());
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(folders));
		}
		//request root folder
		else if (action.equals("getFolders")) {
			ArrayList<TreeFolder> folders = listFolders(File.listRoots());
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(folders));
		}
		/*
		 * Edit Requests
		 */
		else if(action.equals("getAlbum")) {
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(JSONFactory.getAlbumByID(albumid)));
		}
		/* 
		 * Request JSON files
		 */
		else if(action.equals("identifiedartists")) {
			ArtistList artists = JSONFactory.getIdentifiedArtists();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(artists));
		}
		else if(action.equals("unidentifiedartists")) {
			ArtistList artists = JSONFactory.getUnIdentifiedArtists();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(artists));
		}
		else if(action.equals("nocoverartists")) {
			ArtistList artists = JSONFactory.getNoCoverArtists();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(artists));
		}
		else if(action.equals("identifiedalbums")) {
			AlbumList albums = JSONFactory.getIdentifiedAlbums();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(albums));
		}
		else if(action.equals("unidentifiedalbums")) {
			AlbumList albums = JSONFactory.getUnIdentifiedAlbums();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(albums));
		}
		else if(action.equals("nocoveralbums")) {
			AlbumList albums = JSONFactory.getNoCoverAlbums();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(albums));
		}
		else if(action.equals("identifiedsongs")) {
			SongList songs = JSONFactory.getIdentifiedSongs();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(songs));
		}
		else if(action.equals("unidentifiedsongs")) {
			SongList songs = JSONFactory.getUnIdentifiedSongs();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(songs));
		}
		else if(action.equals("nocoversongs")) {
			SongList songs = JSONFactory.getNoCoverSongs();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(songs));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = (request.getParameter("action")==null) ? "":request.getParameter("action");
		/*
		 * Configuration Updates
		 */
		//add configured folder
		if(action.equals("addFolder")){
			c.addFolder(new Folder(request.getParameter("folder")));
			Configuration.setConfiguration(c);
			response.getWriter().write("ok");
		}
		//remove configured folder
		else if (action.equals("removeFolder")){
			String folder = request.getParameter("folder");
			//Remove from configuration
			c.removeFolder(folder);
			Configuration.setConfiguration(c);
			//Remove songs from database
			ArrayList<Integer> list = collection.selectAllSongIDsFromFolder(folder);
			for(int id:list) {
				collection.deleteSongByID(id);
			}
			//Remove empty albums
			list = collection.selectEmptyAlbums();
			for(int id:list) {
				collection.deleteAlbumByID(id);
			}
			//Remove empty artists
			list = collection.selectEmptyArtists();
			for(int id:list) {
				collection.deleteArtistByID(id);
			}
			JSONFactory.createJSONFromDatabase();
			response.getWriter().write("ok");
		}
		//request to index the files in the configured folders
		else if (action.equals("index")){
			Indexer indexer = new Indexer();
			indexer.indexAll();
			//Remove empty albums after indexing added by error
			ArrayList<Integer> list = collection.selectEmptyAlbums();
			for(int id:list) {
				collection.deleteAlbumByID(id);
			}
			//Remove empty artists
			list = collection.selectEmptyArtists();
			for(int id:list) {
				collection.deleteArtistByID(id);
			} 
			JSONFactory.createJSONFromDatabase();
			response.getWriter().write("ok");
		}
		//reset database and jsons
		else if(action.equals("reset")) {
			collection.deleteAllSongs();
			collection.deleteAllAlbums();
			collection.deleteAllArtists();
			JSONFactory.createJSONFromDatabase();
			response.getWriter().write("ok");
		}
		//update album
		else if (action.equals("editAlbum")){
			EditAlbum album = new EditAlbum(); //new album object
			String json = request.getParameter("data"); //get the json as a string
			album = new Gson().fromJson(json, EditAlbum.class); //parse the json to an EditAlbum object
			int artistID = collection.selectArtistByName(album.getAlbumArtist()); //check wether artists already exists
			if(artistID < 0) { //artist doesnt exist, insert now 
				artistID = collection.insertArtist(album.getAlbumArtist());
			}
			//check wether the album already exists and if it is the same, update the album
			int albumID = collection.selectAlbumByTitleArtistDiscNo(album.getTitle(), album.getAlbumArtist(), album.getDisc_no());
		    if(albumID == -1){
		    	albumID = Integer.parseInt(album.getID());
		    }
		    mgmCollection.updateAlbum(album, artistID);
		    for(Song s:album.getSongs()) {
		    	if(!s.getArtist().isEmpty() && !s.getTitle().isEmpty() && !s.getTracknumber().isEmpty() &&
		    			!album.getTitle().isEmpty() && !album.getNo_oftracks().isEmpty()) {
		    		mgmCollection.updateSong(s, true, albumID);
		    	}
		    	else mgmCollection.updateSong(s, false, albumID);
		    }
		    //Remove empty albums
		    ArrayList<Integer> list = collection.selectEmptyAlbums();
			for(int id:list) {
				collection.deleteAlbumByID(id);
			}
			//Remove empty artists
			list = collection.selectEmptyArtists();
			for(int id:list) {
				collection.deleteArtistByID(id);
			}
			JSONFactory.createJSONFromDatabase();
		    response.getWriter().write("ok");
		}
		//edit album cover
		else if (action.equals("editCover")) {
			String ID = request.getParameter("ID"); //get ID
			String link = request.getParameter("url"); //get image url
			EditAlbum a = mgmCollection.selectAlbumByID_ReturnJSONObject(ID); //get album
			//construct cover path
			AudioFile pathmaker = new AudioFile();
			String webPath = pathmaker.getCoverPathFromVariables(AudioFile.WEBCOVERPATH, a.getAlbumArtist(), a.getDisc_no(), a.getTitle(),".png");
			String filePath = pathmaker.getCoverPathFromVariables(AudioFile.COVERPATH, a.getAlbumArtist(), a.getDisc_no(), a.getTitle(),".png");
			mgmCollection.updateAlbumCoverByID(ID, webPath); //edit filepath in db
			URL url = new URL(link);
			BufferedImage image = ImageIO.read(url);
			try{
				ImageIO.write(image, "png", new File(filePath));
			} catch(Exception e) {
				e.printStackTrace();
			}
			//current file is jpg, delete it
			if(!(webPath).equals(a.getCover_filepath())) {
				try {
					File deleteFile = new File(pathmaker.getCoverPathFromVariables(AudioFile.COVERPATH, a.getAlbumArtist(), a.getDisc_no(), a.getTitle(),".jpg"));
					deleteFile.delete();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			JSONFactory.createJSONFromDatabase();
			response.getWriter().write("ok");
		}
		//edit 'show unidentified' option
		else if(action.equals("editShowUnIdentified")) {
			boolean value = request.getParameter("value").equals("true");
			c.setShowUnIdentified(value);
			Configuration.setConfiguration(c);
			JSONFactory.createJSONFromDatabase();
		}
	}
	
	/**
	 * This method generates a Folder Arraylist containing the folders that were passed in the roots parameter
	 * @param roots Array of files
	 * @return Arraylist<Folder> containing all folders in the roots parameter
	 */
	private ArrayList<TreeFolder> listFolders(File[] roots) {
		ArrayList<TreeFolder> folders = new ArrayList<TreeFolder>();
		if(roots!=null){
			for(File f:roots) {
				if(f.getName().indexOf(".")>1||f.getName().indexOf(".")==-1){
					if(f.isDirectory()) {
						TreeFolder fl = new TreeFolder();
						fl.setKey(f.getAbsolutePath());
						if(f.getAbsolutePath().equals(File.separator)) fl.setTitle(File.separator);
						else fl.setTitle(f.getName());
						folders.add(fl);
					}
				}
			}
		}
		return folders;
	}

}
