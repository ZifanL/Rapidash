package org.dc;

import kdrange.KDTreeHelper;
import kdrange.KeyDuplicateException;
import kdrange.KeySizeException;
import org.apache.commons.lang3.tuple.Pair;
import rangetreeboolean.PointNew;
import rangetreeboolean.RangeTreeBoolean;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    static double counter = 0;

    public static void print(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static Double encode(Map<String, Double> map, String str) {
        if (map.containsKey(str)) {
            return map.get(str);
        }
        if (str.equalsIgnoreCase("NaN")) {
            map.put(str, Double.MAX_VALUE);
            return map.get(str);
        }
        map.put(str, counter);
        counter += 1;
        return counter - 1;
    }
    
    private static boolean isValidDataset(String dataset) {
        return dataset.equals("tax") || dataset.equals("tpch") || dataset.equals("ncvoter");
    }


    public static void main(String[] args) throws IOException, KeyDuplicateException, KeySizeException {
        if (args.length < 1) {
            System.out.println("Please specify the dataset. Choose from: tax, tpch, ncvoter");
            System.exit(1);
        }
        String dataset = args[0];
        if (!isValidDataset(dataset)) {
            System.out.println("Invalid dataset: " + dataset);
            System.out.println("Choose from: tax, tpch, ncvoter");
            System.exit(1);
        }
        System.out.println("Using dataset: " + dataset);
        Path path;
        switch (dataset) {
	        case "tax":
	        	path = Paths.get("data/TaxEncoded.csv");
	            break;
	        case "tpch":
	        	path = Paths.get("data/tpch1Mencoded.csv");
	            break;
	        case "ncvoter":
	        	path = Paths.get("data/ncvoter_Statewide_parsedencoded.txt");
	        	break;
	        default:
	        	path = Paths.get("");
        }
        Map<String, Double> stringencode = new HashMap<>();
        File file = new File(path.toString());
        long lineCount;
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            lineCount = stream.count();
        }
        int numberOfLines = (int) (lineCount - 1);
        System.out.println("Number of Tuples: " + numberOfLines);
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        String header = sc.nextLine();
        String[] parts = header.split(",");
        Map<String, Integer> nameLoc = new HashMap<>();
        Map<Integer, String> locName = new HashMap<>();
        Map<String, String> nameType = new HashMap<>();
        int counter = 0;
        for (String part: parts) {
            String type = part.substring(part.indexOf("(") + 1, part.lastIndexOf(")"));
            String name = part.substring(0, part.indexOf("("));
            nameLoc.put(name, counter);
            locName.put(counter, name);
            nameType.put(name,  type);
            counter = counter + 1;
        }
        int numColumns = parts.length;

        int[][] data = new int[numberOfLines][numColumns];
        int row=0;
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] split = line.split(",");
            for (int i = 0; i < split.length; ++i) {
                switch (nameType.get(locName.get(i))) {
                    case "Integer", "Double", "String":
                        data[row][i] = Integer.parseInt(split[i].trim());
                        break;
                    default:
                        data[row][i] = encode(stringencode, split[i].trim()).intValue();
                }
            }
            row = row+1;
        }
        System.out.println("Data loaded.");
        
        switch (dataset) {
	        case "tax":
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.AreaCode = s.AreaCode AND s.Phone = t.Phone)");
	            Tax.taxq4(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.ZipCode = t.ZipCode AND s.City != t.City)");
	            Tax.taxq5(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.State = t.State AND s.HasChild = t.HasChild AND s.ChildExemp != t.ChildExemp)");
	            Tax.taxq6(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.State = t.State AND s.Salary > t.Salary AND s.Rate < t.Rate)");
	            System.out.print("Use range trees -> ");
	            Tax.taxq7rangetreebool(data, nameLoc);
	            System.out.print("Use kd-trees -> ");
	            Tax.taxq7kdtreebool(data, nameLoc);
	            // Apply the optimization in Section 4.7 for enumeration
	            Tax.taxq7enumerate(data, nameLoc);
	            break;
	        case "tpch":
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.Customer = t.Supplier AND s.Supplier = t.Customer)");
	            TPCH.tpchq10(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.Receiptdate >= t.Shipdate AND s.Shipdate <= t.Receiptdate)");
	            System.out.print("Use range trees -> ");
	            TPCH.tpchq11rangetreebool(data, nameLoc);
	            System.out.print("Use kd-trees -> ");
	            TPCH.tpchq11kdtreebool(data, nameLoc);
	            // Apply the optimization in Section 4.7 for enumeration
	            TPCH.tpchq11enumerate(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.ExtPrice > t.ExtPrice AND s.Discount < t.Discount)");
	            System.out.print("Use range trees -> ");
	            TPCH.tpchq12rangetreebool(data, nameLoc);
	            System.out.print("Use kd-trees -> ");
	            TPCH.tpchq12kdtreebool(data, nameLoc);
	            // Apply the optimization in Section 4.7 for enumeration
	            TPCH.tpchq12enumerate(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.Qty = t.Qty AND s.Tax = t.Tax AND s.ExtPrice > t.ExtPrice AND s.Discount < t.Discount)");
	            System.out.print("Use range trees -> ");
	            TPCH.tpchq13rangetreebool(data, nameLoc);
	            System.out.print("Use kd-trees -> ");
	            TPCH.tpchq13kdtreebool(data, nameLoc);
	            // Apply the optimization in Section 4.7 for enumeration
	            TPCH.tpchq13enumerate(data, nameLoc);
	            break;
	        case "ncvoter":
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.countyid = t.countyid AND s.countydesc != t.countydesc)");
	            NCVoter.c1(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.ageatyearend < t.birthyear)");
	            // Apply the optimization in Section 4.6 for boolean decision
	            NCVoter.c2(data, nameLoc);
	            NCVoter.c2tree(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.statuscd = t.statuscd AND s.voterdesc = t.voterdesc AND s.reasoncd != t.reasoncd)");
	            NCVoter.c3(data, nameLoc);
	            System.out.println("==================================================");
	            System.out.println("Constraint: NOT (s.mailzipcode = t.zipcode AND s.statecd != t.mailstate)");
	            NCVoter.c4(data, nameLoc);
	            break;
	        default:
	        	System.out.println("Do nothing.");
        }
    }
}
