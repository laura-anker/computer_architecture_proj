package simulator;

import simulator.cpu.CPU;
import simulator.memory.Cache;
import simulator.memory.Memory;
import simulator.io.ProgramLoader;

public class Simulator {

    private CPU cpu;
    private Memory memory;

    public Simulator() {
        //before cache implementation
        //memory = new Memory();
        //cpu = new CPU(memory);

        //after cache
        Memory ram = new Memory();      // real RAM
        Memory cache = new Cache(ram);  // cache wraps RAM

        this.memory = cache;            // simulator uses cache as memory
        this.cpu = new CPU(cache);      // CPU uses cache too
    }

    public void start() {
        // Load the assembled load file
        ProgramLoader loader = new ProgramLoader();
        int start = loader.load("test_load_part2_4.txt", memory);

        System.out.println("Start address (octal)= " + Integer.toOctalString(start));

        //set pc
        //cpu.getRegisters().PC.set(start); //currently hardcoded to test p1
        
        cpu.getRegisters().PC.set(0016);

        //Setup index registers
        cpu.getRegisters().IX[1].set(1);  // X1 = 1 for indexed instructions

        //run until HLT
        boolean running = true;
        while (running) {
            running = cpu.cycle();  // fetch-decode-execute
            cpu.printState();       // print PC + GPRs
        }

        //print final memory locations used in test
        //System.out.println("\nMemory dump of test addresses:");
        //int[] testAddrs = {20, 21, 25, 30, 31};
        //for (int addr : testAddrs) {
            //System.out.printf("Mem[%02o] = %o%n", addr, memory.read(addr));
        //}
    }
}