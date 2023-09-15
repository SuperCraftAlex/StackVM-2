package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.`var`.MemoryAllocation
import java.util.*

class VMFunction (
    private val ktcode: KTCode,
    val args: Int,
    val code: () -> Unit
) {

    val memAlloc = MemoryAllocation(ktcode, -1, Random().nextLong())

    fun call(vararg args: Any) {
        if (ktcode.locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (args.size != this.args) {
            throw IllegalArgumentException("Invalid number of arguments!")
        }

        for (arg in args) {
            when (arg) {
                is Int -> ktcode.load(arg)
                is Char -> ktcode.load(arg.toInt())
                is MemoryAllocation -> ktcode.load(arg)
                else -> throw IllegalArgumentException("Invalid argument type!")
            }
        }

        ktcode.rotateTop(this.args)

        ktcode.mem += Instructions.CALL.id
        ktcode.unresolvedReferences.add(ktcode.mem.size to this.memAlloc)
        ktcode.mem += 0
    }

}