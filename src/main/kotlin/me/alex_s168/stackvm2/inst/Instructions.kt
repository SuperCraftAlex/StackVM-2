package me.alex_s168.stackvm2.inst

object Instructions {
    private val instructions = ArrayList<Instruction>()


    val NOOP        = reg(0,  "nop", 0)		// nop			 	no operation
    val INC_SP      = reg(1,  "isp", 0)		// isp				increment stack pointer
    val DEC_SP      = reg(2,  "dsp", 0)		// dsp				decrement stack pointer
    val MSP		    = reg(3,  "msp", 1)		// msp [position]	moves the stack pointer to a given memory address
    val REL_MSP     = reg(4,  "rms", 1)		// rms [offset]		moves the stack pointer relative to the current position
    val SET_Z       = reg(5,  "sez", 0)		// sez				sets the top value of the stack to zero

    val JUMP	    = reg(10, "jmp", 1)		// jmp [address]	jumps to the given address
    val JUMP_COND   = reg(11, "jmc", 1)		// jmc [address]	if the condition flag is set, jumps to the given address
    val CALL	    = reg(12, "cal", 1)		// cal [address]	jumps to the given address and pushes the position onto the stack
    val CALL_COND   = reg(13, "cac", 1)		// cac [address]	if the condition flag is set, jumps to the given address and pushes the position onto the stack
    val RET		    = reg(14, "ret", 0)		// ret				jumps to the address on the stack (and pops it)
    val RET_COND    = reg(15, "rec", 0)		// rec				if the condition flag is set, jumps to the address on the stack (and pops it)

    val LD_IMM 	    = reg(20, "ldi", 1)		// ldi [value]		pushes the given value onto the stack
    val LD_ADDR     = reg(21, "lod", 1)		// lod [address]	pushes the value at the given address onto the stack
    val ST_ADDR     = reg(22, "sto", 1)		// sto [address]	pops from the stack into the given address
    val LD_ST_ADDR  = reg(33, "los", 0)		// los				pops the address from the stack and then pushes the value at that address onto the stack
    val ST_ST_ADDR  = reg(24, "sts", 0)		// sts				pops address from the stack, pops value from stack then stores value at address

    val INC			= reg(30, "inc", 0)		// inc				increments the value at the stack
    val DEC			= reg(31, "dec", 0)		// dec				decrements the value at the stack

    val INT			= reg(40, "int", 1)		// int [id]			calls the specified hardware interrupt

    val NOT			= reg(41, "not", 0)		// not				inverts the condition flag
    val AND			= reg(42, "and", 0)		// and				condition flag = condition flag && stack.pop()
    val OR			= reg(43, "orc", 0)		// or				condition flag = condition flag || stack.pop()
    val PH_COND		= reg(44, "phc", 0)		// phc				push condition flag onto thhe stack
    val PO_COND		= reg(45, "poc", 0)		// poc				pop condition flag from the stack

    val ABS			= reg(50, "abs", 0)		// abs				replaces the top element of the stack with the absolute value of it
    val ADD			= reg(51, "add", 0)		// add				replaces the top two elements of the stack with the sum of both
    val SUB			= reg(52, "sub", 0)		// sub				([top-1] [top]) = [top-1] - [top]
    val MUL			= reg(53, "mul", 0)		// mul				([top-1] [top]) = [top-1] * [top]
    val DIV			= reg(54, "div", 0)		// div				([top-1] [top]) = [top-1] / [top]
    val MOD			= reg(55, "mod", 0)		// mod				([top-1] [top]) = [top-1] % [top]

    val DUP			= reg(60, "dup", 0)		// dup				duplicates the top element on the stack
    val SWP			= reg(61, "swp", 0)		// swp				swaps the top two element on the stack


    private fun reg(inst: Instruction): Instruction {
        instructions.add(inst)
        return inst
    }

    private fun reg(id: Int, name: String, args: Int): Instruction {
        val inst = Instruction(id, name, args)
        return reg(inst)
    }

    fun getInst(name: String): Instruction? {
        for (inst in instructions) {
            if (inst.name == name) {
                return inst
            }
        }
        return null
    }

    fun getInst(id: Int): Instruction? {
        for (inst in instructions) {
            if (inst.id == id) {
                return inst
            }
        }
        return null
    }
}