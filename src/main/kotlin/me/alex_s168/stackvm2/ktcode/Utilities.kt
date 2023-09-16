package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.mem.MemoryLayout

fun compile(
    elementSize: Int,
    memoryLayout: MemoryLayout,
    offset: Int,
    block: (KTCode) -> Unit
): IntArray {
    val ktcode = KTCode(offset, elementSize, memoryLayout)

    ktcode.prog = block

    return ktcode.compile()
}