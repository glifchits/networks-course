import java.io.*;
import java.net.*;


public class SynonymClient {

    private static Socket socket;
    private static PrintWriter output;
    private static BufferedReader input;

    public static boolean connect(String ipAddress, int portNumber) throws Exception {
        socket = new Socket(ipAddress, portNumber);
        output = new PrintWriter(socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return true;
    }

    public static boolean isConnected() {
        return socket != null;
    }

    private static void throwIfNotConnected() throws Exception {
        if (!isConnected()) {
            throw new Exception("You are not connected to the server");
        }
    }

    private static void sendDisconnectToServer() throws Exception {
        throwIfNotConnected();
        output.println(); // sends an empty line, signals disconnect
    }

    public static void disconnect() throws Exception {
        throwIfNotConnected();
        sendDisconnectToServer();
        socket.close();
    }

    /* Methods for interacting with the Synonym Protocol */

    public static void get(String getWord) {
        System.out.println("Get word: " + getWord);
    }

    public static void set(String word1, String word2) {
        System.out.println("Set Synonyms: " + word1 + " is a synonym for " + word2);
    }

    public static void remove(String removeWord) {
        System.out.println("Remove word: " + removeWord);
    }

}
