/**
 * This handles a lot of the drawing and
 * action on the board of the game.
 *
 * SHOULD NOT BE CHANGED IN ANY SIGNIFICANT WAY.
 *
 * @author M. Allen
 */
import java.awt.*;

public class Board
{
    // basic features: 2 blocks,
    // & grid where they can be dropped
    private Block nextBlock;
    private Block currentBlock;
    private int[][] grid;
    
    // these control size and layout of board
    // (REALLY DON'T WANT TO CHANGE THIS)
    private final int activeRows = 3;
    private final int totalRows = 5;
    private final int numColumns = 6;
    private final int emptyValue = 0;
    private final int eraseValue = 99;
    
    /**
     * Basic constructor. Initializes board with two random blocks, and
     * emptyValue
     * main playing area.
     */
    public Board()
    {
        int nbType = (int) ( Math.random() * 5 ) + 1;
        int cbType = (int) ( Math.random() * 5 ) + 1;
        nextBlock = new Block( nbType, 200, 149 );
        currentBlock = new Block( cbType, 350, 149 );
        grid = new int[totalRows][numColumns];
    }
    
    /**
     * @return Total number of rows of active playing area.
     */
    public int getRows()
    {
        return grid.length;
    }
    
    /**
     * @return Number of columns of active playing area.
     */
    public int getColumns()
    {
        return grid[0].length;
    }
    
    /**
     * Gives number of rows that actually have a block currently in them.
     *
     * @return Height of highest stack of blocks.
     */
    public int getHeight()
    {
        for ( int row = 0; row < totalRows; row++ )
            for ( int col = 0; col < numColumns; col++ )
                if ( grid[row][col] != emptyValue )
                    return totalRows - row;
        
        return 0;
    }
    
    /**
     * Moves the current block left.
     */
    public void moveLeft()
    {
        if ( ( currentBlock.getWidth() == 2 ) && ( currentBlock.getX() >= 400 ) )
            currentBlock.setX( currentBlock.getX() - 50 );
        else if ( currentBlock.getWidth() == 1 )
        {
            if ( currentBlock.getX() >= 400 )
                currentBlock.setX( currentBlock.getX() - 50 );
            else if ( currentBlock.getX() >= 350 )
                currentBlock.shiftLeft();
        }
    }
    
    /**
     * Moves the current block right.
     */
    public void moveRight()
    {
        if ( ( currentBlock.getWidth() == 2 ) && ( currentBlock.getX() <= 500 ) )
            currentBlock.setX( currentBlock.getX() + 50 );
        else if ( currentBlock.getWidth() == 1 )
        {
            if ( currentBlock.getX() <= 500 )
                currentBlock.setX( currentBlock.getX() + 50 );
            else if ( currentBlock.getX() <= 550 )
                currentBlock.shiftRight();
        }
    }
    
    /**
     * Rotate the current playable block.
     */
    public void rotateBlock()
    {
        currentBlock.rotate();
    }
    
    /**
     * Drops the current block.
     *
     * @return Number of rows we can drop the current playable block.
     */
    public int dropBlock()
    {
        // find where the bottom blocklet in the block is (or the left-hand one,
        // if it's two blocklets wide)
        boolean twoWide = false;
        int side = 0;
        if ( currentBlock.getPart( 1, 0 ) == emptyValue )
            side = ( currentBlock.getX() - 300 ) / 50;
        else if ( currentBlock.getPart( 1, 1 ) == emptyValue )
            side = ( currentBlock.getX() - 350 ) / 50;
        else
        {
            side = ( currentBlock.getX() - 350 ) / 50;
            twoWide = true;
        }
        
        // see if there is emptyValue space for the block
        int deep = 1;
        boolean hole = true;
        if ( twoWide )
        {
            while ( hole )
            {
                if ( ( deep < 4 ) && ( grid[deep + 1][side] == emptyValue ) &&
                    ( grid[deep + 1][side + 1] == emptyValue ) )
                    deep++ ;
                else
                    hole = false;
            }
        }
        else if ( ( currentBlock.getType() == 2 ) ||
                 ( currentBlock.getType() == 3 ) )
        {
            boolean left = ( currentBlock.getPart( 1, 0 ) != emptyValue );
            if ( left )
            {
                while ( hole )
                {
                    if ( ( deep < 4 ) &&
                        ( grid[deep + 1][side] == emptyValue ) &&
                        ( grid[deep][side + 1] == emptyValue ) )
                        deep++ ;
                    else
                        hole = false;
                }
            }
            else
            {
                while ( hole )
                {
                    if ( ( deep < 4 ) &&
                        ( grid[deep][side - 1] == emptyValue ) &&
                        ( grid[deep + 1][side] == emptyValue ) )
                        deep++ ;
                    else
                        hole = false;
                }
            }
        }
        else
        {
            while ( hole )
            {
                if ( ( deep < 4 ) && ( grid[deep + 1][side] == emptyValue ) )
                    deep++ ;
                else
                    hole = false;
            }
        }
        return deep;
    }
    
    /**
     * Updates location of dropping block.
     */
    public void moveCurrentBlockDown()
    {
        currentBlock.setY( currentBlock.getY() + 50 );
    }
    
