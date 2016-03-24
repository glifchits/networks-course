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
    private boolean shouldKeepReading;
    private Logger log;

    public InputReaderThread(BufferedReader reader, Logger log) {
        this.shouldKeepReading = true;
        this.reader = reader;
        this.log = log;
        log.debug("reader thread started");
    }

    public void run() {
        log.debug("reader thread: run");
        try {
            this.processInput();
        } catch (Exception e) {
            this.log.error("Error from process");
            this.log.error(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private void processInput() {
        log.debug("process input");
        while (true) {
            try {
                log.debug("waiting for a line...");
                String line = this.reader.readLine();
                log.debug("Input read a line: " + line);
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }

}
