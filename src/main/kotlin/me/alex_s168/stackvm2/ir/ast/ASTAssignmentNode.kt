package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTAssignmentNode(
    var left: ASTNode,
    var right: ASTNode,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = mutableListOf(left, right),
    line,
    column,
    length,
    parent
) {

    override fun rebuildFromChildren() {
        if (children.size < 2)
            throw ASTNodeRebuildException()
        left = children.first()
        right = children.last()
    }

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "Left:",
            left.toString(0).trimEnd(),
            "Right:",
            right.toString(0).trimEnd()
        )

}