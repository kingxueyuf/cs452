import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hw2_dfs {

	public String[][] table;
	public HashSet<String> Dic = new HashSet<String>();
	public ArrayList<node> variables = new ArrayList<node>();

	public static void main(String args[]) {

		hw2_dfs hw = new hw2_dfs();
		// dictionary_simple.txt
		// dictionary_medium.txt
		// dictionary_large.txts
		args = new String[] { "C:\\Users\\robin-xue\\Desktop\\inputfiles\\dictionary_medium.txt" };
		hw.solve(args);
	}

	/**
	 * Do search 1) store input with appropriate data structure 2) do local CSP
	 * search
	 * 
	 * @param args
	 */
	public void solve(String args[]) {

		getInput(args);
		searchWithLocalCSP();
	}

	/**
	 * Use appropriate data structure to handle input
	 * 
	 * @param args
	 */
	public void getInput(String args[]) {
		Scanner scan = new Scanner(System.in);

		/*
		 * Row & Col input
		 */
		String line = scan.nextLine();
		if (line.charAt(0) == ' ') {
			line = line.substring(1);
		}
		String[] lineArr = line.split(" ");
		int ROW = Integer.valueOf(lineArr[0]);
		int COL = Integer.valueOf(lineArr[1]);
		/*
		 * Table input
		 */
		this.table = new String[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			line = scan.nextLine();
			if (line.charAt(0) == ' ') {
				line = line.substring(1);
			}
			line = line.replaceAll("  ", " ");
			lineArr = line.split(" ");
			for (int j = 0; j < COL; j++) {
				this.table[i][j] = lineArr[j];
				if (isNumber(lineArr[j])) {
					// if it is a number , record it position
					variables.add(new node(i, j));
				}
				// if it is not number , go on loop
			}
		}
		output(table);

		/*
		 * Dictionary input
		 */
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[0])));
			String lineFromDic;
			while ((lineFromDic = br.readLine()) != null) {
				System.out.println(lineFromDic);
				Dic.add(lineFromDic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use local CSP strategy to search
	 */
	public void searchWithLocalCSP() {
		int start = 0;
		boolean res = doRecursion(start);
		if (res) {
			System.out.println("success");
			output(table);
		} else {
			System.out.println("failure");
		}
	}

	public void output(String[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
	}

	public boolean doRecursion(int start) {
		System.out.println("start = " + start);
		output(table);
		if (start == variables.size()) {
			// success
			return true;
		}
		/**
		 * 1.find direction 2.find domain 3.fill the table with one item in
		 * domain
		 * 
		 */
		node n = variables.get(start);
		int row = n.row;
		int col = n.col;
		int direction = getDirection(row, col);
		switch (direction) {
		case 1:
			// right_down
			String[] domain = findProperWord(row, col, 1);
			for (int i = 0; i < domain.length; i++) {
				/**
				 * for each possible double item the 2-d array is like
				 * "APPLES_AND" "ANSWER_AND" "ORANGE_OIL" "IPHONE_INK"
				 */
				String temp = domain[i];
				String[] tempArr = temp.split("_"); // "APPLES_AND"
				fillRight(row, col, tempArr[0]);// fill "APPLE" right
				fillDown(row, col, tempArr[1]);// fill "AND" down
				output(table);
				Dic.remove(tempArr[0]);
				Dic.remove(tempArr[1]);
				boolean res = doRecursion(start + 1);
				if (res == true) {
					return true;
				}
				cleanLastInputOnTable(row, col, direction);
				output(table);
				Dic.add(tempArr[0]);
				Dic.add(tempArr[1]);
			}
			return false;
		case 2:
			// right
			String[] domain2 = findProperWord(row, col, 2);
			if (domain2.length == 0) {
				return false;
			}
			for (int i = 0; i < domain2.length; i++) {
				String temp = domain2[i];
				fillRight(row, col, temp);
				output(table);
				Dic.remove(temp);
				boolean res2 = doRecursion(start + 1);
				if (res2) {
					return true;
				}
				cleanLastInputOnTable(row, col, direction);
				output(table);
				Dic.add(temp);
			}
			break;
		case 3:
			// down
			String[] domain3 = findProperWord(row, col, 3);
			if (domain3.length == 0) {
				return false;
			}
			for (int i = 0; i < domain3.length; i++) {
				String temp = domain3[i];
				fillDown(row, col, temp);
				output(table);
				Dic.remove(temp);
				boolean res3 = doRecursion(start + 1);
				if (res3) {
					return true;
				}
				cleanLastInputOnTable(row, col, direction);
				output(table);
				Dic.add(temp);
			}
			break;
		}
		return false;
	}

	public ArrayList<String> findWordWithLength(int length) {
		ArrayList<String> possibleRes = new ArrayList<String>();
		String[] arr = this.Dic.toArray(new String[0]);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].length() == length) {
				possibleRes.add(arr[i]);
			}
		}
		return possibleRes;
	}

	public String[] findProperWord(int row, int col, int type) {
		switch (type) {
		case 2:
			// right
			int rightLength2 = findRightLength(row, col);

			/*
			 * find word length == rightLength2 and add it into ArrayList
			 */
			ArrayList<String> possibleRes2 = findWordWithLength(rightLength2);

			/*
			 * use loop to scan word in the ArrayList upon and find string that
			 * match the table
			 */
			ArrayList<String> domain3 = new ArrayList<String>();
			Boolean find2 = true;
			for (int i = 0; i < possibleRes2.size(); i++) {
				String temp2 = possibleRes2.get(i);
				find2 = true;
				for (int j = 0; j < rightLength2; j++) {
					if (!isNumber(table[row][col + j])
							&& !table[row][col + j].equals("_")) {
						// compare if it is not digit or "_"
						if (!table[row][col + j].equals(temp2.charAt(j) + "")) {
							find2 = false;
							break;
						}
					}
				}
				if (find2) {
					domain3.add(temp2);
				}
			}
			return domain3.toArray(new String[0]);

		case 3:
			// down
			// find length of down word
			int downLength2 = findDownLength(row, col);
			/*
			 * find word length == downLength2 and add it into ArrayList
			 */
			ArrayList<String> possibleRes = findWordWithLength(downLength2);
			/*
			 * use loop scan word in the ArrayList upon and find string that
			 * match the table;
			 */
			ArrayList<String> domain2 = new ArrayList<String>();
			Boolean find = true;
			for (int i = 0; i < possibleRes.size(); i++) {
				String temp = possibleRes.get(i);
				find = true;
				for (int j = 0; j < downLength2; j++) {
					if (!isNumber(table[row + j][col])
							&& !table[row + j][col].equals("_")) {
						// compare if it is not digit or "_"
						if (!table[row + j][col].equals(temp.charAt(j) + "")) {
							find = false;
							break;
						}
					}
				}
				if (find) {
					domain2.add(temp);
				}
			}
			return domain2.toArray(new String[0]);

		case 1:
			// right _ down
			// find length of right word
			int rightLength = findRightLength(row, col);
			// find length of down word
			int downLength = findDownLength(row, col);

			// find the domain array domain[]
			String[] dic = this.Dic.toArray(new String[0]);
			ArrayList<String> domain = new ArrayList<String>();
			ArrayList<String> rightDomain = new ArrayList<String>();
			ArrayList<String> downDomain = new ArrayList<String>();
			for (int i = 0; i < dic.length; i++) {
				if (dic[i].length() == rightLength) {
					rightDomain.add(dic[i]);
				}
				if (dic[i].length() == downLength) {
					downDomain.add(dic[i]);
				}
			}
			if (rightLength == downLength) {
				for (int i = 0; i < rightDomain.size(); i++) {
					for (int j = 0; j < downDomain.size(); j++) {
						if (i != j
								&& rightDomain.get(i).charAt(0) == downDomain
										.get(j).charAt(0)) {
							domain.add(rightDomain.get(i) + "_"
									+ downDomain.get(j));// right_down
						}
					}
				}
			} else {
				for (int i = 0; i < rightDomain.size(); i++) {
					for (int j = 0; j < downDomain.size(); j++) {
						if (rightDomain.get(i).charAt(0) == downDomain.get(j)
								.charAt(0))
							domain.add(rightDomain.get(i) + "_"
									+ downDomain.get(j));
					}
				}
			}

			return domain.toArray(new String[0]);
		}
		return null;
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
		System.out.println("row=" + row + " col=" + col);
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

	public void fillRight(int row, int col, String word) {

		for (int i = 0; i < word.length(); i++) {
			table[row][col + i] = word.charAt(i) + "";
		}
	}

	public void fillDown(int row, int col, String word) {
		for (int i = 0; i < word.length(); i++) {
			table[row + i][col] = word.charAt(i) + "";
		}
	}

	public void cleanLastInputOnTable(int row, int col, int direction) {
		switch (direction) {
		case 2:
			// right
			cleanRight(row, col);
			break;
		case 3:
			// down
			cleanDown(row, col);
			break;
		case 1:
			// righ_down
			cleanRight(row, col);
			cleanDown(row, col);
			break;
		}
	}

	public void cleanDown(int row, int col) {
		for (int i = row; i < table.length; i++) {
			if (this.table[i][col].equals("&"))
				break;
			this.table[i][col] = "";
		}
	}

	public void cleanRight(int row, int col) {
		for (int i = col; i < table.length; i++) {
			if (this.table[row][i].equals("&"))
				break;
			this.table[row][i] = "";
		}
	}

	public int getDirection(int row, int col) {
		if (row == 0 && col == 0) {
			return 1;
		}
		if (col == 0) {
			return 2;
		}
		if (row == 0) {
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

	class node {
		int row;
		int col;
		ArrayList<String> domain = new ArrayList<String>();

		node(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
}
