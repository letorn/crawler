package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Marker<T extends Point> {

	private double[] center;
	private Map<Long, Integer> pointIndexes = new HashMap<Long, Integer>();
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
		if (point.getPointId() != null) {
			Integer pointIndex = pointIndexes.get(point.getPointId());
			if (pointIndex == null) {
				points.add(point);
				pointIndexes.put(point.getPointId(), points.size() - 1);
				return true;
			}
		}
		return false;
	}

	public boolean update(T point) {
		if (point.getPointId() != null) {
			Integer pointIndex = pointIndexes.get(point.getPointId());
			if (pointIndex != null) {
				points.set(pointIndex, point);
				return true;
			}
		}
		return false;
	}

	public boolean save(T point) {
		if (point.getPointId() != null) {
			Integer pointIndex = pointIndexes.get(point.getPointId());
			if (pointIndex == null) {
				points.add(point);
				pointIndexes.put(point.getPointId(), points.size() - 1);
			} else {
				points.set(pointIndex, point);
			}
			return true;
		}
		return false;
	}

}
