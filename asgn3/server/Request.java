/**
 * Java Imports
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.StringTokenizer;
/**
 * The class handles all requests by clients. 
 * It follows the PaintProtocol outlined.
 * It handles three types of requests GET, POST, DELETE
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#PaintArea
 * @see Class#SpecialSocket
 */
public class Request implements Runnable {

	/**
	 * socket: the socket of the client
	 * data: the global data structure used to store all points
	 * logger: the logger for the object
	 * br: how the input is read
	 */
	final static String CRLF = "\r\n";
	private SpecialSocket socket;
	private PaintArea data;
	private Logger logger;
	private BufferedReader br;

	/**
	 * the constructor with no logger
	 * @param socket the socket of the request (SpecialSocket)
	 * @param syn the paint area to operate on (PaintArea)
	 * @throws SocketException thrown is unable to set timeout
	 */
	public Request(SpecialSocket socket, PaintArea syn) throws SocketException {
		this.socket = socket;
		// set the socket to timeout
		// this.socket.getSocket().setSoTimeout(30000); // timeouts do not make sense
		this.data = syn;
		this.logger = new Logger();
	}

	/**
	 * the constructor with no logger
	 * @param socket the socket of the request (Socket)
	 * @param pa the paint area to operate on (PaintArea)
	 * @param logger the logger for the class (Logger)
	 * @throws SocketException thrown is unable to set timeout
	 */
	public Request(SpecialSocket socket, PaintArea pa, Logger logger) throws SocketException {
		this.socket = socket;
		// this.socket.getSocket().setSoTimeout(30000); // timeouts do not make sense
		this.data = pa;
		this.logger = logger;
	}
	/**
	 * handle the InterrupedException
	 * @param InterruptedException e
	 */
	public void handleInterruptedException(InterruptedException e){
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		lines.add("TODO");
	}
	/**
	 * handles the IOException
	 * @param IOException e
	 */
	public void handleIOException(IOException e){
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		lines.add("TODO");
	}
	/**
	 * handle the number format exception
	 * @param NumberFormatException e
	 * @throws IOException 
	 */
	public void handleNumberFormatException(NumberFormatException e) throws IOException{
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		lines.add("PaintProtocol/1.0 400 Bad Request" + CRLF);
		lines.add("Content-Type: text/html" + CRLF);
		lines.add(CRLF);
		lines.add("A point had a number format issue" + CRLF);
		this.socket.writeLines(lines);
		this.logger.info("The thread has handled the exception");
	}
	/**
	 * handles the PointException
	 * @param PointException e
	 * @throws IOException
	 */
	public void handlePointException(PointException e) throws IOException{
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		this.logger.debug("Point was not valid");
		lines.add("PaintProtocol/1.0 400 Bad Request" + CRLF);
		lines.add("Content-Type: text/html" + CRLF);
		lines.add(CRLF);
		lines.add("A point did not have the proper format" + CRLF);
		this.socket.writeLines(lines);
		this.logger.info("The thread has handled the exception");
	}
	/**
	 * handle the IllegalArgumentException
	 * @param IllegalArgumentException e
	 * @throws IOException
	 */
	public void handleIllegalArgumentException(IllegalArgumentException e) throws IOException{
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		this.logger.debug("Color was not valid");
		lines.add("PaintProtocol/1.0 400 Bad Request" + CRLF);
		lines.add("Content-Type: text/html" + CRLF);
		lines.add(CRLF);
		lines.add("The Color was not valid" + CRLF);
		this.socket.writeLines(lines);
		this.logger.info("The thread has handled the exception");		
	}
	/**
	 * handles InvalidParameterException
	 * @param InvalidParameterException e
	 * @throws IOException
	 */
	public void handleInvalidParametersException(InvalidParametersException e) throws IOException{
		LinkedList <String> lines = new LinkedList <String>();
		e.printStackTrace();
		this.logger.debug("Point was not valid");
		lines.add("PaintProtocol/1.0 400 Bad Request" + CRLF);
		lines.add("Content-Type: text/html" + CRLF);
		lines.add(CRLF);
		lines.add("A point was missing the proper parameters" + CRLF);
		this.socket.writeLines(lines);
		this.logger.info("The thread has handled the exception");
	}
	/**
	 * the implement method for the thread
	 */
	public void run(){
		try {
		    this.processRequest();
		} catch(SocketException e) {
			this.logger.debug("The socket was closed on the other end");
		} catch (Exception e) {
			this.logger.error("Error from process");
		    this.logger.error(e.getMessage());
		    e.printStackTrace(System.out);
		}
	}
	/**
	 * handle a get request
	 * @throws IOException
	 */
	public void getRequest() throws IOException{
		try {
			LinkedList <Point> lp = this.data.getPoints();
			LinkedList <String> lines = new LinkedList <String>();
			lines.add("PaintProtocol/1.0 200 Successful" + CRLF);
			lines.add("Content-Type: text/html" + CRLF);
			lines.add(CRLF);
			for (Point pt: lp){
				lines.add(pt.format()  + CRLF); // write the all the points
			}
			lines.add(CRLF); // write end of line
			this.socket.writeLines(lines);
			
		} catch (InterruptedException e) {
			this.handleInterruptedException(e);
		}
	}
	/**
	 * handles a post request
	 * @param br2: how the method reads the input for the request
	 * @throws IOException
	 */
	public void postRequest(BufferedReader br2) throws IOException{
		LinkedList <String> lines = new LinkedList <String>();
		try{
			boolean done = false;
			String requestLine = null;
			StringTokenizer tokens = null;
			LinkedList<Point> points = new LinkedList<Point>();
			while (!done){
				requestLine = br2.readLine(); // read in each point
				tokens = new StringTokenizer(requestLine);
				if (tokens.countTokens() < 2){ // if less than assume done or messed up line
					if (tokens.countTokens() == 1 && tokens.nextToken().equals("END")){
						this.logger.debug("Done processing post of POST request");
						done = true;
					}else if (tokens.countTokens() == 0){
						this.logger.debug("Empty line to assuming end of request");
						done = true;
					}else{
						throw new InvalidParametersException("Missing argument");
					}
				}else{
					points.add(new Point(requestLine)); // add the points to a list
					
				}
			}
			lines.add("PaintProtocol/1.0 201 Successful" + CRLF);
			lines.add("Content-Type: text/html" + CRLF);
			lines.add(CRLF);
			for (Point point :points){
				lines.add(point.format() + CRLF); // format the response
			}
			lines.add(CRLF);
			this.data.addPoints(points); // add the points
		} catch (NumberFormatException e) {
			this.handleNumberFormatException(e);
		} catch (InterruptedException e) {
			this.handleInterruptedException(e);
		} catch (PointException e) {
			this.handlePointException(e);
		} catch (InvalidParametersException e) {
			this.handleInvalidParametersException(e);
		} catch (IllegalArgumentException e){
			this.handleIllegalArgumentException(e);
		}
	}
	/**
	 * handles a delete request
	 * @param br2: how the method reads the input for the request
	 * @throws IOException
	 */
	public void deleteRequest(BufferedReader br2) throws IOException{
		try{
			boolean done = false;
			String requestLine = null;
			StringTokenizer tokens = null;
			LinkedList<Point> points = new LinkedList<Point>();
			while (!done){
				requestLine = br2.readLine(); // read in each point
				tokens = new StringTokenizer(requestLine);
				if (tokens.countTokens() < 2){ // if less than assume done or messed up line
					if (tokens.countTokens() == 1 && tokens.nextToken().equals("END")){
						done = true;
						this.logger.debug("Finished deleteing points");
					}else if(tokens.countTokens() == 0){
						this.logger.debug("Empty was given so assuming done request");
						done = true;
					}else{
						throw new InvalidParametersException("Missing argument");
					}
				}else{
					points.add(new Point(requestLine));
				}
			}
			this.data.removePoints(points); // remove all the points
		}catch (NumberFormatException e) {
			this.handleNumberFormatException(e);
		} catch (InterruptedException e) {
			this.handleInterruptedException(e);
		} catch (PointException e) {
			this.handlePointException(e);
		} catch (InvalidParametersException e) {
			this.handleInvalidParametersException(e);
		} catch (IllegalArgumentException e){
			this.handleIllegalArgumentException(e);
		}
	}
	/**
	 * process requests
	 * @throws IOException
	 */
	public void processRequest()throws IOException{
		// Get a reference to the socket's input and output streams
		java.io.InputStream is = this.socket.getSocket().getInputStream();
		// Set up input stream filters.
		this.br = new BufferedReader(new InputStreamReader(is));
		// Get the request line of the request message.
		boolean done = false;
		String requestLine = null;
		StringTokenizer tokens = null;
		String method = null;
		try{
			while (!done){
			    requestLine = br.readLine();
				this.logger.info("Request Line:" + requestLine);
				// Extract the method name from the request line.
				tokens = new StringTokenizer(requestLine);
				method = tokens.nextToken();
				// Debug info for private use
				this.logger.debug("Incoming!!!");
				this.logger.debug(requestLine);
				this.logger.debug("Method: " + method );
				if (method.toUpperCase().compareTo("GET") == 0){
					this.getRequest();
				}else if (method.toUpperCase().compareTo("POST") == 0){
					this.postRequest(br);
				}else if(method.toUpperCase().compareTo("DELETE") == 0){
					this.deleteRequest(br);
				}else if (method.toUpperCase().compareTo("QUIT") == 0 ||
						requestLine.length() == 0){
				    done = true;
				    this.logger.debug("Closing connection");
				}else{
					//  invalid request
					LinkedList <String> lines = new LinkedList <String>();
					lines.add("PaintProtocol/1.0 405 Method Not Allowed" + CRLF);
					lines.add("Content-Type: text/html" + CRLF);
					lines.add(CRLF);
					lines.add("GET SET REMOVE" + CRLF);
					this.socket.writeLines(lines);
				}
			}
		}finally{
			
		}
	}
	/**
	 * The exception class for invalid parameters
	 * @author Dallas Fraser - 110242560
	 * @author George Lifchits - 100691350
	 * @version 1.0
	 * @extends Exception
	 */
    public class InvalidParametersException extends Exception{
		/**
		 *
		 */
		private static final long serialVersionUID = -6310136941733882114L;
		public InvalidParametersException(String message){
			super(message);
		}
    }
}
