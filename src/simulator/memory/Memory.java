package simulator.memory;
//represents RAM

import simulator.registers.RegisterFile;

public class Memory {

    private int[] memory = new int[2048];
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

    public int read(int address) {
        /* 
        if (address < 0 || address >= memory.length) {
            if (registers != null) {
                registers.MFR.set(8); // 1000 binary for address beyond 2048
            }
            throw new RuntimeException("Invalid memory address");
        }*/
        address &= 0x7FF;
        return memory[address] & 0xFFFF; //returns the 16bit word at the addr
    }

    public void write(int address, int value) {
        /*  //write to mem, used by program loader, instruction executor
        if (address < 0 || address >= memory.length) {
            if (registers != null) {
                registers.MFR.set(8); // 1000 binary for address beyond 2048
            }
            throw new RuntimeException("Invalid memory address");
        }*/
        address &= 0x7FF;
        memory[address] = value & 0xFFFF;
    }
}

