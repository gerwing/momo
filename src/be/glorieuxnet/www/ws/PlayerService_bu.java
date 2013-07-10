package be.glorieuxnet.www.ws;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.glorieuxnet.www.dao.CollectionDAO;

/**
 * Servlet implementation class PlayerService
 * This is a backup class that streams audio without Partial content support
 * This class is NOT USED in the software
 */
public class PlayerService_bu extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CollectionDAO collection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlayerService_bu() {
        super();
        collection = new CollectionDAO();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = (request.getParameter("action")==null) ? "":request.getParameter("action");
		if(action.equals("play")) {
			String id = request.getParameter("id");
			String filepath = collection.selectSongFilepathByID(id);
			//Stream File
			ServletOutputStream stream = null;
		    BufferedInputStream buf = null;
		    try {
		      stream = response.getOutputStream();
		      File file = new File(filepath);
		      //set response headers
		      response.setContentType(getMimeType(file));
		      response.addHeader("Content-Disposition", "attachment; filename="
		          + file.getName());
		      response.setContentLength((int) file.length());
		      
		      FileInputStream input = new FileInputStream(file);
		      buf = new BufferedInputStream(input);
		      int readBytes = 0;
		      //read from the file; write to the ServletOutputStream
		      while ((readBytes = buf.read()) != -1)
		        stream.write(readBytes);
		    } catch (IOException ioe) {
		      throw new ServletException(ioe.getMessage());
		    } finally {
		      if (stream != null)
		    	  stream.close();
		      if (buf != null)
		        buf.close();
		    }
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	/**
	 * Return the MIME type for a given file f
	 * @param f Audio file
	 * @return MIME type
	 */
	private String getMimeType(File f) {
		String mimetype = "";
		if(f.getName().indexOf("mp3")>-1) mimetype = "audio/mp3";
		else if (f.getName().indexOf("mp4")>-1) mimetype = "audio/mp4";
		else if (f.getName().indexOf("m4a")>-1) mimetype = "audio/m4a";
		else if (f.getName().indexOf("ogg")>-1) mimetype = "audio/ogg";
		else if (f.getName().indexOf("wav")>-1) mimetype = "audio/wav";
		return mimetype;
	}

}
