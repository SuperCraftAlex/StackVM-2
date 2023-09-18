package me.alex_s168.stackvm2.ir.ast

class ASTVariableNode(
    val name: String,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    mutableListOf(),
    line,
    column,
    length,
    parent
) {
    override fun toStringExtra(): List<String> =
        listOf(
            "  Name: $name"
        )
}