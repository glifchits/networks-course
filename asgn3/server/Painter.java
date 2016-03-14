package a3;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Painter
{
   public static void main( String args[] )
   { 
      // create JFrame
      JFrame application = new JFrame( "A simple paint program" );

      PaintPanel paintPanel = new PaintPanel(); // create paint panel
      application.getContentPane().add( paintPanel, BorderLayout.CENTER ); // in center
      
      // create a label and place it in SOUTH of BorderLayout
      application.getContentPane().add( new JLabel( "Drag the mouse to draw" ), 
         BorderLayout.SOUTH );

      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      application.setSize( 400, 200 ); // set frame size
      application.setVisible( true ); // display frame
   } // end main
} // end class Painter
