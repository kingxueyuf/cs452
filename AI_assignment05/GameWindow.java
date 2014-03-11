/**
 * Basic window code for game.
 *
 * DO NOT CHANGE THIS.
 *
 * @author M. Allen
 */
import java.awt.*;
import javax.swing.*;

@SuppressWarnings( "serial" )
public class GameWindow extends JFrame
{
    /**
     * Initiates the window.
     */
    public GameWindow()
    {
        super( "Li'l Tetris 2013" );
    }
    
    /**
     * Adds a JPanel to the window.
     *
     * @param p JPanel to be added to window.
     */
    public void addPanel( JPanel p )
    {
        p.setPreferredSize( new Dimension( 800, 600 ) );
        getContentPane().add( p );
    }
    
    /**
     * Displays contents of window.
     */
    public void showFrame()
    {
        this.pack();
        this.setVisible( true );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}