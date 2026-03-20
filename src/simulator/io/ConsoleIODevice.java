package simulator.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * IODevice that reads from stdin and writes to stdout
 * Useful for console-based testing with piped input
 */
public class ConsoleIODevice implements IODevice {
    
    private BufferedReader reader;
    private StringBuilder inputBuffer;
    private int inputPosition = 0;

    public ConsoleIODevice() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.inputBuffer = new StringBuilder();
    }

    @Override
    public int readChar() {
        // If we've consumed all buffered input, read a new line
        if (inputPosition >= inputBuffer.length()) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    return 0; // EOF
                }
                inputBuffer = new StringBuilder(line + "\n");
                inputPosition = 0;
            } catch (IOException e) {
                System.err.println("Error reading from stdin: " + e.getMessage());
                return 0;
            }
        }

        // Return next character from buffer
        char c = inputBuffer.charAt(inputPosition);
        inputPosition++;
        return (int) c;
    }

    @Override
    public void writeChar(int c) {
        System.out.print((char) c);
        System.out.flush();
    }
}
