import java.io.*;

public class AdventOfCodeDay2 {
    public static void main(String[] args) throws IOException {
        System.out.println(simpleSolution(new File(args[0])));
    }

    public static int simpleSolution(File file) throws IOException {
        try(BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            int totalSafeReports = 0;
            boolean currentReportIsSafe;

            String line = bReader.readLine();
            while (line != null) {
                // Check that all numbers are always increasing or always decreasing
                // and the difference for 2 adjecent numbers is between 1 and 3 (included)

                // For that I split the line to numbers by spaces, create 3 helper variables - 2 adjecent numbers
                // and a boolean for knowing if it's increasing or decreasing.

                currentReportIsSafe = true; // Reset the helper variable every iteration

                String[] numbersAsStrings = line.split(" ");
                if (numbersAsStrings.length == 1) { // For only one number, it is stable by rules, don't do any further checking
                    totalSafeReports++;
                    // Move to next line
                    line = bReader.readLine();
                    continue;
                }
                if (numbersAsStrings.length == 0) { // For an empty line, skip it
                    // Move to next line
                    line = bReader.readLine();
                    continue;
                }

                boolean isIncreasing;
                int numberInLine, nextNumberInLine;

                // Initialise the variables, then go to loop for autmating the rest
                numberInLine = Integer.parseInt(numbersAsStrings[0]);
                nextNumberInLine = Integer.parseInt(numbersAsStrings[1]);
                if (numberInLine == nextNumberInLine) { // For equal numbers, the report can't be stable
                    // Move to next line
                    line = bReader.readLine();
                    continue;
                }
                if (Math.abs(numberInLine - nextNumberInLine) > 3) { // If the difference is too big, the report is also not stable!
                    // Move to next line
                    line = bReader.readLine();
                    continue;
                }
                isIncreasing = numberInLine < nextNumberInLine;

                for (int i = 2; i < numbersAsStrings.length; i++) {
                    // Move over both numbers, then check if report is still stable
                    numberInLine = nextNumberInLine;
                    nextNumberInLine = Integer.parseInt(numbersAsStrings[i]);

                    // Check that the difference is between 1 and 3
                    int difference = numberInLine - nextNumberInLine;
                    if (Math.abs(difference) > 3 || Math.abs(difference) < 1) {
                        currentReportIsSafe = false;
                        break;
                    }
                    if (isIncreasing && difference > 0 || !isIncreasing && difference < 0) {
                        currentReportIsSafe = false;
                        break;
                    }
                }

                if (currentReportIsSafe)
                    totalSafeReports++;
                // Move to next line
                line = bReader.readLine();
            }
            return totalSafeReports;
        }
    }
}
