/**
 * Java Imports
 */
import java.awt.Color;
import java.util.Random;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
/**
 * The SpecialSocket that ensures no two threads are writing at the same time
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#Socket
 */
public class SpecialSocket{
	/**
	 * socket: the underlying socket
	 */
	private Socket socket;
	/*
	 * random colour assigned to this client
	 */
	private Color color;
	final static int MAX  = 255;
	final static int BASE = 50; // lowest value (RGB)
	final static int TOP  = 230; // highest value (RGB)
	/**
	 * public constructor
	 * @param socket: the socket to use
	 */
	public SpecialSocket(Socket socket) {
		this.socket = socket;
		Random rand = new Random();
		this.color = new Color(
			BASE+rand.nextInt(MAX-BASE-(TOP-MAX)),
			BASE+rand.nextInt(MAX-BASE-(TOP-MAX)),
			BASE+rand.nextInt(MAX-BASE-(TOP-MAX))
		);
	}
	/**
	 * getter for socket
	 */
	public Socket getSocket(){
		return this.socket;
	}

	public Color getColor() {
		return this.color;
	}
	/**
	 * used to write a request
	 * @param lines: every line of the request
	 * @throws IOException when unable to write a line
	 */
	public synchronized void writeLines(LinkedList<String> lines) throws IOException{
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
		for(String line: lines){
			os.writeBytes(line);
		}
	}

}
