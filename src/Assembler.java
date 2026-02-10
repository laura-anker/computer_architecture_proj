import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Assembler {

    Map<String, Integer> dictionary = new HashMap<>();

    public void run(File sourceFile){
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

    //this is the part i was working on, kept the skeleton but updated some work with LOC and DATA
    //wanted to keep the OG for reference        
    /* 
        try (Scanner myReader = new Scanner(sourceFile)) { 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                
                if (data.isEmpty() || data.startsWith(";")){//if line is empty or comment, move to next line
                    continue;
                }

                //Use the split command to break the line into up to 4 parts based on one or more spaces
                String[] splitData = data.trim().split("\\s+");

                int val = 0;
                if (splitData[0].endsWith(":")){//indicates label, used to define location
                    String label = splitData[0].substring(0, splitData[0].length() - 1);//get label without :
                    if (dictionary.containsKey(label)){//error if label is duplicated
                        System.out.println("Duplicate label: "+label);
                        return false;
                    }
                    dictionary.put(label, codeLocation);//add labal, location pair to dictionary
                    val = 1;
                }

                //check for code or data generated after label (don't increment if there's not)
                if (val < splitData.length){
                    codeLocation+=1; //increment location if code is generated
                }
                
            }*/
    //end of reference TRY

        try (Scanner myReader = new Scanner(sourceFile)) { 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
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


//start pass 2
    public boolean pass2(File sourceFile){
        //Set code location to 0
        int codeLocation = 0; // set code location to 0 at start

        //Read a line of the file
        try (Scanner myreader = new Scanner(sourceFile);//this try automatically closes these when done even if error happens
            PrintWriter listingFile = new PrintWriter("listing.txt");
            PrintWriter loadFile = new PrintWriter("load.txt")) {
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
                }

                if (index >= splitData.length) {
                    continue; // line only had label
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

                    // write to listing file in octal
                    listingFile.printf("%06o  %06o  %s%n", codeLocation, dataValue, originalLine);
                    // write to load file in octal
                    loadFile.printf("%06o  %06o%n", codeLocation, dataValue);
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

                // default fields
                int r = 0, ix = 0, i = 0, address = 0;
                // zero-operand instructions
                if (opcodeStr.equals("HLT")) {
                    // all fields stay 0
                }else if (opcodeStr.equals("LDX") || opcodeStr.equals("STX")) {
                    // LDX ix,address
                    ix = Integer.parseInt(splitData[index]);
                    address = Integer.parseInt(splitData[index + 1]);
                }
                else {
                    // r,ix,address[,i]
                    r = Integer.parseInt(splitData[index]);
                    ix = Integer.parseInt(splitData[index + 1]);
                    address = Integer.parseInt(splitData[index + 2]);

                    if (index + 3 < splitData.length) {
                        i = Integer.parseInt(splitData[index + 3]);
                        if (i != 0 && i != 1) {
                            System.out.println("invalid indirect bit");
                            return false;
                        }
                    }

                }
                

                //build our 16-bit instruction
                int instruction = (opcode << 10) | (r << 8) | (ix << 6) | (i << 5) | (address & 0x1F);
                //int instruction = (opcode << 11) | (r << 9) | (ix << 7) | (i << 6) | (address & 0x7F);

                // write listing file in octal
                listingFile.printf("%06o  %06o  %s%n", codeLocation, instruction, originalLine);
                // write load file in octal
                loadFile.printf("%06o  %06o%n", codeLocation, instruction);
                codeLocation++; // increment location
            }//end while

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
        File sourceFile = new File("source.txt");;
        Assembler a = new Assembler();
        a.run(sourceFile);
    }
//end main

}//end public class assembler

// added main inside the class
// had to fix some typos like data.data
//changed object creation
//added a return in pass two 