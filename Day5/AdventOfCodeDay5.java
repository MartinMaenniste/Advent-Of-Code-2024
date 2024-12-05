import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventOfCodeDay5 {
    public static void main(String[] args) throws IOException {
        /*
        * The task is the following:
        * You are given a file in which there are 2 parts. First part up to an empty line are number ordering rules.
        * These are in the form of <number>|<number>
        * The number ordering rules mean that the first number must appear before the second in the lists to come.
        * For example 65|17 means 65 must be before 17, if these numbers exist for it to be valid ordering.
        * The second part are lists of numbers, seperated by a comma.
        *
        * The task is to find which number orderings have the correct order according to the rules defined at the start of the file.
        * From these correct orderings, find the middle value (by index, not value) and add them all up.
        * The resulting sum is the wanted number.
        * */

        System.out.println(totalSumOfMiddleNumberOfCorrectLists(new File(args[0])));
    }

    private static int totalSumOfMiddleNumberOfCorrectLists(File file) throws IOException {
        /*
        * Read file by line.
        * Until an empty line, store the rules.
        * Afterwards come the lists.
        * Let a helper method process the lists and return either 0 for an incorrect list or the value of its middle element for a list with the correct order.
        * Sum the return values into one variable that is returned at the end of the method.
        * */

        // Store rules in hashmap for quick lookup - first number is the number that comes before, the second is what comes after
        Map<Integer, List<Integer>> ruleSet = new HashMap<>();
        int totalSumOfCorrectListMiddleElements = 0;

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = bufferedReader.readLine();

            // BufferedReader removes the newline character so new line is just a blank string
            while(!line.equals("")) { // After newline come the lists, different logic for handling those lines
                int indexOfSeperator = line.indexOf('|');
                int firstNumber = getIntFromStringByIndex(line, 0, indexOfSeperator);
                int secondNumber = getIntFromStringByIndex(line, indexOfSeperator+1);

                // Add the numbers - either append or put the new elements to the list
                if (ruleSet.containsKey(firstNumber))
                    ruleSet.get(firstNumber).add(secondNumber);
                else {
                    List<Integer> valueArray = new ArrayList<>();
                    valueArray.add(secondNumber);
                    ruleSet.put(firstNumber, valueArray);
                }

                line = bufferedReader.readLine();
            }

            // Now look at the sequences of numbers, add the middle number of every correct sequence
            line = bufferedReader.readLine();
            while (line != null) {
                totalSumOfCorrectListMiddleElements +=
                        checkAndGetMiddleElementFromSequence(line, ruleSet);
                line = bufferedReader.readLine();
            }
        }

        return totalSumOfCorrectListMiddleElements;
    }
    private static int checkAndGetMiddleElementFromSequence(String sequenceAsString, Map<Integer, List<Integer>> ruleSet) {
        /*
        * Simple, naive solution:
        * Assume that contradicting rules are possible. For example:
        * 44|16
        * 16|83
        * 83|44
        * This means that if 44, 16 and 83 are all in a sequence, then it is automatically incorrect.
        *
        * Check every number against those following it and make sure it's according to the rule set.
        * */

        String[] numbersAsStrings = sequenceAsString.split(",");
        // Convert the nubmers to integers
        int[] numbers = new int[numbersAsStrings.length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(numbersAsStrings[i]);
        }

        // Check the ruleset - for every number, if it's in the rule set, all the numbers following must be after it in the rules or not in the rules for this number.
        for (int firstNumberIndex = 0; firstNumberIndex < numbers.length; firstNumberIndex++) {
            for (int secondNumberIndex = firstNumberIndex+1; secondNumberIndex < numbers.length; secondNumberIndex++) {
                int firstNumber = numbers[firstNumberIndex];
                int secondNumber = numbers[secondNumberIndex];

                // For every pair of numbers, I want to check if they are in the incorrect order.
                if (ruleSet.containsKey(secondNumber)) {
                    // Check for first number in the value section - if it's there, the numbers should be the other way around!
                    List<Integer> values = ruleSet.get(secondNumber);
                    for (Integer value : values) {
                        if (value == firstNumber)
                            return 0; // Incorrect ordering
                    }
                }
            }
        }
        return numbers[numbers.length/2];
    }
    /**
     * Method tries to make an integer from the elements between start and end index (end index is not included!!) and returns the constructed int
     * If an integer can't be constructed, an exception will be thrown
     * @param string - The string from which an int will be constructed
     * @param startIndex - First index of the number in the string
     * @param endIndex - The first index after the last number in the string.
     * @return Method returns the constructed number or throws an exception if some of the characters aren't digits
     */
    private static int getIntFromStringByIndex(String string, int startIndex, int endIndex) {
        int constructedNumber = 0;

        // The characters are stored by some logic. In the characters, somewhere comes a point:
        // ..... 0 1 2 3 4 ... so the numbers are right next to eachother, '0' marks the start of that sequence
        // If that value is subtracted from a digit, we get the value (offset).
        char firstNumberChar = '0';

        int currentPowerOfTenValue = 1;
        int lengthOfNumber = endIndex-startIndex;

        for (int i = 0; i < lengthOfNumber; i++) {
            char currentCharacter = string.charAt(endIndex-1-i); // Start from the back - where the smallest powers of 10 are.
            if (!Character.isDigit(currentCharacter))
                throw new NumberFormatException("Can't make a number out of the digit " + currentCharacter);
            constructedNumber += (currentCharacter-firstNumberChar) * currentPowerOfTenValue;
            currentPowerOfTenValue *= 10;
        }

        return constructedNumber;
    }

    /**
     * Method tries to make an integer out of the digits between the start index that is given as a parameter and the end of the string.
     * @param string - String from which a number is parsed from
     * @param startIndex - Index of the number's first digit
     * @return Method returns the integer value of the number in the string. When such a number can't be made, an exception will be thrown
     */
    private static int getIntFromStringByIndex(String string, int startIndex) {
        return getIntFromStringByIndex(string, startIndex, string.length());
    }
}
