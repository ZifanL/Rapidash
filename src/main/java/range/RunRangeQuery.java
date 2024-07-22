package range;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RunRangeQuery {

	public static void main(String[] args) {
        String csvFile = "C:\\Users\\shaleendeep\\Documents\\sigmodrevision\\src\\main\\resources\\randomdouble.csv";

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
        
        double[][] numbers = null;
        //read csv file
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//
//            // read all lines from CSV file into ArrayList
//            while ((line = br.readLine()) != null) {
//                lines.add(line);
//            }
//
//            // create 2D int array based on number of lines in CSV file
//            int numRows = lines.size() - 1; // exclude header line (if present)
//            int numCols = lines.get(0).split(cvsSplitBy).length;
//            numbers = new double[numRows][numCols];
//
//            // check if first line contains headers
//            boolean hasHeaders = Character.isLetter(lines.get(0).charAt(0));
//
//            // iterate over remaining lines and split into integer values
//            for (int i = hasHeaders ? 1 : 0; i < lines.size(); i++) {
//                String[] values = lines.get(i).split(cvsSplitBy);
//                for (int j = 0; j < values.length; j++) {
//                    numbers[i - (hasHeaders ? 1 : 0)][j] = Double.parseDouble(values[j]);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        numbers = new double[100000][3];

        for (int  i = 0; i < numbers.length; ++i) {
            for (int j = 0; j < numbers[0].length; ++j) {
                numbers[i][j] = Math.random() * (100 - 0) + 0;
            }
        }
        
        long start = System.nanoTime();

        System.out.println("starting");
        
        RangeTree rangeT = new RangeTree(numbers);

        System.out.println("tree done");
        double[] smallest = new double[numbers[0].length];
        for (int i = 0; i < smallest.length; i++) {
        	smallest[i] = Integer.MIN_VALUE;
        }
        
        long numViolations = 0;
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(i);
        	numViolations += rangeT.queryCount(smallest, numbers[i]);  
        }
        
        long end = System.nanoTime();
        
        System.out.println("number of violations: " + numViolations);
        System.out.println("Time: " + (end - start) / 1000000 + " ms");
	}

}
