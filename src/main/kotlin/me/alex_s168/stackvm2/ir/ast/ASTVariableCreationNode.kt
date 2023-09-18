package me.alex_s168.stackvm2.ir.ast

class ASTVariableCreationNode(
    val left: ASTNode,
    val types: List<ASTNode>,
    val default: ASTNode? = null,
    line: Int,
    column: Int
): ASTNode(listOf(left) + types + if (default != null) listOf(default) else emptyList(), line, column) {

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "Left:",
            left.toString(0).trimEnd(),
            "Types:"
        ) + types.map { it.toString(0).trimEnd() } + if (default != null) listOf(
            "Init:",
            default.toString(0).trimEnd()
        ) else emptyList()

}