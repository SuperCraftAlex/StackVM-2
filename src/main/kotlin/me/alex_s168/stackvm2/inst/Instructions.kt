package me.alex_s168.stackvm2.inst

import me.alex_s168.stackvm2.inst.ArgType.*
import me.alex_s168.stackvm2.inst.InstructionProperties.*

object Instructions {
    private val instructions = ArrayList<Instruction>()

    // nop			 	no operation
    val NOOP        = reg(
        id = 0,
        name = "nop",
        args = listOf(),
        properties = listOf()
    )

    // isp				increment stack pointer
    val INC_SP      = reg(
        id = 1,
        name = "isp",
        args = listOf(),
        properties = listOf(
            SP_MODIFYING
        )
    )

    // dsp				decrement stack pointer
    val DEC_SP      = reg(
        id = 2,
        name = "dsp",
        args = listOf(),
        properties = listOf(
            SP_MODIFYING
        )
    )

    // msp [position]	moves the stack pointer to a given memory address
    val MSP		    = reg(
        id = 3,
        name = "msp",
        args = listOf(
            ADDRESS
        ),
        properties = listOf(
            SP_MODIFYING
        )
    )

    // rms [offset]		moves the stack pointer relative to the current position
    val REL_MSP     = reg(
        id = 4,
        name = "rms",
        args = listOf(
            RELATIVE
        ),
        properties = listOf(
            SP_MODIFYING
        )
    )

    // sez				sets the top value of the stack to zero
    val SET_Z       = reg(
        id = 5,
        name = "sez",
        args = listOf(),
        properties = listOf()
    )

    // jmp [address]	jumps to the given address
    val JUMP	    = reg(
        id = 10,
        name = "jmp",
        args = listOf(
            PC_ADDRESS
        ),
        properties = listOf(
            JUMPING
        )
    )

    // jmc [address]	if the condition flag is set, jumps to the given address
    val JUMP_COND   = reg(
        id = 11,
        name = "jmc",
        args = listOf(
            PC_ADDRESS
        ),
        properties = listOf(
            JUMPING,
            CONDITIONAL
        )
    )

    // cal [address]	jumps to the given address and pushes the position onto the stack
    val CALL	    = reg(
        id = 12,
        name = "cal",
        args = listOf(
            PC_ADDRESS
        ),
        properties = listOf(
            JUMPING,
            CALLING
        )
    )

    // cac [address]	if the condition flag is set, jumps to the given address and pushes the position onto the stack
    val CALL_COND   = reg(
        id = 13,
        name = "cac",
        args = listOf(
            PC_ADDRESS
        ),
        properties = listOf(
            JUMPING,
            CALLING,
            CONDITIONAL
        )
    )

    // ret				jumps to the address on the stack (and pops it)
    val RET		    = reg(
        id = 14,
        name = "ret",
        args = listOf(),
        properties = listOf(
            JUMPING,
            RETURNING
        )
    )

    // rec				if the condition flag is set, jumps to the address on the stack (and pops it)
    val RET_COND    = reg(
        id = 15,
        name = "rec",
        args = listOf(),
        properties = listOf(
            JUMPING,
            RETURNING,
            CONDITIONAL
        )
    )


    // ldi [value]		pushes the given value onto the stack
    val LD_IMM 	    = reg(
        id = 20,
        name = "ldi",
        args = listOf(
            IMMEDIATE
        ),
        properties = listOf(
            ONLY_PUSH
        )
    )

    // ldo [address]	pushes the value at the given address onto the stack
    val LD_ADDR     = reg(
        id = 21,
        name = "ldo",
        args = listOf(
            ADDRESS
        ),
        properties = listOf(
            ONLY_PUSH
        )
    )

    // sto [address]	pops from the stack into the given address
    val ST_ADDR     = reg(
        id = 22,
        name = "sto",
        args = listOf(
            ADDRESS
        ),
        properties = listOf(
            ONLY_POP
        )
    )

    // los				pops the address from the stack and then pushes the value at that address onto the stack
    val LD_ST_ADDR  = reg(
        id = 33,
        name = "los",
        args = listOf(),
        properties = listOf()
    )

    // sts				pops address from the stack, pops value from stack then stores value at address
    val ST_ST_ADDR  = reg(
        id = 24,
        name = "sts",
        args = listOf(),
        properties = listOf()
    )

    // inc				increments the value at the stack
    val INC			= reg(
        id = 30,
        name = "inc",
        args = listOf(),
        properties = listOf(
            OP_1_1
        )
    )

    // dec				decrements the value at the stack
    val DEC			= reg(
        id = 31,
        name = "dec",
        args = listOf(),
        properties = listOf(
            OP_1_1
        )
    )

    // int [id]			calls the specified hardware interrupt
    val INT			= reg(
        id = 40,
        name = "int",
        args = listOf(
            MAGIC_NUMBER
        ),
        properties = listOf()
    )

    // not				inverts the condition flag
    val NOT			= reg(
        id = 41,
        name = "not",
        args = listOf(),
        properties = listOf()
    )

    // and				condition flag = condition flag && stack.pop()
    val AND			= reg(
        id = 42,
        name = "and",
        args = listOf(),
        properties = listOf()
    )

    // or				condition flag = condition flag || stack.pop()
    val OR			= reg(
        id = 43,
        name = "orc",
        args = listOf(),
        properties = listOf()
    )

    // phc				push condition flag onto thhe stack
    val PH_COND		= reg(
        id = 44,
        name = "phc",
        args = listOf(),
        properties = listOf(
            ONLY_PUSH
        )
    )

    // poc				pop condition flag from the stack
    val PO_COND		= reg(
        id = 45,
        name = "poc",
        args = listOf(),
        properties = listOf(
            ONLY_POP
        )
    )

    // abs				replaces the top element of the stack with the absolute value of it
    val ABS			= reg(
        id = 50,
        name = "abs",
        args = listOf(),
        properties = listOf(
            OP_1_1
        )
    )

    // add				replaces the top two elements of the stack with the sum of both
    val ADD			= reg(
        id = 51,
        name = "add",
        args = listOf(),
        properties = listOf(
            OP_2_1
        )
    )

    // sub				replaces the top two elements of the stack with the difference of both
    val SUB			= reg(
        id = 52,
        name = "sub",
        args = listOf(),
        properties = listOf(
            OP_2_1
        )
    )

    // mul				replaces the top two elements of the stack with the product of both
    val MUL			= reg(
        id = 53,
        name = "mul",
        args = listOf(),
        properties = listOf(
            OP_2_1
        )
    )

    // div				replaces the top two elements of the stack with the quotient of both
    val DIV			= reg(
        id = 54,
        name = "div",
        args = listOf(),
        properties = listOf(
            OP_2_1
        )
    )

    // mod				replaces the top two elements of the stack with the remainder of both
    val MOD			= reg(
        id = 55,
        name = "mod",
        args = listOf(),
        properties = listOf(
            OP_2_1
        )
    )

    // dup				duplicates the top element on the stack
    val DUP			= reg(
        id = 60,
        name = "dup",
        args = listOf(),
        properties = listOf()
    )

    // swp				swaps the top two element on the stack
    val SWP			= reg(
        id = 61,
        name = "swp",
        args = listOf(),
        properties = listOf()
    )

    private fun reg(inst: Instruction): Instruction {
        instructions.add(inst)
        return inst
    }

    private fun reg(id: Int, name: String, args: List<ArgType>, properties: List<InstructionProperties>): Instruction {
        val inst = Instruction(id, name, args.size, args, properties)
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