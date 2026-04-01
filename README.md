# computer_architecture_proj

Important note for Part 1 - You must compile and run from the src file using commands "javac simulator/Main.java" and "java simulator/Main". Sometimes you might need to recompile an individual file and you should also do this from src like "javac simulator/io/ProgramLoader.java" or it will get confused and won't let you compile.

Adding to this note for details - When we complie, we must be in the directory right above our top level package. The package in this case is our simulator that is within the src folder. Java is building class files based on current directory, so if we are not in the src file, the class files are created in the wrong location, and then at runtime they are unable to be found. For this same reason the individual files have to be compiled in the same way. 

# Part 3, due 4/18:

To create
- fix program 1
- implement program 2
- machine faults
- TRAP instruction
- CHK

Program 1:
A program that reads 20 numbers (integers) from the keyboard, prints the numbers to the console printer, requests a number from the user, and searches the 20 numbers read in for the number closest to the number entered by the user. Print the number entered by the user and the number closest to that number. Your numbers should not be 1…10, but distributed over the range of -32768 … 32767. Therefore, as you read a character in, you need to check it is a digit, convert it to a number, and assemble the integer. You should be able to handle negative numbers. 

Program 2:
A program that reads a set of a paragraph of 6 sentences from a file into memory. It prints the sentences on the console printer. It then asks the user for a word. It searches the paragraph to see if it contains the word. If so, it prints out the word, the sentence number, and the word number in the sentence.
  
To submit
- Your simulator, packaged as a JAR file, running program 2
- Load instructions from a file.
- Simple documentation describing how to use your simulator, what the console layout is and how to operate it. Include source code for program 2.
- File containing program 2 as machine code.
- Your team’s design notes
- Source code – well documented.
- Test Data
- GitHub submit logs
- Demonstration that Program 2 works.
