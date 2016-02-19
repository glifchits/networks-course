

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

public class StopAndWaitSender {
	DGSocket socket;
	Logger logger;
	BufferedReader in;
	DatagramPacket packet;
	DatagramPacket in_packet;
	FileInputStream fp;
	InetAddress ia;
	int sequence;
	public StopAndWaitSender(String hostAddress,
								int senderPort,
								int receiverPort,
								String fileName,
								Logger logger) throws UnknownHostException,
														SocketException,
														FileNotFoundException {
		this.socket = new DGSocket(senderPort, logger);
		this.logger = logger;
		this.ia = InetAddress.getByName(hostAddress);
		byte[] data = new byte[128];
		byte[] in_data = new byte[1];
		this.packet = new DatagramPacket(data, data.length, this.ia, receiverPort);
		this.in_packet = new DatagramPacket(in_data, in_data.length, this.ia, receiverPort);
		this.socket.setSoTimeout(10000);
		this.in = new BufferedReader(new FileReader(fileName));
		this.fp = new FileInputStream(new File(fileName));
		this.logger.debug("Created sender");
		this.sequence = 0;
	}
	public void sendPacket() throws IOException{
		this.logger.debug("Sending packet");
		try{
			this.logger.debug(this.packet.getData());
			this.socket.send(this.packet);
			this.socket.receive(this.in_packet);
			this.logger.debug("Got response");
			if (!this.in_packet.getAddress().equals(this.ia)){
				this.logger.debug("Not from the send address");
				this.sendPacket();
			}
		}catch(SocketTimeoutException e) {
			this.logger.debug("Timeout occurred so resending packet");
			this.sendPacket();
		}
		
	}
	public void sendFile() throws IOException{
		byte[] data = new byte[128];
		int bytesRead;
		while ((bytesRead = this.fp.read(data, 2, 126)) > 0){
			
			data[0] = (byte) this.sequence; // set the sequence number
			data[1] = (byte) bytesRead; //send number of bytes read
			this.sequence = (this.sequence + 1) % 2; // update the sequence number
			this.packet.setData(data); //set date of the packet
			this.sendPacket();			
		}
		// signal the file is done
		data[0] = (byte) this.sequence; // set the sequence number
		data[1] = (byte) 127; //send number of bytes read		
		this.packet.setData(data);
		this.sendPacket();
		this.logger.debug("Done sending file");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			if (args.length < 4){
				throw new Exception("Missing an arugment: hostAddress receiverPort senderPort  fileName");
			}
			String hostAddress = args[0];
			int receiverPort = new Integer(args[1]).intValue();
			int senderPort = new Integer(args[2]).intValue();
			String fileName = args[3];
			Logger log = null;
			if (args.length > 4){
				int level = new Integer(args[4]).intValue();
				log = new Logger(level);
			}else{
				log = new Logger(0);
			}
			log.debug(fileName);
			StopAndWaitSender sw = new StopAndWaitSender(hostAddress,
															senderPort,
															receiverPort,
															fileName,
															log);
			sw.sendFile();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	
}
