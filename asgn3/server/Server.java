package a3;

import java.io.* ;
import java.io.IOException;
import java.net.* ;
import java.util.LinkedList;


/**
 * The Server object used to process HTTP requests.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#PaintArea
 */
public class Server {
	final static String CRLS = "\r\n";
	private PaintArea data;
	private Logger logger;
	private LinkedList<Socket> sockets;

	/**
	 * the default constructor
	 */
	public Server() {
		// TODO Auto-generated constructor stub
		this.data = new PaintArea();
		this.logger = new Logger();
		this.sockets = new LinkedList<Socket>();
	}

	/**
	 * the constructor with a given logger
	 * @params logger the logger for the class (Logger)
	 */
	public Server(Logger logger){
	    this.logger = logger;
		this.data = new PaintArea(this.logger);
		this.sockets = new LinkedList<Socket>();
	}

	/**
	 * 
	 * @param port the port number to run on
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
			    this.sockets.add(connection);
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
