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

        ProgramLoader loader = new ProgramLoader();
        int start = loader.load("programs/load.txt", memory);
        cpu.getRegisters().PC.set(start);



        singleStep();
        singleStep();
    }

    public void singleStep() {
        cpu.cycle();
        cpu.printState();
    }
}

