package simulator.cpu;

import simulator.instruction.Opcode;
import simulator.memory.Memory;
import simulator.registers.RegisterFile;
import simulator.instruction.Instruction;

//executes instructions
public class InstructionExecutor {

    private RegisterFile regs;
    private Memory memory;
    private EffectiveAddressCalculator eaCalc;
    private CPU cpu;

    public InstructionExecutor(RegisterFile regs, Memory memory, CPU cpu) {
        this.regs = regs;
        this.memory = memory;
        this.eaCalc = new EffectiveAddressCalculator();
        this.cpu = cpu;
    }

    //todo finish adding opcode instructions for each case
    public boolean execute(Instruction inst) {
    
        switch (inst.opcode) {//check opcode for instruction

            case Opcode.LDR:
                executeLDR(inst); //calls methos
                break;

            case Opcode.STR: // Store register to memory
                executeSTR(inst);
                break;

            case Opcode.LDA: // Load register with address
                executeLDA(inst);
                break;

            case Opcode.LDX: // Load index register from memory
                executeLDX(inst);
                break;

            case Opcode.STX: // Store index register to memory
                executeSTX(inst);
                break;

            case Opcode.HLT: // stops machine
                return false;

            //arithmetic/logical

            case Opcode.AMR://add memory to register
                executeAMR(inst);
                break;

            case Opcode.SMR://subtract memory from register
                executeSMR(inst);
                break;

            case Opcode.AIR://add immediate to register
                executeAIR(inst);
                break;

            case Opcode.SIR://subtract immediate from register
                executeSIR(inst);
                break;
            
            //transfer instructions

            case Opcode.JZ: // jump if zero
                executeJZ(inst);
                break;

            case Opcode.JNE: // jump if not equal
                executeJNE(inst);
                break;

            case Opcode.JCC://jump if condition code
                executeJCC(inst);
                break;

            case Opcode.JMA: //unconditional jump to address
                executeJMA(inst);
                break;

            case Opcode.JSR://jump and save return address
                executeJSR(inst);
                break;

            case Opcode.RFS://return from subroutine w/ return code as immed portion?
                executeRFS(inst);
                break;

            case Opcode.SOB://subtract 1 and branch
                executeSOB(inst);
                break;

            case Opcode.JGE://jump greater than or equal to
                executeJGE(inst);
                break;

            //multiply and divide logical operations

            case Opcode.MLT://multiple register vy register
                executeMLT(inst);
                break;

            case Opcode.DVD://divide register by register
                executeDVD(inst);
                break;

            case Opcode.TRR://test the equality of 2 registers
                executeTRR(inst);
                break;

            case Opcode.AND://logical and
                executeAND(inst);
                break;

            case Opcode.ORR://logical or
                executeORR(inst);
                break;

            case Opcode.NOT://logical not
                executeNOT(inst);
                break;

            //shift and rotate

            case Opcode.SRC:
                executeSRC(inst);//shift register by count
                break;

            case Opcode.RRC://rotate register by count
                executeRRC(inst);
                break;

            // I/O

            case Opcode.IN:
                int ch = cpu.getIODevice().readChar();
                regs.GPR[inst.r].set(ch);
                break;

            case Opcode.OUT://output character to device from register
                int out = regs.GPR[inst.r].get();  
                cpu.getIODevice().writeChar(out);
                break;

            case Opcode.CHK://check device status to register
                executeCHK(inst);
                break;

            case Opcode.TRAP://trap
                executeTRAP(inst);
                break;

            //helpful for debugging
            default:
                System.out.println("Unknown instruction: opcode=" + inst.opcode);
                return false;
        }

        return true;
    }

    //to do, make each instrction from the cases

    //load gpr from mem
    private void executeLDR(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory); //take address, add index reg if any, checks indirect mem
        int value = memory.read(ea); //reads word at effective address from mem

