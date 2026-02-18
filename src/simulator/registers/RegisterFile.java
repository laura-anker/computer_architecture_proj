package simulator.registers;
//stores all registers

public class RegisterFile {

    public Register[] GPR = new Register[4];
    public Register[] IX = new Register[4];

    public Register PC = new Register();
    public Register IR = new Register();
    public Register MAR = new Register();
    public Register MBR = new Register();

    public RegisterFile() {
        for (int i = 0; i < 4; i++) {
            GPR[i] = new Register();
            IX[i] = new Register();
        }
    }

    public Register getGPR(int r) {
        return GPR[r];
    }

    public Register getIX(int x) {
        return IX[x];
    }
}
