; Program 1
; Reads 20 space-separated signed integers from the keyboard, echoes them,
; reads one target integer, and prints the closest stored value.
; Invalid input prints ERR and halts.

; Low-memory constants used by direct-address instructions.
	LOC 6
ZERO:   Data 0               ; 6
ONE:    Data 1               ; 7
TEN:    Data 10              ; 8
ASC0:   Data 48              ; 9  '0'
ASC9:   Data 57              ; 10 '9'
ASCMIN: Data 45              ; 11 '-'
ASCSP:  Data 32              ; 12 ' '
ASCNL:  Data 10              ; 13 '\n'
ASCT:   Data 84              ; 14 'T'
ASCC:   Data 67              ; 15 'C'
ASCE:   Data 69              ; 16 'E'
ASCR:   Data 82              ; 17 'R'
NUMBAS: Data 40              ; 18 number array base
SCRBAS: Data 70              ; 19 scratch base
JTBAS:  Data 500             ; 20 jump-table base
DIGBAS: Data 86              ; 21 digit buffer base
MSG20P: Data 550             ; 22 "ENTER 20 NUMBERS: "
MSGTP:  Data 569             ; 23 "ENTER TARGET: "
MSGTLP: Data 584             ; 24 "TARGET: "
MSGCLP: Data 593             ; 25 "CLOSEST: "
MSGERP: Data 603             ; 26 "ERROR" + newline

; Numbers read from the keyboard live here.
	LOC 40
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0

; Scratch region, addressed relative to X2=70.
; +0  input value
; +1  sign flag
; +2  digit seen flag
; +3  temp char
; +4  current array pointer
; +5  search pointer
; +6  target value
; +7  best value
; +8  best diff
; +9  candidate value
; +10 candidate diff
; +11 print value
; +12 print count
; +13 temp
; +14 print pointer
; +15 loop count
; +16..+20 digit buffer
	LOC 70
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0
	Data 0

	LOC 100
Start:  LDX 2,19,0           ; X2 <- 70, scratch base
	LDX 3,20,0           ; X3 <- 500, jump-table base

	LDR 0,0,22,0
	STR 0,2,11,0
	JSR 3,30,1

	LDR 0,0,18,0         ; current array pointer <- 40
	STR 0,2,4,0

	LDR 0,0,6,0          ; loop count <- 20 (load ZERO constant)
	AIR 0,20
	STR 0,2,15,0

Read20Loop:
	JSR 3,25,1           ; read next number into scratch +0

	LDR 0,2,0,0
	STR 0,2,4,1          ; store through current array pointer

	STR 0,2,11,0         ; print echoed number
	JSR 3,24,1
	LDR 0,0,12,0
	OUT 0,1

	LDR 0,2,4,0          ; advance current array pointer
	AIR 0,1
	STR 0,2,4,0

	LDR 0,2,15,0         ; decrement remaining input count
	SIR 0,1
	STR 0,2,15,0
	JNE 0,3,0,1

	LDR 0,0,13,0
	OUT 0,1

	LDR 0,0,23,0
	STR 0,2,11,0
	JSR 3,30,1

	JSR 3,25,1           ; read search target
	LDR 0,2,0,0
	STR 0,2,6,0

	LDR 0,0,18,0         ; search pointer <- 40
	STR 0,2,5,0

	LDR 0,2,5,1          ; best value <- first number
	STR 0,2,7,0
	STR 0,2,9,0
	JSR 3,21,1
	LDR 0,2,10,0
	STR 0,2,8,0

	LDR 0,2,5,0          ; advance search pointer
	AIR 0,1
	STR 0,2,5,0

	LDR 0,0,6,0          ; remaining search count <- 19 (load ZERO constant)
	AIR 0,19
	STR 0,2,15,0

SearchLoop:
	LDR 0,2,5,1          ; candidate <- *search pointer
	STR 0,2,9,0
	JSR 3,21,1

	LDR 0,2,10,0         ; if candidate diff >= best diff, skip update
	SMR 0,2,8,0
	JGE 0,3,9,1

	LDR 0,2,9,0
	STR 0,2,7,0
	LDR 0,2,10,0
	STR 0,2,8,0

