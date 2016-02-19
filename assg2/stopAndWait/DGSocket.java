/**
 * 
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * @author Dallas
 *
 */
public class DGSocket extends DatagramSocket {
	private int reliability;
	private Logger logger;
	private int count;
	/**
	 * 
	 */
	public DGSocket()throws SocketException {
		// TODO Auto-generated constructor stub
		super(5555);
		this.reliability = 0;
		this.logger = new Logger();
		this.count = 0;
	}
	
	public DGSocket(int port, Logger logger)throws SocketException{
		super(port);
		this.reliability = 0;
		this.logger = logger;
		this.count = 0;
	}
	
	public DGSocket(int port, int reliability, Logger logger) throws SocketException{
		super(port);
		this.reliability = reliability;
		this.logger = logger;
		this.count = 0;
	}
	
	
	
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
