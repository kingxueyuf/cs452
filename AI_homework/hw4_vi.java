import java.util.Scanner;

/**
 * @author robin-xue
 * @project_name AI_homework
 * @file_name hw4_vi.java
 * @tag 
 * @date 2013-11-24
 */
public class hw4_vi {

	int rowNum;
	int colNum;
	int[][] mazeTable;
	int[][] mazePolicy;
	double[][] mazeUtility;
	private static double deltaMin = 1e-9;
	final static double knockWallReward = -10;
	final static double usualMove = -1;
	final static double gamma = 0.9;

	public static void main(String args[]) {
		hw4_vi vi = new hw4_vi();
		vi.solve();
	}

	public void solve() {
		getInput();
		valueIteration();

	}

	public void valueIteration() {
		double delta = 1;
		do {

			delta = updateU_P();

		} while (delta > deltaMin);
		this.outputRes(this.mazePolicy);
	}

	public double updateU_P() {
		double maxDelta = -999;
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < this.colNum; j++) {
				if (!isObstacle(i, j)) {
					Result res = findMaxUtilityPolicy(i, j);
					this.mazeUtility[i][j] = res.utility;
					this.mazePolicy[i][j] = res.policy;
					if (res.delta > maxDelta) {
						maxDelta = res.delta;
					}
				}
			}
		return maxDelta;
	}

	public Result findMaxUtilityPolicy(int row, int col) {
		Result res = new Result();
		double maxUtility = -999;
		int maxP = 0;
		for (int i = 0; i < 4; i++) {
			double utility = 0;
			switch (i) {
			case 0:
				utility = goUp(row, col);
				break;
			case 1:
				utility = goDown(row, col);
				break;
			case 2:
				utility = goLeft(row, col);
				break;
			case 3:
				utility = goRight(row, col);
				break;
			}
			if (utility > maxUtility) {
				maxUtility = utility;
				maxP = i;
			}
		}
		res.policy = maxP;
		res.utility = maxUtility;
		res.delta = Math.abs(this.mazeUtility[row][col] - maxUtility);
		return res;

	}

	public double goUp(int row, int col) {
		double reward;
		double utility;
		if (this.canReachTheNode(row - 1, col)) {
			utility = this.mazeUtility[row - 1][col];
			reward = this.getReward(row - 1, col);
		} else {
			utility = this.mazeUtility[row][col];
			reward = knockWallReward;
		}
		return reward + gamma * utility;
	}

	public double goLeft(int row, int col) {
		double reward;
		double utility;
		if (this.canReachTheNode(row, col - 1)) {
			utility = this.mazeUtility[row][col - 1];
			reward = this.getReward(row, col - 1);
		} else {
			utility = this.mazeUtility[row][col];
			reward = knockWallReward;
		}
		return reward + gamma * utility;
	}

	public double goDown(int row, int col) {
		double reward;
		double utility;
		if (this.canReachTheNode(row + 1, col)) {
			utility = this.mazeUtility[row + 1][col];
			reward = this.getReward(row + 1, col);
		} else {
			utility = this.mazeUtility[row][col];
			reward = knockWallReward;
		}
		return reward + gamma * utility;
	}

	public double goRight(int row, int col) {
		double reward;
		double utility;
		if (this.canReachTheNode(row, col + 1)) {
			utility = this.mazeUtility[row][col + 1];
			reward = this.getReward(row, col + 1);
		} else {
			utility = this.mazeUtility[row][col];
			reward = knockWallReward;
		}
		return reward + gamma * utility;
	}

	public void getInput() {
		Scanner scan = new Scanner(System.in);
		this.rowNum = scan.nextInt();
		this.colNum = scan.nextInt();
		this.mazeTable = new int[rowNum][colNum];
		this.mazePolicy = new int[rowNum][colNum];
		this.mazeUtility = new double[rowNum][colNum];

		for (int i = 0; i < rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				this.mazeTable[i][j] = scan.nextInt();
			}
	}

	public boolean isObstacle(int row, int col) {
		if (this.mazeTable[row][col] == -1)
			return true;
		return false;
	}

	public boolean canReachTheNode(int row, int col) {
		if (row > -1 && row < this.rowNum && col > -1 && col < this.colNum
				&& this.mazeTable[row][col] != -1)
			return true;
		return false;
	}

	public double getReward(int row, int col) {
		if (this.mazeTable[row][col] > 1) {
			// reach to the goal
			return mazeTable[row][col] * 10;
		}
		return usualMove;
	}

	public void outputRes(int[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				if (isObstacle(i, j)) {
					System.out.print("*");
					continue;
				}
				switch (table[i][j]) {
				case 0:
					System.out.print("¡ü");
					break;
				case 1:
					System.out.print("¡ý");
					break;
				case 2:
					System.out.print("¡û");
					break;
				case 3:
					System.out.print("¡ú");
					break;
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	class Result {
		int policy;
		double utility;
		double delta;
	}

}
