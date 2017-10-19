/* *******************
 * This controls the operation of the Game.
 * You may want to make some changes here,
 * in order to do different numbers of runs, etc.
 *
 * @author M. Allen
 * ******************
 */
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;

@SuppressWarnings( "serial" )
public class PlayGame extends JPanel implements ActionListener
{
    // Following controls number of steps & games for learning.
    // Can be made smaller for testing your code, but:
    // 1. learningSteps should be AT LEAST 2500
    // 2. numRuns is going to need to be quite high
    // 3. Both variables must use same values for all tests when comparing
    // different
    // methods, like Random, Greedy, different Learning Methods, etc.
    private final int learningSteps = 600000;
    private final int numRuns = 100;
    
    // This controls the speed of the game. Set it to a low value for faster
    // game, and high value for slower game. (When learning, make it fast.)
    private final int speed = 0;
    
    // Creates the board
    private Board board = new Board();
    
    // Different types of agent are possible:
    // [a] Random actions
    // [b] Greedy (no-learning) policy
    // [c] Learning agent
    private Agent agent = new Agent( board.getRows(), board.getColumns(), Agent.FIXED_AGENT );
    
    // These control basic features of the app.
    // You probably don't want to change them much
    private boolean gameOver = false;
    private int leftToPlay = learningSteps;
    private int score = learningSteps;
    private int gamesPlayed = 1;
    private boolean learning = false;
    private volatile boolean interrupt = false;
    private JButton reset = new JButton( "Reset" );
    private JButton quit = new JButton( "Quit" );
    private JButton random = new JButton( "Random" );
    private JButton fixed = new JButton( "Fixed" );
    private JButton learn = new JButton( "Learn" );
    private BufferedWriter fout;
    private String fileName = "testData.txt";
    
    /**
     * Basic constructor
     *
     * DO NOT CHANGE.
     */
    public PlayGame()
    {
        setFocusable( true );
        setBackground( Color.black );
        
        // add the buttons for user actions
        setLayout( null );
        reset.setBounds( 720, 10, 80, 25 );
        add( reset );
        reset.addActionListener( this );
        quit.setBounds( 720, 40, 80, 25 );
        add( quit );
        quit.addActionListener( this );
        random.setBounds( 720, 120, 80, 25 );
        add( random );
        random.addActionListener( this );
        fixed.setBounds( 720, 150, 80, 25 );
        add( fixed );
        fixed.addActionListener( this );
        learn.setBounds( 720, 180, 80, 25 );
        add( learn );
        learn.addActionListener( this );
    }
    
    /**
     * Starts the learning process.
     *
     * DO NOT CHANGE.
     */
    public void learnGame()
    {
        learning = true;
        interrupt = false;
        score = learningSteps;
        leftToPlay = learningSteps;
        gameOver = false;
        gamesPlayed = 1;
        board = new Board();
        
        // robin-xue fixed here, render UI
        paintImmediately( 0, 0, 800, 600 );
        
        // we call the learning routine in
        // a separate thread (allows us to interrupt
        // it by hitting "Reset" if we want)
        LearnerThread lrnThrd = new LearnerThread();
        lrnThrd.start();
    }
    
    // When learning, gets action from agent, takes it.
    // Actions are pairs of numbers (x,y):
    // x says how many squares to the right to
    // move block (0-5), and y says how many times
    // to rotate it (0-3) before dropping it.
    //
    // Returns a reward value (see getDropReward() method).
    private double takeAction( int[] act )
    {
//    	long start = System.currentTimeMillis();
        // reward to return
        double r = 0.0;
        
        // move the block to the right
        for ( int i = 0; i < act[0]; i++ )
        {
            board.moveRight();
			// robin-xue fixed here, render UI
            paintImmediately( 0, 0, 800, 600 );
            stall( speed );
        }
        // rotate it
        for ( int i = 0; i < act[1]; i++ )
            board.rotateBlock();
        // robin-xue fixed here, render UI
        paintImmediately( 0, 0, 800, 600 );
        
        // drops the block, returns the reward it gets
        r = getDropReward();
//        System.out.println("takeAction use"
//				+ (System.currentTimeMillis() - start));
        return r;
    }
    
