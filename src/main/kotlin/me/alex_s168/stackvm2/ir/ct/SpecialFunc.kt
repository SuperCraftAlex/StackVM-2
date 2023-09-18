package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.`var`.Type

abstract class SpecialFunc(
    val name: String,
    val args: List<Type>
) {

    abstract fun run(args: List<ASTNode>): ASTNode?

}