package simulator.registers;
//generic register abstraction

public class Register {

    private int value;

    public int get() {
        return value;
    }

    public void set(int v) {
        value = v & 0xFFFF; // 16-bit
    }
}