    // drops the block when learning
    // (ADDS IN ROOM FOR REWARD FUNCTION FOR LEARNER)
    private double getDropReward()
    {
        // R-value to return
        double reward = 0.0;
        
        // gets value from board to figure out
        // how deep it can drop it, then
        // re-draws board (DON'T CHANGE THIS)
        int deep = board.dropBlock();
        animateDrop( ( deep + 4 ), 10 );
        board.reconfigure();
        // robin-xue fixed here, render UI
        paintImmediately( 0, 0, 800, 600 );
        
        // this is called whenever we have actually
        // filled up a row, so it can be trimmed out
        // (the good thing in Tetris)
        if ( board.findFullRows() )
        {
        	// robin-xue fixed here, render UI
            paintImmediately( 0, 0, 800, 600 );
            stall( speed * 2 );
            board.trimRows();
            
            // robin-xue fixed here
            // Add positive reward when found full row or rows
            // Currently give reward as 100
            reward = 100;
            return reward;
        }
        
        // here, if losePoints is greater than 0,
        // means that our stack is too high, and will
        // be pushed down (so final score is reduced)
        int losePoints = board.getOverHeight();
        if ( losePoints > 0 )
        {
            score -= losePoints;
            board.pushDownRows( losePoints );
            // robin-xue fixed here, render UI
            paintImmediately( 0, 0, 800, 600 );
            
            // robin-xue fixed here
            // Add negative reward when make stack higher
            // Currently give reward as -100
            reward = -100;
            return reward;
        }
        
        // counts down # of blocks left to play
        // (DON'T CHANGE)
        leftToPlay-- ;
        gameOver = ( leftToPlay <= 0 );
        if ( gameOver )
            score -= board.getHeight();
        
        // returns the reward for whatever happened
        // when the block was dropped
        // (NOTE: AS WRITTEN, JUST RETURNS 0 EVERY TIME!!)
        reward= -1;
        return reward;
    }
    
    // this creates the separate thread for learning routines
    // (allows us to hit "Reset" and interrupt
    // DO NOT MAKE MAJOR CHANGES TO THIS
    private class LearnerThread extends Thread
    {
        
        public void run()
        {
            // NOTE: whenever learning is called, this will write
            // the results (final score) to a text file. If you run it again,
            // it will WRITE OVER THE FILE! Don't forget to change
            // the name of the file if you want to save your results!
            try
            {
                fout = new BufferedWriter( new FileWriter( fileName ) );
                fout.write( "Total Blocks: " + learningSteps );
                fout.newLine();
                fout.newLine();
            }
            catch ( IOException e )
            {
                System.err.println( "ERROR:  File problem: " + e );
            }
            
            for ( int r = 0; r < numRuns; r++ )
            {
            	// robin-xue fixed here
            	// p(taking action by random) = Learning rate
            	// Learning rate = 1.0 / (numberOfRuns + 1), which means
            	// The more times we run, p(taking action by random) goes down
            	// The more times we run, p(taking action by learned experience) goes up
            	double d = 1.0;
            	agent.setE(d/(r+1));
                score = learningSteps;
                leftToPlay = learningSteps;
                gameOver = false;
                board = new Board();
                // robin-xue fixed here, render UI
                paintImmediately( 0, 0, 800, 600 );
                
                for ( int s = 0; s < learningSteps; s++ )
                {
                    if ( interrupt )
                        break;
                    
                    agent.getFullState( board );
                    
//                    long start = System.currentTimeMillis();
                    double reward = takeAction( agent.chooseAction() );
//                    System.out.println("takeAction use" +(System.currentTimeMillis() - start));
                    
                    agent.getReward( reward );
                }
                
                if ( interrupt )
                    break;
                else
                {
                    gameOver = true;
                    // robin-xue fixed here, render UI
                    paintImmediately( 0, 0, 800, 600 );
                    stall( 1500 );
                    try
                    {
                    	System.out.println(gamesPlayed + " " + score);
                        fout.write( gamesPlayed + " " + score );
                        fout.newLine();
                    }
                    catch ( IOException e )
                    {
                        System.err.println( "Error writing data: " + e );
                    }
                    gamesPlayed++ ;
                }
            }
            
            if ( interrupt )
                resetGame();
            else
                paintImmediately( 0, 0, 800, 600 );
            
            learning = false;
            gamesPlayed = 1;
            try
            {
                fout.close();
            }
            catch ( IOException e )
            {
                System.err.println( "Error closing file: " + e );
            }
            // robin-xue fixed here, render UI
            paintImmediately( 0, 0, 800, 600 );
            
            //robin-xue add
//            agent.storeFile("storeData_learn.txt");
        }
    }
    