SkipUpdate:
	LDR 0,2,5,0
	AIR 0,1
	STR 0,2,5,0

	LDR 0,2,15,0
	SIR 0,1
	STR 0,2,15,0
	JNE 0,3,1,1

	LDR 0,0,13,0
	OUT 0,1

	LDR 0,0,24,0
	STR 0,2,11,0
	JSR 3,30,1
	LDR 0,2,6,0
	STR 0,2,11,0
	JSR 3,24,1
	LDR 0,0,13,0
	OUT 0,1

	LDR 0,0,25,0
	STR 0,2,11,0
	JSR 3,30,1
	LDR 0,2,7,0
	STR 0,2,11,0
	JSR 3,24,1
	LDR 0,0,13,0
	OUT 0,1
	HLT

; Read signed decimal into scratch +0.
ReadNum:
	LDR 0,0,6,0          ; input value = 0
	STR 0,2,0,0
	STR 0,2,1,0          ; sign flag = 0
	STR 0,2,2,0          ; digit seen = 0

ReadWait:
	IN 0,0
	STR 0,2,3,0
	SMR 0,0,12,0         ; skip leading spaces
	JZ 0,3,2,1

	LDR 0,2,3,0          ; optional leading minus
	SMR 0,0,11,0
	JZ 0,3,16,1

	LDR 0,2,3,0          ; otherwise must be a digit
	SMR 0,0,9,0
	JGE 0,3,14,1
	JMA 3,8,1

ReadMinus:
	LDR 0,0,7,0
	STR 0,2,1,0
	JMA 3,3,1

ReadBody:
	CHK 1,0
	JZ 1,3,4,1           ; buffer empty ends token

	IN 0,0
	STR 0,2,3,0
	SMR 0,0,12,0         ; space ends token
	JZ 0,3,4,1

	LDR 0,2,3,0          ; '-' in body is invalid
	SMR 0,0,11,0
	JZ 0,3,8,1

	LDR 0,2,3,0
	SMR 0,0,9,0
	JGE 0,3,14,1
	JMA 3,8,1

ReadLowerOk:
	LDR 1,0,10,0         ; verify char <= '9'
	SMR 1,2,3,0
	JGE 1,3,5,1
	JMA 3,8,1

ReadDigit:
	LDR 0,2,0,0          ; value = value * 10 + digit
	LDR 2,0,8,0
	MLT 0,2
	STR 1,2,0,0

	LDR 0,2,3,0
	SMR 0,0,9,0
	AMR 0,2,0,0
	STR 0,2,0,0

	LDR 0,0,7,0          ; digit seen = 1
	STR 0,2,2,0
	JMA 3,3,1

ReadFinalize:
	LDR 0,2,2,0          ; token must contain at least one digit
	JNE 0,3,18,1
	JMA 3,8,1

ReadFinalOk:
	LDR 0,2,1,0          ; if sign flag set, negate value
	JNE 0,3,19,1
	RFS 0

ReadNegateReturn:
	LDR 0,2,0,0
	NOT 0
	AIR 0,1
	STR 0,2,0,0
	RFS 0

; Compute abs(candidate - target) into scratch +10.
AbsDiff:
	LDR 0,2,9,0
	SMR 0,2,6,0
	JGE 0,3,20,1
	NOT 0
	AIR 0,1

AbsStore:
	STR 0,2,10,0
	RFS 0

; Print signed decimal from scratch +11.
PrintNum:
	LDR 0,2,11,0
	JNE 0,3,22,1
	LDR 0,0,9,0
	OUT 0,1
	RFS 0

PrintNonZero:
	JGE 0,3,23,1
	LDR 0,0,11,0
	OUT 0,1
	LDR 0,2,11,0
	NOT 0
	AIR 0,1
	STR 0,2,11,0

PrintPositiveSetup:
	LDR 0,0,6,0
	STR 0,2,12,0
	LDR 0,0,21,0
	STR 0,2,14,0

