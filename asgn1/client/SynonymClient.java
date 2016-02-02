import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 *
 * SynonymClient
 *
 * This class encapsulates methods which directly communicate with the
 * SynonymProtocol server
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#ClientGui
 * @see Class#Request
 */
public class SynonymClient {

    final static String CRLF = "\r\n";
    final static int SUCCESS = 200;

    private Socket socket;
    private DataOutputStream output;
    private BufferedReader input;

    public SynonymClient(){
        socket = null;
        output = null;
        input = null;
    }

    /**
     * Creates a connection to the SynonymProtocol server.
     *
     * @param ipAddress the IP address of the server
     * @param portNumber the port number the server is exposed on
     * @throws any exception that might occur while initializing the socket
     * or DataOutputStream
     */
    public boolean connect(String ipAddress, int portNumber) throws Exception {
        socket = new Socket(ipAddress, portNumber);
        output = new DataOutputStream(this.socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return true;
    }

    /**
     * @returns whether this client instance is connected to a server
     */
    public boolean isConnected() {
        return socket != null;
    }

    /**
     * Use this method to guarantee that the client is connected to a server
     * before attempting to interact with it.
     *
     * @throws Exception if the client instance is not connected to a server
     */
    private void throwIfNotConnected() throws Exception {
        if (!isConnected()) {
            throw new Exception("You are not connected to the server");
        }
    }

    /**
     * Sends the disconnect message to the server, signalling that we wish to
     * terminate the connection to the server.
     * @throws Exception if there is no connection to the server or if there
     * is an error writing bytes to the server
     */
    private void sendDisconnectToServer() throws Exception {
        throwIfNotConnected();
        output.writeBytes(CRLF); // sends an empty line, signals disconnect
    }

    /**
     * An all in one method which wraps all necessary functionality to
     * disconnect from the server.
     */
    public void disconnect() throws Exception {
        throwIfNotConnected();
        sendDisconnectToServer();
        socket.close();
    }

    /* Methods for interacting with the Synonym Protocol */

    /**
     * Sends a GET to the server: gets synonyms of a word
     *
     * @param getWord the string whose synonyms we want to retrieve
     * @returns a comma-separated single-line string of all getWord's synonyms
     * stored on the server
     * @throws SocketException if there is an issue with the server socket
     */
    public String get(String getWord) throws SocketException {
        System.out.println("Get word: " + getWord);
        String request = "GET " + getWord;
        try {
            output.writeBytes(request + CRLF);
            String header = input.readLine();
            input.readLine(); // Content Type
            input.readLine(); // CRLF
            String body = input.readLine();
            System.out.println(header);
            System.out.println(body);
            int code = parseHeaderCode(header);
            if (code == SUCCESS) {
                return body;
            } else {
                return parseHeaderMessage(header);
            }
        } catch (SocketException e) {
            throw e;
        } catch(Exception e) {
            System.out.println("Exception:" + e.toString());
            return e.toString();
        }
    }

    /**
     * Sends a SET to the server: defines two words as synonyms of each other
     *
     * @param word1 set word1 to be synonym of word2
     * @param word2 set word2 to be synonym of word1
     * @returns the server's response body
     * @throws SocketException if there is an issue with the server socket
     */
    public String set(String word1, String word2)  throws SocketException {
        System.out.println("Set Synonyms: " + word1 + " is a synonym for " + word2);
        String request = "SET " + word1 + " " + word2;
        try {
            output.writeBytes(request + CRLF);
            String header = input.readLine();
            input.readLine(); // Content-type
            input.readLine(); // CRLF
            String body = input.readLine();
            int code = parseHeaderCode(header);
            if (code == SUCCESS) {
                return body;
            } else {
                return parseHeaderMessage(header);
            }
        } catch (SocketException e) {
            throw e;
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Sends a REMOVE to the server: choose a word to remove from the server's
     * memory of synonyms
     *
     * @param removeWord the word which we want to remove from the server memory
     * @returns the response body
     * @throws SocketException if there is an issue with the server socket
     */
    public String remove(String removeWord) throws SocketException {
        System.out.println("Remove word: " + removeWord);
        String request = "REMOVE " + removeWord;
        try {
            output.writeBytes(request + CRLF);
            String header = input.readLine();
            input.readLine(); // Content-type
            input.readLine(); // CRLF
            String body = input.readLine();
            int code = parseHeaderCode(header);
            if (code == SUCCESS) {
                return body;
            } else {
                return parseHeaderMessage(header);
            }
        } catch (SocketException e) {
            throw e;
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Retrieves the response status code by parsing the response header
     *
     * @param header the response header
     * @returns the status code
     * @throws NoSuchElementException if the parser cannot find a token
     */
    public int parseHeaderCode(String header) throws NoSuchElementException{
        int code = 0;
        try {
            StringTokenizer tokens = new StringTokenizer(header);
            tokens.nextToken();
            String codeString = tokens.nextToken();
            code = Integer.parseInt(codeString);
        } catch (NoSuchElementException e) {
            throw e;
        }
        return code;
    }

    /**
     * Retrieves the response message by parsing the response header
     *
     * @param header the response header
     * @returns the response message
     */
    public String parseHeaderMessage(String header) {
        String message = null;
        try {
            StringTokenizer tokens = new StringTokenizer(header);
            tokens.nextToken();
            tokens.nextToken();
            message = "";
            while (tokens.hasMoreTokens()){
                message += tokens.nextToken() + " ";
            }
        } catch (NoSuchElementException e) {
            throw e;
        }
        return message;
    }
}
