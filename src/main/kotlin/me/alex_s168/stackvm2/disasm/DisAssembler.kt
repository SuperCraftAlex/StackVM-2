package me.alex_s168.stackvm2.disasm

import me.alex_s168.stackvm2.inst.ArgType
import me.alex_s168.stackvm2.inst.Instructions

class DisAssembler(
    val code: IntArray,
    var pc: Int = 0
) {

    val usedMemoryAddresses = HashMap<Int, String>()

    fun disassemble(): String {
        val sb = StringBuilder()

        while (pc < code.size) {
            val inst = Instructions.getInst(code[pc])

            if (inst == null) {
                sb.append("UNKNOWN: ")
                sb.append(code[pc])
                sb.append("\n")
                pc ++
                continue
            }

            sb.append(inst.name)
            sb.append(" ")
            pc ++
            for ((j, instArg) in inst.args.withIndex()) {
                val arg = code[pc + j]

                if (instArg == ArgType.IMMEDIATE) {
                    sb.append('i')
                    sb.append(arg)
                }
                else if (instArg == ArgType.ADDRESS) {
                    usedMemoryAddresses.putIfAbsent(arg, "addr_$arg")
                    sb.append(usedMemoryAddresses[arg])
                }
                else if (instArg == ArgType.PC_ADDRESS) {
                    usedMemoryAddresses.putIfAbsent(arg, "label_$arg")
                    sb.append(usedMemoryAddresses[arg])
                }
                else if (instArg == ArgType.MAGIC_NUMBER) {
                    sb.append("0x")
                    sb.append(arg.toString(16))
                }
                else if (instArg == ArgType.RELATIVE) {
                    sb.append("r")
                    sb.append(arg)
                }


                sb.append(" ")
            }
            sb.append("\n")
            pc += inst.argCount
        }

        return sb.toString()
    }

}