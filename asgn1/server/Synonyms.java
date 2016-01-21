import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class consists of the interface for the underlining data structure.
 * It helps to seperate the Synonyms results and the underlying strucutre.
 * <p>
 * There are three main methods getPair, addPair, removePair which all describe 
 * their use. Each method requires access to a lock ,which allows for concurrency.
 * The relations  between synonyms are reflective, symmetric and transitive.
 * </p>
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#DataStructure
 * @see Class#Logger
 * 
 */
public class Synonyms{
	/**
	 * 
	 */
	private final ReentrantReadWriteLock lock;
	private final Lock reader;
	private final Lock writer;
	private Logger logger;
	private DataStructure data;

	/**
	 * The default constructor which initializes the locks and the data structre.
	 * It assumes the default logger.
	 */
	public Synonyms() {
		// TODO Auto-generated constructor stub
		this.logger = new Logger();
		this.lock = new ReentrantReadWriteLock();
		this.reader = this.lock.readLock();
		this.writer = this.lock.writeLock();
		this.data = new DataStructure(this.logger);
	}

	/**
	 * The overloaded constructor which initializes the locks and the data structure.
	 * It takes a logger.
	 * @param logger the logger object (Logger)
	 */
	public Synonyms(Logger logger){
		this(); // call base constructor
		this.logger = logger;
		this.data = new DataStructure(this.logger);
	}

	/**
	 * a method used to add a pair of strings (word, pair)
	 * @param word the word to be added
	 * @param match the matching synonym for the word
	 * @throws NullPointerException
	 * @return result  a string indication the action taken
	 */
	public void addPair(String word, String match)throws NullPointerException{
		this.writer.lock();
		try{
			this.logger.debug("Adding pair - " + word + " : " + match);
			this.data.put(word, match);
		}catch(NullPointerException e){
			this.logger.debug("Null Pointer when adding pair- " +
								word + " : " + match);
			this.logger.error(e.getMessage());
			throw e;
		}catch (Exception e){
			this.logger.debug("An error has occurred when adding pair- " +
								word + " : " + match);
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
		}
		return;
	}

	/**
	 * a method to remove a word
	 * @param word the word to remove (String)
	 * @throws NullPointerException when the word is not found
	 * @return
	 */
	public void removePair(String word)throws NullPointerException{
		this.writer.lock();
		try{
			this.logger.debug("Removing word: " + word);
			this.data.remove(word);
		} catch(NullPointerException e){
			this.logger.debug("Null Pointer when removing word: " +
								word);
			this.logger.debug(e.getMessage());
			throw e; // throw the error up
		}catch(Exception e){
			this.logger.error("An error has occurred when removing word: " +
								word);
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
			// make sure to unlock regardless of what happened
		}
		return;
	}

	/**
	 * a method that gets all the matching synonyms for the word.
	 * @param word the word to find synonyms for (String)
	 * @throws NullPointerException when the word is not found
	 * @return matches all the synonyms associated with word. Comma separated (String)
	 */
	public String getPair(String word)throws NullPointerException{
		String matches = null;
		this.reader.lock();
		try{
			this.logger.debug("Get synonyms for word: "+ word);
			matches = this.data.get(word);
			this.logger.debug("Resulting synonyms for word"
								+ word + " - " + matches);
		}catch (NullPointerException e){
			this.logger.debug("Null Pointer when getting word: " +
					word);
			this.logger.debug(e.getMessage());
			throw e;
		}catch (Exception e){
			this.logger.error("An error has occurred when getting word: " +
					word);
			this.logger.error(e.getMessage());
		}finally{
			this.reader.unlock();
		}
		return matches;
	}

	/**
	 * The main function. It is used for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Test just one thread and basic operations
		 */
		Logger lg = new Logger(0);
		Synonyms s = new Synonyms(lg);
		s.addPair("Hello" , "Hi");
		String result = s.getPair("Hello");
		if(result.compareTo("Hi") != 0){
			lg.error("Put of Hello & Hi did not work");
		}
		result = s.getPair("Hi");
		if(result.compareTo("Hello") != 0){
			lg.error("Put of Hello & Hi did not work");
		}
		s.removePair("Hi");
		s.removePair("Hello");
		/*
		 * Try testing with Two Threads
		 */
	}
}
