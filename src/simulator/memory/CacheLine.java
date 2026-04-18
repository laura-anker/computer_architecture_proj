package simulator.memory;

public class CacheLine {
    public boolean valid;
    public int tag;      // memory address (0–2047)
    public int data;   // 16-bit word
    public int age;      // FIFO count

    public CacheLine() {
        valid = false;
        tag = -1;
        data = 0;
        age = 0;
    }
}
