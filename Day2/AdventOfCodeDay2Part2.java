import java.io.*;

public class AdventOfCodeDay2Part2 {
    public static void main(String[] args) throws IOException {
        /*
        * The task is to find out how many safe reports are in a given file.
        * Each line in a file is one report - levels, seperate by a space.
        * A level is just a number
        * A report is safe if these 3 requirements are met:
        * - for every 2 adjecent numbers in the report, they differ by at most 3 and atleast by 1
        * - Reading the levels from one direction of the line to the other, they must either all be increasing or all be decreasing
        * - For this part 2 of the problem, a report is also safe if removing atmost 1 of the levels results in a safe report
        * */
        System.out.println(howManySafeLines(new File(args[0])));
    }

    public static int howManySafeLines(File file) throws IOException {
        try(BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            int totalSafeReports = 0;

            String line = bReader.readLine();
            while (line != null) {
                if (testLineSafety(line)) // Test safety for every line, count up the total safe lines
                    totalSafeReports++;

                line = bReader.readLine();
            }
            return totalSafeReports;
        }
    }
    private static boolean testLineSafety(String line) {
        String[] numbersAsStrings = line.split(" ");
        if (numbersAsStrings.length == 0) return false;
        if (numbersAsStrings.length == 1 || numbersAsStrings.length == 2) return true;

        // Test for safety when removing each of the elements
        // Note - since there are atleast 3 elements, if the whole line is safe, removing any of the elements also results in a safe line
        for (int i = 0; i < numbersAsStrings.length; i++) {
            if (testLineSafetyWithoutElementAtIndex(numbersAsStrings, i))
                return true; // Only one safe line is needed
        }
        return false;
    }
    private static boolean testLineSafetyWithoutElementAtIndex(String[] numbersAsStrings, int indexToBeIgnored) {
        int indexOfFirstNumber = 0;
        if (indexToBeIgnored == 0)
            indexOfFirstNumber++;
        int previousNumber = Integer.parseInt(numbersAsStrings[indexOfFirstNumber]);
        int secondNumber = indexToBeIgnored == 1 ? Integer.parseInt(numbersAsStrings[2+indexOfFirstNumber]) : Integer.parseInt(numbersAsStrings[1+indexOfFirstNumber]);
        boolean isIncreasing = previousNumber < secondNumber;

        for (int i = indexOfFirstNumber+1; i < numbersAsStrings.length; i++) {
            if (i == indexToBeIgnored)
                continue;
            int currentNumber = Integer.parseInt(numbersAsStrings[i]);
            if (!testNumberSafety(previousNumber, currentNumber, isIncreasing))
                return false;
            previousNumber = currentNumber;
        }
        return true;
    }
    private static boolean testNumberSafety(int numberInLine, int nextNumberInLine, boolean isIncreasing) {
        // Check that the difference is between 1 and 3
        int difference = numberInLine - nextNumberInLine;
        if (Math.abs(difference) > 3 || Math.abs(difference) < 1) {
            return false;
        }
        return (!isIncreasing || difference <= 0) && (isIncreasing || difference >= 0);
    }
}
