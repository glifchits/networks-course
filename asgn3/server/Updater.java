package a3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class Updater implements Runnable {
	private BlockingQueue<LinkedList<String>> updates;
	private LinkedList<SpecialSocket> clients; 
	private Logger logger;
	public Updater(BlockingQueue<LinkedList<String>> updates, Logger logger) {
		this.updates = updates;
		this.clients = new LinkedList<SpecialSocket>();
		this.logger = logger;
	}

	public synchronized void pushUpdate(LinkedList<String> update){
		LinkedList<SpecialSocket> remove = new LinkedList<SpecialSocket>();
		for(SpecialSocket client: this.clients){
			try{
				client.writeLines(update);
			}catch(IOException e){
				e.printStackTrace();
				this.logger.error("Updater failed to pass update");
				remove.add(client);
			}
		}
		// remove bad sockets
		for(SpecialSocket client: this.clients){
			this.clients.remove(client);
		}
	}

	public synchronized void addClient(SpecialSocket client){
		this.clients.add(client);
	}

	public void run(){
		while (true){
			try {
				this.pushUpdate(updates.take());
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
