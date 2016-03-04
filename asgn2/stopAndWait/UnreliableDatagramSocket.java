/**
 * Imports
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * A class that extends DatagramSocket to include reliability number
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DatagramSocket
 */
public class UnreliableDatagramSocket extends DatagramSocket {
	private int reliability;
	private Logger logger;
	private Random rand;

	/**
	 * public constructor with no parameters specified
	 */
	public UnreliableDatagramSocket() throws SocketException {
		this(5555, 0, new Logger());
	}

	/**
	* a public constructors where port and logger are specified
	* @param port: the port number
	* @param logger: the logger to use
	*/
	public UnreliableDatagramSocket(int port, Logger logger) throws SocketException {
		this(port, 0, logger);
	}

	/**
	* a public construcotr where port, reliability, logger are specified
	* @param port: the port number
	* @param reliability: the reliability number
	* @param logger: the logger to use
	*/
	public UnreliableDatagramSocket(int port, int reliability, Logger logger) throws SocketException {
		super(port);
		this.reliability = reliability;
		this.logger = logger;
		this.rand = new Random();
	}

	/**
	* receive a datagram packet
	* the pack will be dropped depending on the reliability number
	* @param p: the datagram packet to be used
	*/
	public void receive(DatagramPacket pkt) throws IOException {
		this.logger.debug("Receiving datagram packet");
		if (this.reliability == 0) {
			this.logger.debug("Packet was received");
			super.receive(pkt);
		} else if (rand.nextInt(this.reliability-1) == 0) {
			this.logger.debug("Packet was dropped");
		}
		return;
	}

}
