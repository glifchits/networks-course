/**
 * Java Imports
 */
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * A class that makes the underlying data structure for the whiteboard server.
 * It stores individual points and uses a lock to ensure synchronization.
 * The underlying data structure is a HashMap of points where the Hash is the 
 * the point x and y coordinates.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#Point
 */
public class PaintArea {
	/**
	 * lock: used to ensure synchronization
	 * reader: this lock is used if one wants to read the points
	 * writer: this lock is used if one wants to write a set of points
	 * logger: used to logging purposes
	 * data: this is used to store the points
	 */
	private final ReentrantReadWriteLock lock;
	private final Lock reader;
	private final Lock writer;
	private Logger logger;
	private HashMap<String, Point> data;
	private BlockingQueue<LinkedList<String>> updates;
	final static String CRLF = "\r\n";

	/**
	 * the default constructor
	 * @param updates: the queue to add any POST, DELETE updates
	 */
	public PaintArea(BlockingQueue<LinkedList<String>> updates) {
		this.logger = new Logger();
		this.lock = new ReentrantReadWriteLock();
		this.reader = this.lock.readLock();
		this.writer = this.lock.writeLock();
		this.data = new HashMap<String, Point> (20);
		this.updates = updates;
	}

	/**
	 * This constructor takes a logger
	 * @param updates: the queue to add any POST, DELETE updates
	 * @param logger: the logger with different logging levels
	 * 
	 */
	public PaintArea(BlockingQueue<LinkedList<String>>updates, Logger logger) {
		this.logger = logger;
		this.lock = new ReentrantReadWriteLock();
		this.reader = this.lock.readLock();
		this.writer = this.lock.writeLock();
		this.data = new HashMap<String, Point> (20);
		this.updates = updates;
	}

	/**
	 * add a set of points to the data set
	 * After the points are added the request is sent to a queue 
	 * to send out updates to all clients
	 * @param points: the list points to add
	 * @throws InterruptedException: thrown when a timeout occurs
	 */
	public void addPoints(LinkedList<Point> points) throws InterruptedException{
		Point current = null;
		try{
			if (this.writer.tryLock(3, TimeUnit.SECONDS)){
				for(Point point :points){
					current = point;
					this.data.put(point.toString(), point);
				}
				LinkedList <String> lines = new LinkedList <String>();
				lines.add("PaintProtocol/1.0 201 Successful" + CRLF);
				lines.add("Content-Type: text/html" + CRLF);
				lines.add(CRLF);
				for (Point point :points){
					lines.add("point " + point.format() + CRLF); // format the response
				}
				lines.add(CRLF);
				this.updates.put(lines);
			}
		}catch(NullPointerException e){
			this.logger.debug("Null Pointer when adding point: (" + current.toString() + ")");
			this.logger.error(e.getMessage());
			throw e;
		}catch (InterruptedException e){
			this.logger.debug("A timeout has occurred when adding point: ("
					+ current.toString() + ")");
			throw e;
		}catch (Exception e){
			this.logger.debug("An error has occurred when adding point- " +
								current.toString());
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
		}
		return;
	}

