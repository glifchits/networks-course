import java.io.*;
import java.net.*;


public class SynonymClient {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public boolean connect(String ipAddress, int portNumber) throws Exception {
        socket = new Socket(ipAddress, portNumber);
        output = new PrintWriter(socket.getOutputStream());
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
        output.println(); // sends an empty line, signals disconnect
    }

    public void disconnect() throws Exception {
        throwIfNotConnected();
        sendDisconnectToServer();
        socket.close();
    }

    /* Methods for interacting with the Synonym Protocol */

    public void get(String getWord) {
        System.out.println("Get word: " + getWord);
    }

    public void set(String word1, String word2) {
        System.out.println("Set Synonyms: " + word1 + " is a synonym for " + word2);
    }

    public void remove(String removeWord) {
        System.out.println("Remove word: " + removeWord);
    }

}
