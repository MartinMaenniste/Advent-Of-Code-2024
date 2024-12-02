import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdventOfCodeDay1 {
    public static void main(String[] args) throws IOException {
        System.out.println(simpleApproach(new File(args[0]))); // Pass in the filename as command line argument!!
    }

    /**
     * This method takes in the filename, does no error checks and just tries to parse ints from lines where 2 numbers are seperated by 3 spaces.
     * Method reads the numbers with BufferedReader, sorts the arrays, using builtin java methods, then adds up the differences and returns it
     * @param file - File type object of the input file, where numbers will be read from
     * @return Method returns the total difference of the n-th smallest numbers of the two columns of the input file text where n is from 1 to the total amount of lines.
     * @throws IOException Since the method does no error checking whether a file exists and if it tries to parse an int from a valid string, up to IOException might be thrown when making mistakes with the method call etc.
     */
    public static long simpleApproach(File file) throws IOException {
        // The 2 arrays that will store all of the numbers from first and second column respectively of the input file text.
        List<Integer> firstColumnNumbers = new ArrayList<>();
        List<Integer> secondColumnNumbers = new ArrayList<>();

        try(BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            // Get all the numbers and put them to corresponding Lists
            String line = bReader.readLine();
            while (line != null) {
                String[] twoNumbersAsStrings = line.split("   "); // When copying the input, the seperator wasn't tab or one space, but appeared to be 3 spaces.. Very much hard coded.

                int firstNumber = Integer.parseInt(twoNumbersAsStrings[0]);
                int secondNumber = Integer.parseInt(twoNumbersAsStrings[1]);

                firstColumnNumbers.add(firstNumber);
                secondColumnNumbers.add(secondNumber);

                // Move to next line
                line = bReader.readLine();
            }
        }
        // Sort to get the pairings starting from the smallest
        firstColumnNumbers.sort(Integer::compareTo);
        secondColumnNumbers.sort(Integer::compareTo);

        // Add up the differences
        long total = 0;
        int totalElements = firstColumnNumbers.size(); // Assume they're both the same size
        for (int i = 0; i < totalElements; i++) {
            int firstNumber = firstColumnNumbers.get(i);
            int secondNumber = secondColumnNumbers.get(i);

            int difference = firstNumber - secondNumber;
            if (difference < 0) difference *= -1; // We want the difference - not which is bigger, but the absolute number

            total += difference;
        }

        return total;
    }
}
