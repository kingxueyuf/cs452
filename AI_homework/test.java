import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class test {
	private String name;
	private int population;

	public test(String name, int population) {
		this.name = name;
		this.population = population;
	}

	public String getName() {
		return this.name;
	}

	public int getPopulation() {
		return this.population;
	}

	public String toString() {
		return getName() + " - " + getPopulation();
	}

	public static void main(String args[]) {
		Comparator<City> OrderIsdn = new Comparator<City>() {
			public int compare(City o1, City o2) {
				// TODO Auto-generated method stub
				// int numbera = o1.getPopulation();
				// int numberb = o2.getPopulation();
				if (o1.f > o2.f) {
					System.out.println(o1.f + " "+o2.f + " 1");
					return 1;
				} else if (o1.f < o2.f) {
					return -1;
				} else {
					return 0;
				}

			}

		};
		Queue<City> priorityQueue = new PriorityQueue<City>(11, OrderIsdn);

		City t1 = new City(102);
		City t2 = new City(1341);
		City t3 = new City(141);

		priorityQueue.add(t1);
		priorityQueue.add(t2);
		priorityQueue.add(t3);
		System.out.println(priorityQueue.poll().f);
	}

}

class City {
	City(int f) {
		this.f = f;
	}

	String name;
	int x;
	int y;
	int rank;
	int g = 0;// 起点到该点的距离
	int h = 0;// 该点到终点的距离
	int f = 0;
	City father;
}
