/**
 * Imports
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A packet window, used as the sender window for the Go-Back-N implementation.
 * It is an linked list of packets and their sequence numbers
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#UnreliableDatagramSocket
 * @see Class#Logger
 */
public class PacketWindow {
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
	 * @param windowSize: the size of the window (# of packets)
	 */
	public PacketWindow(int windowSize) {
		this(windowSize, new Logger(2));
	}

	/**
	 * constructor where one can specify the logger to use
	 * @param windowSize: the size of the window (# of packets)
	 * @param logger: the logger to use
	 */
	public PacketWindow(int windowSize, Logger logger) {
		this.front = null;
		this.nodes = 0;
		this.logger = logger;
		this.windowSize = windowSize;
	}

	/**
	 * @return true if window is full, false otherwise
	 */
	public boolean windowFull() {
		return this.nodes == this.windowSize;
	}

	/**
	 * add a packet to the window
	 * @param sequence: the sequence number of the packet
	 * @param dp: the datagram packet
	 * @throws Exception
	 */
	public void appendPacket(int sequence, DatagramPacket dp) throws Exception {
		Node previous = null;
		Node current = this.front;
		if (this.nodes + 1 > this.windowSize) {
			throw new Exception("Window size was exceeded");
		}
		while (current != null) {
			previous = current;
			current = current.next;
		}
		if (previous == null) {
			this.front = new Node(sequence, dp);
		} else {
			previous.next = new Node(sequence, dp);
		}
		this.logger.debug("A packet was added to the packet window");
		this.nodes += 1;
	}

	/**
	 * transmit all the packets in the window
	 * @param socket: the socket use to send the packets over
	 * @throws IOException
	 */
	public void transmitWindow(UnreliableDatagramSocket socket) throws IOException {
		this.logger.debug("Sending entire packet window");
		Node current = this.front;
		while (current != null) {
			this.logger.debug("Sending packet: " + current.sequence);
			socket.send(current.dp);
			current = current.next;
		}
	}

	/**
	 * move the package window forward
	 * @param acknowledged: the most recent acknowledged packet
	 */
	public boolean movePacketWindow(int acknowledged) {
		boolean moved = false;
		Node current = this.front;
		int position = -1;
		int count = 0;
		while (current != null && position != acknowledged) {
			position = current.sequence;
			current = current.next;
			count += 1;
		}
		if (position == acknowledged) {
			// no change otherwise
			this.front = current;
			moved = true;
			this.nodes -= count;
		}
		this.logger.debug("Packet window was incremented");
		return moved;
	}

	/**
	 * @return true if all packets were sent and acknowledged, false otherwise
	 */
	public boolean doneYet() {
		return (this.front == null);
	}
	/**
	 * the node use for the linked list
	 * @author Dallas Fraser - 110242560
	 * @author George Lifchits - 100691350
	 * @version 1.0
	 *
	 */
	private class Node {
		public int sequence;
		public DatagramPacket dp;
		public Node next;
		Node(int sequence, DatagramPacket dp) {
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
		UnreliableDatagramSocket dg = new UnreliableDatagramSocket(5555, logger);
		PacketWindow pw = new PacketWindow(2, logger);
		// test full
		boolean full = pw.windowFull();
		boolean done = pw.doneYet();
		if (full == true){
			logger.error("Window full on init");
		}
		if (done != true){
			logger.error("Window should be empty so should be done");
		}
		// append some packets
		InetAddress ia = InetAddress.getLocalHost();
		byte[] out_data = new byte[1];
		DatagramPacket dp = new DatagramPacket(out_data,
												out_data.length,
												ia,
												4444);
		try{
			int i = 0;
			while(! pw.windowFull()){
				pw.appendPacket(i, dp);
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
		boolean moved = pw.movePacketWindow(1);
		if(moved != true){
			logger.error("Window was not moved");
		}
		moved = pw.movePacketWindow(3);
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
