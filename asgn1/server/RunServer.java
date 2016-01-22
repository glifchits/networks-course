/**
 * 
 */


/**
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#Server
 */
public final class RunServer {
	/**
	 * @param args two parameters the first one is Port Number and second
	 * 				is the logging level
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			int port = new Integer(args[0]).intValue();
			Logger logger = null;
			if(args.length > 1){
				logger = new Logger(new Integer(args[1]).intValue());
			}else{
				logger = new Logger(2); // production
			}
			System.out.println("Server running on port: " + args[0]);
			Server s = new Server(logger);
			s.run(port);
		}catch( Exception e){
			System.out.println("No port number given");
		}
	}
}
