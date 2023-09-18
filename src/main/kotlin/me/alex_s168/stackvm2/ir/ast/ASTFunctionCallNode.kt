package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTFunctionCallNode(
    var func: ASTNode,
    var arguments: List<ASTNode>,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = (listOf(func) + arguments)
        .toMutableList(),
    line,
    column,
    length,
    parent
) {

    override fun rebuildFromChildren() {
        if (children.isEmpty())
            throw ASTNodeRebuildException()
        func = children.first()
        arguments = children.subList(1, children.size)
    }

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Func:",
            func.toString(1).trimEnd(),
            "  Args:"
        ) + arguments.map { it.toString(1).trimEnd() }

}