package a3;

import java.io.IOException;
import java.net.* ;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


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
	private BlockingQueue<LinkedList<String>> updates;
	private Updater updater;
	/**
	 * the default constructor
	 */
	public Server() {
		this.updates = new LinkedBlockingQueue<LinkedList<String>>();
		this.logger = new Logger();
		this.data = new PaintArea(this.updates, this.logger);
		this.updater = new Updater(this.updates, this.logger);
	}

	/**
	 * the constructor with a given logger
	 * @params logger the logger for the class (Logger)
	 */
	public Server(Logger logger){
	    this.logger = logger;
	    this.updates = new LinkedBlockingQueue<LinkedList<String>>();
		this.data = new PaintArea(this.updates, this.logger);
		this.updater = new Updater(this.updates, this.logger);
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
			Thread uthread = new Thread(this.updater);
			uthread.start();
			while (true) {
			    // Listen for a TCP connection request.
			    SpecialSocket connection = new SpecialSocket(socket.accept());
			    // Construct an object to process the HTTP request message.
			    Request request = new Request(connection, this.data, this.logger);
			    this.updater.addClient(connection);
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
