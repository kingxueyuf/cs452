/* *******************
 * A reduced form of Tetris.
 *
 * Player sees 3 rows of the Tetris board, along with a current and next piece.
 * The piece is rotated and then placed, and new pieces arrive.
 * If a row among the three is completed, it disappears.
 * If the highest block goes above the 3 row limit, the bottom row disappears,
 * and the "HEIGHT" counter goes up by one.
 *
 * After a certain number of blocks (randomly and uniformly chosen), the
 * Final Score = (Total Blocks - HEIGHT); that is, we want to minimize total height.
 *
 * @author M. Allen
 */
public class LittleTetris {
	/**
	 * The main() method initiating game.
	 * 
	 * DO NOT CHANGE.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main( String[] args )
    {
//        GameWindow d = new GameWindow();
//        PlayGame p = new PlayGame();
//        d.addPanel( p );
//        p.requestFocusInWindow();
//        d.showFrame();
    
		/*
		 * robin added
		 */
    	PlayGame p = new PlayGame();
        p.getAgent().setType(Agent.LEARNING_AGENT);
        p.setFileName("testData_learn.txt");
        p.learnGame();
    }
}