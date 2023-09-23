import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MemUnit {
    private static final int[] storage = new int[2000];

    public static void main(String[] args) {
        Scanner cpuInput = new Scanner(System.in);
        File instructionFile = null;

        if (cpuInput.hasNextLine()) {
            instructionFile = new File(cpuInput.nextLine());
            if (!instructionFile.exists()) {
                System.out.println("Oops, file's gone missing!");
                System.exit(0);
            }
        }

        initMemory(instructionFile);

        while (true) {
            if (cpuInput.hasNext()) {
                String commandLine = cpuInput.nextLine();
                if (!commandLine.trim().isEmpty()) {
                    String[] tokens = commandLine.split(",");
                    if ("1".equals(tokens[0])) {
                        int addr = Integer.parseInt(tokens[1]);
                        System.out.println(fetch(addr));
                    } else {
                        int addr = Integer.parseInt(tokens[1]);
                        int data = Integer.parseInt(tokens[2]);
                        store(addr, data);
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private static int fetch(int address) {
        return storage[address];
    }

    private static void store(int address, int value) {
        storage[address] = value;
    }

    private static void initMemory(File file) {
        try {
            Scanner fileScanner = new Scanner(file);
            int index = 0;
            while (fileScanner.hasNext()) {
                if (fileScanner.hasNextInt()) {
                    storage[index++] = fileScanner.nextInt();
                } else {
                    String token = fileScanner.next();
                    if (token.startsWith(".")) {
                        index = Integer.parseInt(token.substring(1));
                    } else if ("//".equals(token)) {
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
}
