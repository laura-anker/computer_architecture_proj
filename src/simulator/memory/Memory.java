package simulator.memory;
//represents RAM

public class Memory {

    private short[] memory = new short[2048];

    public short read(int address) {
        check(address);//ensure valid
        return memory[address]; //returns the 16bit word at the addr
    }

    public void write(int address, short value) { //write to mem, used by program loader, instruction executor
        check(address);
        memory[address] = value;
    }

    private void check(int addr) {
        if (addr < 0 || addr >= memory.length)
            throw new RuntimeException("Invalid memory address");
    }
}

