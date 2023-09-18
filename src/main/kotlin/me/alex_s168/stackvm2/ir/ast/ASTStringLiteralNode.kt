package me.alex_s168.stackvm2.ir.ast

class ASTStringLiteralNode(
    val value: String,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = mutableListOf(),
    line,
    column,
    length,
    parent
) {

    override fun toStringExtra(): List<String> =
        listOf(
            "  Value: $value"
        )

}