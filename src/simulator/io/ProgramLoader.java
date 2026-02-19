package simulator.io;

import simulator.memory.Memory;

import java.io.BufferedReader;
import java.io.FileReader;

public class ProgramLoader {

    public int load(String filename, Memory memory) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;
            int firstAddress = -1;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue; //skip empyt lines
                String[] parts = line.split("\\s+");
                if (parts.length < 2)
                    continue; //split into address and value fields
                try {
                    int address = Integer.parseInt(parts[0], 8);
                    int value   = Integer.parseInt(parts[1], 8);

                    memory.write(address, (short) value); //store word in memory simulation at address

                    if (firstAddress == -1)
                        firstAddress = address; //tracks first mem loc loaded
                    System.out.println("Loaded @" + parts[0] + " = " + parts[1]);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return firstAddress;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
