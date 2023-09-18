package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTArrayIndexNode(
    var array: ASTNode,
    var index: ASTNode,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = mutableListOf(array, index),
    line,
    column,
    length,
    parent
) {

    override fun rebuildFromChildren() {
        if (children.size < 2)
            throw ASTNodeRebuildException()
        array = children.first()
        index = children.last()
    }

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Array:",
            array.toString(1).trimEnd(),
            "  Index:",
            index.toString(1).trimEnd()
        )

}