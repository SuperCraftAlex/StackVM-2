package me.alex_s168.stackvm2.vm.mem

interface Memory {

    fun isWritable(index: Int): Boolean

    fun isReadable(index: Int): Boolean

    operator fun get(index: Int): Int

    operator fun set(index: Int, value: Int)

}