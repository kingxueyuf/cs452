import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

/**
 * @author robin-xue
 * @project_name AI_homework
 * @file_name hw4_pi.java
 * @tag Policy Iteration
 * @date 2013-11-24
 */
public class hw4_pi {

	int rowNum;
	int colNum;
	int[][] mazeTable;
	int[][] mazePolicy;// old policy
	int[][] mazePolicy2;// updated policy
	double[][] mazeUtility;
	Random generator = new Random();
	final static double gamma = 0.9;
	final static double knockWallReward = -10;
	final static double usualMove = -3;

	final static int N = 10000000;

	public static void main(String args[]) {
		hw4_pi hw = new hw4_pi();
		hw.solve();
	}

	public void solve() {
		getInput();
		randomPolicy();
		policyIteration();
		outputRes(this.mazePolicy2);
	}

	public void policyIteration() {
		int n = 1;
		do {
			duplicate(this.mazePolicy2, this.mazePolicy);
			evaluateUtility();
			updateNewPolicy();
			n++;
			this.output2Darray(mazeUtility);
			this.outputRes(mazePolicy2);

		} while (policyChanged());// && n < N
		System.out.println("n = " + n);
	}

	public void evaluateUtility() {
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					DecimalFormat df = new DecimalFormat("0.00");
					this.mazeUtility[i][j] = Double.parseDouble(df
							.format(calculateU(i, j)));
				}
			}
	}

	public void updateNewPolicy() {
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					this.mazePolicy2[i][j] = chooseGreedy(i, j);
				}
			}
	}

	public boolean policyChanged() {
		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					if (this.mazePolicy[i][j] != this.mazePolicy2[i][j])
						return true;
				}
			}
		}

		return false;
	}

	private int chooseGreedy(int i, int j) {
		double maxUtility = -9999;
		double tempUtility = 0;
		int direction = 0;
		for (int d = 0; d < 4; d++) {
			// 4 direction up , down , left , right
			switch (d) {
			case 0:// up
				tempUtility = getUpUtility(i, j);
				break;
			case 1:// down
				tempUtility = getDownUtility(i, j);
				break;
			case 2:// left
				tempUtility = getLeftUtility(i, j);
				break;
			case 3:// right
				tempUtility = getRightUtility(i, j);
				break;
			}
			if (tempUtility >= maxUtility) {
				maxUtility = tempUtility;
				direction = d;
			}
		}
		return direction;
	}

	public double calculateU(int row, int col) {
		double utility = 0;
		switch (this.mazePolicy2[row][col]) {
		case 0:// up
			utility = goUp(row, col);
			break;
		case 1:// down
			utility = goDown(row, col);
			break;
		case 2:// left
			utility = goLeft(row, col);
			break;
		case 3:// right
			utility = goRight(row, col);
			break;
		}
		return utility;
	}

	public double goRight(int row, int col) {
		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row, col + 1)) {
			// can go up
			reward = getReward(row, col + 1);
			nextU = this.mazeUtility[row][col + 1];
		} else {
			// knock the wall -> stay
			reward = knockWallReward;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goLeft(int row, int col) {
		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row, col - 1)) {
			// can go up
			reward = getReward(row, col - 1);
			nextU = this.mazeUtility[row][col - 1];
		} else {
			// knock the wall -> stay
			reward = knockWallReward;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goDown(int row, int col) {
		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row + 1, col)) {
			// can go up
			reward = getReward(row + 1, col);
			nextU = this.mazeUtility[row + 1][col];
		} else {
			// knock the wall -> stay
			reward = knockWallReward;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goUp(int row, int col) {
		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row - 1, col)) {
			// can go up
			reward = getReward(row - 1, col);
			nextU = this.mazeUtility[row - 1][col];
		} else {
			// knock the wall -> stay
			reward = knockWallReward;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double getUpUtility(int row, int col) {
		if (canReachTheNode(row - 1, col)) {
			return this.mazeUtility[row - 1][col];
		} else {
			return this.mazeUtility[row][col];
		}
	}

	public double getDownUtility(int row, int col) {
		if (canReachTheNode(row + 1, col)) {
			return this.mazeUtility[row + 1][col];
		} else {
			return this.mazeUtility[row][col];
		}
	}

	public double getLeftUtility(int row, int col) {
		if (canReachTheNode(row, col - 1)) {
			return this.mazeUtility[row][col - 1];
		} else {
			return this.mazeUtility[row][col];
		}
	}

	public double getRightUtility(int row, int col) {
		if (canReachTheNode(row, col + 1)) {
			return this.mazeUtility[row][col + 1];
		} else {
			return this.mazeUtility[row][col];
		}
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

	public void randomPolicy() {
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					this.mazePolicy2[i][j] = getRandomInt(4);
				}
			}
	}

	public boolean isObstacle(int row, int col) {
		if (this.mazeTable[row][col] == -1)
			return true;
		return false;
	}

	public void getInput() {
		Scanner scan = new Scanner(System.in);
		this.rowNum = scan.nextInt();
		this.colNum = scan.nextInt();
		this.mazeTable = new int[rowNum][colNum];
		this.mazePolicy = new int[rowNum][colNum];
		this.mazePolicy2 = new int[rowNum][colNum];
		this.mazeUtility = new double[rowNum][colNum];

		// maze
		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < colNum; j++) {
				this.mazeTable[i][j] = scan.nextInt();
			}
		}
	}

	public int getRandomInt(int range) {
		int randomIndex = generator.nextInt(range);
		return randomIndex;
	}

	public void duplicate(int[][] src, int[][] dst) {
		// Copy data from src to dst
		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {
				dst[x][y] = src[x][y];
			}
		}
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

	public void output2Darray(double[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
}
