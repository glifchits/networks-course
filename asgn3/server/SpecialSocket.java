package a3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class SpecialSocket{
	private Socket socket;
	public SpecialSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket(){
		return this.socket;
	}
	public synchronized void writeLines(LinkedList<String> lines) throws IOException{
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
		for(String line: lines){
			os.writeBytes(line);
		}
	}

}
