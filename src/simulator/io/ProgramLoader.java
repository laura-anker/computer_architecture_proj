package simulator.io;

import simulator.memory.Memory;

import java.io.BufferedReader;
import java.io.FileReader;

public class ProgramLoader {

    /**
     * Loads a program from a file into memory.
     * Each line of the file should have the format:
     *   ADDRESS VALUE
     * Both in octal. Example:
     *   000024 002034
     *
     * @param filename the path to the load file
     * @param memory the Memory object to load the program into
     * @return the starting address of the first instruction in the file
     */
    public int load(String filename, Memory memory) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line; //current line being read
            int startAddress = -1; //address of first instruction, to be returned

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (line.startsWith("START")) {
                    String[] parts = line.split("\\s+");
                    return Integer.parseInt(parts[1], 8);   // return start address in octal
                }

                //split line into address and value
                String[] parts = line.split("\\s+");
                if (parts.length < 2)
                    continue;//skip weird lines

                try {
                    //parse address and value
                    int address = Integer.parseInt(parts[0], 8);
                    int value   = Integer.parseInt(parts[1], 8);

                    //store value at address in memory
                    memory.write(address, (short) value);

                    System.out.println("Loaded @" + parts[0] + " = " + parts[1]);


                    int opcode = (value >> 10) & 0x3F;//we can maybe delete this? probs old

                    // Set the start address to the first loaded memory location
                    // This assumes the first word in the file is the first instruction
                    // extract opcode

                    // skip data 
                    if (startAddress == -1 && opcode != 0) {
                        startAddress = address;
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            //initialize TRAP table
            int trapTableBase = 0100; // base address for TRAP routines, may need to be changed later?? idk if it'll conflict with loaded programs, but we can change it if it does

            //store base address in memory[0]
            memory.write(0, (short) trapTableBase);

            //initialize all 16 entries to something safe
            for (int i = 0; i < 16; i++) {
                memory.write(trapTableBase + i, (short) 0300); // default routine
            }
            //initialize machine fault handler
            int faultHandlerBase = 0200; // base address for fault handler
            memory.write(1, (short) faultHandlerBase);
            // perhaps initialize some fault handling code, but for now, just set address

            return startAddress;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
