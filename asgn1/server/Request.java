/**
 * 
 */
package server;
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
    	String result = null;
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	try{
    		word = tokenizer.nextToken();
    		match = tokenizer.nextToken();
    		result = this.data.addPair(word, match);
    		statusLine = "HTTP/1.0 200 Successful";
			contentTypeLine = "Content-Type: text/html";
			entityBody = result;		
    	}
    	catch (NoSuchElementException e){
			//  invalid request
			statusLine = "HTTP/1.0 400 Bad Request";
			contentTypeLine = "Content-Type: text/html";
			entityBody = "Missing parameter";
    	}
		this.os.writeBytes(statusLine);
		this.os.writeBytes(contentTypeLine);
		this.os.writeBytes(CRLF);
		this.os.writeBytes(entityBody);
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
    		result = this.data.getPair(word);
    		if (result == null){
    			statusLine = "HTTP/1.0 404 Not found";
    			contentTypeLine = "Content-Type: text/html";
    			entityBody = word + " -  was not found";
    		}else{
    			statusLine = "HTTP/1.0 200 Successful";
    			contentTypeLine = "Content-Type: text/html";
    			entityBody = result;
    		}		
    	}
    	catch (NoSuchElementException e){
			//  invalid request
    		statusLine = "HTTP/1.0 400 Bad Request";
			contentTypeLine = "Content-Type: text/html";
			entityBody = "Missing parameter(s)";
    	}
		this.os.writeBytes(statusLine);
		this.os.writeBytes(contentTypeLine);
		this.os.writeBytes(CRLF);
		this.os.writeBytes(entityBody);
    }

    /**
     * a method to handle REMOVE request
     * @param tokenizer the tokens of the request (StringTokenizer)
     * @throws Exception
     */
    private void removeRequest(StringTokenizer tokenizer) throws Exception{
    	String word = null;
    	String result = null;
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	try{
    		word = tokenizer.nextToken();
    		result = this.data.removePair(word);
    		statusLine = "HTTP/1.0 200 Successful";
			contentTypeLine = "Content-Type: text/html";
			entityBody = result;		
    	}
    	catch (NoSuchElementException e){
			//  invalid request
			statusLine = "HTTP/1.0 400 Bad Request";
			contentTypeLine = "Content-Type: text/html";
			entityBody = "Missing parameter";
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
			String statusLine = "HTTP/1.0 405 Method Not Allowed ";
			String contentTypeLine = "Content-Type: text/html";
			String entityBody = "Method requested is not allowed";
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
}
