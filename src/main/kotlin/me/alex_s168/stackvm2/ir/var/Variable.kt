package me.alex_s168.stackvm2.ir.`var`

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.types.Type

class Variable(
    val name: String,
    val type: Type,
    val isMutable: Boolean,

    val defLine: Int,
    val defColumn: Int,
    val defLength: Int,

    var defaultValue: ASTNode? = null,
) {

    override fun toString(): String =
        "Variable(name=$name, type=$type, mutable=$isMutable)"

}