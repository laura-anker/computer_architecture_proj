import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;


public class Assembler {
    Map<String, Integer> dictionary = new HashMap<>();

    public void run(File sourceFile){
        if (pass1(sourceFile) == false){
            return;//error!
        }
        if (pass2(sourceFile) == false){
            //error!
        }
        return;
    }

    //build label/address map
    //return true if successful, false if error
    public boolean pass1(File sourceFile){
        //set code location to 0
        int codeLocation = 0;
        //Read a line of the file
        try (Scanner myReader = new Scanner(sourceFile)) { 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.isEmpty() || data.startsWith(";")){//if line is empty or comment, move to next line
                    continue;
                }
                //Use the split command to break the line into up to 4 parts based on one or more spaces
                String[] splitData = data.data.trim().split("\\s+");

                int val = 0;
                if (splitData[0].endsWith(":")){//indicates label, used to define location
                    String label = splitData[0].substring(0, splitData[0].length-1);//get label without :
                    if dictionary.contains(label){//error if label is duplicated
                        System.out.println("Duplicate label: "+label);
                        return false;
                    }
                    dictionary.put(label, codeLocation);//add labal, location pair to dictionary
                    val = 1;
                }

                //check for code or data generated after label (don't increment if there's not)
                if (val< splitData.length()){
                    codeLocation+=1; //increment location if code is generated
                }
                
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //use label/address map to generate machine code
    //return true if successful, false if error
    public boolean pass2(File sourceFile){
        //Set code location to 0
        //Read a line of the file
        //Use the split command to break the line into it parts
        //Convert the code according to the second field.
        //Add line to listing file and to load file.
        //If code or data generated, increment the code counter, and go to step2 until termination.
    }
}

public static void main(String[] args){
    File sourceFile = new File("source.txt");;
    Assembler a = Assembler();
    a.run(sourceFile);
}
