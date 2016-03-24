
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

public class PaintPanel extends JPanel {

    private int pointCount = 0; // count number of points

    // array of 10000 java.awt.Point references
    private Point points[] = new Point[ 10000 ];

    private PaintClient client;

    // set up GUI and register mouse event handler
    public PaintPanel(PaintClient client) {
        this.client = client;
        this.addMouseMotionListener(
            new MouseMotionAdapter() {
                // store drag coordinates and repaint
                public void mouseDragged( MouseEvent event ) {
                    if ( pointCount < points.length )  {
                        Point point = event.getPoint();
                        points[ pointCount ] = point;
                        client.submitPoint(point.x, point.y, 10, 20, 255);
                        pointCount++;
                        repaint();
                    }
                }
            }
        );
    }

    // draw oval in a 4-by-4 bounding box at specified location on window
    public void paintComponent( Graphics g ) {
        super.paintComponent( g ); // clears drawing area

        // draw all points in array
        for ( int i = 0; i < pointCount; i++ ) {
            g.fillOval( points[ i ].x, points[ i ].y, 4, 4 );
        }
    }
}
