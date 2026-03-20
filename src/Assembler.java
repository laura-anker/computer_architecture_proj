import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Assembler {

    Map<String, Integer> dictionary = new HashMap<>();
    //int detectedStartAddress = -1;
    public void run(File sourceFile){
        //detectedStartAddress = -1; //reset
        if (pass1(sourceFile) == false){
            System.out.println("error! exiting pass 1");
            return;//error!
        }
        if (pass2(sourceFile) == false){
            System.out.println("error! exiting pass 2");
            return;
            //error!
        }
        return;
    }

    //build label/address map
    //return true if successful, false if error


    //start pass 1
    public boolean pass1(File sourceFile){
        //set code location to 0
        int codeLocation = 0;
        //Read a line of the file
        System.out.println("Reading from: " + sourceFile.getAbsolutePath());
        
        try (Scanner myReader = new Scanner(sourceFile)) { 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                //System.out.println("LINE: [" + data + "]");
                if (data.isEmpty() || data.startsWith(";")){//if line is empty or comment, move to next line
                    continue;
                }
                //Use the split command to break the line into parts
                String[] splitData = data.trim().split("\\s+");
                int index = 0;//this is to track which field we are working in
                //handle the label
                if (splitData[index].endsWith(":")){//indicates label, used to define location
                    String label = splitData[index].substring(0, splitData[index].length() - 1);//get label without :
                    if (dictionary.containsKey(label)){//error if label is duplicated
                        System.out.println("Duplicate label: "+label);
                        return false;
                    }
                    dictionary.put(label, codeLocation);//add label, location pair to dictionary
                    index++;
                    //label only line then continue
                    if (index >= splitData.length){
                        continue;
                    }
                }

                //get opcode or directive
                String opcode = splitData[index].toUpperCase();

                //LOC, we need to set the counter without generating memory
                if (opcode.equals("LOC")){
                    if (index + 1 >= splitData.length){
                        System.out.println("Missing operand for LOC");
                        return false;
                    }
                    try {
                        codeLocation = Integer.parseInt(splitData[index + 1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid LOC value: " + splitData[index + 1]);
                        return false;
                    }
                    continue; //we do not increment on LOC
                }

                //Data 
                if (opcode.equals("DATA")){
                    codeLocation += 1; //allocates one word and update location
                    continue;
                }
                //otherwise assume instruction generates code
                codeLocation += 1;
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }catch (Exception e) {
            System.out.println("Unexpected error in pass1:");
            e.printStackTrace();
            return false;
        }
        return true;
    }
//end of pass 1

    //use label/address map to generate machine code
    //return true if successful, false if error

//start opcode map
    Map<String, Integer> opcodeMap = new HashMap<>();
    //ALL VALUES STORED IN DECIMAL! Shifted later to create octal output
    public Assembler(){
        opcodeMap.put("HLT", 0);   // stop machine
        opcodeMap.put("LDR", 1);   // load register from memory
        opcodeMap.put("STR", 2);   // store register to memory
        opcodeMap.put("LDA", 3);   // load register with address
        opcodeMap.put("AMR", 4);   // add memory to register
        opcodeMap.put("SMR", 5);   // subtract memory from register
        opcodeMap.put("AIR", 6);   // add immediate to register
        opcodeMap.put("SIR", 7);   // subtract immediate from register
        opcodeMap.put("JZ", 8);   // jump if zero
        opcodeMap.put("JNE", 9);  // jump if not equal
        opcodeMap.put("JCC", 10);  // jump if condition code
        opcodeMap.put("JMA", 11);  // unconditional jump
        opcodeMap.put("JSR", 12);  // jump and save return address
        opcodeMap.put("RFS", 13);  // return from subroutine
        opcodeMap.put("SOB", 14);  // subtract one and branch
        opcodeMap.put("JGE", 15);  // jump greater than or equal to

        opcodeMap.put("TRAP", 24); // trap instruction
        opcodeMap.put("SRC", 25);  // shift register by count
        opcodeMap.put("RRC", 26);  // rotate register by count

        opcodeMap.put("LDX", 33);  // load index register from memory
        opcodeMap.put("STX", 34);  // store index register to memory

        opcodeMap.put("IN", 49);   // input character to register
        opcodeMap.put("OUT", 50);  // output character from register
        opcodeMap.put("CHK", 51);  // check device status to register

        opcodeMap.put("MLT", 56);  // multiply register by register
        opcodeMap.put("DVD", 57);  // divide register by register
        opcodeMap.put("TRR", 58);  // test equality of register and register
        opcodeMap.put("AND", 59);  // logical and register with register
        opcodeMap.put("ORR", 60);  // logical or register with register
        opcodeMap.put("NOT", 61);  // logical not of register
    }
//end opcode map

    // Helper to safely get operand
    String getOperand(String[] arr, int idx) {
        if (idx >= arr.length) return null;
        return arr[idx];
    }

    // Helper to resolve label or number
    java.util.function.Function<String, Integer> resolve = (operand) -> {
        if (operand == null) return 0;
        if (dictionary.containsKey(operand)) {
            return dictionary.get(operand);
        }
        return Integer.parseInt(operand);
    };

    // Helper to check if address needs splitting (> 31 requires 5+ bits)
    boolean needsSplitting(int address) {
        return address > 31;
    }

    // Helper to calculate base address (upper bits)
    int getBaseAddress(int address) {
        return address & 0xFFE0; // mask to keep upper bits, zero out lower 5 bits
    }

    // Helper to calculate offset (lower 5 bits)
    int getOffset(int address) {
        return address & 0x1F; // keep only lower 5 bits
    }

    // Helper to emit LDX instruction to load base address into index register
    void emitLDXInstruction(int codeLocation, int indexReg, int baseAddress,
                            PrintWriter listingFile, PrintWriter loadFile, String originalLine) {
        Integer ldxOpcode = opcodeMap.get("LDX");
        int instruction = (ldxOpcode << 10) | (indexReg << 6) | (baseAddress & 0x3FF);
        listingFile.printf("%06o  %06o  ; LDX %d,%d (base addr for: %s)%n", 
                        codeLocation, instruction, indexReg, baseAddress, originalLine.trim());
        loadFile.printf("%06o  %06o%n", codeLocation, instruction);
    }
    

//start pass 2
    public boolean pass2(File sourceFile) {
        int codeLocation = 0;

        try (Scanner myReader = new Scanner(sourceFile);
            PrintWriter listingFile = new PrintWriter("test_listing_p2_fixed.txt");
            PrintWriter loadFile = new PrintWriter("test_load_p2_fixed.txt")) {

            while (myReader.hasNextLine()) {
                String originalLine = myReader.nextLine();
                String line = originalLine;

                // Strip comments
                int commentPos = line.indexOf(';');
                String comment = "";
                if (commentPos != -1) {
                    comment = line.substring(commentPos);
                    line = line.substring(0, commentPos);
                }
                if (line.trim().isEmpty()) {
                    listingFile.printf("                %s%n", originalLine);
                    continue;
                }

                String[] splitData = line.trim().split("[\\s,]+");
                int index = 0;

                // Skip label if present
                if (splitData[index].endsWith(":")) {
                    index++;
                    if (index >= splitData.length) {
                        listingFile.printf("                %s%n", originalLine);
                        continue;
                    }
                }

                String opcodeStr = splitData[index].toUpperCase();
                index++;

                // LOC directive
                if (opcodeStr.equals("LOC")) {
                    if (index < splitData.length) {
                        codeLocation = Integer.parseInt(splitData[index]);
                        listingFile.printf("                %s%n", originalLine);
                    }
                    continue;
                }

                // DATA directive
                if (opcodeStr.equals("DATA")) {
                    int dataValue = 0;
                    if (index < splitData.length) {
                        String operand = splitData[index];
                        if (dictionary.containsKey(operand)) dataValue = dictionary.get(operand);
                        else dataValue = Integer.parseInt(operand);
                    }
                    listingFile.printf("%06o  %06o  %s%n", codeLocation, (dataValue & 0xFFFF), originalLine);
                    loadFile.printf("%06o  %06o%n", codeLocation, (dataValue & 0xFFFF));
                    codeLocation++;
                    continue;
                }

                // Lookup opcode
                Integer opcode = opcodeMap.get(opcodeStr);
                if (opcode == null) {
                    System.out.println("unknown instruction: " + opcodeStr);
                    return false;
                }

                // Default instruction fields
                int r = 0, ix = 0, i = 0, address = 0;
                int ry = 0, al = 0, lr = 0;

                // Parse operands
                String op1 = getOperand(splitData, index);
                String op2 = getOperand(splitData, index + 1);
                String op3 = getOperand(splitData, index + 2);
                String op4 = getOperand(splitData, index + 3);

                switch (opcodeStr) {
                    case "HLT":
                        break;
                    case "IN": case "OUT": case "CHK":
                        r = resolve.apply(op1);
                        address = resolve.apply(op2);
                        break;
                    case "AIR": case "SIR":
                        r = resolve.apply(op1);
                        address = resolve.apply(op2);
                        break;
                    case "MLT": case "DVD": case "TRR": case "AND": case "ORR":
                        r = resolve.apply(op1);
                        ry = resolve.apply(op2);
                        break;
                    case "SRC": case "RRC":
                        r  = resolve.apply(op1);
                        al = resolve.apply(op2);
                        lr = resolve.apply(op3);
                        address = resolve.apply(op4);
                        break;
                    case "JSR":
                        r = resolve.apply(op1); // typically 3
                        address = resolve.apply(op2);
                        break;
                    case "LDX": case "STX":
                        ix = resolve.apply(op1);
                        address = resolve.apply(op2);
                        break;
                    default: // memory reference
                        r  = resolve.apply(op1);
                        ix = resolve.apply(op2);
                        address = resolve.apply(op3);
                        if (op4 != null) {
                            i = Integer.parseInt(op4);
                            if (i != 0 && i != 1) {
                                System.out.println("invalid indirect bit");
                                return false;
                            }
                        }
                        break;
                }

                // Handle base+offset
                if (needsSplitting(address)) {
                    int baseAddress = getBaseAddress(address);
                    int offset = getOffset(address);

                    // Emit LDX 1, baseAddress
                    emitLDXInstruction(codeLocation, 1, baseAddress, listingFile, loadFile, originalLine);
                    codeLocation++;

                    ix = 1;
                    i = 0;
                    address = offset;
                }

                // Build machine instruction
                int instruction = 0;
                switch (opcodeStr) {
                    case "HLT":
                        instruction = opcode;
                        break;
                    case "MLT": case "DVD": case "TRR": case "AND": case "ORR":
                        instruction = (opcode << 10) | (r << 8) | (ry << 6);
                        break;
                    case "SRC": case "RRC":
                        instruction = (opcode << 10) | (r << 8) | (al << 7) | (lr << 6) | (address & 0x3F);
                        break;
                    case "IN": case "OUT": case "CHK":
                        instruction = (opcode << 10) | (r << 8) | (address & 0xFF);
                        break;
                    case "TRAP":
                        instruction = (opcode << 10) | (address & 0xF);
                        break;
                    case "AIR": case "SIR":
                        instruction = (opcode << 10) | (r << 8) | (address & 0x1F);
                        break;
                    case "LDX": case "STX":
                        instruction = (opcode << 10) | (ix << 6) | (i << 5) | (address & 0x3F);
                        break;
                    default:
                        instruction = (opcode << 10) | (r << 8) | (ix << 6) | (i << 5) | (address & 0x1F);
                        break;
                }

                // Emit instruction
                listingFile.printf("%06o  %06o  %s%n", codeLocation, instruction, originalLine);
                loadFile.printf("%06o  %06o%n", codeLocation, instruction);
                codeLocation++;
            }

        } catch (Exception e) {
            System.out.println("Error in pass2: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
//end pass 2

    public static void main(String[] args){
        File sourceFile = new File("test_source_p1.txt"); //hard coding which source file to read
        Assembler a = new Assembler();
        a.run(sourceFile);
    }
//end main


}//end public class assembler
