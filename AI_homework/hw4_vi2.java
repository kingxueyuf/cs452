import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class hw4_vi2 {

	int rowNum;
	int colNum;
	int[][] mazeTable;
	int[][] mazePolicy;
	double[][] mazeUtility;

	HashMap<String, Node> pointMap = new HashMap<String, Node>();
	private static double deltaMin = 1e-9;
	Random generator = new Random();
	int topC = 0;
	int pointVisitedNum = 0;
	int[] pointArr;
	final static double gamma = 0.9;

	public static void main(String args[]) {
		hw4_vi2 vi2 = new hw4_vi2();
		vi2.run();
	}

	public void run() {
		getInput();
		valueIteration();
	}

	public void valueIteration() {
		randomPolicy();
		double delta = 999;
		while (delta > 0.1) {
			delta = evaluateUtility();
			pointVisitedNum = 0;
			outputRes(this.mazePolicy);
			System.out.println("delta = " + delta);
		}

		do {
			updateNewPolicy();
			pointVisitedNum = 0;
			delta = evaluateUtility();
			outputRes(this.mazePolicy);
			System.out.println("delta = " + delta);
		} while (delta > deltaMin);
	}

	public double evaluateUtility() {
		double maxDelta = -999;
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					DecimalFormat df = new DecimalFormat("0.00");
					double newU = calculateU(i, j);
					if (Math.abs(newU - this.mazeUtility[i][j]) > maxDelta) {
						maxDelta = Math.abs(newU - this.mazeUtility[i][j]);
					}
					this.mazeUtility[i][j] = Double
							.parseDouble(df.format(newU));
				}
			}
		return maxDelta;
	}

	public void updateNewPolicy() {
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					this.mazePolicy[i][j] = chooseGreedy(i, j);
				}
			}
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

	public double calculateU(int row, int col) {
		double utility = 0;
		if (thisIsFinalPoint(row, col)) {
			return 0 + this.mazeUtility[row][col];
		}
		switch (this.mazePolicy[row][col]) {
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

	public boolean thisIsFinalPoint(int row, int col) {
		if (thisIsTopCPoint(row, col) && this.pointVisitedNum == topC)
			return true;
		return false;
	}

	public boolean thisIsTopCPoint(int row, int col) {
		int num = this.mazeTable[row][col];
		for (int i = 1; i <= topC; i++) {
			if (this.pointArr[pointArr.length - i] == num) {
				return true;
			}
		}
		return false;
	}

	public double goUp(int row, int col) {

		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row - 1, col)) {
			// can go up
			if (this.pointVisitedNum < topC && thisIsTopCPoint(row - 1, col)) {
				reward = this.mazeTable[row - 1][col];
				pointVisitedNum++;
			} else {
				reward = -1;
			}
			nextU = this.mazeUtility[row - 1][col];
		} else {
			// knock the wall -> stay
			reward = -1;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goDown(int row, int col) {

		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row + 1, col)) {
			// can go up
			if (this.pointVisitedNum < topC && thisIsTopCPoint(row + 1, col)) {
				reward = this.mazeTable[row + 1][col];
				pointVisitedNum++;
			} else {
				reward = -1;
			}
			nextU = this.mazeUtility[row + 1][col];
		} else {
			// knock the wall -> stay
			reward = -1;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goLeft(int row, int col) {

		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row, col - 1)) {
			// can go up
			if (this.pointVisitedNum < topC && thisIsTopCPoint(row, col - 1)) {
				reward = this.mazeTable[row][col - 1];
				pointVisitedNum++;
			} else {
				reward = -1;
			}
			nextU = this.mazeUtility[row][col - 1];
		} else {
			// knock the wall -> stay
			reward = -1;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public double goRight(int row, int col) {

		double reward = 0;
		double nextU = 0;
		if (canReachTheNode(row, col + 1)) {
			// can go up
			if (this.pointVisitedNum < topC && thisIsTopCPoint(row, col + 1)) {
				reward = this.mazeTable[row][col + 1];
				pointVisitedNum++;
			} else {
				reward = -1;
			}
			nextU = this.mazeUtility[row][col + 1];
		} else {
			// knock the wall -> stay
			reward = -1;
			nextU = this.mazeUtility[row][col];
		}
		return reward + gamma * nextU;
	}

	public void randomPolicy() {
		for (int i = 0; i < this.rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				if (!isObstacle(i, j)) {
					this.mazePolicy[i][j] = getRandomInt(4);
				}
			}
	}

	public boolean canReachTheNode(int row, int col) {
		if (row > -1 && row < this.rowNum && col > -1 && col < this.colNum
				&& this.mazeTable[row][col] != -1)
			return true;
		return false;
	}

	public int getRandomInt(int range) {
		int randomIndex = generator.nextInt(range);
		return randomIndex;
	}

	public void getInput() {
		Scanner scan = new Scanner(System.in);
		this.rowNum = scan.nextInt();
		this.colNum = scan.nextInt();
		this.mazeTable = new int[rowNum][colNum];
		this.mazePolicy = new int[rowNum][colNum];
		this.mazeUtility = new double[rowNum][colNum];

		ArrayList list = new ArrayList<Integer>();
		for (int i = 0; i < rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				this.mazeTable[i][j] = scan.nextInt();
				list.add(mazeTable[i][j]);
				pointMap.put(mazeTable[i][j] + "", new Node(i, j));
			}

		pointArr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			pointArr[i] = (Integer) list.get(i);
		}
		Arrays.sort(pointArr);
		topC = scan.nextInt();
	}

	public boolean isObstacle(int row, int col) {
		if (this.mazeTable[row][col] == -1)
			return true;
		return false;
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

	class Node {
		int x;
		int y;

		Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}
}
