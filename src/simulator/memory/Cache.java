package simulator.memory;

public class Cache extends Memory {

    private static final int CACHE_SIZE = 16;

    private CacheLine[] lines = new CacheLine[CACHE_SIZE];
    private Memory backingMemory;
    private int fifoCounter = 0;

    public Cache(Memory backingMemory) {
        this.backingMemory = backingMemory;

        for (int i = 0; i < CACHE_SIZE; i++) {
            lines[i] = new CacheLine();
        }
    }

    @Override
    public short read(int address) {
        // search cache
        for (CacheLine line : lines) {
            if (line.valid && line.tag == address) {
                //System.out.println("CACHE READ " + address + " → HIT");
                return line.data; // HIT
            }
        }

        // MISS → fetch from mem
        short value = backingMemory.read(address);

        insert(address, value);
        //System.out.println("CACHE READ " + address + " → MISS");
        return value;
    }

    @Override
    public void write(int address, short value) {
        // write-through to memory
        backingMemory.write(address, value);

        // update cache if present
        for (CacheLine line : lines) {
            if (line.valid && line.tag == address) {
                line.data = value;
                return;
            }
        }
    }

    private void insert(int address, short value) {
        // check invalid line
        for (CacheLine line : lines) {
            if (!line.valid) {
                fillLine(line, address, value);
                return;
            }
        }

        // invalid lines = FIFO eviction
        CacheLine oldest = lines[0];
        for (CacheLine line : lines) {
            if (line.age < oldest.age) {
                oldest = line;
            }
        }

        fillLine(oldest, address, value);
    }

    private void fillLine(CacheLine line, int address, short value) {
        line.valid = true;
        line.tag = address;
        line.data = value;
        line.age = fifoCounter++;
    }
}
