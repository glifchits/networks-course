

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

public class StopAndWaitReceiver {
	DGSocket socket;
	Logger logger;
	PrintWriter out;
	FileOutputStream fs;
	FileWriter fw;
	DatagramPacket out_packet;
	DatagramPacket in_packet;
	InetAddress ia;
	int sequence;
	boolean binaryFile;
	public StopAndWaitReceiver(String hostAddress,
								int senderPort,
								int receiverPort,
								int reliabilityNumber,
								String fileName,
								Logger logger) throws IOException  {
		this.socket = new DGSocket(receiverPort, reliabilityNumber, logger);
		this.logger = logger;
		this.ia = InetAddress.getByName(hostAddress);
		byte[] data = new byte[1];
		data[0] = (byte) 1;
		byte[] in_data = new byte[128];
		this.out_packet = new DatagramPacket(data, data.length, this.ia, senderPort);
		this.in_packet = new DatagramPacket(in_data, in_data.length, this.ia, senderPort);
		this.socket.setSoTimeout(5000);
		this.fw = new FileWriter(new File(fileName));
		this.fs = new FileOutputStream(new File(fileName));
		this.logger.debug("Created receiver");
		this.sequence = 0;
		binaryFile = false;
	}

	public int receivePacket() throws IOException{
		int result = -1;
		try{
			this.socket.receive(this.in_packet);
			byte[] data = this.in_packet.getData();
			this.logger.debug(data);
			this.logger.debug(this.sequence + " vs " + data[0]);
			if(data[0] == this.sequence && data[1] != 0){
				this.logger.debug("Packet was right sequence");
				this.sequence = (this.sequence + 1) % 2; // update the sequence number
				if(data[1] == 127){
					// we are done
					this.logger.debug("Finish the file");
					this.saveFile();
				}else{
					result = 0;
					this.writeFile(data);
				}
			}else{
				// just drop the packet
				result = 0;
				this.logger.debug("Packet was invalid sequence");
			}
		}catch(SocketTimeoutException e){
			this.logger.debug("Timeout exception");
			result = this.receivePacket();
		}
		return result;
	}
	
	private void writeFile(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		if(this.binaryFile){
			this.fs.write(data, 2, data[1]);
		}else{
			this.fw.write(new String(data,"UTF-8"), 2, data[1]);
		}
		this.acknowledge();
	}

	private void saveFile() throws IOException {
		// TODO Auto-generated method stub
		if(this.binaryFile){
			this.fs.close();
		}else{
			this.fw.close();
		}
		this.acknowledge();
		this.logger.debug("Finished closing file");
	}
	
	private void acknowledge() throws IOException {
		// TODO Auto-generated method stub
		this.logger.debug("Acknowleding the packet");
		this.socket.send(this.out_packet);
		this.logger.debug("Packet shoudl be ack");
		
	}

	public void receiveFile() throws IOException{
		while (this.receivePacket() >= 0 ){
			this.logger.debug("receiving Packet");
		}
	}
	public static void main(String[] args) {
		try{
			if (args.length < 5){
				throw new Exception("Missing an arugment: hostAddress senderPort receiverPort reliabilityNumber fileName");
			}
			String hostAddress = args[0];
			int senderPort = new Integer(args[1]).intValue();
			int receiverPort = new Integer(args[2]).intValue();
			int reliabilityNumber = new Integer(args[3]).intValue();
			String fileName = args[4];
			Logger logger = new Logger(0);
			StopAndWaitReceiver gb = new StopAndWaitReceiver(hostAddress,
															senderPort,
															receiverPort,
															reliabilityNumber,
															fileName,
															logger);
			logger.debug("Created");
			gb.receiveFile();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
