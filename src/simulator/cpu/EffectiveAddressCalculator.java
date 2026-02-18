package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

public class EffectiveAddressCalculator {

    public int computeEA(Instruction inst,
                         RegisterFile regs,
                         Memory memory) {

        int ea = inst.address;

        if (inst.ix != 0)
            ea += regs.getIX(inst.ix).get();

        if (inst.i == 1)
            ea = memory.read(ea);

        return ea;
    }
}
