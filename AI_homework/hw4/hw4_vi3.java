package hw4;

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
	ArrayList<Point> toVisit;

	public static void main(String args[]) {
		hw4_vi3 hw4 = new hw4_vi3();
		MarkovDecisionProcess<State> mdp = hw4.getMDP();

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

	public MarkovDecisionProcess<State> getMDP() {
		MarkovDecisionProcess<State> mdp = new MazeMDP<State>();
		getInput();
		generateMDP(mdp);
		return mdp;
	}

	public void generateMDP(MarkovDecisionProcess<State> mdp) {
		// find all the point node
		ArrayList<Point> allPoint = new ArrayList<Point>();
		for (int i = 0; i < mazeTable.length; i++) {
			for (int j = 0; j < mazeTable[0].length; j++) {
				if (mazeTable[i][j] > 1) {
					allPoint.add(new Point(i, j, mazeTable[i][j]));
				}
				if (mazeTable[i][j] == 1) {
					this.start = new Point(i, j, 1);
				}
			}
		}

		this.sortList(allPoint);
		for (int i = 0; i < this.c; i++) {
			System.out.println("x=" + allPoint.get(i).x + " y="
					+ allPoint.get(i).y + " point=" + allPoint.get(i).point);
		}

		toVisit = new ArrayList<Point>();
		for (int i = 0; i < this.c; i++) {
			toVisit.add(allPoint.get(i));
		}

		for (int i = c; i < allPoint.size(); i++) {
			double diff = Math.abs(allPoint.get(i).point
					- allPoint.get(c - 1).point);
			if (diff < 0.1) {
				toVisit.add(allPoint.get(i));
			}
		}

		for (int i = 0; i < mazeTable.length; i++) {
			for (int j = 0; j < mazeTable[0].length; j++) {
				for (int a = 0; a <= this.c; a++) {
					addX_Y_State(i, j, a, toVisit, mdp);
				}
			}
		}

		HashMap<Double, Integer> pointCount = new HashMap<Double, Integer>();
		for (int i = 0; i < this.c; i++) {
			Integer count = pointCount.get(allPoint.get(i).point);
			if (count == null) {
				count = 1;
			} else {
				count = count + 1;
			}
			pointCount.put(allPoint.get(i).point, count);
		}

		for (State s : mdp.states()) {
			LinkedList<Point> currentRemain = s.remain;
			ArrayList<Double> list = new ArrayList<Double>();
			for (int i = 0; i < allPoint.size(); i++) {
				if (!inTheVisitList(allPoint.get(i), currentRemain)) {
					list.add(allPoint.get(i).point);
				}
			}

			HashMap<Double, Integer> map = new HashMap<Double, Integer>();
			copyMap(map, pointCount);

			for (Double key : pointCount.keySet()) {
				for (int i = 0; i < list.size(); i++) {
					if (Math.abs(list.get(i) - key) < 0.1) {
						map.put(key, map.get(key) - 1);
					}
				}
			}
			s.map = map;
		}
	}

	public void copyMap(HashMap<Double, Integer> map,
			HashMap<Double, Integer> pointCount) {
		for (Double key : pointCount.keySet()) {
			Integer value = pointCount.get(key);
			map.put(key, value);
		}
	}

	public boolean inTheVisitList(Point point, LinkedList<Point> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(point))
				return true;
		}
		return false;
	}

	public void sortList(List<Point> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			if (isLarger(list.get(i + 1), list.get(i))) {
				Point temp = list.get(i);
				list.set(i, list.get(i + 1));
				list.set(i + 1, temp);
			}
			for (int j = i; j > 0; j--) {
				if (isLarger(list.get(i), list.get(i - 1))) {
					Point temp2 = list.get(i - 1);
					list.set(i - 1, list.get(i));
					list.set(i, temp2);
				}

			}
		}
	}

	public boolean isLarger(Point one, Point two) {
		if (one.point - two.point > 0.001)
			return true;
		return false;
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

	public void getInput() {
		Scanner scan = new Scanner(System.in);
		this.rowNum = scan.nextInt();
		this.colNum = scan.nextInt();
		this.mazeTable = new int[rowNum][colNum];

		for (int i = 0; i < rowNum; i++)
			for (int j = 0; j < colNum; j++) {
				this.mazeTable[i][j] = scan.nextInt();
			}
		this.c = scan.nextInt();
	}
}
