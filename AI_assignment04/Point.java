

public class Point {

	int x;
	int y;
	double point;

	public Point(int x, int y, double point) {
		this.x = x;
		this.y = y;
		this.point = point;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this.x == ((Point) obj).x && this.y == ((Point) obj).y) {
			if (Math.abs(this.point - ((Point) obj).point) < 0.2) {
				return true;
			}
		}
		return false;
	}

}
