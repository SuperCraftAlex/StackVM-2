package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTAddressOfNode(
    var value: ASTNode,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = mutableListOf(value),
    line,
    column,
    length,
    parent
) {

    override fun rebuildFromChildren() {
        if (children.isEmpty())
            throw ASTNodeRebuildException()
        value = children.first()
    }

}