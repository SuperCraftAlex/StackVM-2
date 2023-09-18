package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTVariableCreationNode(
    var left: ASTNode,
    var types: List<ASTNode>,
    var default: ASTNode? = null,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = (listOf(left) + types +
            if (default != null) listOf(default)
            else emptyList()
            )
        .toMutableList(),
    line,
    column,
    length,
    parent
) {

    override fun rebuildChildren() {
        children = (listOf(left) + types +
                if (default != null) listOf(default!!)
                else emptyList()
                )
            .toMutableList()
    }

    override fun rebuildFromChildren() {
        if (children.size < 2)
            throw ASTNodeRebuildException()
        left = children[0]
        types = children.subList(1, children.size - 1)
        default = children.getOrNull(children.size - 1)
    }

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "Left:",
            left.toString(0).trimEnd(),
            "Types:"
        ) + types.map { it.toString(0).trimEnd() } + if (default != null) listOf(
            "Init:",
            default!!.toString(0).trimEnd()
        ) else emptyList()

}