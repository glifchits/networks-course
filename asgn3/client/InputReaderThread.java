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
public class InputReaderThread implements Runnable {

    private BufferedReader reader;
    private Logger log;

    public InputReaderThread(BufferedReader reader, Logger log) {
        this.reader = reader;
        this.log = log;
    }

    public void run() {
        log.debug("Socket reader thread is running");
        boolean shouldKeepReading = true;
        while (shouldKeepReading) {
            try {
                log.debug("waiting for a line...");
                String line = this.reader.readLine();
                log.debug("Input read a line: " + line);
            } catch (SocketException e) {
                log.debug("Socket exception caught -- stopping reader");
                // the socket was closed.
                // we terminate the while loop, which ends the thread
                shouldKeepReading = false;
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }

}
