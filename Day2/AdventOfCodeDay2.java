import java.io.*;

public class AdventOfCodeDay2 {
    public static void main(String[] args) throws IOException {

        /*
         * The task is to find out how many safe reports are in a given file.
         * Each line in a file is one report - levels, seperate by a space.
         * A level is just a number
         * A report is safe if these 2 requirements are met:
         * - for every 2 adjecent numbers in the report, they differ by at most 3 and atleast by 1
         * - Reading the levels from one direction of the line to the other, they must either all be increasing or all be decreasing
         * */

        System.out.println(howManySafeReports(new File(args[0])));
    }

    public static int howManySafeReports(File file) throws IOException {
        try(BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            int totalSafeReports = 0;

            String line = bReader.readLine();
            while (line != null) {
                if (checkLineSafety(line))
                    totalSafeReports++;
                line = bReader.readLine();
            }
            return totalSafeReports;
        }
    }
    private static boolean checkLineSafety(String line) {
        // Check that all numbers are always increasing or always decreasing
        // and the difference for 2 adjecent numbers is between 1 and 3

        // For that I split the line to numbers by spaces, create 3 helper variables - 2 adjecent numbers
        // and a boolean for knowing if it's increasing or decreasing.


        String[] numbersAsStrings = line.split(" ");
        if (numbersAsStrings.length == 1) { // For only one number, it is safe by rules, don't do any further checking
            return true;
        }
        if (numbersAsStrings.length == 0) { // For an empty line, skip it
            return false;
        }

        boolean isIncreasing;
        int numberInLine, nextNumberInLine;

        // Initialise the variables, then go to loop for automating the rest
        numberInLine = Integer.parseInt(numbersAsStrings[0]);
        nextNumberInLine = Integer.parseInt(numbersAsStrings[1]);
        if (numberInLine == nextNumberInLine) { // For equal numbers, the report can't be safe
            return false;
        }
        if (Math.abs(numberInLine - nextNumberInLine) > 3) { // If the difference is too big, the report is also not safe!
            return false;
        }
        isIncreasing = numberInLine < nextNumberInLine;

        for (int i = 2; i < numbersAsStrings.length; i++) {
            // Move over both numbers, then check if report is still safe
            numberInLine = nextNumberInLine;
            nextNumberInLine = Integer.parseInt(numbersAsStrings[i]);

            // Check that the difference is between 1 and 3
            int difference = numberInLine - nextNumberInLine;
            if (Math.abs(difference) > 3 || Math.abs(difference) < 1) {
                return false;
            }
            if (isIncreasing && difference > 0 || !isIncreasing && difference < 0) {
                return false;
            }
        }
        // Everything is checked, line is safe
        return true;
    }
}