	/**
	 * removes a list of points
	 * After the points are removed the request is added to a queue
	 * to send updates to all the clients
	 * @params points: the list of points to remove
	 * @throws NullPointerException: thrown when removing an invalid point
	 * @throws InterruptedException: thrown when a timeout occurs
	 */
	public void removePoints(LinkedList<Point> points)
			throws NullPointerException , InterruptedException{
		Point point = null;
		LinkedList<Point> deletedPoints = new LinkedList<Point>();
		try{
			if (this.writer.tryLock(3, TimeUnit.SECONDS)){
				for(Point current: points){
					point = current;
					this.logger.debug("Removing point: " + current.toString());
					Point p = this.data.remove(current.toString());
					this.logger.debug("Point: " + p);
					if (p != null) {
						deletedPoints.add(p);
					} else {
						this.logger.debug("Point is not in the whiteboard. No Delete " + current.toString());
					}
				}
				if (deletedPoints.size() == 0) {
					logger.debug("no points were deleted. Sending no response");
					return;
				}
				LinkedList <String> lines = new LinkedList <String>();
				lines.add("PaintProtocol/1.0 200 DELETED" + CRLF);
				lines.add("Content-Type: text/html" + CRLF);
				lines.add(CRLF);
				for (Point c : deletedPoints){
					point = c;
					lines.add("point " + c.format() + CRLF); // format the response
				}
				lines.add(CRLF);
				this.updates.put(lines);
			}
		} catch(NullPointerException e){
			this.logger.debug("Null Pointer when removing point: " + point.toString());
			this.logger.debug(e.getMessage());
			throw e; // throw the error up
		}catch (InterruptedException e){
			this.logger.debug("A timeout has occurred when getting point: "
					+ point.toString());
			throw e;
		}catch(Exception e){
			this.logger.error("An error has occurred when removing point: " +
								point.toString());
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
			// make sure to unlock regardless of what happened
		}
		return;
	}

	/**
	 * get all the points for the paintarea
	 * @throws InterruptedException: thrown when a timeout occurs
	 */
	public LinkedList<Point> getPoints() throws InterruptedException{
		LinkedList<Point> l = null;
		try{
			if (this.reader.tryLock(3, TimeUnit.SECONDS)){
				l = new LinkedList<Point>();
				for (Point iterable_element : this.data.values()) {
					l.add(iterable_element);
			    }
			}
		}catch (InterruptedException e){
			this.logger.debug("A timeout has occurred when getting points");
			throw e;
		}finally{
			this.reader.unlock();
		}
		return l;
	}

	/**
	 * The main function. It is used for testing purposes
	 * @param args
	 * @throws InterruptedException 
	 * @throws NullPointerException 
	 */
	public static void main(String[] args) throws NullPointerException, InterruptedException {
		// TODO Auto-generated method stub
		/*
		 * Test just one thread and basic operations
		 */
		Logger lg = new Logger(0);
		BlockingQueue<LinkedList<String>> bq = new LinkedBlockingQueue<LinkedList<String>>();
		PaintArea pa = new PaintArea(bq, lg);
		LinkedList<Point> result = pa.getPoints();
		Point p;
		// test empty get
		if(result.size() != 0){
			lg.error("Was not initliazed not to empty");
		}
		// add an point
		LinkedList<Point> points = new LinkedList<Point>();
		points.add(new Point(0, 0, new Color(0)));
		pa.addPoints(points);
		result = pa.getPoints();
		// check the point was added successfully
		if(result.size() != 1){
			lg.error("Point was not added properly");
		}
		p = result.getFirst();
		if (p.getX() != 0){
			lg.error("Point was not correctly");
		}
		// add a second point
		LinkedList<Point> points2 = new LinkedList<Point>();
		points2.add(new Point(10, 0, new Color(255)));
		pa.addPoints(points2);
		result = pa.getPoints();
		if(result.size () != 2){
			lg.error("Second point was not added");
		}
		result = pa.getPoints();
		p = result.getFirst();
		if (p.getX() != 0){
			lg.error("First Point was not correctly");
		}
		p = result.getLast();
		if (p.getX() != 10){
			lg.error("Second Point was not correctly");
		}
		// overwrite a previous point and remove point
		points.removeFirst();
		points.add(new Point(0, 0, new Color(200)));
		pa.addPoints(points);
		points2.removeFirst();
		points2.add(new Point(10, 0, null));
		pa.removePoints(points2);
		result = pa.getPoints();
		if (result.size() != 1){
			lg.error("There should be only one point: " + result.size());
		}
		p  = result.getFirst();
		if(! p.getColor().equals(new Color (200)) ){
			lg.error("Color was not updated correctly");
		}
		lg.info("Completed Testing");
		LinkedList<String> r = bq.take();
		for(String s: r){
			lg.debug(s);
		}
	}
}
