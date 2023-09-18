package me.alex_s168.stackvm2.ir.ast

class ASTTypeNode(
    val name: String,
    val generics: List<ASTTypeNode>,
    val arrSize: ASTNode?,
    line: Int,
    column: Int
): ASTNode(generics, line, column) {

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Name: $name",
            "  Generics:"
        ) + generics.map { it.toString(1).trimEnd() } + if (arrSize != null) listOf(
            "  Array size:",
            arrSize.toString(1).trimEnd()
        ) else listOf()

}