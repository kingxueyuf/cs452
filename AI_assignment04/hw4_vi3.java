import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class hw4_vi3 {

	int[][] mazeTable;
	int rowNum;
	int colNum;
	int c;
	Point start;
	HashMap<String, Point> map;
	ArrayList<Point> toVisit;

	public static void main(String args[]) {
		hw4_vi3 hw4 = new hw4_vi3();
		MarkovDecisionProcess<State> mdp = hw4.getMDP(args);

		// for (State s : mdp.states()) {
		// System.out.println("x = " + s.x + " y = " + s.y + " c = " + s.c);
		// for (int i = 0; i < s.remain.size(); i++) {
		// System.out.println(s.remain.get(i).x + " " + s.remain.get(i).y);
		// }
		// System.out.println("");
		// }

		ValueIteration vi = new ValueIteration();
		vi.setMazeTable(hw4.mazeTable);
		vi.setC(hw4.c);
		vi.setStart(hw4.start);
		vi.setToVisit(hw4.toVisit);
		vi.valueIteration(mdp);
	}

	public MarkovDecisionProcess<State> getMDP(String[] args) {
		MarkovDecisionProcess<State> mdp = new MazeMDP<State>();
		getInput(args);
		generateMDP(mdp);
		return mdp;
	}

	public void generateMDP(MarkovDecisionProcess<State> mdp) {
		map = new HashMap<String, Point>();
		// find all the point node
		ArrayList<Integer> tempQueue = new ArrayList<Integer>();
		for (int i = 0; i < mazeTable.length; i++) {
			for (int j = 0; j < mazeTable[0].length; j++) {
				if (mazeTable[i][j] > 1) {
					tempQueue.add(mazeTable[i][j]);
					map.put(mazeTable[i][j] + "", new Point(i, j,
							mazeTable[i][j]));
				}
				if (mazeTable[i][j] == 1) {
					this.start = new Point(i, j, 1);
				}
			}
		}

		int[] queue = new int[tempQueue.size()];
		for (int i = 0; i < tempQueue.size(); i++) {
			queue[i] = tempQueue.get(i);
		}

		Arrays.sort(queue);

		toVisit = new ArrayList<Point>();

		int last = queue.length - 1;
		for (int i = last; i > last - this.c; i--) {
			toVisit.add(map.get(queue[i] + ""));
		}

		for (int i = 0; i < mazeTable.length; i++) {
			for (int j = 0; j < mazeTable[0].length; j++) {
				for (int a = 0; a <= this.c; a++) {
					addX_Y_State(i, j, a, toVisit, mdp);
				}
			}
		}
	}

	int[] r;

	public void addX_Y_State(int x, int y, int c, ArrayList<Point> toVisit,
			MarkovDecisionProcess<State> mdp) {
		List<LinkedList<Point>> list = new ArrayList<LinkedList<Point>>();
		r = new int[100];
		combination(toVisit.size(), toVisit.size() - c, 0, 0, list);
		for (int i = 0; i < list.size(); i++) {
			State s = new State();
			s.x = x;
			s.y = y;
			s.c = c;
			s.remain = list.get(i);
			mdp.addState(s);
		}
	}

	public void combination(int n, int m, int k, int index,
			List<LinkedList<Point>> list) {
		int i;
		if (index == m) {
			LinkedList listOne = new LinkedList<Point>();
			for (i = 0; i < m; i++) {
				listOne.add(toVisit.get(r[i]));
			}
			list.add(listOne);
			return;
		}

		for (i = k; i < n; i++) {
			r[index] = i;
			combination(n, m, i + 1, index + 1, list);
		}
	}

	public void getInput(String args[]) {
		try {
			BufferedReader scan = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[0])));
			String oneLine = scan.readLine();
			String[] arr = oneLine.split(" ");
			this.rowNum = Integer.valueOf(arr[0]);
			this.colNum = Integer.valueOf(arr[1]);
			this.mazeTable = new int[rowNum][colNum];

			for (int i = 0; i < rowNum; i++) {
				oneLine = scan.readLine();
				arr = oneLine.split(" ");
				for (int j = 0; j < colNum; j++) {
					this.mazeTable[i][j] = Integer.valueOf(arr[j]);
				}
			}
		} catch (Exception e) {

		}
		this.c = Integer.valueOf(args[1]);
	}
}
