import java.io.*;

public class AdventOfCodeDay3 {
    public static void main(String[] args) throws IOException {
        /*
        * The task is the following:
        * You are given a file in which there are multiplication instructions in the form of
        * mul(<number>,<number>)
        * However the file is corrupted so there is a lot of jumble around it
        * Find only the ones that are in this "pure" form, with no extra whitespace or other characters.
        * Do the multiplications and find out their total sum
        * */
        System.out.println(sumOfUncorruptedMultiplications(new File(args[0])));
    }

    private static int sumOfUncorruptedMultiplications(File file) throws IOException {
        /*
        * Start scanning character by character, find the first 'm' and the next ')' that comes along.
        * That forms a window, in which I can check for valid instructions. If a new 'm' is found, move the
        * previous index/pointer along. Since no extra whitespace or any other characters (included nested mul() instructions)
        * are tolerated, the only mul instructions that are allowed would be in this form - I can find an 'm' and ')' and then I can do
        * further analysing to see if it's not corrupted in the middle.
        * */

        // Start looking through the characters
        // If character is 'm', start making a mul command out of it
        // If the character is 'm' again, stop and start collecting again
        // If the character is ')', stop collecting, let it be processed and add to sum - 0 for invalid instruction
        int totalSumOfMultiplications = 0;

        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytesFromFile = inputStream.readAllBytes();

            // Instead of converting all the bytes to char, the other way around also works since only 'm' and ')' are being looked for
            byte lowercaseMCharacterAsByte = (byte)'m';
            byte closeBracketCharacterAsByte = (byte)')';

            int indexOfM = 0;
            boolean firstMFound = false; // If a mul statement is currently in the making - first m found, looking for the first ')'
            for (int i = 0; i < bytesFromFile.length; i++) {
                if (bytesFromFile[i] == lowercaseMCharacterAsByte) { //
                    indexOfM = i;
                    firstMFound = true;
                    continue;
                }
                if (bytesFromFile[i] == closeBracketCharacterAsByte && firstMFound) { // Only if a command is being made, is this a potential candidate
                    totalSumOfMultiplications += processMulInstruction(bytesFromFile, indexOfM, i);
                }
            }
        }

        return totalSumOfMultiplications;
    }

    /**
     * Method checkds if the characters between 2 indexes in a byte array make up a multiplication instruction in the following form:
     * mul(number,number) with no extra whitespace or jumbled characters and the number being an actual number (all characters are digits).
     * @param byteArray - Byte array to check characters from
     * @param startIndexOfMulInstruction - Start index of the multiplication instruction.
     * @param endIndexOfMulInstruction - Last index of the multiplication instruction. NOTE - this index will be checked as well, it's not the index after the instruction
     * @return Method returns the result of multiplication instruction. For invalid instruction, the method returns 0.
     */
    private static int processMulInstruction(byte[] byteArray, int startIndexOfMulInstruction, int endIndexOfMulInstruction) {
        /*
        * Valid instruction must not include any whitespace:
        * - 'm' 'u' 'l' '(' ',' ')' plus the "length" of the 2 numbers must not be longer than the window this method looked through
        * All the non-digit characters have to exist:
        * - The start of the instruction must be "mul("
        * - After '(', there must be ','
        * Both numbers must be valid:
        * - between '(' and ',' must be only digit characters
        * - between ',' and ')' must be only digit characters
        *
        * Note that the final check along with the second ensures the first condition is valid as well.
        * */

        int indexOfStartBracketCharacter = startIndexOfMulInstruction+3;

        if (byteArray[startIndexOfMulInstruction] != (byte)'m' ||
                byteArray[startIndexOfMulInstruction+1] != (byte)'u' ||
                byteArray[startIndexOfMulInstruction+2] != (byte)'l' ||
                byteArray[indexOfStartBracketCharacter] != (byte)'(') // The first 4 characters must be m u l (
            return 0;

        byte commaCharacterAsByte = (byte)',';
        int indexOfCommaCharacter = -1;

        for (int i = startIndexOfMulInstruction+3; i <= endIndexOfMulInstruction; i++) {
            if (byteArray[i] == commaCharacterAsByte) {
                indexOfCommaCharacter = i;
                break;
            }
        }

        if (indexOfCommaCharacter == -1) // Comma not found
            return 0;

        // To avoid throwing exceptions, first check that all the number digits are indeed numbers, then try to parse ints from it
        // Since the bytes need to be converted to characters, save them to array since they might make up a number we need for returning
        // All the indexes are known so a dynamic array is not needed, premake the array to start adding to
        char[] firstNumbersDigits = new char[indexOfCommaCharacter-(indexOfStartBracketCharacter+1)];
        char[] secondNumbersDigits = new char[endIndexOfMulInstruction-(indexOfCommaCharacter+1)];

        for (int currentIndex = indexOfStartBracketCharacter+1; currentIndex < indexOfCommaCharacter; currentIndex++) { // First number
            char currentCharacter = (char)byteArray[currentIndex];
            if (!Character.isDigit(currentCharacter))
                return 0;
        }
        for (int currentIndex = indexOfCommaCharacter+1; currentIndex < endIndexOfMulInstruction; currentIndex++) { // Second number
            char currentCharacter = (char)byteArray[currentIndex];
            if (!Character.isDigit(currentCharacter))
                return 0;
        }

        int firstNumber = getIntegerFromDigitsAsBytes(byteArray, indexOfStartBracketCharacter+1, indexOfCommaCharacter);
        int secondNumber = getIntegerFromDigitsAsBytes(byteArray, indexOfCommaCharacter+1, endIndexOfMulInstruction);

        return firstNumber*secondNumber;
    }

    /**
     * Method takes in a byte array and 2 indexes in between which there is a valid number (all the bytes correspond to digit type characters).
     * The method makes an int type number from these digits and returns the created number
     * @param bytesArray - The array in which the number is
     * @param startIndexOfNumber - First index of the number (its first digit).
     * @param endIndexOfNumber - The end of the number (first index after the last index of the number's digits). This index is not checked, it sets the enpoint and is not itself a digit of the number
     * @return Method returns the number constructed from the digits in the byte array between the specified indexes
     */
    private static int getIntegerFromDigitsAsBytes(byte[] bytesArray, int startIndexOfNumber, int endIndexOfNumber) {
        int constructedNumber = 0;

        /*
        * The number is created by adding the digits to the already constructed number one by one.
        * For that the number has to be multiplied by 10 the right amount of times.
        * The for loop can keep track of the current power of 10.
        * Since it's easier to start from 0 and go to higher powers, the digits are read backwards
        * */

        byte byteValueOfDigit0 = (byte)'0'; // Since the characters should be numbered such that 0 1 2 3 etc. come right after oneanother
                                            // the value can be calculated by subtracting the start index of this digit to byte numbering scheme

        int currentPowerOfTenValue = 1;
        int lengthOfNumber = endIndexOfNumber - startIndexOfNumber;

        for (int i = 0; i < lengthOfNumber; i++) {
            int currentDigit = bytesArray[endIndexOfNumber-1-i] - byteValueOfDigit0;

            constructedNumber += currentDigit*currentPowerOfTenValue;

            currentPowerOfTenValue *= 10;
        }

        return constructedNumber;
    }
}