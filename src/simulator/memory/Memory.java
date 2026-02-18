package simulator.memory;
//represents RAM

public class Memory {

    private short[] memory = new short[2048];

    public short read(int address) {
        check(address);
        return memory[address];
    }

    public void write(int address, short value) {
        check(address);
        memory[address] = value;
    }

    private void check(int addr) {
        if (addr < 0 || addr >= memory.length)
            throw new RuntimeException("Invalid memory address");
    }
}

