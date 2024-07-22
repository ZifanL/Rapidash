package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.example.Main.encode;

public class PerturbTables {
    //static String datasetPath = "C:\\Users\\shaleendeep\\OneDrive - Microsoft\\Documents\\sigmodrevision\\src\\main\\resources\\";
    static String datasetPath = "C:\\Users\\shaleendeep\\OneDrive - Microsoft\\Documents\\sigmodrevision\\src\\main\\resources\\";
    static String filename = "TaxEncoded.csv";
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(datasetPath+filename);
        File file = new File(path.toString());
        long lineCount;
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            lineCount = stream.count();
        }
        int numberOfLines = (int) (lineCount - 1);
        System.out.print(numberOfLines);
        Map<String, Double> stringencode = new HashMap<>();
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        String header = sc.nextLine();
        String[] parts = header.split(",");
        Map<String, Integer> nameLoc = new HashMap<>();
        Map<String, List<Integer>> nameDomain = new HashMap<>();
        Map<Integer, String> locName = new HashMap<>();
        Map<String, String> nameType = new HashMap<>();
        int counter = 0;
        for (String part: parts) {
            String type = part.substring(part.indexOf("(") + 1, part.lastIndexOf(")"));
            String name = part.substring(0, part.indexOf("("));
            System.out.println(type + " " + name);
            nameLoc.put(name, counter);
            nameDomain.put(name, new ArrayList<Integer>());
            locName.put(counter, name);
            nameType.put(name,  type);
            counter = counter + 1;
        }
        int numColumns = parts.length;
        Random rand = new Random();
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
                    //case "String":
                    default:
                        data[row][i] = encode(stringencode, split[i]).intValue();
                }
                nameDomain.get(locName.get(i)).add(data[row][i]);
            }
            row = row+1;
        }
        System.out.println("rows processed: " + row);
        float probability = 0.5f;
        int[][] dataperturbed = new int[numberOfLines][numColumns];
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[0].length; ++j) {
                float f = rand.nextFloat();
                if (f < probability) {
                    dataperturbed[i][j] = nameDomain.get(locName.get(j)).get(new Random().nextInt(nameDomain.get(locName.get(j)).size()));
                } else {
                    dataperturbed[i][j] = data[i][j];
                }
            }
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(datasetPath + "TaxEncoded50" + ".csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            writer.write(header + "\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(dataperturbed.length);
        int c = 0;
        for (int[] rowdata : dataperturbed) {
            try {
                writer.write(IntStream.of(rowdata)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining(",")) + "\n");
                c = c + 1;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("error while writing");
                e.printStackTrace();
            }
        }
        writer.close();
    }
}
