package map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster<T extends Point> {

	private static int[] zoomScale = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 9, 9, 11, 11, 13, 13, 15, 15, 17, 17, 19, 19 };
	private static Map<Integer, Integer> gradeMapper = new HashMap<Integer, Integer>();
	private Map<Integer, Map<String, Marker<T>>> lookup = new HashMap<Integer, Map<String, Marker<T>>>();

	static {
		Arrays.sort(zoomScale);
		for (int i = 0, num = 0; i < zoomScale.length; i++)
			if (!gradeMapper.containsKey(zoomScale[i]))
				gradeMapper.put(zoomScale[i], num++);
	}

	public Cluster() {
		for (int zoom : zoomScale) {
			lookup.put(zoom, new HashMap<String, Marker<T>>());
		}
	}

	public boolean save(T point) {
		for (int zoom : lookup.keySet()) {
			Map<String, Marker<T>> markerMap = lookup.get(zoom);
			double[] center = GeoHash.press(point.getPoint(), gradeMapper.get(zoom));
			String hashCode = String.format("%f-%f", center[0], center[1]);
			Marker<T> marker = markerMap.get(hashCode);
			if (marker == null) {
				marker = new Marker<T>(center);
				markerMap.put(hashCode, marker);
			}
			marker.save(point);
		}
		return true;
	}

	public boolean saveAll(List<T> points) {
		for (T point : points)
			save(point);
		return true;
	}

	public List<Marker<T>> getMarkers(int zoom) {
		if (zoom < 0)
			zoom = 0;
		if (zoom > zoomScale.length - 1)
			zoom = zoomScale.length - 1;
		Map<String, Marker<T>> markerMap = lookup.get(zoomScale[zoom]);
		return new ArrayList<Marker<T>>(markerMap.values());
	}

	public Marker<T> getMarker(int zoom, double[] center) {
		if (zoom < 0)
			zoom = 0;
		if (zoom > zoomScale.length - 1)
			zoom = zoomScale.length - 1;
		Map<String, Marker<T>> markerMap = lookup.get(zoomScale[zoom]);
		return markerMap.get(String.format("%f-%f", center[0], center[1]));
	}

}
