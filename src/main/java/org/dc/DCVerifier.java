package org.dc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kdrange.KDTree;
import kdrange.KDTreeHelper;
import kdrange.KeyDuplicateException;
import kdrange.KeySizeException;
import rangetree.RangeTreeHelper;
import trees.AVLTree;

public class DCVerifier {
	ArrayList<Constraint> atomDCs = new ArrayList<Constraint>();
	InputTable input;
	
	public DCVerifier(Constraint DC, InputTable input) {
		this.atomDCs = DC.decompose();
		System.out.println("Constraints after decomposition:");
		for (Constraint atomDC : atomDCs) {
			System.out.println("- " + atomDC);
		}
		this.input = input;
	}
	
	public long detectViolation(boolean earlyStop, String treeType) throws KeySizeException, KeyDuplicateException {
		long violationCount = 0;
		for (Constraint DC : atomDCs) {
			violationCount += detectViolationSingle(DC, earlyStop, treeType);
			if (earlyStop && violationCount > 0) {
				return violationCount;
			}
		}
		return violationCount;
	}
	
	private long detectViolationSingle(Constraint DC, boolean earlyStop, String treeType) throws KeySizeException, KeyDuplicateException {
		System.out.println("Detecting violations for: " + DC);
		ArrayList<Integer> homoEqLocs = new ArrayList<Integer>();
		// Column indices and operator of the inequalities
		ArrayList<Integer> uneqLocs1 = new ArrayList<Integer>();
		ArrayList<Integer> uneqLocs2 = new ArrayList<Integer>();
		ArrayList<String> ops = new ArrayList<String>();
		for (Predicate pred : DC.predicates) {
			if (pred.operator.equals("==")) {
				homoEqLocs.add(this.input.nameLoc.get(pred.column1));
			} else {
				uneqLocs1.add(this.input.nameLoc.get(pred.column1));
				uneqLocs2.add(this.input.nameLoc.get(pred.column2));
				ops.add(pred.operator);
			}
		}
		if (ops.size() == 0) {
			System.out.println("[Type] Equation-only DC");
			return countDups(homoEqLocs, earlyStop);
		}
		if (ops.size() == 1) {
			if (uneqLocs1.equals(uneqLocs2)) {
				System.out.println("[Type] Homogeneous DC (one inequality)");
				return countViolationsAVLTreeHomo(homoEqLocs, uneqLocs1.get(0), ops.get(0), earlyStop);
			} else {
				System.out.println("[Type] Heterogeneous DC (one inequality)");
				return countViolationsAVLTreeHeter(homoEqLocs, uneqLocs1.get(0), uneqLocs2.get(0), ops.get(0), earlyStop);
			}
		}
		
		if (uneqLocs1.equals(uneqLocs2)) {
			System.out.println("[Type] Homogeneous DC (multiple inequalities), using " + treeType);
			if (treeType.equals("kd-tree")) {
				return countViolationsKDTreeHomo(homoEqLocs, uneqLocs1, ops, earlyStop);
			} else {
				return countViolationsRangeTreeHomo(homoEqLocs, uneqLocs1, ops, earlyStop);
			}
		} else {
			System.out.println("[Type] Heterogeneous DC (multiple inequalities), using " + treeType);
			if (treeType.equals("kd-tree")) {
				return countViolationsKDTreeHeter(homoEqLocs, uneqLocs1, uneqLocs2, ops, earlyStop);
			} else {
				return countViolationsRangeTreeHeter(homoEqLocs, uneqLocs1, uneqLocs2, ops, earlyStop);
			}
		}
	}
	
	private String reverseOp(String op) {
		if (op.equals(">")) return "<";
		if (op.equals("<")) return ">";
		if (op.equals(">=")) return "<=";
		if (op.equals("<=")) return ">=";
		return op;
	}
	
	private ArrayList<String> reverseOp(ArrayList<String> ops) {
		ArrayList<String> opsReversed = new ArrayList<>();
		for (String op : ops) {
			opsReversed.add(reverseOp(op));
		}
		return opsReversed;
	}
	
	private void computeBounds(int[] values, int[] upper, int[] lower, ArrayList<String> ops) {
		for (int k = 0; k < ops.size(); k++) {
			if (ops.get(k).equals(">")) {lower[k] = values[k] + 1; upper[k] = Integer.MAX_VALUE; continue;}
			if (ops.get(k).equals("<")) {lower[k] = Integer.MIN_VALUE; upper[k] = values[k] - 1; continue;}
			if (ops.get(k).equals(">=")) {lower[k] = values[k]; upper[k] = Integer.MAX_VALUE; continue;}
			if (ops.get(k).equals("<=")) {lower[k] = Integer.MIN_VALUE; upper[k] = values[k]; continue;}
		}
	}
	
