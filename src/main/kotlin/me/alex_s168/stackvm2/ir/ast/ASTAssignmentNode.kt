package me.alex_s168.stackvm2.ir.ast

class ASTAssignmentNode(
    val left: ASTNode,
    val right: ASTNode,
    line: Int,
    column: Int
): ASTNode(listOf(left, right), line, column) {

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "Left:",
            left.toString(0).trimEnd(),
            "Right:",
            right.toString(0).trimEnd()
        )

}