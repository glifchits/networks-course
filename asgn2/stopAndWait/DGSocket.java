/**
 * Imports
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * A class that extends DatagramSocket to include reliability number
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DatagramSocket
 */
public class DGSocket extends DatagramSocket {
	private int reliability;
	private Logger logger;
	private int count;
	/**
	 *  the public constructor
	 */
	public DGSocket()throws SocketException {
		// TODO Auto-generated constructor stub
		super(5555);
		this.reliability = 0;
		this.logger = new Logger();
		this.count = 0;
	}
	/**
	* a public constructors where port and logger are specified
	* @param port: the port number
	* @param logger: the logger to use
	*/
	public DGSocket(int port, Logger logger)throws SocketException{
		super(port);
		this.reliability = 0;
		this.logger = logger;
		this.count = 0;
	}
	
	/**
	* a public construcotr where port, reliability, logger are specified
	* @param port: the port number
	* @param reliability: the reliability number
	* @param logger: the logger to use
	*/
	public DGSocket(int port, int reliability, Logger logger) throws SocketException{
		super(port);
		this.reliability = reliability;
		this.logger = logger;
		this.count = 0;
	}
	
	/**
	* receive a datagram packet
	* the pack will be dropped depending on the reliability number
	* @param p: the datagram packet to be used
	*/
	public void receive(DatagramPacket p)throws IOException{
		this.logger.debug("Receiving datagram packet");
		if  ((this.reliability == 0 ) || (this.count % this.reliability) != 0){
			super.receive(p);
			this.logger.debug("Packet was received");
		}else{
			this.logger.debug("Packet was dropped");
		}
		this.count += 1;
		this.logger.debug("Packet count" + this.count);
		return;
	}

}
