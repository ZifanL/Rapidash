package org.dc;
import java.io.*;
import java.util.*;

public class InputTable {
	public int[][] data;
	public Map<String, Integer> nameLoc = new HashMap<>();
	
	public InputTable(String csvFile) {
		this.convertCSVtoIntArray(csvFile, ",");
	}
	
    private void convertCSVtoIntArray(String csvFile, String delimiter) {
        List<List<String>> csvData = new ArrayList<>(); // To store the CSV data in a 2D structure
        Map<String, Integer> stringToIntMap = new LinkedHashMap<>(); // To store unique values and their mapping
        Map<Double, Integer> doubleToIntMap = new LinkedHashMap<>(); // To store unique values and their mapping
        List<String> allValues = new ArrayList<>(); // Store all values to create the mapping later

        boolean isHeader = true; // Flag to check if we're processing the header

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split each line by the delimiter and store values
                String[] row = line.split(delimiter);
                if (isHeader) {
                    // Map header column names to their column index
                    for (int i = 0; i < row.length; i++) {
                    	nameLoc.put(row[i].trim(), i); // Map each header to its column index
                    }
                    isHeader = false; // Next line will be data
                } else {
                    // For data rows
                    List<String> rowData = new ArrayList<>();
                    for (String value : row) {
                        String trimmedValue = value.trim();
                        rowData.add(trimmedValue); // Add value to the row
                        allValues.add(trimmedValue); // Collect all values for mapping later
                    }
                    csvData.add(rowData); // Add the row to the 2D structure
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get unique values and sort them to preserve their order
        Set<String> uniqueValuesString = new TreeSet<>(); // TreeSet automatically sorts the values
        Set<Double> uniqueValuesDouble = new TreeSet<>();
        
        // The order of String columns are not preserved if there are values that look like a number
        for (String value : allValues) {
        	if (isNumeric(value)) {
        		uniqueValuesDouble.add(Double.parseDouble(value));
        	} else {
        		uniqueValuesString.add(value);
        	}
        }
        
        // Assign unique integers to each unique value
        int counter = 0;
        for (double value : uniqueValuesDouble) {
        	doubleToIntMap.put(value, counter++);
        }
        for (String value : uniqueValuesString) {
            stringToIntMap.put(value, counter++);
        }

        // Convert CSV data to a 2D integer array
        data = new int[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            data[i] = new int[row.size()];
            for (int j = 0; j < row.size(); j++) {
            	String value = row.get(j);
            	if (isNumeric(value)) {
            		data[i][j] = doubleToIntMap.get(Double.parseDouble(value));
            	} else {
            		data[i][j] = stringToIntMap.get(row.get(j)); // Map each value to its corresponding integer
            	}
            }
        }
    }
    
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true; // It's a valid number
        } catch (NumberFormatException e) {
            return false; // It's not a valid number
        }
    }

}
