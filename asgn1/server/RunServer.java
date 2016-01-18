/**
 * 
 */
package server;

/**
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - XXXXXXXX
 * @version 1.0
 * @see Class#Server
 */
public class RunServer {

	/**
	 * 
	 */
	public RunServer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
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
			Server s = new Server(logger);
			s.run(port);
		}catch( Exception e){
			System.out.println("No port number given");
		}
	}

}
