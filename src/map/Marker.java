package map;

import java.util.ArrayList;
import java.util.List;

public class Marker<T extends Point> {

	private double[] center;
	private List<T> points = new ArrayList<T>();

	public Marker(double[] center) {
		this.center = center;
	}

	public double[] getCenter() {
		return center;
	}

	public List<T> getPoints() {
		return points;
	}

	public boolean add(T point) {
		if (!points.contains(point))
			return points.add(point);
		return false;
	}

	public boolean equals(Object object) {
		if (object instanceof double[]) {
			double[] other = (double[]) object;
			return center[0] == other[0] && center[1] == other[1];
		}
		if (object instanceof Marker) {
			double[] other = ((Marker) object).getCenter();
			return center[0] == other[0] && center[1] == other[1];
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode();
	}

}
