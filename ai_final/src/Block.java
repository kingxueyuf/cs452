/**
 * This creates the different types of blocks.
 *
 * DO NOT CHANGE THIS IN ANY REAL WAY.
 * (You can add some methods to it, but
 * don't mess with the basic code.)
 *
 * @author M. Allen
 */
import java.awt.*;

public class Block
{
    
    // Type of Block (5 possible):
    //
    // TYPE 1: XX          TYPE 2: X
    //         XX (Green)           X (Yellow)
    //
    // TYPE 3:  X          TYPE 4: X
    //         XX (Red)            X (Blue)
    //
    // TYPE 5: X (Pink)
    //
    private int type;
    
    // x and y coordinates of the bottom right-hand corner,
    // & width of the block
    private int x;
    private int y;
    private int width;
    
    // stores the various components of block
    private int[][] parts = new int[2][2];
    
    /**
     * Creates a block at a given location (bottom right corner).
     *
     * @param t
     * @param xPos
     * @param yPos
     */
    public Block( int t, int xPos, int yPos )
    {
        type = t;
        x = xPos;
        y = yPos;
        
        if ( type == 1 )
        {
            parts[0][0] = 1;
            parts[0][1] = 1;
            parts[1][0] = 1;
            parts[1][1] = 1;
            width = 2;
        }
        else if ( type == 2 )
        {
            parts[0][0] = 2;
            parts[0][1] = 0;
            parts[1][0] = 0;
            parts[1][1] = 2;
            width = 2;
        }
        else if ( type == 3 )
        {
            parts[0][0] = 0;
            parts[0][1] = 3;
            parts[1][0] = 3;
            parts[1][1] = 3;
            width = 2;
        }
        else if ( type == 4 )
        {
            parts[0][0] = 4;
            parts[0][1] = 0;
            parts[1][0] = 4;
            parts[1][1] = 0;
            width = 1;
        }
        else if ( type == 5 )
        {
            parts[0][0] = 0;
            parts[0][1] = 0;
            parts[1][0] = 5;
            parts[1][1] = 0;
            width = 1;
        }
        else
        {
            System.err.println( "Block type " + type +
                               " doesn't exist!  ERROR." );
            System.exit( 0 );
        }
    }
    
    /**
     * Returns a copy of this Block.
     */
    public Block clone()
    {
        Block b = new Block( 1, 0, 0 );
        b.width = this.width;
        b.type = this.type;
        for ( int i = 0; i < parts.length; i++ )
            for ( int j = 0; j < parts[i].length; j++ )
                b.parts[i][j] = this.parts[i][j];
        
        return b;
    }
    
    /*
     * Basic get/set functions.
     */
    public int getType()
    {
        return type;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setX( int newX )
    {
        x = newX;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setY( int newY )
    {
        y = newY;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getPart( int i, int j )
    {
        return parts[i][j];
    }
    
    /**
     * Shifts blocks of width 1 to the right (for board edges).
     */
    public void shiftRight()
    {
        if ( this.parts[1][0] != 0 )
        {
            this.parts[0][1] = this.parts[0][0];
            this.parts[0][0] = 0;
            this.parts[1][1] = this.parts[1][0];
            this.parts[1][0] = 0;
        }
    }
    
    /**
     * Shifts blocks of width 1 to the left (for board edges).
     */
    public void shiftLeft()
    {
        if ( this.parts[1][1] != 0 )
        {
            this.parts[0][0] = this.parts[0][1];
            this.parts[0][1] = 0;
            this.parts[1][0] = this.parts[1][1];
            this.parts[1][1] = 0;
        }
    }
    
    /**
     * Rotate blocks 90 degrees to the right.
     */
    public void rotate()
    {
        if ( this.type == 2 )
        {
            if ( this.parts[0][0] == 0 )
            {
                this.parts[0][0] = 2;
                this.parts[0][1] = 0;
                this.parts[1][0] = 0;
                this.parts[1][1] = 2;
            }
            else
            {
                this.parts[0][0] = 0;
                this.parts[0][1] = 2;
                this.parts[1][0] = 2;
                this.parts[1][1] = 0;
            }
        }
        else if ( this.type == 3 )
        {
            if ( this.parts[0][0] == 0 )
            {
                this.parts[0][0] = 3;
                this.parts[0][1] = 0;
            }
            else if ( this.parts[0][1] == 0 )
            {
                this.parts[0][1] = 3;
                this.parts[1][1] = 0;
            }
            else if ( this.parts[1][0] == 0 )
            {
                this.parts[1][0] = 3;
                this.parts[0][0] = 0;
            }
            else
            {
                this.parts[1][1] = 3;
                this.parts[1][0] = 0;
            }
        }
        else if ( this.type == 4 )
        {
            if ( ( this.parts[0][0] == 0 ) && ( this.parts[0][1] == 0 ) )
            {
                this.parts[0][0] = 4;
                this.parts[1][1] = 0;
                this.width = 1;
            }
            else
            {
                this.parts[0][0] = 0;
                this.parts[0][1] = 0;
                this.parts[1][0] = 4;
                this.parts[1][1] = 4;
                this.width = 2;
            }
        }
    }
    
    /**
     * Draw the block.
     * 
     * @param g Graphics drawing element.
     */
    public void paint( Graphics g )
    {
        Blocklet b;
        Color clr;
        for ( int r = 0; r < 2; r++ )
            for ( int c = 0; c < 2; c++ )
            {
                switch ( parts[r][c] )
                {
                    case 1:
                        clr = Color.green;
                        break;
                    case 2:
                        clr = Color.yellow;
                        break;
                    case 3:
                        clr = Color.red;
                        break;
                    case 4:
                        clr = Color.blue;
                        break;
                    case 5:
                        clr = Color.pink;
                        break;
                    default:
                        clr = Color.black;
                        parts[r][c] = 0;
                        break;
                }
                b = new Blocklet( this.x + ( 50 * c ),
                                 this.y - ( 50 * ( 1 - r ) ), clr );
                if ( parts[r][c] != 0 )
                    b.paint( g );
            }
    }
    
    public int[][] getPartsArray()
    {
    	return this.parts;
    }
    
}
