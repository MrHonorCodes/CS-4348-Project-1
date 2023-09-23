package project1;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class CustomCPU {
    static int programCounter = 0, stackPointer = 1000, instructionReg, accumulator, regX, regY, timerThreshold, instrCount = 0;
    static int sysStackTop = 2000, usrStackTop = 1000;
    
    static boolean isUserMode = true;
    static boolean isHandlingInterrupt = false;

    public static void main(String[] args) {
        String inputFileName = null;
        
        if(args.length == 2) {
            inputFileName = args[0];
            timerThreshold = Integer.parseInt(args[1]);
        } else {
            System.out.println("Invalid number of arguments. Exiting.");
            System.exit(0);
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process memoryProcess = runtime.exec("java Memory");
            
            OutputStream outStream = memoryProcess.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outStream);
            
            InputStream inStream = memoryProcess.getInputStream();
            Scanner memScanner = new Scanner(inStream);
            
            sendFileNameToMemory(printWriter, inStream, outStream, inputFileName);
            
            while (true) {
                if(instrCount > 0 && (instrCount % timerThreshold) == 0 && !isHandlingInterrupt) {
                    handleTimerInterrupt(printWriter, inStream, memScanner, outStream);
                }
                
                int fetchedValue = fetchFromMemory(printWriter, inStream, memScanner, outStream, programCounter);
                
                if (fetchedValue != -1) {
                    executeInstruction(fetchedValue, printWriter, inStream, memScanner, outStream);
                } else {
                    break;
                }
            }
            
            memoryProcess.waitFor();
            int exitCode = memoryProcess.exitValue();
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileNameToMemory(PrintWriter pw, InputStream is, OutputStream os, String fileName) {
        pw.println(fileName);
        pw.flush();
    }

    private static int fetchFromMemory(PrintWriter pw, InputStream is, Scanner memScanner, OutputStream os, int addr) {
        validateMemoryAccess(addr);
        pw.println("1," + addr);
        pw.flush();
        if (memScanner.hasNext()) {
            String temp = memScanner.next();
            if(!temp.isEmpty()) {
                return Integer.parseInt(temp);
            }
        }
        return -1;
    }

    private static void writeToMemory(PrintWriter pw, InputStream is, OutputStream os, int addr, int data) {
        pw.println("2," + addr + "," + data);
        pw.flush();
    }

    private static void executeInstruction(int fetchedValue, PrintWriter pw, InputStream is, Scanner memScanner, OutputStream os) {
        instructionReg = fetchedValue;
        int operand;
        
        switch(instructionReg) {
            case 1:
                programCounter++;
                operand = fetchFromMemory(pw, is, memScanner, os, programCounter);
                accumulator = operand;
                if(!isHandlingInterrupt) instrCount++;
                programCounter++;
                break;
            // ... (other cases)
            default:
                System.out.println("Unknown instruction.");
                System.exit(0);
        }
    }

    private static void validateMemoryAccess(int addr) {
        if(isUserMode && addr > 1000) {
            System.out.println("Memory violation. Exiting.");
            System.exit(0);
        }
    }

    private static void handleTimerInterrupt(PrintWriter pw, InputStream is, Scanner memScanner, OutputStream os) {
        isHandlingInterrupt = true;
        isUserMode = false;
        int tempSP = stackPointer;
        stackPointer = sysStackTop;
        pushToStack(pw, is, os, tempSP);
        
        int tempPC = programCounter;
        programCounter = 1000;
        pushToStack(pw, is, os, tempPC);
    }

    private static void pushToStack(PrintWriter pw, InputStream is, OutputStream os, int value) {
        stackPointer--;
        writeToMemory(pw, is, os, stackPointer, value);
    }

    private static int popFromStack(PrintWriter pw, InputStream is, Scanner memScanner, OutputStream os) {
        int value = fetchFromMemory(pw, is, memScanner, os, stackPointer);
        writeToMemory(pw, is, os, stackPointer, 0);
        stackPointer++;
        return value;
    }
}