    // resets the game
    // DO NOT MAKE MAJOR CHANGES
    private void resetGame()
    {
        score = learningSteps;
        leftToPlay = learningSteps;
        gameOver = false;
        learning = false;
        interrupt = false;
        board = new Board();
        this.requestFocusInWindow();
        repaint();
    }
    
    // Ignore key which is held down.
    public void keyPressed( KeyEvent e )
    {
    }
    
    // Ignore key release events.
    public void keyReleased( KeyEvent e )
    {
    }
    
    // this runs the buttons on screen
    // DO NOT CHANGE
    public void actionPerformed( ActionEvent e )
    {
        
        // will ignore all the buttons if learning
        // except for "Reset"
        if ( learning )
        {
            if ( e.getSource() == reset )
                interrupt = true;
        }
        else
        {
            if ( e.getSource() == reset )
            {
                resetGame();
                this.requestFocusInWindow();
                paintImmediately( 0, 0, 800, 600 );
            }
            
            if ( e.getSource() == quit )
                System.exit( 0 );
            
            if ( e.getSource() == learn )
            {
                agent.setType( Agent.LEARNING_AGENT );
                fileName = "testData_learn.txt";
                learnGame();
                this.requestFocusInWindow();
                paintImmediately( 0, 0, 800, 600 );
            }
            
            if ( e.getSource() == random )
            {
                agent.setType( Agent.RANDOM_AGENT );
                fileName = "testData_random.txt";
                learnGame();
                this.requestFocusInWindow();
                paintImmediately( 0, 0, 800, 600 );
            }
            
            if ( e.getSource() == fixed )
            {
                agent.setType( Agent.FIXED_AGENT );
                fileName = "testData_fixed.txt";
                learnGame();
                this.requestFocusInWindow();
                paintImmediately( 0, 0, 800, 600 );
            }
        }
    }
    
    // called to draw the game (DON'T CHANGE MUCH)
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        
        if ( learning )
        {
            g.setColor( Color.yellow );
            g.drawString( "GAME " + gamesPlayed + " of " + numRuns, 200, 200 );
            g.drawString( "   LEFT: " + leftToPlay, 200, 220 );
            g.drawString( "SCORE: " + score, 200, 240 );
            g.drawString( "RESET TO EXIT", 200, 280 );
            board.paint( g );
        }
        // The body of the else paints the screen at each time step of the game.
        else
        {
            g.setColor( Color.yellow );
            g.drawString( "   LEFT: " + leftToPlay, 200, 220 );
            g.drawString( "SCORE: " + score, 200, 240 );
            board.paint( g );
        }
    }
    
    // basic animation for dropping blocks
    private void animateDrop( int deep, int speed )
    {
        for ( int i = 0; i < deep; i++ )
        {
            board.moveCurrentBlockDown();
            // robin-xue fixed here, render UI
            paintImmediately( 0, 0, 800, 600 );
            stall( speed );
        }
    }
    
    // timing function: slows down game-play
    private void stall( int length )
    {
        long start = System.currentTimeMillis();
        long elapse = 0;  // total time elapsed
        while ( elapse < length )
        {
            elapse = System.currentTimeMillis() - start;
        }
        
    }
    
    public Agent getAgent()
    {
    	return this.agent;
    }
    public void setFileName(String name)
    {
		fileName = name;
    }
}