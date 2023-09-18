package me.alex_s168.stackvm2.ir.ast

class ASTVariableNode(
    val name: String,
    line: Int,
    column: Int
): ASTNode(emptyList(), line, column) {
    override fun toStringExtra(): List<String> =
        listOf(
            "  Name: $name"
        )
}