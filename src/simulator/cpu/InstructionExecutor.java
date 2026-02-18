package simulator.cpu;

import simulator.instruction.Opcode;
import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

//executes instructions
public class InstructionExecutor {

    private RegisterFile regs;
    private Memory memory;
    private EffectiveAddressCalculator eaCalc;

    public InstructionExecutor(RegisterFile regs, Memory memory) {
        this.regs = regs;
        this.memory = memory;
        this.eaCalc = new EffectiveAddressCalculator();
    }

    public boolean execute(Instruction inst) {

        switch (inst.opcode) {

            case Opcode.LDR:
                executeLDR(inst);
                break;

            case Opcode.HLT:
                return false;
        }

        return true;
    }

    private void executeLDR(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory);
        int value = memory.read(ea);

        regs.getGPR(inst.r).set(value);
    }
}
