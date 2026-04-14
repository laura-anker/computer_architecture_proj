package simulator.memory;
//represents RAM

import simulator.registers.RegisterFile;

public class Memory {

    private short[] memory = new short[2048];
    private RegisterFile registers;

    public Memory() {
        // Default constructor for backward compatibility
    }

    public Memory(RegisterFile registers) {
        this.registers = registers;
    }

    public void setRegisterFile(RegisterFile registers) {
        this.registers = registers;
    }

    public short read(int address) {
        if (address < 0 || address >= memory.length) {
            if (registers != null) {
                registers.MFR.set(8); // 1000 binary for address beyond 2048
            }
            throw new RuntimeException("Invalid memory address");
        }
        return memory[address]; //returns the 16bit word at the addr
    }

    public void write(int address, short value) { //write to mem, used by program loader, instruction executor
        if (address < 0 || address >= memory.length) {
            if (registers != null) {
                registers.MFR.set(8); // 1000 binary for address beyond 2048
            }
            throw new RuntimeException("Invalid memory address");
        }
        memory[address] = value;
    }
}