PrintLoop:
	LDR 0,2,11,0
	LDR 2,0,8,0
	DVD 0,2
	STR 0,2,11,0
	STR 1,2,13,0

	LDR 0,0,9,0
	AMR 0,2,13,0
	STR 0,2,14,1

	LDR 0,2,14,0
	AIR 0,1
	STR 0,2,14,0

	LDR 0,2,12,0
	AIR 0,1
	STR 0,2,12,0

	LDR 0,2,11,0
	JNE 0,3,6,1

PrintOut:
	LDR 0,2,14,0
	SIR 0,1
	STR 0,2,14,0
	LDR 0,2,14,1
	OUT 0,1

	LDR 0,2,12,0
	SIR 0,1
	STR 0,2,12,0
	JNE 0,3,7,1
	RFS 0

; Print zero-terminated string whose address is in scratch +11.
PrintStr:
	LDR 0,2,11,0
	STR 0,2,14,0

PrintStrLoop:
	LDR 0,2,14,1
	JZ 0,3,31,1
	OUT 0,1
	LDR 0,2,14,0
	AIR 0,1
	STR 0,2,14,0
	JMA 3,29,1

PrintStrDone:
	RFS 0

ErrorHandler:
	LDR 0,0,26,0
	STR 0,2,11,0
	JSR 3,30,1
	HLT

; Jump-table entries, addressed relative to X3=500.
	LOC 500
JT0:    Data Read20Loop
JT1:    Data SearchLoop
JT2:    Data ReadWait
JT3:    Data ReadBody
JT4:    Data ReadFinalize
JT5:    Data ReadDigit
JT6:    Data PrintLoop
JT7:    Data PrintOut
JT8:    Data ErrorHandler
JT9:    Data SkipUpdate
JT10:   Data Start
JT11:   Data Start
JT12:   Data Start
JT13:   Data Start
JT14:   Data ReadLowerOk
JT15:   Data Start
JT16:   Data ReadMinus
JT17:   Data Start
JT18:   Data ReadFinalOk
JT19:   Data ReadNegateReturn
JT20:   Data AbsStore
JT21:   Data AbsDiff
JT22:   Data PrintNonZero
JT23:   Data PrintPositiveSetup
JT24:   Data PrintNum
JT25:   Data ReadNum
JT26:   Data PrintStr
JT27:   Data PrintStrDone
JT28:   Data ErrorHandler
JT29:   Data PrintStrLoop
JT30:   Data PrintStr
JT31:   Data PrintStrDone

; User-facing strings.
	LOC 550
MSG20:  Data 69              ; E
	Data 78              ; N
	Data 84              ; T
	Data 69              ; E
	Data 82              ; R
	Data 32              ; ' '
	Data 50              ; 2
	Data 48              ; 0
	Data 32              ; ' '
	Data 78              ; N
	Data 85              ; U
	Data 77              ; M
	Data 66              ; B
	Data 69              ; E
	Data 82              ; R
	Data 83              ; S
	Data 58              ; :
	Data 32              ; ' '
	Data 0

MSGTQ:  Data 69              ; E
	Data 78              ; N
	Data 84              ; T
	Data 69              ; E
	Data 82              ; R
	Data 32              ; ' '
	Data 84              ; T
	Data 65              ; A
	Data 82              ; R
	Data 71              ; G
	Data 69              ; E
	Data 84              ; T
	Data 58              ; :
	Data 32              ; ' '
	Data 0

MSGTL:  Data 84              ; T
	Data 65              ; A
	Data 82              ; R
	Data 71              ; G
	Data 69              ; E
	Data 84              ; T
	Data 58              ; :
	Data 32              ; ' '
	Data 0

MSGCL:  Data 67              ; C
	Data 76              ; L
	Data 79              ; O
	Data 83              ; S
	Data 69              ; E
	Data 83              ; S
	Data 84              ; T
	Data 58              ; :
	Data 32              ; ' '
	Data 0

MSGER:  Data 69              ; E
	Data 82              ; R
	Data 82              ; R
	Data 79              ; O
	Data 82              ; R
	Data 10              ; '\n'
	Data 0
