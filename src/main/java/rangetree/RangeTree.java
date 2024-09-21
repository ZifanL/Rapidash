package rangetree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * O(n*log^d(n)) memory and build time
 * O(log^d-1(n)) query time
 *
 * @author zyulyaev
 * @since 04.01.17
 */
public class RangeTree {
    private static final Comparator<Point> POINT_COMPARATOR = (a, b) -> {
        if (a.dimension() != b.dimension())
            return Double.compare(a.dimension(), b.dimension());
        for (int i = a.dimension() - 1; i >= 0; i--)
            if (a.get(i) != b.get(i))
                return Double.compare(a.get(i), b.get(i));
        return 0;
    };

    private final Node root;
    
    public RangeTree(double[][] arr) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
        	points.add(new Point(arr[i]));
        }
        int dimensions = arr[0].length;
        if (dimensions < 2)
            throw new IllegalArgumentException("Only 2-D and greater is supported");
//        List<Point> sorted = points.stream().sorted(POINT_COMPARATOR).collect(Collectors.toList());
//        this.root = buildNode(sorted, dimensions - 1);
        this.root = buildNode(points, dimensions - 1);
    }

    public RangeTree(Collection<Point> points, int dimensions) {
        if (dimensions < 2)
            throw new IllegalArgumentException("Only 2-D and greater is supported");
        List<Point> sorted = points.stream().sorted(POINT_COMPARATOR).collect(Collectors.toList());
        this.root = buildNode(sorted, dimensions - 1);
    }

    private static Node buildNode(List<Point> points, int dimension) {
        if (dimension > 1) {
            List<NodeInfo<Node>> nodes = new ArrayList<>();
            int start = 0;
            double coord = points.get(start).get(dimension);
            for (int i = 1; i <= points.size(); i++) {
                if (i == points.size() || points.get(i).get(dimension) != coord) {
                    Leaf leaf = new Leaf(buildNode(points.subList(start, i), dimension - 1), dimension, coord);
                    nodes.add(new NodeInfo<>(leaf, 0, start, i));

                    for (int size = nodes.size(); size > 1 && nodes.get(size - 2).height == nodes.get(size - 1).height; size--) {
                        NodeInfo last = nodes.remove(size - 1);
                        NodeInfo preLast = nodes.remove(size - 2);
                        List<Point> pts = points.subList(preLast.from, last.to);
                        pts.sort((a, b) -> Double.compare(a.get(dimension - 1), b.get(dimension - 1)));
                        Joint joint = new Joint(buildNode(pts, dimension - 1), dimension, preLast.node, last.node);
                        nodes.add(new NodeInfo<>(joint, last.height + 1, preLast.from, last.to));
                    }

                    start = i;
                    if (start < points.size())
                        coord = points.get(start).get(dimension);
                }
            }

            for (int size = nodes.size(); size > 1; size--) {
                NodeInfo last = nodes.remove(size - 1);
                NodeInfo preLast = nodes.remove(size - 2);
                List<Point> pts = points.subList(preLast.from, last.to);
                pts.sort((a, b) -> Double.compare(a.get(dimension - 1), b.get(dimension - 1)));
                Joint joint = new Joint(buildNode(pts, dimension - 1), dimension, preLast.node, last.node);
                nodes.add(new NodeInfo<>(joint, last.height + 1, preLast.from, last.to));
            }
            return nodes.get(0).node;
        } else {
            List<NodeInfo<CascadeNode>> nodes = new ArrayList<>();
            int start = 0;
            double coord = points.get(start).get(dimension);
            for (int i = 1; i <= points.size(); i++) {
                if (i == points.size() || points.get(i).get(dimension) != coord) {
                    List<Point> pts = points.subList(start, i);
                    pts.sort((a, b) -> Double.compare(a.get(0), b.get(0)));
                    CascadeLeaf leaf = new CascadeLeaf(new ArrayList<>(pts), coord);
                    nodes.add(new NodeInfo<>(leaf, 0, start, i));

                    for (int size = nodes.size(); size > 1 && nodes.get(size - 2).height == nodes.get(size - 1).height; size--) {
                        NodeInfo<CascadeNode> last = nodes.remove(size - 1);
                        NodeInfo<CascadeNode> preLast = nodes.remove(size - 2);
                        CascadeJoint joint = new CascadeJoint(preLast.node, last.node);
                        nodes.add(new NodeInfo<>(joint, last.height + 1, preLast.from, last.to));
                    }

                    start = i;
                    if (start < points.size())
                        coord = points.get(start).get(dimension);
                }
            }
            for (int size = nodes.size(); size > 1; size--) {
                NodeInfo<CascadeNode> last = nodes.remove(size - 1);
                NodeInfo<CascadeNode> preLast = nodes.remove(size - 2);
                CascadeJoint joint = new CascadeJoint(preLast.node, last.node);
                nodes.add(new NodeInfo<>(joint, last.height + 1, preLast.from, last.to));
            }
            return nodes.get(0).node;
        }
    }

    public Collection<Point> query(Point from, Point to) {
        return root.query(from, to);
    }
    
    public Collection<Point> query(double[] from, double[] to) {
        Point fromP = new Point(from);
        Point toP = new Point(to);
    	return root.query(fromP, toP);
    }
    
    public int queryCount(Point from, Point to) {
    	return root.queryCount(from, to);
    }
    
    public int queryCount(double[] from, double[] to) {
        Point fromP = new Point(from);
        Point toP = new Point(to);
    	return root.queryCount(fromP, toP);
    }

    private static boolean inside(double coord, double from, double to) {
        return coord >= from && coord < to;
    }

    private static List<Point> merge(List<Point> left, List<Point> right, int dim) {
        List<Point> result = new ArrayList<>(left.size() + right.size());
        for (int l = 0, r = 0; l < left.size() || r < right.size(); ) {
            if (l < left.size() && r < right.size())
                result.add(left.get(l).get(dim) <= right.get(r).get(dim) ? left.get(l++) : right.get(r++));
            else if (l < left.size())
                result.add(left.get(l++));
            else
                result.add(right.get(r++));
        }
        return result;
    }

    private interface Node {
        Collection<Point> query(Point from, Point to);
        int queryCount(Point from, Point to);

        double getMin();

        double getMax();
    }

    /**
     * @return index of first greater or equal point
     */
    private static int binSearch(List<Point> points, Point from, int dim) {
        int l = -1, r = points.size();
        while (r - l > 1) {
            int m = l + r >> 1;
            if (points.get(m).get(dim) >= from.get(dim))
                r = m;
            else
                l = m;
        }
        return r;
    }

    private static <T> List<T> concat(Collection<? extends T> left, Collection<? extends T> right) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        result.addAll(left);
        result.addAll(right);
        return result;
    }

    private static class Leaf implements Node {
        private final Node inner;
        private final int dimension;
        private final double coord;

        private Leaf(Node inner, int dimension, double coord) {
            this.inner = inner;
            this.dimension = dimension;
            this.coord = coord;
        }

        @Override
        public Collection<Point> query(Point from, Point to) {
            if (coord < from.get(dimension) || coord >= to.get(dimension))
                return Collections.emptyList();
            return inner.query(from, to);
        }
        
        @Override
        public int queryCount(Point from, Point to) {
            if (coord < from.get(dimension) || coord >= to.get(dimension))
                return 0;
            return inner.queryCount(from, to);
        }

        @Override
        public double getMin() {
            return coord;
        }

        @Override
        public double getMax() {
            return coord;
        }
    }

    private static class Joint implements Node {
        private final Node inner;
        private final int dimension;
        private final double min;
        private final double max;

        private final Node left;
        private final Node right;

        private Joint(Node inner, int dimension, Node left, Node right) {
            this.inner = inner;
            this.dimension = dimension;
            this.left = left;
            this.right = right;

            this.min = Math.min(left.getMin(), right.getMin());
            this.max = Math.max(left.getMax(), right.getMax());
        }

        @Override
        public Collection<Point> query(Point from, Point to) {
            if (min >= to.get(dimension) || max < from.get(dimension))
                return Collections.emptyList();
            if (min >= from.get(dimension) && max < to.get(dimension))
                return inner.query(from, to);
            return concat(left.query(from, to), right.query(from, to));
        }
        
        @Override
        public int queryCount(Point from, Point to) {
            if (min >= to.get(dimension) || max < from.get(dimension))
                return 0;
            if (min >= from.get(dimension) && max < to.get(dimension))
                return inner.queryCount(from, to);
            return left.queryCount(from, to) + right.queryCount(from, to);
        }

        @Override
        public double getMin() {
            return min;
        }

        @Override
        public double getMax() {
            return max;
        }
    }

    private static abstract class CascadeNode implements Node {
        final List<Point> points; // ordered by 0 dim

        CascadeNode(List<Point> points) {
            this.points = points;
        }

        @Override
        public Collection<Point> query(Point from, Point to) {
            if (getMax() < from.get(1) || getMin() >= to.get(1))
                return Collections.emptyList();
            return query(from, to, binSearch(points, from, 0));
        }

        Collection<Point> query(Point from, Point to, int hint) {
            if (getMax() < from.get(1) || getMin() >= to.get(1))
                return Collections.emptyList();
            if (getMin() >= from.get(1) && getMax() < to.get(1)) {
                List<Point> result = new ArrayList<>();
                for (int i = hint; i < points.size() && inside(points.get(i).get(0), from.get(0), to.get(0)); i++)
                    result.add(points.get(i));
                return result;
            } else {
                return subQuery(from, to, hint);
            }
        }
        
        @Override
        public int queryCount(Point from, Point to) {
            if (getMax() < from.get(1) || getMin() >= to.get(1))
                return 0;
            if (from.get(0) >= to.get(0))
            	return 0;
            return queryCount(from, to, binSearch(points, from, 0), binSearch(points, to, 0));
        }
        
        int queryCount(Point from, Point to, int hintLow, int hintHigh) {
            if (getMax() < from.get(1) || getMin() >= to.get(1))
                return 0;
            if (getMin() >= from.get(1) && getMax() < to.get(1)) {
                return hintHigh - hintLow;
            } else {
                return subQueryCount(from, to, hintLow, hintHigh);
            }
        }

        abstract Collection<Point> subQuery(Point from, Point to, int hint);
        abstract int subQueryCount(Point from, Point to, int hintLow, int hintHigh);
    }

    private static class CascadeLeaf extends CascadeNode {
        private final double coord; // 1 dim

        private CascadeLeaf(List<Point> points, double coord) {
            super(points);
            this.coord = coord;
        }

        @Override
        public double getMin() {
            return coord;
        }

        @Override
        public double getMax() {
            return coord;
        }

        @Override
        public Collection<Point> subQuery(Point from, Point to, int hint) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int subQueryCount(Point from, Point to, int hintLow, int hintHigh) {
            throw new UnsupportedOperationException();
        }
    }

    private static class CascadeJoint extends CascadeNode {
        private final double min; // 1 dim
        private final double max; // 1 dim

        private final CascadeNode left;
        private final int[] leftRef;

        private final CascadeNode right;
        private final int[] rightRef;

        private CascadeJoint(CascadeNode left, CascadeNode right) {
            super(merge(left.points, right.points, 0));
            this.leftRef = buildRef(points, left.points);
            this.rightRef = buildRef(points, right.points);
            this.min = Math.min(left.getMin(), right.getMin());
            this.max = Math.max(left.getMax(), right.getMax());
            this.left = left;
            this.right = right;
        }

        @Override
        public double getMin() {
            return min;
        }

        @Override
        public double getMax() {
            return max;
        }

        @Override
        public Collection<Point> subQuery(Point from, Point to, int hint) {
            return concat(left.query(from, to, leftRef[hint]), right.query(from, to, rightRef[hint]));
        }
        
        @Override
        public int subQueryCount(Point from, Point to, int hintLow, int hintHigh) {
            return left.queryCount(from, to, leftRef[hintLow], leftRef[hintHigh]) + 
            		right.queryCount(from, to, rightRef[hintLow], rightRef[hintHigh]);
        }

        private static int[] buildRef(List<Point> points, List<Point> subPoints) {
            int[] refs = new int[points.size() + 1];
            refs[points.size()] = subPoints.size();
            for (int i = 0, ref = 0; i < points.size(); ++i) {
                while (ref < subPoints.size() && points.get(i).get(0) > subPoints.get(ref).get(0))
                    ref++;
                refs[i] = ref;
            }
            return refs;
        }
    }

    private static class NodeInfo<N extends Node> {
        private final N node;
        private final int height;
        private final int from;
        private final int to;

        private NodeInfo(N node, int height, int from, int to) {
            this.node = node;
            this.height = height;
            this.from = from;
            this.to = to;
        }
    }
}
