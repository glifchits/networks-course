/**
 * Imports
 */

package a2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * The go back n sender. Each package has a size of 128 bytes.
 * The first byte is the sequence number, the second byte is the number of bytes being sent
 * the remaining 126 bytes are the data being sent
 * The sender will wait a reasonable amount of time before re-sending the
 * window of packets 
 * It follows the standard GBN from Computer Networking: A Top-Down Approach .
 * All files are sent as bytes.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DGSocket
 * @see Class#PackageWindow
 */
public class GoBackNSender {
	/**
	* 
	* {@link socket}: the UPD socket 
	* @see Class#DGSocket
	* {@link logger}: the logger for the class 
	* @see Class#Logger
	* {@link fp}: the file input stream  for reading files
	* {@link temp_packet}: the datagram packet for responding
	* {@link in_packet}: the datagram packet for receiving
	* {@link ia}: the internet address of the sender
	* {@link sequence}: the sequence number of package (0-127)
	* {@link lastSent}: the time the last transmission was sent (long)
	* {@link pw}: the package window
	* @see Class#PackageWindow
	* {@link doneReading}: the flag of whether done reading from the file
	* {@link packetNumber}: the packet number of last read packet
	*/
	private DGSocket socket;
	private Logger logger;
	private DatagramPacket temp_packet;
	private DatagramPacket in_packet;
	private PackageWindow pw;
	private FileInputStream fp;
	private InetAddress ia;
	private int sequence;
	private long lastSent;
	private static int WAITTIME = 10000; // wait time in milliseconds
	private int packetNumber;
	private boolean doneReading;
	/**
	 * the deagilt constructor
	 * the public constructor
	 * @param hostAddress: a String of the host address
	 * @param senderPort: the port number of the sender
	 * @param receiverPort: the port number of this receiver
	 * @param fileName: the name of the file to output
	 * @param windowSize: the size of the window of packets
	 * @param logger: the logger of the class
	 * 
	 * @throws FileNotFoundException if unable to find file
	 * @throws UnknownHostException if unable to find address for host
	 * @throws SocketException if unable to create UDP socket
	*/
	public GoBackNSender(String hostAddress,
							int senderPort,
							int receiverPort,
							String fileName,
							int windowSize,
							Logger logger) throws UnknownHostException, SocketException, FileNotFoundException {
		this.socket = new DGSocket(senderPort, logger);
		this.logger = logger;
		this.ia = InetAddress.getByName(hostAddress);
		byte[] data = new byte[128];
		byte[] in_data = new byte[1];
		this.temp_packet = new DatagramPacket(data, data.length, this.ia, receiverPort);
		this.in_packet = new DatagramPacket(in_data, in_data.length, this.ia, receiverPort);
		this.socket.setSoTimeout(WAITTIME);
		this.fp = new FileInputStream(new File(fileName));
		this.logger.debug("Created sender");
		this.sequence = 0;
		this.pw = new PackageWindow(windowSize, logger);
		this.packetNumber = 0;
		this.doneReading = false;
	}

	/**
	 * ready the packet to be sent
	 * @return ready: whether the is  a packet ready to be sent or not
	 * @throws IOException
	 */
	public boolean readyPacket() throws IOException{
		byte[] data = new byte[128];
		int bytesRead;
		boolean ready = false;
		if (!doneReading && (bytesRead = this.fp.read(data, 2, 126)) > 0){
			data[0] = (byte) this.packetNumber; // set the packet number
			data[1] = (byte) bytesRead; //send number of bytes read
			this.temp_packet.setData(data); //set date of the packet

			ready = true;
		}else{
			this.doneReading = true;
			ready = false;
		}
		return ready;
	}

	/**
	 * receive the response packet from sender
	 * @throws IOException
	 */
	public void receivePacket() throws IOException{
		// check time to see if need to resend packet
		long endTime = System.nanoTime();
		long duration = (int) ((endTime - this.lastSent) / 1000000);
		if (duration >  WAITTIME){
			this.logger.debug("Resenind window since time has expired");
			this.pw.transmitWindow(this.socket);
			this.lastSent = System.nanoTime();
		}
		try{
			this.socket.receive(this.in_packet);
			if (!this.in_packet.getAddress().equals(this.ia)){
				this.logger.debug("Not from the send address");
				this.receivePacket(); // may need to resend
			}else{
				this.sequence = (this.sequence + 1) % 2; // update the sequence number
				boolean moved = this.pw.movePackageWindow(this.in_packet.getData()[0]);
				if (!moved){
					this.receivePacket(); // not a valid ack or moved window
				}
			}
		}catch(SocketTimeoutException e){
			this.logger.debug("Timeout occurred so resending window");
			this.pw.transmitWindow(this.socket);
		}
		
		return;
		
	}

	/**
	 * singal the file has finished being sent
	 * @throws IOException
	 */
	public void signalFinished() throws IOException{
		// signal the file is done
		byte[] data = new byte[128];
		data[0] = (byte) this.sequence; // set the sequence number
		data[1] = (byte) 127; //send number of bytes read		
		this.temp_packet.setData(data);
		try{
			this.socket.receive(this.in_packet);
			if (!this.in_packet.getAddress().equals(this.ia)){
				this.logger.debug("Not from the send address");
				this.signalFinished();
			}else{
				if(this.in_packet.getData()[0] == this.sequence){
					this.logger.debug("Package was ack");
					this.sequence = (this.sequence + 1) % 2; // update the sequence number	
				}else{
					this.logger.debug("Acknowledgement for wrong package");
					this.signalFinished();
				}
			}	
		}catch(SocketTimeoutException e) {
			this.logger.debug("Timeout occurred so resending packet");
			this.signalFinished();
		}
		this.logger.debug("Done sending file");
	}

	/**
	 * send the file to the recipient
	 */
	public void sendFile() throws Exception{
		boolean done = false;
		while(!done){
			while(!this.pw.windowFull() && this.readyPacket()){
				this.readyPacket(); // ready the packet
				this.pw.appendPackage(this.packetNumber, this.temp_packet); // add the packet to the window
				this.logger.debug("Adding packet: " + this.packetNumber);
				this.packetNumber = (this.packetNumber + 1) % 128;
			}
			this.pw.transmitWindow(this.socket);
			this.lastSent = System.nanoTime();
			this.receivePacket();
			done = this.pw.doneYet(); // all packages have been acknowledged
			if (done && !this.doneReading){
				done = this.doneReading; // still need to read some packages
			}
		}
		this.signalFinished(); // signal done sending file
	}

	/**
	 *  how to run the program
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			if (args.length < 5){
				throw new Exception("Missing an arugment: hostAddress receiverPort senderPort fileName windowSize");
			}
			String hostAddress = args[0];
			int senderPort = new Integer(args[2]).intValue();
			int receiverPort = new Integer(args[1]).intValue();
			String fileName = args[3];
			int windowSize = new Integer(args[4]).intValue();
			if (windowSize < 0  || windowSize > 128){
				throw new Exception("Invalid window size");
			}
			int loggerLevel = 2;
			if (args.length > 5){
				loggerLevel = new Integer(args[5]).intValue();
			}
			Logger log = new Logger(loggerLevel);
			GoBackNSender gbs = new GoBackNSender(hostAddress,
													senderPort,
													receiverPort,
													fileName,
													windowSize,
													log);
			gbs.sendFile();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

}
