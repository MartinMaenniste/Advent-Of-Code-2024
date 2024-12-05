import java.io.*;

public class AdventOfCodeDay4Part2 {
    public static void main(String[] args) throws IOException {
        /*
         * The task is to find 2 M A S sequences that make up an x in a field of letters.
         * The sequence M A S can be in either of the 4 directions - both diagonals, both ways
         */
        System.out.println(howManyX_MAS(new File(args[0])));
    }

    private static int howManyX_MAS(File file) throws IOException {
        // Find all the letters 'A' and use a helper method to see if an X-MAS is made from it.

        int totalX_MASOccurences = 0;
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

            byte capitalACharacterAsByte = (byte)'A';

            for (int i = 0; i < bytesArray.length; i++) {
                if (bytesArray[i] == capitalACharacterAsByte
                        && x_MASStartsFromHere(bytesArray, i, lineLength)) {
                    totalX_MASOccurences++;
                }
            }
        }
        return totalX_MASOccurences;
    }

    private static boolean x_MASStartsFromHere(byte[] bytesArray, int i, int lineLength) {
        // Both diagonals need to make up x-mas in some way.
        // That means that for the 4 corners around index i, there have to be 2 sets of m and 2 sets of s.
        // The only way these characters wouldn't make an x-mas is if "M A M" and "S A S" are on diagonals.

        // If in either of the directions, there doesn't exist a diagonal without going out of bounds, an x-mas can't start from here
        if (i - 1 - lineLength < 0
                || i + 1 + lineLength >= bytesArray.length)
            return false;

        int howManyMCharacters = 0, howManySCharacters = 0;
        byte upperCaseMAsByte = (byte)'M';
        byte upperCaseSAsByte = (byte)'S';

        // Check the 4 corners and count up M's and S's

        i = i - 1 - lineLength; // Upper left corner
        if (bytesArray[i] == upperCaseMAsByte)
            howManyMCharacters++;
        if (bytesArray[i] == upperCaseSAsByte)
            howManySCharacters++;

        i += 2; // Upper right corner
        if (bytesArray[i] == upperCaseMAsByte)
            howManyMCharacters++;
        if (bytesArray[i] == upperCaseSAsByte)
            howManySCharacters++;

        i += 2*lineLength; // Down right corner
        if (bytesArray[i] == upperCaseMAsByte)
            howManyMCharacters++;
        if (bytesArray[i] == upperCaseSAsByte)
            howManySCharacters++;

        i -= 2; // Down left corner
        if (bytesArray[i] == upperCaseMAsByte)
            howManyMCharacters++;
        if (bytesArray[i] == upperCaseSAsByte)
            howManySCharacters++;

        // Now the 2 checks can be made:
        // - Are there 2 sets of M's and 2 sets of S's
        // - Do the diagonals have different characters (meaning one has to be M and other S since there are only 4 corners)
        return howManyMCharacters == 2
                && howManySCharacters == 2
                && bytesArray[i] != bytesArray[i+2*(1-lineLength)];
    }
}
