
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Memory {

    private static final int[] dataBank = new int[2000];

    public static void main(String[] cmdArgs) {
        Scanner inputScanner = new Scanner(System.in);
        File dataFile = null;

        if (inputScanner.hasNextLine()) {
            dataFile = new File(inputScanner.nextLine());
            if (!dataFile.exists()) {
                System.out.println("No such file.");
                System.exit(0);
            }
        }

        populateDataBank(dataFile);

        while (inputScanner.hasNext()) {
            String instruction = inputScanner.nextLine();
            if (!instruction.isEmpty()) {
                executeInstruction(instruction);
            }
        }
    }

    private static void populateDataBank(File dataFile) {
        try (Scanner fileScanner = new Scanner(dataFile)) {
            int index = 0;
            while (fileScanner.hasNext()) {
                if (fileScanner.hasNextInt()) {
                    dataBank[index++] = fileScanner.nextInt();
                } else {
                    String token = fileScanner.next();
                    if (token.startsWith(".")) {
                        index = Integer.parseInt(token.substring(1));
                    } else if (token.equals("//")) {
                        fileScanner.nextLine();
                    } else {
                        fileScanner.nextLine();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void executeInstruction(String instruction) {
        String[] parts = instruction.split(",");
        String action = parts[0];
        int address, value;

        if ("1".equals(action)) {
            address = Integer.parseInt(parts[1]);
            System.out.println(retrieveData(address));
        } else {
            address = Integer.parseInt(parts[1]);
            value = Integer.parseInt(parts[2]);
            storeData(address, value);
        }
    }

    private static int retrieveData(int address) {
        return dataBank[address];
    }

    private static void storeData(int address, int value) {
        dataBank[address] = value;
    }
}
