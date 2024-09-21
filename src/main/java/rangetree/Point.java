package rangetree;

import java.util.Arrays;

/**
 * @author zyulyaev
 * @since 04.01.17
 */
public class Point {
    private final double[] coords;

    public Point(double[] coords) {
        this.coords = coords.clone();
    }

    public int dimension() {
        return coords.length;
    }

    public double get(int dim) {
        return coords[dim];
    }

    @Override
    public String toString() {
        return Arrays.toString(coords);
    }
}
