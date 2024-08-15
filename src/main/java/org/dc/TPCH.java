package org.dc;

import kdrange.KDTreeHelper;
import kdrange.KeyDuplicateException;
import kdrange.KeySizeException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import rangetreeboolean.PointNew;
import rangetreeboolean.RangeTreeBoolean;
import trees.AVLTree;

import java.util.*;

public class TPCH {

    // ¬(t.Customer = t′.Supplier ∧ t.Supplier = t′.Customer)
    public static void tpchq10(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        HashSet<Pair<Integer, Integer>> t = new HashSet<>();
        int cust = nameLoc.get("custkey");
        int supp = nameLoc.get("suppkey");
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            if (t.contains(Pair.of(data[i][supp], data[i][cust]))) {
                if (!foundViolation) {
                    long end = System.currentTimeMillis();
                    long elapsed = end - start;
                    System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                    foundViolation = true;
                }
            }
            t.add(Pair.of(data[i][cust], data[i][supp]));
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void tpchq11enumerate(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long begin = runtime.totalMemory() - runtime.freeMemory();
        int r = nameLoc.get("receiptdate");
        int s = nameLoc.get("shipdate");
        List<Triple<Integer, Integer, Integer>> list = new ArrayList<>();
        for (int i=0; i < data.length; ++i) {
            list.add(Triple.of(data[i][r], data[i][s], 0));
            list.add(Triple.of(data[i][s], data[i][r], 1));
        }
        Collections.sort(list, new Comparator<Triple<Integer, Integer, Integer>>() {
            @Override
            public int compare(final Triple<Integer, Integer, Integer> o1, final Triple<Integer, Integer, Integer> o2) {
                if (o1.getLeft().intValue() < o2.getLeft().intValue())
                    return 1;
                if (o1.getLeft().intValue() == o2.getLeft().intValue())
                    return 0;
                return -1;
            }
        });
        long c = 0;
        AVLTree t = new AVLTree();
        for (Triple<Integer, Integer, Integer> e : list) {
            if (e.getRight() == 0) {
                t.insert(e.getMiddle());
            }
            if (e.getRight() == 1) {
                c += t.count(e.getMiddle(), "<=");
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }


    public static void tpchq11kdtreebool(int[][] data, Map<String, Integer> nameLoc) throws KeyDuplicateException, KeySizeException {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("shipdate");
        int r = nameLoc.get("receiptdate");
        Map<Integer, KDTreeHelper<Integer>> map = new HashMap<Integer, KDTreeHelper<Integer>>();
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            if (!map.containsKey(data[i][s])) {
                map.put(data[i][s], new KDTreeHelper<>(2));
            }
            double[] e = new double[]{data[i][r], data[i][s]};
            if (map.get(data[i][s]).rangeCount(new double[]{data[i][r], Double.MIN_VALUE}, new double[]{Double.MAX_VALUE, data[i][s]}) > 0) {
            	foundViolation = true;
                break;
            }
            if (map.get(data[i][s]).rangeCount(new double[]{Double.MIN_VALUE, data[i][r]}, new double[]{data[i][s], Double.MAX_VALUE}) > 0) {
            	foundViolation = true;
                break;
            }
            map.get(data[i][s]).insert(e, i);
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }

    public static void tpchq11rangetreebool(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("shipdate");
        int r = nameLoc.get("receiptdate");
        RangeTreeBoolean tree = new RangeTreeBoolean();
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            Pair<Integer, Integer> p =Pair.of(data[i][r], data[i][s]);
            if (tree.query(new PointNew(new int[]{data[i][r], Integer.MIN_VALUE}),
                    new PointNew(new int[]{Integer.MAX_VALUE, data[i][s]}))) {
            	foundViolation = true;
                break;
            }
            if (tree.query(new PointNew(new int[]{Integer.MIN_VALUE, data[i][r]}),
                    new PointNew(new int[]{data[i][s], Integer.MAX_VALUE}))) {
            	foundViolation = true;
                break;
            }
            tree.insert(new PointNew(new int[]{data[i][r], data[i][s]}));
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }

    public static void tpchq12kdtreebool(int[][] data, Map<String, Integer> nameLoc) throws KeyDuplicateException, KeySizeException {
        long start = System.currentTimeMillis();
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        KDTreeHelper<Integer> tree = new KDTreeHelper<>(2);
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            double[] tup = new double[]{data[i][e], data[i][d]};
            if (tree.rangeCount(new double[]{data[i][e]+1, Double.MIN_VALUE}, new double[]{Double.MAX_VALUE, data[i][d]-1}) > 0) {
            	foundViolation = true;
                break;
            }
            if (tree.rangeCount(new double[]{Double.MIN_VALUE, data[i][d]+1}, new double[]{data[i][e]-1, Double.MAX_VALUE}) > 0) {
            	foundViolation = true;
                break;
            }
            tree.insert(tup, i);
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }


    public static void tpchq12rangetreebool(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        RangeTreeBoolean tree = new RangeTreeBoolean();
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            if (tree.query(new PointNew(new int[]{data[i][e], Integer.MIN_VALUE}),
                    new PointNew(new int[]{Integer.MAX_VALUE, data[i][d]}))) {
            	foundViolation = true;
                break;
            }
            if (tree.query(new PointNew(new int[]{Integer.MIN_VALUE, data[i][d]}),
                    new PointNew(new int[]{data[i][e], Integer.MAX_VALUE}))) {
            	foundViolation = true;
            	break;
            }
            tree.insert(new PointNew(new int[]{data[i][e], data[i][d]}));
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }


    public static void tpchq12enumerate(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        for (int i=0; i < data.length; ++i) {
            list.add(Pair.of(data[i][e], data[i][d]));
        }
        Collections.sort(list, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(final Pair<Integer, Integer> o1, final Pair<Integer, Integer> o2) {
                if (o1.getLeft().intValue() < o2.getLeft().intValue())
                    return 1;
                if (o1.getLeft().intValue() == o2.getLeft().intValue())
                    return 0;
                return -1;
            }
        });
        long c = 0;
        AVLTree t = new AVLTree();
        for (Pair<Integer, Integer> p : list) {
            c += t.count(p.getRight(), "<");
            t.insert(p.getRight());
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }


    public static void tpchq13kdtreebool(int[][] data, Map<String, Integer> nameLoc) throws KeyDuplicateException, KeySizeException {
        long start = System.currentTimeMillis();
        int q = nameLoc.get("quantity");
        int t = nameLoc.get("tax");
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        boolean foundViolation = false;
        Map<Pair<Integer, Integer>, KDTreeHelper<Integer>> map = new HashMap<Pair<Integer, Integer>, KDTreeHelper<Integer>>();

        for (int i=0; i < data.length; ++i) {
            if (!map.containsKey(Pair.of(data[i][q], data[i][t]))) {
                map.put(Pair.of(data[i][q], data[i][t]), new KDTreeHelper<Integer>(2));
            }
            KDTreeHelper<Integer> tree = map.get(Pair.of(data[i][q], data[i][t]));
            double[] tup = new double[]{data[i][e], data[i][d]};
            if (tree.rangeCount(new double[]{data[i][e]+1, Double.MIN_VALUE}, new double[]{Double.MAX_VALUE, data[i][d]-1}) > 0) {
            	foundViolation = true;
                break;
            }
            if (tree.rangeCount(new double[]{Double.MIN_VALUE, data[i][d]+1}, new double[]{data[i][e]-1, Double.MAX_VALUE}) > 0) {
            	foundViolation = true;
                break;
            }
            tree.insert(tup, i);
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }

    public static void tpchq13rangetreebool(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int q = nameLoc.get("quantity");
        int t = nameLoc.get("tax");
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        boolean foundViolation = false;
        Map<Pair<Integer, Integer>, RangeTreeBoolean> statepointsmap = new HashMap<Pair<Integer, Integer>, RangeTreeBoolean>();
        for (int i=0; i < data.length; ++i) {
            Pair<Integer, Integer> p =Pair.of(data[i][q], data[i][t]);
            if (!statepointsmap.containsKey(p)) {
                statepointsmap.put(p, new RangeTreeBoolean());
            }
            RangeTreeBoolean tree = statepointsmap.get(p);
            if (tree.query(new PointNew(new int[]{data[i][e], Integer.MIN_VALUE}),
                    new PointNew(new int[]{Integer.MAX_VALUE, data[i][d]}))) {
            	foundViolation = true;
                break;
            }
            if (tree.query(new PointNew(new int[]{Integer.MIN_VALUE, data[i][d]}),
                    new PointNew(new int[]{data[i][e], Integer.MAX_VALUE}))) {
            	foundViolation = true;
                break;
            }
            tree.insert(new PointNew(new int[]{data[i][e], data[i][d]}));
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        else {
        	System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
        }
    }


    public static void tpchq13enumerate(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int q = nameLoc.get("quantity");
        int t = nameLoc.get("tax");
        int e = nameLoc.get("extendedprice");
        int d = nameLoc.get("discount");
        Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> map = new HashMap<>();
        for (int i=0; i < data.length; ++i) {
            Pair p = Pair.of(data[i][q], data[i][t]);
            if (!map.containsKey(p)) {
                map.put(p, new ArrayList<Pair<Integer, Integer>>());
            }
            map.get(p).add(Pair.of(data[i][e], data[i][d]));
        }
        for (Map.Entry<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> entry : map.entrySet()) {
            Collections.sort(entry.getValue(), new Comparator<Pair<Integer, Integer>>() {
                @Override
                public int compare(final Pair<Integer, Integer> o1, final Pair<Integer, Integer> o2) {
                    if (o1.getLeft().intValue() < o2.getLeft().intValue())
                        return 1;
                    if (o1.getLeft().intValue() == o2.getLeft().intValue())
                        return 0;
                    return -1;
                }
            });
        }
        long c = 0;
        for (Map.Entry<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> entry : map.entrySet()) {
            AVLTree tree = new AVLTree();
            for (Pair<Integer, Integer> p : entry.getValue()) {
                int count = tree.count(p.getRight(), "<");
                c += count;
                tree.insert(p.getRight());
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }
}
