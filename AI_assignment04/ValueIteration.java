import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ValueIteration {

	private final static int UP = 0;
	private final static int DOWN = 1;
	private final static int LEFT = 2;
	private final static int RIGHT = 3;
	private final static int STAY = 4;

	private static double minDealta = 0.1;
	private static double jumpDelta = 0.00001;
	private final static double gamma = 0.9;

	private int c;

	private MarkovDecisionProcess<State> mdp;
	private int[][] mazeTable;

	private ArrayList<Point> needToVisit;

	private Random generator = new Random();

	private Point start;

	public void setMazeTable(int[][] mazeTable) {
		this.mazeTable = mazeTable;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public void setToVisit(ArrayList<Point> list) {
		this.needToVisit = list;
	}

	public void setC(int c) {
		this.c = c;
	}

	public void valueIteration(MarkovDecisionProcess<State> mdp) {
		this.mdp = mdp;
		HashMap<State, Double> U = initUtility(mdp);
		HashMap<State, Integer> P = initPolicy(mdp);

		double delta = 0;
		do {
			delta = evaluateUtility(mdp, U, P);
			// System.out.println(delta);
		} while (delta > minDealta);

		do {
			updatePolicy(mdp, U, P);
			delta = evaluateUtility(mdp, U, P);
			// System.out.println(delta);
		} while (delta > jumpDelta);

		State currentState = null;
		for (State s : mdp.states()) {
			if (s.x == start.x && s.y == start.y && s.c == 0) {
				currentState = s;
				break;
			}
		}
		while (true) {
			int p = P.get(currentState);
			addStep(p);
			if (p == this.STAY)
				break;
			Next next = calculateU(currentState, p, U);
			currentState = next.s;
		}
		State startState = null;
		for (State s : mdp.states()) {
			if (s.x == this.start.x && s.y == start.y && s.c == 0) {
				startState = s;
				break;
			}
		}
		outputPath(U.get(startState));
	}

	public double evaluateUtility(MarkovDecisionProcess<State> mdp,
			HashMap<State, Double> U, HashMap<State, Integer> P) {

		double maxDelta = -999;
		for (State s : mdp.states()) {
			// for each state
			Double oldUtility = U.get(s);
			Integer statePolicy = P.get(s);
			Next next = calculateU(s, statePolicy, U);
			double newUtility = calculateNextUtility(next);
			U.put(s, newUtility);

			if (Math.abs(newUtility - oldUtility) > maxDelta) {
				maxDelta = Math.abs(newUtility - oldUtility);
			}
		}
		return maxDelta;
	}

	public void updatePolicy(MarkovDecisionProcess<State> mdp,
			HashMap<State, Double> U, HashMap<State, Integer> P) {
		for (State s : mdp.states()) {
			// 4 direction
			if (s.c == this.c
					&& inToVisit(new Point(s.x, s.y, mazeTable[s.x][s.y]))) {
				P.put(s, STAY);
			} else {
				double maxUtility = -99;
				int policy = -1;
				for (int i = 0; i < 4; i++) {
					Next next = this.calculateU(s, i, U);
					double utility = this.calculateNextUtility(next);
					if (utility > maxUtility) {
						policy = i;
						maxUtility = utility;
					}
				}
				P.put(s, policy);
			}
		}
	}

	public boolean inToVisit(Point point) {
		for (int i = 0; i < this.needToVisit.size(); i++) {
			if (needToVisit.get(i).x == point.x
					&& needToVisit.get(i).y == point.y) {
				return true;
			}
		}
		return false;
	}

	public double calculateNextUtility(Next next) {
		return next.reward + this.gamma * next.utility;
	}

	public Next calculateU(State s, Integer policy, HashMap<State, Double> U) {

		Next next = null;
		switch (policy) {
		case UP:
			next = goUp(s, U);
			break;
		case DOWN:
			next = goDown(s, U);
			break;
		case LEFT:
			next = goLeft(s, U);
			break;
		case RIGHT:
			next = goRight(s, U);
			break;
		case STAY:
			next = goStay(s, U);
		}
		return next;
	}

	public Next goStay(State s, HashMap<State, Double> U) {
		double reward = 0;
		double nextUtility = 0;
		reward = 0;
		State nextS = s;
		nextUtility = findUtility(nextS, U);

		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next goUp(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x - 1, y)) {
			// move up
			if (inRemainList(x - 1, y, s)) {
				reward = this.mazeTable[x - 1][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x - 1, y, s);
				nextS = findState(x - 1, y, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x - 1, y, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);

			} else {
				reward = -1;
				LinkedList<Point> remainList = s.remain;
				nextS = findState(x - 1, y, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x - 1, y, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			}
		} else {
			// stay
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x, y, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			} else {
				if (currentCapacity == this.c
						&& inToVisit(new Point(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = findUtility(nextS, U);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next goDown(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x + 1, y)) {
			// move up
			if (inRemainList(x + 1, y, s)) {
				reward = this.mazeTable[x + 1][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x + 1, y, s);
				nextS = findState(x + 1, y, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x + 1, y, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);

			} else {
				reward = -1;
				LinkedList<Point> remainList = s.remain;
				nextS = findState(x + 1, y, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x + 1, y, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			}
		} else {
			// stay
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = findUtility(nextS, U);
			} else {
				if (currentCapacity == this.c
						&& inToVisit(new Point(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = findUtility(nextS, U);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next goLeft(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x, y - 1)) {
			// move up
			if (inRemainList(x, y - 1, s)) {
				reward = this.mazeTable[x][y - 1];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y - 1, s);
				nextS = findState(x, y - 1, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x, y - 1, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);

			} else {
				reward = -1;
				LinkedList<Point> remainList = s.remain;
				nextS = findState(x, y - 1, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x, y - 1, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			}
		} else {
			// stay
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = findUtility(nextS, U);
			} else {
				if (currentCapacity == this.c
						&& inToVisit(new Point(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = findUtility(nextS, U);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next goRight(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x, y + 1)) {
			// move up
			if (inRemainList(x, y + 1, s)) {
				reward = this.mazeTable[x][y + 1];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y + 1, s);
				nextS = findState(x, y + 1, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x, y + 1, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			} else {
				reward = -1;
				LinkedList<Point> remainList = s.remain;
				nextS = findState(x, y + 1, currentCapacity, remainList);
				if (nextS == null) {
					outputState(x, y + 1, currentCapacity, remainList);
				}
				nextUtility = findUtility(nextS, U);
			}
		} else {
			// stay
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<Point> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = findUtility(nextS, U);
			} else {
				if (currentCapacity == this.c
						&& inToVisit(new Point(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = findUtility(nextS, U);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public LinkedList<Point> updateRemainList(int x, int y, State s) {
		LinkedList<Point> remain = s.remain;
		LinkedList<Point> newRemainList = new LinkedList<Point>();
		for (int i = 0; i < remain.size(); i++) {
			if (remain.get(i).x == x && remain.get(i).y == y) {
				continue;
			}
			Point point = new Point(remain.get(i).x, remain.get(i).y,
					remain.get(i).point);
			newRemainList.add(point);
		}
		return newRemainList;
	}

	public boolean inRemainList(int x, int y, State s) {
		for (int i = 0; i < s.remain.size(); i++) {
			if (s.remain.get(i).x == x && s.remain.get(i).y == y)
				return true;
		}
		return false;
	}

	public State findState(int x, int y, int currentCapacity,
			LinkedList<Point> remain) {
		for (State s : mdp.states()) {
			if (s.x == x && s.y == y && s.c == currentCapacity) {
				if (currentCapacity == this.c)
					return s;
				if (equalRemain(s.remain, remain)) {
					return s;
				}
			}
		}
		return null;
	}

	public Double findUtility(State s, HashMap<State, Double> U) {
		return U.get(s);
	}

	public boolean equalRemain(LinkedList<Point> one, LinkedList<Point> two) {
		if (one.size() != two.size())
			return false;
		for (int i = 0; i < one.size(); i++) {
			if (!inTheList(two, one.get(i)))
				return false;
		}
		return true;
	}

	public boolean inTheList(LinkedList<Point> two, Point point) {
		for (int i = 0; i < two.size(); i++) {
			if (point.x == two.get(i).x && point.y == two.get(i).y) {
				return true;
			}
		}
		return false;
	}

	public boolean canReachTheNode(int row, int col) {
		if (row > -1 && row < this.mazeTable.length && col > -1
				&& col < this.mazeTable[0].length
				&& this.mazeTable[row][col] != -1)
			return true;
		return false;
	}

	public HashMap<State, Double> initUtility(MarkovDecisionProcess<State> mdp) {
		HashMap<State, Double> map = new HashMap<State, Double>();
		for (State s : mdp.states()) {
			map.put(s, 0.0);
		}
		return map;
	}

	public HashMap<State, Integer> initPolicy(MarkovDecisionProcess<State> mdp) {
		HashMap<State, Integer> map = new HashMap<State, Integer>();
		for (State s : mdp.states()) {
			map.put(s, getRandomInt(4));
		}
		return map;
	}

	public int getRandomInt(int range) {
		int randomIndex = generator.nextInt(range);
		return randomIndex;
	}

	ArrayList<Integer> path = new ArrayList<Integer>();

	public void addStep(int p) {
		path.add(p);
	}

	public void outputPath(Double s) {
		DecimalFormat df = new DecimalFormat("0.00000");
		System.out.println("V(s_0) = " + Double.parseDouble(df.format(s)));
		System.out.print("Path of length " + (path.size() - 1) + ":");
		for (int i = 0; i < path.size() - 1; i++) {
			switch (path.get(i)) {
			case UP:
				System.out.print(" Up");
				break;
			case DOWN:
				System.out.print(" Down");
				break;
			case LEFT:
				System.out.print(" Left");
				break;
			case RIGHT:
				System.out.print(" Right");
				break;
			}
			if (i != (path.size() - 2))
				System.out.print(",");
		}
	}

	public void outputDirection(int p) {
		switch (p) {
		case UP:
			System.out.println("Up");
			break;
		case DOWN:
			System.out.println("¡ý");
			break;
		case LEFT:
			System.out.println("¡û");
			break;
		case RIGHT:
			System.out.println("¡ú");
			break;
		}

	}

	public void outputState(int x, int y, int currentCapacity, List<Point> list) {

		System.out.println("null state");
		System.out.println("x= " + (x) + " y= " + y + " c= " + currentCapacity);

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).x + " " + list.get(i).y);
		}
		System.out.println();
	}

	class Next {
		State s;
		Double reward;
		Double utility;

		public Next(State s, Double reward, Double utility) {
			this.s = s;
			this.reward = reward;
			this.utility = utility;
		}
	}

}
