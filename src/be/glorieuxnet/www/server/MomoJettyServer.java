package be.glorieuxnet.www.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import be.glorieuxnet.www.ws.CollectionService;
import be.glorieuxnet.www.ws.ManagementService;
import be.glorieuxnet.www.ws.PlayerService;

/**
 * @author Gerwin Glorieux
 * This class will start a new Jetty Server on Port 8900 with the CollectionService, ManagementService and 
 * PlayerService Servlet running in it
 */
public class MomoJettyServer {
	/**
	 * Construct a new Server
	 * @param port Port the Server should be accessible on
	 * @throws Exception
	 */
	public MomoJettyServer (int port) throws Exception {
		init(port);
	}
	
	private void init(int port) throws Exception {
		//create server
		Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);
 
        //add HTTP handler
        ResourceHandler http_handler = new ResourceHandler();
        http_handler.setDirectoriesListed(true);
        http_handler.setWelcomeFiles(new String[]{ "index.html" });
        http_handler.setResourceBase("web");
        
        //add Servlet handler
        ServletContextHandler servlet_handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servlet_handler.setContextPath("/");
 
        servlet_handler.addServlet(new ServletHolder(new CollectionService()),"/CollectionService/*");
        servlet_handler.addServlet(new ServletHolder(new ManagementService()),"/ManagementService/*");
        servlet_handler.addServlet(new ServletHolder(new PlayerService()),"/PlayerService/*");
 
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { http_handler, servlet_handler, new DefaultHandler() });
        server.setHandler(handlers);
 
        server.start();
        server.join();
	}
}
