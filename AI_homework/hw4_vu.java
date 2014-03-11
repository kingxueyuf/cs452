import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class hw4_vu {
	int row;
	int col;
	int[][] mazeTable;
	int[][] mazeDirection;
	double[][] mazeValue;
	Random generator = new Random();
	double gamma = 0.9;
	private static double deltaMin = 1e-9;
	private static int N = 10000; // max number of iterations of Value Iteration

	HashMap<String, Node> map = new HashMap<String, Node>();
	int[] queue;
	int c;
	int reached = 0;

	public static void main(String args[]) {
		hw4_vu vu = new hw4_vu();
		vu.run();
	}

	public void run() {
		Scanner scan = new Scanner(System.in);
		// while (scan.hasNext()) {
		row = scan.nextInt();
		col = scan.nextInt();
		initMaze(row, col);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				this.mazeTable[i][j] = scan.nextInt();
			}
		}
		this.outputTable(mazeTable);
		c = scan.nextInt();

		// test input
		// output2Darray(this.mazeValue);
		doValueUpdate(c);
		// }
		// outputDirection(this.mazeDirection);

	}

	public void doValueUpdate(int c) {
		findThePointLocation();
		randomGenerateDirection(this.mazeDirection, this.mazeTable);
		// this.outputDirection(mazeDirection);
		// outputDirection(this.mazeDirection);
		double delta = 0;

		do {
			delta = updateMazeValue(this.mazeValue, this.mazeDirection,
					this.mazeTable);
			this.output2Darray(mazeValue);
			// outputDirection(this.mazeDirection);
			// this.output2Darray(mazeValue);
			System.out.println("delta = " + delta);
		} while (delta > 0.1);
		while (true) {
			/* test */
			System.out.println("before:");
			outputDirection(this.mazeDirection);
			/* test end */

			updateDirectionGreedy(this.mazeDirection, this.mazeValue,
					this.mazeTable);

			/* test */
			System.out.println("after:");
			outputDirection(this.mazeDirection);
			System.out.println();
			/* test end */

			delta = updateMazeValue(this.mazeValue, this.mazeDirection,
					this.mazeTable);

			/* test */
			this.output2Darray(this.mazeValue);
			System.out.println("delta = " + delta);
			/* test end */

			if (delta < 1e-9)
				break;
			// if (delta > 0.1) {
			// while (delta > 0.1) {
			// delta = updateMazeValue(this.mazeValue, this.mazeDirection,
			// this.mazeTable);
			// }
			// }
		}
		choosePath(c);
	}

	public void findThePointLocation() {
		ArrayList<Integer> pointArray = new ArrayList<Integer>();
		// System.out.println(this.row);
		for (int i = 0; i < this.row; i++) {
			for (int j = 0; j < this.col; j++) {
				if (this.mazeTable[i][j] > 1) {
					// System.out.println("here");
					pointArray.add(this.mazeTable[i][j]);
					this.map.put(this.mazeTable[i][j] + "", new Node(i, j));
				}
			}
		}
		queue = new int[pointArray.size()];
		for (int i = 0; i < pointArray.size(); i++) {
			queue[i] = pointArray.get(i);
		}
		Arrays.sort(queue);
	}

	public double updateMazeValue(double[][] mazeV, int[][] mazeD, int[][] mazeT) {
		double maxDelta = 0;
		// double reward;
		double newValue;
		for (int i = 0; i < mazeV.length; i++) {
			for (int j = 0; j < mazeV[i].length; j++) {
				if (mazeT[i][j] != -1) {
					newValue = updateFunction(i, j, mazeV, mazeD, mazeT);
					if (Math.abs(newValue - mazeV[i][j]) > maxDelta)
						maxDelta = Math.abs(newValue - mazeV[i][j]);
					DecimalFormat df = new DecimalFormat("0.00");
					mazeV[i][j] = Double.parseDouble(df.format(newValue));
				}
			}
		}
		reached = 0;
		return maxDelta;
	}

	public boolean stayHere(int row, int col) {
		if (topC(row, col)) {
			// stay
			reached++;
			if (lastOne()) {
				return true;
			}
		}
		return false;
	}

	public boolean lastOne() {
		if (c == reached)
			return true;
		return false;
	}

	public boolean topC(int row, int col) {
		for (int i = 0; i < this.c; i++) {
			// top C
			Node node = this.map.get(this.queue[i] + "");
			if (node.x == row && node.y == col)
				return true;
		}
		return false;
	}

	public double updateFunction(int i, int j, double[][] mazeV, int[][] mazeD,
			int[][] mazeT) {
		if (stayHere(i, j)) {
			return 1000 + 0.9 * mazeV[i][j];
		}
		int direction = mazeD[i][j];
		double nextV = 0;
		double reward = 0;
		switch (direction) {
		case 0:// up
			if ((i - 1) > -1 && mazeT[i - 1][j] != -1) {
				nextV = mazeV[i - 1][j];
				reward = rewardFunction(i - 1, j, mazeT, true);
			} else {
				// knock the wall
				nextV = mazeV[i][j];
				reward = rewardFunction(i, j, mazeT, false);
			}
			break;
		case 1:// down
			if ((i + 1) < mazeT.length && mazeT[i + 1][j] != -1) {
				nextV = mazeV[i + 1][j];
				reward = rewardFunction(i + 1, j, mazeT, true);
			} else {
				// knock the wall
				nextV = mazeV[i][j];
				reward = rewardFunction(i, j, mazeT, false);
			}
			break;
		case 2:// left
			if ((j - 1) > -1 && mazeT[i][j - 1] != -1) {
				// reach next node
				nextV = mazeV[i][j - 1];
				reward = rewardFunction(i, j - 1, mazeT, true);
			} else {
				// knock the wall
				nextV = mazeV[i][j];
				reward = rewardFunction(i, j, mazeT, false);
			}
			break;
		case 3:// right
			if ((j + 1) < mazeT[0].length && mazeT[i][j + 1] != -1) {
				// reach next node
				nextV = mazeV[i][j + 1];
				reward = rewardFunction(i, j + 1, mazeT, true);
			} else {
				// knock the wall
				nextV = mazeV[i][j];
				reward = rewardFunction(i, j, mazeT, false);
			}
			break;
		}
		return (reward + this.gamma * nextV);
	}

	public double rewardFunction(int i, int j, int[][] mazeT,
			boolean reachNextNode) {
		if (!reachNextNode) {
			// knock the wall
			return -2;
		}
		if (mazeT[i][j] > 1)
			return mazeT[i][j];
		return -1;
	}

	public void updateDirectionGreedy(int[][] mazeD, double[][] mazeV,
			int[][] mazeT) {
		// this.output2Darray(mazeV);
		for (int i = 0; i < mazeV.length; i++) {
			for (int j = 0; j < mazeV[i].length; j++) {

				int newDirection = getMaxValueDirection(i, j, mazeV, mazeT,
						mazeD);
				// System.out.println(mazeD[i][j]+" "+newDirection);
				mazeD[i][j] = newDirection;
				// this.outputDirection(mazeD);
			}
		}
	}

	public int getMaxValueDirection(int i, int j, double[][] mazeV,
			int[][] mazeT, int[][] mazeD) {
		int maxDirection = 0;
		double maxValue = -9999999;
		for (int d = 0; d < 4; d++) {
			switch (d) {
			case 0:// up
				if ((i - 1) > -1 && mazeT[i - 1][j] != -1) {
					// can reach up point
					if (mazeV[i - 1][j] >= maxValue) {
						maxValue = mazeV[i - 1][j];
						maxDirection = 0;
					}
				} else {
					// stay
					if (mazeV[i][j] >= maxValue) {
						maxValue = mazeV[i][j];
						maxDirection = mazeD[i][j];
					}
				}
				break;
			case 1:// down
				if ((i + 1) < mazeT.length && mazeT[i + 1][j] != -1) {
					// can reach down point
					if (mazeV[i + 1][j] > maxValue) {
						maxValue = mazeV[i + 1][j];
						maxDirection = 1;
					}
				} else {
					// stay
					if (mazeV[i][j] >= maxValue) {
						maxValue = mazeV[i][j];
						maxDirection = mazeD[i][j];
					}
				}
				break;
			case 2:// left
				if ((j - 1) > -1 && mazeT[i][j - 1] != -1) {
					// can reach down point
					if (mazeV[i][j - 1] > maxValue) {
						maxValue = mazeV[i][j - 1];
						maxDirection = 2;
					}

				} else {
					// stay
					if (mazeV[i][j] >= maxValue) {
						maxValue = mazeV[i][j];
						maxDirection = mazeD[i][j];
					}
				}
				break;

			case 3:// right
				if ((j + 1) < mazeT[0].length && mazeT[i][j + 1] != -1) {
					// can reach down point
					if (mazeV[i][j + 1] > maxValue) {
						maxValue = mazeV[i][j + 1];
						maxDirection = 3;
					}
				} else {
					// stay
					if (mazeV[i][j] >= maxValue) {
						maxValue = mazeV[i][j];
						maxDirection = mazeD[i][j];
					}
				}
				break;
			}
		}
		return maxDirection;
	}

	public void choosePath(int c) {

	}

	public void randomGenerateDirection(int[][] mazeD, int[][] mazeT) {
		for (int i = 0; i < mazeD.length; i++) {
			for (int j = 0; j < mazeD[i].length; j++) {
				if (mazeT[i][j] != -1)
					mazeD[i][j] = getRandomInt(4);
			}
		}
	}

	public int getRandomInt(int range) {
		int randomIndex = generator.nextInt(range);
		return randomIndex;
	}

	public void initMaze(int row, int col) {
		this.mazeTable = new int[row][col];
		this.mazeDirection = new int[row][col];
		this.mazeValue = new double[row][col];

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

	public void outputDirection(int[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
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
	}

	public void outputTable(int[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j] + " ");
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
