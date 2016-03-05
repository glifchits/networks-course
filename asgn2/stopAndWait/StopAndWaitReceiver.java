/**
 * Imports
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * The stop and wait receiver. It has a buffer of 126 bytes.
 * It will acknowledge each packet by returning one byte with a value of sequence number.
 * It follows the standard Reliable Data Transfer 3.0 from Computer Networking: A Top-Down Approach.
 * The receiver will acknowledge the packet sequence number processed.
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DGSocket
 */
public class StopAndWaitReceiver {
	/**
	*
	* {@link socket}: the UDP socket
	* @see Class#DGSocket
	* {@link logger}: the logger for the class
	* @see Class#Logger
	* {@link fs}: the file output stream when transferring binary files
	* {@link fw}: the file writer for text files
	* {@link out_packet}: the datagram packet for responding
	* {@link in_packet}: the datagram packet for receiving
	* {@link ia}: the internet address of the sender
	* @see Class#
	* {@link sequence}: the sequence number of package (0 or 1)
	*/
	private UnreliableDatagramSocket socket;
	private Logger logger;
	private FileOutputStream fs;
	private DatagramPacket out_packet;
	private DatagramPacket in_packet;
	private InetAddress ia;
	private int sequence;
	private long firstPacketReceivedTime;

	private int SO_TIMEOUT = 5000;
	private float NS_TO_S = 1000*1000*1000;

	private int DATA_BUF = 124;
	private int END_BYTES = DATA_BUF+1;
	private int PACKET_SIZE = DATA_BUF+2;

	/**
	* The public constructor
	* @param hostAddress: a String of the host address
	* @param senderPort: the port number of the sender
	* @param receiverPort: the port number of this receiver
	* @param reliabilityNumber: the reliability number of the server @see Class#UnreliableDatagramSocket
	* @param fileName: the name of the file to output
	* @param logger: the logger of the class
	*
	* @throws IOException if unable to access file, create socket
	* @throws UnknownHostException if unable to find address for host
	* @throws SocketException if unable to create UDP socket
	*/
	public StopAndWaitReceiver(String hostAddress,
								int senderPort,
								int receiverPort,
								int reliabilityNumber,
								String fileName,
								Logger logger) throws IOException,
														UnknownHostException,
														SocketException {
		this.socket = new UnreliableDatagramSocket(receiverPort, reliabilityNumber, logger);
		this.logger = logger;
		this.ia = InetAddress.getByName(hostAddress);
		byte[] data = new byte[1];
		byte[] in_data = new byte[PACKET_SIZE];
		this.out_packet = new DatagramPacket(data, data.length, this.ia, senderPort);
		this.in_packet = new DatagramPacket(in_data, in_data.length, this.ia, senderPort);
		this.socket.setSoTimeout(SO_TIMEOUT);
		this.fs = new FileOutputStream(new File(fileName));
		this.logger.debug("Created receiver");
		this.sequence = 0;
		this.firstPacketReceivedTime = 0;
	}

	/**
	* a method that receives a packet.
	* It will call itself recursively until the packet is received
	* @throws IOException: this occurs when the socket is unable to receive a packet
	*/
	public int receivePacket() throws IOException {
		int result = -1;
		try {
			this.socket.receive(this.in_packet);
			byte[] data = this.in_packet.getData();
			this.logger.debug("Expected seq num " + this.sequence + ". Got " + data[0]);
			if (data[0] == this.sequence && data[1] != 0) {
				this.logger.debug("Packet was right sequence");
				if (this.firstPacketReceivedTime == 0) {
					this.firstPacketReceivedTime = System.nanoTime();
					this.logger.debug("Received first packet");
				}
				if (data[1] == END_BYTES) {
					// we are done
					this.logger.debug("Finish the file");
					this.saveFile();
				} else {
					// packet was received and should write to the file
					result = 0;
					this.writeFile(data);
				}
			} else {
				// just drop the packet
				result = 0;
				this.logger.debug("Packet was invalid sequence");
			}
		} catch(SocketTimeoutException e) {
			this.logger.debug("Timeout exception");
			result = this.receivePacket();
		}
		return result;
	}

	/**
	* write the data file using the appropriate output object
	* It will acknowledge the packet was processed
	* @param data: the byte data to output
	* @throws IOException: this occurs when unable to write to the file
	*/
	private void writeFile(byte[] data) throws IOException {
		this.fs.write(data, 2, data[1]);
		this.acknowledge();
	}

	/**
	* save the file and closes it
	* It will acknowledge the file was closed
	* @throws IOException: this occurs when unable to close the file
	*/
	private void saveFile() throws IOException {
		this.fs.close();
		this.acknowledge();
		this.logger.debug("Finished closing file");
	}

	/**
	* acknowledges the packet sequence was processed
	* @throws IOException: occurs when unable to send ack
	*/
	private void acknowledge() throws IOException {
		this.logger.debug("Acknowledging the packet");
		byte[] data = new byte[1];
		data[0] = (byte) this.sequence;
		this.out_packet.setData(data);
		this.socket.send(this.out_packet);
		this.sequence = (this.sequence + 1) % 2; // update the sequence number
		this.logger.debug("Packet should be ack");
	}

	/**
	* this will continually receive packet until final packet is signaled
	* @throws IOException: occurs in various submethods
	*/
	public void receiveFile() throws IOException {
		this.logger.info("Receiver: receiving file");
		long receiverStartTime = System.nanoTime();
		while (this.receivePacket() >= 0) {
			this.logger.debug("Receiving packet");
		}
		long receiverEndTime = System.nanoTime();
		double seconds = (receiverEndTime - receiverStartTime) / NS_TO_S;
		double secondsFromFirst = (receiverEndTime - this.firstPacketReceivedTime) / NS_TO_S;
		this.logger.info("Receiver: receive file complete");
		this.logger.info(String.format("Total time to receive file: %.3g seconds", seconds));
		this.logger.info(String.format("Total time to receive file since first packet received: %.3g seconds", secondsFromFirst));
	}

	/**
	* There are 5 mandatory parameters  and one optional parameter
	* @param 0:hostAddress: a string of the host address
	* @param 1: the sender port number
	* @param 2: the receiver port number
	* @param 3: the reliability number of the receiver
	* @param 4: the file name
	* @param 5: the logging level
	*/
	public static void main(String[] args) {
		try {
			if (args.length < 5) {
				throw new Exception("Missing an argument: hostAddress senderPort receiverPort reliabilityNumber fileName");
			}
			String hostAddress = args[0];
			int senderPort = new Integer(args[1]).intValue();
			int receiverPort = new Integer(args[2]).intValue();
			int reliabilityNumber = new Integer(args[3]).intValue();
			String fileName = args[4];
			Logger log;
			if (args.length > 5) {
				// logging level was set
				log = new Logger(new Integer(args[5]).intValue());
			} else {
				log = new Logger(2);
			}
			StopAndWaitReceiver gb = new StopAndWaitReceiver(hostAddress,
															senderPort,
															receiverPort,
															reliabilityNumber,
															fileName,
															log);
			log.debug("Created");
			gb.receiveFile();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
