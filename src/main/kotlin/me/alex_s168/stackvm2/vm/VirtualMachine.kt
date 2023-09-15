package me.alex_s168.stackvm2.vm

import me.alex_s168.ktlib.counter.UpCounter
import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.vm.mem.Memory
import kotlin.math.abs

class VirtualMachine(
    private val mem: Memory,
    private var pc: Int = 512,
    private val interrupts: Map<Int, (VirtualMachine) -> Unit>
) {
    private var sp: Int = 0
    private var condF = true

    var running = true

    private var prevPc = -1

    private val ticks = UpCounter()

    fun ticksElapsed(): Int =
        ticks.get()

    fun push(v: Int) {
        mem[sp ++] = v
    }

    fun pop(): Int {
        return mem[-- sp]
    }

    fun tick() {
        if (!running) {
            return
        }

        ticks.next()

        prevPc = pc
        when (mem[pc]) {
            Instructions.NOOP.id  	 	-> {

            }
            Instructions.INC_SP.id	 	-> {
                sp ++
            }
            Instructions.DEC_SP.id	 	-> {
                sp --
            }
            Instructions.SET_Z.id 	 	-> {
                mem[sp] = 0
            }
            Instructions.MSP.id    		-> {
                sp = mem[pc + 1]
                //sp = mem[pc + 1]
                pc ++
            }
            Instructions.REL_MSP.id		-> {
                sp += mem[pc + 1]
                pc ++
            }

            Instructions.JUMP.id 		-> {
                pc = mem[pc + 1] - 1
            }
            Instructions.JUMP_COND.id 	-> {
                if (condF) {
                    pc = mem[pc + 1] - 1
                }
            }
            Instructions.CALL.id 		-> {
                val old = pc
                pc = mem[pc + 1] - 1
                push(old + 1)
            }
            Instructions.CALL_COND.id 	-> {
                if (condF) {
                    val old = pc
                    pc = mem[pc + 1] - 1
                    push(old + 1)
                }
            }
            Instructions.RET.id 		-> {
                pc = mem[sp - 1]
                sp --
            }
            Instructions.RET_COND.id 	-> {
                if (condF) {
                    pc = mem[sp - 1]
                    sp --
                }
            }

            Instructions.LD_IMM.id 		-> {
                push(mem[pc + 1])
                pc ++
            }
            Instructions.LD_ADDR.id		-> {
                mem[sp] = mem[mem[pc + 1]]
                pc ++
                sp ++
            }
            Instructions.ST_ADDR.id		-> {
                mem[mem[pc + 1]] = mem[sp - 1]
                pc ++
                sp --
            }
            Instructions.LD_ST_ADDR.id	-> {
                push(mem[pop()])
            }
            Instructions.ST_ST_ADDR.id	-> {
                mem[pop()] = pop()
            }

            Instructions.INC.id		 	-> {
                mem[sp - 1] = mem[sp - 1] + 1
            }
            Instructions.DEC.id		 	-> {
                mem[sp - 1] = mem[sp - 1] + 1
            }

            Instructions.INT.id		 	-> {
                val id = mem[pc + 1]
                if (id in interrupts) {
                    interrupts[id]!!(this)
                }
                pc ++
            }

            Instructions.NOT.id		 	-> {
                condF = !condF
            }
            Instructions.AND.id		 	-> {
                condF = condF && mem[sp - 1] != 0
                sp --
            }
            Instructions.OR.id		 	-> {
                condF = condF && mem[sp - 1] != 0
                sp --
            }

            Instructions.PH_COND.id		-> {
                mem[sp] = if (condF) 1 else 0
                sp ++
            }
            Instructions.PO_COND.id		-> {
                condF = mem[sp - 1] != 0
                sp --
            }

            Instructions.ABS.id		-> {
                mem[sp - 1] = abs(mem[sp - 1])
            }
            Instructions.ADD.id		-> {
                mem[sp - 2] = mem[sp - 2] + mem[sp - 1]
                sp --
            }
            Instructions.SUB.id		-> {
                mem[sp - 2] = mem[sp - 2] - mem[sp - 1]
                sp --
            }
            Instructions.MUL.id		-> {
                mem[sp - 2] = mem[sp - 2] * mem[sp - 1]
                sp --
            }
            Instructions.DIV.id		-> {
                mem[sp - 2] = mem[sp - 2] * mem[sp - 1]
                sp --
            }
            Instructions.MOD.id		-> {
                mem[sp - 2] = mem[sp - 2] % mem[sp - 1]
                sp --
            }

            Instructions.DUP.id		-> {
                mem[sp] = mem[sp - 1]
                sp ++
            }
            Instructions.SWP.id		-> {
                val tmp = mem[sp-1]
                mem[sp-1] = mem[sp-2]
                mem[sp-2] = tmp
            }

            else -> {}
        }

        pc ++
    }

    fun debug() {
        fun printInt(v: Int) =
            if (v < 0) "- " + v.toString().substring(1).padStart(4, '0')
            else v.toString().padStart(6, '0')

        val inst = Instructions.getInst(mem[prevPc])

        var txt = ""
        if (inst == null) {
            txt = "#err "
        }
        else {
            txt += inst.name + "  "
            var argStr = ""
            for (a in (0..<inst.argCount)) {
                argStr += printInt(mem[prevPc + a + 1]) + "  "
            }
            txt += argStr
        }

        print(txt.padEnd(20))
        txt = "   sp:  ${printInt(sp)}  pc:  ${printInt(pc)}    "
        print(txt.padEnd(26) + "[")
        txt = "  "

        for (i in (0..<6)) {
            txt += if (sp-i <= 0) {
                "        "
            } else {
                if (i == 5) {
                    "......" + "  "
                } else {
                    printInt(mem[sp-i-1]) + "  "
                }
            }
        }

        println("$txt]")
    }

    companion object {
        const val ELEMENT_SIZE: Int = Int.SIZE_BYTES
    }
}

