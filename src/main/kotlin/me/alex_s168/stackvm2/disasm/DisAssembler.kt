package me.alex_s168.stackvm2.disasm

import me.alex_s168.stackvm2.inst.ArgType
import me.alex_s168.stackvm2.inst.Instructions

class DisAssembler(
    val code: IntArray,
    var pc: Int = 0
) {

    val usedMemoryAddresses = HashMap<Int, String>()
    val usedLabels = HashMap<Int, String>()

    private fun insertLabels(a: String, inCodeOff: Int): String {
        val sb = StringBuilder("_start:\n")

        var c = inCodeOff
        for (line in a.split('\n')) {
            if (c in usedMemoryAddresses.keys) {
                sb.append(usedMemoryAddresses[c])
                sb.append(":\n")
            }
            else if (c in usedLabels.keys) {
                sb.append(usedLabels[c])
                sb.append(":\n")
            }

            sb.append("    ")
            sb.append(line)
            sb.append('\n')

            c += line.split(" ").count() - 1
        }

        return sb.toString()
    }

    fun disassemble(inCodeOff: Int): String {
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
                    usedLabels.putIfAbsent(arg, "label_$arg")
                    sb.append(usedLabels[arg])
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

        return insertLabels(sb.toString(), inCodeOff)
    }

}