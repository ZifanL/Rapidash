package org.dc;

import kdrange.KeyDuplicateException;
import kdrange.KeySizeException;

import java.io.IOException;

public class Main {
    private static boolean isValidDataset(String dataset) {
        return dataset.equals("tax") || dataset.equals("tpch") || dataset.equals("ncvoter");
    }


    public static void main(String[] args) throws IOException, KeyDuplicateException, KeySizeException {
        if (args.length < 1) {
            System.out.println("Please specify the dataset. Choose from: tax, tpch, ncvoter or provide the path to a customized csv file.");
            System.exit(1);
        }
        String dataset = args[0];
        if (!isValidDataset(dataset)) {
        	if (args.length < 2) {
                System.out.println("Please provide the path to the contraint file.");
                System.exit(1);
        	}
        	InputTable input = new InputTable(dataset);
        	Constraint constraint = new Constraint(args[1], input.nameLoc);
        	System.out.println("Using customized dataset: " + dataset);
        	System.out.println("Constraint: " + constraint);
        	DCVerifier dcVerifier = new DCVerifier(constraint, input);
        } else {
        	System.out.println("Run the experiments in the paper, dataset: " + dataset);
        	Experiment exp = new Experiment(dataset);
        	exp.execute();
        }

    }
}
