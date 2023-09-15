package me.alex_s168.stackvm2.inst

data class Instruction(
    val id: Int,
    val name: String,
    val argCount: Int,
    val args: List<ArgType>,
    val properties: List<InstructionProperties>
)