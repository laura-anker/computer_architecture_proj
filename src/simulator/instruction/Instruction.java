package simulator.instruction;
//represents decoded instruction

public class Instruction {

    public int opcode;
    public int r;
    public int ix;
    public int i;
    public int address;

    public Instruction(int opcode, int r, int ix, int i, int address) {
        this.opcode = opcode;
        this.r = r;
        this.ix = ix;
        this.i = i;
        this.address = address;
    }
}
