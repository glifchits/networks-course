import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * PaintClient
 *
 * Class which encapsulates all network functionality for the Whiteboard client
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 */
public class PaintClient {

    private Logger log;
    private Socket socket;
    private DataOutputStream output;
    private BufferedReader input;
    private Thread thread;

    public PaintClient() {
        log = new Logger(Logger.DEBUG);
        socket = null;
        output = null;
        input  = null;
        thread = null;
    }

    /*
     * @param {String} ipAddress server IP Address
     * @param {int} port server port number
     * @throws Exception
     */
    public boolean connect(String ipAddress, int portNumber) throws Exception {
        log.debug("establishing connection, IP: " + ipAddress + ", port: " + portNumber);
        socket = new Socket(ipAddress, portNumber);
        output = new DataOutputStream(socket.getOutputStream());
        input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        thread = new Thread(new InputReaderThread(input, log));
        thread.start();
        return true;
    }

    /**
     * An all in one method which wraps all necessary functionality to
     * disconnect from the server.
     */
    public void disconnect() throws Exception {
        throwIfNotConnected();
        output.write("QUIT".getBytes());
        socket.close();
    }

    /**
     * @returns whether this client instance is connected to a server
     */
    private boolean isConnected() {
        return socket != null;
    }

    /*
     * Simple method to call as a guard before doing anything that requires
     * the connection to be established
     */
    private void throwIfNotConnected() throws Exception {
        if (!isConnected()) {
            throw new Exception("You are not connected to the server");
        }
    }

}
