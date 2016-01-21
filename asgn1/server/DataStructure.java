import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The data structure that ensures the relationships between synonyms.
 * The relation of synonyms are reflective, symmetric and transitive. It uses a
 * HashMap where each key is a word and the values is a LinkedList of its
 * synonyms.
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#HashMap
 * @see Class#LinkedList
 */
public class DataStructure {
	private HashMap<String, LinkedList<String>> data;
	private Logger logger;
	
	/**
	 * The default constructor. It uses the default logger
	 */
	public DataStructure() {
		this.data = new HashMap <String, LinkedList<String>>(20);
		this.logger = new Logger();
	}

	/**
	 * The overloaded constructor. Specify the logger to be used
	 * @param logger the logger to be used (Logger)
	 */
	public DataStructure(Logger logger){
		this();
		this.logger = logger;
	}

	/**
	 * a method that gets the synonyms associated with the key
	 * @param key the key to get synonyms for (String)
	 * @throws NullPointerException when the item was not found
	 * @return result the synonyms associated with the key. Comma seperated (String)
	 */
	public String get(String key)throws NullPointerException{
		String result = "";
		LinkedList<String> list = this.data.get(key);
		Iterator<String> items = list.iterator();
		String item = null;
		while (items.hasNext()) {
			item  = items.next();
			this.logger.debug("Item:" + item);
			result += item + ",";
		}
		if (result.compareTo("") == 0){
			result = null;
		}else{
			result = result.substring(0, result.length()-1); // remove extra ,
		}
		this.logger.debug("Items:" + result);
		return result;
	}

	/**
	 * a method the removes all association with the key.
	 * @param key the key to remove (String)
	 * @throws NullPointerException raised when key does not exist
	 * @return result the of the transaction
	 */
	public void remove(String key)throws NullPointerException{
		LinkedList<String> list = this.data.get(key);
		LinkedList<String> removeList = null;
		// now remove all neighbors
		String item = null;
		Iterator<String> items = list.iterator();
		while(items.hasNext()){
			item = items.next();
			this.logger.debug("Removing word from Item: " + item);
			removeList = this.data.get(item);
			removeList.remove(key);
		}
		this.data.remove(key); // remove node
		return;
	}

	/**
	 * a method to add associate the value with the key
	 * @param key the key to associate with (String)
	 * @param value the value to be associated with the key (String)
	 * @throws NullPointerException should not really be raised
	 * @return result the result of the transaction (String)
	 */
	public void put(String key, String value) throws NullPointerException{
		LinkedList<String> keyList = this.data.get(key);
		LinkedList<String> valueList = this.data.get(value);
		if (keyList == null){
			// create a new list and add value to it
			keyList = new LinkedList<String> ();
			keyList.add(value);
			this.data.put(key, keyList);
		}else if(keyList.contains(value) == false){
			// add value since it is not added yet
			keyList.add(value);
		}
		if (valueList == null){
			// create a new list and add key to it
			valueList = new LinkedList<String> ();
			valueList.add(key);
			this.data.put(value, valueList);
		}else if(valueList.contains(key) == false){
			// add value since it is not added yet
			valueList.add(key);
		}
		/*
		 * Update each other
		 * now they are synonyms but need to update each others neighbors
		 */
		// now add value to all of key's neighbors and its neighbors to valueList
		Iterator<String> items = keyList.iterator();
		LinkedList<String> addList = null;
		String item;
		while(items.hasNext()){
			item = items.next();
			addList = this.data.get(item);
			if (item.compareTo(value) != 0  && addList.contains(value) == false){
				// add value to the list
				addList.add(value);
				this.logger.debug("Adding word from Item: " + item);
			}
			if(value.compareTo(item) != 0 && valueList.contains(item) == false){
				valueList.add(item);
			}
		}
		// now add key to all of value's neighbors and its neighbors to keyList
		items = valueList.iterator();
		addList = null;
		while(items.hasNext()){
			item = items.next();
			addList = this.data.get(item);
			if (item.compareTo(key) != 0 && addList.contains(key) == false){
				addList.add(key);
				this.logger.debug("Adding word from Item: " + item);
			}
			if(key.compareTo(item) != 0 && keyList.contains(item) == false){
				keyList.add(item);
			}
		}		
		return;
	}

	/**
	 * A main function used primarily for testing the Data Structure
	 * @param args
	 */
	public static void main(String[] args) {
		// initialize structure
		Logger lg = new Logger(0);
		DataStructure ds = new DataStructure();
		// testing element get on something missing
		String result = null;
		try{
			result = ds.get("FUCKER");
			lg.info(result);
			System.out.println("Should have raised Exception");
		}catch (NullPointerException e){
		}
		/*
		 * TESTING put (get)
		 */
		// test simple insert
		ds.put("Hey", "Hello");
		if(ds.get("Hey").compareTo("Hello") != 0){
			System.out.println("Failed to insert Hey");
		}
		if(ds.get("Hello").compareTo("Hey") != 0){
			System.out.println("Failed to insert Hello");
		}
		// now see if reflection holds
		ds.put("Hi", "Hey");
		if (ds.get("Hi").compareTo("Hey,Hello") != 0){
			System.out.println("Reflection failed for Hi");
		}
		if (ds.get("Hello").compareTo("Hey,Hi") != 0){
			System.out.println("Reflection failed for Hello");
		}
		// test if try to double insert something
		ds.put("Hi", "Hey");
		if (ds.get("Hi").compareTo("Hey,Hello") != 0){
			System.out.println("Double insert happened");
		}
		/*
		 * TESTING remove
		 */
		// now see check deletion
		try{
			ds.remove("Hello");
			System.out.println(ds.get("Hello"));
			System.out.println("Should have raised exception");
		}catch (NullPointerException e){
		}
		if (ds.get("Hi").compareTo("Hey") != 0){
			System.out.println("Remove of Hello missed reflection of Hi");
		}
		if (ds.get("Hey").compareTo("Hi") != 0){
			System.out.println("Remove of Hello missed reflection of Hey");
		}
		System.out.println("Test Complete");
	}
}
