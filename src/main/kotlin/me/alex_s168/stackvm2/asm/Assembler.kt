package me.alex_s168.stackvm2.asm

import me.alex_s168.stackvm2.inst.Instructions

class Assembler(
    val labels: HashMap<String, Int> = HashMap(),
    val gen: MutableList<Int>,
    var index: Int = 0
) {

    val errors = ArrayList<String>()

    private val unresolved = mutableListOf<Pair<Int, String>>()

    fun addSource(txt: String): Assembler {
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

                repeat(v) { gen += 0 }
                index += v
            }

            if (args[0] != "data") {
                val inst = Instructions.getInst(args[0])

                if (inst == null) {
                    errors.add("instruction not found: ${args[0]}")
                    continue
                }

                gen += inst.id
                index ++
            }

            for (arg in args.subList(1, args.size)) {

                val v = arg.toIntOrNull() ?: (0).also {
                    unresolved.add(index to arg)
                }

                gen += v
                index ++
            }
        }

        return this
    }

    fun resolveLocalLabels(): Assembler {
        for ((i, arg) in unresolved.toList()) {
            if (arg.startsWith("_"))
                continue

            if (arg in labels) {
                gen[i] = labels[arg]!!
                unresolved.remove(i to arg)
            }
            else {
                errors.add("invalid literal: $arg")
            }
        }

        return this
    }

    fun resolveLabels(): Assembler {
        for ((i, arg) in unresolved) {
            if (arg in labels)
                gen[i] = labels[arg]!!
            else errors.add("invalid literal: $arg")
        }

        return this
    }

    fun getGlobalLabels(): HashMap<String, Int> =
        labels.filter {
            it.key.startsWith("_")
        }.toMap(HashMap())

    fun getUnresolvedLabels(): List<Pair<Int, String>> =
        unresolved.toList()

    // not used YET
    fun complete(): Assembler {
        return this
    }

}