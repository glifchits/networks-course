/**
 * Java Imports
 */
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
/**
 * A class that is used to updates all clients
 * It runs as a thread and read updates from a queue
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 */
public class Updater implements Runnable {
	/**
	 * updates: the queue the updates are added to
	 * clients: the list of clients to update
	 * logger: the logger used
	 */
	private BlockingQueue<LinkedList<String>> updates;
	private LinkedList<SpecialSocket> clients; 
	private Logger logger;
	/**
	 * the public constructor
	 * @param updates: the update queue
	 * @param logger: the logger to use
	 */
	public Updater(BlockingQueue<LinkedList<String>> updates, Logger logger) {
		this.updates = updates;
		this.clients = new LinkedList<SpecialSocket>();
		this.logger = logger;
	}
	/**
	 * use to push an update to all clients
	 * @param update: every line for the request
	 */
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
	/**
	 * adds a client to updater
	 * @param client: the specia socket for the client
	 */
	public synchronized void addClient(SpecialSocket client){
		this.clients.add(client);
	}
	/**
	 * run method for the thread
	 */
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
