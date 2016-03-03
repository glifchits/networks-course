/**
 * Imports
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A package widow. It helps dealing with all the packages.
 * It used an underlying linked list of packages and their sequence numbers
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DGSocket
 * @see Class#Logger
 */
public class PackageWindow {
	/**
	* {@link front}: the front or first package of the window
	* @see Class#Node
	* {@link logger}: the logger for the class 
	* @see Class#Logger
	* {@link nodes}: the count of the number of nodes
	* {@link windowSize}: the maximum window size
	 */
	private Node front;
	private int nodes;
	private Logger logger;
	private int windowSize;
	/**
	 * the default constructor
	 * @param windowSize: the size of the window (# of packets)
	 */
	public PackageWindow(int windowSize) {
		// TODO Auto-generated constructor stub
		this.front = null;
		this.nodes = 0;
		this.logger = new Logger(2);
		this.windowSize = windowSize;
	}

	/**
	 * constructor where one can specify the logger to use
	 * @param windowSize: the size of the window (# of packets)
	 * @param logger: the logger to use
	 */
	public PackageWindow(int windowSize, Logger logger){
		this.front = null;
		this.nodes = 0;
		this.logger = logger;
		this.windowSize = windowSize;
	}

	/**
	 * A method to check if the window is full or not
	 * @return full: true if window is full, false otherwise
	 */
	public boolean windowFull(){
		boolean full = false;
		if (this.nodes == this.windowSize){
			full = true;
		}
		return full;
	}

	/**
	 * add a package to the window
	 * @param sequence: the sequence number of the package
	 * @param dp: the datagram packet
	 * @throws Exception 
	 */
	public void appendPackage(int sequence, DatagramPacket dp)
				throws Exception{
		Node previous = null;
		Node current = this.front;
		if (this.nodes + 1 > this.windowSize){
			throw new Exception("Window size was exceeded");
		}
		while (current !=  null){
			previous = current;
			current = current.next;
		}
		if (previous == null){
			this.front = new Node(sequence, dp);
		}else{
			previous.next = new Node(sequence, dp);
		}
		this.nodes += 1;
	}

	/**
	 * transmit all the packages in the window
	 * @param socket: the socket use to send the packages over
	 * @throws IOException
	 */
	public void transmitWindow(DGSocket socket) throws IOException{
		Node current = this.front;
		while (current != null){
			this.logger.debug("Sending package: " + current.sequence);
			socket.send(current.dp);
			this.logger.debug(current.dp.getData());
			current = current.next;
		}
	}

	/**
	 * move the package window forward
	 * @param acknowledged: the most recent acknowledged packet
	 */
	public boolean movePackageWindow(int acknowledged){
		boolean moved = false;
		Node current = this.front;
		int position = -1;
		int count = 0;
		while (current != null && position != acknowledged){
			position = current.sequence;
			current = current.next;
			count += 1;
			
		}
		if(position == acknowledged){
			// no change otherwise
			this.front = current;
			moved = true;
			this.nodes -=  count;
		}
		return moved;
	}

	/**
	 * tells whether all of the packets in the window have be sent and acknowledged
	 * @return done: true all done, false otherwise
	 */
	public boolean doneYet(){
		boolean done = false;
		if (this.front == null){
			done = true;
		}
		return done;
	}
	/**
	 * the node use for the linked list
	 * @author Dallas Fraser - 110242560
	 * @author George Lifchits - 100691350
	 * @version 1.0
	 *
	 */
	private class Node{
		public int sequence;
		public DatagramPacket dp;
		public Node next; 
		Node(int sequence, DatagramPacket dp){
			this.sequence = sequence;
			this.dp = dp;
		}
	}

	/**
	 * use for testing purposes
	 * @param args: logging level
	 * @throws SocketException
	 * @throws UnknownHostException
	 * @throws WindowSizeExceededException
	 */
	public static void main(String[] args) throws SocketException, UnknownHostException, Exception {
		int loggingLevel = 2;
		if (args.length > 0){
			loggingLevel = 0;
		}
		Logger logger = new Logger(loggingLevel);
		DGSocket dg = new DGSocket(5555, logger);
		PackageWindow pw = new PackageWindow(2, logger);
		// test full
		boolean full = pw.windowFull();
		boolean done = pw.doneYet();
		if (full == true){
			logger.error("Window full on init");
		}
		if (done != true){
			logger.error("Window should be empty so should be done");
		}
		// append some packages
		InetAddress ia = InetAddress.getLocalHost();
		byte[] out_data = new byte[1];
		DatagramPacket dp = new DatagramPacket(out_data,
												out_data.length,
												ia,
												4444);
		try{
			int i = 0;
			while(! pw.windowFull()){
				pw.appendPackage(i, dp);
				i++;
			}
		}catch (Exception e){
			logger.error("window full failed");
		}
		// fake a transmission
		try{
			pw.transmitWindow(dg);
		}catch (IOException e){
			logger.error("IO exception by transmit window");
		}
		// update window position
		boolean moved = pw.movePackageWindow(1);
		if(moved != true){
			logger.error("Window was not moved");
		}
		moved = pw.movePackageWindow(3);
		if(moved == true){
			logger.error("Window was moved when it should not have been");
		}
		
		full = pw.windowFull();
		done = pw.doneYet();
		if (full == true){
			logger.error("Window  should not be full");
		}
		if (done != true){
			logger.error("All packages were transmitted");
		}		
	}
}
