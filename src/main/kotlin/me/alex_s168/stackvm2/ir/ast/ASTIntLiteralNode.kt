package me.alex_s168.stackvm2.ir.ast

class ASTIntLiteralNode(
    val value: Int,
    line: Int,
    column: Int
): ASTNode(emptyList(), line, column) {

    override fun toStringExtra(): List<String> =
        listOf(
            "  Value: $value"
        )

}