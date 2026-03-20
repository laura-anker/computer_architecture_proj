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

    // helper to safely get operand
    String getOperand(String[] arr, int idx) {
        if (idx >= arr.length) return null;
        return arr[idx];
    }

    // helper to resolve label or number
    java.util.function.Function<String, Integer> resolve = (operand) -> {
        if (operand == null) return 0;
        if (dictionary.containsKey(operand)) {
            return dictionary.get(operand);
        }
        return Integer.parseInt(operand);
    };

//start pass 2
    public boolean pass2(File sourceFile){
        //Set code location to 0
        int codeLocation = 0; // set code location to 0 at start

        //Read a line of the file
        try (Scanner myreader = new Scanner(sourceFile);//this try automatically closes these when done even if error happens
            PrintWriter listingFile = new PrintWriter("test_listing_part2_2.txt");
            PrintWriter loadFile = new PrintWriter("test_load_part2_2.txt")) {
            // read the file line by line
            while (myreader.hasNextLine()) {
                String originalLine = myreader.nextLine();
                String line = originalLine;

                // strip comments for parsing
                int commentPos = line.indexOf(';');
                if (commentPos != -1) {
                    line = line.substring(0, commentPos);
                }
                //skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }


                //Use the split command to break the line into it parts
                String[] splitData = line.trim().split("[\\s,]+");
                int index = 0;

                // handle label field if present
                if (splitData[index].endsWith(":")) {
                    index++; // skip label
                    if (index >= splitData.length) {
                    continue; // line only had label
                    }
                }

                

                String opcodeStr = splitData[index].toUpperCase(); // get opcode
                index++;

                //handle loc
                if (opcodeStr.equals("LOC")) {
                    if (index < splitData.length) {
                        codeLocation = Integer.parseInt(splitData[index]);
                        listingFile.printf("                %s%n", originalLine);//print line listing file only
                    
                    }//end if
                    continue; // loc does not generate code
                }//end if
                //handle data
                else if (opcodeStr.equals("DATA")) {
                    // data n -> generate a word with n
                    int dataValue = 0;
                    if (index < splitData.length) {
                        String operand = splitData[index];
                        // check if operand is a label
                        if (dictionary.containsKey(operand)) {
                            dataValue = dictionary.get(operand); // replace with label address
                        }//end if 
                        else {
                            dataValue = Integer.parseInt(operand); // decimal value
                        }//end else
                    }//end if

                    // write to listing file in octal (mask to 16 bits to handle negative/data values)
                    listingFile.printf("%06o  %06o  %s%n", codeLocation, (dataValue & 0xFFFF), originalLine);
                    // write to load file in octal (mask to 16 bits)
                    loadFile.printf("%06o  %06o%n", codeLocation, (dataValue & 0xFFFF));
                    codeLocation++; // increment location
                    continue;
                }

                // look up opcode in the map
                Integer opcode = opcodeMap.get(opcodeStr);
                if (opcode == null) {
                    System.out.println("unknown instruction: " + opcodeStr);
                    listingFile.close();//not sure if closes are necessary?
                    loadFile.close();
                    return false; // stop if invalid opcode
                }//end if

                // findfirst real instruction address
                //if (detectedStartAddress == -1) {
                //    detectedStartAddress = codeLocation;
                //}

                // default fields
                int r = 0, ix = 0, i = 0, address = 0;
                int ry = 0;     // for register-to-register
                int al = 0;     // for shift/rotate
                int lr = 0;

                int count = 0;  // for shift/rotate
                int deviceId = 0; // for IO

                // -------- INSTRUCTION TYPES --------

                // 1. ZERO OPERAND
                if (opcodeStr.equals("HLT")) {
                    // nothing to do
                }

                // 2. I/O: IN, OUT, CHK → r, device
                else if (opcodeStr.equals("IN") || opcodeStr.equals("OUT") || opcodeStr.equals("CHK")) {
                    r = resolve.apply(getOperand(splitData, index));
                    address = resolve.apply(getOperand(splitData, index + 1));
                }

                // 3. IMMEDIATE: AIR, SIR → r, immediate
                else if (opcodeStr.equals("AIR") || opcodeStr.equals("SIR")) {
                    r = resolve.apply(getOperand(splitData, index));
                    address = resolve.apply(getOperand(splitData, index + 1));
                }

                // 4. REGISTER-TO-REGISTER
                else if (opcodeStr.equals("MLT") || opcodeStr.equals("DVD") ||
                        opcodeStr.equals("TRR") || opcodeStr.equals("AND") ||
                        opcodeStr.equals("ORR")) {

                    r = resolve.apply(getOperand(splitData, index));
                    ry = resolve.apply(getOperand(splitData, index + 1));
                }
                // 5. SHIFT / ROTATE: SRC, RRC → r, al, lr, count
                else if (opcodeStr.equals("SRC") || opcodeStr.equals("RRC")) {

                    r     = resolve.apply(getOperand(splitData, index));
                    count = resolve.apply(getOperand(splitData, index + 1));
                    lr    = resolve.apply(getOperand(splitData, index + 2));
                    al    = resolve.apply(getOperand(splitData, index + 3));
                }

                // 6. LDX/STX → ix, address
                else if (opcodeStr.equals("LDX") || opcodeStr.equals("STX")) {

                    ix = resolve.apply(getOperand(splitData, index));
                    address = resolve.apply(getOperand(splitData, index + 1));
                }

                // 7. DEFAULT: MEMORY REFERENCE → r, ix, address[, i]
                else {

                    r  = resolve.apply(getOperand(splitData, index));
                    ix = resolve.apply(getOperand(splitData, index + 1));
                    address = resolve.apply(getOperand(splitData, index + 2));

                    String iField = getOperand(splitData, index + 3);
                    if (iField != null) {
                        i = Integer.parseInt(iField);
                        if (i != 0 && i != 1) {
                            System.out.println("invalid indirect bit");
                            return false;
                        }
                    }
                }
                

                //build our 16-bit instruction
                //int instruction = (opcode << 10) | (r << 8) | (ix << 6) | (i << 5) | (address & 0x1F);
                int instruction = 0;

                if (opcodeStr.equals("MLT") || opcodeStr.equals("DVD") ||
                    opcodeStr.equals("TRR") || opcodeStr.equals("AND") ||
                    opcodeStr.equals("ORR")) {

                    instruction = (opcode << 10) | (r << 8) | (ry << 6);

                }
                else if (opcodeStr.equals("SRC") || opcodeStr.equals("RRC")) {

                    instruction = (opcode << 10) | (r << 8) | (al << 7) | (lr << 6) | (count & 0xF);

                }
                else if (opcodeStr.equals("IN") || opcodeStr.equals("OUT") || opcodeStr.equals("CHK")) {

                    instruction = (opcode << 10) | (r << 8) | (address & 0xFF);

                }
                else if (opcodeStr.equals("TRAP")) {
                    instruction = (opcode << 10) | (address & 0xF);
                }
                else if (opcodeStr.equals("AIR") || opcodeStr.equals("SIR")) {
                    instruction = (opcode << 10) | (r   << 8) | (address & 0x1F);
                }
                else if (opcodeStr.equals("LDX") || opcodeStr.equals("STX")) {
                    instruction = (opcode << 10) | (ix  << 6) | (i   << 5) | (address & 0x3FF);
                }
                else {
                    instruction = (opcode << 10) | (r << 8) | (ix << 6) | (i << 5) | (address & 0x3FF);
                }

                // write listing file in octal
                listingFile.printf("%06o  %06o  %s%n", codeLocation, instruction, originalLine);
                // write load file in octal
                loadFile.printf("%06o  %06o%n", codeLocation, instruction);
                codeLocation++; // increment location
                
            }//end while
            // START directive at end of load file
            //loadFile.printf("START %06o%n", detectedStartAddress);
        } //end try
    

        catch (FileNotFoundException e) { //error handling in reading file
            System.out.println("an error occurred reading the file");
            e.printStackTrace();
            return false;

        } //end catch 

        catch (NumberFormatException e) { //error handling in source
            System.out.println("invalid number in source file");
            e.printStackTrace(); 
            return false;
        }//end catch 
        
        return true;
    }
//end pass 2

    public static void main(String[] args){
        File sourceFile = new File("test_source_part2_2.txt"); //hard coding which source file to read
        Assembler a = new Assembler();
        a.run(sourceFile);
    }
//end main


}//end public class assembler
