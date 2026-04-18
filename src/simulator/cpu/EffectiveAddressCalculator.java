package simulator.cpu;

import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

public class EffectiveAddressCalculator {

    public int computeEA(Instruction inst,
                         RegisterFile regs,
                         Memory memory) {

        int ea = inst.address & 0x7FF; //base address
            

        if (inst.i == 1) {       //checking the indirect bit, not an address but the contents of memory
            System.out.printf("DEBUG before indirect: mem[%o] = %o\n", ea, memory.read(ea));
            ea = memory.read(ea) & 0x7FF; //masking to get the last 16 bits since memory is word addressable and we want the value at that address
        }

        if (inst.ix != 0){
                    //int ixVal = regs.getIX(inst.ix).get() & 0x3FF;  // use only low 10 bits as address
                    //ea = (ea + ixVal) & 0x3FF;
                    ea += regs.getIX(inst.ix).get() & 0x7FF;
                }       //if index add its value to the base
                    //
        System.out.printf(
    "EA DEBUG: opcode=%d r=%d ix=%d i=%d addr=%o → EA=%o\n",
    inst.opcode, inst.r, inst.ix, inst.i, inst.address, ea
);

        return ea; //final memory address that cpu uses for read/write
    }
}
