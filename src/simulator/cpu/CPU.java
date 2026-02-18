package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;
//for execution
public class CPU {

    private Memory memory;
    private RegisterFile registers;

    private InstructionDecoder decoder;
    private InstructionExecutor executor;

    public CPU(Memory memory) {
        this.memory = memory;
        this.registers = new RegisterFile();

        decoder = new InstructionDecoder();
        executor = new InstructionExecutor(registers, memory);
    }

    public RegisterFile getRegisters() {
        return registers;
    }

    public void cycle() {

        fetch();

        Instruction inst = decode();

        boolean cont = execute(inst);

        if (!cont)
            System.out.println("HALT encountered.");
    }

    private void fetch() {

        registers.MAR.set(registers.PC.get());

        short word = memory.read(registers.MAR.get());
        registers.MBR.set(word);

        registers.IR.set(word);

        registers.PC.set(registers.PC.get() + 1);
    }

    private Instruction decode() {
        return decoder.decode((short) registers.IR.get());
    }

    private boolean execute(Instruction inst) {
        return executor.execute(inst);
    }

    public void printState() {
        System.out.println("PC=" + registers.PC.get()
                + " R0=" + registers.GPR[0].get());
    }
}
