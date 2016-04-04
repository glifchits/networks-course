
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javafx.util.Pair;

public class PaintPanel extends JPanel {

    private HashMap<String, ColouredPoint> points = new HashMap<String, ColouredPoint>();

    private PaintClient client;
    private Color clientColor;

    public synchronized void addPointToPanel(ColouredPoint colPoint) {
        String xy = colPoint.x + " " + colPoint.y;
        points.put(xy, colPoint);
        repaint();
    }

    public synchronized void deletePointFromPanel(Point point) {
        String xy = point.x + " " + point.y;
        points.remove(xy);
        repaint();
    }

    public void setClientColor(Color color) {
        this.clientColor = color;
    }

    public Color getClientColor() {
        return this.clientColor;
    }

    private void handleMouseEvent(MouseEvent event) {
        if (client.isConnected()) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                addPoint(event);
            } else if (SwingUtilities.isRightMouseButton(event)) {
                deletePoint(event);
            }
        }
    }

    private void addPoint(MouseEvent event) {
        ColouredPoint point = new ColouredPoint(event.getPoint(), getClientColor());
        client.submitPoint(point);
        addPointToPanel(point);
    }

    private void deletePoint(MouseEvent event) {
        Point point = event.getPoint();
        int DELETE_RADIUS = 3;
        for (int x = -DELETE_RADIUS; x <= DELETE_RADIUS; x++) {
            for (int y = -DELETE_RADIUS; y <= DELETE_RADIUS; y++) {
                Point pt = new Point(point.x + x, point.y + y);
                client.deletePoint(pt);
                deletePointFromPanel(pt);
            }
        }
    }

    // set up GUI and register mouse event handler
    public PaintPanel(PaintClient client) {
        this.client = client;

        this.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent event) {
                    handleMouseEvent(event);
                }
            }
        );

        this.addMouseMotionListener(
            new MouseMotionAdapter() {
                // store drag coordinates and repaint
                public void mouseDragged( MouseEvent event ) {
                    handleMouseEvent(event);
                }
            }
        );
    }

    // draw oval in a 4-by-4 bounding box at specified location on window
    public synchronized void paintComponent( Graphics g ) {
        super.paintComponent( g ); // clears drawing area
        for (ColouredPoint point : this.points.values()) {
            g.setColor(point.getColor());
            g.fillOval( point.x, point.y, 4, 4 );
        }
    }
}
