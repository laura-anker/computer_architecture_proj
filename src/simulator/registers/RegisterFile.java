package simulator.registers;
//stores all registers

public class RegisterFile {

    public Register[] GPR = new Register[4]; //R0-R3
    public Register[] IX = new Register[4]; //X1-X3

    public Register PC = new Register(); //program counter
    public Register IR = new Register(); // instruction register
    public Register MAR = new Register(); //memeory address register
    public Register MBR = new Register();//memory buffer register

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
