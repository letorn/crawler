package model.map;

import java.util.ArrayList;
import java.util.List;

public class Clusterer<T> {

	private int[] zooms = {5,7,9,11,13,15,};
	private int[] indexes = { 5, 5, 5, 5, 5, 5, 5, 7, 7, 9, 9, 11, 11, 13, 13, 15, 15, 17, 17, 19, 19, 21, 21 };
	private List<List<Marker<T>>> data;

	public Clusterer() {
		data = new ArrayList<List<Marker<T>>>();
		for (int index : indexes)
			data.add(new ArrayList<Marker<T>>());
	}

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>(4);
		list.set(3, "d");
		list.
		System.out.println(list.size());
	}

}
