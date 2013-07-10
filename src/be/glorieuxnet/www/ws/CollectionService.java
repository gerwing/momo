package be.glorieuxnet.www.ws;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.glorieuxnet.www.JSON.AlbumList;
import be.glorieuxnet.www.JSON.ArtistList;
import be.glorieuxnet.www.JSON.JSONFactory;
import be.glorieuxnet.www.JSON.SongList;

import com.google.gson.Gson;

/**
 * Servlet implementation class CollectionService
 * Use this Service to retrieve the collections for a Music Player application
 */
public class CollectionService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CollectionService() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = (request.getParameter("action")==null) ? "":request.getParameter("action");
		if(action.equals("allartists")) {
			ArtistList artists = JSONFactory.getAllArtists();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(artists));
		}
		else if (action.equals("allalbums")) {
			AlbumList albums = JSONFactory.getAllAlbums();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(albums));
		}
		else if (action.equals("allsongs")) {
			SongList songs = JSONFactory.getAllSongs();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(songs));
		}
		else if(action.equals("compartists")) {
			ArtistList artists = JSONFactory.getCompArtists();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(artists));
		}
		else if(action.equals("compalbums")) {
			AlbumList albums = JSONFactory.getCompAlbums();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(albums));
		}
		else if(action.equals("compsongs")) {
			SongList songs = JSONFactory.getCompSongs();
			response.setContentType("application/json");
			response.getWriter().write(new Gson().toJson(songs));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
