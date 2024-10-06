package rangetree;

import java.util.Arrays;

/**
 * @author zyulyaev
 * @since 04.01.17
 */
public class Point {
    private final int[] coords;
    private final boolean[] inclusive;
    private final int index;

    public Point(int[] coords) {
    	this.index = 0;
       	this.coords = coords.clone();
    	inclusive = new boolean[coords.length];
        for (int i = 0; i < inclusive.length; i++) {
        	this.inclusive[i] = false;
        }
    }
    
    public Point(int[] coords, boolean[] inclusive) {
    	this.coords = coords.clone();
    	this.inclusive = inclusive.clone();
    	this.index = 0;
    }
    
    public Point(int[] coords, int index) {
    	this.index = index;
       	this.coords = coords.clone();
    	inclusive = new boolean[coords.length];
        for (int i = 0; i < inclusive.length; i++) {
        	this.inclusive[i] = false;
        }
    }
    
    public Point(int[] coords, boolean[] inclusive, int index) {
    	this.coords = coords.clone();
    	this.inclusive = inclusive.clone();
    	this.index = index;
    }

    public int dimension() {
        return coords.length;
    }

    public int get(int dim) {
        return coords[dim];
    }
    
    public boolean getInclusive(int dim) {
    	return inclusive[dim];
    }
    
    public int getIndex() {
    	return index;
    }

    @Override
    public String toString() {
        return Arrays.toString(coords);
    }
}
