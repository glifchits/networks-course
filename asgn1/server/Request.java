/**
 * 
 */

import java.io.* ;
import java.net.* ;
import java.util.* ;

/**
 * A class object to handle three types of requests
 * <ul>
 * 		<li>
 * 			SET messages containing pairs of words (synonyms to be added to synonyms lists)
 * 		</li>
 * 		<li>
 * 			GET messages containing requests for synonyms (there is one type of request that contains single
 * 			word)
 * 		</li>
 * 		<li>
 * 			REMOVE messages containing requests to remove a word from synonyms list
 * 		<li>
 * </ul>
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - XXXXXXXX
 * @version 1.0
 * @see Class#Server
 */


public class Request implements Runnable {
	
	/**
	 * 
	 */
	final static String CRLF = "\r\n";
	private Socket socket;
	private DataOutputStream os;
	private Synonyms data;
	private Logger logger;
	/**
	 * the only constructor
	 * @param socket the socket of the request (Socket)
	 * @param s the synonym structure for the requests to operate on (Synonyms)
	 */
	public Request(Socket socket, Synonyms syn) {
		this.socket = socket;
		this.os = null;
		this.data = syn;
		this.logger = new Logger();
	}

	public Request(Socket socket, Synonyms syn, Logger logger) {
		this.socket = socket;
		this.os = null;
		this.data = syn;
		this.logger = logger;
	}

