package me.alex_s168.stackvm2.ktcode.`var`

import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.KTCode
import java.util.Objects
import java.util.Random
import kotlin.math.max

open class MemoryAllocation(
    val ktcode: KTCode,
    val size: Int,
    val uuid: Long = ktcode.random.nextLong(),
    val initVal: IntArray = IntArray(max(0, size)) { 0 }
): Stackable {

    open val value: Stackable
        get() = this

    val eAm = max(1, size / ktcode.elemSize)

    fun load(index: Int) {
        if (index > eAm)
            throw UnsupportedOperationException("Cannot load from offset bigger than size of MemoryAllocation!")

        ktcode.mem += Instructions.LD_ADDR.id
        ktcode.unresolvedReferences3.add(Triple(ktcode.mem.size, this, index))
        ktcode.mem += 0
    }

    override fun getKTCode(): KTCode =
        ktcode

    override fun getElemSize(): Int =
        eAm

    override fun putOntoStack(offset: Int) =
        load(offset)

    override fun putOntoStack(offset: Stackable) {
        ktcode.mem += Instructions.LD_IMM.id
        ktcode.unresolvedReferences.add(ktcode.mem.size to this)
        ktcode.mem += 0

        if (offset.getElemSize() != 1)
            throw UnsupportedOperationException("Index of MemoryAllocation needs to be Int!")

        offset.putOntoStack()

        ktcode.mem += Instructions.ADD.id

        ktcode.stackLoad()
    }

    override fun putOntoStack() {
        if (this !in ktcode.memAllocs)
            throw IllegalArgumentException("Cannot load from unallocated memory!")

        repeat(eAm) {
            load(it)
        }
    }

}