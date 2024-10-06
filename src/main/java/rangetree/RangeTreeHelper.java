package rangetree;

public class RangeTreeHelper {
	private RangeTreeCount rt;
	
	public RangeTreeHelper() {
		rt = new RangeTreeCount();
	}
	
	public void insert(int [] key, int value) {
		Point p = new Point(key);
		rt.insert(p);
	}
	
	public long rangeCount(int [] lowk, int [] uppk) {
		boolean [] inclusive = new boolean[lowk.length];
		for (int i = 0; i < inclusive.length; i++) {
			inclusive[i] = true;
		}
		Point low = new Point(lowk, inclusive);
		Point up = new Point(uppk, inclusive);
		return rt.query(low, up);
	}
}
