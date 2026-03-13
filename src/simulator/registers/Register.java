package simulator.registers;
//generic register abstraction

public class Register {

    private int value;
    private int mask;

    public Register(int bits) {
        this.mask = (1 << bits) - 1;  // creates bit mask
        this.value = 0;
    }

    public void set(int val) {
        value = val & mask;
    }

    public int get() {
        return value;
    }

    public void clear() {
        value = 0;
    }
}
