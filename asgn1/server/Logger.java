import java.io.BufferedReader;
import java.io.IOException;

/**
 * A class used to logging information. The default is to only log errors and info.
 * To log debug information than change the mode to Debug of the logger.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 */
public class Logger {
	private int mode;
	private static int DEBUG = 0;
	private static int INFO = 1;
	private static int PRODUCTION = 2;

	/**
	 * the default constructor. Initializes the mode to INFO
	 */
	public Logger(){
		this.mode = INFO;
	}

	/**
	 * the constructor used to specify what type of logging
	 * @param mode the desired mode of the logger (int)
	 */
	public Logger(int mode) {
		this.mode = mode;
	}
	
	/**
	 * a method to log the information if in DEBUG mode
	 * @param message the message to log (String)
	 */
	public void debug(String message){
		if (this.mode == DEBUG){
			System.out.println("DEBUG - " + message);
		}
	}
	
	/**
	 * a method to log the information only if not PRODUCTION
	 * @param message the message to log (String)
	 */
	public void info(String message){
		if (this.mode != PRODUCTION){
			System.out.println("INFO - " + message);
		}
	}
	
	/**
	 * a method to log the error
	 * @param message the error to log (String)
	 */
	public void error(String message){
		System.err.println("ERROR - "+ message);
	}

	/**
	 * a method to log the message in the buffered reader if in DEBUG mode
	 * @param br the buffered reader to log (BufferedReader)
	 */
	public void debug(BufferedReader br){
		if (this.mode == DEBUG){
			String headerLine = null;
			try {
				while ((headerLine = br.readLine()).length() != 0) {
					System.out.println(headerLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * a method to log the message in the buffered reader
	 * @param br the buffered reader to log (BufferedReader)
	 */
	public void info(BufferedReader br){
			String headerLine = null;
			if (this.mode != PRODUCTION){
				try {
					while ((headerLine = br.readLine()).length() != 0) {
						System.out.println(headerLine);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	/**
	 * a method to log the error in the buffered reader
	 * @param br the buffered reader to log (BufferedReader)
	 */
	public void error(BufferedReader br){
		String headerLine = null;
		try {
			while ((headerLine = br.readLine()).length() != 0) {
				System.err.println(headerLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
