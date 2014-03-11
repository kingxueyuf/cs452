import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class hw1 {

	public City resultCity;

	public static void main(String args[]) {
		hw1 hw = new hw1();
		String inputPath = args[0];
		hw.getResult(inputPath);
	}

	public void getResult(String inPath) {
		ArrayList<City> cityList = new ArrayList<City>();

		Comparator<City> order = new Comparator<City>() {
			@Override
			public int compare(City o1, City o2) {
				// TODO Auto-generated method stub
				// System.out.println("o1.f = " + o1.f + " o2.f = " + o2.f);
				if (o1.f > o2.f) {
					// System.out.println("1");
					return 1;
				}
				if (o1.f < o2.f) {
					// System.out.println("-1");
					return -1;
				} else {
					return 0;
				}
			}
		};

		PriorityQueue<City> P_Q = new PriorityQueue<City>(11, order);
		ArrayList<City> close = new ArrayList<City>();

		try {
			BufferedReader scan = new BufferedReader(new InputStreamReader(
					new FileInputStream(inPath)));

			int cityNum = Integer.valueOf(scan.readLine());
			int[][] distance = new int[cityNum][cityNum];
			for (int i = 0; i < cityNum; i++) {
				City city = new City();
				String nextLine = scan.readLine();
				String[] array = nextLine.split(" ");

				city.name = array[0];
				int j = 1;
				for (; j < array.length; j++) {
					try {
						Integer.valueOf(array[j]);
						break;
					} catch (NumberFormatException e) {
						city.name += " " + array[j];
					}
				}
				city.x = Integer.valueOf(array[j]);
				city.y = Integer.valueOf(array[j + 1]);
				city.rank = i;
				cityList.add(city);
			}
			for (int i = 0; i < cityNum; i++) {
				String next = scan.readLine();
				String[] array1 = next.split(" ");
				for (int j = 0; j < cityNum; j++) {
					if (array1[j].equals("x")) {
						distance[i][j] = -1;
					} else {
						distance[i][j] = Integer.valueOf(array1[j]);
					}
				}
			}
			String startCity = scan.readLine().replace(" ", "").split("=")[1];
			String endCity = scan.readLine().replace(" ", "").split("=")[1];
			// System.out.println(endCity);
			int indexOfStartCity;
			int indexOfEndCity;
			int endX = 0;
			int endY = 0;

			for (int i = 0; i < cityNum; i++) {
				if (cityList.get(i).name.equals(startCity)) {
					indexOfStartCity = i;
					close.add(cityList.get(i));
				}
				if (cityList.get(i).name.equals(endCity)) {
					indexOfEndCity = i;
					endX = cityList.get(i).x;
					endY = cityList.get(i).y;
				}
			}

			while (true) {
				City lastInClose = close.get(close.size() - 1);
				// 从close队列找出最后一个node
				// expand this node
				for (int i = 0; i < cityNum; i++) {
					// 找到所有和当前city连接的city
					// 计算G(起点到当前点的距离) || H(当前点到终点的预估距离)
					int tempDis = distance[lastInClose.rank][i];
					if (tempDis == -1 || tempDis == 0) {
						continue;
					} else {
						if (close.contains(cityList.get(i)))
							continue;

						City temp = new City();
						int tempX = cityList.get(i).x;
						int tempY = cityList.get(i).y;
						int tempH = tempDis;
						int tempG = huresticFunc(tempX, tempY, endX, endY);
						temp.g = tempG;
						temp.h = tempH + lastInClose.h;
						temp.f = temp.g + temp.h;
						temp.name = cityList.get(i).name;
						temp.rank = cityList.get(i).rank;

						if (P_Q.contains(cityList.get(i))) {
							testWhetherChangeFather(temp, P_Q, lastInClose);
						} else {
							temp.father = lastInClose;// 给当前节点设置父节点
							P_Q.add(temp);// add to open queue
						}
					}
				}
				/*
				 * after expand this node and add all his child node into open
				 * queue
				 */
				City city = P_Q.poll();
				close.add(city);
				if (city == null) {
					if (P_Q.size() == 0) {
						System.out.println("fail");
						break;
					}
				}
				if (city.name.equals(endCity)) {
					this.resultCity = city;
					outputResult(this.resultCity, close.size(), P_Q.size());
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	public void outputResult(City city, int closeSize, int p_qSize) {
		City point = city;
		ArrayList<City> list = new ArrayList<City>(20);
		while (point != null) {
			list.add(point);
			point = point.father;
		}
		System.out.print("Route found:");
		for (int i = list.size() - 1; i > -1; i--) {
			System.out.print(" ");
			System.out.print(list.get(i).name);
			if (i != 0) {
				System.out.print(" ->");
			}
		}
		System.out.println();
		System.out.println("Distance: " + city.h + " miles");
		System.out.println();
		System.out.println("Total nodes generated: " + closeSize);
		System.out.println("Left in open list: " + p_qSize);

	}

	public void testWhetherChangeFather(City newPath, PriorityQueue<City> p_q,
			City father) {

		// 新路线到node的F值是否小于老路线到node的F值
		// 小于->改变其父节点
		// 大于->do nothing

		Iterator<City> it = p_q.iterator();
		while (it.hasNext()) {
			City city = it.next();
			if (city.name.equals(newPath.name)) {
				if (newPath.f < city.f) {
					city.father = father;
					city.f = newPath.f;
					city.h = newPath.h;
				}
			}
		}
	}

	public int huresticFunc(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	class City {
		String name;
		int x;
		int y;
		int rank;
		int g = 0;// 起点到该点的距离
		int h = 0;// 该点到终点的距离
		int f = 0;
		City father;

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if (((City) obj).getName() == this.name)
				return true;
			return false;
		}

		public String getName() {
			return this.name;
		}

	}
}
