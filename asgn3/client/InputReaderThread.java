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

    final static String PROTOCOL = "PaintProtocol/1.0";
    final static String CONTENT_TYPE = "Content-Type:";
    final static String POINT = "point";
    final static int GET = 200;
    final static int POST = 201;
    final static String CRLF = "\r\n";
    private enum Response {
        GET, POST
    }
    private Response currentResponse;
    private BufferedReader reader;
    private Logger log;

    public InputReaderThread(BufferedReader reader, Logger log) {
        this.reader = reader;
        this.log = log;
        this.currentResponse = null;
    }

    public void run() {
        log.debug("Socket reader thread is running");
        boolean shouldKeepReading = true;
        while (shouldKeepReading) {
            try {
                String line = this.reader.readLine();
                handleResponse(line);
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

    private void handleResponse(String line) {
        StringTokenizer tokens = new StringTokenizer(line);
        if (tokens.countTokens() == 0) {
            // empty line, pass
            return;
        }
        int numTokens = tokens.countTokens();
        String firstToken = tokens.nextToken();
        log.debug("first token " + firstToken);
        log.debug("line " + line);
        log.debug("count tokens " + numTokens);
        if (firstToken.compareTo(PROTOCOL) == 0) {
            int statusCode = Integer.parseInt(tokens.nextToken());
            if (statusCode == GET) {
                currentResponse = Response.GET;
            } else if (statusCode == POST) {
                currentResponse = Response.POST;
            } else {
                // TODO others
                currentResponse = null;
            }
        }
        else if (numTokens == 2 && firstToken.compareTo(CONTENT_TYPE) == 0) {
            log.debug("handling content type");
            return;
            // pass
        } else if ((currentResponse == Response.GET || currentResponse == Response.POST)
                    && firstToken.compareTo(POINT) == 0) {
            int x = Integer.parseInt(tokens.nextToken());
            int y = Integer.parseInt(tokens.nextToken());
            String rgbString = tokens.nextToken();
            StringTokenizer rgb = new StringTokenizer(rgbString, ":");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            ColouredPoint point = new ColouredPoint(x, y, r, g, b);
            log.debug("point " + point.format());
        } else {
            log.debug("unprocessed line");
            log.debug(" - current response "+currentResponse);
            log.debug(" - " + line);
        }
    }

}
