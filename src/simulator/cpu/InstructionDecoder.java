package simulator.cpu;

//extracts feilds from instruction
import simulator.instruction.Instruction;

public class InstructionDecoder {

    public Instruction decode(short word) {

        int opcode = (word >> 10) & 0x3F;
        int r = (word >> 8) & 0x3;
        int ix = (word >> 6) & 0x3;
        int i = (word >> 5) & 0x1;
        int address = word & 0x1F;

        return new Instruction(opcode, r, ix, i, address);
    }
}

