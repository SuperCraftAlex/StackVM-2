package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.`var`.Type

data class CTFunc(
    val name: String,
    val args: List<Type>,
    val run: (List<ASTNode>) -> ASTNode?
)