package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.ast.ASTVariableNode
import me.alex_s168.stackvm2.ir.`var`.getVariable

fun check(node: ASTNode) {
    if (node is ASTVariableNode) {
        if (node.getVariable(node.name) == null)
            Language.exception(
                "'${node.name}' is not defined!",
                node.line,
                node.column,
                node.length
            )
    }

    for (child in node.children)
        check(child)
}