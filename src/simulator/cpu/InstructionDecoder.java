package simulator.cpu;

//extracts fields from instruction
import simulator.instruction.Instruction;

public class InstructionDecoder {

    public Instruction decode(short word) {

        int w = word & 0xFFFF;   //unsigned

        int opcode  = (w >> 10) & 0x3F;
        int r       = (w >> 8)  & 0x3;
        int ix      = (w >> 6)  & 0x3;
        int i       = (w >> 5)  & 0x1;
        int address =  w        & 0x1F;

        return new Instruction(opcode, r, ix, i, address);
    }
}

