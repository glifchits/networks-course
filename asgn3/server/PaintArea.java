package a3;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PaintArea {
	private final ReentrantReadWriteLock lock;
	private final Lock reader;
	private final Lock writer;
	private Logger logger;
	private HashMap<String, Point> data;

	public PaintArea() {
		this.logger = new Logger();
		this.lock = new ReentrantReadWriteLock();
		this.reader = this.lock.readLock();
		this.writer = this.lock.writeLock();
		this.data = new HashMap<String, Point> (20);
	}

	public PaintArea(Logger logger) {
		this.logger = logger;
		this.lock = new ReentrantReadWriteLock();
		this.reader = this.lock.readLock();
		this.writer = this.lock.writeLock();
		this.data = new HashMap<String, Point> (20);
	}

	public void addPoint(Point point) throws InterruptedException{
		try{
			if (this.writer.tryLock(3, TimeUnit.SECONDS)){
				this.data.put(point.toString(), point);
			}
		}catch(NullPointerException e){
			this.logger.debug("Null Pointer when adding point: (" + point.toString() + ")");
			this.logger.error(e.getMessage());
			throw e;
		}catch (InterruptedException e){
			this.logger.debug("A timeout has occurred when adding point: ("
					+ point.toString() + ")");
			throw e;
		}catch (Exception e){
			this.logger.debug("An error has occurred when adding point- " +
								point.toString());
			this.logger.error(e.getMessage());
		}finally{
			this.writer.unlock();
		}
		return;
	}

	public void removePoint(Point point)
			throws NullPointerException , InterruptedException{
		try{
			if (this.writer.tryLock(3, TimeUnit.SECONDS)){
				this.logger.debug("Removing point: " + point.toString());
				Point p = this.data.remove(point.toString());
				this.logger.debug("Point: " + p);
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
		PaintArea pa = new PaintArea(lg);
		LinkedList<Point> result = pa.getPoints();
		Point p;
		// test empty get
		if(result.size() != 0){
			lg.error("Was not initliazed not to empty");
		}
		// add an point
		pa.addPoint(new Point(0, 0, new Color(0)));
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
		pa.addPoint(new Point(10, 0, new Color(255)));
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
		pa.addPoint(new Point(0, 0, new Color(200)));
		pa.removePoint(new Point(10, 0, null));
		result = pa.getPoints();
		if (result.size() != 1){
			lg.error("There should be only one point: " + result.size());
		}
		p  = result.getFirst();
		if(! p.getColor().equals(new Color (200)) ){
			lg.error("Color was not updated correctly");
		}
		lg.info("Completed Testing");
	}
}