    // Implement the run() method of the Runnable interface.
	/**
	 * a Runnable interface
	 */
    public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
    }

    /**
     * a method to handle SET requests
     * @param tokenizer the tokens of the request (StringTokenizer)
     * @throws Exception
     */
    private void setRequest(StringTokenizer tokenizer) throws Exception {
    	String word = null;
    	String match = null;
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	try{
    		word = tokenizer.nextToken();
    		match = tokenizer.nextToken();
    		if (tokenizer.hasMoreTokens()){
    			throw new InvalidParametersException("Too many Parameters");
    		}
    		this.data.addPair(word, match);
    		statusLine = "HTTP/1.0 201 Created" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "";		
    	}catch (NoSuchElementException e){
			//  invalid request
			statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Missing parameter";
    	}catch (NullPointerException e){
    		//  invalid request
    		statusLine = "HTTP/1.0 404 Not Found" + CRLF;
    		contentTypeLine = "Content-Type: text/html" + CRLF;
    		entityBody = word + " was not found";
    	}catch(InterruptedException e){
    		statusLine = "HTTP/1.0 408 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "";
    	}catch (InvalidParametersException e){
			statusLine = "HTTP/1.0 400 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Too many parameters";
		}catch (Exception e){
    		//  invalid request
    		statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
    		contentTypeLine = "Content-Type: text/html" + CRLF;
    		entityBody = "Unknown error";
    	}
		this.os.writeBytes(statusLine);
		this.os.writeBytes(contentTypeLine);
		this.os.writeBytes(CRLF);
		this.os.writeBytes(entityBody);
		this.os.writeBytes(CRLF);
    }

    /**
     * a method to handle GET requests
     * @param tokenizer the tokens of the request (StringTokenizer)
     * @throws Exception
     */
    private void getRequest(StringTokenizer tokenizer) throws Exception{
    	String word = null;
    	String result = null;
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	try{
    		word = tokenizer.nextToken();
    		if (tokenizer.hasMoreTokens()){
    			throw new InvalidParametersException("Too many Parameters");
    		}
    		result = this.data.getPair(word);
    		statusLine = "HTTP/1.0 200 Successful" + CRLF;
    		contentTypeLine = "Content-Type: text/html" ;
    		entityBody = result;
    	}catch (NoSuchElementException e){
			//  invalid request
    		statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Missing parameter(s)";
    	}catch (NullPointerException e){
			statusLine = "HTTP/1.0 404 Not found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = word + " was not found";
    	}catch(InterruptedException e){
    		statusLine = "HTTP/1.0 408 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "";
    	}catch (InvalidParametersException e){
			statusLine = "HTTP/1.0 400 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Too many parameters";
		}catch (Exception e){
    		//  unknown error
    		statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
    		contentTypeLine = "Content-Type: text/html" + CRLF;
    		entityBody = "Unknown error";
    	}
		this.os.writeBytes(statusLine);
		this.os.writeBytes(contentTypeLine);
		this.os.writeBytes(CRLF);
		this.os.writeBytes(entityBody);
		this.os.writeBytes(CRLF);
    }

    /**
     * a method to handle REMOVE request
     * @param tokenizer the tokens of the request (StringTokenizer)
     * @throws Exception
     */
    private void removeRequest(StringTokenizer tokenizer) throws Exception{
    	String word = null;
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	try{
    		word = tokenizer.nextToken();
    		if (tokenizer.hasMoreTokens()){
    			throw new InvalidParametersException("Too many Parameters");
    		}
    		this.data.removePair(word);
    		statusLine = "HTTP/1.0 200 Successful" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "";
    	}catch (NoSuchElementException e){
			//  invalid request
			statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Missing parameter";
    	}catch (NullPointerException e){
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = word + " was not found";
		}catch(InterruptedException e){
			statusLine = "HTTP/1.0 408 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "";
		}catch (InvalidParametersException e){
			statusLine = "HTTP/1.0 400 Request Timeout" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "Too many parameters";
		}catch (Exception e){
    		//  unknown error
    		statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
    		contentTypeLine = "Content-Type: text/html" + CRLF;
    		entityBody = "Unknown error";
		}
		this.os.writeBytes(statusLine);
		this.os.writeBytes(contentTypeLine);
		this.os.writeBytes(CRLF);
		this.os.writeBytes(entityBody);
    }

    /**
     * a method to process all requests
     * @throws Exception
     */
    private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
		InputStream is = this.socket.getInputStream();
		this.os = new DataOutputStream(this.socket.getOutputStream());
		// Set up input stream filters.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		// Get the request line of the request message.
		String requestLine = br.readLine();
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		String method = tokens.nextToken();
		// Debug info for private use
		this.logger.debug("Incoming!!!");
		this.logger.debug("Incoming!!!");
		this.logger.debug(requestLine);
		this.logger.debug(br);
		if (method.compareTo("GET") == 0){
			this.getRequest(tokens);
		}else if (method.compareTo("SET") == 0){
			this.setRequest(tokens);
		}else if(method.compareTo("REMOVE") == 0){
			this.removeRequest(tokens);
		}else{
			//  invalid request
			String statusLine = "HTTP/1.0 405 Method Not Allowed" + CRLF;
			String contentTypeLine = "Content-Type: text/html" + CRLF;
			String entityBody = "GET SET REMOVE";
			this.os.writeBytes(statusLine);
			this.os.writeBytes(contentTypeLine);
			this.os.writeBytes(CRLF);
			this.os.writeBytes(entityBody);
		}
		// Close streams and socket.
		this.os.close();
		br.close();
		this.socket.close();
    }
    /**
     * a set method to set output stream. Just used to mock requests
     * @param os - the output stream (DataOutputStream)
     */
    public void setOS(DataOutputStream os){
    	this.os = os;
    }

	/**
	 * The main function. It is used for testing purposes
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*
		 * SET requests
		 */
		Logger lg = new Logger(0);
		Synonyms syn = new Synonyms(lg);
		// mock request
		Request r = new Request(new Socket(), syn, lg);
	    // set to output to console
		DataOutputStream writer = new DataOutputStream(System.out);
		r.setOS(writer);
		StringTokenizer st = new StringTokenizer("Hello");
		try{
			// bad request
			System.out.println("SET: Sending bad request");
			r.setRequest(st);
			System.out.println("-------------------");
			// good request
			System.out.println("SET: Sending Good request");
			st = new StringTokenizer("Hello Hi", " ");
			r.setRequest(st);
			System.out.println("-------------------");
			System.out.println("SET: Sending request with too many parameters");
			st = new StringTokenizer("Hello Hi FUCK", " ");
			r.setRequest(st);
			System.out.println("-------------------");
			
		}catch (Exception e){
			lg.error(e.getMessage());
		}
		/*
		 *  GET Requests
		 */
		try{
			// good request
			System.out.println("GET: Sending Good request");
			st = new StringTokenizer("Hello");
			r.getRequest(st);
			System.out.println("-------------------");
			System.out.println("GET: Sending Bad request");
			st = new StringTokenizer("FUCKER");
			r.getRequest(st);
			System.out.println("-------------------");
			System.out.println("GET: Sending request with too many parameters");
			st = new StringTokenizer("GOAT FUCKER");
			r.getRequest(st);
			System.out.println("-------------------");
		}catch (Exception e){
			lg.error(e.getMessage());
		}
		
		/*
		 * REMOVE requests
		 */
		try{
			// bad request
			System.out.println("REMOVE: Sending Bad request");
			st = new StringTokenizer("FUCKER");
			r.removeRequest(st);
			System.out.println("-------------------");
			System.out.println("REMOVE: Sending Good request");
			st = new StringTokenizer("Hello");
			r.removeRequest(st);
			System.out.println("-------------------");
			System.out.println("REMOVE: sending request with too many parameters");
			st = new StringTokenizer("GOAT FUCKER");
			r.removeRequest(st);
			System.out.println("-------------------");
			// checking if remove worked
			System.out.println("REMOVE: Checking if Hello removed");
			st = new StringTokenizer("Hello");
			r.getRequest(st);
			System.out.println("-------------------");
		}catch(Exception e){
			lg.error(e.getMessage());
		}
	}

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
