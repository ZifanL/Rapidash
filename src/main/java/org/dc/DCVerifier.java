package org.dc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public long detectViolation(boolean earlyStop) {
		long violationCount = 0;
		for (Constraint DC : atomDCs) {
			violationCount += detectViolationSingle(DC, earlyStop);
			if (earlyStop && violationCount > 0) {
				return violationCount;
			}
		}
		return violationCount;
	}
	
	private long detectViolationSingle(Constraint DC, boolean earlyStop) {
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
				System.out.println("[Type] Homogeneous DC");
				return countViolationsAVLTreeHomo(homoEqLocs, uneqLocs1.get(0), ops.get(0), earlyStop);
			} else {
				System.out.println("[Type] Heterogeneous DC");
				return countViolationsAVLTreeHeter(homoEqLocs, uneqLocs1.get(0), uneqLocs2.get(0), ops.get(0), earlyStop);
			}
			
		}
		return 0;
	}
	
	private String reverseOp(String op) {
		if (op.equals(">")) return "<";
		if (op.equals("<")) return ">";
		if (op.equals(">=")) return "<=";
		if (op.equals("<=")) return ">=";
		return op;
	}
	
	private long countViolationsAVLTreeHomo(ArrayList<Integer> homoEqLocs, Integer uneqLoc,
			String op, boolean earlyStop) {
	    /*
	     * Use AVL trees to find violations when there is only one inequality.
	     * The two columns on the left-hand-side and the right-hand-side are the same.
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
	     * The two columns on the left-hand-side and the right-hand-side are the same.
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
