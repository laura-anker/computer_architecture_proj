package simulator.io;

public interface IODevice {
    int readChar();        // for IN
    void writeChar(int c); // for OUT
}
