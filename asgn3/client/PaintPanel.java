
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

public class PaintPanel extends JPanel {

    private int pointCount = 0; // count number of points

    // array of 10000 java.awt.Point references
    private ColouredPoint points[] = new ColouredPoint[ 10000 ];

    private PaintClient client;
    private Color clientColor;

    public void addPoint(int x, int y, int r, int g, int b) {
        addPoint(new ColouredPoint(x, y, r, g, b));
    }

    public void addPoint(ColouredPoint point) {
        if (pointCount < points.length) {
            points[pointCount] = point;
            pointCount++;
            repaint();
        }
    }

    public void setClientColor(Color color) {
        this.clientColor = color;
    }

    public Color getClientColor() {
        return this.clientColor;
    }

    // set up GUI and register mouse event handler
    public PaintPanel(PaintClient client) {
        this.client = client;
        this.addMouseMotionListener(
            new MouseMotionAdapter() {
                // store drag coordinates and repaint
                public void mouseDragged( MouseEvent event ) {
                    // these colours are temporary defaults
                    ColouredPoint point = new ColouredPoint(event.getPoint(), getClientColor());
                    client.submitPoint(point);
                    addPoint(point);
                }
            }
        );
    }

    // draw oval in a 4-by-4 bounding box at specified location on window
    public void paintComponent( Graphics g ) {
        super.paintComponent( g ); // clears drawing area

        // draw all points in array
        for ( int i = 0; i < pointCount; i++ ) {
            ColouredPoint point = points[i];
            g.setColor(point.getColor());
            g.fillOval( point.x, point.y, 4, 4 );
        }
    }
}
