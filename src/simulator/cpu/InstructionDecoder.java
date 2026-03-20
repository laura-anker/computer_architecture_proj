package simulator.cpu;

import simulator.instruction.Instruction;
import simulator.instruction.Opcode;

public class InstructionDecoder {

    public Instruction decode(short word) {

        int w = word & 0xFFFF;   // treat instruction as unsigned

        int opcode = (w >> 10) & 0x3F;

        Instruction inst = new Instruction(opcode);

        switch (opcode) {

            // --------------------------------------------------
            // Load / Store / Memory Reference Format
            // Opcode | R | IX | I | Address
            // --------------------------------------------------

            case Opcode.LDR:
            case Opcode.STR:
            case Opcode.LDA:
            case Opcode.AMR:
            case Opcode.SMR:
            case Opcode.JZ:
            case Opcode.JNE:
            case Opcode.JCC:
            case Opcode.JMA:
            case Opcode.JSR:
            case Opcode.SOB:
            case Opcode.JGE:
            case Opcode.LDX:
            case Opcode.STX:

                inst.r       = (w >> 8) & 0x3;
                inst.ix      = (w >> 6) & 0x3;
                inst.i       = (w >> 5) & 0x1;
                inst.address =  w       & 0x1F;

                break;

            // --------------------------------------------------
            // Immediate Instructions
            // Opcode | R | Immediate(5)
            // --------------------------------------------------

            case Opcode.AIR:
            case Opcode.SIR:

                inst.r       = (w >> 8) & 0x3;
                inst.address =  w       & 0x1F;

                break;

            // --------------------------------------------------
            // Register-to-Register
            // Opcode | Rx | Ry
            // --------------------------------------------------

            case Opcode.MLT:
            case Opcode.DVD:
            case Opcode.TRR:
            case Opcode.AND:
            case Opcode.ORR:
            case Opcode.NOT:

                inst.r  = (w >> 8) & 0x3;
                inst.ry = (w >> 6) & 0x3;

                break;

            // --------------------------------------------------
            // Shift / Rotate
            // Opcode | R | AL | LR | Count
            // --------------------------------------------------

            case Opcode.SRC:
            case Opcode.RRC:

                inst.r     = (w >> 8) & 0x3;
                inst.al    = (w >> 7) & 0x1;
                inst.lr    = (w >> 6) & 0x1;
                inst.count = w & 0xF;        

                break;
            // --------------------------------------------------
            // I/O instructions
            // Opcode | R | DeviceID
            // --------------------------------------------------

            case Opcode.IN:
            case Opcode.OUT:
            case Opcode.CHK:

                inst.r        = (w >> 8) & 0x3;
                inst.deviceId =  w       & 0xFF;

                break;

            // --------------------------------------------------
            // Trap
            // Opcode | TrapCode
            // --------------------------------------------------

            case Opcode.TRAP:

                inst.trapCode =  w & 0xF;

                break;

            default:
                System.out.println("Unknown opcode: " + opcode);
        }

        return inst;
    }
}