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

            case Opcode.STR: // Store register to memory
                executeSTR(inst);
                break;

            case Opcode.LDA: // Load register with address
                executeLDA(inst);
                break;

            case Opcode.LDX: // Load index register from memory
                executeLDX(inst);
                break;

            case Opcode.STX: // Store index register to memory
                executeSTX(inst);
                break;

            case Opcode.HLT:
                return false;

            //helpful for debugging
            default:
                System.out.println("Unknown instruction: opcode=" + inst.opcode);
                return false;
        }

        return true;
    }

    //to do, make each instrction from the cases

    //load gpr from mem
    private void executeLDR(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory); //take address, add index reg if any, checks indirect mem
        int value = memory.read(ea); //reads word at effective address from mem

        regs.getGPR(inst.r).set(value); //write val into general purpose register
    }

    //store gpr to mem
    private void executeSTR(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        int value = regs.getGPR(inst.r).get();
        memory.write(ea, (short)value);
    }

    //load GPR w/ the address itself, not memory content
    private void executeLDA(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        regs.getGPR(inst.r).set(ea);
    }

    ///oad index register from mem
    private void executeLDX(Instruction inst) {
        int value = memory.read(inst.address); // LDX does not use indirect or IX
        regs.getIX(inst.r).set(value); // inst.r is IX reg number
    }

    //store index register to mem
    private void executeSTX(Instruction inst) {
        int value = regs.getIX(inst.r).get();
        memory.write(inst.address, (short)value);
    }
}
