/**
 * Java Imports
 */

/**
 * A exception used for the point class
 * It stores the x and y coordinate
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @extend Exception
 */
public class PointException extends Exception {
	/**
	 * serialVersionUID: was recommended by Eclipse
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the public constructor
	 * @params message: the error message
	 */
	public PointException(String message) {
		// TODO Auto-generated constructor stub
		super (message);
	}

}
