# computer_architecture_proj

Important note for Part 1 - You must compile and run from the src file using commands "javac simulator/Main.java" and "java simulator/Main". Sometimes you might need to recompile an individual file and you should also do this from src like "javac simulator/io/ProgramLoader.java" or it will get confused and won't let you compile.

Adding to this note for details - When we complie, we must be in the directory right above our top level package. The package in this case is our simulator that is within the src folder. Java is building class files based on current directory, so if we are not in the src file, the class files are created in the wrong location, and then at runtime they are unable to be found. For this same reason the individual files have to be compiled in the same way. 

# Part 2, due 3/21:

To create
- Implement memory and cache design
- Design and implement the modules for enhanced memory and cache operations
- Implement all instructions except: CHK, floating point/vector operations, Trap. CHK = 63, Float = 33,34,35,36,37,50,51, Trap = 30
- Extend user interface
- Demonstrate 1st program running on your simulator.

Program 1:
A program that reads 20 numbers (integers) from the keyboard, prints the numbers to the console printer, requests a number from the user, and searches the 20 numbers read in for the number closest to the number entered by the user. Print the number entered by the user and the number closest to that number. Your numbers should not be 1…10, but distributed over the range of -32768 … 32767. Therefore, as you read a character in, you need to check it is a digit, convert it to a number, and assemble the integer. You should be able to handle negative numbers. 
  
To submit
- Cache Design and Implementation
- Have all instructions working except Part IV
- Have your cache design at least coded out if not working

- Demonstrate that individual instructions work.
- Your user interface, e.g., operator’s console should be used to test instructions, etc.
- Include source code for Program 1.
- Your simulator, packaged as a JAR file, running program 1.
- File containing program 1 as machine code.
- Demonstration that Program 1 works.
- Test Case
- GitHub submit logs
- Simple documentation describing how to use your simulator, what the console layout is and how to operate it.
- Your team’s design notes
- Source code – well documented
