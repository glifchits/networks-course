package server;

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
	 * The main function. It is used for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	/**
	 * a method used to add a pair of strings (word, pair)
	 * @param word the word to be added
	 * @param match the matching synonym for the word
	 * @return result  a string indication the action taken
	 */
	public String addPair(String word, String match){
		String result = null;
		this.writer.lock();
		try{
			this.logger.debug("Adding pair - " + word + " : " + match);
			result = this.data.put(word, match);
			if (result.compareTo(word + " : " + match + " - was added") == 0 ){
				this.logger.debug(word + " : " + match + " - was added");
			}
			result = this.data.put(match, word); // make sure it is reflective
			if (result.compareTo(match + " : " + word + " - was added") == 0 ){
				this.logger.debug(match + " : " + word + " - was added");
			}
		}catch (Exception e){
			this.logger.error("An error has occurred when adding pair- " +
								word + " : " + match);
			this.logger.error(e.getMessage());
		}finally{
			this.reader.unlock();
		}
		return result;
	}
	/**
	 * 
	 * @param word
	 * @return
	 */
	public String removePair(String word){
		String result = null;
		this.writer.lock();
		try{
			this.logger.debug("Removing word: " + word);
			result = this.data.remove(word);
			if (result == null){
				result = word + " was not in dictionary";
				this.logger.debug("Word was not in dictionary: "+ word);
			}else{
				this.logger.debug(word +" : " + result + " - was removed");
				result = word +" : " + result + " - was removed";
			}
		} catch(Exception e){
			this.logger.error("An error has occurred when removing word: " +
								word);
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
		}
		return result;
	}
	/**
	 * a method that gets all the matching synonyms for the word.
	 * @param word the word to find synonyms for (String)
	 * @return matches all the synonyms associated with word. Comma separated (String)
	 */
	public String getPair(String word){
		String matches = null;
		this.reader.lock();
		try{
			this.logger.debug("Get synonyms for word: "+ word);
			matches = this.data.get(word);
			this.logger.debug("Resulting synonyms for word"
								+ word + " - " + matches);
		}catch (Exception e){
			this.logger.error("An error has occurred when getting word: " +
					word);
			this.logger.error(e.getMessage());
		}finally{
			this.reader.unlock();
		}
		return matches;
	}
}
