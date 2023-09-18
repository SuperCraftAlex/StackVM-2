package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.exception.ASTNodeRebuildException

class ASTTypeNode(
    var name: String,
    var generics: List<ASTTypeNode>,
    var arrSize: ASTNode?,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = generics.toMutableList(),
    line,
    column,
    length,
    parent
) {

    override fun rebuildFromChildren() {
        throw ASTNodeRebuildException()
    }

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Name: $name",
            "  Generics:"
        ) + generics.map { it.toString(1).trimEnd() } + if (arrSize != null) listOf(
            "  Array size:",
            arrSize!!.toString(1).trimEnd()
        ) else listOf()

}