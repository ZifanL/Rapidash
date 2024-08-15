package org.dc;

import org.apache.commons.lang3.tuple.Pair;
import trees.AVLTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NCVoter {

    public static void c1(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int id = nameLoc.get("county_id");
        int desc = nameLoc.get("county_desc");
        boolean foundViolation = false;
        Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
        for (int i=0; i < data.length; ++i) {
            if (map.containsKey(data[i][id])) {
                if (map.get(data[i][id]).containsKey(data[i][desc]) && map.get(data[i][id]).size() > 1) {
                    if (!foundViolation) {
                        long end = System.currentTimeMillis();
                        long elapsed = end - start;
                        System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                        foundViolation = true;
                    }
                }
                map.get(data[i][id]).put(data[i][desc], map.get(data[i][id]).get(data[i][desc]) + 1);
            } else {
                map.put(data[i][id], new HashMap<>());
                map.get(data[i][id]).put(data[i][desc], 1);
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void c2(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        boolean foundViolation = false;
        int age = nameLoc.get("age_at_year_end");
        int b = nameLoc.get("birth_year");
        int byear = Integer.MIN_VALUE;
        int ageval = Integer.MAX_VALUE;
        for (int i=0; i < data.length; ++i) {
            byear = Math.max(byear, data[i][b]);
            ageval = Math.min(ageval, data[i][age]);
            if (ageval < byear && !foundViolation) {
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                foundViolation = true;
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
    }

    public static void c2tree(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int age = nameLoc.get("age_at_year_end");
        int b = nameLoc.get("birth_year");
        int byear = Integer.MIN_VALUE;
        int ageval = Integer.MAX_VALUE;
        AVLTree tree = new AVLTree();
        for (int i=0; i < data.length; ++i) {
            byear = Math.max(byear, data[i][b]);
            ageval = Math.min(ageval, data[i][age]);
            tree.insert(data[i][b]);
        }
        long v = 0;
        for (int i=0; i < data.length; ++i) {
            v += tree.count(data[i][age], ">");
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void c3(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int status = nameLoc.get("status_cd");
        int voter = nameLoc.get("voter_status_desc");
        int reason = nameLoc.get("reason_cd");
        boolean foundViolation = false;
        Map<Pair<Integer, Integer>, Map<Integer, Integer>> map = new HashMap<>();
        for (int i=0; i < data.length; ++i) {
            Pair p = Pair.of(data[i][status], data[i][voter]);
            if(map.containsKey(p)) {
                if (map.get(p).size() > 1 && !foundViolation) {
                    foundViolation = true;
                    long end = System.currentTimeMillis();
                    long elapsed = end - start;
                    System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
                }
                if (!map.get(p).containsKey(data[i][reason]))
                    map.get(p).put(data[i][reason], 0);
                map.get(p).put(data[i][reason], map.get(p).get(data[i][reason]) + 1);
            } else {
                map.put(p, new HashMap<>());
                map.get(p).put(data[i][reason], 1);
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }

    public static void c4(int[][] data, Map<String, Integer> nameLoc) {
        long start = System.currentTimeMillis();
        int mail = nameLoc.get("mail_zipcode");
        int zip = nameLoc.get("zip_code");
        int scd = nameLoc.get("state_cd");
        int mst = nameLoc.get("mail_state");
        boolean foundViolation = false;
        Map<Integer, Set<Integer>> scdmap = new HashMap<>();
        Map<Integer, Set<Integer>> mstmap = new HashMap<>();
        for (int i=0; i < data.length; ++i) {
            if (!scdmap.containsKey(data[i][mail])) {
                scdmap.put(data[i][mail], new HashSet<>());
            }
            if (!mstmap.containsKey(data[i][zip])) {
                mstmap.put(data[i][zip], new HashSet<>());
            }
            scdmap.get(data[i][mail]).add(data[i][scd]);
            mstmap.get(data[i][zip]).add(data[i][mst]);
            if (!foundViolation && (scdmap.get(data[i][mail]).size() > 1 || mstmap.get(data[i][zip]).size() > 1)) {
                foundViolation = true;
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                System.out.println("Found the first violation, time elapsed: " + elapsed + " ms.");
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if (!foundViolation) {
        	System.out.println("Found no violation, time elapsed: " + elapsed + " ms.");
        }
        System.out.println("Finished enumeration, time elapsed: " + elapsed + " ms.");
    }
}
