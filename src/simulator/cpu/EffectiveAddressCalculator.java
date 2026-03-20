package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

public class EffectiveAddressCalculator {

    public int computeEA(Instruction inst,
                     RegisterFile regs,
                     Memory memory) {

        int ea = inst.address; // base address

        // indexing
        if (inst.ix != 0) {
            ea += regs.getIX(inst.ix).get();
        }

        // indirect
        if (inst.i == 1) {
            ea = memory.read(ea) & 0xFFFF;  // ✅ FIX
        }

        return ea & 0x3FF;  // ✅ also good practice
    }
}
