package server;
import java.io.* ;
import java.net.* ;


/**
 * The Server object used to process HTTP requests.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - XXXXXXXX
 * @version 1.0
 * @see Class#Synonyms
 */
public class Server {
	final static String CRLS = "\r\n";
	private Synonyms data;
	private Logger logger;

	/**
	 * 
	 */
	public Server() {
		// TODO Auto-generated constructor stub
		this.data = new Synonyms();
		this.logger = new Logger();
	}

	public Server(Logger logger){
		this.data = new Synonyms();
		this.logger = logger;
	}
	/**
	 * 
	 * @param args
	 */
	public void run(int port) {
		try{
			// Establish the listen socket.
			@SuppressWarnings("resource")
			ServerSocket socket = new ServerSocket(port);		
			// Process service requests in an infinite loop.
			while (true) {
			    // Listen for a TCP connection request.
			    Socket connection = socket.accept();
			    // Construct an object to process the HTTP request message.
			    Request request = new Request(connection, this.data, this.logger);
			    // Create a new thread to process the request.
			    Thread thread = new Thread(request);
			    // Start the thread.
			    thread.start();
			}
		} catch (IOException e){
			System.out.println("Error Creating Socket");
		}
	}
}
