package org.dc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Constraint {
	ArrayList<Predicate> predicates = new ArrayList<>();
	boolean isSymmetric;
	
	public Constraint(String filePath, Map<String, Integer> nameLoc) {
		this.loadConstraint(filePath, nameLoc);
		this.isSymmetric();
	}
	
	public Constraint(ArrayList<Predicate> predicates) {
		this.predicates.addAll(predicates);
		this.isSymmetric();
	}
	
    public void loadConstraint(String filePath, Map<String, Integer> nameLoc) {
        ArrayList<String> columns1 = new ArrayList<>();
        ArrayList<String> columns2 = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    columns1.add(parts[0]);
                    columns2.add(parts[2]);
                    operators.add(parts[1]);
                }else {
                	System.out.println("Discard \"" + line + "\". A constraint must contain three parts: column1, operator, column2");
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
	    for (int i = 0; i < columns1.size(); i++) {
	        String colName1 = columns1.get(i);
	        String colName2 = columns2.get(i);
	        String opName = operators.get(i);
	        if (nameLoc.get(colName1) == null) throw new IllegalArgumentException("Column " + colName1 + " does not exist! Column list: " + nameLoc.keySet());
	        if (nameLoc.get(colName2) == null) throw new IllegalArgumentException("Column " + colName2 + " does not exist! Column list: " + nameLoc.keySet());
	        
	        if (opName.equals("==") || opName.equals("=")) {
	        	this.predicates.add(new Predicate(colName1, colName2, "=="));
	        } else if (opName.equals("<>") || opName.equals("!=")) {
	        	this.predicates.add(new Predicate(colName1, colName2, "<>"));
	        } else if (opName.equals(">") || opName.equals("<") || opName.equals(">=") || opName.equals("<=")){
	        	this.predicates.add(new Predicate(colName1, colName2, opName));
	        } else {
	        	throw new IllegalArgumentException("Operator " + opName + " is not valid! OPerator should be in [==, <>, <, >, <=, >=]");
	        }
	    }
    }
    
    private void isSymmetric() {
    	for (Predicate pred : this.predicates) {
    		if (!pred.column1.equals(pred.column2) || (!pred.operator.equals("==") && !pred.operator.equals("<>"))) {
    			this.isSymmetric = false;
    			return;
    		}
    	}
    	this.isSymmetric = true;
    }
    
    public ArrayList<Constraint> decompose() {
    	ArrayList<Constraint> DCs = new ArrayList<Constraint>();
    	ArrayList<Predicate> predicates1 = new ArrayList<Predicate>();
    	ArrayList<Predicate> predicates2 = new ArrayList<Predicate>();
    	boolean foundUneq = false;
    	for (Predicate pred : this.predicates) {
    		if (!foundUneq && pred.operator.equals("<>")) {
    			predicates1.add(new Predicate(pred.column1, pred.column2, ">"));
    			predicates2.add(new Predicate(pred.column1, pred.column2, "<"));
    			foundUneq = true;
    		} else if (pred.operator.equals("==") && !pred.column1.equals(pred.column2)){
    			Predicate predGeq = new Predicate(pred.column1, pred.column2, ">=");
    			Predicate predLeq = new Predicate(pred.column1, pred.column2, "<=");
    			predicates1.add(predGeq);
    			predicates1.add(predLeq);
    			predicates2.add(predGeq);
    			predicates2.add(predLeq);
    		} else {
    			predicates1.add(pred);
    			predicates2.add(pred);
    		}
    	}
    	if (foundUneq && !this.isSymmetric) {
    		Constraint DC1 = new Constraint(predicates1);
    		Constraint DC2 = new Constraint(predicates2);
    		DCs.addAll(DC1.decompose());
    		DCs.addAll(DC2.decompose());
    	} else if (foundUneq) {
    		Constraint DC1 = new Constraint(predicates1);
    		DCs.addAll(DC1.decompose());
    	}
    	else {
    		DCs.add(new Constraint(predicates1));
    	}
    	return DCs;
    }

	@Override
	public String toString() {
		ArrayList<String> predStrings = new ArrayList<>();
		for (Predicate pred : this.predicates) {
			predStrings.add(pred.toString());
		}
		return "NOT (" + String.join(" AND ", predStrings) + ")";
	}
}
