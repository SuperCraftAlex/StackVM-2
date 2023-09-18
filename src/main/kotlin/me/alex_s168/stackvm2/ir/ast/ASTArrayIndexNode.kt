package me.alex_s168.stackvm2.ir.ast

class ASTArrayIndexNode(
    val array: ASTNode,
    val index: ASTNode,
    line: Int,
    column: Int
): ASTNode(listOf(array, index), line, column) {

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Array:",
            array.toString(1).trimEnd(),
            "  Index:",
            index.toString(1).trimEnd()
        )

}