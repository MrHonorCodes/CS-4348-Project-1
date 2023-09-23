import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MemStorage {
    static int[] dataBank; // Static array for memory

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Insufficient arguments provided.");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            initializeMemory(filePath);
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
            System.exit(1);
        }

        Scanner userInput = new Scanner(System.in);

        while (userInput.hasNextLine()) {
            String inputLine = userInput.nextLine();
            char action = inputLine.charAt(0);
            int location, value;

            switch (action) {
                case 'r':
                    location = Integer.parseInt(inputLine.substring(1));
                    System.out.println(retrieve(location));
                    break;
                case 'w':
                    String[] parameters = inputLine.substring(1).split(",");
                    location = Integer.parseInt(parameters[0]);
                    value = Integer.parseInt(parameters[1]);
                    deposit(location, value);
                    break;
                case 'e':
                    System.exit(0);
                    break;
            }
        }
        userInput.close();
    }

    private static int retrieve(int address) {
        return dataBank[address];
    }

    private static void deposit(int address, int data) {
        dataBank[address] = data;
    }

    private static void initializeMemory(String path) throws FileNotFoundException {
        dataBank = new int[2000];
        Scanner fileScanner = new Scanner(new File(path));
        int index = 0;

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine().trim();
            if (line.isEmpty()) continue;

            if (line.charAt(0) == '.') {
                index = Integer.parseInt(line.substring(1).split("\\s+")[0]);
                continue;
            }

            if (!Character.isDigit(line.charAt(0))) continue;

            String[] parts = line.split("\\s+");
            if (parts.length < 1) continue;

            dataBank[index++] = Integer.parseInt(parts[0]);
        }
        fileScanner.close();
    }
}
