import java.io.*;

public class AdventOfCodeDay4 {
    public static void main(String[] args) throws IOException {
        /*
        * The task is to find the amount of occurences of the word "xmas"
        * in a field of letters. The occurence starts from x and can move
        * outwards in any of the 8 directions.
        * */
        System.out.println(findXMASOccurencesInLetterField(new File(args[0])));
    }

    private static int findXMASOccurencesInLetterField(File file) throws IOException {
        /*
        * Get all the bytes
        * every time an 'X' is found, call out a helper method that checks all 8 directions and returns how many
        * XMAS start from it
        * */
        int totalXMASOccurences = 0;
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytesArray = inputStream.readAllBytes();

            // Find out the length of one line once and use it everywhere
            int lineLength = -1;
            byte newLineCharacterAsByte = (byte)'\n';
            for (int i = 0; i < bytesArray.length; i++) {
                if (bytesArray[i] == newLineCharacterAsByte) {
                    lineLength = i+1;
                    break;
                }
            }
            // If everything is on one line, it will count right, right and up, right and down as the same thing - 3 times. Same for left.
            // So the answer will be wrong for a file with just the one line!
            if (lineLength == -1) {
                throw new RuntimeException("The method doesn't display a correct answer for a file with just one line.");
            }

            byte capitalXCharacterAsByte = (byte)'X';

            for (int i = 0; i < bytesArray.length; i++) {
                if (bytesArray[i] == capitalXCharacterAsByte) {
                    totalXMASOccurences += howManyXMASStartFromHere(bytesArray, i, lineLength);
                }
            }
        }
        return totalXMASOccurences;
    }
    private static int howManyXMASStartFromHere(byte[] bytesArray, int i, int lineLength) {
        // We know that bytesArray[i] is the letter 'X'
        // check for the next characters in all 8 directions

        // Check from current index to the right first and then go as a spiral clockwise for other directions
        int totalXMASesFromHere = 0;

        // First direction - right
        int incrementForNextLetter = 1;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Second direction - down right
        incrementForNextLetter = 1 + lineLength;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Third direction - down
        incrementForNextLetter--;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Fourth direction - down left
        incrementForNextLetter--;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Fifth direction - left
        incrementForNextLetter -= lineLength;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Sixth direction - up left
        incrementForNextLetter -= lineLength;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Seventh direction - up
        incrementForNextLetter++;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;
        // Eighth direction - up right
        incrementForNextLetter++;
        if (checkXMASFromIndexWithIncrement(bytesArray, i, incrementForNextLetter))
            totalXMASesFromHere++;

        return totalXMASesFromHere;
    }
    private static boolean checkXMASFromIndexWithIncrement(byte[] bytesArray, int startIndex, int incrementForNextLetter) {
        byte uppercaseMCharacterAsByte = (byte)'M';
        byte uppercaseACharacterAsByte = (byte)'A';
        byte uppercaseSCharacterAsByte = (byte)'S';

        if (startIndex + 3*incrementForNextLetter < bytesArray.length
                && startIndex + 3*incrementForNextLetter > 0) {
            return bytesArray[startIndex+incrementForNextLetter] == uppercaseMCharacterAsByte
                    && bytesArray[startIndex+2*incrementForNextLetter] == uppercaseACharacterAsByte
                    && bytesArray[startIndex+3*incrementForNextLetter] == uppercaseSCharacterAsByte;
        }
        return false; // False if it would go out of bounds
    }
}
