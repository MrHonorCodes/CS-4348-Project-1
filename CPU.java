
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class CPU
{
    private int PC, SP, IR, AC, X, Y;
    private int timer;
    private boolean kernelMode;
    private Scanner memoryIn;
    private PrintWriter memoryOut;

    public CPU(Scanner memoryIn, PrintWriter memoryOut) {
        this.memoryIn = memoryIn;
        this.memoryOut = memoryOut;
        kernelMode = false;
        PC = IR = AC = X = Y = timer = 0;
        SP = 1000;
    }

    public static void main(String[] args) {
		/* 
		System.out.println("Number of arguments in CPU: " + args.length);
		for (String arg : args) {
			System.out.println("Argument in CPU: " + arg);
		}
		*/
        if (args.length < 1) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String programInput = args[0];
		//System.out.println("Program Input: " + programInput);

        try {
            ProcessBuilder builder = new ProcessBuilder("java", "Memory", programInput);
            Process memory = builder.start();

            final InputStream error = memory.getErrorStream();
            new Thread(() -> {
                byte[] buffer = new byte[8192];
                int length;
                try {
                    while ((length = error.read(buffer)) > 0) {
                        System.err.write(buffer, 0, length);
                    }
                } catch (IOException exp) {
                    exp.printStackTrace();
                }
            }).start();

            Scanner memoryIn = new Scanner(memory.getInputStream());
            PrintWriter memoryOut = new PrintWriter(memory.getOutputStream());
            CPU cpu = new CPU(memoryIn, memoryOut);
            cpu.run();
        } catch (IOException exp) {
            exp.printStackTrace();
            System.err.println("Unable to create new process.");
            System.exit(1);
        }
    }
    /* 
		private void fetch()
		{
			IR = readMemory(PC++);
		}
		*/
		private void push(int data)
		{
			writeMemory(--SP, data);
		}
		
		private int pop()
		{
			return readMemory(SP++);
		}

        public void run()
        {
            boolean running = true;
            int myTimeout = 100;  // Replace this with whatever value makes sense for your application.
            while(running)
            {
				IR = readMemory(PC++);
                running = instructionExecute();
                timer++;
                
                if(timer >= myTimeout)
                {
                    if(!kernelMode) 
                    {
                        timer = 0;
                        kernelMode();
                        PC = 1000;
                    }
                }
            }
        }
		private  void kernelMode()
		{
			kernelMode = true;
			int tempSP = SP; 
			SP = 2000;
			push(tempSP);
			push(PC);
			push(IR);
			push(AC);
			push(X);
			push(Y);
		}

		private int readMemory(int address)
		{
			if(address >= 1000 && !kernelMode)
			{
				System.err.println("Memory violation: accessing system address " + address + " in user mode");
				System.exit(-1);
			}
			memoryOut.println("r"+address);
			memoryOut.flush();
			return Integer.parseInt(memoryIn.nextLine());
		}
		
		private void writeMemory(int address, int data)
		{
			memoryOut.printf("w%d,%d\n", address, data);
			memoryOut.flush();
		}
		
		private void endMemoryProcess()
		{
			memoryOut.println("e");
			memoryOut.flush();
		}

		private boolean instructionExecute()
		{
			switch(IR)
			{
				case 1: // Load value: Load value into AC
					IR = readMemory(PC++);
					AC = IR;
					break;
				case 2: // Load addr: Load value at address into AC
					IR = readMemory(PC++);
					AC = readMemory(IR);
					break;
				case 3: // LoadInd addr: Load value from address at given address into AC
					IR = readMemory(PC++);
					AC = readMemory(readMemory(IR));
					break;
				case 4: // LoadInxX addr: Load value at (given address + X) into AC
					IR = readMemory(PC++);
					AC = readMemory(IR + X);
					break;
				case 5: // LoadInxY addr: Load value at (given address + Y) into AC
					IR = readMemory(PC++);
					AC = readMemory(IR + Y);
					break;
				case 6: // LoadSpX: Load from (SP+X) into AC
					AC = readMemory(SP+X);
					break;
				case 7: // Store addr: Store AC to address
					IR = readMemory(PC++);
					writeMemory(IR, AC);
					break;
				case 8: // Get: Get random int 1-100 into AC
					AC = (int) (Math.random()*100+1);
					break;
				case 9: // Put port: If port=1, write AC to screen as int, if port=2, write AC to screen as char
					IR = readMemory(PC++);
					if(IR == 1)
					{
						System.out.print(AC);
					}
						
					else if(IR == 2)
					{
						System.out.print((char)AC);
					}
						
					break;
				case 10: // AddX: Add X to AC
					AC += X; 
					break;
				case 11: // AddY: Add Y to AC
					AC += Y; 
					break;
				case 12: // SubX: Sub X to AC
					AC -= X; 
					break;
				case 13: // SubY: Sub Y to AC
					AC -= Y; 
					break;
				case 14: // CopyToX: Copy value in AC to X
					X = AC; 
					break;
				case 15: // CopyFromX: Copy value in X to AC
					AC = X; 
					break;
				case 16: // CopyToY: Copy AC to Y
					Y = AC; 
					break;
				case 17: // CopyFromY: Copy Y to AC
					AC = Y; 
					break;
				case 18: // CopyToSp: Copy AC to SP
					SP = AC; 
					break;
				case 19: // CopyFromSp: Copy SP to AC
					AC = SP; 
					break;
				case 20: // Jump addr: Jump to address
					IR = readMemory(PC++);
					PC = IR;
					break;
				case 21: // JumpIfEqual addr: Jump only if AC is zero
					IR = readMemory(PC++);
					if(AC == 0)
						PC = IR;
					break;
				case 22: // JumpIfNotEqual addr: Jump only if AC is not zero
					IR = readMemory(PC++);
					if(AC != 0)
						PC = IR;
					break;
				case 23: // Call addr: Push return addr to stack, jump
					IR = readMemory(PC++);
					push(PC);
					PC = IR;
					break;
				case 24: // Ret: Pop return addr, jump back
					PC = pop();
					break;
				case 25: // IncX: Increment X
					X++; 
					break;
				case 26: // DecX: Decrement X
					X--; 
					break;
				case 27: // Push: Push AC onto stack
					push(AC);
					break;
				case 28: // Pop: Pop from stack onto AC
					AC = pop();
					break;
				case 29: 
					// Disable interrupts during interrupt processing
					if(!kernelMode)
					{
						kernelMode();
						PC = 1500;
					}
					break;
				case 30: 
					Y = pop();
					X = pop();
					AC = pop();
					IR = pop();
					PC = pop();
					SP = pop();
					kernelMode = false;
					break;
				case 50: 
					endMemoryProcess();
					return false;
				default: 
					System.err.println("Invalid instruction.");
					endMemoryProcess();
					return false;
			}
			return true;
		}
		
	}
