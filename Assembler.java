import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Assembler {
    public void run(File sourceFile){
        if (pass1(sourceFile) == false){
            //error!
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
        //Read a line of the file
        try (Scanner myReader = new Scanner(sourceFile)) { 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                //Use the split command to break the line into its parts
                //Process the line, if it is a label, add the label to a dictionary with the code location. Process
                //the rest of the line (it could be blank, if so no code is generated). Check for errors in the
                //code.
                //If code or data was not generated, end loop. Otherwise increment code location
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
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
