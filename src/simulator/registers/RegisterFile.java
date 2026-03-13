package simulator.registers;

public class RegisterFile {

    public Register[] GPR = new Register[4]; // 16 bits
    public Register[] IX = new Register[4];  // 16 bits

    public Register PC;   // 12 bits
    public Register CC;   // 4 bits
    public Register IR;   // 16 bits
    public Register MAR;  // 12 bits
    public Register MBR;  // 16 bits
    public Register MFR;  // 4 bits

    public RegisterFile() {

        for (int i = 0; i < 4; i++) {
            GPR[i] = new Register(16);
            IX[i] = new Register(16);
        }

        PC = new Register(12);
        CC = new Register(4);
        IR = new Register(16);
        MAR = new Register(12);
        MBR = new Register(16);
        MFR = new Register(4);
    }

    public Register getGPR(int r) {
        return GPR[r];
    }

    public Register getIX(int x) {
        return IX[x];
    }

    public Register getPC() {
        return PC;
    }

    public int getCCBit(int bit) {
        return (CC.get() >> bit) & 1;
    }

    public void setCCBit(int bit, int value) {

        int cc = CC.get();

        if (value == 1)
            cc |= (1 << bit);
        else
            cc &= ~(1 << bit);

        CC.set(cc);
    }
}
