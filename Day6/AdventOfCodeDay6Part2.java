import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventOfCodeDay6Part2 {
    public static void main(String[] args) throws IOException {
        /*
         * Try adding an obstacle to a tile that doesn't have it yet. See if guard gets stuck in a loop.
         * Loop over all the options.
         * When moving, create a hashmap for int-current tile and int[]-moved to.
         * */
        long start = System.currentTimeMillis();
        System.out.println(howManyPossibleLoops(new File(args[0])));
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    private static int howManyPossibleLoops(File file) throws IOException {
        byte[] tiles;
        try (InputStream inputStream = new FileInputStream(file)) {
            tiles = inputStream.readAllBytes();
        }
        int indexOfGuard = -1;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == (byte) '^') {
                indexOfGuard = i;
                break;
            }
        }
        int lineLength = 0;
        for (byte tile : tiles) {
            lineLength++;
            if (tile == (byte) '\n')
                break;
        }

        markTilesWithX(tiles, lineLength, indexOfGuard);

        int totalLoops = 0;
        byte blockedTileCharacterAsByte = (byte)'#';
        byte emptyTileCharacterAsByte = (byte)'.';
        // Try out all the possibilities, add an obstacle, see if that would cause a loop and make it back into a regular tile
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != (byte) 'X')
                continue;

            tiles[i] = blockedTileCharacterAsByte;
            if (doesGuardGetStuckInALoop(tiles, lineLength, indexOfGuard))
                totalLoops++;
            tiles[i] = emptyTileCharacterAsByte;
        }
        return totalLoops;
    }
    private static void markTilesWithX(byte[] tiles, int lineLength, int indexOfGuard) {
        byte characterToMarkWithAsByte = (byte)'X';
        int[] xAndYVelocity = new int[2];
        setVelocitiesFromCharacter((char)tiles[indexOfGuard], xAndYVelocity);

        // Don't touch the first tile.
        indexOfGuard = nextPosition(tiles, lineLength, indexOfGuard, xAndYVelocity);

        while(indexOfGuard != -1) {
            tiles[indexOfGuard] = characterToMarkWithAsByte;
            indexOfGuard = nextPosition(tiles, lineLength, indexOfGuard, xAndYVelocity);
        }
    }
    private static boolean doesGuardGetStuckInALoop(byte[] tiles, int lineLength, int indexOfGuard) {
        int[] xAndYVelocity = new int[2];

        // Initial direction of the guard
        char guardChar = (char) tiles[indexOfGuard];
        setVelocitiesFromCharacter(guardChar, xAndYVelocity);

        // Start moving the guard, save where it's been already
        Map<Integer, List<Integer>> movedFromTo = new HashMap<>();
        int nextPosition = nextPosition(tiles, lineLength, indexOfGuard, xAndYVelocity);
        while (nextPosition != -1) {
            // Has the guard already been here AND moved to the same tile we want to move now? That's a loop!
            if (movedFromTo.containsKey(indexOfGuard)
                    && movedFromTo.get(indexOfGuard).contains(nextPosition))
                return true;
            // Guard is taking a path it hasn't before
            if (movedFromTo.containsKey(indexOfGuard))
                movedFromTo.get(indexOfGuard).add(nextPosition);
            else {
                List<Integer> newArrayForHashMap = new ArrayList<>();
                newArrayForHashMap.add(nextPosition);
                movedFromTo.put(indexOfGuard, newArrayForHashMap);
            }
            indexOfGuard = nextPosition;
            nextPosition = nextPosition(tiles, lineLength, indexOfGuard, xAndYVelocity);
        }
        return false;
    }
    private static void setVelocitiesFromCharacter(char guardCharacter, int[] xAndYVelocity) {
        switch (guardCharacter) {
            case '^':
                xAndYVelocity[0] = 0; xAndYVelocity[1] = -1; break;
            case '>':
                xAndYVelocity[0] = 1; xAndYVelocity[1] = 0; break;
            case 'v':
                xAndYVelocity[0] = 0; xAndYVelocity[1] = 1; break;
            case '<':
                xAndYVelocity[0] = -1; xAndYVelocity[1] = 0; break;
            default:
                throw new RuntimeException("Invalid character for guard!! expected one of the following: ^>v<, but got: " + guardCharacter);
        }
    }
    private static int nextPosition(byte[] tiles, int lineLength, int indexOfGuard, int[] xAndYVelocity) {
        int nextPosition = indexOfGuard + xAndYVelocity[0] + lineLength * xAndYVelocity[1];
        if (nextPosition < 0 || nextPosition >= tiles.length || tiles[nextPosition] == (byte)'\r' || tiles[nextPosition] == (byte)'\n')
            return -1;
        if (tiles[nextPosition] == (byte)'#') {
            makeRightTurn(xAndYVelocity);
            return nextPosition(tiles, lineLength, indexOfGuard, xAndYVelocity);
        }
        return nextPosition;
    }

    private static void makeRightTurn(int[] xAndYVelocity) {
        int oldXVel = xAndYVelocity[0];
        int oldYVel = xAndYVelocity[1];

        int newXVel = oldYVel;
        int newYVel = oldXVel;

        // If the guard was moving up or down, the x value is different from the y value, otherwise it can just be copied
        //       0, -1
        // -1, 0        1, 0
        //       0, 1
        // First is the x velocity sign, second is y velocity sign
        if (oldXVel == 0)
            newXVel *= -1;

        xAndYVelocity[0] = newXVel;
        xAndYVelocity[1] = newYVel;
    }
}