        regs.getGPR(inst.r).set(value); //write val into general purpose register
    }

    //store gpr to mem
    private void executeSTR(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        int value = regs.getGPR(inst.r).get();
        memory.write(ea, (short)value);
    }

    //load GPR w/ the address itself, not memory content
    private void executeLDA(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        regs.getGPR(inst.r).set(ea);
    }

    ///oad index register from mem
    private void executeLDX(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        int value = memory.read(ea);
        regs.getIX(inst.ix).set(value);
    }

    //store index register to mem
    private void executeSTX(Instruction inst) {
        //int value = regs.getIX(inst.r).get();
        //memory.write(inst.address, (short)value);
        int ea = eaCalc.computeEA(inst, regs, memory);
        int value = regs.getIX(inst.ix).get(); //change from .r to .ix
        memory.write(ea, (short)value);
    }

    //ARITHMETIC INSTRUCTIONS

    //add memory to register
    private void executeAMR(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        int memVal = memory.read(ea);

        int result = regs.getGPR(inst.r).get() + memVal;

        regs.getGPR(inst.r).set(result & 0xFFFF);
    }

    //subtract memory from register
    private void executeSMR(Instruction inst) {
        int ea = eaCalc.computeEA(inst, regs, memory);
        int memVal = memory.read(ea);

        int result = regs.getGPR(inst.r).get() - memVal;

        regs.getGPR(inst.r).set(result & 0xFFFF);
    }

    //add immediate to register
    private void executeAIR(Instruction inst) {
        int result = regs.getGPR(inst.r).get() + inst.address;
        regs.getGPR(inst.r).set(result & 0xFFFF);
    }

    //subtract immediate from register
    private void executeSIR(Instruction inst) {
        int result = regs.getGPR(inst.r).get() - inst.address;
        regs.getGPR(inst.r).set(result & 0xFFFF);
    }

    //BRANCH INSTRUCTIONS
    //question!!  - else increment PC? or is this done elsewhere??

    //jump if zero
    private void executeJZ(Instruction inst) {

        if (regs.getGPR(inst.r).get() == 0) {

            int ea = eaCalc.computeEA(inst, regs, memory);
            regs.getPC().set(ea);

        }
    }

    //jump if not equal
    private void executeJNE(Instruction inst) {

        if (regs.getGPR(inst.r).get() != 0) {

            int ea = eaCalc.computeEA(inst, regs, memory);
            regs.getPC().set(ea);

        }
    }

    //unconditional jump to address
    private void executeJMA(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory);

        regs.getPC().set(ea);

    }

    //subtract one and branch
    private void executeSOB(Instruction inst) {

        int value = regs.getGPR(inst.r).get() - 1;

        regs.getGPR(inst.r).set(value);

        if (value > 0) {

            int ea = eaCalc.computeEA(inst, regs, memory);

            regs.getPC().set(ea);
        }
    }

    //need fix, no CC register
    private void executeJCC(Instruction inst) {

        if (regs.getCCBit(inst.r) == 1) {

            int ea = eaCalc.computeEA(inst, regs, memory);

            regs.getPC().set(ea);
        }
    }

    //jump and save return address
    /* 
    private void executeJSR(Instruction inst) {

        int ea = eaCalc.computeEA(inst, regs, memory);

        // save return address
        regs.getGPR(3).set(regs.getPC().get() +1);

        // jump to subroutine
        regs.getPC().set(ea);
    }
*/
    private void executeJSR(Instruction inst) {
    int target = inst.address;                 // absolute 10-bit address
    regs.getGPR(3).set(regs.getPC().get() + 1);
    regs.getPC().set(target);
    }

    //return from subroutine with return code as immed portion (optional) stored in instructions's addr field
    private void executeRFS(Instruction inst) {

        // immediate return value
        regs.getGPR(0).set(inst.address);

        // restore PC from R3
        regs.getPC().set(regs.getGPR(3).get());
    }

    //jump greater than or equal to
    private void executeJGE(Instruction inst) {

        int value = regs.getGPR(inst.r).get();

        if (value >= 0) {

            int ea = eaCalc.computeEA(inst, regs, memory);

            regs.getPC().set(ea);
        }
    }

    // REGISTER OPERATIONS

    //logical AND of register and register
    private void executeAND(Instruction inst) {

        int r = regs.getGPR(inst.r).get();
        int x = regs.getGPR(inst.ry).get();

        regs.getGPR(inst.r).set(r & x);
    }

    //logical OR of register and register
    private void executeORR(Instruction inst) {

        int r = regs.getGPR(inst.r).get();
        int x = regs.getGPR(inst.ry).get();

        regs.getGPR(inst.r).set(r | x);
    }

    //logical NOT of register and register
    private void executeNOT(Instruction inst) {

        int value = regs.getGPR(inst.r).get();

        regs.getGPR(inst.r).set(~value & 0xFFFF);
    }

    //test equality of register and register
    private void executeTRR(Instruction inst) {

        int r = regs.getGPR(inst.r).get();
        int x = regs.getGPR(inst.ry).get();
        if (r == x){
            regs.setCCBit(3, 1);
        }else{
            regs.setCCBit(3, 0);
        }
    }

    // MULTIPLY AND DIVIDE

    //multiply register by register
    private void executeMLT(Instruction inst) {
        if (inst.r != 0 && inst.r != 2) {
            System.out.println("MLT requires R0 or R2 as first operand");
            return;
        }

        int rx = regs.getGPR(inst.r).get();
        int ry = regs.getGPR(inst.ry).get();

        long result = (long) rx * ry;

        int high = (int)(result >> 16);
        int low = (int)(result & 0xFFFF);

        regs.getGPR(inst.r).set(high);
        regs.getGPR(inst.r + 1).set(low);

        //set overflow flag is overflow?? look into this instruction
        if (result > 0xFFFFFFFFL) {
            regs.setCCBit(0, 1); // overflow
        }
    }

    //divide register by register
    private void executeDVD(Instruction inst) {
        if (inst.r != 0 && inst.r != 2) {
            System.out.println("DVD requires R0 or R2 as first operand");
            return;
        }

        int rx = regs.getGPR(inst.r).get();
        int ry = regs.getGPR(inst.ry).get();

        if (ry == 0) {
            //set divide zero flag
            regs.setCCBit(2, 1);
            return;
        }

        int quotient = rx / ry;
        int remainder = rx % ry;

        regs.getGPR(inst.r).set(quotient);
        regs.getGPR(inst.r + 1).set(remainder);  
    }

    // SHIFT AND ROTATE

    //shift register by count
   private void executeSRC(Instruction inst) {

        int value = regs.getGPR(inst.r).get();
        int count = inst.count;

        if (inst.lr == 1) { // LEFT

            value = value << count;

        } else { // RIGHT

            if (inst.al == 1)
                value = value >>> count; // logical
            else
                value = value >> count;  // arithmetic
        }

        regs.getGPR(inst.r).set(value & 0xFFFF);
    }

    //rotate register by count
    private void executeRRC(Instruction inst) {

        int value = regs.getGPR(inst.r).get() & 0xFFFF;
        int count = inst.count % 16;   // rotating more than 16 repeats

        for (int i = 0; i < count; i++) {

            if (inst.lr == 1) {  // ROTATE LEFT

                if (inst.al == 1) {  // logical rotate
                    int msb = (value >> 15) & 1;
                    value = ((value << 1) & 0xFFFF) | msb;
                } 
                else {  // arithmetic rotate (preserve sign bit)
                    int sign = (value >> 15) & 1;
                    int next = (value >> 14) & 1;

                    value = ((value << 1) & 0x7FFF) | next;
                    value |= (sign << 15);
                }

            } else {  // ROTATE RIGHT

                if (inst.al == 1) {  // logical rotate
                    int lsb = value & 1;
                    value = (value >> 1) | (lsb << 15);
                } 
                else {  // arithmetic rotate (preserve sign bit)
                    int sign = (value >> 15) & 1;
                    int lsb = value & 1;

                    value = (value >> 1) | (lsb << 14);
                    value &= 0x7FFF;
                    value |= (sign << 15);
                }
            }
        }

        regs.getGPR(inst.r).set(value & 0xFFFF);
    }

    // I/O Instructions

    //input character to register from device
    //private void executeIN(Instruction inst) {

        //System.out.print("Input number: ");

        //java.util.Scanner sc = new java.util.Scanner(System.in);

        //int value = sc.nextInt();

        //regs.getGPR(inst.r).set(value);
    //}

    //output character to device from register
    ///private void executeOUT(Instruction inst) {
        ///int value = regs.getGPR(inst.r).get();
        //System.out.println("OUTPUT: " + value);
   // }

    //check device status to register
    private void executeCHK(Instruction inst) {
        int status = cpu.getIODevice().getStatus();
        regs.getGPR(inst.r).set(status);
    }

    //traps to memory address 0
    private void executeTRAP(Instruction inst) {
        //table has max 16 entries representing 16 routines
        //trap code contains index to table (0-15)
        //goes to routine whose address is stored in memory location 0
        //executes those instructions then returns to instruction stored in memory location 2
        memory.write(2, (short)(regs.getPC().get() + 1));// store PC+1 in memory location 2
        int trapCode = inst.address & 0xF; // get lower 4 bits for trap code (0-15)
        int trapTableBase = memory.read(0); // read base address of trap table from memory location 0
        int routineAddr = memory.read(trapTableBase + trapCode); // get routine address
        
        regs.getPC().set(routineAddr);
    }

}
