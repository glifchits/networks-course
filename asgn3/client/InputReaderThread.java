import java.io.*;
import java.net.*;
import java.awt.Color;
import java.awt.Point;
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
    final static String SET_COLOR = "color";
    final static int SUCCESS = 200;
    final static int POST = 201;
    final static String CRLF = "\r\n";
    private enum Response {
        GET, POST, DELETE
    }
    private Response currentResponse;
    private BufferedReader reader;
    private Logger log;
    private PaintPanel panel;

    public InputReaderThread(BufferedReader reader, Logger log, PaintPanel p) {
        this.reader = reader;
        this.log = log;
        this.currentResponse = null;
        this.panel = p;
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
        log.debug("[line] " + line);
        log.debug("[first token] " + firstToken);
        log.debug("[count tokens] " + numTokens);
        log.debug("[current response] " + currentResponse);
        if (firstToken.compareTo(PROTOCOL) == 0) {
            int statusCode = Integer.parseInt(tokens.nextToken());
            String message = tokens.nextToken();
            log.debug("status code " + statusCode + ", message " + message);
            if (statusCode == SUCCESS) {
                if (message.compareTo("Successful") == 0) {
                    log.debug("setting current response: GET");
                    currentResponse = Response.GET;
                } else if (message.compareTo("DELETED") == 0) {
                    log.debug("setting current response: DELETE");
                    currentResponse = Response.DELETE;
                }
            } else if (statusCode == POST) {
                log.debug("setting current response: POST");
                currentResponse = Response.POST;
            } else {
                // TODO others
                log.debug("setting current response: null");
                currentResponse = null;
            }
        }
        else if (numTokens == 2 && firstToken.compareTo(CONTENT_TYPE) == 0) {
            log.debug("handling content type");
            return;
            // pass
        } else if ((currentResponse == Response.GET || currentResponse == Response.POST)
                    && firstToken.compareTo(POINT) == 0) {
            log.debug("adding point to panel, currentResponse is " + currentResponse);
            int x = Integer.parseInt(tokens.nextToken());
            int y = Integer.parseInt(tokens.nextToken());
            String rgbString = tokens.nextToken();
            StringTokenizer rgb = new StringTokenizer(rgbString, ":");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            ColouredPoint point = new ColouredPoint(x, y, r, g, b);
            log.debug("point " + point.format());
            if (this.panel != null) {
                this.panel.addPointToPanel(point);
            }
        } else if (currentResponse == Response.DELETE && firstToken.compareTo(POINT) == 0) {
            log.debug("deleting point from panel");
            int x = Integer.parseInt(tokens.nextToken());
            int y = Integer.parseInt(tokens.nextToken());
            Point point = new Point(x, y);
            log.debug("delete point " + point.x + " " + point.y);
            if (this.panel != null) {
                this.panel.deletePointFromPanel(point);
            }
        } else if (firstToken.compareTo(SET_COLOR) == 0) {
            log.debug("setting the colour of this connected client");
            StringTokenizer rgb = new StringTokenizer(tokens.nextToken(), ":");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            Color col = new Color(r, g, b);
            if (this.panel != null) {
                this.panel.setClientColor(col);
            }
        } else {
            log.debug("unprocessed line");
            log.debug(" - current response "+currentResponse);
            log.debug(" - " + line);
        }
    }

}
