import java.io.*;

public class AdventOfCodeDay7 {
    public static void main(String[] args) throws IOException {
        /*
        * For input rows of numbers and a wanted total are given in the form
        * <total>: <number> <number> ...
        * The task is to use either addition or multiplication such that the value of the expression is the total at the start.
        * Expressions are evaluated left to right, not according to precedence rules.
        * If it's not possible to get the watned total with the given numbers, ignore it.
        * For all the possible totals, sum them together and that is the result.
        * */
        System.out.println(getSumOfPossibleTotals(new File(args[0])));
    }

    private static long getSumOfPossibleTotals(File file) throws IOException {
        long totalSum = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                totalSum += canTotalBeAchieved(line);
                line = bufferedReader.readLine();
            }
        }

        return totalSum;
    }

    private static long canTotalBeAchieved(String line) {
        /*
        * Return 0 if it's not possible, return the int value of total if it can somehow be reached
        * */
        String[] totalAndNumbers = line.split(": ");
        long total = Long.parseLong(totalAndNumbers[0]);
        String[] numbersAsStrings = totalAndNumbers[1].split(" ");
        long[] numbersAsInts = new long[numbersAsStrings.length];
        for (int i = 0; i < numbersAsInts.length; i++) {
            numbersAsInts[i] = Long.parseLong(numbersAsStrings[i]);
        }

        return getCanTotalBeAchieved(total, numbersAsInts) ? total : 0;
    }

    private static boolean getCanTotalBeAchieved(long total, long[] numbersAsInts) {
        return getCanTotalBeAchievedRec(numbersAsInts, total, numbersAsInts[0], 1);
    }
    private static boolean getCanTotalBeAchievedRec(long[] numbers, long wantedTotal, long currentTotal, int index) {
        if (index >= numbers.length) return wantedTotal == currentTotal;

        return getCanTotalBeAchievedRec(numbers, wantedTotal, currentTotal + numbers[index], index+1)
                || getCanTotalBeAchievedRec(numbers, wantedTotal, currentTotal * numbers[index], index+1);
    }
}
