package rangetreeboolean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RunRangeQueryNew {

	public static void main(String[] args) {
        String csvFile = "C:\\Users\\shaleendeep\\OneDrive - Microsoft\\Documents\\sigmodrevision2\\src\\main\\resources\\randomint.txt";

//        for (int i = 0; i < args.length; i++) {
//          if (args[i].equals("--file")) {
//            // if we find the --file option, the next argument should be the filename
//            csvFile = args[i+1];
//            break;
//          }
//        }


        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> lines = new ArrayList<>();

        int[][] numbers = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            // read all lines from CSV file into ArrayList
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            // create 2D int array based on number of lines in CSV file
            int numRows = lines.size() - 1; // exclude header line (if present)
            int numCols = lines.get(0).split(cvsSplitBy).length;
            numbers = new int[numRows][numCols];

            // check if first line contains headers
            boolean hasHeaders = Character.isLetter(lines.get(0).charAt(0));

            // iterate over remaining lines and split into integer values
            for (int i = hasHeaders ? 1 : 0; i < lines.size(); i++) {
                String[] values = lines.get(i).split(cvsSplitBy);
                for (int j = 0; j < values.length; j++) {
                    numbers[i - (hasHeaders ? 1 : 0)][j] = Integer.parseInt(values[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int[] smallest = new int[numbers[0].length];
        for (int i = 0; i < smallest.length; i++) {
        	smallest[i] = Integer.MIN_VALUE;
        }
        PointNew minusInf = new PointNew(smallest);
        
        int[] largest = new int[numbers[0].length];
        for (int i = 0; i < largest.length; i++) {
        	largest[i] = Integer.MAX_VALUE;
        }
        PointNew Inf = new PointNew(largest);
        
        // Specify whether the bound is inclusive or not
        boolean[] inclusive = new boolean[numbers[0].length];
        Arrays.fill(inclusive, false);

        long start = System.nanoTime();
        boolean haveViolation = false;
        RangeTreeBoolean rangeTB = new RangeTreeBoolean();
        for (int i = 0; i < numbers.length; i++) {
        	PointNew curr = new PointNew(numbers[i], inclusive);
        	if (rangeTB.query(minusInf, curr) || rangeTB.query(curr, Inf)) {
        		haveViolation = true;
                System.out.println(curr);
        		//break;
        	}
        	rangeTB.insert(curr);
        }
        long end = System.nanoTime();
        System.out.println("have violation: " + haveViolation);
        System.out.println("Time: " + (end - start) / 1000000 + " ms");
	}

}
