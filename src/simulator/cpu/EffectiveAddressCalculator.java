package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

public class EffectiveAddressCalculator {

    public int computeEA(Instruction inst,
                         RegisterFile regs,
                         Memory memory) {

        int ea = inst.address; //base address

        if (inst.ix != 0)       //if index add its value to the base
            ea += regs.getIX(inst.ix).get();

        if (inst.i == 1)        //checking the indirect bit, not an address but the contents of memory
            ea = memory.read(ea);

        return ea; //final memory address that cpu uses for read/write
    }
}
