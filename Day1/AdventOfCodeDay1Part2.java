import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdventOfCodeDay1Part2 {
    public static void main(String[] args) throws IOException {
        System.out.println(simpleApproach(new File(args[0])));
    }
    public static long simpleApproach(File file) throws IOException {
        Set<Integer> integersInFirstColumn = new HashSet<>();
        Map<Integer, Integer> numberOfOccurencesForSecondColumn = new HashMap<>();
        try(BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            // Get all the numbers and save how many times they were in the file to corresponding hashmaps
            String line = bReader.readLine();
            while (line != null) {
                String[] twoNumbersAsStrings = line.split("   "); // When copying the input, the seperator wasn't tab or one space, but appeared to be 3 spaces.. Very much hard coded.

                int firstNumber = Integer.parseInt(twoNumbersAsStrings[0]);
                int secondNumber = Integer.parseInt(twoNumbersAsStrings[1]);

                // After getting the number, save the occurence
                integersInFirstColumn.add(firstNumber);
                numberOfOccurencesForSecondColumn.put(secondNumber, numberOfOccurencesForSecondColumn.getOrDefault(secondNumber, 0)+1);

                // Move to next line
                line = bReader.readLine();
            }
        }

        int total = 0;
        for (Integer integerInFirstColumn : integersInFirstColumn) { // We want to check all numbers in first column, so we can multiply by the occurences in the second row.
            int similarityScore = integerInFirstColumn *
                    numberOfOccurencesForSecondColumn.getOrDefault(integerInFirstColumn, 0);
            total += similarityScore;
        }
        return total;
    }
}
