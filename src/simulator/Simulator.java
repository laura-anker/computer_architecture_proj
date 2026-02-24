package simulator;

import simulator.cpu.CPU;
import simulator.memory.Memory;
import simulator.io.ProgramLoader;

//connects components, handles user commands, runs execution loop

public class Simulator {

    private CPU cpu;
    private Memory memory;

    public Simulator() {
        memory = new Memory();
        cpu = new CPU(memory);
    }

    public void start() {
        //load load file, can edit to other file name
        ProgramLoader loader = new ProgramLoader();
        int start = loader.load("programs/test_load_part1.txt", memory);
        //print statement just for debugging
        System.out.println("Start address (octal)= "+Integer.toOctalString(start));
        cpu.getRegisters().PC.set(start);


        //currently hard coded for testing
        singleStep();
        singleStep();
    }

    public void singleStep() { //executes and prints one cycle from cpu
        cpu.cycle();
        cpu.printState();
    }
}

