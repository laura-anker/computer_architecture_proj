package simulator.instruction;
//represents decoded instruction

//need different types for different types of instructions!

public class Instruction {

    // common
    public int opcode;

    // load/store format
    public int r;
    public int ix;
    public int i;
    public int address;

    // register-to-register
    public int rx;
    public int ry;

    // shift / rotate
    //also has r
    public int al;
    public int lr;
    public int count;

    // IO
    //also has r
    public int deviceId;

    // trap
    public int trapCode;

    public Instruction(int opcode) {
        this.opcode = opcode;
    }
}
