/**
 * Basic filled squares, for graphics purposes mostly.
 *
 * DO NOT CHANGE THIS CODE.
 */
import java.awt.Color;
import java.awt.Graphics;

public class Blocklet
{
    // x and y coordinates of the bottom left corner, & color of object.
    private int x;
    private int y;
    private Color col;
    
    /**
     * Create a new object at given position, with given color.
     *
     * @param xPos x-coordinate of bottom left corner.
     * @param yPos y-coordinate of bottom left corner.
     * @param c Color of object.
     */
    public Blocklet( int xPos, int yPos, Color c )
    {
        x = xPos;
        y = yPos;
        col = c;
    }
    
    /**
     * Set x-coordinate of bottom left corner.
     *
     * @param xPos x-coordinate of bottom left corner.
     */
    public void setX( int xPos )
    {
        x = xPos;
    }
    
    /**
     * Set y-coordinate of bottom left corner.
     *
     * @param yPos y-coordinate of bottom left corner.
     */
    public void setY( int yPos )
    {
        y = yPos;
    }
    
    /**
     * Draw the object.
     *
     * @param g Graphics drawing object.
     */
    public void paint( Graphics g )
    {
        g.setColor( this.col );
        g.fillRect( this.x + 1, this.y - 48, 49, 49 );
    }
}