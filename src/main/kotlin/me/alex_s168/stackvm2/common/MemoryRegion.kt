package me.alex_s168.stackvm2.common

data class MemoryRegion(
    val start: Int,
    val end: Int,
    val isWritable: Boolean,
    val isReadable: Boolean,
    @Suppress("ArrayInDataClass") var mem: IntArray? = null
)