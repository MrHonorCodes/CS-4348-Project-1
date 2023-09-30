import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

    private static final int MEMORY_SIZE = 2000;
    private static int[] memory;

    public static void main(String[] args) {
        validateInputArguments(args);
        String inputFilePath = args[0];

        try {
            initializeMemory(inputFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFilePath);
            System.exit(0);
        }

        handleMemoryOperations();
    }

    private static void validateInputArguments(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Memory <input-file-path>");
            System.exit(0);
        }
    }

    private static void initializeMemory(String inputFilePath) throws FileNotFoundException {
        memory = new int[MEMORY_SIZE];
        try (Scanner scanner = new Scanner(new File(inputFilePath))) {
            int address = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.startsWith(".")) {
                    address = Integer.parseInt(line.substring(1).split("\\s+")[0]);
                    continue;
                }

                if (Character.isDigit(line.charAt(0))) {
                    memory[address++] = Integer.parseInt(line.split("\\s+")[0]);
                }
            }
        }
    }

    private static void handleMemoryOperations() {
        try (Scanner inputScanner = new Scanner(System.in)) {
            while (inputScanner.hasNextLine()) {
                String line = inputScanner.nextLine();
                String command = line.substring(0, 1);
                int address, data;

                switch (command) {
                    case "R": // read
                        address = Integer.parseInt(line.substring(1));
                        if (isValidAddress(address)) {
                            System.out.println(readMemory(address));
                        }
                        break;

                    case "W": // write
                        String[] params = line.substring(1).split(",");
                        address = Integer.parseInt(params[0]);
                        data = Integer.parseInt(params[1]);
                        if (isValidAddress(address)) {
                            writeMemory(address, data);
                        }
                        break;

                    case "E": // exit
                        System.exit(0);
                }
            }
        }
    }
    private static boolean isValidAddress(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            System.err.println("Invalid memory address.");
            return false;
        }
        return true;
    }
    private static int readMemory(int address) {
        return memory[address];
    }

    private static void writeMemory(int address, int data) {
        memory[address] = data;
    }
}
