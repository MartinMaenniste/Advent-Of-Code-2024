import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdventOfCodeDay9Part2 {
    public static void main(String[] args) throws IOException {
        /*
        * We have a text file that represents a disk. It is a long one line of numbers where the numbers represent the quantity of file or empty bits.
        * The numbers alternate between representing file size and empty space size, starting with file size.
        * For example 39582 means there are 3 file bits, then 9 empty bits, then 5 file bits, 8 empty bits and 2 file bits.
        * Since the files are all different, they all have their own index, starting from 0. If . represents empty space, the example above could be represented as:
        * 000.........11111........22
        *
        * The task is to try to compact the disk, by moving whole files into left most empty spaces, going from biggest to smallest. In the example above, the two bits of
        * file with index 2 would be moved right after the first file (index 0) and then the five bits of file with index 1 will be moved:
        * 000.........11111........22 -> 00022.......11111........ -> 0002211111..........
        * If there isn't enough empty space for a file, it will be ignored.
        *
        * Finally, the answer is got by adding up all the numbers multiplied by their indexes.
        * in the answer of the example above would go as follows:
        * 0*0 + 1*0 + 2*0 + 3*2 + 4*2 + 5*1 + 6*1 + 7*1 + 8*1 + 9*1
        * = 6 + 8 + 5 + 6 + 7 + 8 + 9 = 49
        * */

        System.out.println(getSumOfFinalDisk(new File(args[0])));
    }

    private static long getSumOfFinalDisk(File file) throws IOException {

        // Start by getting all the numbers from the file
        List<Integer> numbers = getNumbersFromFile(file);

        // Now we have the original file represantation, make it into actual file so we can keep indexes and compact it.
        // Represent the disk as array of numbers, where the number is the index of the file and -1 is empty space
        // For example [1,3,2,4,6] -> [0, -1, -1, -1, 1, 1, -1, -1, -1, -1, 2, 2, 2, 2, 2, 2]
        List<Integer> disk = getDiskFromNumbers(numbers);

        // Try to compact the disk
        compactDisk(disk);

        /*List<Integer> testNumbers = List.of(4,8,5,7,2,4,5,2,8,1,3,5,1,4,9,3,7,5,2,1,6);
        List<Integer> testDisk = getDiskFromNumbers(testNumbers);
        compactDisk(testDisk);
        System.out.println(getSumOfDisk(testDisk));*/

        // Get the sum of compacted disk and return it
        return getSumOfDisk(disk);
    }


    private static List<Integer> getNumbersFromFile(File file) throws IOException {
        // We know the file is one line and all numbers, we can parse numbers from byte array and add them to returnable list

        // Make the list we will add to and open file:
        List<Integer> numbers;
        try (InputStream inputStream = new FileInputStream(file)){
            byte[] allBytes = inputStream.readAllBytes();
            numbers = new ArrayList<>(allBytes.length);

            for (int i = 0; i < allBytes.length; i++) {
                int number = (allBytes[i] - '0'); // Parse the number - byte values for numbers 0...9 are all "next to eachother"
                numbers.add(number);
            }
        }

        return numbers;
    }
    private static List<Integer> getDiskFromNumbers(List<Integer> numbers) {
        List<Integer> disk = new ArrayList<>();

        int fileIndexNumber = 0;
        for (int i = 0; i < numbers.size(); i++) {
            if (i % 2 == 0) {
                addToArray(disk, fileIndexNumber, numbers.get(i));
                fileIndexNumber++;
            }
            else {
                addToArray(disk, -1, numbers.get(i));
            }
        }

        return disk;
    }

    private static void addToArray(List<Integer> addHere, int numToAdd, int howManyToAdd) {
        for (int i = 0; i < howManyToAdd; i++) {
            addHere.add(numToAdd);
        }
    }

    private static void compactDisk(List<Integer> disk) {
        // We can start looking at largest numbers by looping through the list starting from last index. Try to put it in empty spaces starting from left, but
        // make sure it doesn't get put later on into the list. Do that until there are no more files to reposition or the first free space index is larger than
        // the index of largest number we haven't replaced yet

        int lastIndexOfLargestUnlookedFile = getIndexOfLastFileBitFromDisk(disk);
        int firstIndexOfFirstEmptySpace = getIndexOfFirstEmptySpaceFromDisk(disk);

        if (lastIndexOfLargestUnlookedFile == -1 || firstIndexOfFirstEmptySpace == -1)
            throw new RuntimeException("Incorrect disk!");

        int currentFileNumber = disk.get(lastIndexOfLargestUnlookedFile);

        while(lastIndexOfLargestUnlookedFile > firstIndexOfFirstEmptySpace && currentFileNumber > 0)
        {
            // Try to move file, get new indexes until all files are looked through or there is no more empty space.

            // If the file did move, the function will return new first available space index.
            firstIndexOfFirstEmptySpace = tryToMoveFile(disk, lastIndexOfLargestUnlookedFile, firstIndexOfFirstEmptySpace);

            currentFileNumber--;

            lastIndexOfLargestUnlookedFile = getNewIndexForFile(disk, lastIndexOfLargestUnlookedFile, currentFileNumber);
        }
    }
    private static int getIndexOfLastFileBitFromDisk(List<Integer> disk) {
        for (int i = disk.size()-1; i >= 0; i--) {
            if (disk.get(i) != -1)
                return i;
        }
        return -1;
    }
    private static int getIndexOfFirstEmptySpaceFromDisk(List<Integer> disk) {
        return findFirstEmptySpace(disk, 0);
    }
    private static int tryToMoveFile(List<Integer> disk, int lastIndexOfLargestUnlookedFile, int firstIndexOfFirstEmptySpace) {
        int startIndexOfCurrentEmptySpace = firstIndexOfFirstEmptySpace;
        int sizeOfCurrentEmptySpace = getSizeOfEmptySpaceAtIndex(disk, startIndexOfCurrentEmptySpace);
        int sizeOfFile = getSizeOfFile(disk, lastIndexOfLargestUnlookedFile);

        while (startIndexOfCurrentEmptySpace < lastIndexOfLargestUnlookedFile) {
            if (sizeOfFile <= sizeOfCurrentEmptySpace) {
                moveFile(disk, lastIndexOfLargestUnlookedFile, startIndexOfCurrentEmptySpace, sizeOfFile);
                break;
            }
            startIndexOfCurrentEmptySpace = findNextEmptySpace(disk, startIndexOfCurrentEmptySpace);
            sizeOfCurrentEmptySpace = getSizeOfEmptySpaceAtIndex(disk, startIndexOfCurrentEmptySpace);
        }

        return findFirstEmptySpace(disk, firstIndexOfFirstEmptySpace);
    }

    private static int getSizeOfEmptySpaceAtIndex(List<Integer> disk, int startIndexOfCurrentEmptySpace) {
        int size = 0;
        for (int i = startIndexOfCurrentEmptySpace; i < disk.size(); i++) {
            if (disk.get(i) != -1)
                break;

            size++;
        }
        return size;
    }
    private static int getSizeOfFile(List<Integer> disk, int lastIndexOfLargestUnlookedFile) {
        int size = 0;
        int fileNumber = disk.get(lastIndexOfLargestUnlookedFile);
        for (int i = lastIndexOfLargestUnlookedFile; i >= 0; i--) {
            if (disk.get(i) != fileNumber)
                break;

            size++;
        }
        return size;
    }
    private static void moveFile(List<Integer> disk, int lastIndexOfLargestUnlookedFile, int startIndexOfCurrentEmptySpace, int fileSize) {
        // Move the file
        for (int iteration = 0; iteration < fileSize; iteration++) {
            disk.set(startIndexOfCurrentEmptySpace+iteration, disk.get(lastIndexOfLargestUnlookedFile));
        }

        // Mark where file was as empty space
        for (int iteration = 0; iteration < fileSize; iteration++) {
            disk.set(lastIndexOfLargestUnlookedFile-iteration, -1);
        }
    }

    private static int findFirstEmptySpace(List<Integer> disk, int startIndex) {
        for (int i = startIndex; i < disk.size(); i++) {
            if (disk.get(i) == -1)
                return i;
        }
        return -1;
    }
    private static int findNextEmptySpace(List<Integer> disk, int startIndex) {
        if (disk.get(startIndex) == -1) { // Skip over current empty space
            for (; startIndex < disk.size(); startIndex++) {
                if (disk.get(startIndex) != -1)
                    break;
            }
        }
        for (; startIndex < disk.size(); startIndex++) {
            if (disk.get(startIndex) == -1)
                break;
        }
        return startIndex;
    }
    private static int getNewIndexForFile(List<Integer> disk, int lastIndexOfLargestUnlookedFile, int newFileNumber) {
        if (newFileNumber < 0)
            return -1;

        for (int i = lastIndexOfLargestUnlookedFile; i >= 0; i--) {
            if (disk.get(i) == newFileNumber)
                return i;
        }

        throw new RuntimeException("Unknown error occured trying to move index of last file!");
    }
    private static long getSumOfDisk(List<Integer> disk) {
        long sum = 0;
        for (int i = 0; i < disk.size(); i++) {
            if (disk.get(i) == -1)
                continue;
            sum += (long) i * disk.get(i);
        }
        return sum;
    }
}
