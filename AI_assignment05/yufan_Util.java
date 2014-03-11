import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class yufan_Util {

	private static Random generator = new Random();
	private static double r = 0.9;

	public static double random() {
		double num = generator.nextDouble();
		return num;
	}

	public static yufan_State learnState(Block currentBlock, int[][] board,
			ArrayList<yufan_State> mdp) {
		for (int i = 0; i < mdp.size(); i++) {
			yufan_State s0 = mdp.get(i);
			if (s0.equalState(currentBlock, board)) {
				/*
				 * if has existed this state just return the state itself
				 */
				return s0;
			}
		}
		/*
		 * else add this new state into mdp
		 */
		yufan_State s1 = new yufan_State(currentBlock, board);
		mdp.add(s1);
		return s1;
	}

	public static yufan_StateActionPair learnStateAction(yufan_State currentS,
			int[] act, ArrayList<yufan_StateActionPair> pair) {
		// TODO Auto-generated method stub
		for (int i = 0; i < pair.size(); i++) {
			/*
			 * if this stateAction has existed just return it self
			 */
			yufan_StateActionPair sa = pair.get(i);
			if (equalAct(sa.getAct(), act) && equalState(sa.getS(), currentS)) {
				return sa;
			}
		}

		/*
		 * else add this new stateAction into mdp
		 */
		yufan_StateActionPair sa = new yufan_StateActionPair();
		sa.setAct(act);
		sa.setS(currentS);
		pair.add(sa);
		return sa;
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

	public static void updateLastStateActionU(
			yufan_StateActionPair lastStateAction, Double currentU,
			HashMap<yufan_StateActionPair, Double> uMap, double lastReward, double e) {
		// TODO Auto-generated method stub
		Double Q = uMap.get(lastStateAction);

		Q = Q + e*(lastReward + r * currentU - Q);

		uMap.put(lastStateAction, Q);
	}

	public static int[] actGreedy(yufan_State currentState,
			HashMap<yufan_StateActionPair, Double> uMap) {
		// TODO Auto-generated method stub
		Double maxU = -999999.9;
		int[] bestAct = null;
		for (yufan_StateActionPair sa : uMap.keySet()) {
			if (sa.getS().equalState(currentState)) {
				System.out.println("equal state");

				if (uMap.get(sa).doubleValue() - maxU.doubleValue() > 0.01) {
					bestAct = sa.getAct();
					maxU = uMap.get(sa).doubleValue();
				}
			}
		}
		return bestAct;
	}

}