	private long countViolationsRangeTreeHomo(ArrayList<Integer> homoEqLocs, ArrayList<Integer> uneqLocs,
			ArrayList<String> ops, boolean earlyStop) {
	    /*
	     * Use range trees to find violations.
	     * The columns on the left-hand-side and the right-hand-side are the same.
	     */
		long violationCount = 0;
		Map<List<Integer>, RangeTreeHelper> treesMap = new HashMap<>();
    	List<Integer> indices = new ArrayList<>();
    	for (int i = 0; i < input.data.length; i++) {
    	    indices.add(i);
    	}
    	Collections.shuffle(indices);
    	for (int i : indices) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
	        int[] ineqValues = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValues[k] = input.data[i][uneqLocs.get(k)];
	        }
			if (treesMap.containsKey(eqValues)) {
				int[] upperBound = new int[ops.size()];
				int[] lowerBound = new int[ops.size()];
				computeBounds(ineqValues, upperBound, lowerBound, ops);
				violationCount += treesMap.get(eqValues).rangeCount(lowerBound, upperBound);
				computeBounds(ineqValues, upperBound, lowerBound, reverseOp(ops));
				violationCount += treesMap.get(eqValues).rangeCount(lowerBound, upperBound);
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMap.put(eqValues, new RangeTreeHelper());
			}
			treesMap.get(eqValues).insert(ineqValues, i);
		}	
		return violationCount;
	}
	
	private long countViolationsRangeTreeHeter(ArrayList<Integer> homoEqLocs, ArrayList<Integer> uneqLocsLeft,
			ArrayList<Integer> uneqLocsRight, ArrayList<String> ops, boolean earlyStop) {
	    /*
	     * Use range trees to find violations.
	     * The columns on the left-hand-side and the right-hand-side are different.
	     */
		long violationCount = 0;
		
		Map<List<Integer>, RangeTreeHelper> treesMapAsLeftSide = new HashMap<>();
		Map<List<Integer>, RangeTreeHelper> treesMapAsRightSide = new HashMap<>();
		
    	List<Integer> indices = new ArrayList<>();
    	for (int i = 0; i < input.data.length; i++) {
    	    indices.add(i);
    	}
    	Collections.shuffle(indices);
    	for (int i : indices) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
	        int[] ineqValuesLeft = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValuesLeft[k] = input.data[i][uneqLocsLeft.get(k)];
	        }
	        int[] ineqValuesRight = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValuesRight[k] = input.data[i][uneqLocsRight.get(k)];
	        }
			
			if (treesMapAsLeftSide.containsKey(eqValues)) {
				int[] upperBound = new int[ops.size()];
				int[] lowerBound = new int[ops.size()];
				computeBounds(ineqValuesRight, upperBound, lowerBound, ops);
				violationCount += treesMapAsLeftSide.get(eqValues).rangeCount(lowerBound, upperBound);
				
				computeBounds(ineqValuesLeft, upperBound, lowerBound, reverseOp(ops));
				violationCount += treesMapAsRightSide.get(eqValues).rangeCount(lowerBound, upperBound);
				
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMapAsLeftSide.put(eqValues, new RangeTreeHelper());
				treesMapAsRightSide.put(eqValues, new RangeTreeHelper());
			}
			
			treesMapAsLeftSide.get(eqValues).insert(ineqValuesLeft, i);
			treesMapAsRightSide.get(eqValues).insert(ineqValuesRight, i);
		}
		return violationCount;
	}
	
	private long countViolationsKDTreeHomo(ArrayList<Integer> homoEqLocs, ArrayList<Integer> uneqLocs,
			ArrayList<String> ops, boolean earlyStop) throws KeySizeException, KeyDuplicateException {
	    /*
	     * Use kd trees to find violations.
	     * The columns on the left-hand-side and the right-hand-side are the same.
	     */
		long violationCount = 0;
		Map<List<Integer>, KDTreeHelper<Integer>> treesMap = new HashMap<>();
    	List<Integer> indices = new ArrayList<>();
    	for (int i = 0; i < input.data.length; i++) {
    	    indices.add(i);
    	}
    	Collections.shuffle(indices);
    	for (int i : indices) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
	        int[] ineqValues = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValues[k] = input.data[i][uneqLocs.get(k)];
	        }
			if (treesMap.containsKey(eqValues)) {
				int[] upperBound = new int[ops.size()];
				int[] lowerBound = new int[ops.size()];
				computeBounds(ineqValues, upperBound, lowerBound, ops);
				violationCount += treesMap.get(eqValues).rangeCount(lowerBound, upperBound);
				computeBounds(ineqValues, upperBound, lowerBound, reverseOp(ops));
				violationCount += treesMap.get(eqValues).rangeCount(lowerBound, upperBound);
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMap.put(eqValues, new KDTreeHelper<>(ops.size()));
			}
			treesMap.get(eqValues).insert(ineqValues, i);
		}	
		return violationCount;
	}
	
	private long countViolationsKDTreeHeter(ArrayList<Integer> homoEqLocs, ArrayList<Integer> uneqLocsLeft,
			ArrayList<Integer> uneqLocsRight, ArrayList<String> ops, boolean earlyStop) throws KeySizeException, KeyDuplicateException {
	    /*
	     * Use KD trees to find violations.
	     * The columns on the left-hand-side and the right-hand-side are different.
	     */
		long violationCount = 0;
		
		Map<List<Integer>, KDTreeHelper<Integer>> treesMapAsLeftSide = new HashMap<>();
		Map<List<Integer>, KDTreeHelper<Integer>> treesMapAsRightSide = new HashMap<>();
		
    	List<Integer> indices = new ArrayList<>();
    	for (int i = 0; i < input.data.length; i++) {
    	    indices.add(i);
    	}
    	Collections.shuffle(indices);
    	for (int i : indices) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
	        int[] ineqValuesLeft = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValuesLeft[k] = input.data[i][uneqLocsLeft.get(k)];
	        }
	        int[] ineqValuesRight = new int[ops.size()];
	        for (int k = 0; k < ops.size(); k++) {
	            ineqValuesRight[k] = input.data[i][uneqLocsRight.get(k)];
	        }
			
			if (treesMapAsLeftSide.containsKey(eqValues)) {
				int[] upperBound = new int[ops.size()];
				int[] lowerBound = new int[ops.size()];
				computeBounds(ineqValuesRight, upperBound, lowerBound, ops);
				violationCount += treesMapAsLeftSide.get(eqValues).rangeCount(lowerBound, upperBound);
				
				computeBounds(ineqValuesLeft, upperBound, lowerBound, reverseOp(ops));
				violationCount += treesMapAsRightSide.get(eqValues).rangeCount(lowerBound, upperBound);
				
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMapAsLeftSide.put(eqValues, new KDTreeHelper<>(ops.size()));
				treesMapAsRightSide.put(eqValues, new KDTreeHelper<>(ops.size()));
			}
			
			treesMapAsLeftSide.get(eqValues).insert(ineqValuesLeft, i);
			treesMapAsRightSide.get(eqValues).insert(ineqValuesRight, i);
		}
		return violationCount;
	}
	
	private long countViolationsAVLTreeHomo(ArrayList<Integer> homoEqLocs, Integer uneqLoc,
			String op, boolean earlyStop) {
	    /*
	     * Use AVL trees to find violations when there is only one inequality.
	     * The columns on the left-hand-side and the right-hand-side are the same.
	     */
		long violationCount = 0;
		Map<List<Integer>, AVLTree> treesMap = new HashMap<>();
		for (int i = 0; i < input.data.length; ++i) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
			if (treesMap.containsKey(eqValues)) {
				violationCount += treesMap.get(eqValues).count(input.data[i][uneqLoc], op);
				violationCount += treesMap.get(eqValues).count(input.data[i][uneqLoc], reverseOp(op));
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMap.put(eqValues, new AVLTree());
			}
			treesMap.get(eqValues).insert(input.data[i][uneqLoc]);
		}	
		return violationCount;
	}
	
	private long countViolationsAVLTreeHeter(ArrayList<Integer> homoEqLocs, Integer uneqLocLeft,
			Integer uneqLocRight, String op, boolean earlyStop) {
	    /*
	     * Use AVL trees to find violations when there is only one inequality.
	     * The columns on the left-hand-side and the right-hand-side are different.
	     */
		long violationCount = 0;
		Map<List<Integer>, AVLTree> treesMapAsLeftSide = new HashMap<>();
		Map<List<Integer>, AVLTree> treesMapAsRightSide = new HashMap<>();
		for (int i = 0; i < input.data.length; ++i) {
			List<Integer> eqValues = new ArrayList<>();
			for (int j : homoEqLocs) {
				eqValues.add(input.data[i][j]);
			}
			if (treesMapAsLeftSide.containsKey(eqValues)) {
				violationCount += treesMapAsLeftSide.get(eqValues).count(input.data[i][uneqLocRight], op);
				violationCount += treesMapAsRightSide.get(eqValues).count(input.data[i][uneqLocLeft], reverseOp(op));
				if (earlyStop && violationCount > 0) {
					return violationCount;
				}
			} else {
				treesMapAsLeftSide.put(eqValues, new AVLTree());
				treesMapAsRightSide.put(eqValues, new AVLTree());
			}
			treesMapAsLeftSide.get(eqValues).insert(input.data[i][uneqLocLeft]);
			treesMapAsRightSide.get(eqValues).insert(input.data[i][uneqLocRight]);
		}	
		return violationCount;
	}
	
	private long countDups(ArrayList<Integer> colLocs, boolean earlyStop) {
	    /*
	     * Use a hash set to detect violations when the constraint only contains equalities.
	     */
		long violationCount = 0;
		Map<List<Integer>, Integer> counter = new HashMap<>();
		for (int i = 0; i < input.data.length; ++i) {
			List<Integer> row = new ArrayList<>();
			for (int j : colLocs) {
				row.add(input.data[i][j]);
			}
			if (counter.containsKey(row)) {
				violationCount += counter.get(row);
				counter.put(row, counter.get(row) + 2);
				if (earlyStop) {
					return violationCount;
				}
			} else {
				counter.put(row, 2);
			}
		}
		return violationCount;
	}
}
