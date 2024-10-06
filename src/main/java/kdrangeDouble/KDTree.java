package kdrangeDouble;

import java.sql.SQLSyntaxErrorException;
import java.util.*;

public class KDTree {
	
	private KDTreeHelper<Integer> kd;
	
    public KDTree(double[][] arr) throws KeySizeException, KeyDuplicateException {
    	kd = new KDTreeHelper<Integer>(arr[0].length);
    	List<Integer> indices = new ArrayList<>();
    	for (int i = 0; i < arr.length; i++) {
    	    indices.add(i);
    	}
    	Collections.shuffle(indices);
    	for (int i : indices) kd.insert(arr[i], i);
    }

    public Collection<Integer> query(double[] from, double[] to) throws KeySizeException {
		double[] toEx = new double[to.length];
    	for (int i = 0; i < to.length; i++) toEx[i] = to[i] - 1;
    	return kd.range(from, toEx);
    }
    
    public int queryCount(double[] from, double[] to) throws KeySizeException {
		double[] toEx = new double[to.length];
    	for (int i = 0; i < to.length; i++) toEx[i] = to[i] - 1;
		// System.out.println(Arrays.toString(from) + " " + Arrays.toString(toEx));
        return kd.rangeCount(from, toEx);
    }

}
