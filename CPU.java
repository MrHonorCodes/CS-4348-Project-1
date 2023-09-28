import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CPU {

    private static int accumulator = 0;
    private static int xRegister = 0;
    private static int yRegister = 0;
    private static int stackPointer = 1000;
    private static Scanner memScanner = new Scanner(System.in);

    public static void main(String[] args) {
        PrintWriter memWriter = new PrintWriter(System.out, true);

        while (true) {
            String instruction = memScanner.nextLine();
            if (instruction.isEmpty()) {
                break;
            }
            executeCommand(instruction, memWriter);
        }
    }
    private static void executeCommand(String instruction, PrintWriter memWriter) {
        String[] tokens = instruction.split(",");
        int command = Integer.parseInt(tokens[0]);
        int operand = tokens.length > 1 ? Integer.parseInt(tokens[1]) : 0;

        switch (command) {
            case 1:
                accumulator = operand;
                break;
            case 2:
                memWriter.println("1," + operand);
                accumulator = Integer.parseInt(memScanner.nextLine());
                break;
            case 3:
                memWriter.println("1," + operand);
                int indirectAddr = Integer.parseInt(memScanner.nextLine());
                memWriter.println("1," + indirectAddr);
                accumulator = Integer.parseInt(memScanner.nextLine());
                break;
            case 4:
                memWriter.println("1," + (operand + xRegister));
                accumulator = Integer.parseInt(memScanner.nextLine());
                break;
            case 5:
                memWriter.println("1," + (operand + yRegister));
                accumulator = Integer.parseInt(memScanner.nextLine());
                break;
            case 6:
                accumulator = stackPointer + xRegister;
                break;
            case 7:
                memWriter.println("2," + operand + "," + accumulator);
                break;
            case 8:
                accumulator = (int) (Math.random() * 100) + 1;
                break;
            case 9:
                if (operand == 1) {
                    System.out.print(accumulator);
                } else {
                    System.out.print((char) accumulator);
                }
                break;
            case 10:
                accumulator += xRegister;
                break;
            case 11:
                accumulator += yRegister;
                break;
            case 14:
                xRegister = accumulator;
                break;
            case 16:
                yRegister = accumulator;
                break;
            case 20:
                memWriter.println("3," + operand);
                break;
            case 21:
                if (accumulator == 0) {
                    memWriter.println("3," + operand);
                }
                break;
            case 50:
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command");
        }
    }
}
