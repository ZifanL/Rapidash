package org.dc;

import org.apache.commons.lang3.tuple.Pair;

import kdrangeDouble.KDTreeHelper;
import kdrangeDouble.KeyDuplicateException;
import kdrangeDouble.KeySizeException;
import rangetreeboolean.PointNew;
import rangetreeboolean.RangeTreeBoolean;
import trees.AVLTree;

import java.util.*;

public class Tax {

    public static void taxq4(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int a = nameLoc.get("AreaCode");
        int p = nameLoc.get("Phone");
        boolean foundViolation = false;
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        for (int i=0; i < data.length; ++i) {
            if (map.containsKey(data[i][a])) {
                if (map.get(data[i][a]).contains(data[i][p])) {
                    if (!foundViolation) {
                        long end = System.currentTimeMillis();
                        long elapsed = end - start;
                        System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                        foundViolation = true;
                    }
                } else {
                    map.get(data[i][a]).add(data[i][p]);
                }
            } else {
                map.put(data[i][a], new HashSet<Integer>());
                map.get(data[i][a]).add(data[i][p]);
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void taxq5(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int z = nameLoc.get("Zip");
        int c = nameLoc.get("City");
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        boolean foundViolation = false;
        for (int i=0; i < data.length; ++i) {
            if (map.containsKey(data[i][z])) {
                if (data[i][c] != map.get(data[i][z])) {
                    if (!foundViolation) {
                        long end = System.currentTimeMillis();
                        long elapsed = end - start;
                        System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                        foundViolation = true;
                    }
                }
            } else {
                map.put(data[i][z], data[i][c]);
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void taxq6(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("State");
        int h = nameLoc.get("HasChild");
        int c = nameLoc.get("ChildExemp");
        boolean foundViolation = false;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i=0; i < data.length; ++i) {
            String str = data[i][s] +  " " + data[i][h];
            if (map.containsKey(str)) {
                if (data[i][c] != map.get(str)) {
                    if (!foundViolation) {
                        long end = System.currentTimeMillis();
                        long elapsed = end - start;
                        System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                        foundViolation = true;
                    }
                }
            } else {
                map.put(str, data[i][c]);
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void taxq7kdtreebool(int[][] data, Map<String, Integer> nameLoc) throws KeyDuplicateException, KeySizeException {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("State");
        int salary = nameLoc.get("Salary");
        int r = nameLoc.get("Rate");
        boolean foundViolation = false;
        Map<Integer, KDTreeHelper<Integer>> statepointsmap = new HashMap<Integer, KDTreeHelper<Integer>>();
        for (int i=0; i < data.length; ++i) {
            if (!statepointsmap.containsKey(data[i][s])) {
                statepointsmap.put(data[i][s], new KDTreeHelper<>(2));
            }
            double[] e = new double[]{data[i][salary], data[i][r]};
            if (statepointsmap.get(data[i][s]).rangeCount(new double[]{data[i][salary]+1, Double.MIN_VALUE}, new double[]{Double.MAX_VALUE, data[i][r]-1}) > 0) {
            	foundViolation = true;
            	break;
            }
            if (statepointsmap.get(data[i][s]).rangeCount(new double[]{Double.MIN_VALUE, data[i][r]+1}, new double[]{data[i][salary]-1, Double.MAX_VALUE}) > 0) {
            	foundViolation = true;
            	break;
            }
            statepointsmap.get(data[i][s]).insert(e, i);
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

    public static void taxq7rangetreebool(int[][] data, Map<String, Integer> nameLoc) throws KeyDuplicateException, KeySizeException {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("State");
        int salary = nameLoc.get("Salary");
        int r = nameLoc.get("Rate");
        boolean foundViolation = false;
        Map<Integer, RangeTreeBoolean> map = new HashMap<Integer, RangeTreeBoolean>();
        for (int  i = 0; i < data.length; ++i) {
            if (!map.containsKey(data[i][s])) {
                map.put(data[i][s], new RangeTreeBoolean());
            }
            RangeTreeBoolean tree = map.get(data[i][s]);
            if (tree.query(new PointNew(new int[]{data[i][salary], Integer.MIN_VALUE}),
                    new PointNew(new int[]{Integer.MAX_VALUE, data[i][r]}))) {
            	foundViolation = true;
                break;
            }
            if (tree.query(new PointNew(new int[]{Integer.MIN_VALUE, data[i][r]}),
                    new PointNew(new int[]{data[i][salary], Integer.MAX_VALUE}))) {
            	foundViolation = true;
                break;
            }
            tree.insert(new PointNew(new int[]{data[i][salary], data[i][r]}));
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

    public static void taxq7enumerate(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int s = nameLoc.get("State");
        int salary = nameLoc.get("Salary");
        int r = nameLoc.get("Rate");
        Map<Integer, List<Pair<Integer, Integer>>> statepointsmap = new HashMap<Integer, List<Pair<Integer, Integer>>>();
        for (int i=0; i < data.length; ++i) {
            if (!statepointsmap.containsKey(data[i][s])) {
                statepointsmap.put(data[i][s], new ArrayList<Pair<Integer, Integer>>());
            }
            statepointsmap.get(data[i][s]).add(Pair.of(data[i][salary], data[i][r]));
        }
        for (Map.Entry<Integer, List<Pair<Integer, Integer>>> e : statepointsmap.entrySet()) {
            Collections.sort(e.getValue(), new Comparator<Pair<Integer, Integer>>() {
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
        for (Map.Entry<Integer, List<Pair<Integer, Integer>>> e : statepointsmap.entrySet()) {
            AVLTree t = new AVLTree();
            for (Pair<Integer, Integer> p : e.getValue()) {
                c += t.count(p.getRight(), "<");
                t.insert(p.getRight());
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }
}
