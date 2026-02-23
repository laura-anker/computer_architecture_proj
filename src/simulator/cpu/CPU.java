package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;
//for execution (fetch decode execute)
public class CPU {

    private Memory memory; //holds instructions and data
    private RegisterFile registers; //holds all registers

    private InstructionDecoder decoder; //coverts binary to instruction
    private InstructionExecutor executor; //interacts with registers and memory to complete instruction

    public CPU(Memory memory) {
        this.memory = memory;
        this.registers = new RegisterFile();

        decoder = new InstructionDecoder();
        executor = new InstructionExecutor(registers, memory);
    }

    public RegisterFile getRegisters() {
        return registers;
    }

    public void cycle() { //individually calls stages of the cycle
        System.out.println(
            "PC BEFORE FETCH = " +
            Integer.toOctalString(registers.PC.get())
        );

        fetch();

        Instruction inst = decode();

        boolean cont = execute(inst);
        System.out.println(
            "PC AFTER EXECUTE = " +
            Integer.toOctalString(registers.PC.get())
        );

        if (!cont)
            System.out.println("HALT encountered.");
    }

    private void fetch() {

        registers.MAR.set(registers.PC.get()); //mem addr reg points to next instruction

        short word = memory.read(registers.MAR.get()); //read the instruction
        registers.MBR.set(word); //store it in the memory buffer reg

        registers.IR.set(word); //copy into instruction register and store for decoding

        registers.PC.set(registers.PC.get() + 1); //incremend pc to get ready for the next instruction
    }

    private Instruction decode() {
        return decoder.decode((short) registers.IR.get()); //turns binary from IR to the instruction object to use in execute
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
