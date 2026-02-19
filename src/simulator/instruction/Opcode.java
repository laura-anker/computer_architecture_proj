package simulator.instruction;

//same opcode map as the assembler
public class Opcode {
    public static final int HLT = 0;
    public static final int LDR = 1;
    public static final int STR = 2;
    public static final int LDA = 3;
    public static final int AMR  = 4;
    public static final int SMR  = 5;
    public static final int AIR  = 6;
    public static final int SIR  = 7;
    public static final int JZ   = 8;
    public static final int JNE  = 9;
    public static final int JCC  = 10;
    public static final int JMA  = 11;
    public static final int JSR  = 12;
    public static final int RFS  = 13;
    public static final int SOB  = 14;
    public static final int JGE  = 15;

    public static final int TRAP = 24;
    public static final int SRC  = 25;
    public static final int RRC  = 26;

    public static final int LDX  = 33;
    public static final int STX  = 34;

    public static final int IN   = 49;
    public static final int OUT  = 50;
    public static final int CHK  = 51;

    public static final int MLT  = 56;
    public static final int DVD  = 57;
    public static final int TRR  = 58;
    public static final int AND  = 59;
    public static final int ORR  = 60;
    public static final int NOT  = 61;

    //need to put in all other values (done 2/19)
}