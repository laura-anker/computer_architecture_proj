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

    //todo finish adding opcode instructions for each case
    public boolean execute(Instruction inst) {
    
        switch (inst.opcode) {//check opcode for instruction

            case Opcode.LDR:
                executeLDR(inst); //calls methos
                break;

            case Opcode.HLT:
                return false;
        }

        return true;
    }

    //to do, make each instrction from the cases
    private void executeLDR(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory); //take address, add index reg if any, checks indirect mem
        int value = memory.read(ea); //reads word at effective address from mem

        regs.getGPR(inst.r).set(value); //write val into general purpose register
    }
}
