/**
 * Imports
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * The stop and wait sender. It sends of size of 126 bytes.
 * The first byte is the sequence number, the second byte is the number of bytes being sent
 * the remaining 124 bytes are the data being sent
 * The sender will wait a reasonable amount of time before re-sending the packet
 * It follows the standard Reliable Data Transfer 3.0 from Computer Networking: A Top-Down Approach.
 * All files are sent as bytes.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#UnreliableDatagramSocket
 */
public class StopAndWaitSender {
	/**
	* {@link socket}: the UDP socket
	* @see Class#UnreliableDatagramSocket
	* {@link logger}: the logger for the class
	* @see Class#Logger
	* {@link fp}: the file input stream  for reading files
	* {@link out_packet}: the datagram packet for responding
	* {@link in_packet}: the datagram packet for receiving
	* {@link ia}: the internet address of the sender
	* {@link sequence}: the sequence number of package (0 or 1)
	*/
	private UnreliableDatagramSocket socket;
	private Logger logger;
	private DatagramPacket out_packet;
	private DatagramPacket in_packet;
	private FileInputStream fp;
	private InetAddress ia;
	private int sequence;

	private int DATA_BUF = 124;
	private int END_BYTES = DATA_BUF + 1; // 1 + maximum data buf size
	private int PACKET_SIZE = DATA_BUF + 2; // 1 byte for seq# and 1 for packet size

	/**
	* the public constructor
	* @param hostAddress: a String of the host address
	* @param senderPort: the port number of the sender
	* @param receiverPort: the port number of this receiver
	* @param fileName: the name of the file to output
	* @param logger: the logger of the class
	*
	* @throws FileNotFoundException if unable to find file
	* @throws UnknownHostException if unable to find address for host
	* @throws SocketException if unable to create UDP socket
	*/
	public StopAndWaitSender(String hostAddress,
								int senderPort,
								int receiverPort,
								String fileName,
								Logger logger) throws UnknownHostException,
														SocketException,
														FileNotFoundException {
		this.socket = new UnreliableDatagramSocket(senderPort, logger);
		this.logger = logger;
		this.ia = InetAddress.getByName(hostAddress);
		byte[] data = new byte[PACKET_SIZE];
		byte[] in_data = new byte[1];
		this.out_packet = new DatagramPacket(data, data.length, this.ia, receiverPort);
		this.in_packet = new DatagramPacket(in_data, in_data.length, this.ia, receiverPort);
		this.socket.setSoTimeout(10000);
		this.fp = new FileInputStream(new File(fileName));
		this.logger.debug("Created sender");
		this.sequence = 0;
	}

	/**
	 * Sends the outgoing packet
	 */
	public void sendPacket() throws IOException {
		try {
			this.logger.debug("Sending packet");
			this.socket.send(this.out_packet);
			this.socket.receive(this.in_packet);
			this.logger.debug("Got an incoming packet");
			if (!this.in_packet.getAddress().equals(this.ia)) {
				this.logger.debug("... not from the receiver address, resending packet");
				this.sendPacket();
			} else {
				if (this.in_packet.getData()[0] == this.sequence) {
					this.logger.debug("Packet was ACKed");
					this.sequence = (this.sequence + 1) % 2; // update the sequence number
				} else {
					this.logger.debug("ACK received for wrong packet, resending packet");
					this.sendPacket();
				}
			}
		} catch(SocketTimeoutException e) {
			this.logger.debug("Timeout occurred, resending packet");
			this.sendPacket();
		}
	}

	public void sendFile() throws IOException {
		byte[] data = new byte[PACKET_SIZE];
		int bytesRead;
		while ((bytesRead = this.fp.read(data, 2, DATA_BUF)) > 0) {
			data[0] = (byte) this.sequence; // set the sequence number
			data[1] = (byte) bytesRead; // send number of bytes read
			this.out_packet.setData(data); // set data of the packet
			this.sendPacket();
		}
		// signal the file is done
		data[0] = (byte) this.sequence; // set the sequence number
		data[1] = (byte) END_BYTES; // send number of bytes read
		this.out_packet.setData(data);
		this.sendPacket();
		this.logger.debug("Done sending file");
	}

	public static void main(String[] args) {
		try {
			if (args.length < 4) {
				throw new Exception("Missing an argument: hostAddress receiverPort senderPort fileName");
			}
			String hostAddress = args[0];
			int receiverPort = new Integer(args[1]).intValue();
			int senderPort = new Integer(args[2]).intValue();
			String fileName = args[3];
			Logger log;
			if (args.length > 4) {
				int level = new Integer(args[4]).intValue();
				log = new Logger(level);
			} else {
				log = new Logger(0);
			}
			log.debug(fileName);
			StopAndWaitSender sw = new StopAndWaitSender(hostAddress,
														senderPort,
														receiverPort,
														fileName,
														log);
			sw.sendFile();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
