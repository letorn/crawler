package model.map;

import java.util.List;

public class Marker<T> {

	private Double[] point;
	private List<T> list;

	public Marker(Double[] point) {
		this.point = point;
	}

	public Double[] point() {
		return point;
	}

	public List<T> list() {
		return list;
	}

	public Boolean add(T t) {
		if (!list.contains(t))
			return list.add(t);
		return false;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Marker) {
			Double[] point2 = ((Marker) obj).point();
			if (point != null && point.length == 2 && point2 != null && point2.length == 2)
				return point[0].equals(point2[0]) && point[1].equals(point2[1]);
		}
		return false;
	}

}
