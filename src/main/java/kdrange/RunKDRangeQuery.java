package kdrange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RunKDRangeQuery {
    public static void main(String [] args) throws KeySizeException, KeyDuplicateException {
        String csvFile = "C:\\Users\\shaleendeep\\OneDrive - Microsoft\\Documents\\sigmodrevision2\\src\\main\\resources\\randomdouble.txt";

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

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            // read all lines from CSV file into ArrayList
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            // create 2D int array based on number of lines in CSV file
            int numRows = lines.size() - 1; // exclude header line (if present)
            int numCols = lines.get(0).split(cvsSplitBy).length;
            numbers = new double[numRows][numCols];

            // check if first line contains headers
            boolean hasHeaders = Character.isLetter(lines.get(0).charAt(0));

            // iterate over remaining lines and split into integer values
            for (int i = hasHeaders ? 1 : 0; i < lines.size(); i++) {
                String[] values = lines.get(i).split(cvsSplitBy);
                for (int j = 0; j < values.length; j++) {
                    numbers[i - (hasHeaders ? 1 : 0)][j] = Double.parseDouble(values[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        numbers = new double[100000][3];
//
//        for (int  i = 0; i < numbers.length; ++i) {
//            for (int j = 0; j < numbers[0].length; ++j) {
//                numbers[i][j] = Math.random() * (100 - 0) + 0;
//            }
//        }

        long start = System.nanoTime();

        System.out.println("starting");
        
        KDTree kdT = new KDTree(numbers);

        System.out.println("Tree done");
        double[] smallest = new double[numbers[0].length];
        for (int i = 0; i < smallest.length; i++) {
        	smallest[i] = Double.MIN_VALUE;
        }
        
        long numViolations = 0;
        for (int i = 0; i < numbers.length; i++) {
            double[] qlower = new double[]{numbers[i][0], numbers[i][1]};
            double[] qupper = new double[]{numbers[i][0]+1, numbers[i][1]+1};
            System.out.println(i + " " + kdT.queryCount(qlower, qupper));
        	numViolations += kdT.queryCount(qlower, qupper);
        }

//        for (int i = 0; i < numbers.length; ++i) {
//            System.out.println(kdT.query(new double[]{numbers[i][0], Double.MIN_VALUE},
//                    new double[]{Double.MAX_VALUE, numbers[i][1]}));
//        }
        
        long end = System.nanoTime();
        
        System.out.println("number of violations: " + numViolations);
        System.out.println("Time: " + (end - start) / 1000000 + " ms");
	}
}
