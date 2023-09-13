package me.alex_s168.stackvm2.vm.mem

class SimpleMemory(
    private val mem: IntArray
): Memory {

    override fun get(index: Int): Int =
        mem[index]

    override fun set(index: Int, value: Int) {
        mem[index] = value
    }

    override fun isWritable(index: Int): Boolean =
        index < mem.size && index >= 0

    override fun isReadable(index: Int): Boolean =
        index < mem.size && index >= 0

}