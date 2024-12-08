import java.io.*;

public class AdventOfCodeDay6 {
    public static void main(String[] args) throws IOException {
        /*
        * The task is the following:
        * Given a map with . as places that can be visited, # as obstacles and arrow (starting as ^) as guard,
        * find out how many tiles the guard will visit before leaving the mapped area.
        * The guard goes straight when there is no obstacle directly in front and turns right 90 degrees otherwise.
        * */
        System.out.println(countGuardPathTiles(new File(args[0])));
    }
    public static int countGuardPathTiles(File file) throws IOException {
        byte[] tiles;

        try(InputStream inputStream = new FileInputStream(file)) {
            tiles = inputStream.readAllBytes();
        }

        return countGuardPathTiles(tiles);
    }
    public static int countGuardPathTiles(byte[] tiles) {
        int lineLength = 0;
        byte endLineAsByte = (byte)'\n';

        for (byte tile : tiles) {
            lineLength++;
            if (tile == endLineAsByte)
                break;
        }

        byte guardStartingPositionAsByte = (byte)'^';
        int guardPosition = 0;

        for (byte tile : tiles) {
            if (tile == guardStartingPositionAsByte)
                break;
            guardPosition++;
        }

        if (lineLength == 0 || guardPosition == 0)
            return 0;

        // Play next move
        // Check if is outside the area
        // Either loop back or count up the 'X' tiles

        do {
            guardPosition = moveGuard(tiles, lineLength, guardPosition);
        } while (guardPosition != -1 && guardIndexWithinArrayBounds(tiles, guardPosition));

        printMap(tiles);
        return howManyCapitalXLettersInMap(tiles);
    }
    public static void printMap(byte[] tiles) {
        for (byte tile : tiles) {
            System.out.print((char)tile);
        }
    }
    public static int moveGuard(byte[] tiles, int lineLength, int guardPosition) {
        int guardTile = tiles[guardPosition];

        // Different logic depending on the direction the guard is facing

        if (guardTile == (byte)'^')
            return moveGuardUpDown(tiles, guardPosition, lineLength, -1);
        if (guardTile == (byte)'v')
            return moveGuardUpDown(tiles, guardPosition, lineLength, 1);
        // For left-right, an extra check is needed since the guard can "change rows" - that is the newline character column
        // If guard has entered it, it means out of the map
        if (guardTile == (byte)'>')
            return moveGuardLeftRight(tiles, guardPosition, lineLength, 1);
        if (guardTile == (byte)'<')
            return moveGuardLeftRight(tiles, guardPosition, lineLength, -1);
        return -1; // No guard found
    }
    public static boolean guardIndexWithinArrayBounds(byte[] tiles, int guardPosition) {
        return guardPosition >= 0 && guardPosition < tiles.length;
    }
    public static int moveGuardUpDown(byte[] tiles, int guardPosition, int lineLength, int moveDirection) {
        int newPosition = guardPosition+(lineLength*moveDirection);
        if (guardIndexWithinArrayBounds(tiles, newPosition)) {
            // If there tile guard wants to move to is blocked, turn 90 degrees
            if (tiles[newPosition] == (byte)'#') {
                tiles[guardPosition] = moveDirection == -1 ? (byte)'>':(byte)'<';
                return guardPosition;
            }
            // Otherwise move to that tile
            tiles[guardPosition] = (byte)'X';
            tiles[newPosition] = moveDirection == -1 ? (byte)'^':(byte)'v';
        }
        // Guard is outside the map, set the position to X and return that index
        tiles[guardPosition] = (byte)'X';
        return newPosition;
    }
    public static int moveGuardLeftRight(byte[] tiles, int guardPosition, int lineLength, int moveDirection) {
        int newPosition = guardPosition+moveDirection;
        if (guardIndexWithinArrayBounds(tiles, newPosition)) {
            // Check if the tile is blocked
            if (tiles[newPosition] == (byte)'#') {
                tiles[guardPosition] = moveDirection == 1 ? (byte)'v':(byte)'^';
                return guardPosition;
            }
            // Otherwise move the guard
            tiles[guardPosition] = (byte)'X';
            if (newPosition % lineLength == 0)
                return 0; // Guard changed "rows" or has moved out of the map - newline isn't part of the map
            tiles[newPosition] = moveDirection == 1 ? (byte)'>':(byte)'<';
        }
        // Guard is outside the map, set the position to X and return that index
        tiles[guardPosition] = (byte)'X';
        return newPosition;
    }
    public static int howManyCapitalXLettersInMap(byte[] tiles) {
        int counter = 0;
        byte capitalXAsByte = (byte)'X';
        for (byte tile : tiles) {
            if (tile == capitalXAsByte)
                counter++;
        }
        return counter;
    }
}