    /**
     * Adds dropped block to grid, and gets next blocks.
     */
    public void reconfigure()
    {
        // switch to new block
        Block tempBlk = currentBlock;
        currentBlock = nextBlock;
        currentBlock.setX( 350 );
        currentBlock.setY( 149 );
        int nbType = (int) ( Math.random() * 5 ) + 1;
        nextBlock = new Block( nbType, 200, 149 );
        
        // now figure out what new board looks like
        int baseRow = 4 - ( ( 550 - tempBlk.getY() ) / 50 );
        int leftIndex = ( tempBlk.getX() - 350 ) / 50;
        grid[baseRow][leftIndex] += tempBlk.getPart( 1, 0 );
        grid[baseRow][leftIndex + 1] += tempBlk.getPart( 1, 1 );
        grid[baseRow - 1][leftIndex] += tempBlk.getPart( 0, 0 );
        grid[baseRow - 1][leftIndex + 1] += tempBlk.getPart( 0, 1 );
    }
    
    /**
     * Finds any complete rows, marks them by turning them white, before they
     * are eliminated.
     *
     * @return true if and only if there are any full rows to eliminate.
     */
    public boolean findFullRows()
    {
        boolean full = false;
        
        for ( int row = 0; row < totalRows; row++ )
        {
            boolean thisRowFull = true;
            
            for ( int col = 0; col < numColumns; col++ )
                if ( grid[row][col] == emptyValue )
                    thisRowFull = false;
            
            if ( thisRowFull )
            {
                full = true;
                for ( int j = 0; j < numColumns; j++ )
                    grid[row][j] = eraseValue;
            }
        }
        return full;
    }
    
    /**
     * Eliminate any complete rows.
     */
    public void trimRows()
    {
        for ( int i = 0; i < totalRows; i++ )
            if ( grid[i][0] == eraseValue )
            {
                
                for ( int j = i; j >= 0; j-- )
                    if ( j > 0 )
                        for ( int k = 0; k < numColumns; k++ )
                            grid[j][k] = grid[j - 1][k];
                    else
                        for ( int k = 0; k < numColumns; k++ )
                            grid[j][k] = emptyValue;
            }
    }
    
    /**
     * @return Number of rows that are over-height (between 0 and 2).
     */
    public int getOverHeight()
    {
        int height = 0;
        
        // check top two rows to see if emptyValue
        for ( int row = 0; row < ( totalRows - activeRows ); row++ )
        {
            boolean full = false;
            for ( int col = 0; col < numColumns; col++ )
                if ( grid[row][col] != emptyValue )
                    full = true;
            if ( full )
                height++ ;
        }
        return height;
    }
    
    /**
     * Eliminate any over-height rows by pushing them down (a bad thing).
     *
     * @param tooHigh Number of rows that must be pushed down.
     */
    public void pushDownRows( int tooHigh )
    {
        for ( int i = totalRows - 1; i >= totalRows - activeRows; i-- )
            for ( int j = 0; j < numColumns; j++ )
                grid[i][j] = grid[i - tooHigh][j];
        
        for ( int i = 0; i < ( totalRows - activeRows ); i++ )
            for ( int j = 0; j < numColumns; j++ )
                grid[i][j] = emptyValue;
    }
    
    /**
     * Copies current playable block.
     *
     * @return A copy of current playable block.
     */
    public Block copyCurrentBlock()
    {
        return currentBlock.clone();
    }
    
    /**
     * Copies next playable block.
     * 
     * @return A copy of next playable block.
     */
    public Block copyNextBlock()
    {
        return nextBlock.clone();
    }
    
    /**
     * Copies current board state.
     * 
     * @return Copy of block values in current board.
     */
    public int[][] copyBoard()
    {
        int[][] copy = new int[totalRows][numColumns];
        
        for ( int r = 0; r < totalRows; r++ )
            for ( int c = 0; c < numColumns; c++ )
                copy[r][c] = grid[r][c];
        
        return copy;
    }
    
    /**
     * Draw the board.
     */
    public void paint( Graphics g )
    {
        // draws the walls
        g.setColor( Color.lightGray );
        g.fillRect( 349, 551, 302, 5 );
        g.fillRect( 345, 50, 5, 506 );
        g.fillRect( 651, 50, 5, 506 );
        
        // draws the grid contents
        Blocklet b;
        Color clr;
        for ( int r = 0; r < totalRows; r++ )
            for ( int c = 0; c < numColumns; c++ )
            {
                switch ( grid[r][c] )
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
                    case eraseValue:
                        clr = Color.white;
                        break;
                    default:
                        clr = Color.black;
                        grid[r][c] = 0;
                        break;
                }
                if ( grid[r][c] != emptyValue )
                {
                    b = new Blocklet( 350 + ( 50 * c ), 350 + ( 50 * r ), clr );
                    b.paint( g );
                }
            }
        // draw the current blocks
        g.setColor( Color.red );
        g.drawString( "NEXT:  ", 150, 149 );
        nextBlock.paint( g );
        currentBlock.paint( g );
    }
}