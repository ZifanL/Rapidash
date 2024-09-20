package org.dc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Constraint {
	ArrayList<SingleConstraint> singleConstraints = new ArrayList<>();
	ArrayList<Predicate> predicates = new ArrayList<>();
	
	public Constraint(String filePath, Map<String, Integer> nameLoc) {
		this.loadConstraint(filePath, nameLoc);
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
        
        ArrayList<Predicate> homoEqPreds = new ArrayList<>();
        ArrayList<Predicate> uneqPreds = new ArrayList<>();
        ArrayList<Predicate> ineqPreds = new ArrayList<>();
        
	    for (int i = 0; i < columns1.size(); i++) {
	        String colName1 = columns1.get(i);
	        String colName2 = columns2.get(i);
	        String opName = operators.get(i);
	        if (nameLoc.get(colName1) == null) throw new IllegalArgumentException("Column " + colName1 + " does not exist! Column list: " + nameLoc.keySet());
	        if (nameLoc.get(colName2) == null) throw new IllegalArgumentException("Column " + colName2 + " does not exist! Column list: " + nameLoc.keySet());
	        
	        if (opName.equals("==") || opName.equals("=")) {
	        	if (colName1.equals(colName2)) {
	        		homoEqPreds.add(new Predicate(colName1, colName2, "=="));
	        	}
	        } else if (opName.equals("<>") || opName.equals("!=")) {
	        	uneqPreds.add(new Predicate(colName1, colName2, "<>"));
	        } else if (opName.equals(">") || opName.equals("<") || opName.equals(">=") || opName.equals("<=")){
	        	ineqPreds.add(new Predicate(colName1, colName2, opName));
	        } else {
	        	throw new IllegalArgumentException("Operator " + opName + " is not valid! OPerator should be in [==, <>, <, >, <=, >=]");
	        }
	    }
	    this.predicates.addAll(homoEqPreds);
	    this.predicates.addAll(ineqPreds);
	    this.predicates.addAll(uneqPreds);
	    
	    for (String binaryString : this.generateBinaryCombinations(uneqPreds.size())) {
	    	ArrayList<Predicate> allPreds = new ArrayList<>();
	    	allPreds.addAll(homoEqPreds);
	    	allPreds.addAll(ineqPreds);
	    	for (int i = 0; i < uneqPreds.size(); i++) {
	    		allPreds.add(new Predicate(uneqPreds.get(i).column1, uneqPreds.get(i).column2, binary2Op(binaryString.charAt(i))));
	    	}
	    	this.singleConstraints.add(new SingleConstraint(allPreds));
	    }
    }
    
    private static String binary2Op(char binary) {
    	if (binary == '0') {
    		return "<";
    	} else {
    		return ">";
    	}
    }
    
    private static ArrayList<String> generateBinaryCombinations(int n) {
        // Total number of combinations is 2^n
        int totalCombinations = (int) Math.pow(2, n);
        ArrayList<String> allCombos = new ArrayList<>();
        // Iterate over each number from 0 to 2^n - 1
        for (int i = 0; i < totalCombinations; i++) {
            // Convert the current number to a binary string
            String binaryString = Integer.toBinaryString(i);
            // Add leading zeros to make sure the binary string has exactly n bits
            while (binaryString.length() < n) {
                binaryString = "0" + binaryString;
            }
            allCombos.add(binaryString);
        }
        return allCombos;
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
