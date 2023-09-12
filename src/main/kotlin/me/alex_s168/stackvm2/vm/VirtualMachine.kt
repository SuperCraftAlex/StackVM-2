package me.alex_s168.stackvm2.vm

import me.alex_s168.stackvm2.inst.Instructions
import kotlin.math.abs

class VirtualMachine(
    val mem: IntArray,
    private val interrupts: Map<Int, (VirtualMachine) -> Unit>
) {
    var sp: Int = 0
    private var pc: Int = 512
    private var condF = true

    var running = true

    fun tick() {
        if (!running) {
            return
        }

        pc %= mem.size

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
                pc = mem[pc + 1] - 1
                mem[sp] = pc
                sp += 1
            }
            Instructions.CALL_COND.id 	-> {
                if (condF) {
                    pc = mem[pc + 1] - 1
                    mem[sp] = pc
                    sp ++
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
                mem[sp] = mem[pc + 1]
                pc ++
                sp ++
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
                mem[sp - 1] = mem[mem[sp - 1]]
            }
            Instructions.ST_ST_ADDR.id	-> {
                mem[mem[sp - 1]] = mem[mem[sp - 2]]
                sp -= 2
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

        pc += 1
    }

    fun debug() {
        val inst = Instructions.getInst(mem[pc])
        print("next:  ")

        var txt = ""
        if (inst == null) {
            txt = "#err "
        }
        else {
            txt += inst.name + "  "
            var argStr = ""
            for (a in (0..<inst.args)) {
                argStr += mem[pc + a + 1].toString().padStart(6, '0') + "  "
            }
            txt += argStr
        }

        print(txt.padEnd(20))
        txt = "  current:  sp:  ${sp.toString().padStart(6, '0')}  pc:  ${pc.toString().padStart(6, '0')}    "
        print(txt.padEnd(26) + "[")
        txt = "  "

        for (i in (0..<6)) {
            txt += if (sp-i <= 0) {
                "        "
            } else {
                if (i == 5) {
                    "......" + "  "
                } else {
                    mem[sp-i-1].toString().padStart(6, '0') + "  "
                }
            }
        }

        println("$txt]")
    }
}

