package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.`var`.MemoryAllocation
import me.alex_s168.stackvm2.ktcode.`var`.Stackable
import java.util.*
import kotlin.collections.ArrayList

class VMFunction (
    private val ktcode: KTCode,
    val args: Int,
    val code: (FunctionCallData) -> Unit
) {

    val memAlloc = MemoryAllocation(ktcode, -1, Random().nextLong())

    operator fun invoke(vararg args: Any) {
        this.call(*args)
    }

    fun call(vararg args: Any) {
        if (ktcode.locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (args.size != this.args) {
            throw IllegalArgumentException("Invalid number of arguments!")
        }

        for (arg in args.reversed()) {
            when (arg) {
                is Int -> ktcode.load(arg)
                is Char -> ktcode.load(arg.code)
                is Stackable -> arg.putOntoStack()
                else -> throw IllegalArgumentException("Invalid argument type!")
            }
        }

        ktcode.mem += Instructions.CALL.id
        ktcode.unresolvedReferences.add(ktcode.mem.size to this.memAlloc)
        ktcode.mem += 0
    }

    class FunctionCallData(
        val func: VMFunction
    ) {

        private val args = ArrayList<Stackable>()
        private var ret: Stackable? = null

        init {
            repeat(func.args) {
                args.add(func.ktcode.getTop())
            }
        }

        operator fun get(index: Int): Stackable =
            args[index]

        fun setReturn(value: Stackable) {
            ret = value
        }

        fun getReturnValue(): Stackable? =
            ret

    }

}