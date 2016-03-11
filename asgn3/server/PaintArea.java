package a3;

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
				this.data.remove(point);
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

	public LinkedList<Point> getPoints(){
		LinkedList<Point> l = null;
		try{
			if (this.writer.tryLock(3, TimeUnit.SECONDS)){
				LinkedList<Point> l = new LinkedList<Point>();
				for (Point iterable_element : this.data.values()) {
					l.add(iterable_element);
			    }
			}
		}catch (InterruptedException e){
			this.logger.debug("A timeout has occurred when getting points");
			throw e;
		}finally{
			this.writer.unlock();
		}
		return l;

	}


}
