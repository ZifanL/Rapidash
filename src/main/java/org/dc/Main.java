package org.dc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kdrangeDouble.KeyDuplicateException;
import kdrangeDouble.KeySizeException;

public class Main {
    public static void main(String[] args) throws IOException, KeyDuplicateException, KeySizeException, kdrange.KeySizeException, kdrange.KeyDuplicateException {
    	Map<String, String> namedArgs = argsParse(args);
    	if (namedArgs.get("experiment") != null) {
    		if (!isValidExperiment(namedArgs.get("experiment"))) {
    			System.out.println("experiment Should be one of the following: tax, tpch, ncvoter");
    			System.exit(1);
    		}
    		System.out.println("Run the experiments in the paper on " + namedArgs.get("experiment"));
        	Experiment exp = new Experiment(namedArgs.get("experiment"));
        	exp.execute();
    	} else if (namedArgs.get("dataset") != null){
        	if (namedArgs.get("constraint") == null) {
                System.out.println("Please provide the path to the contraint file using --constraint [path]");
                System.exit(1);
        	}
        	boolean earlystop = true;
           	if (namedArgs.get("earlystop") != null && namedArgs.get("earlystop").toLowerCase().equals("false")) {
           		earlystop = false;
           	}
           	String treeType = "range-tree";
           	if (namedArgs.get("treetype") != null && namedArgs.get("treetype").toLowerCase().startsWith("kd")) {
           		treeType = "kd-tree";
           	}
           	
    		String dataset = namedArgs.get("dataset");
           	InputTable input = new InputTable(dataset);
        	Constraint constraint = new Constraint(namedArgs.get("constraint"), input.nameLoc);
        	
        	System.out.println("Using customized dataset: " + dataset);
        	System.out.println("Constraint: " + constraint);
        	System.out.println("");
        	System.out.println("Start violation detection");
        	
        	long startTime = System.nanoTime();
        	DCVerifier dcVerifier = new DCVerifier(constraint, input);
        	long numberOfViolations = dcVerifier.detectViolation(earlystop, treeType);
        	long endTime = System.nanoTime();
            long duration = (endTime - startTime);  // in nanoseconds
            double durationInMillis = duration / 1_000_000.0;  // convert to milliseconds
        	
        	System.out.println("======================================");
        	if (earlystop) {
        		if (numberOfViolations > 0) {
        			System.out.println("[Result] A violation is found!");
        		} else {
        			System.out.println("[Result] No violations.");
        		}
        	} else {
        		System.out.println("[Result] Number of violations: " + numberOfViolations);
        	}
        	System.out.println("[Execution time] " + durationInMillis + " ms");
        	
    	}
    }
    
    private static boolean isValidExperiment(String dataset) {
        return dataset.equals("tax") || dataset.equals("tpch") || dataset.equals("ncvoter");
    }
    
    private static Map<String, String> argsParse(String[] args){
	    // Create a map to store named arguments and their values
	    Map<String, String> namedArgs = new HashMap<>();
	
	    // Parse the command-line arguments
	    for (int i = 0; i < args.length; i++) {
	        // Check if the argument is a named argument (starts with --)
	        if (args[i].startsWith("--")) {
	            // Get the name of the argument (strip the -- prefix)
	            String name = args[i].substring(2);
	
	            // Check if the next element is a value and not another named argument
	            if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
	                String value = args[i + 1];
	                // Store the named argument and its value in the map
	                namedArgs.put(name, value);
	                i++;  // Skip the value since it's already processed
	            } else {
	                // Store the named argument with an empty value
	                namedArgs.put(name, null);
	            }
	        }
	    }
	    System.out.println("Parsed named arguments:");
	    for (Map.Entry<String, String> entry : namedArgs.entrySet()) {
	        System.out.println("- " + entry.getKey() + ": " + entry.getValue());
	    }
	    System.out.println("");
	    return namedArgs;
    }
}
