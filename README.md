# computer_architecture_proj

Important note for working on Part 1 - You must compile and run from the src file using commands "javac simulator/Main.java" and "java simulator/Main". Sometimes you might need to recompile an individual file and you should also do this from src like "javac simulator/io/ProgramLoader.java" or it will get confused and won't let you compile.

Adding to this note for details - When we complie, we must be in the directory right above our top level package. The package in this case is our simulator that is within the src folder. Java is building class files based on current directory, so if we are not in the src file, the class files are created in the wrong location, and then at runtime they are unable to be found. For this same reason the individual files have to be compiled in the same way. 

# Part 1, due 2/28:

To create
- Design and implement the basic machine architecture.
- Implement a simple memory
- Execute Load and Store instructions
- Build initial user interface to simulator
  
To submit
- Your simulator, packaged as a JAR file.
- Simple documentation describing how to use your simulator, what the console layout is and how to operate it.
- Test Cases
  - You should be able to enter data into any of R0 – R3; enter data into memory via switches; enter the various Load and Store instructions instructions into memory; enter address into PC and press Single Step switch to execute the instruction at that address.
  - For this deliverable, when the IPL button is pushed the machine should pre load a program that shows that the machine works and stop at the beginning, ready for the user to hit run or single step. This program will be short but show all addressing modes for the load and store instructions, allowing single step through. The program. Memory contents at the address given in the MAR should be on the user console.
- Your team’s design notes
- Source code – well documented.
- GitHub submit logs
