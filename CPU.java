import java.io.PrintWriter;
import java.util.Scanner;

public class CPU {
    private static int accumulator = 0;
    private static int xRegister = 0;
    private static int yRegister = 0;
    private static int stackPointer = 1000;
    private static int programCounter = 0;
    private static int instructionRegister = 0;
    private static int timer;
    private static boolean kernelMode = false;
    private static Scanner memScanner = new Scanner(System.in);
    private static PrintWriter memWriter = new PrintWriter(System.out, true);

    public static void main(String[] args) {
        timer = Integer.parseInt(args[0]); // Initialize timer from command-line arguments

        while (true) {
            if (timer == 0 && !kernelMode) {
                triggerTimerInterrupt();
            }

            String instruction = memScanner.nextLine();
            if (instruction.isEmpty()) {
                break;
            }
            executeCommand(instruction);

            timer--; // Decrement timer
        }
    }

    private static void executeCommand(String instruction) {
        instructionRegister = Integer.parseInt(instruction.split(",")[0]);
        int operand = instruction.split(",").length > 1 ? Integer.parseInt(instruction.split(",")[1]) : 0;

        switch (instructionRegister) {
            case 1: // Load value
                accumulator = operand;
                programCounter++;
                break;

            case 2:  // Load addr
                memWriter.println("1," + operand);
                accumulator = Integer.parseInt(memScanner.nextLine());
                programCounter++;

                break;

            case 3:  // LoadInd addr
                memWriter.println("1," + operand);
                int indirectAddr = Integer.parseInt(memScanner.nextLine());
                memWriter.println("1," + indirectAddr);
                accumulator = Integer.parseInt(memScanner.nextLine());
                programCounter++;
                break;

            case 4:  // LoadIdxX addr
                memWriter.println("1," + (operand + xRegister));
                accumulator = Integer.parseInt(memScanner.nextLine());
                programCounter++;
                break;

            case 5:  // LoadIdxY addr
                memWriter.println("1," + (operand + yRegister));
                accumulator = Integer.parseInt(memScanner.nextLine());
                programCounter++;
                break;

            case 6:  // LoadSpX
                accumulator = stackPointer + xRegister;
                programCounter++;
                break;

            case 7:  // Store addr
                memWriter.println("2," + operand + "," + accumulator);
                programCounter++;
                break;

            case 8:  // Get
                accumulator = (int) (Math.random() * 100) + 1;
                programCounter++;
                break;

            case 9:  // Put port
                if (operand == 1) {
                    System.out.print(accumulator);
                } else {
                    System.out.print((char) accumulator);
                }
                programCounter++;
                break;

            case 10:  // AddX
                accumulator += xRegister;
                programCounter++;
                break;

            case 11:  // AddY
                accumulator += yRegister;
                programCounter++;
                break;

            case 12:  // SubX
                accumulator -= xRegister;
                programCounter++;
                break;

            case 13:  // SubY
                accumulator -= yRegister;
                programCounter++;
                break;

            case 15:  // CopyFromX
                accumulator = xRegister;
                programCounter++;
                break;

            case 17:  // CopyFromY
                accumulator = yRegister;
                programCounter++;
                break;

            case 18:  // CopyToSp
                stackPointer = accumulator;
                programCounter++;
                break;

            case 19:  // CopyFromSp
                accumulator = stackPointer;
                programCounter++;
                break;

            case 20:  // Jump to the address
                programCounter = operand;
                break;

            case 21:  // Jump to the address only if the value in the AC is zero
                if (accumulator == 0) {
                    programCounter = operand;
                } else {
                    programCounter++;
                }
                break;

            case 22:  // JumpIfNotEqual
                if (accumulator != 0) {
                    memWriter.println("3," + operand);
                }
                programCounter++;
                break;

            case 23:  // Call
                // Push return address onto stack, jump to the address
                programCounter++;
                pushToStack(programCounter);
                programCounter = operand;
                break;

            case 24:  // Ret
                // Pop return address from the stack, jump to the address
                programCounter = popFromStack();
                break;

            case 25:  // IncX
                xRegister++;
                programCounter++;
                break;

            case 26:  // DecX
                xRegister--;
                programCounter++;
                break;

            case 27:  // Push
                pushToStack(accumulator);
                programCounter++;
                break;

            case 28:  // Pop
                accumulator = popFromStack();
                programCounter++;
                break;

            case 29:  // Int (Perform system call)
                kernelMode = true;
                stackPointer = 2000;  // Switch to system stack
                pushToStack(stackPointer);
                pushToStack(programCounter);
                programCounter = 1500;  // Jump to system call address
            break;

            case 30:  // IRet (Return from system call)
                programCounter = popFromStack();
                stackPointer = popFromStack();
                kernelMode = false;  // Switch back to user mode
                break;
            
            case 50: // End
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command");
        }

        programCounter++; // Increment program counter
    }

    private static void triggerTimerInterrupt() {
        kernelMode = true; // Switch to Kernel Mode
    
        // Switch to system stack and push saved values
        stackPointer = 1000;
        pushToStack(stackPointer);
        pushToStack(programCounter);
    
        // Jump to timer interrupt handler at address 1000
        programCounter = 1000;
    }
    
    private static void pushToStack(int value) {
        // Function to push a value onto the current stack
        memWriter.println("2," + stackPointer + "," + value);
        stackPointer--;
    }
    private static int popFromStack() {
        memWriter.println("1," + stackPointer);
        int value = Integer.parseInt(memScanner.nextLine());
        stackPointer++;
        return value;
    }
}
