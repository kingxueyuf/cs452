import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hw2_csp {

	public String[][] table;
	public String[][] copyTable;
	public Conflict[][] conflictArr;
	public HashSet<String> Dic = new HashSet<String>();
	public HashSet<String> DicCopy = new HashSet<String>();
	public ArrayList<node> variables = new ArrayList<node>();
	public ArrayList<node> variablesCopy = new ArrayList<node>();
	public Random rand = new Random();
	public int ROW = 0;
	public int COL = 0;
	public int time;

	public static void main(String[] args) {
		hw2_csp csp = new hw2_csp();
		// dictionary_simple.txt
		// dictionary_medium.txt
		// dictionary_large.txts
		// xword00.txt
		// xword01.txt
		// xword02.txt
//		args = new String[] {
//				"C:\\Users\\robin-xue\\Desktop\\inputfiles\\xword02.txt",
//				"C:\\Users\\robin-xue\\Desktop\\inputfiles\\dictionary_medium.txt",
//				"1000", "3" };

		csp.getInput(args);
		// int s = Integer.valueOf(args[2]);
		// int p = Integer.valueOf(args[3]);
		csp.search(Integer.valueOf(args[2]), Integer.valueOf(args[3]));
	}

	public void search(int s, int r) {
		boolean getRes = false;
		// random generate result
		int i = 0;
		for (i = 0; i < r; i++) {
			initAllDataStructure();
			randomGenerateResult();
			if (i == 0) {
				this.printInit();
			}
			// System.out.println("---------random-----------");
			// output(table);
			// System.out.println("---------random-----------");
			getRes = doCS(s);
			if (getRes)
				break;
			// if(getRes)
			// System.out.println("find!!!!!!!");
		}
		this.printRes(i, getRes);
	}

	public void printRes(int i, boolean find) {
		System.out.println("Puzzle Solution:");
		this.printVariables();
		System.out.println();
		System.out.println("Conflict Count: " + this.calculateConflictNum());
		System.out.println();
		System.out.println("Random Restarts:" + i);
		System.out.println();
		System.out.println("Search Steps Required:" + time);
	}

	public void printInit() {
		System.out.println(variables.size() + " words");
		System.out.println(this.calculateConflictNum() + " constraints");
		System.out.println();
		System.out.println("Initial Random State:");
		printVariables();
		System.out.println();
		System.out.println("Conflict Count: " + this.calculateConflictNum());
		System.out.println();
	}

	public void printVariables() {
		for (int i = 0; i < variables.size(); i++) {
			int direction = variables.get(i).direction;
			switch (direction) {
			case 1:
				// right_down
				System.out.println(i + "-across = "
						+ variables.get(i).rightWord);
				System.out.println(i + "-down = " + variables.get(i).downWord);
				break;
			case 2:
				// right
				System.out.println(i + "-across = "
						+ variables.get(i).rightWord);
				break;
			case 3:
				// down
				System.out.println(i + "-down = " + variables.get(i).downWord);
				break;
			}
		}
	}

	public void initAllDataStructure() {
		// init table[][]
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				this.table[i][j] = this.copyTable[i][j];
			}
		}
		// init conflictArr[][]
		conflictArr = new Conflict[ROW][COL];

		// init variables
		this.variables = new ArrayList<node>();
		for (int i = 0; i < this.variablesCopy.size(); i++) {
			int row = this.variablesCopy.get(i).row;
			int col = this.variablesCopy.get(i).col;
			this.variables.add(new node(row, col));
		}
		// init Dic
		this.Dic = new HashSet<String>();
		String[] arr = this.DicCopy.toArray(new String[0]);
		for (int i = 0; i < arr.length; i++) {
			this.Dic.add(arr[i]);
		}
	}

	public boolean doCS(int p) {
		int conflictNum = calculateConflictNum();
		time = 0;
		boolean find = false;
		while (conflictNum > 0) {
			// random get a variable
			int index = getRandomNumber(variables.size());
			node node = variables.get(index);
			int row = node.row;
			int col = node.col;
			// get direction
			int direction = getDirection(row, col);
			switch (direction) {
			case 1:
				// right_down

				// right first
				int rightLength1 = findRightLength(row, col);
				String[] arr1 = findArrayWithLength(rightLength1);
				if (arr1 == null) {
					break;
				}
				for (int i = 0; i < arr1.length; i++) {
					if (subConflict(row, col, arr1[i], 2)) {
						// arr[i] is current word with rightLength
						updateAll(row, col, arr1[i], 2, index);
						break;
					}
				}

				// down then
				int downLength1 = findDownLength(row, col);
				String[] arr2 = findArrayWithLength(downLength1);
				if (arr2 == null) {
					break;
				}
				for (int i = 0; i < arr2.length; i++) {
					if (subConflict(row, col, arr2[i], 3)) {
						// arr[i] is current word with rightLength
						updateAll(row, col, arr2[i], 3, index);
						break;
					}
				}
				break;
			case 2:
				// right
				int rightLength2 = findRightLength(row, col);
				String[] arr3 = findArrayWithLength(rightLength2);
				if (arr3 == null) {
					break;
				}
				for (int i = 0; i < arr3.length; i++) {
					if (subConflict(row, col, arr3[i], direction)) {
						// arr3[i] is current word with rightLength
						updateAll(row, col, arr3[i], direction, index);
						break;
					}
				}
				break;
			case 3:
				// down
				int downLength3 = findDownLength(row, col);
				String[] arr4 = findArrayWithLength(downLength3);
				if (arr4 == null) {
					break;
				}
				for (int i = 0; i < arr4.length; i++) {
					if (subConflict(row, col, arr4[i], direction)) {
						// arr[i] is current word with rightLength
						updateAll(row, col, arr4[i], direction, index);
						break;
					}
				}
				break;
			}
			time++;
			conflictNum = calculateConflictNum();
			// output(table);
			if (conflictNum == 0) {
				find = true;
				break;
			}
			if (time == p) {
				break;
			}
		}
		// // System.out.println("-------------" + find + "----------------");
		// System.out.println("Conflict num = " + conflictNum);
		// output(table);
		return find;
	}

	public int calculateConflictNum() {
		int conflictNum = 0;
		for (int i = 0; i < conflictArr.length; i++) {
			for (int j = 0; j < conflictArr[i].length; j++) {
				if (conflictArr[i][j] != null
						&& !conflictArr[i][j].fillRight
								.equals(conflictArr[i][j].fillDown)) {
					conflictNum++;
				}
			}
		}
		return conflictNum;
	}

	public void updateAll(int row, int col, String word, int direction,
			int index) {
		// update table
		// update confilct[][]
		// update dic
		// update variables[]
		switch (direction) {
		case 2:
			// right
			for (int i = 0; i < word.length(); i++) {
				// 1)update conflict[][]
				Conflict conflict = this.conflictArr[row][col + i];
				updateConflict(conflict, word.charAt(i) + "", direction);
				// 2)update table[][]
				table[row][col + i] = word.charAt(i) + "";
			}
			// 3)update Dic
			if (this.variables.get(index).rightWord != null) {
				this.Dic.add(this.variables.get(index).rightWord);
			}
			this.Dic.remove(word);

			// 4)update variables[]
			variables.get(index).rightWord = word;
			break;
		case 3:
			// down
			for (int i = 0; i < word.length(); i++) {
				// 1)update conflict[][]
				Conflict conflict = this.conflictArr[row + i][col];
				updateConflict(conflict, word.charAt(i) + "", direction);
				// 2)update table[][]
				table[row + i][col] = word.charAt(i) + "";
			}
			// 3)update Dic
			if (this.variables.get(index).downWord != null) {
				this.Dic.add(this.variables.get(index).downWord);
			}
			this.Dic.remove(word);
			// 4)update variables[]
			variables.get(index).downWord = word;
			break;
		}
	}

	public void updateConflict(Conflict conflict, String word, int direction) {
		if (conflict == null)
			return;
		if (conflict.fillDown.equals(conflict.fillRight)) {
			if (conflict.now.equals(word)) {
				return;
			}
		}

		switch (direction) {
		case 2:
			// right
			conflict.now = word;
			conflict.fillRight = word;
			break;
		case 3:
			// down
			conflict.now = word;
			conflict.fillDown = word;
			break;
		}
	}

	public boolean subConflict(int row, int col, String word, int direction) {
		int profit = 0;
		switch (direction) {
		case 2:
			// right
			for (int i = 0; i < word.length(); i++) {
				Conflict conflict = this.conflictArr[row][col + i];
				if (conflict == null) {
					// nothing here
				} else {
					if (conflict.fillRight.equals(conflict.fillDown)) {
						if (!conflict.now.equals(word.charAt(i))) {
							profit--;
						}
					} else {
						if (conflict.fillDown.equals(word.charAt(i) + "")) {
							profit++;
						}
					}
				}
			}

			if (profit > 0)
				return true;
			return false;
		case 3:
			// down
			for (int i = 0; i < word.length(); i++) {
				Conflict conflict = this.conflictArr[row + i][col];
				if (conflict == null) {

				} else {
					if (conflict.fillRight.equals(conflict.fillDown)) {
						if (!conflict.now.equals(word.charAt(i))) {
							profit--;
						}
					} else {
						if (conflict.fillRight.equals(word.charAt(i) + "")) {
							profit++;
						}
					}
				}
			}
			if (profit > 0)
				return true;
			return false;
		}

		return false;
	}

	public String[] findArrayWithLength(int length) {

		ArrayList<String> arr = new ArrayList<String>();
		// for (int i = 0; i < usedWordList.size(); i++) {
		// this.Dic.remove(usedWordList.get(i));
		// }
		String[] dicArray = this.getDicArray();
		if (dicArray == null)
			return null;
		for (int i = 0; i < dicArray.length; i++) {
			if (dicArray[i].length() == length) {
				arr.add(dicArray[i]);
			}
		}
		return arr.toArray(new String[0]);
	}

	public void randomGenerateResult() {
		for (int i = 0; i < variables.size(); i++) {
			node node = variables.get(i);
			int row = node.row;
			int col = node.col;
			int direction = this.getDirection(row, col);
			node.direction = direction;
			// 1 right_down
			// 2 right
			// 3 down
			switch (direction) {
			case 1:
				// right_down
				// right
				int rightLength = findRightLength(row, col);
				String word = getRandomLengthWord(rightLength);
				if (word == null)
					break;
				node.rightWord = word;
				fillRight(row, col, word);

				// down
				int downLength = findDownLength(row, col);
				String word2 = getRandomLengthWord(downLength);
				node.downWord = word2;
				fillDown(row, col, word2);
				break;
			case 2:
				// right
				int rightLength3 = findRightLength(row, col);
				String word3 = getRandomLengthWord(rightLength3);
				if (word3 == null)
					break;
				node.rightWord = word3;
				fillRight(row, col, word3);
				break;
			case 3:
				// down
				int downLength4 = findDownLength(row, col);
				String word4 = getRandomLengthWord(downLength4);
				if (word4 == null)
					break;
				node.downWord = word4;
				fillDown(row, col, word4);
				break;
			}
		}
	}

	public String getRandomLengthWord(int length) {
		String[] dic = getDicArray();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < dic.length; i++) {
			if (dic[i].length() == length) {
				list.add(dic[i]);
			}
		}
		if (list.size() == 0)
			return null;
		int randomNum = getRandomNumber(list.size());
		return list.get(randomNum);
	}

	public int getRandomNumber(int size) {
		return rand.nextInt(size);
	}

	public String[] getDicArray() {

		return this.Dic.toArray(new String[0]);
	}

	public void fillRight(int row, int col, String word) {

		/*
		 * 1) fill the word with right direction 2) calculate the conflicts
		 */
		for (int i = 0; i < word.length(); i++) {
			String temp = table[row][col + i];
			if (!temp.equals("_") && !isNumber(temp)) {
				// has a char in this position
				if (!temp.equals(word.charAt(i) + "")) {
					// shouldbe != now , so conflict occurs
					conflictArr[row][col + i] = new Conflict(word.charAt(i)
							+ "", temp, temp);
				} else {
					conflictArr[row][col + i] = new Conflict(temp, temp, temp);
				}
			} else {
				table[row][col + i] = word.charAt(i) + "";
			}
		}
	}

	public void fillDown(int row, int col, String word) {
		/*
		 * 1) fill the word with down direction 2) calculate the conflicts
		 */
		for (int i = 0; i < word.length(); i++) {
			String temp = table[row + i][col];
			if (!temp.equals("_") && !isNumber(temp)) {
				if (!temp.equals(word.charAt(i) + "")) {
					// conflict occurs
					conflictArr[row + i][col] = new Conflict(temp,
							word.charAt(i) + "", temp);
				} else {
					// no conflict
					conflictArr[row + i][col] = new Conflict(temp, temp, temp);
				}
			} else {
				table[row + i][col] = word.charAt(i) + "";
			}
		}
	}

	public int findRightLength(int row, int col) {
		int rightLength = 0;
		for (int i = col; i < table.length + 1; i++) {
			if (i == table.length) {
				rightLength = table.length - col;
				return rightLength;
			} else if (table[row][i].equals("&")) {
				rightLength = i - col;
			}
		}
		return rightLength;
	}

	public int findDownLength(int row, int col) {
		// System.out.println("row=" + row + " col=" + col);
		int downLength = 0;
		for (int i = row; row < table.length + 1; i++) {
			// System.out.println(table.length + " " + i);
			if (i == table.length) {
				downLength = table.length - row;
				return downLength;
			} else if (table[i][col].equals("&")) {
				downLength = i - row;
			}
		}
		return downLength;
	}

	public void getInput(String args[]) {

		/*
		 * Row & Col input
		 */
		try {
			BufferedReader scan = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[0])));
			String line = scan.readLine();
			if (line.charAt(0) == ' ') {
				line = line.substring(1);
			}
			String[] lineArr = line.split(" ");
			int ROW = Integer.valueOf(lineArr[0]);
			int COL = Integer.valueOf(lineArr[1]);
			/*
			 * new Conflict[ROW][COL]
			 */
			this.conflictArr = new Conflict[ROW][COL];
			this.ROW = ROW;
			this.COL = COL;

			/*
			 * Table input
			 */
			this.table = new String[ROW][COL];
			this.copyTable = new String[ROW][COL];
			for (int i = 0; i < ROW; i++) {
				line = scan.readLine();
				if (line.charAt(0) == ' ') {
					line = line.substring(1);
				}
				line = line.replaceAll("  ", " ");
				lineArr = line.split(" ");
				for (int j = 0; j < COL; j++) {
					this.table[i][j] = lineArr[j];
					this.copyTable[i][j] = lineArr[j];
					if (isNumber(lineArr[j])) {
						// if it is a number , record it position
						variables.add(new node(i, j));
						variablesCopy.add(new node(i, j));
					}
					// if it is not number , go on loop
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Dictionary input
		 */
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[1])));
			String lineFromDic;
			while ((lineFromDic = br.readLine()) != null) {
				// System.out.println(lineFromDic);
				if (!lineFromDic.equals("")) {
					Dic.add(lineFromDic);
					DicCopy.add(lineFromDic);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDirection(int row, int col) {
		if (row == 0 && col == 0) {
			// right_down
			return 1;
		}
		if (col == 0) {
			// right
			return 2;
		}
		if (row == 0) {
			// down
			return 3;
		}
		if (this.table[row][col - 1].equals("&")) {
			// right
			return 2;
		}
		// down
		return 3;
	}

	public boolean isNumber(String arg) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(arg);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public void output(String[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
	}

	class node {
		int row;
		int col;
		String rightWord;
		String downWord;
		int direction;

		node(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}

	class Conflict {
		String now;
		String fillRight;// now
		String fillDown;// if use this word conflict will be solved

		Conflict(String fillRight, String fillDown, String now) {
			this.fillRight = fillRight;
			this.fillDown = fillDown;
			this.now = now;
		}
	}
}
