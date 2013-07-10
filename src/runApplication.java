import be.glorieuxnet.www.JSON.JSONFactory;
import be.glorieuxnet.www.server.MomoJettyServer;


/**
 * @author Gerwin Glorieux
 * This class includes the Main method that will launch the program
 */
public class runApplication {

	/**
	 * This method will run the application by starting a Jetty server
	 * @param args
	 */
	public static void main(String[] args) {
		JSONFactory.createJSONFromDatabase();
		try {
			new MomoJettyServer(8900); //this is too cool
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
