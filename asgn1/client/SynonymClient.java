import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


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
    public boolean connect(String ipAddress, int portNumber) throws Exception {
        socket = new Socket(ipAddress, portNumber);
        output = new DataOutputStream(this.socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return true;
    }

    public boolean isConnected() {
        return socket != null;
    }

    private void throwIfNotConnected() throws Exception {
        if (!isConnected()) {
            throw new Exception("You are not connected to the server");
        }
    }

    private void sendDisconnectToServer() throws Exception {
        throwIfNotConnected();
        output.writeBytes(CRLF); // sends an empty line, signals disconnect
    }

    public void disconnect() throws Exception {
        throwIfNotConnected();
        sendDisconnectToServer();
        socket.close();
    }

    /* Methods for interacting with the Synonym Protocol */

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
