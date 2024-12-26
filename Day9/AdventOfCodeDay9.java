import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdventOfCodeDay9 {
    public static void main(String[] args) throws IOException {
        /*
        * You are given an array of numbers. They alternate between file size and empty space size, starting with a file.
        * - 123 means there is a file with size 1, then 2 empty spots and then a file with size 3
        * Every file has an id. first file is id 0, second with id 1 etc.
        * 123 could be represented by 0..111 where . is the empty space
        * Task is to start moving the files from furthest back to first empty spaces:
        * 0..111
        * 01.11.
        * 0111..
        * After that add together all the digits multiplied by their index.
        * 0*0 + 1*1 + 2*1 + 3*1 = ...
        * */
        System.out.println(findSumOfNumbers(new File(args[0])));
    }

    private static long findSumOfNumbers(File file) throws IOException {
        // Start by getting all the numbers
        List<Integer> numbers;

        // Since the input is one big line and everything is a number, parsing manually from byte array works
        try (InputStream inputStream = new FileInputStream(file)){
            byte[] allBytes = inputStream.readAllBytes();
            numbers = new ArrayList<>(allBytes.length);

            for (int i = 0; i < allBytes.length; i++) {
                int number = (allBytes[i] - '0');
                numbers.add(number);
            }
        }
        // First make the disk layout - -1 is empty space
        List<Integer> disk = new ArrayList<>(numbers.size());
        int fileIndex = 0;
        for (int i = 0; i < numbers.size(); i++) {
            int amount = numbers.get(i);
            if (i % 2 == 0) {
                // File
                for (int counter = 0; counter < amount; counter++) {
                    disk.add(fileIndex);
                }
                fileIndex++;
                continue;
            }
            // Empty space
            for (int counter = 0; counter < amount; counter++) {
                disk.add(-1);
            }
        }
        // Fill the empty spots of the disk
        int indexOfLastFile = disk.size()-1;
        if (disk.get(indexOfLastFile) == -1)
            throw new RuntimeException("In the representation of the disk, there is empty space at the end!");
        for (int i = 0; i < disk.size(); i++) {
            if (indexOfLastFile <= i)
                break;
            if (disk.get(i) == -1) {
                // Swap with last file
                int temp = disk.get(i);
                disk.set(i, disk.get(indexOfLastFile));
                disk.set(indexOfLastFile, temp);
                do {
                    indexOfLastFile--;
                }while(disk.get(indexOfLastFile) == -1);
            }
        }
        // Calculate the total sum
        long total = 0;
        for (int i = 0; i < disk.size(); i++) {
            if (disk.get(i) == -1)
                break;
            total += (long)i*disk.get(i);
        }
        return total;
    }
}
