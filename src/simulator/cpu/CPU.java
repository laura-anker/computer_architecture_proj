package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;
import simulator.io.IODevice;
//for execution (fetch decode execute)
public class CPU {

    private Memory memory; //holds instructions and data
    private RegisterFile registers; //holds all registers

    private InstructionDecoder decoder; //coverts binary to instruction
    private InstructionExecutor executor; //interacts with registers and memory to complete instruction

    private IODevice io;   // for in and out instructions


    public CPU(Memory memory) {
        this.memory = memory;
        this.registers = new RegisterFile();

        this.decoder = new InstructionDecoder();
        this.executor = new InstructionExecutor(registers, memory, this);

        // Set RegisterFile in Memory for fault handling
        if (memory instanceof simulator.memory.Memory) {
            ((simulator.memory.Memory) memory).setRegisterFile(registers);
        }
    }

    public void setIODevice(IODevice io) {
        this.io = io;
    }

    public IODevice getIODevice() {
        return io;
    }


    public RegisterFile getRegisters() {
        return registers;
    }

    public boolean cycle() { //individually calls stages of the cycle and prints for testing purposes
        System.out.println(
            "PC BEFORE FETCH = " +
            Integer.toOctalString(registers.PC.get())
        );

        System.out.printf("DEBUG after load: mem[256] = %o\n", memory.read(0256));


        fetch();
        Instruction inst = decode();
        boolean cont = execute(inst);
        
        System.out.println(
            "PC AFTER EXECUTE = " +
            Integer.toOctalString(registers.PC.get())
        );

        if (!cont)
            System.out.println("HALT encountered.");

        return cont;
    }

    private void fetch() {

        registers.MAR.set(registers.PC.get()); //mem addr reg points to next instruction

        int word = memory.read(registers.MAR.get()) & 0xFFFF; //read the instruction
        registers.MBR.set(word); //store it in the memory buffer reg

        registers.IR.set(word); //copy into instruction register and store for decoding

        registers.PC.set(registers.PC.get() + 1); //incremend pc to get ready for the next instruction
    }

    private Instruction decode() {
        return decoder.decode(registers.IR.get()); //turns binary from IR to the instruction object to use in execute
    }

    //To do
    private boolean execute(Instruction inst) {
        return executor.execute(inst); 
    }

    //should extend to print all registers so we can debug if needed
    public void printState() {
    System.out.print("PC=" + Integer.toOctalString(registers.PC.get()));
    for(int i=0; i<registers.GPR.length; i++){
        System.out.print(" R" + i + "=" + registers.GPR[i].get());
    }
    System.out.println();
}
}
