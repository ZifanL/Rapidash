package rangetreeboolean;

import java.util.Arrays;

/**
 * @author zyulyaev
 * @since 04.01.17
 */
public class PointNew {
    private final int[] coords;
    private final boolean[] inclusive;

    public PointNew(int[] coords) {
    	this.coords = coords.clone();
    	inclusive = new boolean[coords.length];
        for (int i = 0; i < inclusive.length; i++) {
        	this.inclusive[i] = false;
        }
    }
    
    public PointNew(int[] coords, boolean[] inclusive) {
    	this.coords = coords.clone();
    	this.inclusive = inclusive.clone();
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

    @Override
    public String toString() {
        return Arrays.toString(coords);
    }
}
