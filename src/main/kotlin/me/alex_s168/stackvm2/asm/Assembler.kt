package me.alex_s168.stackvm2.asm

import me.alex_s168.stackvm2.inst.Instructions

class Assembler(
    val labels: HashMap<String, Int>,
    val gen: IntArray,
    var index: Int
) {

    val errors = ArrayList<String>()

    fun assemble(txt: String) {
        val unresolved = mutableListOf<Pair<Int, String>>()

        val lines = txt.split('\n')

        for (lineI in lines) {
            val line = " $lineI".split(';')[0].trim()

            if (line.isEmpty()) continue

            if (line.last() == ':') {
                labels[line.substringBefore(':')] = index
                continue
            }

            val args = line.split(" ")

            if (args[0] == "empty") {
                if (args.size != 2) {
                    errors.add("invalid arguments for empty instruction")
                }

                val v = args[1].toIntOrNull()

                if (v == null) {
                    errors.add("not a number: ${args[1]}")
                    continue
                }

                index += v
            }

            if (args[0] != "data") {
                val inst = Instructions.getInst(args[0])

                if (inst == null) {
                    errors.add("instruction not found: ${args[0]}")
                    continue
                }

                gen[index] = inst.id
                index ++
            }

            for (arg in args.subList(1, args.size)) {

                val v = arg.toIntOrNull() ?: (0).also {
                    unresolved.add(index to arg)
                }

                gen[index] = v
                index ++
            }
        }

        for ((i, arg) in unresolved) {
            if (arg in labels)
                gen[i] = labels[arg]!!
            else errors.add("invalid literal: $arg")
        }
    }

}