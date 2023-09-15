package me.alex_s168.stackvm2.ktcode.`var`

import me.alex_s168.stackvm2.ktcode.KTCode
import kotlin.math.max

class RAMAllocation(
    ktcode: KTCode,
    size: Int,
    uuid: Long = 0
): MutableMemoryAllocation<RAMAllocation>(
    ktcode,
    size,
    uuid,
    IntArray(max(0, size)) { 0 }
) {

    override fun putOntoStack() {
        if (this !in ktcode.ramAllocs)
            throw IllegalArgumentException("Cannot load from unallocated memory!")

        putOntoStack(0)
    }

    override fun loadFromStack() {
        if (this !in ktcode.ramAllocs)
            throw IllegalArgumentException("Cannot store to unallocated memory!")

        loadFromStack(0)
    }

}