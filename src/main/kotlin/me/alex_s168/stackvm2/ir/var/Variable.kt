package me.alex_s168.stackvm2.ir.`var`

class Variable(
    val name: String,
    val type: Type,
    val isMutable: Boolean,

    val defLine: Int,
    val defColumn: Int,
    val defLength: Int
) {
}