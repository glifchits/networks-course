package a3;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.omg.CORBA.portable.InputStream;

public class Request implements Runnable {

	/**
	 *
	 */
	final static String CRLF = "\r\n";
	private Socket socket;
	private DataOutputStream os;
	private PaintArea data;
	private Logger logger;
	private BufferedReader br;

	/**
	 * the constructor with no logger
	 * @param socket the socket of the request (Socket)
	 * @param pa the paint area to operate on (PaintArea)
	 */
	public Request(Socket socket, PaintArea syn) throws SocketException {
		this.socket = socket;
		// set the socket to timeout
		this.socket.setSoTimeout(30000);
		this.os = null;
		this.data = syn;
		this.logger = new Logger();
	}

	/**
	 * the constructor with no logger
	 * @param socket the socket of the request (Socket)
	 * @param pa the paint area to operate on (PaintArea)
	 * @param logger the logger for the class (Logger)
	 */
	public Request(Socket socket, PaintArea pa, Logger logger) throws SocketException {
		this.socket = socket;
		this.socket.setSoTimeout(30000);
		this.os = null;
		this.data = pa;
		this.logger = logger;
	}


	public void run(){
		try {
		    this.processRequest();
		} catch(SocketException e) {
			this.logger.debug("The socket was closed on the other end");
		} catch (Exception e) {
			System.out.println("Error from process");
		    this.logger.error(e.getMessage());
		    e.printStackTrace(System.out);
		}
	}

	public void getRequest(){
		try {
			LinkedList <Point> lp = this.data.getPoints();
			this.os.writeBytes("PaintProtocol/1.0 200 Successful"); //  result
			this.os.writeBytes("Content-Type: text/html"); // content type
			this.os.writeBytes(CRLF); // header stuff
			for (Point pt: lp){
				this.os.writeBytes(pt.format()); // write the all the points
			}
			this.os.writeBytes(CRLF); // write end of line
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postRequest(BufferedReader br2){
		try{
			boolean done = false;
			String requestLine = null;
			StringTokenizer tokens = null;
			while (!done){
				requestLine = br2.readLine(); // read in each point
				tokens = new StringTokenizer(requestLine);
				if (tokens.countTokens() < 2){ // if less than assume done or messed up line
					if (tokens.countTokens() == 1 && tokens.nextToken().equals("END")){
						done = true;
					}else{
						throw new InvalidParametersException("Missing argument");
					}
				}else{
					this.data.addPoint(new Point(requestLine));
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PointException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteRequest(BufferedReader br2){
		try{
			boolean done = false;
			String requestLine = null;
			StringTokenizer tokens = null;
			while (!done){
				requestLine = br2.readLine(); // read in each point
				tokens = new StringTokenizer(requestLine);
				if (tokens.countTokens() < 2){ // if less than assume done or messed up line
					if (tokens.countTokens() == 1 && tokens.nextToken().equals("END")){
						done = true;
					}else{
						throw new InvalidParametersException("Missing argument");
					}
				}else{
					this.data.removePoint(new Point(requestLine));
				}
			}
		}catch (IOException e){
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PointException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void processRequest()throws IOException{
		// Get a reference to the socket's input and output streams
		InputStream is = (InputStream) this.socket.getInputStream();
		this.os = new DataOutputStream(this.socket.getOutputStream());
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
					String statusLine = "PaintProtocol/1.0 405 Method Not Allowed" + CRLF;
					String contentTypeLine = "Content-Type: text/html" + CRLF;
					String entityBody = "GET SET REMOVE" + CRLF;
					this.os.writeBytes(statusLine);
					this.os.writeBytes(contentTypeLine);
					this.os.writeBytes(CRLF);
					this.os.writeBytes(entityBody);
				}
			}
		}finally{
			
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
