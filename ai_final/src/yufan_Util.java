import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class yufan_Util {

	private static Random generator = new Random();
	private static double r = 0.9;
	private final static int transitedStableNum = 1;

	public static double random() {
		double num = generator.nextDouble();
		return num;
	}

	public static yufan_State learnState(Block currentBlock, int[][] board, HashMap<String, yufan_State> mdp,
			HashMap<String, ArrayList<String>> s_saMap) {

		Block transitedBlock = transitBlock(currentBlock);
		int[][] transitedBoard = transitBoard(board);

		String blockS = getBlockString(transitedBlock);
		String boardS = getBoardString(transitedBoard);

		String key = boardS + blockS;

		yufan_State state = mdp.get(key);

		/*
		 * if has existed this state just return the state itself
		 */
		if (state != null) {
			return state;
		}
		/*
		 * else add this new state into mdp
		 */
		state = new yufan_State(transitedBlock, transitedBoard, key);
		mdp.put(key, state);

		s_saMap.put(key, new ArrayList<String>());
		return state;
	}

	private static String getBoardString(int[][] transitedBoard) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < transitedBoard.length; i++) {
			for (int j = 0; j < transitedBoard[0].length; j++) {
				sb.append(transitedBoard[i][j]);

			}
		}
		return sb.toString();
	}

	private static String getBlockString(Block transitedBlock) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				sb.append(transitedBlock.getPart(i, j));
			}
		}
		return sb.toString();
	}

	public static yufan_StateActionPair learnStateAction(yufan_State currentS, int[] act,
			HashMap<String, yufan_StateActionPair> pair, HashMap<String, ArrayList<String>> s_saMap) {
		String stateKey = currentS.getBoardBlockS();

		String actS = getActString(act);

		String key = stateKey + actS;

		yufan_StateActionPair sa = pair.get(key);

		/*
		 * if this stateAction has existed just return it self
		 */
		if (sa != null) {
			return sa;
		}
		/*
		 * else add this new stateAction into mdp
		 */
		sa = new yufan_StateActionPair();
		sa.setAct(act);
		sa.setS(currentS);
		sa.setKey(key);
		pair.put(key, sa);

		s_saMap.get(stateKey).add(key);
		return sa;
	}

	private static String getActString(int[] act) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < act.length; i++) {
			sb.append(act[i]);
		}
		return sb.toString();
	}

	public static boolean equalAct(int[] act1, int[] act2) {
		for (int i = 0; i < act1.length; i++) {
			if (act1[i] != act2[i])
				return false;
		}
		return true;
	}

	public static boolean equalState(yufan_State s1, yufan_State s2) {
		return s1.equalState(s2);
	}

	public static void updateLastStateActionU(yufan_StateActionPair lastStateAction, Double currentQ,
			HashMap<yufan_StateActionPair, Double> Q_Map, double lastReward, double e) {
		Double lastQ = Q_Map.get(lastStateAction);

		lastQ = lastQ + e * (lastReward + r * currentQ - lastQ);

		Q_Map.put(lastStateAction, lastQ);
	}

	public static int[] actGreedy(yufan_State currentState, HashMap<yufan_StateActionPair, Double> Q_Map,
			HashMap<String, ArrayList<String>> s_saMap, HashMap<String, yufan_StateActionPair> pair) {
		Double maxU = -999999.9;
		int[] bestAct = null;

		ArrayList<String> sa_key_set = s_saMap.get(currentState.getBoardBlockS());
		for (int i = 0; i < sa_key_set.size(); i++) {
			String sa_key = sa_key_set.get(i);
			yufan_StateActionPair sa = pair.get(sa_key);
			if (Q_Map.get(sa).doubleValue() - maxU.doubleValue() > 0.01) {
				bestAct = sa.getAct();
				maxU = Q_Map.get(sa).doubleValue();
			}
		}

		return bestAct;
	}

	private static int[][] transitBoard(int[][] board2) {
		int[][] transitedBoard = new int[board2.length][board2[0].length];
		for (int i = 0; i < board2.length; i++) {
			for (int j = 0; j < board2[i].length; j++) {
				transitedBoard[i][j] = board2[i][j];
				if (transitedBoard[i][j] != 0) {
					transitedBoard[i][j] = transitedStableNum;
				}
			}
		}
		return transitedBoard;
	}

	private static Block transitBlock(Block currentBlock2) {
		Block transitedBlock = new Block(currentBlock2.getType(), currentBlock2.getX(), currentBlock2.getY());
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (transitedBlock.getPart(i, j) != 0) {
					transitedBlock.getPartsArray()[i][j] = transitedStableNum;
				}
			}
		}
		return transitedBlock;
	}
}
