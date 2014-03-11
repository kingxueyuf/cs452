import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the agent that plays the game. Currently does nothing but act
 * randomly or greedily.
 * 
 * @author M. Allen
 * @author YOUR NAME HERE
 */

public class Agent {
	// This allows the agent to record entire state of game board
	// (NOTE: probably a good idea to leave these alone; even
	// if your agent ends up throwing out some of the information,
	// you can handle that later by writing methods to do so).
	private Block currentBlock;
	private Block nextBlock;
	private int[][] board;

	// Agent type: can be used to test different
	// sorts of policies (random, fixed, learning).
	private int type;

	// Constants for different agent types.
	public static final int RANDOM_AGENT = 0;
	public static final int FIXED_AGENT = 1;
	public static final int LEARNING_AGENT = 2;

	/**
	 * Basic constructor (creates space for storing board-state, sets type).
	 * 
	 * @param rows
	 *            Number of rows in board-space.
	 * @param cols
	 *            Number of columns in board-space.
	 * @param t
	 *            Type of agent.
	 */
	public Agent(int rows, int cols, int t) {
		currentBlock = new Block(1, 0, 0);
		nextBlock = new Block(1, 0, 0);
		board = new int[rows][cols];
		type = t;
	}

	/**
	 * Sets type of agent.
	 * 
	 * @param t
	 *            Agent type (will be either random/fixed/learning).
	 */
	public void setType(int t) {
		type = t;
	}

	/**
	 * Agent can see the full state of the board. You may want to change this,
	 * or add other methods, so that it does not keep the entire state (that is,
	 * it may throw out some of this information, or change it, once it gets it
	 * from the Board object).
	 * 
	 * @param b
	 *            The game-board at its current state.
	 */
	public void getFullState(Board b) {
		currentBlock = b.copyCurrentBlock();
		nextBlock = b.copyNextBlock();
		board = b.copyBoard();
	}

	/**
	 * Choose an action in game.
	 * 
	 * @return Pair of integers, where first is number of spaces to move block
	 *         to right, and second is number of times to rotate it before drop.
	 */
	public int[] chooseAction() {
		// acts based on type
		if (type == RANDOM_AGENT)
			return actRandomly();
		else if (type == FIXED_AGENT)
			return actFixedly();
		else
			return actLearnedly();
	}

	/**
	 * For an agent that chooses a random action each time (useful to compare
	 * against a learning agent as a baseline).
	 * 
	 * @return Two integer values: number of squares to move block right, number
	 *         of times to rotate it.
	 */
	public int[] actRandomly() {
		int moveR = 0;
		int rotate = 0;

		// 1. moves the block right some random amount (0-5 steps)
		// 2. randomly rotates (0-3 turns)
		// 3. drops
		if (currentBlock.getWidth() == 2)
			moveR = (int) (Math.random() * 5);
		else
			moveR = (int) (Math.random() * 6);

		rotate = (int) (Math.random() * 4);
		int[] act = { moveR, rotate };
		return act;
	}

	/**
	 * For an agent that chooses a fixed action each time (useful to compare
	 * against a learning agent as a baseline). It tries to place the tile in
	 * the first location it can, as deep as possible.
	 * 
	 * @return Two integer values: number of squares to move block right, number
	 *         of times to rotate it.
	 */
	public int[] actFixedly() {
		int moveR = 0;
		int rotate = 0;
		int left = 0;
		int deep = 1;
		boolean twoWide = false;

		// for the single pink blocks, find the deepest hole
		if (currentBlock.getType() == 5) {
			for (int i = 0; i < board[2].length; i++)
				if (board[2][i] == 0) {
					if (deep == 1)
						left = i;

					// see how deep it is
					for (int j = 1; j < 3; j++) {
						if (board[2 + j][i] == 0) {
							if (j >= deep) {
								left = i;
								deep = j + 1;
							}
						} else
							break;
					}

				}
			moveR = left;
		} else {
			// see if it can find a hole that is
			// at least two squares wide
			for (int i = 0; i < board[2].length - 1; i++) {
				// find a wide enough hole on top row
				if ((board[2][i] == 0) && (board[2][i + 1] == 0)) {
					twoWide = true;
					if (deep == 1) {
						left = i;
					}
					// see how deep it is
					for (int j = 1; j < 3; j++) {
						if ((board[2 + j][i] == 0)
								&& (board[2 + j][i + 1] == 0)) {
							if (j >= deep) {
								left = i;
								deep = j + 1;
							}
						} else
							break;
					}
				}
			}
			// if it can find a wide enough hole, drop it in
			if (twoWide) {
				moveR = left;
				// rotate the skinny blue blocks
				if (currentBlock.getType() == 4)
					rotate = 1;
			} else { // randomly select a move if no wide hole
				moveR = (int) (Math.random() * 5);
				rotate = (int) (Math.random() * 4);
			}
		}

		// output the relevant action
		int[] act = { moveR, rotate };
		return act;
	}

	/**
	 * For an agent that chooses actions by learning.
	 * 
	 * RIGHT NOW THIS IS JUST A RANDOM AGENT: CHANGE THIS.
	 * 
	 * @return Two integer values: number of squares to move block right, number
	 *         of times to rotate it.
	 */
	double E = 0.9;
	ArrayList<yufan_StateActionPair> pair = new ArrayList<yufan_StateActionPair>();
	HashMap<yufan_StateActionPair, Double> uMap = new HashMap<yufan_StateActionPair, Double>();
	ArrayList<yufan_State> mdp = new ArrayList<yufan_State>();

	yufan_State lastState;
	yufan_StateActionPair lastStateAction;

	public int[] actLearnedly() {
		double R = yufan_Util.random();
		int[] act;

		yufan_State currentState = yufan_Util.learnState(this.currentBlock,
				this.board, mdp);
		if (R < E) {
			act = actRandomly();
			// learn this state and action
		} else {
			act = yufan_Util.actGreedy(currentState, uMap);

			if (act == null) {
				// never meet this state before
//				System.out.println("choose Greedy but this is a new state"
//						+ "R = " + R + "E = " + E);
				act = actRandomly();
			} else {
//				System.out.println("choose Greedy this is an old state R = "
//						+ R + "E = " + E);
			}
		}

		yufan_StateActionPair currentStateAction = yufan_Util.learnStateAction(
				currentState, act, pair);

		Double currentU = uMap.get(currentStateAction);

//		System.out.println("Leart StateAction Size=" + uMap.size());

		if (currentU == null) {
			currentU = 0.0;
			uMap.put(currentStateAction, currentU);
		}
//		System.out.println(currentU);

		if (lastStateAction != null) {
			yufan_Util.updateLastStateActionU(lastStateAction, currentU, uMap,
					lastReward,this.E);
		}

		lastStateAction = currentStateAction;
		return act;
	}

	public void setE(double e) {
		this.E = e;

	}

	/**
	 * Process rewards from any actions.
	 * 
	 * @param r
	 *            The reward gained for the last action.
	 */
	private double lastReward;

	public void getReward(double r) {
		// THIS DOES NOTHING; CHANGE THIS.
		this.lastReward = r;
	}

}