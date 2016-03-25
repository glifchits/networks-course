/**
 * Java Imports
 */
import java.awt.Color;
import java.awt.Point;
/**
 * A class is used to store a points and its color
 * It stores the x and y coordinate
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 */
public class ColouredPoint {
	/**
	 * x: the x coordinate (int)
	 * y: the y coordinate (int)
	 * color: the color of the point (Color)
	 */
	public int x;
	public int y;
	private Color color;
	/**
	 * the public constructor
	 * @params x the cooridnate position on the horizontal axis
	 * @params y the cooridnate position on the vertical axis
	 */
	public ColouredPoint(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public ColouredPoint(int x, int y, int r, int g, int b) {
		this(x, y, new Color(r, g, b));
	}

	public ColouredPoint(Point point, int r, int g, int b) {
		this(point.x, point.y, r, g, b);
	}
	/**
	 * a constructor that takes a string containing the following format
	 * x y rr:gg:bb
	 * @params f: the string format of a point
	 * @throws PointException thrown when the format is missing a parameter
	 * @see Method#format
	 */
	public ColouredPoint(String f) throws Exception {
		String[] part = f.split(" ");
		if (part.length != 3){
			throw new Exception("Missing parameter on point: "+ f);
		}
		String[] colors = part[2].split(":");
		this.x = Integer.parseInt(part[0]);
		this.y = Integer.parseInt(part[1]);
		this.color = new Color( Integer.parseInt(colors[0]),
				  				Integer.parseInt(colors[1]),
				  				Integer.parseInt(colors[2]));
	}
	/**
	 * used to crease the hash of the point
	 * @return hash: the resulting hash (int)
	 */
	public int hashCode() {
		int hash;
		hash = this.x * 1000 + y;
		System.out.println("Hashing: " + hash);
		return hash;
	}
	/**
	 * a function that returns the string represenation of the point
	 * @return the string of the point
	 */
	public String toString() {
		return this.x + " " + this.y;
	}
	/**
	 * used to format the string for its x and y coordinate along with its color
	 * @returns result: the formatted string (x y rr:gg:bb)
	 */
	public String format(){
		String result = "";
		if (this.color != null){
			result = (this.x + " " + this.y + " " + this.color.getRed()+":"+
					this.color.getGreen()+":"+ this.color.getBlue());
		}else{
			result = (this.x + " " + this.y);
		}
		return result;
	}
	/**
	 * check if two objects are equal
	 * @returns same: true if the objects are equal
	 */
	public boolean equals(Point rhs){
		boolean same = true;
		if ((rhs.x != this.x) ||(rhs.y != this.y)){
			same = false;
		}
		return same;
	}
	/**
	 * a setter for color
	 */
	public void setColor(Color color){
		this.color = color;
	}
	/**
	 * a getter for color
	 */
	public Color getColor(){
		return this.color;
	}
	/**
	 * a getter for x coordinate
	 */
	public int getX(){
		return this.x;
	}
	/**
	 * a getter for y coordinate
	 */
	public int getY(){
		return this.y;
	}
}
