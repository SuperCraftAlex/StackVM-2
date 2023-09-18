package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.ast.ASTFunctionCallNode
import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.ast.ASTVariableNode

fun execSpecial(node: ASTNode) {
    if (node is ASTFunctionCallNode) {
        if ((node.func as? ASTVariableNode)?.name?.last() == '!') {

        }
    }

    for (child in node.children)
        execSpecial(child)
}